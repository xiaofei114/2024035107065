package com.ruoyi.ai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.ai.domain.AiChatMessage;
import com.ruoyi.ai.domain.ChatRequest;
import com.ruoyi.ai.domain.ChatResponse;
import com.ruoyi.ai.service.IAiChatService;
import com.ruoyi.common.config.AiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI聊天服务实现类
 * 提供与火山引擎Ark API的交互功能，支持上下文记忆和流式输出
 *
 * @author ruoyi
 */
@Service
public class AiChatServiceImpl implements IAiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);

    @Autowired
    private AiProperties aiProperties;

    @Autowired
    private ChatSessionManager sessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 线程池用于处理流式请求
     */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * API调用超时时间（毫秒）
     */
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 120000;

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 发送聊天消息并获取AI回复（支持上下文）
     *
     * @param request 聊天请求对象
     * @return AI回复响应对象
     */
    @Override
    public ChatResponse chat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        String sessionId = request.getSessionId();
        String userMessage = request.getMessage();
        String maskedInput = maskSensitiveInfo(userMessage);

        log.info("[AI聊天] 方法入口 - 用户输入: {}, 会话ID: {}", maskedInput, sessionId);

        // 参数校验
        if (userMessage == null || userMessage.trim().isEmpty()) {
            log.warn("[AI聊天] 参数校验失败 - 消息内容为空");
            return ChatResponse.error("消息内容不能为空");
        }

        // 构建带上下文的请求体
        List<AiChatMessage> contextMessages = sessionManager.buildContextMessages(
                sessionId,
                aiProperties.getSystemPrompt(),
                userMessage
        );

        String requestBody = buildRequestBody(contextMessages, request.getTemperature(), request.getMaxTokens(), false);
        log.info("[AI聊天] API调用 - 请求消息数: {}", contextMessages.size());

        int retryCount = 0;

        while (retryCount < MAX_RETRY_COUNT) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(aiProperties.getBaseUrl() + "/chat/completions");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + aiProperties.getApiKey());

                // 发送请求
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                log.info("[AI聊天] API响应码: {}", responseCode);

                StringBuilder response = new StringBuilder();
                if (responseCode == HttpStatus.OK.value()) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                    }
                } else {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                    }
                    log.error("[AI聊天] API返回错误 - 状态码: {}, 响应: {}", responseCode, response);
                    return ChatResponse.error("AI服务返回错误，状态码: " + responseCode);
                }

                // 解析响应
                String responseContent = parseResponseContent(response.toString());
                long responseTime = System.currentTimeMillis() - startTime;

                // 保存到会话历史
                if (sessionId != null && !sessionId.isEmpty()) {
                    sessionManager.addUserMessage(sessionId, userMessage);
                    sessionManager.addAssistantMessage(sessionId, responseContent);
                    log.info("[AI聊天] 消息已保存到会话 {}，当前历史数: {}",
                            sessionId, sessionManager.getMessages(sessionId).size());
                }

                log.info("[AI聊天] 方法出口 - 响应内容: {}, 耗时: {}ms",
                        maskSensitiveInfo(responseContent), responseTime);

                return ChatResponse.success(responseContent, aiProperties.getModelName(), null, responseTime);

            } catch (SocketTimeoutException e) {
                retryCount++;
                log.error("[AI聊天] API调用超时 - 第{}次重试, 错误: {}", retryCount, e.getMessage());
                if (retryCount >= MAX_RETRY_COUNT) {
                    return ChatResponse.error("AI服务响应超时，请稍后重试");
                }
                try {
                    Thread.sleep(1000 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return ChatResponse.error("请求被中断");
                }
            } catch (java.net.UnknownHostException e) {
                log.error("[AI聊天] 网络异常 - 无法解析主机地址: {}", e.getMessage());
                return ChatResponse.error("网络连接失败，请检查网络设置");
            } catch (java.net.ConnectException e) {
                log.error("[AI聊天] 网络异常 - 连接被拒绝: {}", e.getMessage());
                return ChatResponse.error("无法连接到AI服务，请检查服务地址配置");
            } catch (java.io.IOException e) {
                log.error("[AI聊天] 网络IO异常: {}", e.getMessage());
                return ChatResponse.error("网络通信异常，请稍后重试");
            } catch (Exception e) {
                log.error("[AI聊天] 未知异常: {}", e.getMessage(), e);
                return ChatResponse.error("系统异常，请联系管理员");
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        return ChatResponse.error("AI服务暂时不可用，请稍后重试");
    }

    /**
     * 流式聊天（SSE）
     *
     * @param request 聊天请求对象
     * @return SseEmitter 流式响应
     */
    @Override
    public SseEmitter chatStream(ChatRequest request) {
        String sessionId = request.getSessionId();
        String userMessage = request.getMessage();

        SseEmitter emitter = new SseEmitter(0L); // 无超时

        executorService.execute(() -> {
            try {
                // 构建带上下文的请求体（启用流式）
                List<AiChatMessage> contextMessages = sessionManager.buildContextMessages(
                        sessionId,
                        aiProperties.getSystemPrompt(),
                        userMessage
                );

                String requestBody = buildRequestBody(contextMessages, request.getTemperature(), request.getMaxTokens(), true);

                URL url = new URL(aiProperties.getBaseUrl() + "/chat/completions");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + aiProperties.getApiKey());
                connection.setRequestProperty("Accept", "text/event-stream");

                // 发送请求
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpStatus.OK.value()) {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("AI服务返回错误: " + responseCode));
                    emitter.complete();
                    return;
                }

                // 读取流式响应
                StringBuilder fullResponse = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                break;
                            }

                            // 解析流式数据
                            String content = parseStreamContent(data);
                            if (content != null && !content.isEmpty()) {
                                fullResponse.append(content);
                                // 将换行符转义，避免 SSE 分割数据
                                String escapedContent = content.replace("\n", "\\n").replace("\r", "");
                                emitter.send(SseEmitter.event()
                                        .name("message")
                                        .data(escapedContent));
                            }
                        }
                    }
                }

                // 保存完整响应到会话
                if (sessionId != null && !sessionId.isEmpty()) {
                    sessionManager.addUserMessage(sessionId, userMessage);
                    sessionManager.addAssistantMessage(sessionId, fullResponse.toString());
                }

                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("done"));
                emitter.complete();

            } catch (Exception e) {
                log.error("[AI聊天] 流式响应异常: {}", e.getMessage());
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data(e.getMessage()));
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }
            }
        });

        return emitter;
    }

    /**
     * 清空会话历史
     */
    public void clearSession(String sessionId) {
        sessionManager.clearSession(sessionId);
    }

    /**
     * 构建API请求体
     */
    private String buildRequestBody(List<AiChatMessage> messages, Double temperature, Integer maxTokens, boolean stream) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"model\":\"").append(escapeJson(aiProperties.getModelName())).append("\",");
        sb.append("\"messages\":").append(messagesToJson(messages)).append(",");
        sb.append("\"stream\":").append(stream);

        if (temperature != null) {
            sb.append(",\"temperature\":").append(temperature);
        }
        if (maxTokens != null) {
            sb.append(",\"max_tokens\":").append(maxTokens);
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * 将消息列表转换为JSON数组
     */
    private String messagesToJson(List<AiChatMessage> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < messages.size(); i++) {
            AiChatMessage msg = messages.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"role\":\"").append(msg.getRole()).append("\",");
            sb.append("\"content\":\"").append(escapeJson(msg.getContent())).append("\"");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 解析API响应内容
     */
    private String parseResponseContent(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null) {
                    JsonNode content = message.get("content");
                    if (content != null) {
                        return content.asText();
                    }
                }
            }
            return "无法解析响应内容";
        } catch (Exception e) {
            log.error("[AI聊天] 解析响应失败: {}", e.getMessage());
            return "解析响应失败";
        }
    }

    /**
     * 解析流式响应内容
     */
    private String parseStreamContent(String data) {
        try {
            JsonNode root = objectMapper.readTree(data);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode delta = choices.get(0).get("delta");
                if (delta != null) {
                    JsonNode content = delta.get("content");
                    if (content != null) {
                        return content.asText();
                    }
                }
            }
        } catch (Exception e) {
            log.debug("[AI聊天] 解析流式数据失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * JSON字符串转义
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * 脱敏处理敏感信息
     */
    private String maskSensitiveInfo(String input) {
        if (input == null || input.length() <= 10) {
            return input;
        }
        return input.substring(0, 10) + "...";
    }
}
