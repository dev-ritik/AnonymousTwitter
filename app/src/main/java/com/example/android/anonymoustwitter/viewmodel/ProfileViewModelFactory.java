package com.example.android.anonymoustwitter.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {

    private final ArrayList<String> favs;

    public ProfileViewModelFactory(ArrayList<String> favs) {
        this.favs = favs;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(favs);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}