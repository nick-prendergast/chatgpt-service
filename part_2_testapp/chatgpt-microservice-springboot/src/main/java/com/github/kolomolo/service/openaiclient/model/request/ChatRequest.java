package com.github.kolomolo.service.openaiclient.model.request;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "Question cannot be empty")
        String question
) {
}
