package com.github.kolomolo.service.openaiclient.service.API;

import com.github.kolomolo.service.openaiclient.common.ChatConstants;
import com.github.kolomolo.service.openaiclient.exception.TranscriptionException;
import com.github.kolomolo.service.openaiclient.model.request.TranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.request.WhisperTranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClient;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClientConfig;
import com.github.kolomolo.service.openaiclient.service.ErrorMessageExtractor;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TranscriptionService {
    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig config;

    public TranscriptionService(OpenAIClient openAIClient, OpenAIClientConfig config) {
        this.openAIClient = openAIClient;
        this.config = config;
    }

    public WhisperTranscriptionResponse transcribe(TranscriptionRequest request) {
        validateRequest(request);
        log.info("Transcribing file: {} with model: {}",
                request.file().getName(),
                config.getAudioModel());
        try {
            WhisperTranscriptionRequest whisperRequest = new WhisperTranscriptionRequest(config.getAudioModel(), request.file());
            return openAIClient.createTranscription(whisperRequest);

        } catch (FeignException e) {
            String errorMessage = ErrorMessageExtractor.extract(e);
            log.error("Transcription API error - Status: {}, Details: {}",
                    e.status(),
                    errorMessage);
            throw new TranscriptionException(errorMessage, e);
        }
    }

    private void validateRequest(TranscriptionRequest request) {
        if (request == null || request.file() == null) {
            log.warn("Invalid transcription request received");
            throw new IllegalArgumentException(ChatConstants.Error.INVALID_TRANSCRIPTION);
        }
    }
}
