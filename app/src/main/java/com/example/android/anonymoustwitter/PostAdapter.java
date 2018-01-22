package com.example.android.anonymoustwitter;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private ArrayList<Post> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView messageTextView, timeTextView, likes, unlikes,NameText;
        ImageView photoImageView;
        LinearLayout messageLayout2;
        LinearLayout messageLayout;

        SparkButton likeButton, unlikeButton;

        CheckBox favouritePost;


        private ViewHolder(View view) {
            super(view);
            photoImageView = (ImageView) view.findViewById(R.id.photoImageView);
            messageTextView = (TextView) view.findViewById(R.id.messageTextView);
            messageLayout = view.findViewById(R.id.messageLayout);
            messageLayout2=view.findViewById(R.id.linearLayout2);
            NameText=view.findViewById(R.id.NameTextView);

            timeTextView = (TextView) view.findViewById(R.id.time);
            likes = (TextView) view.findViewById(R.id.reactUpCount);
            unlikes = (TextView) view.findViewById(R.id.reactdownCount);

            likeButton = view.findViewById(R.id.reactup);
            unlikeButton = view.findViewById(R.id.reactdown);

            favouritePost = view.findViewById(R.id.favorite);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostAdapter(ArrayList<Post> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_message, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Post post = mDataset.get(position);
        Log.i(post.getText(), "standpoint po99");
        Log.i("standpoint po98", post.getPosterId());

        if (post.getPhotoUrl() != null) {
            if (post.getText() == null) {
//                Log.i(post.getText(), "standpoint p100");
                holder.messageTextView.setVisibility(View.GONE);
                holder.photoImageView.setVisibility(View.VISIBLE);
                Glide.with(holder.photoImageView.getContext())
                        .load(post.getPhotoUrl())
                        .into(holder.photoImageView);
            } else {
                holder.messageTextView.setVisibility(View.VISIBLE);
                holder.messageTextView.setText(post.getText());
                holder.photoImageView.setVisibility(View.VISIBLE);
                Glide.with(holder.photoImageView.getContext())
                        .load(post.getPhotoUrl())
                        .into(holder.photoImageView);
            }
        } else {
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.photoImageView.setVisibility(View.GONE);
            holder.messageTextView.setText(post.getText());

        }
        if (post.getPosterId().equals(MainActivity.mUserId)) {

            holder.messageLayout.setGravity(RIGHT);
        } else {
            holder.messageLayout.setGravity(LEFT);
        }

        if(post.getUser()!=null){
            holder.NameText.setText(post.getUser());
        }else{
            holder.NameText.setText("anonymous");
        }

        holder.likes.setText(Integer.toString(post.getLikedUsers().size() - 1));
        holder.unlikes.setText(Integer.toString(post.getUnlikedUsers().size() - 1));

        if (post.getLikedUsers().contains(MainActivity.mUserId)) {
            holder.likeButton.setChecked(true);

            holder.unlikeButton.setChecked(false);

        } else {
            holder.likeButton.setChecked(false);
        }

        if (post.getUnlikedUsers().contains(MainActivity.mUserId)) {
            holder.unlikeButton.setChecked(true);
            holder.likeButton.setChecked(false);

        } else {
            holder.unlikeButton.setChecked(false);
        }

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("like button pressed", "standpoint m167");
                holder.likeButton.setAnimationSpeed(1f);
                holder.likeButton.playAnimation();


            }
        });

        holder.likeButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                if (buttonState) {
                    // Button is active
//                    Log.i("end as true", "standpoint m205");
                } else {
                    // Button is inactive
//                    Log.i("end as false", "standpoint m209");

                }

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
                Log.i("started anim", "standpoint po179");
                if (post.getLikedUsers().contains(MainActivity.mUserId)) { //trying to get neutral from liked
                    Log.i("neutral from liked", "standpoint po192");
                    post.getLikedUsers().remove(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.likeButton.setChecked(false);
                    changeData("likedUsers", post.getTimeCurrent(), post.getLikedUsers());
                } else if (!post.getLikedUsers().contains(MainActivity.mUserId) && !post.getUnlikedUsers().contains(MainActivity.mUserId)) { //trying to like from neutral
                    Log.i("like from neutral", "standpoint po202");

                    post.getLikedUsers().add(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.likeButton.setChecked(true);
                    changeData("likedUsers", post.getTimeCurrent(), post.getLikedUsers());
                } else if (!post.getLikedUsers().contains(MainActivity.mUserId) && post.getUnlikedUsers().contains(MainActivity.mUserId)) { //trying to like from unlike
                    Log.i("like from unlike", "standpoint po209");
                    post.getLikedUsers().add(MainActivity.mUserId);
                    post.getUnlikedUsers().remove(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.likeButton.setChecked(true);
                    holder.unlikeButton.setChecked(false);
                    changeData("likedUsers", post.getTimeCurrent(), post.getLikedUsers());
                    changeData("unlikedUsers", post.getTimeCurrent(), post.getUnlikedUsers());
                }
                if (buttonState) {
                    // Button is active
                    holder.likeButton.setChecked(false);
                    Log.i("start true-false", "standpoint m219");

                } else {
                    // Button is inactive
                    holder.likeButton.setChecked(true);
                    Log.i("start false-true", "standpoint m223");

                }

            }
        });

        holder.unlikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("unlike button pressed", "standpoint po167");
                holder.unlikeButton.setAnimationSpeed(1f);
                holder.unlikeButton.playAnimation();


            }
        });
        holder.unlikeButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                if (buttonState) {
                    // Button is active
                    Log.i("end as true", "standpoint po205");
                } else {
                    // Button is inactive
                    Log.i("end as false", "standpoint po209");

                }
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
                Log.i("started anim", "standpoint po179");
                if (post.getUnlikedUsers().contains(MainActivity.mUserId)) { //trying to get neutral from unliked
                    Log.i("neutral from unliked", "standpoint po298");
                    post.getUnlikedUsers().remove(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.unlikeButton.setChecked(false);
                    changeData("unlikedUsers", post.getTimeCurrent(), post.getUnlikedUsers());
                } else if (!post.getLikedUsers().contains(MainActivity.mUserId) && !post.getUnlikedUsers().contains(MainActivity.mUserId)) { //trying to unlike from neutral
                    Log.i("unlike from neutral", "standpoint po305");
                    post.getUnlikedUsers().add(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.unlikeButton.setChecked(true);
                    changeData("unlikedUsers", post.getTimeCurrent(), post.getUnlikedUsers());
                } else if (post.getLikedUsers().contains(MainActivity.mUserId) && !post.getUnlikedUsers().contains(MainActivity.mUserId)) { //trying to unlike from like
                    Log.i("unlike from like", "standpoint po312");
                    post.getLikedUsers().remove(MainActivity.mUserId);
                    post.getUnlikedUsers().add(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.likeButton.setChecked(false);
                    holder.unlikeButton.setChecked(true);
                    changeData("likedUsers", post.getTimeCurrent(), post.getLikedUsers());
                    changeData("unlikedUsers", post.getTimeCurrent(), post.getUnlikedUsers());
                }
                holder.likes.setText(Integer.toString(post.getLikedUsers().size() - 1));
                holder.unlikes.setText(Integer.toString(post.getUnlikedUsers().size() - 1));
            }
        });

        String[] timeArray = post.getTimeCurrent().split(" ");

        String[] currentTimeArray =android.text.format.DateFormat.format("MMM dd, yyyy hh:mm:ss aaa", new java.util.Date()).toString().split(" ");

        if (!timeArray[2].equals(currentTimeArray[2])) {
//            System.out.println("stadpoint124");

            holder.timeTextView.setText(timeArray[0] + " " + timeArray[1] + " " + timeArray[2] + "\n" + timeArray[3] + " " + timeArray[4]);
        } else {
//            System.out.println("stadpoint128");
            if (!timeArray[0].equals(currentTimeArray[0]) || !timeArray[1].equals(currentTimeArray[1])) {
                holder.timeTextView.setText(timeArray[0] + " " + timeArray[1] + "\n" + timeArray[3] + " " + timeArray[4]);
            } else {
                holder.timeTextView.setText(timeArray[3] + " " + timeArray[4]);
            }
        }

        if (post.getSaveIt().contains(MainActivity.mUserId)) {
            holder.favouritePost.setChecked(true);
            System.out.println("stadpoint p427");

        } else {
            holder.favouritePost.setChecked(false);
            System.out.println("stadpoint p431");
        }

        holder.favouritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!post.getSaveIt().contains(MainActivity.mUserId)) {
                    post.getSaveIt().add(MainActivity.mUserId);
                    changeData("saveIt", post.getTimeCurrent(), post.getSaveIt());
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.favouritePost.setChecked(true);
                    System.out.println("stadpoint p437");

                } else {
                    post.getSaveIt().remove(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.favouritePost.setChecked(false);
                    changeData("saveIt", post.getTimeCurrent(), post.getSaveIt());
                    System.out.println("stadpoint p443");
                }
//                System.out.println("stadpoint p233");

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void changeData(final String arrayName, String time, final ArrayList<String> getArrayList) {

//        Log.i(time, "standpoint m411");

        Query query = MainActivity.mMessagesDatabaseReference.orderByChild("timeCurrent").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ArrayList<String> likers = (ArrayList<String>) child.child(arrayName).getValue();

                    child.getRef().child(arrayName).setValue(getArrayList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
