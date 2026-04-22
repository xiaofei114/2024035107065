package com.ruoyi.ai.service;

import com.ruoyi.ai.domain.ChatRequest;
import com.ruoyi.ai.domain.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI聊天服务接口
 *
 * @author ruoyi
 */
public interface IAiChatService {

    /**
     * 发送聊天消息并获取AI回复（支持上下文记忆）
     *
     * @param request 聊天请求对象，包含用户消息
     * @return AI回复响应对象
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 流式聊天（SSE）
     *
     * @param request 聊天请求对象
     * @return SseEmitter 流式响应
     */
    SseEmitter chatStream(ChatRequest request);

    /**
     * 清空会话历史
     *
     * @param sessionId 会话ID
     */
    void clearSession(String sessionId);
}
