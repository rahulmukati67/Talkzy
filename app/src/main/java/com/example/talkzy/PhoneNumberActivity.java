package com.example.talkzy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.talkzy.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;

public class PhoneNumberActivity extends AppCompatActivity {
     ActivityPhoneNumberBinding binding;
     FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(PhoneNumberActivity.this , MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding.verifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneNumberActivity.this , OTPActivity.class);
                intent.putExtra("PhoneNumber" , binding.PhoneNumber.getText().toString());
                startActivity(intent);
            }
        });

    }
}