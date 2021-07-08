package com.pilates.app.model.dto;

public class AddClassSelectionsDto extends StatusMessageDto {
    private SelectionListDto purposes;
    private SelectionListDto expLevels;
    private SelectionListDto streamingTypes;
    private SelectionListDto pilatesTypes;
    private SelectionListDto classDurations;

    public SelectionListDto getPurposes() {
        return purposes;
    }

    public SelectionListDto getExpLevels() {
        return expLevels;
    }

    public SelectionListDto getStreamingTypes() {
        return streamingTypes;
    }

    public SelectionListDto getPilatesTypes() {
        return pilatesTypes;
    }

    public SelectionListDto getClassDurations() {
        return classDurations;
    }
}