package com.practice.aicodereview.service;

import com.practice.aicodereview.model.dto.CodeReviewRequest;
import com.practice.aicodereview.model.dto.CodeReviewResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 代码评审服务测试
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
@SpringBootTest
class CodeReviewServiceTest {

    @Autowired
    private CodeReviewService codeReviewService;

    @Test
    void testReviewCode() {
        // 创建测试请求
        CodeReviewRequest request = new CodeReviewRequest();
        request.setDiffContent("+ public void test() {\n+     System.out.println(\"Hello\");\n+ }");
        request.setModel("glm-4-flash");

        // 执行评审
        CodeReviewResponse response = codeReviewService.reviewCode(request);

        // 验证结果
        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertNotNull(response.getReviewResult());
        assertNotNull(response.getModel());

        System.out.println("评审结果: " + response.getReviewResult());
    }

    @Test
    void testGetGitDiff() {
        // 测试获取 Git Diff
        String diff = codeReviewService.getGitDiff("HEAD~1", "HEAD");

        assertNotNull(diff);
        System.out.println("Git Diff 长度: " + diff.length());
    }
}
