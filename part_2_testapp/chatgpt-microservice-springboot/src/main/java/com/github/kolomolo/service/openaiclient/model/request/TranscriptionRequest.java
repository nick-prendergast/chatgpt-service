package com.github.kolomolo.service.openaiclient.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TranscriptionRequest {

    private MultipartFile file;
}
