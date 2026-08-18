package com.fitple.fitple.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileContentService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public String extractText(String fileUrl, String contentType) {

        if (fileUrl == null || fileUrl.isBlank()) {
            return "";
        }

        Path filePath = convertUrlToPath(fileUrl);

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException(
                    "파일을 찾을 수 없습니다: " + fileUrl
            );
        }

        try {

            if ("application/pdf".equalsIgnoreCase(contentType)
                    || fileUrl.toLowerCase().endsWith(".pdf")) {

                return extractPdf(filePath);
            }

            if ("text/plain".equalsIgnoreCase(contentType)
                    || fileUrl.toLowerCase().endsWith(".txt")) {

                return Files.readString(filePath);
            }

            return "지원하지 않는 파일 형식입니다: " + contentType;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "파일 내용을 읽는 데 실패했습니다.",
                    e
            );
        }
    }

    private String extractPdf(Path filePath) throws IOException {

        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {

            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(document);
        }
    }

    private Path convertUrlToPath(String fileUrl) {

        String prefix = "/uploads/";

        if (!fileUrl.startsWith(prefix)) {
            throw new IllegalArgumentException(
                    "잘못된 파일 URL입니다: " + fileUrl
            );
        }

        String relativePath =
                fileUrl.substring(prefix.length());

        Path uploadPath = Paths.get(
                System.getProperty("user.dir"),
                uploadDir
        ).toAbsolutePath().normalize();

        Path targetPath =
                uploadPath.resolve(relativePath).normalize();

        if (!targetPath.startsWith(uploadPath)) {
            throw new IllegalArgumentException(
                    "허용되지 않은 파일 경로입니다."
            );
        }

        return targetPath;
    }
}
