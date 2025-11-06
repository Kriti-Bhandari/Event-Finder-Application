package com.example.project;
public class Regevent {
    public String  name, date, location, category;
    public int id;
    public double ticket_price;

    public Regevent(int id, String name, String date, String location, String category, double ticket_price) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.category = category;
        this.ticket_price = ticket_price;
    }
    public int getEventId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public double getTicket_price() {
        return ticket_price;
    }
}

