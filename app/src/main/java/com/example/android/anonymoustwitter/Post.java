
package com.example.android.anonymoustwitter;

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

    public Post(String text, String photoUrl, String currentTime, String userID, ArrayList likers, ArrayList unlikers, ArrayList saveIt) {
        this.text = text;
        this.photoUrl = photoUrl;
        this.time = currentTime;
//        this.name = username;
        this.likedUsers = likers;
        this.unlikedUsers = unlikers;
        this.userId = userID;
        this.saveIt = saveIt;

    }

//    public Post(String text, String photoUrl, String currentTime, String username, ArrayList likers, ArrayList unlikers,ArrayList saveIt) {
//        this.text = text;
//        this.photoUrl = photoUrl;
//        this.time = currentTime;
//        this.name = username;
//        this.likedUsers = likers;
//        this.unlikedUsers = unlikers;
//        this.saveIt=saveIt;
//    }


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
//        System.out.println(time);
//        System.out.println("standpoint 56");

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
//        System.out.println("standpoint 75");

        System.out.println(likedUsers.get(0));

        return likedUsers;
    }

    public ArrayList<String> getUnlikedUsers() {
//        System.out.println("standpoint 79");
        System.out.println(unlikedUsers.get(0));

        return unlikedUsers;
    }

    public ArrayList<String> getSaveIt() {

        return saveIt;
    }

    public void setLikedUsers(ArrayList<String> likers) {
//        System.out.println("standpoint 84");
        this.likedUsers = likers;

    }

    public void setUnlikedUsers(ArrayList<String> unlikers) {
//        System.out.println("standpoint 84");
        this.unlikedUsers = unlikers;

    }

    public void setSaveIt(ArrayList<String> saveIt) {
//        System.out.println("standpoint 84");
        this.saveIt = saveIt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}