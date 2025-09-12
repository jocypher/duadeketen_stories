package com.duadeketen.tts.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class TtsService {
    private final SupabaseService supabaseService;

    @Value("${flask.tts.url}")
    private String flaskTtsUrl;

    private final OkHttpClient client = new OkHttpClient();

    public TtsService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public String generateSpeech(int pageNumber, String gaText, String existingAudioUrl) throws Exception {
        if (existingAudioUrl != null && !existingAudioUrl.isEmpty()) {
            return existingAudioUrl;
        }

        RequestBody body = RequestBody.create(gaText, MediaType.parse("text/plain"));
        Request request = new Request.Builder()
                .url(flaskTtsUrl + "/generate-speech")
                .post(body)
                .build();

        File tempFile = File.createTempFile("story-" + pageNumber, ".mp3");

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("TTS generation failed: " + response);
            }
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                if (response.body() != null) {
                    fos.write(response.body().bytes());
                }
            }
        }

        return supabaseService.uploadFile(tempFile, "audio/story-" + pageNumber + ".mp3");
    }
}
