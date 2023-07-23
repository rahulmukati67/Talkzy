package com.example.talkzy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.talkzy.databinding.ActivityChatBinding;
import com.example.talkzy.databinding.ItemResourceBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder>{

    Context context;
    ArrayList<UserStatus> userStatuses;

    public TopStatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resource, parent, false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {
        if(position>=-1) {
            UserStatus userStatus = userStatuses.get(position);
            Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() - 1);
            Glide.with(context).load(lastStatus.getImageUrl()).into(holder.binding.image);
            holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());
            holder.binding.profileName.setText(userStatus.getName());
//            holder.binding.time.setText((int)userStatus.getLastUpdated());

            long time = userStatus.getLastUpdated();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
            holder.binding.time.setText(dateFormat.format(new Date(time)));

            holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<MyStory> myStories = new ArrayList<>();
//                UserStatus userStatus = new UserStatus();
                    for (Status status : userStatus.getStatuses()) {
                        myStories.add(new MyStory(status.getImageUrl()));
                    }

                    new StoryView.Builder(((StatusActivity) context).getSupportFragmentManager())
                            .setStoriesList(myStories) // Required
                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                            .setTitleText(userStatus.getName()) // Default is Hidden
                            .setSubtitleText("") // Default is Hidden
                            .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                            .setStoryClickListeners(new StoryClickListeners() {
                                @Override
                                public void onDescriptionClickListener(int position) {
                                    //your action
                                }

                                @Override
                                public void onTitleIconClickListener(int position) {
                                    //your action
                                }
                            }) // Optional Listeners
                            .build() // Must be called before calling show method
                            .show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class TopStatusViewHolder extends RecyclerView.ViewHolder {
        ItemResourceBinding binding;
        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= ItemResourceBinding.bind(itemView);
        }
    }
}
