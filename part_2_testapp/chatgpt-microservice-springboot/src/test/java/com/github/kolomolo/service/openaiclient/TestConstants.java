package com.github.kolomolo.service.openaiclient;

public final class TestConstants {
    private TestConstants() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static final class HttpHeaders {
        public static final String AUTHORIZATION = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";
    }

    public static final class JwtTokens {
        public static final String VALID = "test-jwt-token";
        public static final String EXPIRED = "expired.jwt.token";
    }

    public static final class Endpoints {
        public static final String LOGIN = "/api/v1/auth/login";
        public static final String CHAT = "/api/v1/chat";
        public static final String TRANSCRIPTION = "/api/v1/transcription";
    }

    public static final class ContentTypes {
        public static final String JSON = "application/json";
        public static final String TEXT = "text/plain";
        public static final String AUDIO_MPEG = "audio/mpeg";
    }

    // Test Data Constants
    public static final class TestData {
        public static final String USERNAME = "testuser";
        public static final String PASSWORD = "testpass";
        public static final String MESSAGE = "Test message";
        public static final String CHAT_RESPONSE = "Test chat response";
        public static final String TRANSCRIBED_TEXT = "transcribed text";
    }
}