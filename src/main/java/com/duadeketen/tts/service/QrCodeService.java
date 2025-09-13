package com.duadeketen.tts.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;


@Service
public class QrCodeService {
    private final SupabaseService supabaseService;

    public QrCodeService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public String generateQrCode(int pageNumber, String storyUrl) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix matrix = qrCodeWriter.encode(storyUrl, BarcodeFormat.QR_CODE, 250, 250);

        // Write QR code to memory
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
        byte[] qrBytes = baos.toByteArray();

        // Upload to Supabase directly from memory
        return supabaseService.uploadFile(
                "qrcodes/page-" + pageNumber + ".png",
                qrBytes,
                "image/png"
        );
    }


}
