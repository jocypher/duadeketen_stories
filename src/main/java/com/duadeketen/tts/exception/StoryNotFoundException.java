package com.duadeketen.tts.exception;

public class StoryNotFoundException extends RuntimeException {
    public StoryNotFoundException(String message) {
        super("Story not found with id " + message);
    }

    public StoryNotFoundException(Long id) {
        super("Story not found with id " + id);
    }
}
