package com.pilates.app.model.dto;

import java.util.ArrayList;

public class TrainerCertificateListDto extends StatusMessageDto {
    private ArrayList<TrainerCertificateDto> certificates;

    public ArrayList<TrainerCertificateDto> getCertificates() {
        return certificates;
    }

    public void setCertificates(ArrayList<TrainerCertificateDto> certificates) {
        this.certificates = certificates;
    }
}