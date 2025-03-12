package com.studytgt.backend.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.UUID;

@RestController
@RequestMapping("/qr")
public class QRController {

    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    public void generateQRCode(HttpServletResponse response) throws Exception {
        String randomId = UUID.randomUUID().toString();
        String url = "https://studytgt.com/studyroom/" + randomId; 
        int width = 200;
        int height = 200;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        OutputStream outputStream = response.getOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        outputStream.flush();
    }
}