//Sourcecode from Firebase. 10. feb. 2023. EmailPasswordActivity.java. Version 2.0.//
// URL: https://github.com/firebase/snippets-android/blob/067d83afbe16fe078e0b68e511827f8cfcdd6467/auth/app/src/main/java/com/google/firebase/quickstart/auth/EmailPasswordActivity.java#L49-L57//

package com.example.knit.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.knit.MainActivity;
import com.example.knit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Lets the user to sign in to an existing account with Firebase Authentication.
 * After signing in the user is sent to the MainActivity.class.
 */

public class SignInActivity extends AppCompatActivity {
    // Objects
    private String email, password;

    // Widgets
    private EditText viewEmail, viewPassword;
    private TextInputLayout layoutEmail, layoutPassword;
    private Button btnSignIn, btnSwitch, btnForgotPassword;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();

        // Initialize widgets
        // Edit texts
        viewEmail = findViewById(R.id.signin_edit_email);
        viewPassword = findViewById(R.id.signin_edit_password);

        // Layouts
        layoutEmail = findViewById(R.id.signin_layout_email);
        layoutPassword = findViewById(R.id.signin_layout_password);

        // Buttons
        btnSignIn = findViewById(R.id.signin_btn_signIn);
        btnSwitch = findViewById(R.id.signin_btn_register);
        btnForgotPassword = findViewById(R.id.signin_btn_forgot_password);

        // Progress bar
        progressBar = findViewById(R.id.signin_progress_bar);

        // Click listener for signing in user
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });

        // Click listener for switching to register activity
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToRegister();
            }
        });
    }

    /**
     * Validates the user input and displays an error if the e-mail or password is missing.
     * If the input is valid is a progress indicator displayed, the user is signed in
     * with Firebase Authentication, and sent to the MainActivity.class.
     * If the sign in fails is an error message displayed.
     */
    private void signInUser() {
        email = viewEmail.getText().toString().trim();
        password = viewPassword.getText().toString().trim();

        // Validate input
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError(getResources().getString(R.string.enter_valid_mail));
            return;
        }
        if (password.matches("")) {
            layoutPassword.setError(getResources().getString(R.string.enter_password));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                layoutEmail.setError(getResources().getString(R.string.wrong_input));
                layoutPassword.setError(getResources().getString(R.string.wrong_input));
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * This method sends the user to the Register activity.
     */
    private void switchToRegister() {
        Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}


