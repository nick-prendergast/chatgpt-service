package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.model.request.ChatGPTRequest;
import com.github.kolomolo.service.openaiclient.model.request.ChatRequest;
import com.github.kolomolo.service.openaiclient.model.request.Message;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClient;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class ChatService {
    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig config;

    public ChatService(OpenAIClient openAIClient, OpenAIClientConfig config) {
        this.openAIClient = openAIClient;
        this.config = config;
        log.info("ChatService initialized with model: {}", config.getModel());
    }

    public String chat(ChatRequest chatRequest) {
        validateRequest(chatRequest);
        try {
            log.debug("Processing chat request: {}", chatRequest.question());
            Message message = new Message("user", chatRequest.question());

            ChatGPTRequest request = new ChatGPTRequest(config.getModel(), Collections.singletonList(message));

            String response = openAIClient.chat(request)
                    .choices()
                    .getFirst()
                    .message()
                    .content();
            log.debug("Received response for question: {}", chatRequest.question());
            return response;
        } catch (Exception e) {
            log.error("Error processing chat request: {}", chatRequest.question(), e);
            return ErrorMessageExtractor.extract(e);
        }
    }

    private void validateRequest(ChatRequest request) {
        if (request == null || request.question() == null || request.question().trim().isEmpty()) {
            log.warn("Invalid chat request received");
            throw new IllegalArgumentException("Invalid chat request");
        }
    }
}
