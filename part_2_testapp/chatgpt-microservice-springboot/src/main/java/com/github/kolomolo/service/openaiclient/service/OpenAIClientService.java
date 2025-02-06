package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.model.request.*;
import com.github.kolomolo.service.openaiclient.model.response.ChatGPTResponse;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClient;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClientConfig;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OpenAIClientService {
    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;

    public OpenAIClientService(OpenAIClient openAIClient, OpenAIClientConfig openAIClientConfig) {
        this.openAIClient = openAIClient;
        this.openAIClientConfig = openAIClientConfig;
    }

    public String chat(ChatRequest chatRequest) {
        try {
            Message message = new Message("user", chatRequest.getQuestion());

            ChatGPTRequest chatGPTRequest = new ChatGPTRequest(openAIClientConfig.getModel(),Collections.singletonList(message));

            ChatGPTResponse response = openAIClient.chat(chatGPTRequest);
            return response.getChoices().getFirst().getMessage().getContent();
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("message")) {
                try {
                    errorMessage = errorMessage.split("message\": \"")[1].split("\"")[0];
                } catch (Exception ex) {
                    // If splitting fails, use the original message
                }
            }
            return errorMessage;
        }
    }

    public WhisperTranscriptionResponse createTranscription(TranscriptionRequest transcriptionRequest) {
        try {
            WhisperTranscriptionRequest whisperRequest = new WhisperTranscriptionRequest();
            whisperRequest.setModel(openAIClientConfig.getAudioModel());
            whisperRequest.setFile(transcriptionRequest.getFile());

            return openAIClient.createTranscription(whisperRequest);
        } catch (Exception e) {
            WhisperTranscriptionResponse response = new WhisperTranscriptionResponse();
            response.setText("Error: " + e.getMessage());
            return response;
        }
    }
}
