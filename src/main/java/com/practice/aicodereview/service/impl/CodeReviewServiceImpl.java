package com.practice.aicodereview.service.impl;

import com.alibaba.fastjson2.JSON;
import com.practice.aicodereview.config.ChatGlmProperties;
import com.practice.aicodereview.model.dto.ChatCompletionRequest;
import com.practice.aicodereview.model.dto.ChatCompletionSyncResponse;
import com.practice.aicodereview.model.dto.CodeReviewRequest;
import com.practice.aicodereview.model.dto.CodeReviewResponse;
import com.practice.aicodereview.service.CodeReviewService;
import com.practice.aicodereview.utils.BearerTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * 代码评审服务实现
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
@Slf4j
@Service
public class CodeReviewServiceImpl implements CodeReviewService {

    private final ChatGlmProperties chatGlmProperties;
    private final BearerTokenUtils bearerTokenUtils;

    @Autowired
    public CodeReviewServiceImpl(ChatGlmProperties chatGlmProperties, BearerTokenUtils bearerTokenUtils) {
        this.chatGlmProperties = chatGlmProperties;
        this.bearerTokenUtils = bearerTokenUtils;
    }

    @Override
    public CodeReviewResponse reviewCode(CodeReviewRequest request) {
        try {
            // 确定使用的模型
            String model = request.getModel();
            if (model == null || model.isEmpty()) {
                model = chatGlmProperties.getDefaultModel();
            }

            // 执行代码评审
            String reviewResult = reviewCode(request.getDiffContent(), model);

            return CodeReviewResponse.success(reviewResult, model);

        } catch (Exception e) {
            log.error("代码评审失败", e);
            return CodeReviewResponse.error("代码评审失败: " + e.getMessage());
        }
    }

    @Override
    public String reviewCode(String diffCode, String model) {
        try {
            log.info("开始执行代码评审，模型: {}, Diff 长度: {}", model, diffCode.length());

            // 获取 Token
            String token = bearerTokenUtils.getToken(chatGlmProperties.getApiKeySecret());

            // 创建请求
            URL url = new URL(chatGlmProperties.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "AI-Code-Review/1.0.0");
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);

            // 构建请求体
            ChatCompletionRequest chatCompletionRequest = buildChatRequest(diffCode, model);

            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = JSON.toJSONString(chatCompletionRequest).getBytes(StandardCharsets.UTF_8);
                os.write(input);
                os.flush();
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            log.debug("API 响应码: {}", responseCode);

            BufferedReader in;
            if (responseCode >= 200 && responseCode < 300) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            String responseBody = content.toString();
            log.debug("API 响应: {}", responseBody);

            if (responseCode >= 200 && responseCode < 300) {
                // 解析响应
                ChatCompletionSyncResponse response = JSON.parseObject(responseBody, ChatCompletionSyncResponse.class);
                if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                    String result = response.getChoices().get(0).getMessage().getContent();
                    log.info("代码评审成功，结果长度: {}", result.length());
                    return result;
                } else {
                    throw new RuntimeException("API 响应格式异常");
                }
            } else {
                throw new RuntimeException("API 调用失败，响应码: " + responseCode + ", 响应: " + responseBody);
            }

        } catch (Exception e) {
            log.error("调用 ChatGLM API 失败", e);
            throw new RuntimeException("调用 ChatGLM API 失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getGitDiff(String fromHash, String toHash) {
        try {
            log.info("获取 Git Diff: {} -> {}", fromHash, toHash);

            ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", fromHash, toHash);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder diffCode = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                diffCode.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.warn("Git diff 命令退出码: {}", exitCode);
            }

            String result = diffCode.toString();
            log.info("获取 Git Diff 成功，长度: {}", result.length());
            return result;

        } catch (Exception e) {
            log.error("获取 Git Diff 失败", e);
            throw new RuntimeException("获取 Git Diff 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建聊天请求
     *
     * @param diffCode Git Diff 内容
     * @param model    模型名称
     * @return 聊天请求
     */
    private ChatCompletionRequest buildChatRequest(String diffCode, String model) {
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(model);

        // 构建系统提示词
        String systemPrompt = buildSystemPrompt();

        // 构建消息列表
        ArrayList<ChatCompletionRequest.Prompt> messages = new ArrayList<>();
        messages.add(new ChatCompletionRequest.Prompt("system", systemPrompt));
        messages.add(new ChatCompletionRequest.Prompt("user", "请根据以下 Git Diff 记录对代码做出评审：\n\n" + diffCode));

        request.setMessages(messages);

        return request;
    }

    /**
     * 构建系统提示词
     *
     * @return 系统提示词
     */
    private String buildSystemPrompt() {
        return "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言。" +
                "请你根据 Git Diff 记录，对代码做出评审。" +
                "评审内容包括但不限于：" +
                "1. 代码质量和可读性" +
                "2. 潜在的 Bug 和问题" +
                "3. 性能优化建议" +
                "4. 安全性问题" +
                "5. 最佳实践建议" +
                "6. 架构设计建议" +
                "请用清晰、专业的语言给出评审意见。";
    }
}
