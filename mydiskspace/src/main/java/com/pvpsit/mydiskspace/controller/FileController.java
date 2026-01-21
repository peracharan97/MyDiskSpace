package com.pvpsit.mydiskspace.controller;

import com.pvpsit.mydiskspace.entity.FileEntity;
import com.pvpsit.mydiskspace.repository.FileRepository;
import com.pvpsit.mydiskspace.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:3000")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    // 1️⃣ Upload file and save to DB
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileEntity savedFile = fileService.saveFile(file);
            return ResponseEntity.ok("File uploaded successfully. ID: " + savedFile.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }

    // 2️⃣ Download file by ID
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {

        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + id));

        Path path = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }

    // 3️⃣ List all files
    @GetMapping("/all")
    public ResponseEntity<?> listAllFiles() {
        return ResponseEntity.ok(fileRepository.findAll());
    }
}

