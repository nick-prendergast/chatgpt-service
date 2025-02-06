package com.github.kolomolo.service.openaiclient.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class WhisperTranscriptionRequest {

    private String model;
    private MultipartFile file;
}
