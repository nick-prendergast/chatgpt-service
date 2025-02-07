package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.exception.ChatException;
import com.github.kolomolo.service.openaiclient.model.request.ChatGPTRequest;
import com.github.kolomolo.service.openaiclient.model.request.ChatRequest;
import com.github.kolomolo.service.openaiclient.model.request.Message;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClient;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClientConfig;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatService {
    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig config;

    public ChatService(OpenAIClient openAIClient, OpenAIClientConfig config) {
        this.openAIClient = openAIClient;
        this.config = config;
        log.info("Initialized with model: {}", config.getModel());
    }

    public String chat(ChatRequest chatRequest) {
        validateRequest(chatRequest);
        try {
            log.debug("Processing request: {}", chatRequest.question());
            Message message = new Message("user", chatRequest.question());
            ChatGPTRequest request = new ChatGPTRequest(config.getModel(), Collections.singletonList(message));

            String response = openAIClient.chat(request)
                    .choices()
                    .getFirst()
                    .message()
                    .content();

            log.debug("Received response for: {}", chatRequest.question());
            return response;
        } catch (FeignException e) {
            String errorMessage = ErrorMessageExtractor.extract(e);
            log.error("Chat API error - Status: {}, Message: {}", e.status(), errorMessage);
            throw new ChatException(errorMessage, e);
        }
    }

    private void validateRequest(ChatRequest request) {
        if (request == null || request.question() == null || request.question().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid chat request");
        }
    }
}