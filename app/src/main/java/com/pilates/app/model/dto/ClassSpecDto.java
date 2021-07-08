package com.pilates.app.model.dto;

import com.google.gson.GsonBuilder;
import com.pilates.app.model.dto.enums.ExperienceLevel;
import com.pilates.app.model.dto.enums.StreamingType;

public class ClassSpecDto extends StatusMessageDto implements Request {
    private Long id;
    private String className;
    private Integer duration;
    private Double price;
    private Integer maxUsers;
    private StreamingType streamingType;
    private ExperienceLevel experienceLevel;
    private SelectionListDto expLevels;
    private SelectionListDto streamingTypes;
    private SelectionListDto classDurations;


    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, ClassSpecDto.class);
    }

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public StreamingType getStreamingType() {
        return streamingType;
    }

    public void setStreamingType(StreamingType streamingType) {
        this.streamingType = streamingType;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public SelectionListDto getExpLevels() {
        return expLevels;
    }

    public void setExpLevels(SelectionListDto expLevels) {
        this.expLevels = expLevels;
    }

    public SelectionListDto getStreamingTypes() {
        return streamingTypes;
    }

    public void setStreamingTypes(SelectionListDto streamingTypes) {
        this.streamingTypes = streamingTypes;
    }

    public SelectionListDto getClassDurations() {
        return classDurations;
    }

    public void setClassDurations(SelectionListDto classDurations) {
        this.classDurations = classDurations;
    }
}
