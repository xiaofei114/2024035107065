package com.ruoyi.ai.service.impl;

import com.ruoyi.ai.domain.AiChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天会话管理器
 * 用于管理多轮对话的上下文记忆
 *
 * @author ruoyi
 */
@Component
public class ChatSessionManager {

    private static final Logger log = LoggerFactory.getLogger(ChatSessionManager.class);

    /**
     * 会话存储：sessionId -> 消息列表
     */
    private final Map<String, List<AiChatMessage>> sessionStore = new ConcurrentHashMap<>();

    /**
     * 最大上下文消息数量（保留最近的几轮对话）
     */
    private static final int MAX_CONTEXT_MESSAGES = 10;

    /**
     * 获取会话的消息历史
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    public List<AiChatMessage> getMessages(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return new ArrayList<>();
        }
        return sessionStore.getOrDefault(sessionId, new ArrayList<>());
    }

    /**
     * 添加消息到会话
     *
     * @param sessionId 会话ID
     * @param message   消息
     */
    public void addMessage(String sessionId, AiChatMessage message) {
        if (sessionId == null || sessionId.isEmpty()) {
            return;
        }

        sessionStore.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);

        // 限制上下文长度，保留最近的消息
        trimMessages(sessionId);

        log.debug("[会话管理] 会话 {} 添加消息，当前消息数: {}", sessionId,
                sessionStore.get(sessionId).size());
    }

    /**
     * 添加用户消息
     */
    public void addUserMessage(String sessionId, String content) {
        addMessage(sessionId, AiChatMessage.user(content));
    }

    /**
     * 添加AI回复消息
     */
    public void addAssistantMessage(String sessionId, String content) {
        addMessage(sessionId, AiChatMessage.assistant(content));
    }

    /**
     * 清空会话历史
     *
     * @param sessionId 会话ID
     */
    public void clearSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            sessionStore.remove(sessionId);
            log.info("[会话管理] 会话 {} 已清空", sessionId);
        }
    }

    /**
     * 获取带上下文的完整消息列表（包含系统提示词）
     *
     * @param sessionId    会话ID
     * @param systemPrompt 系统提示词
     * @param userMessage  当前用户消息
     * @return 完整消息列表
     */
    public List<AiChatMessage> buildContextMessages(String sessionId, String systemPrompt, String userMessage) {
        List<AiChatMessage> messages = new ArrayList<>();

        // 1. 添加系统提示词
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(AiChatMessage.system(systemPrompt));
        }

        // 2. 添加历史消息
        List<AiChatMessage> history = getMessages(sessionId);
        messages.addAll(history);

        // 3. 添加当前用户消息
        messages.add(AiChatMessage.user(userMessage));

        return messages;
    }

    /**
     * 限制消息数量，防止上下文过长
     *
     * @param sessionId 会话ID
     */
    private void trimMessages(String sessionId) {
        List<AiChatMessage> messages = sessionStore.get(sessionId);
        if (messages != null && messages.size() > MAX_CONTEXT_MESSAGES * 2) {
            // 保留最近的几轮对话（每轮包含用户消息和AI回复）
            List<AiChatMessage> trimmed = new ArrayList<>(
                    messages.subList(messages.size() - MAX_CONTEXT_MESSAGES * 2, messages.size())
            );
            sessionStore.put(sessionId, trimmed);
            log.debug("[会话管理] 会话 {} 消息数量超过限制，已裁剪至 {} 条", sessionId, trimmed.size());
        }
    }
}
