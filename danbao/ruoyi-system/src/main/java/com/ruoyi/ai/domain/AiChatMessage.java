package com.ruoyi.ai.domain;

/**
 * AI聊天消息对象（用于上下文记忆）
 *
 * @author ruoyi
 */
public class AiChatMessage {

    /**
     * 消息角色：system/user/assistant
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    public AiChatMessage() {
    }

    public AiChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public static AiChatMessage system(String content) {
        return new AiChatMessage("system", content);
    }

    public static AiChatMessage user(String content) {
        return new AiChatMessage("user", content);
    }

    public static AiChatMessage assistant(String content) {
        return new AiChatMessage("assistant", content);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
