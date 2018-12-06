package com.example.android.anonymoustwitter.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.anonymoustwitter.model.Post;
import com.example.android.anonymoustwitter.viewmodel.PostViewModel;
import com.example.android.anonymoustwitter.R;
import com.example.android.anonymoustwitter.model.UserInfo;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 100;
    //set default message limit

    private static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    static boolean calledAlready = false;

    private ProgressBar mProgressBar;
    private ImageView imageView;
    private EditText mMessageEditText;
    private ImageButton mSendButton, cancelButton, mPhotoPickerButton;
    private CoordinatorLayout mainLayout;
    private CoordinatorLayout imageLayout;
    private Uri selectedImageUri;
    private LinearLayout input;

    private RecyclerView mRecyclerView;
    public static PostAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    public static UserInfo userInfo;

    public static String mUsername;
    public static String mUserId;
    private String mEmailId;

    private SwipeRefreshLayout mySwipeRefreshLayout;
    private int itemPos = 0;
    private String messageKey, mPrevKey, mLastKey;

    public static DatabaseReference mMessagesDatabaseReference;
    public static DatabaseReference mUserDatabaseReference;
    private ChildEventListener refreshChildListener;//to listen the changes in db
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private StorageReference mChatPhotosStorageReference;
    private int mPosition = RecyclerView.NO_POSITION;
    private LiveData<List<Post>> databaseLiveData;
    private LiveData<Long> liveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        mUsername = ANONYMOUS;
        mEmailId = "";
        mUserId = null;

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        FirebaseDatabase mfirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        mMessagesDatabaseReference = mfirebaseDatabase.getReference().child("input");
        mUserDatabaseReference = mfirebaseDatabase.getReference().child("user");

        mChatPhotosStorageReference = mFirebaseStorage.getReference("chat_photos");

        mFirebaseAuth = FirebaseAuth.getInstance();

        mProgressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mPhotoPickerButton = findViewById(R.id.photoPickerButton);
        mMessageEditText = findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);
        cancelButton = findViewById(R.id.cancel);
        mainLayout = findViewById(R.id.main_content);
        imageView = findViewById(R.id.inputImage);
        imageLayout = findViewById(R.id.imageLayout);
        input = findViewById(R.id.linearLayout);
        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);

        mAdapter = new PostAdapter();

        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


//        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                // Row is swiped from recycler view
//                // remove it from adapter
//
//                Log.i("point m170", "swipped");
//                final int position = viewHolder.getAdapterPosition(); //get position which is swipe
//
//                if (direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.LEFT) {
//                    //if swipe left
//
//                    if (posts.get(position).getPosterId().equals(mUserId)) {
//
//                        new AlertDialog.Builder(MainActivity.this) //alert for confirm to delete
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .setTitle("Caution")
//                                .setMessage("This post will be deleted")
//                                .setPositiveButton("CANCEL", new DialogInterface.OnClickListener() { //when click on DELETE
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                }).
//                                setNegativeButton("DELETE", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        mAdapter.notifyItemRemoved(position);    //item removed from recylcerview
//                                        mMessagesDatabaseReference.orderByKey().startAt(posts.get(position).getKey()).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                                                    child.getRef().removeValue();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//
//                                        });
//                                        posts.remove(position);  //then remove item
//
//                                    }
//
//                                }).show();  //show alert dialog
//                    }
//                }
//
//            }
//
//
//            @Override
//            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
//                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                // view the background view
//
//            }
//        };
//        new ItemTouchHelper(itemTouchHelperCallback).
//                attachToRecyclerView(mRecyclerView);


        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpej");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        cancelButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                imageLayout.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);

                selectedImageUri = null;
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ArrayList<String> likers = new ArrayList<>();
                final ArrayList<String> unlikers = new ArrayList<>();
                final ArrayList<String> favouriteArrayList = new ArrayList<>();

                likers.add("1234");
                unlikers.add("1234");
                favouriteArrayList.add("1234");

                if (selectedImageUri != null) {
                    Log.i("point m192", selectedImageUri.toString());
                    imageLayout.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    final StorageReference photoREf = mChatPhotosStorageReference.child(Objects.requireNonNull(selectedImageUri.getLastPathSegment()));
                    photoREf.putFile(selectedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Log.i("point", "then: ");
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "error while uploading", Toast.LENGTH_SHORT).show();
                            }

                            // Continue with the task to get the download URL
                            return photoREf.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                String downloadURL = downloadUri.toString();
                                Log.i("point m201", selectedImageUri.toString());
                                mProgressBar.setVisibility(View.INVISIBLE);
                                imageView.setImageResource(0);
                                Post post = new Post(mMessageEditText.getText().toString().trim(), downloadURL, calculateTime(), mUserId, likers, unlikers, favouriteArrayList);

                                mMessagesDatabaseReference.push().setValue(post);
                                selectedImageUri = null;
                            } else {
                                Toast.makeText(MainActivity.this, "error while uploading", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else if (mMessageEditText.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter some text", Toast.LENGTH_SHORT).show();
                } else {
                    Post post = new Post(mMessageEditText.getText().toString().trim(), null, calculateTime(), mUserId, likers, unlikers, favouriteArrayList);
                    mMessagesDatabaseReference.push().setValue(post);
                }
                // Clear input box
                mMessageEditText.setText("");
            }
        });


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //to find if user is signed or not
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed
                    Log.i("point m200", "user log in" + mUserId + " " + user + " " + user.getUid());
                    onSignInitialize(user.getDisplayName(), user.getEmail(), user.getUid());
                    Query query = mUserDatabaseReference.orderByChild("userId").equalTo(mUserId);
                    query.addValueEventListener(vel);
                } else {
                    //user signed out
                    onSignOutCleaner();

                    startActivityForResult((new Intent(getApplicationContext(), LoginActivity.class)),
                            RC_SIGN_IN);
                    Snackbar snackbar = Snackbar.make(mainLayout, "Logged out successfully", Snackbar.LENGTH_SHORT);
                    View sbView = snackbar.getView();
                    TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.GREEN);
                    snackbar.show();
                }
            }
        };

//        mySwipeRefreshLayout.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener()
//
//                {
//                    @Override
//                    public void onRefresh() {
//                        itemPos = 0;
//
//                        refreshChildListener = new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
//                                messageKey = dataSnapshot.getKey();
//                                Log.i("point ma352", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);
//                                Post post = dataSnapshot.getValue(Post.class);
//                                Log.i("point ma354", post.getText());
//
//                                if (!mPrevKey.equals(messageKey)) {
//
//                                    Log.i("point ma356", post.getText());
//                                    if (post.getKey() == null) post.setKey(dataSnapshot.getKey());
//                                    posts.add(itemPos++, post);
//
//                                } else {
//                                    mLayoutManager.scrollToPositionWithOffset(itemPos - 1, 0);
//                                    mPrevKey = mLastKey;
//
//                                }
//                                if (itemPos == 1) {
//
//                                    mLastKey = messageKey;
//
//                                }
//
//                                mAdapter.notifyDataSetChanged();
//                                mySwipeRefreshLayout.setRefreshing(false);
//
//                            }
//
//                            @Override
//                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                                Post post = dataSnapshot.getValue(Post.class);//as Post has all the three required parameter
//
//                                for (Iterator<Post> iterator = posts.iterator(); iterator.hasNext(); ) {
//                                    if (iterator.next().getKey() == dataSnapshot.getKey())
//                                        iterator.remove();
//                                }
//                                mAdapter.notifyDataSetChanged();
//
//                            }
//
//                            @Override
//                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        };

//                        mMessagesDatabaseReference.orderByKey().endAt(mLastKey).limitToLast(7).addChildEventListener(refreshChildListener);
//                        childEventListenersList.add(refreshChildListener);
//                    }
//                });


    }

    private void showWeatherDataView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private ValueEventListener vel = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            long count = dataSnapshot.getChildrenCount();
            Log.i("point ma460", count + " " + dataSnapshot);
            if (count == 0) {
                Log.i("point ma462", "seems new");

                ArrayList<String> favorite = new ArrayList<>();
                favorite.add("random fav");
                userInfo = new UserInfo(mUsername, mUserId, mEmailId, favorite);
                mUserDatabaseReference.push().setValue(userInfo);
            } else {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    userInfo = dsp.getValue(UserInfo.class); //add result into array list

                }
                Log.i("point ma474", dataSnapshot + "");

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//signing prosses result called before onResume
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) { //if returns request code sign in
            if (resultCode == RESULT_OK) {//successful login
                Snackbar snackbar = Snackbar.make(mainLayout, "Logged in successfully", Snackbar.LENGTH_SHORT);//snackbar
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.GREEN);
                snackbar.show();

            } else if (resultCode == RESULT_CANCELED) {//dont login
                Snackbar snackbar = Snackbar.make(mainLayout, "Logging in failed!!", Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.RED);
                snackbar.show();
            }
        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            imageLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);

        }

    }

    public void asd(View v) {
        Log.i("point", "asd: " + mAdapter.getItemCount());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mUsername = ANONYMOUS;
                mEmailId = "";
                mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
                AuthUI.getInstance().signOut(this);//from login providers and smart lock//redirects to onpause and on resume
                return true;

            case R.id.profile:
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("email", mEmailId);
                startActivity(intent);
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("point m309", "onpause");
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
        //remove authentication

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("point m323", "resume");
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        //authenticate and carry
    }

    private void onSignInitialize(String username, String email, String uid) {
        mUsername = username;
        mEmailId = email;
        mUserId = uid;


        PostViewModel viewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        liveData = viewModel.getDataSnapshotLiveData();
        liveData.observe(this, countObserver);

        databaseLiveData = viewModel.getDatabaseLiveData();
        databaseLiveData.observe(this, postObserver
        );
    }

    private Observer<Long> countObserver = new Observer<Long>() {
        @Override
        public void onChanged(@Nullable Long aLong) {
            // update the UI here with values in the snapshot
            System.out.println("We're done loading the initial " + aLong + " items");
            input.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

    };

    private Observer<List<Post>> postObserver = new Observer<List<Post>>() {
        @Override
        public void onChanged(@Nullable List<Post> posts) {
//                Log.i("point 430", "onChanged: " + mAdapter.getItemCount());
            if (posts == null) posts = new ArrayList<>();

            Log.i("point", "onChanged: " + posts.size());
            mAdapter.swapForecast(posts);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            mRecyclerView.smoothScrollToPosition(mPosition);

            // Show the list or the loading screen based on whether the forecast data exists
            // and is loaded
            if (posts.size() != 0) showWeatherDataView();
            else showLoading();
        }
    };

    private void onSignOutCleaner() {
        mUsername = ANONYMOUS;
        mEmailId = "";
        mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());

        databaseLiveData.removeObserver(postObserver);
        liveData.removeObserver(countObserver);
    }

    public String calculateTime() {
        return android.text.format.DateFormat.format("MMM dd, yyyy hh:mm:ss aaa", new java.util.Date()).toString();
    }

}
