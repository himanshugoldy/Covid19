package com.example.authenticationapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView verifyMsg,dashboard;
    Button resendCode;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        dashboard = findViewById(R.id.dashboard);
        verifyMsg = findViewById(R.id.verifyMsg);
        resendCode = findViewById(R.id.resendCode);
        FirebaseUser fuser = fAuth.getCurrentUser();

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!fuser.isEmailVerified()){
                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this,"Verification Email has sent.",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent "+e.getMessage());
                        }
                    });
                }else{
                    verifyMsg.setVisibility(View.INVISIBLE);
                    resendCode.setVisibility(View.INVISIBLE);
//                  startActivity(new Intent(getApplicationContext(),Biometric.class));
                }
            }
        });






        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fuser.isEmailVerified()){
                    verifyMsg.setVisibility(View.INVISIBLE);
                    resendCode.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(getApplicationContext(),Biometric.class));
                }else{
                    Toast.makeText(MainActivity.this,"Please Verify Email!",Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
}