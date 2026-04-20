package com.practice.aicodereview.service;

import com.practice.aicodereview.model.dto.CodeReviewRequest;
import com.practice.aicodereview.model.dto.CodeReviewResponse;

/**
 * 代码评审服务接口
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
public interface CodeReviewService {

    /**
     * 执行代码评审
     *
     * @param request 评审请求
     * @return 评审响应
     */
    CodeReviewResponse reviewCode(CodeReviewRequest request);

    /**
     * 执行代码评审（使用指定模型）
     *
     * @param diffCode Git Diff 内容
     * @param model    模型名称
     * @return 评审结果
     */
    String reviewCode(String diffCode, String model);

    /**
     * 获取 Git Diff 内容
     *
     * @param fromHash 起始 commit hash
     * @param toHash   结束 commit hash
     * @return Git Diff 内容
     */
    String getGitDiff(String fromHash, String toHash);
}
