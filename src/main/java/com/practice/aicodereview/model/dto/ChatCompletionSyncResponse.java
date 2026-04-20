package com.practice.aicodereview.model.dto;

import lombok.Data;

import java.util.List;

/**
 * ChatGLM 聊天完成响应 DTO
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
@Data
public class ChatCompletionSyncResponse {

    /**
     * 选择列表
     */
    private List<Choice> choices;

    /**
     * 使用情况
     */
    private Usage usage;

    /**
     * 选择项
     */
    @Data
    public static class Choice {
        /**
         * 消息
         */
        private Message message;

        /**
         * 完成原因
         */
        private String finishReason;

        /**
         * 索引
         */
        private Integer index;
    }

    /**
     * 消息
     */
    @Data
    public static class Message {
        /**
         * 角色
         */
        private String role;

        /**
         * 内容
         */
        private String content;
    }

    /**
     * 使用情况
     */
    @Data
    public static class Usage {
        /**
         * 提示 token 数
         */
        private Integer promptTokens;

        /**
         * 完成 token 数
         */
        private Integer completionTokens;

        /**
         * 总 token 数
         */
        private Integer totalTokens;
    }
}
