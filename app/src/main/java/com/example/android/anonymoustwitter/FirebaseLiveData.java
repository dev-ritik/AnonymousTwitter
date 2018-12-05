package com.example.android.anonymoustwitter;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FirebaseLiveData extends LiveData<List<Post>> {
    private static final String LOG_TAG = "point";

    private final Query query;
    private final MyChildEventListener childListener = new MyChildEventListener();

    public FirebaseLiveData(Query query) {
        this.query = query;
    }

    private FirebaseLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    @Override
    protected void onActive() {
        Log.i(LOG_TAG, "onActive");
        query.addChildEventListener(childListener);
    }

    @Override
    protected void onInactive() {
        Log.i(LOG_TAG, "onInactive");
        query.removeEventListener(childListener);
    }

    private class MyChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Log.i("point 46", "onChildAdded: " + dataSnapshot);
            Post post = dataSnapshot.getValue(Post.class);//as Post has all the three required parameter

            if(post==null)return;
            if (post.getKey() == null) {
                post.setKey(dataSnapshot.getKey());
            }
            List<Post> posts = getValue();
            if(posts==null)posts=new ArrayList<>();
            for (Post post1 : posts) if (post1.getKey().equals(dataSnapshot.getKey())) return;
            posts.add(post);
            setValue(posts);

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Log.i("point", "onChildChanged: ");
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Log.i("point 62", dataSnapshot.getKey());
            List<Post> posts = getValue();
            if (posts == null) posts = new ArrayList<>();
            for (Iterator<Post> iterator = posts.iterator(); iterator.hasNext(); ) {
//
                if (iterator.next().getKey().equals(dataSnapshot.getKey())) {
                    iterator.remove();
                }
            }
            Log.i("point", "onChildRemoved: "+posts.size());
            setValue(posts);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}