package com.practice.aicodereview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ChatGLM API 配置属性
 * 从 application.yml 读取配置
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "chatglm.api")
public class ChatGlmProperties {

    /**
     * API 密钥（格式：apiKey.apiSecret）
     */
    private String apiKeySecret;

    /**
     * API 请求地址
     */
    private String url;

    /**
     * Token 过期时间（毫秒）
     */
    private Long tokenExpireMillis = 1800000L;

    /**
     * 默认模型
     */
    private String defaultModel = "glm-4-flash";
}
