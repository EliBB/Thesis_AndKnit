package com.example.knit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.knit.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
/**
 * Checks whether the user is signed in by the Firebase Authentication.
 * The user is either sent to the MainActivity.class if already signed in
 * or sent to the SignInActivity.class if not.
 * If the user is sent to the MainActivity.class is the task and activity stack cleared.
 */

public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = (new Intent(SplashScreenActivity.this, SignInActivity.class));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }
}