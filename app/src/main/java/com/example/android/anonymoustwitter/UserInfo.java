package com.example.android.anonymoustwitter;

import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ritik on 26-01-2018.
 */

/*
Object to store all user related information
 */

public class UserInfo {

    private String userName;
    private String userId;
    private String emailId;
    private ArrayList<String> favourites;

    public UserInfo() {
    }


    public UserInfo(String userName, String userId, String emailId, ArrayList favourites) {
        this.userName = userName;
        this.userId = userId;
        this.emailId = emailId;
        this.favourites = favourites;
    }

    public ArrayList<String> getFavourites() {
        return favourites;
    }

    public void setFavourites(ArrayList<String> favourites) {
        this.favourites = favourites;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}