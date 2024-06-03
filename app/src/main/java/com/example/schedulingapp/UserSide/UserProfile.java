package com.example.schedulingapp.UserSide;

public class UserProfile {
    private String username;
    private String email;
    private String profilePicture;
    private String age;
    private String name;
    private String gender;
    private String phonenumber;
    private String uid;

    public UserProfile() {
        // Default constructor required for Firestore
    }

    public UserProfile(String name, String username, String email, String phonenumber, String age, String profilePicture, String uid, String gender) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.phonenumber = phonenumber;
        this.age = age;
        this.profilePicture = profilePicture;
        this.uid = uid;
        this.gender = gender;
    }

    // Getters and setters
    public String getUid(){
        return uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getPhonenumber(){
        return phonenumber;
    }
    public void setPhonenumber(String phonenumber){
        this.phonenumber = phonenumber;
    }
    public String getAge(){
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePicture = profilePictureUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
