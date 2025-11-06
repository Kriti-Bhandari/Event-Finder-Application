package com.example.project;
public class SearchEvent {
    private int id;
    private String name, description, date, location, category;
    private double ticketPrice;

    public SearchEvent(int id, String name, String description, String date, String location, String category, double ticketPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.location = location;
        this.category = category;
        this.ticketPrice = ticketPrice;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public String getCategory() { return category; }
    public double getTicketPrice() { return ticketPrice; }
}
