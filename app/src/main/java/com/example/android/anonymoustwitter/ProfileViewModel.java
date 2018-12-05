package com.example.android.anonymoustwitter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProfileViewModel extends ViewModel {
    private static final DatabaseReference HOT_STOCK_REF =
            FirebaseDatabase.getInstance().getReference().child("input");

    public ProfileViewModel(ArrayList<String> favs) {
        for (String fav : favs
                ) {
            HOT_STOCK_REF.orderByKey().startAt(fav).limitToFirst(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                    Log.i("point", "onChildAdded: ");
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post == null) return;
                    if (post.getKey() == null) {
                        post.setKey(dataSnapshot.getKey());
                    }

                    List<Post> posts = databaseLiveData.getValue();
                    if (posts == null) posts = new ArrayList<>();
                    for (Post post1 : posts)
                        if (post1.getKey().equals(dataSnapshot.getKey())) return;
                    posts.add(post);
                    databaseLiveData.setValue(posts);
                }


                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.i("point", "onChildRemoved: ");
                    List<Post> posts = databaseLiveData.getValue();
                    if (posts == null) posts = new ArrayList<>();
                    for (Iterator<Post> iterator = posts.iterator(); iterator.hasNext(); ) {
//
                        if (iterator.next().getKey().equals(dataSnapshot.getKey())) {
                            iterator.remove();
                        }


                    }
                    Log.i("point", "onChildRemoved: " + posts.size());
                    databaseLiveData.setValue(posts);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private final MutableLiveData<List<Post>> databaseLiveData = new MutableLiveData<>();


    @NonNull
    public MutableLiveData<List<Post>> getDatabaseLiveData() {
        return databaseLiveData;
    }
}