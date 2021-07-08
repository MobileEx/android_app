package com.pilates.app.model.dto;

public class ClassFilterRequest extends StatusMessageDto {
    private int page;
    private String mode;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}