package com.github.kolomolo.service.openaiclient.openaiclient;

import com.github.kolomolo.service.openaiclient.model.request.ChatGPTRequest;
import com.github.kolomolo.service.openaiclient.model.request.Message;
import com.github.kolomolo.service.openaiclient.model.request.WhisperTranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.response.ChatGPTResponse;
import com.github.kolomolo.service.openaiclient.model.response.Choice;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class OpenAIClientTest {

    @MockBean
    private OpenAIClient openAIClient;

    @Test
    void chat_WithValidRequest_ShouldReturnResponse() {
        ChatGPTRequest request = new ChatGPTRequest("gpt-3.5-turbo",
                List.of(new Message("user", "Test message")));

        Choice choice = new Choice(0, new Message("assistant", "Test response"), "stop");

        ChatGPTResponse expectedResponse = new ChatGPTResponse(
                null,
                null,
                null,
                null,
                List.of(choice),
                null
        );
        when(openAIClient.chat(request)).thenReturn(expectedResponse);
        ChatGPTResponse response = openAIClient.chat(request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(openAIClient).chat(request);
    }

    @Test
    void createTranscription_WithValidRequest_ShouldReturnResponse() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                "test".getBytes()
        );

        WhisperTranscriptionRequest request = new WhisperTranscriptionRequest("whisper-1", file);


        WhisperTranscriptionResponse expectedResponse = new WhisperTranscriptionResponse("Test transcription");

        when(openAIClient.createTranscription(request)).thenReturn(expectedResponse);
        WhisperTranscriptionResponse response = openAIClient.createTranscription(request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(openAIClient).createTranscription(request);
    }
}
