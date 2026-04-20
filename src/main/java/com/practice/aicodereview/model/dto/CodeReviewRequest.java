package com.practice.aicodereview.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代码评审请求 DTO
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeReviewRequest {

    /**
     * Git Diff 内容
     */
    @NotBlank(message = "Git Diff 内容不能为空")
    private String diffContent;

    /**
     * 模型名称（可选，默认使用配置文件中的默认模型）
     */
    private String model;

    /**
     * 自定义提示词（可选）
     */
    private String customPrompt;

    /**
     * 仓库名称（可选，用于生成更具体的评审意见）
     */
    private String repositoryName;

    /**
     * 分支名称（可选）
     */
    private String branchName;

    /**
     * Commit ID（可选）
     */
    private String commitId;
}
