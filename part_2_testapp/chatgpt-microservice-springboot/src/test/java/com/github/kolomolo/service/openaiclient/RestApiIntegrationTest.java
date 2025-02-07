package com.github.kolomolo.service.openaiclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kolomolo.service.openaiclient.model.request.AuthenticationRequest;
import com.github.kolomolo.service.openaiclient.model.request.ChatRequest;
import com.github.kolomolo.service.openaiclient.model.request.TranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.response.AuthenticationResponse;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import com.github.kolomolo.service.openaiclient.service.AuthenticationService;
import com.github.kolomolo.service.openaiclient.service.ChatService;
import com.github.kolomolo.service.openaiclient.service.TranscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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

    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Setup authentication token
        AuthenticationRequest authRequest = new AuthenticationRequest("test@example.com", "password");
        AuthenticationResponse authResponse = new AuthenticationResponse("test-jwt-token");
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(authResponse);

        jwtToken = authResponse.token();
    }

    @Test
    void authentication_WithValidCredentials_ShouldReturnToken() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password");

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void chat_WithValidToken_ShouldReturnResponse() throws Exception {
        // given
        String expectedResponse = "Test chat response";
        ChatRequest chatRequest = new ChatRequest("Test message");
        when(chatService.chat(any(ChatRequest.class))).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(post("/api/v1/chat")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void chat_WithoutToken_ShouldReturn401() throws Exception {
        // given
        ChatRequest chatRequest = new ChatRequest("Test message");

        // when & then
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void transcription_WithValidTokenAndFile_ShouldReturnTranscription() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                "test audio content".getBytes()
        );
        WhisperTranscriptionResponse expectedResponse = new WhisperTranscriptionResponse("transcribed text");
        when(transcriptionService.transcribe(any(TranscriptionRequest.class)))
                .thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(multipart("/api/v1/transcription")
                        .file(file)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("transcribed text"));
    }

    @Test
    void chat_WithInvalidRequest_ShouldReturn400() throws Exception {
        // given
        ChatRequest invalidRequest = new ChatRequest(""); // Assuming empty message is invalid

        // when & then
        mockMvc.perform(post("/api/v1/chat")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void chat_WithExpiredToken_ShouldReturn401() throws Exception {
        // given
        String expiredToken = "expired.jwt.token";
        ChatRequest chatRequest = new ChatRequest("Test message");

        // when & then
        mockMvc.perform(post("/api/v1/chat")
                        .header("Authorization", "Bearer " + expiredToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isUnauthorized());
    }
}