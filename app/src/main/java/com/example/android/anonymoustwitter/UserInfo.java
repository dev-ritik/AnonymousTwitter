package com.example.android.anonymoustwitter;

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
    private String dpUrl;
    private String emailId;
    private ArrayList<String> favouriteList;

    public UserInfo() {
    }

    public UserInfo(String username, String userid, String emailId, ArrayList<String> favouriteList) {
        this.userName = username;
        this.userId = userid;
        this.emailId = emailId;
        this.favouriteList = favouriteList;

    }


    public ArrayList<String> getfavouriteList() {
        return favouriteList;
    }

    public void setfavouriteList(ArrayList<String> favouriteList) {
        this.favouriteList = favouriteList;
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

    public String getdpUrl() {
        return dpUrl;
    }

    public void setdpUrl(String dpUrl) {
        this.dpUrl = dpUrl;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}