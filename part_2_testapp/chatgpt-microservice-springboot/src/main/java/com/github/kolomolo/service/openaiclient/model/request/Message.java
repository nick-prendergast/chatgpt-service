package com.github.kolomolo.service.openaiclient.model.request;

public record Message(String role, String content) {
    public String getRole() { return role; }
    public String getContent() { return content; }
}
// JSP EL requires JavaBean-style getters; without them, it throws PropertyNotFoundException
