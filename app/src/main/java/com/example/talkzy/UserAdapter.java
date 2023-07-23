package com.example.talkzy;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.talkzy.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
       Context context;

       ArrayList<User> users;

    public UserAdapter(Context context , ArrayList<User> users) {
        this.context = context;
        this.users=users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation,parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
         User user = users.get(position);
         holder.binding.profileName.setText(user.getName());
        Glide.with(context).load(user.getProfileImage()).into(holder.binding.profilepic);


        String senderUid = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderUid+user.getUid();

        FirebaseDatabase.getInstance().getReference()
                        .child("chats")
                                .child(senderRoom)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if((snapshot.exists())){
                                                    String lastMsg = snapshot.child("LastMsg").getValue(String.class);
                                                    Long timeLong = snapshot.child("Time").getValue(Long.class);
                                                    if (timeLong != null) {
                                                        long time = timeLong;
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                                                        holder.binding.time.setText(dateFormat.format(new Date(time)));
                                                    } else {
                                                        holder.binding.time.setText("");
                                                    }
                                                    holder.binding.lastChat.setText(lastMsg);

                                                }else{
                                                    holder.binding.lastChat.setText("Tap to chat");

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , ChatActivity.class);
                intent.putExtra("Name" , user.getName());
                intent.putExtra("Uid" , user.getUid());
//                intent.putExtra("ProfilePic" , user.getProfileImage());
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
             RowConversationBinding binding;

        public UserViewHolder(@NonNull View itemView) {
              super(itemView);
             binding = RowConversationBinding.bind(itemView);
        }
    }
}
