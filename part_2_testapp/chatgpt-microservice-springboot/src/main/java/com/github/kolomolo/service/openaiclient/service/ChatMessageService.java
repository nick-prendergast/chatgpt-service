package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.model.request.ChatRequest;
import com.github.kolomolo.service.openaiclient.model.request.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatService chatService;

    public void processMessage(String prompt, List<Message> history) {
        addUserMessage(prompt, history);
        addAssistantResponse(prompt, history);
    }

    private void addUserMessage(String prompt, List<Message> history) {
        history.add(new Message("user", prompt));
    }

    private void addAssistantResponse(String prompt, List<Message> history) {
        try {
            String response = chatService.chat(new ChatRequest(prompt));
            history.add(new Message("assistant", response));
        } catch (Exception e) {
            history.add(new Message("assistant", "Error: " + e.getMessage()));
        }
    }
}