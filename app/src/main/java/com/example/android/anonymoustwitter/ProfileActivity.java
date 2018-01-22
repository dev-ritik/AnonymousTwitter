package com.example.android.anonymoustwitter;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.DividerItemDecoration;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.bumptech.glide.Glide;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.makeramen.roundedimageview.RoundedTransformationBuilder;
        import com.squareup.picasso.Picasso;
        import com.squareup.picasso.Transformation;

        import java.util.ArrayList;

        import static com.example.android.anonymoustwitter.MainActivity.mUserId;

public class ProfileActivity extends AppCompatActivity {

    public static ChildEventListener mChildEventListenerProfile;


    private RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Post> posts;


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
        ImageView profilePic=findViewById(R.id.profile_image);
        if(MainActivity.mUser!=null) {
            userName.setText(MainActivity.mUser);
        }
        else {
            userName.setVisibility(View.GONE);
        }

        try{
            if(MainActivity.mUserProfile!=null) {
                Log.i(MainActivity.mUserProfile.toString(),"standpoint pr60");
//            Glide.with(profilePic.getContext())
//                    .load(MainActivity.mUserProfile)
//                    .into(profilePic);

//                profilePic.setImageURI(MainActivity.mUserProfile);
                Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(30)
                        .oval(false)
                        .build();
                Picasso.with(ProfileActivity.this)
                        .load(MainActivity.mUserProfile.toString())
                        .transform(transformation)
                        .into(profilePic);
            }
            else {
                Log.i("profile pic=null","standpoint pr75");

                profilePic.setImageResource(R.drawable.icon_profile_empty);
            }
        }catch (Exception e){
            profilePic.setImageResource(R.drawable.icon_profile_empty);
        }

        TextView emailId = findViewById(R.id.email);
        emailId.setText(email);

        posts = new ArrayList<>();

        mAdapter = new PostAdapter(posts);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if (mChildEventListenerProfile == null) {
            Log.i("mChildEvenProfe", "standpoint pr49");
            mChildEventListenerProfile = new ChildEventListener() {//working with db after authentication
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.i("onchildadded", "standpoint pr61");

                    //attached to all added child(all past and future child)
                    Post post = dataSnapshot.getValue(Post.class);
                    Log.i(post.getSaveIt().get(post.getSaveIt().size() - 1), "standpoint p65");
                    Log.i(mUserId, "standpoint pr66");
                    Log.i(MainActivity.mUserId, "standpoint p67");

                    if (post.getSaveIt().contains(MainActivity.mUserId)) {
                        posts.add(post);
                        Log.i("onitemadded", "standpoint pr71");


                    }

                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    // changed content of a child
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    // child deleted
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    //moved position of a child
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // error or permission denied
                }

            };
            MainActivity.mMessagesDatabaseReference.addChildEventListener(mChildEventListenerProfile);
            if (mChildEventListenerProfile != null) {
                Log.i("mChildEventLisrPro add", "standpoint pr85");

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("standpoint pr112");
        if (mChildEventListenerProfile != null) {
            Log.i(mChildEventListenerProfile.toString(), "standpoint pr114");
            MainActivity.mMessagesDatabaseReference.removeEventListener(mChildEventListenerProfile);
            mChildEventListenerProfile = null;
        }
//        detachDatabaseReadListener();

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}