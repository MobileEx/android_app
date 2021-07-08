package com.pilates.app.model.dto;

import com.google.gson.GsonBuilder;

import java.util.List;

public class PortfolioDto implements Request {

    public Integer getStartTrainYear() {
        return startTrainYear;
    }

    public void setStartTrainYear(Integer startTrainYear) {
        this.startTrainYear = startTrainYear;
    }

    public Integer getAvailableTimePerWeek() {
        return availableTimePerWeek;
    }

    public void setAvailableTimePerWeek(Integer availableTimePerWeek) {
        this.availableTimePerWeek = availableTimePerWeek;
    }

    public DayPart getAvailablePartOfDay() {
        return availablePartOfDay;
    }

    public void setAvailablePartOfDay(DayPart availablePartOfDay) {
        this.availablePartOfDay = availablePartOfDay;
    }

    public List<PilatesType> getPilatesTypes() {
        return pilatesTypes;
    }

    public void setPilatesTypes(List<PilatesType> pilatesTypes) {
        this.pilatesTypes = pilatesTypes;
    }

    public String getAboutTrainer() {
        return aboutTrainer;
    }

    public void setAboutTrainer(String aboutTrainer) {
        this.aboutTrainer = aboutTrainer;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Integer> getTrainerClassDurations() {
        return trainerClassDurations;
    }

    public void setTrainerClassDurations(List<Integer> trainerClassDurations) {
        this.trainerClassDurations = trainerClassDurations;
    }

    public enum DayPart {
        MORNING, EVENING, LUNCHTIME, AFTERNOON
    }

    public enum PilatesType {
        BALL, MAT, RING
    }

    private Long userId;
    private Integer startTrainYear;
    private Integer availableTimePerWeek;
    private DayPart availablePartOfDay;

    private List<PilatesType> pilatesTypes;
    private String aboutTrainer;
    private String links;
    private List<Integer> trainerClassDurations;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, PortfolioDto.class);
    }

}
