package com.github.kolomolo.service.openaiclient.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kolomolo.service.openaiclient.TestConstants;
import com.github.kolomolo.service.openaiclient.config.SecurityConfig;
import com.github.kolomolo.service.openaiclient.exception.GlobalExceptionHandler;
import com.github.kolomolo.service.openaiclient.model.request.ChatRequest;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtAuthenticationHandler;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtTokenExtractor;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtService;
import com.github.kolomolo.service.openaiclient.security.SecurityPathMatcher;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtAuthenticationFilter;
import com.github.kolomolo.service.openaiclient.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
class ChatControllerTest {
    private static final String MOCK_TOKEN = TestConstants.HttpHeaders.BEARER_PREFIX + TestConstants.JwtTokens.VALID;
    private static final String VALID_TOKEN = TestConstants.JwtTokens.VALID;

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
                .thenReturn(TestConstants.TestData.USERNAME);
    }

    @Test
    @WithMockUser(username = TestConstants.TestData.USERNAME)
    void chat_WithValidRequest_ShouldReturnResponse() throws Exception {
        ChatRequest request = new ChatRequest(TestConstants.TestData.MESSAGE);
        String expectedResponse = TestConstants.TestData.CHAT_RESPONSE;

        when(chatService.chat(any(ChatRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post(TestConstants.Endpoints.CHAT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void chat_WithMissingAuthorizationHeader_ShouldReturnUnauthorized() throws Exception {
        ChatRequest request = new ChatRequest(TestConstants.TestData.MESSAGE);

        mockMvc.perform(post(TestConstants.Endpoints.CHAT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void chat_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        ChatRequest request = new ChatRequest(TestConstants.TestData.MESSAGE);

        when(jwtService.validateTokenAndGetUsername(VALID_TOKEN))
                .thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(post(TestConstants.Endpoints.CHAT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TestConstants.TestData.USERNAME)
    void chat_WithEmptyQuestion_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(TestConstants.Endpoints.CHAT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(new ChatRequest(""))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Question cannot be empty")));
    }

    @Test
    @WithMockUser(username = TestConstants.TestData.USERNAME)
    void chat_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        when(chatService.chat(any(ChatRequest.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(post(TestConstants.Endpoints.CHAT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION, MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(new ChatRequest(TestConstants.TestData.MESSAGE))))
                .andExpect(status().isInternalServerError());
    }
}