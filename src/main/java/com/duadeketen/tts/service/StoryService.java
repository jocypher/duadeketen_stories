package com.duadeketen.tts.service;

import com.duadeketen.tts.entity.DuadeketenStory;
import com.duadeketen.tts.exception.StoryNotFoundException;
import com.duadeketen.tts.repository.StoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryService {
    private final StoryRepository repository;
    private final QrCodeService qrCodeService;
    private final TtsService ttsService;

    public StoryService(StoryRepository repository, QrCodeService qrCodeService, TtsService ttsService) {
        this.repository = repository;
        this.qrCodeService = qrCodeService;
        this.ttsService = ttsService;
    }

    public DuadeketenStory createStory(DuadeketenStory story) {
        return repository.save(story);
    }

    public DuadeketenStory getStoryByPage(int pageNumber) throws Exception {
        DuadeketenStory story = repository.findByPageNumber(pageNumber)
                .orElseThrow(() -> new StoryNotFoundException("page " + pageNumber));

        if (story.getQrCodeUrl() == null || story.getQrCodeUrl().isEmpty()) {
            String storyUrl = "https://duadeketen-frontend.vercel.app/stories/" + pageNumber;
            String qrUrl = qrCodeService.generateQrCode(pageNumber, storyUrl);
            story.setQrCodeUrl(qrUrl);
        }

        if (story.getAudioUrl() == null || story.getAudioUrl().isEmpty()) {
            String audioUrl = ttsService.generateSpeech(pageNumber, story.getGaText(), story.getAudioUrl());
            story.setAudioUrl(audioUrl);
        }

        return repository.save(story);
    }

    public List<DuadeketenStory> getAllStories() {
        return repository.findAll();
    }
}
