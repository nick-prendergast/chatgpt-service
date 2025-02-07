package com.github.kolomolo.service.openaiclient.service.GUI;

import com.github.kolomolo.service.openaiclient.exception.ChatException;
import com.github.kolomolo.service.openaiclient.model.request.ChatRequest;
import com.github.kolomolo.service.openaiclient.model.request.Message;
import com.github.kolomolo.service.openaiclient.service.ChatService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatService chatService;

    private ChatMessageService chatMessageService;
    private List<Message> history;

    @BeforeEach
    void setUp() {
        chatMessageService = new ChatMessageService(chatService);
        history = new ArrayList<>();
    }

    @Test
    void processMessage_ShouldAddUserAndAssistantMessages() {
        String prompt = "test message";
        String response = "test response";
        when(chatService.chat(any())).thenReturn(response);

        chatMessageService.processMessage(prompt, history);

        assertEquals(2, history.size());
        assertEquals("user", history.get(0).role());
        assertEquals(prompt, history.get(0).content());
        assertEquals("assistant", history.get(1).role());
        assertEquals(response, history.get(1).content());
    }

    @Test
    void processMessage_WhenErrorOccurs_ShouldAddErrorMessage() {
        String prompt = "test message";
        when(chatService.chat(any(ChatRequest.class)))
                .thenThrow(new ChatException("API Error", mock(FeignException.class)));

        chatMessageService.processMessage(prompt, history);

        assertEquals(2, history.size());
        assertEquals("user", history.get(0).role());
        assertEquals(prompt, history.get(0).content());
        assertEquals("assistant", history.get(1).role());
        assertTrue(history.get(1).content().contains("Error"));
    }
}