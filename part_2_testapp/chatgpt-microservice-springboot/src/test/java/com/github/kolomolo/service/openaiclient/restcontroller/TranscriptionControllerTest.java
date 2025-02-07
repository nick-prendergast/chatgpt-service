package com.github.kolomolo.service.openaiclient.restcontroller;

import com.github.kolomolo.service.openaiclient.config.SecurityConfig;
import com.github.kolomolo.service.openaiclient.exception.GlobalExceptionHandler;
import com.github.kolomolo.service.openaiclient.model.request.TranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import com.github.kolomolo.service.openaiclient.security.JwtAuthenticationHandler;
import com.github.kolomolo.service.openaiclient.security.JwtTokenExtractor;
import com.github.kolomolo.service.openaiclient.security.JwtService;
import com.github.kolomolo.service.openaiclient.security.SecurityPathMatcher;
import com.github.kolomolo.service.openaiclient.security.JwtAuthenticationFilter;
import com.github.kolomolo.service.openaiclient.service.TranscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TranscriptionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class, GlobalExceptionHandler.class, JwtTokenExtractor.class,           // Add this
        JwtAuthenticationHandler.class,
        SecurityPathMatcher.class})
@TestPropertySource(properties = {
        "jwt.secret-key=test-secret-key",
        "jwt.username=testuser",
        "jwt.password=testpass"
})
class TranscriptionControllerTest {
    private static final String MOCK_TOKEN = "Bearer mock.jwt.token";
    private static final String VALID_TOKEN = "mock.jwt.token";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TranscriptionService transcriptionService;
    @MockBean
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        when(jwtService.validateTokenAndGetUsername(VALID_TOKEN))
                .thenReturn("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void transcribe_WithValidRequest_ShouldReturnResponse() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", "test audio".getBytes()
        );

        when(transcriptionService.transcribe(any(TranscriptionRequest.class)))
                .thenReturn(new WhisperTranscriptionResponse("Test transcription"));

        mockMvc.perform(multipart("/api/v1/transcription")
                        .file(audioFile)
                        .header("Authorization", MOCK_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Test transcription"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void transcribe_WithMissingFile_ShouldReturnBadRequest() throws Exception {
        when(transcriptionService.transcribe(any()))
                .thenThrow(new IllegalArgumentException("Required request part 'file' is not present"));

        mockMvc.perform(multipart("/api/v1/transcription")
                        .header("Authorization", MOCK_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transcribe_WithMissingAuthorizationHeader_ShouldReturnUnauthorized() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", "test audio".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/transcription")
                        .file(audioFile))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void transcribe_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", "test audio".getBytes()
        );

        when(transcriptionService.transcribe(any(TranscriptionRequest.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(multipart("/api/v1/transcription")
                        .file(audioFile)
                        .header("Authorization", MOCK_TOKEN))
                .andExpect(status().isInternalServerError());
    }
}