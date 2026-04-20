package com.practice.aicodereview.domain.service.impl;

import com.practice.aicodereview.domain.model.GlmModel;
import com.practice.aicodereview.domain.service.AbstractOpenAiCodeReviewService;
import com.practice.aicodereview.infrastructure.git.GitCommand;
import com.practice.aicodereview.infrastructure.message.EmailMessage;
import com.practice.aicodereview.infrastructure.openai.IOpenAI;
import com.practice.aicodereview.infrastructure.openai.dto.ChatCompletionRequestDTO;
import com.practice.aicodereview.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @description:
 * @author：nihongyu
 * @date: 2026/4/20
 */
@Service
public class OpenAiCodeReviewService extends AbstractOpenAiCodeReviewService {


    public OpenAiCodeReviewService(GitCommand gitCommand, IOpenAI openAI, EmailMessage emailMessage) {
        super(gitCommand, openAI, emailMessage);
    }

    @Override
    protected String getDiffCode() throws IOException, InterruptedException {
        return gitCommand.diff();
    }

    @Override
    protected String codeReview(String diffCode) throws Exception {
        ChatCompletionRequestDTO chatCompletionRequest = new ChatCompletionRequestDTO();
        chatCompletionRequest.setModel(GlmModel.GLM_4_7_FLASH.getCode());
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequestDTO.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(new ChatCompletionRequestDTO.Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码如下:"));
                add(new ChatCompletionRequestDTO.Prompt("user", diffCode));
            }
        });

        ChatCompletionSyncResponseDTO completions = openAI.completions(chatCompletionRequest);
        ChatCompletionSyncResponseDTO.Message message = completions.getChoices().get(0).getMessage();
        return message.getContent();
    }

    @Override
    protected String recordCodeReview(String recommend) throws Exception {
        return gitCommand.commitAndPush(recommend);
    }

    @Override
    protected void pushMessage(String logUrl) throws Exception {
        emailMessage.snedMessage(logUrl);
    }
}
