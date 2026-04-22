package com.ruoyi.ai.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * AI聊天请求对象
 *
 * @author ruoyi
 */
public class ChatRequest {

    /**
     * 用户输入的消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 4000, message = "消息内容长度不能超过4000字符")
    private String message;

    /**
     * 会话ID（可选，用于保持上下文）
     */
    private String sessionId;

    /**
     * 模型参数：温度（可选，默认0.7）
     */
    private Double temperature;

    /**
     * 模型参数：最大生成token数（可选，默认2048）
     */
    private Integer maxTokens;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
}
