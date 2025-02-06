package com.github.kolomolo.service.openaiclient.model.request;

import java.util.List;

public record ChatGPTRequest(String model, List<Message> messages) { }
