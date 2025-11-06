package com.example.project;

public class Event {
    private int eventId;
    private String eventName;
    private String eventDescription;
    private String eventDate;
    private String eventTime;
    private String eventLocation;
    private String eventCategory;
    private double ticketPrice;
    private int totalSeats;

    public Event(int eventId,int totalSeats , String eventName, String eventDescription, String eventDate, String eventTime,
                 String eventLocation, String eventCategory, double ticketPrice) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventLocation = eventLocation;
        this.eventCategory = eventCategory;
        this.ticketPrice = ticketPrice;
        this.totalSeats = totalSeats;

    }


    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventDate() {
        return eventDate;
    }
    public String getEventTime() {
        return eventTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public int getEventId() {
        return eventId;
    }
    public int getTotalSeats() {
        return totalSeats;
    }
}
