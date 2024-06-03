package com.example.schedulingapp;

public class SearchHistoryItem {
    private String name;
    private String profession;

    public SearchHistoryItem(String name, String profession) {
        this.name = name;
        this.profession = profession;
    }

    public String getName() {
        return name;
    }

    public String getProfession() {
        return profession;
    }
}

