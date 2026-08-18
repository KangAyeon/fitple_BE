package com.fitple.fitple.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    // application.properties에서 설정 안 하면 기본값 "./uploads" 사용
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    // application.properties에서 설정 안 하면 기본값 "http://localhost:8080/images" 사용
    @Value("${file.base-url:http://localhost:8080/images}")
    private String baseUrl;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");
    private static final long MAX_SIZE = 20 * 1024 * 1024; // 20MB

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어있습니다.");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("이미지 용량은 20MB를 초과할 수 없습니다.");
        }

        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("jpg, jpeg, png 파일만 업로드할 수 있습니다.");
        }

        String savedFileName = UUID.randomUUID() + "." + extension;

        try {
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            Path targetPath = dirPath.resolve(savedFileName);
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.", e);
        }

        return baseUrl + "/" + savedFileName;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("파일 확장자를 확인할 수 없습니다.");
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}