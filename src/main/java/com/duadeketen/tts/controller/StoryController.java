package com.duadeketen.tts.controller;

import com.duadeketen.tts.dto.StoryResponse;
import com.duadeketen.tts.entity.DuadeketenStory;
import com.duadeketen.tts.service.StoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    public ResponseEntity<StoryResponse> createStory(@RequestBody DuadeketenStory story) {
        DuadeketenStory saved = storyService.createStory(story);
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping("/page/{pageNumber}")
    public ResponseEntity<StoryResponse> getStoryByPage(@PathVariable int pageNumber) throws Exception {
        DuadeketenStory story = storyService.getStoryByPage(pageNumber);
        return ResponseEntity.ok(toResponse(story));
    }

    @GetMapping
    public ResponseEntity<List<StoryResponse>> getAllStories() {
        List<StoryResponse> responses = storyService.getAllStories()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private StoryResponse toResponse(DuadeketenStory story) {
        return new StoryResponse(
                story.getPageNumber(),
                story.getGaText(),
                story.getQrCodeUrl(),
                story.getAudioUrl()
        );
    }
}
