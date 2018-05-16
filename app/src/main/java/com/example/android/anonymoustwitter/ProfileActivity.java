package com.example.android.anonymoustwitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Iterator;

import static com.example.android.anonymoustwitter.MainActivity.mMessagesDatabaseReference;
import static com.example.android.anonymoustwitter.MainActivity.mUserId;
import static com.example.android.anonymoustwitter.MainActivity.userInfo;

public class ProfileActivity extends AppCompatActivity {

    public static ChildEventListener mChildEventListenerProfile;


    private RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Post> favouritePosts;


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

        mAdapter = new PostAdapter(favouritePosts);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        if (mChildEventListenerProfile == null) {
//            Log.i("mChildEvenProfe", "standpoint pr49");
//            mChildEventListenerProfile = new ChildEventListener() {//working with db after authentication
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    Log.i("onchildadded", "standpoint pr61");
//
//                    attached to all added child(all past and future child)
//                    Post post = dataSnapshot.getValue(Post.class);
//                    Log.i(post.getSaveIt().get(post.getSaveIt().size() - 1), "standpoint p65");
//                    Log.i(mUserId, "standpoint pr66");
//                    Log.i(MainActivity.mUserId, "standpoint p67");
//
//                    if (post.getSaveIt().contains(MainActivity.mUserId)) {
//                        posts.add(post);
//                        Log.i("onitemadded", "standpoint pr71");
//
//                    }
//
//                    mAdapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                     changed content of a child
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                     child deleted
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                    moved position of a child
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                     error or permission denied
//                }
//
//            };
//            MainActivity.mMessagesDatabaseReference.addChildEventListener(mChildEventListenerProfile);
//            if (mChildEventListenerProfile != null) {
//                Log.i("mChildEventLisrPro add", "standpoint pr85");
//
//            }
//        }

        for (int i = 0; i < userInfo.getFavourites().size(); i++) {
            mMessagesDatabaseReference.orderByKey().startAt(userInfo.getFavourites().get(i)).limitToFirst(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.i("onchildadded", "point pr102");

                    Post post = dataSnapshot.getValue(Post.class);
                    Log.i(post.getText(), "point pr65");
                    Log.i(MainActivity.mUserId, "standpoint p67");

                    favouritePosts.add(post);
                    Log.i("onitemadded", "standpoint pr71");


                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);//as Post has all the three required parameter

                    for (Iterator<Post> iterator = favouritePosts.iterator(); iterator.hasNext(); ) {
                        if (iterator.next().getTimeCurrent() == post.getTimeCurrent())
                            iterator.remove();
                    }
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("standpoint pr112");
        if (mChildEventListenerProfile != null) {
            Log.i(mChildEventListenerProfile.toString(), "standpoint pr114");
            mMessagesDatabaseReference.removeEventListener(mChildEventListenerProfile);
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