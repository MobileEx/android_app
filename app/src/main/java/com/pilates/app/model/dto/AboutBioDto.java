package com.pilates.app.model.dto;

import com.google.gson.GsonBuilder;

public class AboutBioDto implements Request {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, AboutBioDto.class);
    }
}
