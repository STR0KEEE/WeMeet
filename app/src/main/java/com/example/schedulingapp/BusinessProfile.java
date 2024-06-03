package com.example.schedulingapp;

public class BusinessProfile {
    private String id;
    private String name;
    private String email;
    private String type;
    private String about;

    public BusinessProfile() {
        // Default constructor required for Firestore
    }

    public BusinessProfile(String id, String name, String type, String email, String about) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
        this.about = about;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    public String toString() {
        return name; // Display the business name in the spinner
    }
}
