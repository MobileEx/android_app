package com.pilates.app.model.dto;

import com.pilates.app.model.UserRole;

public class StatusMessageDto {

    private Boolean error;
    private String message;

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public static StatusMessageDto status(Boolean error, String message) {
        StatusMessageDto result = new StatusMessageDto();
        result.setError(error);
        result.setMessage(message);
        return result;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
