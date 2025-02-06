package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.common.ChatConstants;
import com.github.kolomolo.service.openaiclient.model.request.Message;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChatSessionService {

    @SuppressWarnings("unchecked")
    public List<Message> getHistory(HttpSession session) {
        log.debug("Retrieving chat history for session: {}", session.getId());
        var history = (ArrayList<Message>) session.getAttribute(ChatConstants.Session.HISTORY_KEY);
        if (history == null) {
            log.debug("Creating new chat history for session: {}", session.getId());
            history = new ArrayList<>();
            session.setAttribute(ChatConstants.Session.HISTORY_KEY, history);
        }
        return history;
    }
}