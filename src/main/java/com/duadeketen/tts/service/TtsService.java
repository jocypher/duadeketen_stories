package com.duadeketen.tts.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class TtsService {
    private final SupabaseService supabaseService;

    @Value("${flask.tts.url}")
    private String flaskTtsUrl;

    @Value("${tts.mode:TEST}")
    private String ttsMode;

    private final OkHttpClient client = new OkHttpClient();

    public TtsService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public String generateSpeech(int pageNumber, String gaText, String existingAudioUrl) throws Exception {
        // if already exists, reuse
        if (existingAudioUrl != null && !existingAudioUrl.isEmpty()) {
            return existingAudioUrl;
        }

        if ("TEST".equalsIgnoreCase(ttsMode)) {


            File sampleFile = new File("src/main/resources/audios/sample-audio.mp3");

            return supabaseService.uploadFile(
                    sampleFile,
                    "audio/test-story-" + pageNumber + ".mp3"
            );
        } else if ("FLASK".equalsIgnoreCase(ttsMode)) {
            RequestBody body = RequestBody.create(gaText, MediaType.parse("text/plain"));
            Request request = new Request.Builder()
                    .url(flaskTtsUrl + "/generate-speech")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("TTS generation failed: " + response);
                }

                byte[] audioBytes = response.body().bytes();
                return supabaseService.uploadFile(
                        "audio/story-" + pageNumber + ".mp3",
                        audioBytes,
                        "audio/mpeg"
                );
            }
        } else {
            throw new IllegalStateException("Unknown tts.mode: " + ttsMode);
        }
    }
}
