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
        jwtToken = TestConstants.JwtTokens.VALID;

        // Setup auth service mock
        AuthenticationResponse authResponse = new AuthenticationResponse(jwtToken);
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(authResponse);

        // Setup JWT service mock
        when(jwtService.validateTokenAndGetUsername(eq(jwtToken)))
                .thenReturn(TestConstants.TestData.USERNAME);
        when(jwtService.validateTokenAndGetUsername(eq(TestConstants.JwtTokens.EXPIRED)))
                .thenThrow(new ExpiredJwtException(null, null, "Token has expired"));

        // Setup chat service mock
        when(chatService.chat(any(ChatRequest.class)))
                .thenReturn(TestConstants.TestData.CHAT_RESPONSE);

        // Setup transcription service mock
        when(transcriptionService.transcribe(any()))
                .thenReturn(new WhisperTranscriptionResponse(TestConstants.TestData.TRANSCRIBED_TEXT));

        // Setup security path matcher
        when(securityPathMatcher.shouldFilter(anyString())).thenReturn(true);
    }

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void chat_WithValidToken_ShouldReturnResponse() throws Exception {
        ChatRequest chatRequest = new ChatRequest(TestConstants.TestData.MESSAGE);

        mockMvc.perform(post(TestConstants.Endpoints.CHAT)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION,
                                TestConstants.HttpHeaders.BEARER_PREFIX + jwtToken)
                        .contentType(TestConstants.ContentTypes.JSON)
                        .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(TestConstants.TestData.CHAT_RESPONSE));
    }

    @Test
    void chat_WithInvalidRequest_ShouldReturn400() throws Exception {
        ChatRequest invalidRequest = new ChatRequest("");

        mockMvc.perform(post(TestConstants.Endpoints.CHAT)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION,
                                TestConstants.HttpHeaders.BEARER_PREFIX + jwtToken)
                        .contentType(TestConstants.ContentTypes.JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transcription_WithValidTokenAndFile_ShouldReturnTranscription() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.mp3",
                TestConstants.ContentTypes.AUDIO_MPEG,
                "test audio content".getBytes()
        );

        MvcResult result = mockMvc.perform(multipart(TestConstants.Endpoints.TRANSCRIPTION)
                        .file(file)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION,
                                TestConstants.HttpHeaders.BEARER_PREFIX + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        WhisperTranscriptionResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                WhisperTranscriptionResponse.class
        );
        assertEquals(TestConstants.TestData.TRANSCRIBED_TEXT, response.text());
    }

    @Test
    void chat_WithoutToken_ShouldReturn401() throws Exception {
        ChatRequest chatRequest = new ChatRequest(TestConstants.TestData.MESSAGE);

        mockMvc.perform(post(TestConstants.Endpoints.CHAT)
                        .contentType(TestConstants.ContentTypes.JSON)
                        .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void chat_WithExpiredToken_ShouldReturn401() throws Exception {
        ChatRequest chatRequest = new ChatRequest(TestConstants.TestData.MESSAGE);

        mockMvc.perform(post(TestConstants.Endpoints.CHAT)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION,
                                TestConstants.HttpHeaders.BEARER_PREFIX + TestConstants.JwtTokens.EXPIRED)
                        .contentType(TestConstants.ContentTypes.JSON)
                        .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void chat_WithInvalidContentType_ShouldReturn415() throws Exception {
        mockMvc.perform(post(TestConstants.Endpoints.CHAT)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION,
                                TestConstants.HttpHeaders.BEARER_PREFIX + jwtToken)
                        .contentType(TestConstants.ContentTypes.TEXT)
                        .content(TestConstants.TestData.MESSAGE))
                .andExpect(status().isUnsupportedMediaType());
    }
}