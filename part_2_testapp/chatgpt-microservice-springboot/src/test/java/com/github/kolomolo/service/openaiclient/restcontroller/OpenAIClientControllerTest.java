package com.github.kolomolo.service.openaiclient.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kolomolo.service.openaiclient.exception.GlobalExceptionHandler;
import com.github.kolomolo.service.openaiclient.model.request.ChatRequest;
import com.github.kolomolo.service.openaiclient.model.request.TranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import com.github.kolomolo.service.openaiclient.security.JwtAuthenticationFilter;
import com.github.kolomolo.service.openaiclient.security.JwtService;
import com.github.kolomolo.service.openaiclient.security.SecurityConfig;
import com.github.kolomolo.service.openaiclient.service.OpenAIClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OpenAIClientController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
        "jwt.secret-key=test-secret-key",
        "jwt.username=testuser",
        "jwt.password=testpass"
})
class OpenAIClientControllerTest {

    private static final String MOCK_TOKEN = "Bearer mock.jwt.token";
    private static final String VALID_TOKEN = "mock.jwt.token";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OpenAIClientService openAIClientService;
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

        when(openAIClientService.chat(any(ChatRequest.class)))
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
        ChatRequest request = new ChatRequest("");

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Question cannot be empty")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_WithNullQuestion_ShouldReturnBadRequest() throws Exception {
        ChatRequest request = new ChatRequest(null);

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Question cannot be empty")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_WithUnsupportedMediaType_ShouldReturnUnsupportedMediaType() throws Exception {
        ChatRequest request = new ChatRequest("What is OpenAI?");

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.TEXT_PLAIN)
                        .header("Authorization", MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string(containsString("Unsupported content type. Please use application/json")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chat_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        ChatRequest request = new ChatRequest("What is OpenAI?");

        when(openAIClientService.chat(any(ChatRequest.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", MOCK_TOKEN)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createTranscription_WithValidRequest_ShouldReturnResponse() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                "test audio content".getBytes()
        );

        WhisperTranscriptionResponse expectedResponse = new WhisperTranscriptionResponse("Test transcription");

        when(openAIClientService.createTranscription(any(TranscriptionRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(multipart("/api/v1/transcription")
                        .file(audioFile)
                        .header("Authorization", MOCK_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Test transcription"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createTranscription_WithMissingFile_ShouldReturnBadRequest() throws Exception {
        when(openAIClientService.createTranscription(any()))
                .thenThrow(new IllegalArgumentException("Required request part 'file' is not present"));

        mockMvc.perform(multipart("/api/v1/transcription")
                        .header("Authorization", MOCK_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Required request part 'file' is not present")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createTranscription_WithInvalidFileType_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "invalid content".getBytes()
        );

        when(openAIClientService.createTranscription(any()))
                .thenThrow(new IllegalArgumentException("Unsupported file type"));

        mockMvc.perform(multipart("/api/v1/transcription")
                        .file(invalidFile)
                        .header("Authorization", MOCK_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Unsupported file type")));
    }

    @Test
    void createTranscription_WithMissingAuthorizationHeader_ShouldReturnUnauthorized() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                "test audio content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/transcription")
                        .file(audioFile))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createTranscription_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                "test audio content".getBytes()
        );

        when(openAIClientService.createTranscription(any(TranscriptionRequest.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(multipart("/api/v1/transcription")
                        .file(audioFile)
                        .header("Authorization", MOCK_TOKEN))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }
}