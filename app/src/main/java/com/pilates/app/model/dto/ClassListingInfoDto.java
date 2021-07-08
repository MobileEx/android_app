package com.pilates.app.model.dto;

import java.util.ArrayList;

public class ClassListingInfoDto extends StatusMessageDto {
    private Long id;
    private String className;
    private Integer duration;
    private String startDateTime;
    private ArrayList<SelectionItemDto> purposes;
    private String mainImage;
    private String trainerAvatar;
    private Double rating;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public ArrayList<SelectionItemDto> getPurposes() {
        return purposes;
    }

    public void setPurposes(ArrayList<SelectionItemDto> purposes) {
        this.purposes = purposes;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getTrainerAvatar() {
        return trainerAvatar;
    }

    public void setTrainerAvatar(String trainerAvatar) {
        this.trainerAvatar = trainerAvatar;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}