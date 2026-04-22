package com.ruoyi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * AI配置属性类 - 火山引擎Ark API
 * 用于读取application.yml中的AI相关配置
 *
 * @author ruoyi
 */
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    /** API密钥，用于身份验证 */
    private String apiKey;

    /** API基础URL地址 */
    private String baseUrl;

    /** 模型接入点ID */
    private String modelName;

    /** AI人设提示词（系统提示词），用于设定AI的角色和行为 */
    private String systemPrompt;

    @PostConstruct
    public void init() {
        validateConfig();
        printConfig();
    }

    /**
     * 验证AI配置是否正确加载
     * 如果配置缺失，抛出明确的异常
     */
    private void validateConfig() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("AI配置错误: ai.api-key 不能为空，请在application.yml中配置有效的API密钥");
        }
        if (apiKey.equals("ark-你的密钥")) {
            throw new IllegalStateException("AI配置错误: ai.api-key 未修改，请将 'ark-你的密钥' 替换为实际的API密钥");
        }
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalStateException("AI配置错误: ai.base-url 不能为空，请在application.yml中配置API基础URL");
        }
        if (modelName == null || modelName.isEmpty()) {
            throw new IllegalStateException("AI配置错误: ai.model-name 不能为空，请在application.yml中配置模型接入点ID");
        }
        if (modelName.equals("你的接入点ID")) {
            throw new IllegalStateException("AI配置错误: ai.model-name 未修改，请将 '你的接入点ID' 替换为实际的模型接入点ID");
        }
    }

    /**
     * 打印AI配置信息（启动时）
     */
    private void printConfig() {
        System.out.println("========================================");
        System.out.println("AI配置加载成功");
        System.out.println("----------------------------------------");
        System.out.println("Base URL: " + baseUrl);
        System.out.println("Model Name: " + modelName);
        System.out.println("API Key: " + maskApiKey(apiKey));
        System.out.println("========================================");
    }

    /**
     * 脱敏显示API密钥
     */
    private String maskApiKey(String key) {
        if (key == null || key.length() <= 8) {
            return "****";
        }
        return key.substring(0, 4) + "****" + key.substring(key.length() - 4);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
}
