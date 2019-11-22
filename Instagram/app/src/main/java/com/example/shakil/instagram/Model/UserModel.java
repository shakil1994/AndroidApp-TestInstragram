package com.example.shakil.instagram.Model;

public class UserModel {
    private String uid, userName, fullName, phone, bio, imageLink;

    public UserModel() {
    }

    public UserModel(String uid, String userName, String fullName, String phone, String bio, String imageLink) {
        this.uid = uid;
        this.userName = userName;
        this.fullName = fullName;
        this.phone = phone;
        this.bio = bio;
        this.imageLink = imageLink;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
