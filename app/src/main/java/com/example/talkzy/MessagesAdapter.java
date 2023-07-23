package com.example.talkzy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talkzy.databinding.ItemRecieveBinding;
import com.example.talkzy.databinding.ItemSentBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {
      Context context;
      ArrayList<Message> messages;
      final int ITEM_SENT =1;
      final int ITEM_REC =2;
      String senderRoom ;
      String receiverRoom;
    public MessagesAdapter(Context context , ArrayList<Message> messages , String senderRoom, String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.receiverRoom=receiverRoom;
        this.senderRoom = senderRoom;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        }else{
            return ITEM_REC;

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent , parent ,false);
            return  new SentViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve, parent, false);
            return  new ReceiverViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        int reaction [] = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reaction)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {

            if(holder.getClass()==SentViewHolder.class){
                SentViewHolder viewHolder = (SentViewHolder) holder;
                viewHolder.binding.reaction.setImageResource(reaction[pos]);
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);
            }else{
                ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                viewHolder.binding.reaction.setImageResource(reaction[pos]);
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);
            }
             message.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);


            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            return true; // true is closing popup, false is requesting a new selection
        });



        if(holder.getClass()== SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder)holder;
            viewHolder.binding.message.setText(message.getMessage());

            if(message.getFeeling()>=0){
//                message.setFeeling(reaction[(int) message.getFeeling()]);
                viewHolder.binding.reaction.setImageResource(reaction[message.getFeeling()]);
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);

            }else{
                viewHolder.binding.reaction.setVisibility(View.GONE);
            }
            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });
        }else{
             ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
             viewHolder.binding.message.setText(message.getMessage());
            if(message.getFeeling()>=0){
//                message.setFeeling(reaction[(int) message.getFeeling()]);
                viewHolder.binding.reaction.setImageResource(reaction[message.getFeeling()]);
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);
            }else{
                viewHolder.binding.reaction.setVisibility(View.GONE);
            }
            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{
                ItemSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends  RecyclerView.ViewHolder{
             ItemRecieveBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecieveBinding.bind(itemView);
        }
    }
}
