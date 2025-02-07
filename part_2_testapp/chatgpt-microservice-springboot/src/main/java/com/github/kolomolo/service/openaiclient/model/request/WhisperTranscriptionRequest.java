package com.github.kolomolo.service.openaiclient.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class WhisperTranscriptionRequest {
    public String model;
    public  MultipartFile file;
}