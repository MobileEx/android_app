package com.pilates.app.model.dto;

import java.util.ArrayList;

public class UserDetailListDto extends StatusMessageDto {
    private ArrayList<UserInfoDto> details;

    public ArrayList<UserInfoDto> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<UserInfoDto> details) {
        this.details = details;
    }
}
