package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.common.ChatConstants;
import com.github.kolomolo.service.openaiclient.model.request.TranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.request.WhisperTranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClient;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TranscriptionService {
    private static final Logger logger = LoggerFactory.getLogger(TranscriptionService.class);
    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig config;

    public TranscriptionService(OpenAIClient openAIClient, OpenAIClientConfig config) {
        this.openAIClient = openAIClient;
        this.config = config;
    }

    public WhisperTranscriptionResponse transcribe(TranscriptionRequest request) {
        validateRequest(request);
        logger.info("Starting transcription for file: {}", request.file().getName());
        try {
            WhisperTranscriptionRequest whisperRequest = new WhisperTranscriptionRequest(config.getAudioModel(), request.file());
            logger.debug("Using audio model: {}", config.getAudioModel());
            return openAIClient.createTranscription(whisperRequest);
        } catch (Exception e) {
            logger.error("Transcription error", e);
            return new WhisperTranscriptionResponse("Error: " + ErrorMessageExtractor.extract(e));
        }
    }

    private void validateRequest(TranscriptionRequest request) {
        if (request == null || request.file() == null) {
            logger.warn("Invalid transcription request received");
            throw new IllegalArgumentException(ChatConstants.Error.INVALID_TRANSCRIPTION);
        }
    }
}
