package com.github.kolomolo.service.openaiclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kolomolo.service.openaiclient.model.request.AuthenticationRequest;
import com.github.kolomolo.service.openaiclient.model.request.ChatRequest;
import com.github.kolomolo.service.openaiclient.model.response.AuthenticationResponse;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import com.github.kolomolo.service.openaiclient.security.SecurityPathMatcher;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtService;
import com.github.kolomolo.service.openaiclient.service.API.AuthenticationService;
import com.github.kolomolo.service.openaiclient.service.ChatService;
import com.github.kolomolo.service.openaiclient.service.API.TranscriptionService;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private ChatService chatService;

    @MockBean
    private TranscriptionService transcriptionService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private SecurityPathMatcher securityPathMatcher;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        jwtToken = VALID_JWT;

        // Setup auth service mock
        AuthenticationResponse authResponse = new AuthenticationResponse(jwtToken);
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(authResponse);

        // Setup JWT service mock
        when(jwtService.validateTokenAndGetUsername(eq(jwtToken)))
                .thenReturn(TEST_USERNAME);
        when(jwtService.validateTokenAndGetUsername(eq(EXPIRED_JWT)))
                .thenThrow(new ExpiredJwtException(null, null, "Token has expired"));

        // Setup chat service mock
        when(chatService.chat(any(ChatRequest.class)))
                .thenReturn(TEST_CHAT_RESPONSE);

        // Setup transcription service mock
        when(transcriptionService.transcribe(any()))
                .thenReturn(new WhisperTranscriptionResponse(TRANSCRIBED_TEXT));

        // Setup security path matcher
        when(securityPathMatcher.shouldFilter(anyString())).thenReturn(true);
    }

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authentication_WithValidCredentials_ShouldReturnToken() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(TEST_USERNAME, TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(CONTENT_TYPE_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AuthenticationResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );
        assertEquals(VALID_JWT, response.token());
    }

    @Test
    void chat_WithValidToken_ShouldReturnResponse() throws Exception {
        ChatRequest chatRequest = new ChatRequest(TEST_MESSAGE);

        mockMvc.perform(post(CHAT_ENDPOINT)
                        .header(AUTH_HEADER, BEARER_PREFIX + jwtToken)
                        .contentType(CONTENT_TYPE_JSON)
                        .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_CHAT_RESPONSE));
    }

    @Test
    void chat_WithInvalidRequest_ShouldReturn400() throws Exception {
        ChatRequest invalidRequest = new ChatRequest("");

        mockMvc.perform(post(CHAT_ENDPOINT)
                        .header(AUTH_HEADER, BEARER_PREFIX + jwtToken)
                        .contentType(CONTENT_TYPE_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transcription_WithValidTokenAndFile_ShouldReturnTranscription() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.mp3",
                CONTENT_TYPE_AUDIO,
                "test audio content".getBytes()
        );

        MvcResult result = mockMvc.perform(multipart(TRANSCRIPTION_ENDPOINT)
                        .file(file)
                        .header(AUTH_HEADER, BEARER_PREFIX + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        WhisperTranscriptionResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                WhisperTranscriptionResponse.class
        );
        assertEquals(TRANSCRIBED_TEXT, response.text());
    }

    @Test
    void chat_WithoutToken_ShouldReturn401() throws Exception {
        ChatRequest chatRequest = new ChatRequest(TEST_MESSAGE);

        mockMvc.perform(post(CHAT_ENDPOINT)
                        .contentType(CONTENT_TYPE_JSON)
                        .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void chat_WithExpiredToken_ShouldReturn401() throws Exception {
        ChatRequest chatRequest = new ChatRequest(TEST_MESSAGE);

        mockMvc.perform(post(CHAT_ENDPOINT)
                        .header(AUTH_HEADER, BEARER_PREFIX + EXPIRED_JWT)
                        .contentType(CONTENT_TYPE_JSON)
                        .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void chat_WithInvalidContentType_ShouldReturn415() throws Exception {
        mockMvc.perform(post(CHAT_ENDPOINT)
                        .header(AUTH_HEADER, BEARER_PREFIX + jwtToken)
                        .contentType(CONTENT_TYPE_TEXT)
                        .content(TEST_MESSAGE))
                .andExpect(status().isUnsupportedMediaType());
    }

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String VALID_JWT = "test-jwt-token";
    private static final String EXPIRED_JWT = "expired.jwt.token";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    private static final String CHAT_ENDPOINT = "/api/v1/chat";
    private static final String TRANSCRIPTION_ENDPOINT = "/api/v1/transcription";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT = "text/plain";
    private static final String CONTENT_TYPE_AUDIO = "audio/mpeg";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpass";
    private static final String TEST_MESSAGE = "Test message";
    private static final String TEST_CHAT_RESPONSE = "Test chat response";
    private static final String TRANSCRIBED_TEXT = "transcribed text";
    
}
