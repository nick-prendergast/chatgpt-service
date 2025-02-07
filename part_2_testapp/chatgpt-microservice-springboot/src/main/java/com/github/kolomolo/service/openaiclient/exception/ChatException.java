package com.github.kolomolo.service.openaiclient.exception;

public class ChatException extends RuntimeException {
    public ChatException(String message, Throwable cause) {
        super(message, cause);
    }
}
