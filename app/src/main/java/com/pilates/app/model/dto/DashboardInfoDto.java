package com.pilates.app.model.dto;

public class DashboardInfoDto extends StatusMessageDto {
    private long minutesBeforeNextClass;
    private String nextClassStartDateTime;
    private boolean trainerApproved;
    private boolean hasNextClass;
    private Double currentBalance;

    public long getMinutesBeforeNextClass() {
        return minutesBeforeNextClass;
    }

    public void setMinutesBeforeNextClass(long minutesBeforeNextClass) {
        this.minutesBeforeNextClass = minutesBeforeNextClass;
    }

    public String getNextClassStartDateTime() {
        return nextClassStartDateTime;
    }

    public void setNextClassStartDateTime(String nextClassStartDateTime) {
        this.nextClassStartDateTime = nextClassStartDateTime;
    }

    public boolean isTrainerApproved() {
        return trainerApproved;
    }

    public void setTrainerApproved(boolean trainerApproved) {
        this.trainerApproved = trainerApproved;
    }

    public boolean isHasNextClass() {
        return hasNextClass;
    }

    public void setHasNextClass(boolean hasNextClass) {
        this.hasNextClass = hasNextClass;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }
}