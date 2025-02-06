package com.github.kolomolo.service.openaiclient.model.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ChatGPTResponse {
    private String id;
    private String object;
    private String model;
    private LocalDate created;
    private List<Choice> choices;
    private Usage usage;
}
