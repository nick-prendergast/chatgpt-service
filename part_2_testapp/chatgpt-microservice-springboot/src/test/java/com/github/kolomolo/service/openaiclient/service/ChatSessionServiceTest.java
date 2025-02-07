package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.model.request.Message;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;

class ChatSessionServiceTest {

    private final ChatSessionService chatSessionService = new ChatSessionService();

    @Test
    void getHistory_WhenSessionEmpty_ShouldCreateNewHistory() {
        MockHttpSession session = new MockHttpSession();

        List<Message> history = chatSessionService.getHistory(session);

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void getHistory_WhenHistoryExists_ShouldReturnExistingHistory() {
        MockHttpSession session = new MockHttpSession();
        List<Message> firstCall = chatSessionService.getHistory(session);
        Message message = new Message("user", "test");
        firstCall.add(message);

        List<Message> secondCall = chatSessionService.getHistory(session);

        assertSame(firstCall, secondCall);
        assertEquals(1, secondCall.size());
        assertEquals(message, secondCall.get(0));
    }
}