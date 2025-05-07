package com.contentmanager.model;

import lombok.Data;

@Data
public class FileRequest {
    private String idCapture;
    private String base64Content;
    private String fileName;
} 