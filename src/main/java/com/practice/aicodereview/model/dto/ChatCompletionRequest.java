package com.practice.aicodereview.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ChatGLM 聊天完成请求 DTO
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionRequest {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 消息列表
     */
    private List<Prompt> messages;

    /**
     * 消息提示
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prompt {
        /**
         * 角色（user/assistant/system）
         */
        private String role;

        /**
         * 内容
         */
        private String content;
    }
}
