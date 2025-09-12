package com.duadeketen.tts.service;

import com.duadeketen.tts.config.SupabaseProperties;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;

@Service
public class SupabaseService {
    private final OkHttpClient client = new OkHttpClient();
    private final SupabaseProperties properties;

    public SupabaseService(SupabaseProperties properties) {
        this.properties = properties;
    }

    public String uploadFile(String path, byte[] fileContent, String contentType) throws Exception {
        String url = properties.getUrl() + "/storage/v1/object/" + properties.getBucket() + "/" + path;

        RequestBody body = RequestBody.create(fileContent, MediaType.parse(contentType));
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + properties.getKey())
                .header("apikey", properties.getKey())
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Upload failed: " + response);
            }
        }

        return properties.getUrl() + "/storage/v1/object/public/" + properties.getBucket() + "/" + path;
    }

    public String uploadFile(File file, String path) throws Exception {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        String contentType = guessContentType(path);
        return uploadFile(path, fileContent, contentType);
    }

    private String guessContentType(String path) {
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".mp3")) return "audio/mpeg";
        return "application/octet-stream";
    }
}
