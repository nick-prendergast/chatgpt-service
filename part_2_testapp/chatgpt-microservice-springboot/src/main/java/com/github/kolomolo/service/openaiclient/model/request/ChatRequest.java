package com.github.kolomolo.service.openaiclient.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
    @NotBlank(message = "Question cannot be empty")
    private String question;
}
