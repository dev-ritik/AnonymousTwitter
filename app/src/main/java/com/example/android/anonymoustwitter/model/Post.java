package com.example.android.anonymoustwitter.model;

import java.util.ArrayList;

public class Post {

    private String text;
    private String photoUrl;
    private String name;
    private String userId;
    private ArrayList<String> likedUsers;
    private ArrayList<String> unlikedUsers;
    private String time;
    private ArrayList<String> saveIt;
    private String key;

    public Post() {
    }

    public Post(String text, String photoUrl, String currentTime, String userID, ArrayList<String> likers, ArrayList<String> unlikers, ArrayList<String> saveIt, String key) {
        this.text = text;
        this.photoUrl = photoUrl;
        this.time = currentTime;
//        this.name = username;
        this.likedUsers = likers;
        this.unlikedUsers = unlikers;
        this.userId = userID;
        this.saveIt = saveIt;
        this.key = key;
    }

    public Post(String text, String photoUrl, String currentTime, String userID, ArrayList<String>  likers, ArrayList<String> unlikers, ArrayList<String> saveIt) {
        this.text = text;
        this.photoUrl = photoUrl;
        this.time = currentTime;
//        this.name = username;
        this.likedUsers = likers;
        this.unlikedUsers = unlikers;
        this.userId = userID;
        this.saveIt = saveIt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPosterId() {
        return userId;
    }

    public void setPosterId(String userId) {
        this.userId = userId;
    }

    public void setTimeCurrent(String time) {
        this.time = time;
    }

    public String getTimeCurrent() {
        return time;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUser() {
        return name;
    }

    public void setUser(String user) {
        this.name = user;
    }

    public ArrayList<String> getLikedUsers() {
        return likedUsers;
    }

    public ArrayList<String> getUnlikedUsers() {
        return unlikedUsers;
    }

    public ArrayList<String> getSaveIt() {
        return saveIt;
    }

    public void setLikedUsers(ArrayList<String> likers) {
        this.likedUsers = likers;

    }

    public void setUnlikedUsers(ArrayList<String> unlikers) {
        this.unlikedUsers = unlikers;

    }

    public void setSaveIt(ArrayList<String> saveIt) {
        this.saveIt = saveIt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}