package com.example.talkzy;

public class User {

    private  String Uid, Name , PhoneNumber , ProfileImage;
    public User(){}

    public User(String uid, String name, String phoneNumber, String profileImage) {
        Uid = uid;
        Name = name;
        PhoneNumber = phoneNumber;
        ProfileImage = profileImage;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }
}
