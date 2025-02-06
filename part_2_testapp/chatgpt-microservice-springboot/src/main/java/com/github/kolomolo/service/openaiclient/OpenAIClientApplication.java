package com.github.kolomolo.service.openaiclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OpenAIClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenAIClientApplication.class, args);
    }

}
