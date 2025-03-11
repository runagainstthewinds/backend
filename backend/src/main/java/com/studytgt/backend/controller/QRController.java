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

@RestController
@RequestMapping("/qr")
public class QRController {

    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    public void generateQRCode(HttpServletResponse response) throws Exception {
        String url = "https://www.youtube.com/watch?v=qz9tKlF431k&ab_channel=Cl%C3%A9mentMihailescu"; // URL to redirect to
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