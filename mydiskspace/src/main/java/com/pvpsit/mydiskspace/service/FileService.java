package com.pvpsit.mydiskspace.service;



import com.pvpsit.mydiskspace.entity.FileEntity;
import com.pvpsit.mydiskspace.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    private final String UPLOAD_DIR = "C:/uploads/";

    public FileEntity saveFile(MultipartFile file) throws IOException {

        // Ensure directory exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Avoid filename collision
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(fileName);
        fileEntity.setFileType(file.getContentType());
        fileEntity.setFilePath(path.toString());
        fileEntity.setFileSize(file.getSize());

        return fileRepository.save(fileEntity);
    }
}