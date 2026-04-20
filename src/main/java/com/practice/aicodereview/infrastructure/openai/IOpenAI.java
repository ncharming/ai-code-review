package com.practice.aicodereview.infrastructure.openai;


import com.practice.aicodereview.infrastructure.openai.dto.ChatCompletionRequestDTO;
import com.practice.aicodereview.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;

public interface IOpenAI {

    ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO requestDTO) throws Exception;

}
