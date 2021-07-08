package com.pilates.app.model.dto;

import com.pilates.app.model.UserRole;

public class LoginResponseDto extends StatusMessageDto {

    private Long id;
    private String name;
    private UserRole role;
    private String accessToken;
    private Boolean hasAccount;

    public LoginResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Boolean getHasAccount() {
        return hasAccount;
    }

    public void setHasAccount(Boolean hasAccount) {
        this.hasAccount = hasAccount;
    }
}
