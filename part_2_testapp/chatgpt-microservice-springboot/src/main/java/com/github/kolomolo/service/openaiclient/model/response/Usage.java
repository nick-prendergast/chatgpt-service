package com.github.kolomolo.service.openaiclient.model.response;

public record Usage(
        String promptTokens,
        String completionTokens,
        String totalTokens
) {
}
