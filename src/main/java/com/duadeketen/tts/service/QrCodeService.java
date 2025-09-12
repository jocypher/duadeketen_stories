package com.duadeketen.tts.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class QrCodeService {
    private final SupabaseService supabaseService;

    public QrCodeService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public String generateQrCode(int pageNumber, String storyUrl) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix matrix = qrCodeWriter.encode(storyUrl, BarcodeFormat.QR_CODE, 250, 250);

        File tempFile = File.createTempFile("qrcode-" + pageNumber, ".png");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            MatrixToImageWriter.writeToStream(matrix, "PNG", fos);
        }

        return supabaseService.uploadFile(tempFile, "qrcodes/page-" + pageNumber + ".png");
    }
}
