package com.example.jasim.tour.model;

import java.io.Serializable;

/**
 * Created by Your Boss on 8/18/2016.
 */
public class Events implements Serializable{

    private long eventID;
    private long eventUser;
    private String eventName;
    private String eventLocation;
    private int budget;
    private String eventStartDate;
    private String eventEndDate;

    public Events() {
    }

    public Events(long eventID, long eventUser, String eventName, String eventLocation, int budget, String eventStartDate, String eventEndDate) {
        this.eventID = eventID;
        this.eventUser = eventUser;
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.budget=budget;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    public Events(long eventUser, String eventName, String eventLocation, int budget, String eventStartDate, String eventEndDate) {
        this.eventUser = eventUser;
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.budget=budget;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public long getEventUser() {
        return eventUser;
    }

    public void setEventUser(long eventUser) {
        this.eventUser = eventUser;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }
}
