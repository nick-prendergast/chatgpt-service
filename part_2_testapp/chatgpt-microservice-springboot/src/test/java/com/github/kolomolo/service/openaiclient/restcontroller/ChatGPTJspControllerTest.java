package com.github.kolomolo.service.openaiclient.restcontroller;

import com.github.kolomolo.service.openaiclient.security.jwt.JwtAuthenticationHandler;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtTokenExtractor;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtService;
import com.github.kolomolo.service.openaiclient.security.SecurityPathMatcher;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtAuthenticationFilter;
import com.github.kolomolo.service.openaiclient.service.ChatMessageService;
import com.github.kolomolo.service.openaiclient.service.ChatSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(
        controllers = ChatGPTJspController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        }
)
@Import({
        JwtTokenExtractor.class,
        JwtAuthenticationHandler.class,
        SecurityPathMatcher.class,
        JwtAuthenticationFilter.class
})
class ChatGPTJspControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatMessageService chatMessageService;

    @MockBean
    private ChatSessionService chatSessionService;

    @MockBean
    private JwtService jwtService;

    @Test
    void showChatPage_ShouldReturnChatView() throws Exception {
        when(chatSessionService.getHistory(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(model().attributeExists("conversationHistory"));
    }

    @Test
    void chat_ShouldRedirectToResults() throws Exception {
        when(chatSessionService.getHistory(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(post("/chatgpt")
                        .param("prompt", "test message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chatgpt/results"));
    }

    @Test
    void showResults_WithoutFromPost_ShouldRedirectToRoot() throws Exception {
        mockMvc.perform(get("/chatgpt/results"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void showResults_WithFromPost_ShouldShowChat() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("fromPost", true);
        when(chatSessionService.getHistory(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/chatgpt/results").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(model().attributeExists("conversationHistory"));
    }
}