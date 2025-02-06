package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.model.request.TranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.request.WhisperTranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClient;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
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
        try {
            WhisperTranscriptionRequest whisperRequest = new WhisperTranscriptionRequest(config.getAudioModel(), request.file());

            return openAIClient.createTranscription(whisperRequest);
        } catch (Exception e) {
            logger.error("Transcription error", e);
            return new WhisperTranscriptionResponse("Error: " + ErrorMessageExtractor.extract(e));
        }
    }

    private void validateRequest(TranscriptionRequest request) {
        if (request == null || request.file() == null) {
            throw new IllegalArgumentException("Invalid transcription request");
        }
    }
}
