package com.practice.aicodereview.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Bearer Token 工具类
 * 用于生成和缓存 ChatGLM API 的 JWT Token
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
@Slf4j
@Component
public class BearerTokenUtils {

    private final Cache<String, String> tokenCache;

    @Autowired
    public BearerTokenUtils(Cache<String, String> tokenCache) {
        this.tokenCache = tokenCache;
    }

    /**
     * 根据 API Key Secret 获取 Token
     * 格式：apiKey.apiSecret
     *
     * @param apiKeySecret API 密钥（格式：apiKey.apiSecret）
     * @return JWT Token
     */
    public String getToken(String apiKeySecret) {
        String[] split = apiKeySecret.split("\\.");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid API Key format. Expected: apiKey.apiSecret");
        }
        return getToken(split[0], split[1]);
    }

    /**
     * 对 ApiKey 进行签名
     *
     * @param apiKey    登录创建 ApiKey <a href="https://open.bigmodel.cn/usercenter/apikeys">apikeys</a>
     * @param apiSecret apiKey 的后半部分
     * @return Token
     */
    public String getToken(String apiKey, String apiSecret) {
        // 尝试从缓存获取 Token
        String token = tokenCache.getIfPresent(apiKey);
        if (token != null) {
            log.debug("Token found in cache for apiKey: {}", maskApiKey(apiKey));
            return token;
        }

        // 创建新 Token
        log.debug("Generating new token for apiKey: {}", maskApiKey(apiKey));
        Algorithm algorithm = Algorithm.HMAC256(apiSecret.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> payload = new HashMap<>();
        payload.put("api_key", apiKey);
        payload.put("exp", System.currentTimeMillis() + 30 * 60 * 1000L);
        payload.put("timestamp", Calendar.getInstance().getTimeInMillis());

        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", "HS256");
        headerClaims.put("sign_type", "SIGN");

        token = JWT.create()
                .withPayload(payload)
                .withHeader(headerClaims)
                .sign(algorithm);

        // 缓存 Token
        tokenCache.put(apiKey, token);

        return token;
    }

    /**
     * 掩码 API Key 用于日志输出（隐藏敏感信息）
     *
     * @param apiKey API Key
     * @return 掩码后的 API Key
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
