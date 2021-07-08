package com.pilates.app.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrainerCertificateDto extends StatusMessageDto {
    private Long id;
    private String name;
    private String date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
