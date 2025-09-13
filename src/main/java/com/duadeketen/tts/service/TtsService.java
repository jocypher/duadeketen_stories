package com.duadeketen.tts.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


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
            return existingAudioUrl; // already generated
        }

        // Send Ga text to Flask
        RequestBody body = RequestBody.create(gaText, MediaType.parse("text/plain"));
        Request request = new Request.Builder()
                .url(flaskTtsUrl + "/generate-speech")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("TTS generation failed: " + response);
            }

            // Get audio bytes directly from Flask response
            byte[] audioBytes = response.body().bytes();

            // Upload to Supabase as mp3
            return supabaseService.uploadFile(
                    "audio/story-" + pageNumber + ".mp3",
                    audioBytes,
                    "audio/mpeg"
            );
        }
    }
}

