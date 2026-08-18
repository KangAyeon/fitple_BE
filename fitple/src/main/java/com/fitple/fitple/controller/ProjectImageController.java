package com.fitple.fitple.controller;

import com.fitple.fitple.dto.response.ImageUploadResponse;
import com.fitple.fitple.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectImageController {

    private final ImageStorageService imageStorageService;

    @Operation(summary = "프로젝트 대표 이미지 업로드", description = "이미지를 업로드하고 저장된 URL을 반환합니다. 반환된 URL을 프로젝트 생성 시 imageUrl로 함께 보내세요.")
    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
        String imageUrl = imageStorageService.store(file);
        return ResponseEntity.ok(ImageUploadResponse.builder().imageUrl(imageUrl).build());
    }
}