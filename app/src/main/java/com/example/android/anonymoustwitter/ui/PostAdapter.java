package com.example.android.anonymoustwitter.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.anonymoustwitter.model.Post;
import com.example.android.anonymoustwitter.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.Gravity.START;
import static com.example.android.anonymoustwitter.ui.MainActivity.mMessagesDatabaseReference;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView messageTextView, timeTextView, likes, unlikes;
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
            messageLayout2 = view.findViewById(R.id.linearLayout2);

            timeTextView = (TextView) view.findViewById(R.id.time);
            likes = (TextView) view.findViewById(R.id.reactUpCount);
            unlikes = (TextView) view.findViewById(R.id.reactdownCount);

            likeButton = view.findViewById(R.id.reactup);
            unlikeButton = view.findViewById(R.id.reactdown);

            favouritePost = view.findViewById(R.id.favorite);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
//    public PostAdapter(ArrayList<Post> myDataset) {
//        mDataset = myDataset;
//    }

    public PostAdapter() {
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_message, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }


//    void swapForecast(final List<Post> posts) {
//        // If there was no forecast data, then recreate all of the list
//        if (mDataset == null) {
//            mDataset = posts;
//            notifyDataSetChanged();
//        } else {
//            /*
//             * Otherwise we use DiffUtil to calculate the changes and update accordingly. This
//             * shows the four methods you need to override to return a DiffUtil callback. The
//             * old list is the current list stored in mDataset, where the new list is the new
//             * values passed in from the observing the database.
//             */
//
//            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
//                @Override
//                public int getOldListSize() {
//                    return mDataset.size();
//                }
//
//                @Override
//                public int getNewListSize() {
//                    return posts.size();
//                }
//
//                @Override
//                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//                    Boolean asd = mDataset.get(oldItemPosition).getKey().equals(
//                            posts.get(newItemPosition).getKey());
//                    return asd;
//                }
//
//                @Override
//                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//                    Post newWeather = posts.get(newItemPosition);
//                    Post oldWeather = mDataset.get(oldItemPosition);
//                    return newWeather.getKey().equals(oldWeather.getKey());
//                }
//            });
////            mDataset = posts;
//            mDataset=new ArrayList<>(posts);
//            result.dispatchUpdatesTo(PostAdapter.this);
//
//        }
//    }


    void swapForecast(final List<Post> posts) {
        // If there was no forecast data, then recreate all of the list
        if (mDataset == null) {
            mDataset = posts;
            notifyDataSetChanged();
        } else {
            mDataset = posts;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Post post = mDataset.get(position);

        if (post.getPhotoUrl() != null) {
            if (post.getText() == null) {
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

            holder.messageLayout.setGravity(Gravity.END);
        } else {
            holder.messageLayout.setGravity(START);
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
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
                Log.i("started anim", "standpoint po179");
                if (post.getLikedUsers().contains(MainActivity.mUserId)) { //trying to get neutral from liked
                    Log.i("neutral from liked", "standpoint po192");
                    post.getLikedUsers().remove(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.likeButton.setChecked(false);
                    changeData("likedUsers", post.getKey(), post.getLikedUsers());
                } else if (!post.getLikedUsers().contains(MainActivity.mUserId) && !post.getUnlikedUsers().contains(MainActivity.mUserId)) { //trying to like from neutral
                    Log.i("like from neutral", "standpoint po202");

                    post.getLikedUsers().add(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.likeButton.setChecked(true);
                    changeData("likedUsers", post.getKey(), post.getLikedUsers());
                } else if (!post.getLikedUsers().contains(MainActivity.mUserId) && post.getUnlikedUsers().contains(MainActivity.mUserId)) { //trying to like from unlike
                    Log.i("like from unlike", "standpoint po209");
                    post.getLikedUsers().add(MainActivity.mUserId);
                    post.getUnlikedUsers().remove(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.likeButton.setChecked(true);
                    holder.unlikeButton.setChecked(false);
                    changeData("likedUsers", post.getKey(), post.getLikedUsers());
                    changeData("unlikedUsers", post.getKey(), post.getUnlikedUsers());
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

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
                Log.i("started anim", "standpoint po179");
                if (post.getUnlikedUsers().contains(MainActivity.mUserId)) { //trying to get neutral from unliked
                    Log.i("neutral from unliked", "standpoint po298");
                    post.getUnlikedUsers().remove(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.unlikeButton.setChecked(false);
                    changeData("unlikedUsers", post.getKey(), post.getUnlikedUsers());
                } else if (!post.getLikedUsers().contains(MainActivity.mUserId) && !post.getUnlikedUsers().contains(MainActivity.mUserId)) { //trying to unlike from neutral
                    Log.i("unlike from neutral", "standpoint po305");
                    post.getUnlikedUsers().add(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.unlikeButton.setChecked(true);
                    changeData("unlikedUsers", post.getKey(), post.getUnlikedUsers());
                } else if (post.getLikedUsers().contains(MainActivity.mUserId) && !post.getUnlikedUsers().contains(MainActivity.mUserId)) { //trying to unlike from like
                    Log.i("unlike from like", "standpoint po312");
                    post.getLikedUsers().remove(MainActivity.mUserId);
                    post.getUnlikedUsers().add(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.likeButton.setChecked(false);
                    holder.unlikeButton.setChecked(true);
                    changeData("likedUsers", post.getKey(), post.getLikedUsers());
                    changeData("unlikedUsers", post.getKey(), post.getUnlikedUsers());
                }
                holder.likes.setText(Integer.toString(post.getLikedUsers().size() - 1));
                holder.unlikes.setText(Integer.toString(post.getUnlikedUsers().size() - 1));
            }
        });

        String[] timeArray = post.getTimeCurrent().split(" ");

        String[] currentTimeArray = android.text.format.DateFormat.format("MMM dd, yyyy hh:mm:ss aaa", new java.util.Date()).toString().split(" ");

        if (!timeArray[2].equals(currentTimeArray[2])) {

            holder.timeTextView.setText(String.format("%s %s %s\n%s %s", timeArray[0], timeArray[1], timeArray[2], timeArray[3], timeArray[4]));
        } else {
            if (!timeArray[0].equals(currentTimeArray[0]) || !timeArray[1].equals(currentTimeArray[1])) {
                holder.timeTextView.setText(String.format("%s %s\n%s %s", timeArray[0], timeArray[1], timeArray[3], timeArray[4]));
            } else {
                holder.timeTextView.setText(String.format("%s %s", timeArray[3], timeArray[4]));
            }
        }

        if (post.getSaveIt().contains(MainActivity.mUserId)) {
            holder.favouritePost.setChecked(true);

        } else {
            holder.favouritePost.setChecked(false);
        }

        holder.favouritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!post.getSaveIt().contains(MainActivity.mUserId)) {
                    post.getSaveIt().add(MainActivity.mUserId);
                    changeData("saveIt", post.getKey(), post.getSaveIt());
                    changeFavouriteData(post.getKey(), true);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.favouritePost.setChecked(true);

                } else {
                    post.getSaveIt().remove(MainActivity.mUserId);
                    MainActivity.mAdapter.notifyDataSetChanged();
                    holder.favouritePost.setChecked(false);
                    changeData("saveIt", post.getKey(), post.getSaveIt());
                    changeFavouriteData(post.getKey(), false);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mDataset == null) return 0;
        return mDataset.size();
    }

    public void changeData(final String arrayName, String key, final ArrayList<String> getArrayList) {
        mMessagesDatabaseReference.orderByKey().startAt(key).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    child.getRef().child(arrayName).setValue(getArrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void changeFavouriteData(String key, boolean add) {
        Log.i(key, "point pa358");
        if (add)
            MainActivity.userInfo.getFavourites().add(key);
        else MainActivity.userInfo.getFavourites().remove(key);

        Query query = MainActivity.mUserDatabaseReference.orderByChild("userId").equalTo(MainActivity.mUserId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    child.getRef().child("favourites").setValue(MainActivity.userInfo.getFavourites());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
