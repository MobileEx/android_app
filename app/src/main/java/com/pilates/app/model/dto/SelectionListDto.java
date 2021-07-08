package com.pilates.app.model.dto;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class SelectionListDto extends StatusMessageDto implements Request {
    private ArrayList<SelectionItemDto> data;

    public ArrayList<SelectionItemDto> getData() {
        return data;
    }

    public void setData(ArrayList<SelectionItemDto> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, SelectionListDto.class);
    }
}