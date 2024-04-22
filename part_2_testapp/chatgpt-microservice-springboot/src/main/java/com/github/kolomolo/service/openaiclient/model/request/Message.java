package com.github.kolomolo.service.openaiclient.model.request;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
public class Message implements Serializable {
    private String role;
    private String content;
}
