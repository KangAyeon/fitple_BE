package com.fitple.fitple.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 초대 링크 등 텍스트를 QR코드 이미지(PNG)로 생성해서 로컬 디스크에 저장하고,
 * 접근 가능한 URL을 반환한다. 이미지 업로드(ImageStorageService)와 같은 업로드 폴더/URL 설정을 공유한다.
 */
@Service
public class QrCodeService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080/images}")
    private String baseUrl;

    private static final int QR_SIZE = 300;

    public String generateQrCode(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);

            String fileName = "qr-" + UUID.randomUUID() + ".png";

            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            Path targetPath = dirPath.resolve(fileName);

            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", targetPath);

            return baseUrl + "/" + fileName;

        } catch (WriterException | IOException e) {
            throw new RuntimeException("QR코드 생성 중 오류가 발생했습니다.", e);
        }
    }
}