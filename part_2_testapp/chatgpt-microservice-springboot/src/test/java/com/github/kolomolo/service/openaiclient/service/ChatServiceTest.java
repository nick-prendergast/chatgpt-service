package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.exception.ChatException;
import com.github.kolomolo.service.openaiclient.model.request.ChatGPTRequest;
import com.github.kolomolo.service.openaiclient.model.request.ChatRequest;
import com.github.kolomolo.service.openaiclient.model.request.Message;
import com.github.kolomolo.service.openaiclient.model.response.ChatGPTResponse;
import com.github.kolomolo.service.openaiclient.model.response.Choice;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClient;
import com.github.kolomolo.service.openaiclient.openaiclient.OpenAIClientConfig;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    private static final String TEST_MODEL = "gpt-3.5-turbo";
    private static final String TEST_QUESTION = "Hello, how are you?";
    private static final String TEST_RESPONSE = "I'm doing well, thank you!";

    @Mock
    private OpenAIClient openAIClient;
    @Mock
    private OpenAIClientConfig config;
    @InjectMocks
    private ChatService chatService;

    @Test
    void chat_WithSuccessfulResponse_ShouldReturnContent() {
        when(config.getModel()).thenReturn(TEST_MODEL);
        ChatGPTResponse mockResponse = createMockChatGPTResponse();
        when(openAIClient.chat(any())).thenReturn(mockResponse);

        String result = chatService.chat(new ChatRequest(TEST_QUESTION));

        assertEquals(TEST_RESPONSE, result);
        verify(openAIClient).chat(any());
    }

    @Test
    void chat_WithException_ShouldHandleError() {
        when(config.getModel()).thenReturn(TEST_MODEL);

        when(openAIClient.chat(any()))
                .thenThrow(new FeignException.InternalServerError(
                        "Service error",
                        mock(Request.class),
                        "Error details".getBytes(),
                        null
                ));

        assertThrows(ChatException.class, () ->
                chatService.chat(new ChatRequest(TEST_QUESTION))
        );
    }


    private ChatGPTResponse createMockChatGPTResponse() {
        return new ChatGPTResponse(
                null, null, null, null,
                List.of(new Choice(0, new Message("assistant", TEST_RESPONSE), null)),
                null
        );
    }
}