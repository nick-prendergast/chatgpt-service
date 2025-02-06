package com.github.kolomolo.service.openaiclient.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.Path;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleUnauthorizedException_ShouldReturnUnauthorizedResponse() {
        // Given
        UnauthorizedException ex = new UnauthorizedException("Unauthorized access");

        // When
        ResponseEntity<Object> response = exceptionHandler.handleUnauthorizedException(ex);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized access", response.getBody());
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerErrorResponse() {
        // Given
        RuntimeException ex = new RuntimeException("Internal error");

        // When
        ResponseEntity<Object> response = exceptionHandler.handleRuntimeException(ex);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal error", response.getBody());
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithFirstFieldError() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "Validation error message");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        // When
        ResponseEntity<Object> response = exceptionHandler.handleValidationExceptions(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation error message", response.getBody());
    }

    @Test
    void handleMediaTypeNotSupported_ShouldReturnUnsupportedMediaTypeResponse() {
        // Given
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("Unsupported media type");

        // When
        ResponseEntity<String> response = exceptionHandler.handleMediaTypeNotSupported();

        // Then
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertEquals("Unsupported content type. Please use application/json", response.getBody());
    }

    @Test
    void handleHttpMessageNotReadable_ShouldReturnBadRequestResponse() {
        // Given
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Not readable");

        // When
        ResponseEntity<Object> response = exceptionHandler.handleHttpMessageNotReadable(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Not readable", response.getBody());
    }

    @Test
    void handleUnknownFields_ShouldReturnBadRequestWithFieldDetails() {
        // Given
        UnrecognizedPropertyException ex = mock(UnrecognizedPropertyException.class);
        when(ex.getPropertyName()).thenReturn("unknownField");

        // Use Mockito's any() matcher for the class
        when(ex.getReferringClass()).thenAnswer(invocation -> TestClassForUnknownFieldsTest.class);

        // When
        ResponseEntity<String> response = exceptionHandler.handleUnknownFields(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unknown field: 'unknownField'. Expected fields: testField", response.getBody());
    }

    // Helper class for testing unknown fields
    private static class TestClassForUnknownFieldsTest {
        private String testField;
    }


    @Test
    void handleConstraintViolationException_ShouldReturnBadRequestWithViolationDetails() {
        // Given
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(mock(Path.class));
        when(violation.getPropertyPath().toString()).thenReturn("fieldName");
        when(violation.getMessage()).thenReturn("Constraint violation message");

        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        when(ex.getConstraintViolations()).thenReturn(violations);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleConstraintViolationException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("fieldName: Constraint violation message", response.getBody());
    }

}