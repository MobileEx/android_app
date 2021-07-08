package com.pilates.app.model.dto;

import java.util.ArrayList;

public class ClassFilteredListDto extends StatusMessageDto {
    private ArrayList<ClassListingInfoDto> data;
    private int totalPages;

    public ArrayList<ClassListingInfoDto> getData() {
        return data;
    }

    public void setData(ArrayList<ClassListingInfoDto> data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
