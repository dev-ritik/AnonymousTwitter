package com.example.android.anonymoustwitter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 100;
    //set default message limit

    private static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    static boolean calledAlready = false;

    private PostAdapter mPostAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private ImageView imageView;
    private EditText mMessageEditText;
    private ImageButton mSendButton, cancelButton;
    private CoordinatorLayout mainLayout;
    private CoordinatorLayout imageLayout;
    private Uri selectedImageUri, downloadUrl;
    private LinearLayout input;

    private RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Post> posts;

    ArrayList<String> likers;
    ArrayList<String> unlikers;
    ArrayList<String> favouriteArrayList;

    public static String mUserId;
    public static String mUser;

    private String mEmailId;
    private long count;

    private FirebaseDatabase mfirebaseDatabase;
    public static DatabaseReference mMessagesDatabaseReference;
    public static ChildEventListener mChildEventListener;//to listen the changes in db
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        mUserId = ANONYMOUS;
        mEmailId = "";
        count = 0;

        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mMessagesDatabaseReference = mfirebaseDatabase.getReference().child("input");
        mChatPhotosStorageReference = mFirebaseStorage.getReference("chat_photos");

        mFirebaseAuth = FirebaseAuth.getInstance();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);
        cancelButton = findViewById(R.id.cancel);
        mainLayout = findViewById(R.id.main_content);
        imageView = findViewById(R.id.inputImage);
        imageLayout = findViewById(R.id.imageLayout);
        input = findViewById(R.id.linearLayout);


        posts = new ArrayList<>();

        mAdapter = new PostAdapter(posts);

        mRecyclerView.setAdapter(mAdapter);

//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter

                Log.i("swipped", "standpoint m170");
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.LEFT) {
                    //if swipe left
                    Log.i("swipped", "standpoint m175");

                    if (posts.get(position).getPosterId().equals(mUserId)) {
                        Log.i("swipped", "standpoint m177");

                        new AlertDialog.Builder(MainActivity.this) //alert for confirm to delete
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Caution")
                                .setMessage("This post will be deleted")
                                .setPositiveButton("CANCEL", new DialogInterface.OnClickListener() { //when click on DELETE
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).
                                setNegativeButton("DELETE", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mAdapter.notifyItemRemoved(position);    //item removed from recylcerview
                                        System.out.println("standpoint m185");

                                        Query query = MainActivity.mMessagesDatabaseReference.orderByChild("timeCurrent").equalTo(posts.get(position).getTimeCurrent());
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                    child.getRef().removeValue();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        posts.remove(position);  //then remove item

                                    }

                                }).show();  //show alert dialog
                    }
                }

            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // view the background view

            }
        };
                new ItemTouchHelper(itemTouchHelperCallback).
                attachToRecyclerView(mRecyclerView);


        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
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

                downloadUrl = null;
                selectedImageUri = null;
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener()

        {//send button
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click

                likers = new ArrayList<>();
                unlikers = new ArrayList<>();
                favouriteArrayList = new ArrayList<>();

                likers.add("1234");
                unlikers.add("1234");
                favouriteArrayList.add("1234");

                if (selectedImageUri != null) {
                    Log.i(selectedImageUri.toString(), "standpoint m192");
                    imageLayout.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    StorageReference photoREf = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
//            //take last part of uri location link and make child of mChatPhotosStorageReference
                    photoREf.putFile(selectedImageUri).addOnSuccessListener(MainActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        //                    upload file to firebase onsucess of upload
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadUrl = taskSnapshot.getDownloadUrl();//url of uploaded image
//                            Log.i(selectedImageUri.toString(),"standpoint m201");
                            Log.i(mMessageEditText.getText().toString(), "standpoint m202");
                            mProgressBar.setVisibility(View.INVISIBLE);
                            likers = new ArrayList<>();
                            unlikers = new ArrayList<>();
                            favouriteArrayList = new ArrayList<>();
                            likers.add("1234");
                            unlikers.add("1234");
                            favouriteArrayList.add("1234");
                            imageView.setImageResource(0);
                            Post post = new Post(mMessageEditText.getText().toString().trim(), downloadUrl.toString(), calculateTime(),mUser, mUserId, likers, unlikers, favouriteArrayList);
                            mMessagesDatabaseReference.push().setValue(post);
                            downloadUrl = null;
                            selectedImageUri = null;
                        }
                    });
                } else if (mMessageEditText.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter some text", Toast.LENGTH_SHORT).show();
                } else {
                    Post post = new Post(mMessageEditText.getText().toString().trim(), null, calculateTime(),mUser, mUserId, likers, unlikers, favouriteArrayList);
                    mMessagesDatabaseReference.push().setValue(post);
                }
                // Clear input box
                mMessageEditText.setText("");
                Log.i("button pressed", "standpoint 165");
            }
        });


        mAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //to find if user is signed or not
                if (mChildEventListener != null) {
                    Log.i(mChildEventListener.toString(), "standpoint m203");
                }
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed
//                    Toast.makeText(MainActivity.this, "Welcome to FriendlyChat!", Toast.LENGTH_SHORT).show();
                    Log.i("user log in", "standpoint m200");
                    onSignInitilize(user.getUid(), user.getEmail());
                    mUser = user.getDisplayName();//send name and operate with db
//                    Log.i(user.getEmail(),"standpoint m202");//p
//                    Log.i(user.getPhotoUrl().toString(),"standpoint m205");//p
//                    Log.i(user.getUid(),"standpoint m206");//p

                } else {
                    //user signed out
                    onSignOutCleaner();

                    startActivityForResult((new Intent(getApplicationContext(), com.example.android.anonymoustwitter.LoginActivity.class)),
                            RC_SIGN_IN);
                    Snackbar snackbar = Snackbar.make(mainLayout, "Logged out successfully", Snackbar.LENGTH_SHORT);
                    View sbView = snackbar.getView();
                    TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.GREEN);
                    snackbar.show();
                }
            }
        };
    }


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
            Log.i(selectedImageUri.toString(), "standpoint m302");
            imageView.setImageURI(selectedImageUri);

        }

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
                mUserId = ANONYMOUS;
                mEmailId = "";
                posts.clear();
//        mPostAdapter.clear();//clear adapter so that it doesn't holds any earlier data
                mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
                AuthUI.getInstance().signOut(this);//from login providers and smart lock//redirects to onpause and on resume
                return true;

            case R.id.profile:
                Intent intent = new Intent(getApplicationContext(), com.example.android.anonymoustwitter.ProfileActivity.class);

                intent.putExtra("email", mEmailId);
                startActivity(intent);
                System.out.println("standpoint m313");
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        Log.i("onpause", "standpoint m309");
        detachDatabaseReadListener();
        posts.clear();
//        mPostAdapter.clear();//clear all data stored in adapter
        mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
        Log.i("clear", "standpoint 263");
        //remove authentication

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("resume", "standpoint m323");
        if (mChildEventListener != null) {
            Log.i(mChildEventListener.toString(), "standpoint m351");
        }
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        if (mChildEventListener != null) {
            Log.i(mChildEventListener.toString(), "standpoint m355");
        }
        //authenticate and carry
    }

    private void onSignInitilize(String userid, String email) {
        mUserId = userid;
        mEmailId = email;
        attachDatabaseListener();//sync and download content and update adapter

    }

    private void onSignOutCleaner() {
        mUserId = ANONYMOUS;
        mEmailId = "";
        posts.clear();
//        mPostAdapter.clear();//clear adapter so that it doesn't holds any earlier data
        mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());

    }

    private void attachDatabaseListener() {
        mMessagesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("We're done loading the initial " + dataSnapshot.getChildrenCount() + " items");
                input.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (mChildEventListener != null) {
            Log.i(mChildEventListener.toString(), "standpoint m293");
        }
        if (mChildEventListener == null) {
            Log.i("mChildEventListener", "standpoint 298");
            mChildEventListener = new ChildEventListener() {//working with db after authentication
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.i("onchildadded", "standpoint 1");
//                    Log.i(dataSnapshot.getKey(), "standpoint 1");

                    //attached to all added child(all past and future child)
                    Post post = dataSnapshot.getValue(Post.class);//as Post has all the three required parameter
                    Log.i(post.getPosterId(), "standpoint m483");
                    posts.add(post);

                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    // changed content of a child
                    Log.i("child changed", "standpoint m370");
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    // child deleted
                    Post post = dataSnapshot.getValue(Post.class);//as Post has all the three required parameter

                    for (Iterator<Post> iterator = posts.iterator(); iterator.hasNext(); ) {
                        if (iterator.next().getTimeCurrent() == post.getTimeCurrent())
                            iterator.remove();
                    }
                    Log.i(Integer.toString(posts.size()), "standpoint m389");
                    mAdapter.notifyDataSetChanged();

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
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
            Log.i("child addeddd", "standpoint m610");
            if (mChildEventListener != null) {
                Log.i("mChildEventListener add", "standpoint 322");
            }
        }

    }


    private void detachDatabaseReadListener() {
        if (mChildEventListener != null)
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
        mChildEventListener = null;
        Log.i("mchild=null", "standpoint m622");
    }

    public String calculateTime() {
//        return DateFormat.getDateTimeInstance().format(new Date());
        return android.text.format.DateFormat.format("MMM dd, yyyy hh:mm:ss aaa", new java.util.Date()).toString();
//        String dt="11-01-2016 5:8 AM";
//        DateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
//        return format.toString();

    }

}
