package com.hc.stackoverflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@RestController
public class StackoverflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(StackoverflowApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "Application is running!";
    }
}