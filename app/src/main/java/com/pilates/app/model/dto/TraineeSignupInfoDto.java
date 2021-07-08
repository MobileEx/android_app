package com.pilates.app.model.dto;

import com.google.gson.GsonBuilder;
import com.pilates.app.model.dto.enums.ClassPurpose;
import com.pilates.app.model.dto.enums.ExperienceLevel;

import java.util.List;

public class TraineeSignupInfoDto implements Request {
    private ExperienceLevel experienceLevel;
    private Integer availableTimePerWeek;
    private List<PortfolioDto.DayPart> availablePartOfDay;
    private List<Integer> classDurations;
    private List<ClassPurpose> goals;
    private Long trainerId;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, TraineeSignupInfoDto.class);
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public Integer getAvailableTimePerWeek() {
        return availableTimePerWeek;
    }

    public void setAvailableTimePerWeek(Integer availableTimePerWeek) {
        this.availableTimePerWeek = availableTimePerWeek;
    }

    public List<PortfolioDto.DayPart> getAvailablePartOfDay() {
        return availablePartOfDay;
    }

    public void setAvailablePartOfDay(List<PortfolioDto.DayPart> availablePartOfDay) {
        this.availablePartOfDay = availablePartOfDay;
    }

    public List<Integer> getClassDurations() {
        return classDurations;
    }

    public void setClassDurations(List<Integer> classDurations) {
        this.classDurations = classDurations;
    }

    public List<ClassPurpose> getGoals() {
        return goals;
    }

    public void setGoals(List<ClassPurpose> goals) {
        this.goals = goals;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }
}