package com.github.kolomolo.service.openaiclient.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kolomolo.service.openaiclient.TestConstants;
import com.github.kolomolo.service.openaiclient.config.SecurityConfig;
import com.github.kolomolo.service.openaiclient.exception.UnauthorizedException;
import com.github.kolomolo.service.openaiclient.model.request.AuthenticationRequest;
import com.github.kolomolo.service.openaiclient.model.response.AuthenticationResponse;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtAuthenticationHandler;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtTokenExtractor;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtService;
import com.github.kolomolo.service.openaiclient.security.SecurityPathMatcher;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtAuthenticationFilter;
import com.github.kolomolo.service.openaiclient.service.API.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({
        JwtTokenExtractor.class,
        JwtAuthenticationHandler.class,
        SecurityPathMatcher.class,
        JwtService.class,
        JwtAuthenticationFilter.class,
        SecurityConfig.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(
                TestConstants.TestData.USERNAME,
                TestConstants.TestData.PASSWORD
        );
        AuthenticationResponse response = new AuthenticationResponse("mockToken");

        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post(TestConstants.Endpoints.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"));
    }

    @Test
    void login_WithMissingUsername_ShouldReturnBadRequest() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("", TestConstants.TestData.PASSWORD);

        mockMvc.perform(post(TestConstants.Endpoints.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username is required"));
    }

    @Test
    void login_WithMissingPassword_ShouldReturnBadRequest() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("testUser", "");

        mockMvc.perform(post(TestConstants.Endpoints.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Password is required"));
    }

    @Test
    void login_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{\"username\": \"testUser\", \"password\":}";

        mockMvc.perform(post(TestConstants.Endpoints.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("JSON parse error")));
    }

    @Test
    void login_WithUnsupportedMediaType_ShouldReturnUnsupportedMediaType() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("testUser", "testPassword");

        mockMvc.perform(post(TestConstants.Endpoints.LOGIN)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string("Unsupported content type. Please use application/json"));
    }

    @Test
    void login_WithUnknownField_ShouldReturnBadRequest() throws Exception {
        String jsonWithUnknownField = "{\"username\":\"testUser\",\"password\":\"testPassword\",\"unknown\":\"value\"}";

        mockMvc.perform(post(TestConstants.Endpoints.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithUnknownField))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Unrecognized field \"unknown\"")))
                .andExpect(content().string(containsString("not marked as ignorable")));
    }

    @Test
    void login_WhenUnauthorized_ShouldReturnUnauthorized() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("testUser", "testPassword");

        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new UnauthorizedException("Invalid credentials"));

        mockMvc.perform(post(TestConstants.Endpoints.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void login_WhenRuntimeException_ShouldReturnInternalServerError() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("testUser", "testPassword");

        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(post(TestConstants.Endpoints.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }
}