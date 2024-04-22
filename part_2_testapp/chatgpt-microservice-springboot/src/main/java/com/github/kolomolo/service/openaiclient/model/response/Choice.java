package com.github.kolomolo.service.openaiclient.model.response;

import com.github.kolomolo.service.openaiclient.model.request.Message;
import lombok.Data;

import java.io.Serializable;

@Data
public class Choice implements Serializable {
    private Integer index;
    private Message message;
    private String finishReason;
}
