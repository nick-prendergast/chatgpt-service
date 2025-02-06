package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.model.request.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatSessionService {
    private static final String HISTORY_KEY = "history";

    @SuppressWarnings("unchecked")
    public List<Message> getHistory(HttpSession session) {
        var history = (ArrayList<Message>) session.getAttribute(HISTORY_KEY);
        if (history == null) {
            history = new ArrayList<>();
            session.setAttribute(HISTORY_KEY, history);
        }
        return history;
    }
}