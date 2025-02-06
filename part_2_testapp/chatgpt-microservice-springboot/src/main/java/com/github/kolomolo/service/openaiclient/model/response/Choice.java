package com.github.kolomolo.service.openaiclient.model.response;

import com.github.kolomolo.service.openaiclient.model.request.Message;
import lombok.Data;

@Data
public class Choice {
    private Integer index;
    private Message message;
    private String finishReason;
}
