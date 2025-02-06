package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.model.request.*;
import com.github.kolomolo.service.openaiclient.model.response.ChatGPTResponse;
import com.github.kolomolo.service.openaiclient.model.response.Choice;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenAIClientServiceTest {

    @Mock
    private OpenAIClient openAIClient;

    @Mock
    private OpenAIClientConfig openAIClientConfig;

    @InjectMocks
    private OpenAIClientService openAIClientService;

    private static final String TEST_MODEL = "gpt-3.5-turbo";
    private static final String TEST_AUDIO_MODEL = "whisper-1";
    private static final String TEST_QUESTION = "Hello, how are you?";
    private static final String TEST_RESPONSE = "I'm doing well, thank you!";

    @Test
    void chat_WithSuccessfulResponse_ShouldReturnContent() {
        // Given
        when(openAIClientConfig.getModel()).thenReturn(TEST_MODEL);
        ChatGPTResponse mockResponse = createMockChatGPTResponse();
        ChatRequest chatRequest = new ChatRequest(TEST_QUESTION);

        when(openAIClient.chat(any(ChatGPTRequest.class))).thenReturn(mockResponse);

        // When
        String result = openAIClientService.chat(chatRequest);

        // Then
        assertEquals(TEST_RESPONSE, result);
        verify(openAIClient).chat(any(ChatGPTRequest.class));
    }

    @Test
    void chat_WithException_ShouldReturnErrorMessage() {
        // Given
        when(openAIClientConfig.getModel()).thenReturn(TEST_MODEL);
        ChatRequest chatRequest = new ChatRequest(TEST_QUESTION);
        String errorMessage = "Something went wrong";

        when(openAIClient.chat(any(ChatGPTRequest.class))).thenThrow(new RuntimeException(errorMessage));

        // When
        String result = openAIClientService.chat(chatRequest);

        // Then
        assertEquals(errorMessage, result);
        verify(openAIClient).chat(any(ChatGPTRequest.class));
    }

    @Test
    void chat_WithNestedErrorMessage_ShouldExtractProperErrorMessage() {
        // Given
        when(openAIClientConfig.getModel()).thenReturn(TEST_MODEL);
        ChatRequest chatRequest = new ChatRequest(TEST_QUESTION);
        String complexErrorMessage = "Some prefix message\": \"Detailed error message\" some suffix";

        when(openAIClient.chat(any(ChatGPTRequest.class))).thenThrow(new RuntimeException(complexErrorMessage));

        // When
        String result = openAIClientService.chat(chatRequest);

        // Then
        assertEquals("Detailed error message", result);
        verify(openAIClient).chat(any(ChatGPTRequest.class));
    }

    @Test
    void createTranscription_WithSuccessfulTranscription_ShouldReturnResponse() {
        // Given
        when(openAIClientConfig.getAudioModel()).thenReturn(TEST_AUDIO_MODEL);
        MultipartFile mockFile = new MockMultipartFile("test.mp3", "test content".getBytes());
        TranscriptionRequest transcriptionRequest = new TranscriptionRequest();
        transcriptionRequest.setFile(mockFile);

        WhisperTranscriptionResponse mockResponse = new WhisperTranscriptionResponse();
        mockResponse.setText("Transcribed text");

        when(openAIClient.createTranscription(any(WhisperTranscriptionRequest.class))).thenReturn(mockResponse);

        // When
        WhisperTranscriptionResponse result = openAIClientService.createTranscription(transcriptionRequest);

        // Then
        assertEquals("Transcribed text", result.getText());
        verify(openAIClient).createTranscription(any(WhisperTranscriptionRequest.class));
    }

    @Test
    void createTranscription_WithException_ShouldReturnErrorResponse() {
        // Given
        when(openAIClientConfig.getAudioModel()).thenReturn(TEST_AUDIO_MODEL);
        MultipartFile mockFile = new MockMultipartFile("test.mp3", "test content".getBytes());
        TranscriptionRequest transcriptionRequest = new TranscriptionRequest();
        transcriptionRequest.setFile(mockFile);

        String errorMessage = "Transcription failed";
        when(openAIClient.createTranscription(any(WhisperTranscriptionRequest.class)))
                .thenThrow(new RuntimeException(errorMessage));

        // When
        WhisperTranscriptionResponse result = openAIClientService.createTranscription(transcriptionRequest);

        // Then
        assertTrue(result.getText().contains(errorMessage));
        verify(openAIClient).createTranscription(any(WhisperTranscriptionRequest.class));
    }

    private ChatGPTResponse createMockChatGPTResponse() {
        ChatGPTResponse response = new ChatGPTResponse();
        List<Choice> choices = new ArrayList<>();
        Choice choice = new Choice();
        Message message = new Message("assistant", OpenAIClientServiceTest.TEST_RESPONSE);
        choice.setMessage(message);
        choices.add(choice);
        response.setChoices(choices);
        return response;
    }
}