package com.practice.aicodereview.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * ChatGLM 配置类
 * 配置 Token 缓存等 Bean
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
@Configuration
public class ChatGlmConfig {

    @Autowired
    private ChatGlmProperties chatGlmProperties;

    /**
     * 配置 Token 缓存
     * 缓存时间设置为 Token 过期时间减去 1 分钟（提前刷新）
     *
     * @return Token 缓存
     */
    @Bean
    public Cache<String, String> tokenCache() {
        long expireMillis = chatGlmProperties.getTokenExpireMillis() - 60000L;
        return CacheBuilder.newBuilder()
                .expireAfterWrite(expireMillis, TimeUnit.MILLISECONDS)
                .build();
    }
}
