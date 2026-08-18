package com.fitple.fitple.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public String saveChatFile(MultipartFile file) throws IOException {

        Path uploadPath = Paths.get(
                System.getProperty("user.dir"),
                uploadDir,
                "chat"
        );

        Files.createDirectories(uploadPath);

        String originalFileName = file.getOriginalFilename();

        String extension = "";

        if (originalFileName != null) {
            int extensionIndex =
                    originalFileName.lastIndexOf(".");

            if (extensionIndex >= 0) {
                extension =
                        originalFileName.substring(extensionIndex);
            }
        }

        String savedFileName =
                UUID.randomUUID() + extension;

        Path targetPath =
                uploadPath.resolve(savedFileName);

        file.transferTo(targetPath.toFile());

        return "/uploads/chat/" + savedFileName;
    }

    public String saveProfileFile(MultipartFile file) throws IOException {

        Path uploadPath = Paths.get(
                System.getProperty("user.dir"),
                uploadDir,
                "profile"
        );

        Files.createDirectories(uploadPath);

        String originalFileName = file.getOriginalFilename();

        String extension = "";

        if (originalFileName != null) {
            int extensionIndex =
                    originalFileName.lastIndexOf(".");

            if (extensionIndex >= 0) {
                extension =
                        originalFileName.substring(extensionIndex);
            }
        }

        String savedFileName =
                UUID.randomUUID() + extension;

        Path targetPath =
                uploadPath.resolve(savedFileName);

        file.transferTo(targetPath.toFile());

        return "/uploads/profile/" + savedFileName;
    }
}