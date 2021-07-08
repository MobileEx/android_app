package com.pilates.app.controls.listeners;

public interface OnDateTimePickedListener {
    void pickedDate(int year, int month, int day, String formatted);
    void pickedTime(int hour, int minutes, String formatted);
}
