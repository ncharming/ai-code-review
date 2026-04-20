package com.practice.aicodereview.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代码评审响应 DTO
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeReviewResponse {

    /**
     * 评审结果
     */
    private String reviewResult;

    /**
     * 评审状态（success/error）
     */
    private String status;

    /**
     * 错误信息（如果评审失败）
     */
    private String errorMessage;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 创建成功响应
     *
     * @param reviewResult 评审结果
     * @param model        使用的模型
     * @return 响应对象
     */
    public static CodeReviewResponse success(String reviewResult, String model) {
        return CodeReviewResponse.builder()
                .reviewResult(reviewResult)
                .status("success")
                .model(model)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建失败响应
     *
     * @param errorMessage 错误信息
     * @return 响应对象
     */
    public static CodeReviewResponse error(String errorMessage) {
        return CodeReviewResponse.builder()
                .status("error")
                .errorMessage(errorMessage)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
