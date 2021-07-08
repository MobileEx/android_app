package com.pilates.app.model.dto;

import com.google.gson.GsonBuilder;
import com.pilates.app.model.dto.enums.ExperienceLevel;
import com.pilates.app.model.dto.enums.StreamingType;

public class ClassTimingDto extends StatusMessageDto implements Request {
    private Long id;
    private String startDate;
    private String startTime;
    private Boolean multipleClasses;
    private Integer numberOfClasses;


    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, ClassTimingDto.class);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getMultipleClasses() {
        return multipleClasses;
    }

    public void setMultipleClasses(Boolean multipleClasses) {
        this.multipleClasses = multipleClasses;
    }

    public Integer getNumberOfClasses() {
        return numberOfClasses;
    }

    public void setNumberOfClasses(Integer numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
