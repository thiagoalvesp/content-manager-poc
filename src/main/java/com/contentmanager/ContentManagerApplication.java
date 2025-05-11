package com.contentmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ContentManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentManagerApplication.class, args);
    }
} 