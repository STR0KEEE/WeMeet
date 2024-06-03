package com.example.schedulingapp;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Appointment {
    private String id;
    private String businessId;
    private Timestamp date;
    private String duration;
    private boolean isAvailable;
    private String customerId;

    public Appointment() {
        // Firestore requires an empty constructor
    }

    public Appointment(String id, String businessId, Timestamp date, String duration, boolean isAvailable, String customerId) {
        this.id = id;
        this.businessId = businessId;
        this.date = date;
        this.duration = duration;
        this.isAvailable = isAvailable;
        this.customerId = customerId;
    }

    public Appointment(String id, String businessId, Timestamp date, String duration, boolean isAvailable) {
        this.id = id;
        this.businessId = businessId;
        this.date = date;
        this.duration = duration;
        this.isAvailable = isAvailable;
    }

    // Getter and setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date.toDate());
    }
}
