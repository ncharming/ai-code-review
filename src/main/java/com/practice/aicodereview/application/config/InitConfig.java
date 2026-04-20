package com.practice.aicodereview.application.config;

import com.practice.aicodereview.domain.service.impl.OpenAiCodeReviewService;
import com.practice.aicodereview.infrastructure.git.GitCommand;
import com.practice.aicodereview.infrastructure.message.EmailMessage;
import com.practice.aicodereview.infrastructure.openai.IOpenAI;
import com.practice.aicodereview.infrastructure.openai.impl.ChatGLM;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author：nihongyu
 * @date: 2026/4/20
 */
@Component
@Slf4j
public class InitConfig {

    @PostConstruct
    public void init(){

        log.info("项目启动，准备开始代码评审");

        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );

        EmailMessage message = new EmailMessage(getEnv("SNED_EMIAL"),
                getEnv("SNED_EMIAL_PWD"),
                getEnv("COMMIT_EMAIL"));

        IOpenAI openAI = new ChatGLM("https://open.bigmodel.cn/api/paas/v4/chat/completions", getEnv("CHATGLM_APIKEYSECRET"));

        OpenAiCodeReviewService openAiCodeReviewService = new OpenAiCodeReviewService(gitCommand, openAI, message);
        openAiCodeReviewService.exec();

        log.info("代码评审结束");
    }

    private  String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null");
        }
        return value;
    }

}
