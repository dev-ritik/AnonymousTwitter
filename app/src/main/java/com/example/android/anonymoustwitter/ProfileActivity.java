package com.example.android.anonymoustwitter;

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

import com.google.firebase.database.ChildEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.anonymoustwitter.MainActivity.userInfo;

public class ProfileActivity extends AppCompatActivity {

    public static ChildEventListener mChildEventListenerProfile;


    private RecyclerView mRecyclerView;
    private PostAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        mLayoutManager = new LinearLayoutManager(this);
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
//
//        for (int i = 0; i < userInfo.getFavourites().size(); i++) {
//            mMessagesDatabaseReference.orderByKey().startAt(userInfo.getFavourites().get(i)).limitToFirst(1).addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    Log.i("onchildadded", "point pr102");
//
//                    Post post = dataSnapshot.getValue(Post.class);
//                    Log.i(post.getText(), "point pr65");
//                    favouritePosts.add(post);
//
//                    mAdapter.notifyDataSetChanged();
//
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    for (Iterator<Post> iterator = favouritePosts.iterator(); iterator.hasNext(); ) {
//                        if (iterator.next().getKey() == dataSnapshot.getKey())
//                            iterator.remove();
//                    }
//                    mAdapter.notifyDataSetChanged();
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("standpoint pr112");
        if (mChildEventListenerProfile != null) {
            Log.i(mChildEventListenerProfile.toString(), "standpoint pr114");
//            mMessagesDatabaseReference.removeEventListener(mChildEventListenerProfile);
            mChildEventListenerProfile = null;
        }
//        detachDatabaseReadListener();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}