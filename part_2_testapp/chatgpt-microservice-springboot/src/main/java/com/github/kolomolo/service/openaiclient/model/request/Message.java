package com.github.kolomolo.service.openaiclient.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String role;
    private String content;
}
