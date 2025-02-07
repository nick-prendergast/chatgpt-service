package com.github.kolomolo.service.openaiclient;


public class TestConstants {
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String VALID_JWT = "test-jwt-token";
    public static final String EXPIRED_JWT = "expired.jwt.token";
    public static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    public static final String CHAT_ENDPOINT = "/api/v1/chat";
    public static final String TRANSCRIPTION_ENDPOINT = "/api/v1/transcription";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final String CONTENT_TYPE_AUDIO = "audio/mpeg";
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_PASSWORD = "testpass";
    public static final String TEST_MESSAGE = "Test message";
    public static final String TEST_CHAT_RESPONSE = "Test chat response";
    public static final String TRANSCRIBED_TEXT = "transcribed text";
}
