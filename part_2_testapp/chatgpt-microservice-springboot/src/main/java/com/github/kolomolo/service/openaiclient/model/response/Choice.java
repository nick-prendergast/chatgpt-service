package com.github.kolomolo.service.openaiclient.model.response;

import com.github.kolomolo.service.openaiclient.model.request.Message;

public record Choice(
        Integer index,
        Message message,
        String finishReason
) {
}

