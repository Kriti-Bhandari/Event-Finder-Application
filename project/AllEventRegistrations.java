package com.example.project;
public class AllEventRegistrations {

    private int registrationId;
    private String eventName;
    private String username;
    private String email;
    private String registrationDate;

    public AllEventRegistrations(int registrationId, String eventName, String username, String email, String registrationDate) {
        this.registrationId = registrationId;
        this.eventName = eventName;
        this.username = username;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    // Getters
    public int getRegistrationId() {
        return registrationId;
    }
    public String getEventName() { return eventName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRegistrationDate() { return registrationDate; }
}
