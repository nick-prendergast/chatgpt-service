package com.github.kolomolo.service.openaiclient.model.response;

import lombok.Data;

@Data
public class Usage {

    private String promptTokens;
    private String completionTokens;
    private String totalTokens;
}
