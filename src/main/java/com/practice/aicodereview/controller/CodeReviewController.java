package com.practice.aicodereview.controller;

import com.practice.aicodereview.model.dto.CodeReviewRequest;
import com.practice.aicodereview.model.dto.CodeReviewResponse;
import com.practice.aicodereview.service.CodeReviewService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 代码评审控制器
 * 提供 REST API 接口
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/review")
public class CodeReviewController {

    private final CodeReviewService codeReviewService;

    @Autowired
    public CodeReviewController(CodeReviewService codeReviewService) {
        this.codeReviewService = codeReviewService;
    }

    /**
     * 执行代码评审
     *
     * @param request 评审请求
     * @return 评审响应
     */
    @PostMapping("/code")
    public ResponseEntity<CodeReviewResponse> reviewCode(@Valid @RequestBody CodeReviewRequest request) {
        log.info("收到代码评审请求，Diff 长度: {}", request.getDiffContent().length());

        try {
            CodeReviewResponse response = codeReviewService.reviewCode(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("代码评审失败", e);
            return ResponseEntity.status(500)
                    .body(CodeReviewResponse.error("代码评审失败: " + e.getMessage()));
        }
    }

    /**
     * 获取 Git Diff 并进行评审
     *
     * @param fromHash 起始 commit hash
     * @param toHash   结束 commit hash
     * @return 评审响应
     */
    @GetMapping("/git/{fromHash}/{toHash}")
    public ResponseEntity<CodeReviewResponse> reviewGitDiff(
            @PathVariable String fromHash,
            @PathVariable String toHash) {

        log.info("收到 Git Diff 评审请求: {} -> {}", fromHash, toHash);

        try {
            // 获取 Git Diff
            String diffCode = codeReviewService.getGitDiff(fromHash, toHash);

            // 构建评审请求
            CodeReviewRequest request = new CodeReviewRequest();
            request.setDiffContent(diffCode);
            request.setRepositoryName("current");

            // 执行评审
            CodeReviewResponse response = codeReviewService.reviewCode(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Git Diff 代码评审失败", e);
            return ResponseEntity.status(500)
                    .body(CodeReviewResponse.error("Git Diff 代码评审失败: " + e.getMessage()));
        }
    }

    /**
     * 健康检查
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Code Review Service is running");
    }
}
