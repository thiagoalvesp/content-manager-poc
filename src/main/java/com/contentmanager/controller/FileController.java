package com.contentmanager.controller;

import com.contentmanager.model.FileRequest;
import com.contentmanager.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final S3Service s3Service;

    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestBody FileRequest request) {
        String idFileFolder = s3Service.uploadFile(
            request.getIdCapture(),
            request.getBase64Content(),
            request.getFileName()
        );
        return ResponseEntity.ok(idFileFolder);
    }

    @GetMapping("/list/{idCapture}")
    public ResponseEntity<List<String>> listFiles(@PathVariable String idCapture) {
        List<String> files = s3Service.listFiles(idCapture);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/{idCapture}/{idFile}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String idCapture,
            @PathVariable String idFile) {
        s3Service.deleteFile(idCapture, idFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{idCapture}/{idFile}")
    public ResponseEntity<String> downloadFile(
            @PathVariable String idCapture,
            @PathVariable String idFile) {
        String base64Content = s3Service.downloadFile(idCapture, idFile);
        return ResponseEntity.ok(base64Content);
    }
} 