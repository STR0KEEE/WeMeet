package com.example.schedulingapp.UserSide;

/*public class SearchHistoryItem {
    private int iconResourceId;
    private String name;
    private String profession;
    private int anotherIconResourceId;

    public SearchHistoryItem(int iconResourceId, String name, String profession, int anotherIconResourceId) {
        this.iconResourceId = iconResourceId;
        this.name = name;
        this.profession = profession;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public String getName() {
        return name;
    }

    public String getProfession() {
        return profession;
    }
    *//*

}
*/
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

