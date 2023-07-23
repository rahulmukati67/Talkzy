package com.example.talkzy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.talkzy.databinding.ActivityStatusBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StatusActivity extends AppCompatActivity {
       ActivityStatusBinding binding;
       TopStatusAdapter statusAdapter;
       ArrayList<UserStatus> statuses;
       ProgressDialog dialog;
       FirebaseDatabase database;
       User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Status...");
        dialog.setCancelable(false);
        statuses = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        binding.StatusDp.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Status");;

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    statuses.clear();
                    for(DataSnapshot storysnaphot:snapshot.getChildren()){
                        UserStatus status = new UserStatus();
                        status.setName(storysnaphot.child("name").getValue(String.class));
                        status.setProfileImage(storysnaphot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storysnaphot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses1 = new ArrayList<>();
                        for(DataSnapshot statusnaphot : storysnaphot.child("status").getChildren() ){
                            Status sampleStatus = statusnaphot.getValue(Status.class);
                            statuses1.add(sampleStatus);
                        }
                        status.setStatuses(statuses1);
                        statuses.add(status);
                    }

                  statusAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(!(statuses ==null)) {
            statusAdapter = new TopStatusAdapter(this, statuses);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            binding.StatusRecyclerView.setLayoutManager(layoutManager);
            binding.StatusRecyclerView.setAdapter(statusAdapter);

        }else{
            Toast.makeText(this, "Nothing to show right now.....", Toast.LENGTH_SHORT).show();
        }




        binding.AddStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 75);
            }
        });


        binding.bottommenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.chat:
                        Intent intent = new Intent(StatusActivity.this,MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                        break;
                }
                return false;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(data.getData()!=null){
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime()+"");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                         if(task.isSuccessful()){
                             reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {
                                     UserStatus userStatus = new UserStatus();
                                     userStatus.setName(user.getName());
                                     userStatus.setProfileImage(user.getProfileImage());
                                     userStatus.setLastUpdated(date.getTime());

                                     HashMap<String , Object> obj = new HashMap<>();
                                     obj.put("name" , userStatus.getName());
                                     obj.put("profileImage" , userStatus.getProfileImage());
                                     obj.put("lastUpdated" , userStatus.getLastUpdated());

                                     String imageUrl = uri.toString();
                                     Status status = new Status(imageUrl,userStatus.getLastUpdated());
                                     database.getReference().child("stories")
                                                     .child(FirebaseAuth.getInstance().getUid())
                                                             .updateChildren(obj);

                                     database.getReference().child("stories")
                                                     .child(FirebaseAuth.getInstance().getUid())
                                                             .child("status")
                                                                     .push()
                                                                             .setValue(status);


                                     dialog.dismiss();


                                 }
                             });
                         }
                    }
                });
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}