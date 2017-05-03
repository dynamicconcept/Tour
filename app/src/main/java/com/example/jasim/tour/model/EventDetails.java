package com.example.jasim.tour.model;

import java.io.Serializable;

public class EventDetails implements Serializable {
    long id;
    long eventId;
    String details;
    String picture;
    String addingTime;
    int budgetUsed;

    public EventDetails() {
    }

    public EventDetails(long id, long eventId, String details, String picture, String addingTime, int budgetUsed) {
        this.id = id;
        this.eventId = eventId;
        this.details = details;
        this.picture = picture;
        this.addingTime = addingTime;
        this.budgetUsed = budgetUsed;
    }

    public EventDetails(long eventId, String details, String picture, String addingTime, int budgetUsed) {

        this.eventId = eventId;
        this.details = details;
        this.picture = picture;
        this.addingTime = addingTime;
        this.budgetUsed = budgetUsed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAddingTime() {
        return addingTime;
    }

    public void setAddingTime(String addingTime) {
        this.addingTime = addingTime;
    }

    public int getBudgetUsed() {
        return budgetUsed;
    }

    public void setBudgetUsed(int budgetUsed) {
        this.budgetUsed = budgetUsed;
    }
}
