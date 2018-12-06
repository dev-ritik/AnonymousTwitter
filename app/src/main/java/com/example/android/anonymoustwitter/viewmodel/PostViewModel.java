package com.example.android.anonymoustwitter.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.android.anonymoustwitter.model.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PostViewModel extends ViewModel {
    private static final DatabaseReference HOT_STOCK_REF =
            FirebaseDatabase.getInstance().getReference().child("input");

    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(HOT_STOCK_REF);
    private final FirebaseLiveData databaseLiveData = new FirebaseLiveData(HOT_STOCK_REF);

    @NonNull
    public LiveData<Long> getDataSnapshotLiveData() {
        return liveData;
    }

    @NonNull
    public LiveData<List<Post>> getDatabaseLiveData() {
        return databaseLiveData;
    }
}