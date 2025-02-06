package com.github.kolomolo.service.openaiclient.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorMessageExtractorTest {

    @Test
    void extract_WithNullMessage_ShouldReturnUnknownError() {
        String result = ErrorMessageExtractor.extract(new RuntimeException());
        assertEquals("Unknown error occurred", result);
    }

    @Test
    void extract_WithSimpleMessage_ShouldReturnOriginalMessage() {
        String message = "Simple error";
        String result = ErrorMessageExtractor.extract(new RuntimeException(message));
        assertEquals(message, result);
    }

    @Test
    void extract_WithNestedMessage_ShouldExtractInnerMessage() {
        String result = ErrorMessageExtractor.extract(
                new RuntimeException("prefix message\": \"Inner error message\" suffix")
        );
        assertEquals("Inner error message", result);
    }

    @Test
    void extract_WithInvalidFormat_ShouldReturnOriginalMessage() {
        String message = "message\": malformed";
        String result = ErrorMessageExtractor.extract(new RuntimeException(message));
        assertEquals(message, result);
    }
}
