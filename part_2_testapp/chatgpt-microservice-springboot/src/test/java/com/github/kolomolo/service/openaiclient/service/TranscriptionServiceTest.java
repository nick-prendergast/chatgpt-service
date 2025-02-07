package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.model.request.TranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClient;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClientConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptionServiceTest {
    private static final String TEST_AUDIO_MODEL = "whisper-1";

    @Mock
    private OpenAIClient openAIClient;
    @Mock
    private OpenAIClientConfig config;
    @InjectMocks
    private TranscriptionService transcriptionService;

    @Test
    void transcribe_WithSuccessfulTranscription_ShouldReturnResponse() {
        when(config.getAudioModel()).thenReturn(TEST_AUDIO_MODEL);
        MultipartFile mockFile = new MockMultipartFile("test.mp3", "test".getBytes());
        when(openAIClient.createTranscription(any())).thenReturn(new WhisperTranscriptionResponse("Transcribed text"));

        WhisperTranscriptionResponse result = transcriptionService.transcribe(new TranscriptionRequest(mockFile));

        assertEquals("Transcribed text", result.text());
    }

    @Test
    void transcribe_WithException_ShouldReturnErrorResponse() {
        when(config.getAudioModel()).thenReturn(TEST_AUDIO_MODEL);
        MultipartFile mockFile = new MockMultipartFile("test.mp3", "test".getBytes());
        when(openAIClient.createTranscription(any())).thenThrow(new RuntimeException("Error"));

        WhisperTranscriptionResponse result = transcriptionService.transcribe(new TranscriptionRequest(mockFile));

        assertTrue(result.text().contains("Error"));
    }
}