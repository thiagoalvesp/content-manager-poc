package com.contentmanager.model;

import lombok.Data;

@Data
public class ConfirmationMessage {
    private String idCapture;
    private String idFile;
    private String status;
    private String message;
} 