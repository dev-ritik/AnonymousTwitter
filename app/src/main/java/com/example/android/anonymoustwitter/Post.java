
package com.example.android.anonymoustwitter;

import android.util.Log;

import java.util.ArrayList;

public class Post {

    private String text;
    private String photoUrl;
    private String name;
    private String posterId;
    private ArrayList<String> likedUsers;
    private ArrayList<String> unlikedUsers;
    private String time;
    private ArrayList<String> saveIt;
    public Post() {
//        Log.i(posterId, "standpoint post43");
//        Log.i(text, "standpoint post44");
//        Log.i(photoUrl, "standpoint post45");
//        Log.i(time, "standpoint post46");
//        Log.i(saveIt.toString(), "standpoint post47");
//        Log.i(likedUsers.toString(), "standpoint post48");
//        Log.i(unlikedUsers.toString(), "standpoint post49");
//        Log.i(name, "standpoint post50");

    }

//    public Post(String text, String photoUrl, String currentTime, String userID, ArrayList likers, ArrayList unlikers, ArrayList saveIt) {
//        this.text = text;
//        this.photoUrl = photoUrl;
//        this.time = currentTime;
////        this.name = username;
//        this.likedUsers = likers;
//        this.unlikedUsers = unlikers;
//        this.posterId = userID;
//        this.saveIt = saveIt;

//    }

    public Post(String text, String photoUrl, String currentTime, String username,String posterId, ArrayList likers, ArrayList unlikers,ArrayList saveIt) {
        this.text = text;
        this.photoUrl = photoUrl;
        this.time = currentTime;
        this.name = username;
        this.likedUsers = likers;
        this.unlikedUsers = unlikers;
        this.posterId = posterId;
        this.saveIt=saveIt;
        Log.i(posterId, "standpoint post43");
        Log.i(text, "standpoint post44");
        Log.i(photoUrl, "standpoint post45");
        Log.i(time, "standpoint post46");
        Log.i(saveIt.toString(), "standpoint post47");
        Log.i(likedUsers.toString(), "standpoint post48");
        Log.i(unlikedUsers.toString(), "standpoint post49");
        Log.i(name, "standpoint post50");

    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPosterId() {
        Log.i(posterId, "standpoint post55");
        return posterId;
    }

    public void setPosterId(String posterId) {
            this.posterId = posterId;
    }

//    public String getPosterName() {
//        return name;
//    }
//
//    public void setPosterName(String username){
//        this.name=username;
//    }

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
}