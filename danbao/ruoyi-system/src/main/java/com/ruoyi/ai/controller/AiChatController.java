package com.ruoyi.ai.controller;

import com.ruoyi.ai.domain.ChatRequest;
import com.ruoyi.ai.domain.ChatResponse;
import com.ruoyi.ai.service.IAiChatService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;

/**
 * AI聊天控制器
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/ai/chat")
public class AiChatController extends BaseController {

    @Autowired
    private IAiChatService aiChatService;

    /**
     * AI聊天接口（支持上下文记忆）
     *
     * @param request 聊天请求
     * @return AI回复
     */
    @PostMapping
    @Log(title = "AI聊天", businessType = BusinessType.OTHER)
    public AjaxResult chat(@Validated @RequestBody ChatRequest request) {
        ChatResponse response = aiChatService.chat(request);
        if ("success".equals(response.getStatus())) {
            return AjaxResult.success(response);
        } else {
            return AjaxResult.error(response.getErrorMessage());
        }
    }

    /**
     * AI流式聊天接口（SSE）
     *
     * @param request 聊天请求
     * @return SseEmitter 流式响应
     */
    @PostMapping("/stream")
    @Log(title = "AI流式聊天", businessType = BusinessType.OTHER)
    public SseEmitter chatStream(@Validated @RequestBody ChatRequest request, HttpServletResponse response) {
        // 设置响应头支持SSE
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");

        return aiChatService.chatStream(request);
    }

    /**
     * 清空会话历史
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/session/{sessionId}")
    @Log(title = "清空AI会话", businessType = BusinessType.DELETE)
    public AjaxResult clearSession(@PathVariable String sessionId) {
        aiChatService.clearSession(sessionId);
        return AjaxResult.success("会话已清空");
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public AjaxResult health() {
        return AjaxResult.success("AI服务正常运行");
    }
}
