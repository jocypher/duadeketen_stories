package com.duadeketen.tts.dto;

public class StoryResponse {
    private int pageNumber;
    private String gaText;
    private String qrCodeUrl;
    private String audioUrl;

    public StoryResponse() {}

    public StoryResponse(int pageNumber, String gaText, String qrCodeUrl, String audioUrl) {
        this.pageNumber = pageNumber;
        this.gaText = gaText;
        this.qrCodeUrl = qrCodeUrl;
        this.audioUrl = audioUrl;
    }

    // getters + setters
    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public String getGaText() { return gaText; }
    public void setGaText(String gaText) { this.gaText = gaText; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}
