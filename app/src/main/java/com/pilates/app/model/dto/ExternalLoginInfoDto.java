package com.pilates.app.model.dto;

import com.google.gson.GsonBuilder;
import com.pilates.app.model.UserRole;

public class ExternalLoginInfoDto implements Request  {
    private String provider;
    private String accessToken;
    private UserRole role;

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, ExternalLoginInfoDto.class);
    }
}
