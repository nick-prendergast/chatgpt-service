package com.github.kolomolo.service.openaiclient.model.request;

import org.springframework.web.multipart.MultipartFile;

public record WhisperTranscriptionRequest(
        String model,
        MultipartFile file
) {
}