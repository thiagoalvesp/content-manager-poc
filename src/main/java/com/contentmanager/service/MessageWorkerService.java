package com.contentmanager.service;

import com.contentmanager.model.ConfirmationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Service
public class MessageWorkerService {

    private static final Logger logger = LoggerFactory.getLogger(MessageWorkerService.class);
    private final SQSService sqsService;
    private final ObjectMapper objectMapper;

    public MessageWorkerService(SQSService sqsService) {
        this.sqsService = sqsService;
        this.objectMapper = new ObjectMapper();
    }

    @Scheduled(fixedDelay = 5000) // Poll every 5 seconds
    public void processMessages() {
        try {
            List<Message> messages = sqsService.receiveMessages();
            
            for (Message message : messages) {
                try {
                    ConfirmationMessage confirmationMessage = objectMapper.readValue(
                            message.body(),
                            ConfirmationMessage.class
                    );
                    
                    // Process the message
                    logger.info("Processing message: {}", confirmationMessage);
                    
                    // Add your business logic here
                    // For example, update database, send notifications, etc.
                    
                    // Delete the message after successful processing
                    sqsService.deleteMessage(message.receiptHandle());
                    
                } catch (Exception e) {
                    logger.error("Error processing message: {}", message.body(), e);
                }
            }
        } catch (Exception e) {
            logger.error("Error receiving messages from SQS", e);
        }
    }
} 