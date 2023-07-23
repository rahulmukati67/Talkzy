package com.example.talkzy;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.talkzy.databinding.ActivitySetupUserProfileBinding;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class SetupUserProfile extends AppCompatActivity {
    ActivitySetupUserProfileBinding binding;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    Uri selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        binding = ActivitySetupUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        database = FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        ProgressDialog dialog = new ProgressDialog(SetupUserProfile.this);
        dialog.setMessage("Creating Profile");
        dialog.setCancelable(false);

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });





        binding.setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.Name.getText().toString();
                if(name.isEmpty()){
                  binding.Name.setError("Please Enter Your Name");
                  return;
                }

                if(selectedImage!=null){
                    dialog.show();
                    StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                         if(task.isSuccessful()){
                             reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {
                                  String imageUrl = uri.toString();
                                  String uid = auth.getUid();
                                  String phoneNumber = auth.getCurrentUser().getPhoneNumber();
                                  String name= binding.Name.getText().toString();
                                  User user = new User(uid, name,phoneNumber,imageUrl);


                                  database.getReference()
                                          .child("users")
                                          .child(uid)
                                          .setValue(user)
                                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(@NonNull Void avoid) {
                                                  Intent intent = new Intent(SetupUserProfile.this,MainActivity.class);
                                                  startActivity(intent);
                                                  dialog.dismiss();
                                                  finish();
                                              }
                                          });
                                 }

                             });
                         }
                        }
                    });
                }else{
//                    dialog.show();
//                    String uid = auth.getUid();
//                    String phoneNumber = auth.getCurrentUser().getPhoneNumber();
//                    User user = new User(uid, name,phoneNumber,"No Image");
//
//
//                    database.getReference()
//                            .child("users")
//                            .child(uid)
//                            .setValue(user)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(@NonNull Void avoid) {
//                                    Intent intent = new Intent(SetupUserProfile.this,MainActivity.class);
//                                    startActivity(intent);
//                                    dialog.dismiss();
//                                    finish();
//                                }
//                            });



                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SetupUserProfile.this);
                    builder1.setMessage("Profile Picture Not Selected.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Okay I will Select",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();


                }
            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            if(data.getData()!=null){
                binding.profileImage.setImageURI(data.getData());
                selectedImage  = data.getData();
            }
        }
    }
}