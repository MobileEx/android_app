package com.pilates.app.model.dto;

import com.google.gson.GsonBuilder;
import com.pilates.app.model.dto.enums.ExperienceLevel;
import com.pilates.app.model.dto.enums.StreamingType;

import java.util.List;

public class ClassFullInfoDto extends StatusMessageDto {
    private Long id;
    private Long setupId;
    private String className;
    private Integer duration;
    private Double price;
    private Integer maxUsers;
    private StreamingType streamingType;
    private ExperienceLevel experienceLevel;
    private String startDateTime;
    private Boolean multipleClasses;
    private Integer numberOfClasses;
    private String description;
    private List<SelectionItemDto> pilatesTypes;
    private List<SelectionItemDto> purposes;
    private List<String> galleryImages;
    private String trainerName;
    private String trainerAvatar;
    private Long trainerId;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, ClassFullInfoDto.class);
    }

    public Long getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public Integer getDuration() {
        return duration;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public StreamingType getStreamingType() {
        return streamingType;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public Boolean getMultipleClasses() {
        return multipleClasses;
    }

    public Integer getNumberOfClasses() {
        return numberOfClasses;
    }

    public String getDescription() {
        return description;
    }

    public List<SelectionItemDto> getPilatesTypes() {
        return pilatesTypes;
    }

    public List<SelectionItemDto> getPurposes() {
        return purposes;
    }

    public List<String> getGalleryImages() {
        return galleryImages;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public String getTrainerAvatar() {
        return trainerAvatar;
    }

    public void setTrainerAvatar(String trainerAvatar) {
        this.trainerAvatar = trainerAvatar;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public Long getSetupId() {
        return setupId;
    }

    public void setSetupId(Long setupId) {
        this.setupId = setupId;
    }
}