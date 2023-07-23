package com.example.talkzy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.material.ProgressIndicatorDefaults;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.talkzy.databinding.ActivityOtpactivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukeshsolanki.OnOtpCompletionListener;

import org.jetbrains.annotations.TestOnly;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

     public String verificationID;
     FirebaseAuth auth;
     FirebaseDatabase database;
     ActivityOtpactivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
       String Number = getIntent().getStringExtra("PhoneNumber");
        Number = "+91"+Number;
        binding.text1.setText("Verify " + Number);
        ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending OTP");
        dialog.setCancelable(false);
        dialog.show();

        auth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        FirebaseAuthSettings firebaseAuthSettings = auth.getFirebaseAuthSettings();

        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(Number, verificationID);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(Number)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        dialog.dismiss();
                        verificationID = verifyId;
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        binding.otpView.requestFocus();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
//        binding.verifybtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String otp = binding.otpEditText.getText().toString().trim();
//                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, otp);
//                if (!otp.isEmpty()) {
//                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                     @Override
//                     public void onComplete(@NonNull Task<AuthResult> task) {
//                          if(task.isSuccessful()){
//                              Toast.makeText(OTPActivity.this, "LoggedIn", Toast.LENGTH_SHORT).show();
//                              Intent intent = new Intent(OTPActivity.this , SetupUserProfile.class);
//                              startActivity(intent);
//                              finishAffinity();
//                          }else{
//                              Toast.makeText(OTPActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                          }
//                     }
//                 });
//                } else {
//                    Toast.makeText(OTPActivity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


         binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
             @Override
             public void onOtpCompleted(String otp) {
                 PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID , otp);
                 auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                          if(task.isSuccessful()){
                              Toast.makeText(OTPActivity.this, "LoggedIn", Toast.LENGTH_SHORT).show();

                                      Intent intent = new Intent(OTPActivity.this , SetupUserProfile.class);
                                      startActivity(intent);
                                      finishAffinity();
                          }else{
                              Toast.makeText(OTPActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                          }
                     }
                 });
             }
         });
    }
}