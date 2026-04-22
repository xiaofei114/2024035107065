package com.ruoyi.ai.domain;

/**
 * AI聊天响应对象
 *
 * @author ruoyi
 */
public class ChatResponse {

    /**
     * 响应状态：success 或 error
     */
    private String status;

    /**
     * AI生成的回复内容
     */
    private String content;

    /**
     * 错误信息（当status为error时）
     */
    private String errorMessage;

    /**
     * 使用的模型名称
     */
    private String model;

    /**
     * 响应token数量
     */
    private Integer totalTokens;

    /**
     * 请求耗时（毫秒）
     */
    private Long responseTime;

    public ChatResponse() {
    }

    public static ChatResponse success(String content, String model, Integer totalTokens, Long responseTime) {
        ChatResponse response = new ChatResponse();
        response.status = "success";
        response.content = content;
        response.model = model;
        response.totalTokens = totalTokens;
        response.responseTime = responseTime;
        return response;
    }

    public static ChatResponse error(String errorMessage) {
        ChatResponse response = new ChatResponse();
        response.status = "error";
        response.errorMessage = errorMessage;
        return response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }
}
