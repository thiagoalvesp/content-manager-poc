package com.contentmanager.controller;

import com.contentmanager.model.ConfirmationMessage;
import com.contentmanager.service.SQSService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/confirmation")
public class ConfirmationController {

    private final SQSService sqsService;
    private final ObjectMapper objectMapper;

    public ConfirmationController(SQSService sqsService) {
        this.sqsService = sqsService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping
    public ResponseEntity<Void> sendConfirmation(@RequestBody ConfirmationMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            sqsService.sendMessage(messageJson);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 