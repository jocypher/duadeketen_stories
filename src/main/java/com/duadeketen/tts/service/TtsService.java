package com.duadeketen.tts.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TtsService {
    private final SupabaseService supabaseService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${flask.tts.url}")
    private String flaskTtsUrl;

    @Value("${tts.mode:FLASK}")
    private String ttsMode;

    private final OkHttpClient client;

    public TtsService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
        // Configure OkHttp client with reasonable timeouts for TTS operations
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)  // TTS can take time
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public String generateSpeech(int pageNumber, String gaText, String existingAudioUrl) throws Exception {
        // if already exists, reuse
        if (existingAudioUrl != null && !existingAudioUrl.isEmpty()) {
            return existingAudioUrl;
        }

//        if ("TEST".equalsIgnoreCase(ttsMode)) {
//            File sampleFile = new File("src/main/resources/audios/sample-audio.mp3");
//            return supabaseService.uploadFile(
//                    sampleFile,
//                    "audio/test-story-" + pageNumber + ".mp3"
//            );
//        }
//        else if ("FLASK".equalsIgnoreCase(ttsMode)) {
            return handleFlaskMode(pageNumber, gaText);
//        } else {
//            throw new IllegalStateException("Unknown tts.mode: " + ttsMode);
//        }
    }

    private String handleFlaskMode(int pageNumber, String gaText) throws Exception {
        // Create JSON request body as expected by Flask API
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("text", gaText);
        requestData.put("return_audio", true);
        requestData.put("format", "wav");

        String jsonBody = objectMapper.writeValueAsString(requestData);

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://ga-tts-api.onrender.com/api/synthesize")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = "";
                if (response.body() != null) {
                    errorBody = response.body().string();
                }
                throw new RuntimeException(
                        String.format("TTS generation failed: %s - %s", response.code(), errorBody)
                );
            }

            // Check if response is actually audio
            String contentType = response.header("Content-Type", "");
            if (!contentType.contains("audio")) {
                String responseBody = response.body().string();
                throw new RuntimeException("Expected audio response but got: " + responseBody);
            }

            // Get audio bytes from Flask API response
            byte[] audioBytes = response.body().bytes();

            if (audioBytes.length == 0) {
                throw new RuntimeException("Received empty audio response from Flask TTS API");
            }

            // Upload the audio file to Supabase
            // Note: Changed to .wav since your Flask API returns WAV format
            return supabaseService.uploadFile(
                    "audio/story-" + pageNumber + ".wav",
                    audioBytes,
                    "audio/wav"
            );
        }
    }
}