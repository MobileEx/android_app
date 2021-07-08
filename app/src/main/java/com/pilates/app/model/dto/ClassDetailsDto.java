package com.pilates.app.model.dto;

import com.google.gson.GsonBuilder;

import java.util.List;

public class ClassDetailsDto extends StatusMessageDto implements Request {
    private Long id;
    private String description;
    private String requirements;
    private List<SelectionItemDto> pilatesTypes;
    private List<SelectionItemDto> purposes;
    private SelectionListDto pilatesTypesOptions;
    private SelectionListDto purposesOptions;


    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, ClassDetailsDto.class);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SelectionItemDto> getPilatesTypes() {
        return pilatesTypes;
    }

    public void setPilatesTypes(List<SelectionItemDto> pilatesTypes) {
        this.pilatesTypes = pilatesTypes;
    }

    public List<SelectionItemDto> getPurposes() {
        return purposes;
    }

    public void setPurposes(List<SelectionItemDto> purposes) {
        this.purposes = purposes;
    }

    public SelectionListDto getPilatesTypesOptions() {
        return pilatesTypesOptions;
    }

    public SelectionListDto getPurposesOptions() {
        return purposesOptions;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
}
