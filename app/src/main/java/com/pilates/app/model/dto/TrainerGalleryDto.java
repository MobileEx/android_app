package com.pilates.app.model.dto;

import java.util.ArrayList;

public class TrainerGalleryDto {
    private ArrayList<SaveGalleryResponseDto> images;

    public ArrayList<SaveGalleryResponseDto> getImages() {
        return images;
    }

    public void setImages(ArrayList<SaveGalleryResponseDto> images) {
        this.images = images;
    }
}
