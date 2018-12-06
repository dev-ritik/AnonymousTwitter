package com.example.android.anonymoustwitter.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.anonymoustwitter.model.Post;
import com.example.android.anonymoustwitter.viewmodel.ProfileViewModel;
import com.example.android.anonymoustwitter.viewmodel.ProfileViewModelFactory;
import com.example.android.anonymoustwitter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.android.anonymoustwitter.ui.MainActivity.userInfo;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private PostAdapter mAdapter;

    ArrayList<Post> favouritePosts;

    private ProfileViewModelFactory mViewModelFactory;
    private ProfileViewModel mViewModel;
    private int mPosition = RecyclerView.NO_POSITION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");


        mRecyclerView = findViewById(R.id.my_recycler_view);
        TextView userName = findViewById(R.id.userName);
        if (MainActivity.mUserId != null) {
            userName.setText(MainActivity.mUsername);
        } else {
            userName.setVisibility(View.GONE);
        }

        TextView emailId = findViewById(R.id.email);
        emailId.setText(email);

        favouritePosts = new ArrayList<>();

        mAdapter = new PostAdapter();
//        mAdapter = new PostAdapter(favouritePosts);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mViewModelFactory = new ProfileViewModelFactory(userInfo.getFavourites());
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ProfileViewModel.class);

        MutableLiveData<List<Post>> favs = mViewModel.getDatabaseLiveData();
        favs.observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
//                Log.i("point 430", "onChanged: " + mAdapter.getItemCount());
                if (posts == null) posts = new ArrayList<>();

                Log.i("point", "onChanged: " + posts.size());
                mAdapter.swapForecast(posts);
                if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                mRecyclerView.smoothScrollToPosition(mPosition);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("point", "onPause: ");

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}