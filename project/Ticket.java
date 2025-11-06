package com.example.project;

public class Ticket {
    private int id;
    private String name;
    private String date;
    private String location;
    private String category;
    private double price;

    public Ticket(int id, String name, String date, String location, String category, double price) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.category = category;
        this.price = price;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
}
