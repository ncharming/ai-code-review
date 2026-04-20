package com.practice.aicodereview;

import com.practice.aicodereview.domain.service.impl.OpenAiCodeReviewService;
import com.practice.aicodereview.infrastructure.git.GitCommand;
import com.practice.aicodereview.infrastructure.message.EmailMessage;
import com.practice.aicodereview.infrastructure.openai.IOpenAI;
import com.practice.aicodereview.infrastructure.openai.impl.ChatGLM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI Code Review Application
 * Spring Boot 3 主启动类
 *
 * @author AI Code Review Team
 * @version 1.0.0
 */
@SpringBootApplication
@Slf4j
public class AiCodeReviewApplication {


    public static void main(String[] args) {

        SpringApplication.run(AiCodeReviewApplication.class, args);
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

        IOpenAI openAI = new ChatGLM(getEnv("CHATGLM_APIHOST"), getEnv("CHATGLM_APIKEYSECRET"));

        OpenAiCodeReviewService openAiCodeReviewService = new OpenAiCodeReviewService(gitCommand, openAI, message);
        openAiCodeReviewService.exec();

        log.info("代码评审结束");
    }

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null");
        }
        return value;
    }

}
