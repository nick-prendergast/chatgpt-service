package com.github.kolomolo.service.openaiclient.restcontroller;

import com.github.kolomolo.service.openaiclient.model.request.TranscriptionRequest;
import com.github.kolomolo.service.openaiclient.model.response.WhisperTranscriptionResponse;
import com.github.kolomolo.service.openaiclient.service.API.TranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transcription")
@RequiredArgsConstructor
public class TranscriptionController {
    private final TranscriptionService transcriptionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WhisperTranscriptionResponse transcribe(@ModelAttribute TranscriptionRequest request) {
        return transcriptionService.transcribe(request);
    }
}

