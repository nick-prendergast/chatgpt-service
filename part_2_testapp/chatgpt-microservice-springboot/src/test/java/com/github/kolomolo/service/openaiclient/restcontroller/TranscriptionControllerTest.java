package com.github.kolomolo.service.openaiclient.restcontroller;

import com.github.kolomolo.service.openaiclient.TestConstants;
import com.github.kolomolo.service.openaiclient.config.SecurityConfig;
import com.github.kolomolo.service.openaiclient.exception.GlobalExceptionHandler;
import com.github.kolomolo.service.openaiclient.model.request.TranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtAuthenticationHandler;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtTokenExtractor;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtService;
import com.github.kolomolo.service.openaiclient.security.SecurityPathMatcher;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtAuthenticationFilter;
import com.github.kolomolo.service.openaiclient.service.API.TranscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TranscriptionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class, GlobalExceptionHandler.class, JwtTokenExtractor.class,
        JwtAuthenticationHandler.class,
        SecurityPathMatcher.class})
class TranscriptionControllerTest {
    private static final String MOCK_TOKEN = TestConstants.HttpHeaders.BEARER_PREFIX + TestConstants.JwtTokens.VALID;
    private static final String VALID_TOKEN = TestConstants.JwtTokens.VALID;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TranscriptionService transcriptionService;
    @MockBean
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        when(jwtService.validateTokenAndGetUsername(VALID_TOKEN))
                .thenReturn(TestConstants.TestData.USERNAME);
    }

    @Test
    @WithMockUser(username = TestConstants.TestData.USERNAME)
    void transcribe_WithValidRequest_ShouldReturnResponse() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file", "test.mp3", TestConstants.ContentTypes.AUDIO_MPEG, "test audio".getBytes()
        );

        when(transcriptionService.transcribe(any(TranscriptionRequest.class)))
                .thenReturn(new WhisperTranscriptionResponse(TestConstants.TestData.TRANSCRIBED_TEXT));

        mockMvc.perform(multipart(TestConstants.Endpoints.TRANSCRIPTION)
                        .file(audioFile)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION, MOCK_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(TestConstants.TestData.TRANSCRIBED_TEXT));
    }

    @Test
    @WithMockUser(username = TestConstants.TestData.USERNAME)
    void transcribe_WithMissingFile_ShouldReturnBadRequest() throws Exception {
        when(transcriptionService.transcribe(any()))
                .thenThrow(new IllegalArgumentException("Required request part 'file' is not present"));

        mockMvc.perform(multipart(TestConstants.Endpoints.TRANSCRIPTION)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION, MOCK_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transcribe_WithMissingAuthorizationHeader_ShouldReturnUnauthorized() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file", "test.mp3", TestConstants.ContentTypes.AUDIO_MPEG, "test audio".getBytes()
        );

        mockMvc.perform(multipart(TestConstants.Endpoints.TRANSCRIPTION)
                        .file(audioFile))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TestConstants.TestData.USERNAME)
    void transcribe_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file", "test.mp3", TestConstants.ContentTypes.AUDIO_MPEG, "test audio".getBytes()
        );

        when(transcriptionService.transcribe(any(TranscriptionRequest.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(multipart(TestConstants.Endpoints.TRANSCRIPTION)
                        .file(audioFile)
                        .header(TestConstants.HttpHeaders.AUTHORIZATION, MOCK_TOKEN))
                .andExpect(status().isInternalServerError());
    }
}