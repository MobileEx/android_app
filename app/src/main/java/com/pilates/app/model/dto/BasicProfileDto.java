package com.pilates.app.model.dto;

import com.google.gson.GsonBuilder;

import java.util.List;

public class BasicProfileDto implements Request {

    private Long userId;
    private Integer startTrainYear;
    private Integer availableTimePerWeek;
    private List<PortfolioDto.DayPart> availablePartOfDay;
    private List<Integer> trainerClassDurations;
    private String phone;
    private String country;
    private String postCode;
    private String address;
    private String email;
    private String password;
    private String name;
    private boolean external;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, BasicProfileDto.class);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public List<PortfolioDto.DayPart> getAvailablePartOfDay() {
        return availablePartOfDay;
    }

    public void setAvailablePartOfDay(List<PortfolioDto.DayPart> availablePartOfDay) {
        this.availablePartOfDay = availablePartOfDay;
    }

    public List<Integer> getTrainerClassDurations() {
        return trainerClassDurations;
    }

    public void setTrainerClassDurations(List<Integer> trainerClassDurations) {
        this.trainerClassDurations = trainerClassDurations;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }
}

