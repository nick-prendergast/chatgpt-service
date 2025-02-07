package com.github.kolomolo.service.openaiclient.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kolomolo.service.openaiclient.config.SecurityConfig;
import com.github.kolomolo.service.openaiclient.exception.GlobalExceptionHandler;
import com.github.kolomolo.service.openaiclient.model.request.ChatRequest;
import com.github.kolomolo.service.openaiclient.security.JwtAuthenticationHandler;
import com.github.kolomolo.service.openaiclient.security.JwtTokenExtractor;
import com.github.kolomolo.service.openaiclient.security.JwtService;
import com.github.kolomolo.service.openaiclient.security.SecurityPathMatcher;
import com.github.kolomolo.service.openaiclient.security.JwtAuthenticationFilter;
import com.github.kolomolo.service.openaiclient.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class, GlobalExceptionHandler.class, JwtTokenExtractor.class,
        JwtAuthenticationHandler.class,
        SecurityPathMatcher.class})
@TestPropertySource(properties = {
        "jwt.secret-key=test-secret-key",
        "jwt.username=testuser",
        "jwt.password=testpass"
})
class ChatControllerTest {
    private static final String MOCK_TOKEN = "Bearer mock.jwt.token";
    private static final String VALID_TOKEN = "mock.jwt.token";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ChatService chatService;
    @MockBean
    private JwtService jwtService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        when(jwtService.validateTokenAndGetUsername(VALID_TOKEN))
                .thenReturn("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_WithValidRequest_ShouldReturnResponse() throws Exception {
        ChatRequest request = new ChatRequest("What is OpenAI?");
        String expectedResponse = "OpenAI is an artificial intelligence research company.";

        when(chatService.chat(any(ChatRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void chat_WithMissingAuthorizationHeader_ShouldReturnUnauthorized() throws Exception {
        ChatRequest request = new ChatRequest("What is OpenAI?");

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void chat_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        ChatRequest request = new ChatRequest("What is OpenAI?");

        when(jwtService.validateTokenAndGetUsername(VALID_TOKEN))
                .thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_WithEmptyQuestion_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(new ChatRequest(""))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Question cannot be empty")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        when(chatService.chat(any(ChatRequest.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(new ChatRequest("test"))))
                .andExpect(status().isInternalServerError());
    }
}