package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.common.ChatConstants;
import com.github.kolomolo.service.openaiclient.model.request.ChatRequest;
import com.github.kolomolo.service.openaiclient.model.request.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final ChatService chatService;

    public void processMessage(String prompt, List<Message> history) {
        log.debug("Processing chat message with history size: {}", history.size());
        addUserMessage(prompt, history);
        addAssistantResponse(prompt, history);
    }

    private void addUserMessage(String prompt, List<Message> history) {
        history.add(new Message(ChatConstants.Roles.USER, prompt));
    }

    private void addAssistantResponse(String prompt, List<Message> history) {
        try {
            String response = chatService.chat(new ChatRequest(prompt));
            history.add(new Message(ChatConstants.Roles.ASSISTANT, response));
        } catch (Exception e) {
            log.error("Error getting chat response", e);
            history.add(new Message("assistant", "Error: " + e.getMessage()));
        }
    }
}