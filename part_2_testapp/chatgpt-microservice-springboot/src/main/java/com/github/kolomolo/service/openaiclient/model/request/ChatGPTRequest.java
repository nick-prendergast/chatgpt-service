package com.github.kolomolo.service.openaiclient.model.request;

import lombok.Data;
import java.util.List;

@Data
public class ChatGPTRequest {

    private String model;
    private List<Message> messages;
}
