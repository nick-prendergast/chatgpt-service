package com.github.kolomolo.service.openaiclient.restcontroller;

import com.github.kolomolo.service.openaiclient.service.GUI.ChatMessageService;
import com.github.kolomolo.service.openaiclient.service.GUI.ChatSessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
public class ChatGPTJspController {
    private final ChatMessageService chatMessageService;
    private final ChatSessionService chatSessionService;

    @GetMapping("/")
    public String showChatPage(Model model, HttpSession session) {
        session.invalidate();
        model.addAttribute("conversationHistory", new ArrayList<>());
        return "chat";
    }

    @PostMapping("/chatgpt")
    public String chat(@RequestParam String prompt, HttpSession session) {
        var history = chatSessionService.getHistory(session);
        chatMessageService.processMessage(prompt, history);
        session.setAttribute("fromPost", true);
        return "redirect:/chatgpt/results";
    }

    @GetMapping("/chatgpt/results")
    public String showResults(Model model, HttpSession session) {
        if (session.getAttribute("fromPost") == null) {
            return "redirect:/";
        }
        session.removeAttribute("fromPost");
        model.addAttribute("conversationHistory", chatSessionService.getHistory(session));
        return "chat";
    }
}