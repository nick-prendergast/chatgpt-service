package com.github.kolomolo.service.openaiclient.common;

public final class ChatConstants {
    private ChatConstants() {
    }

    public static final class Roles {
        public static final String USER = "user";
        public static final String ASSISTANT = "assistant";
    }

    public static final class Session {
        public static final String HISTORY_KEY = "history";
    }

    public static final class Error {
        public static final String ERROR_MESSAGE_DELIMITER = "message\": \"";
        public static final String UNKNOWN_ERROR = "Unknown error occurred";
        public static final String INVALID_TRANSCRIPTION = "Invalid transcription request";
    }
}