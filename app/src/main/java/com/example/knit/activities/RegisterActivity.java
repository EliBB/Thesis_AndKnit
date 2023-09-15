//Sourcecode from Firebase. 10. feb. 2023. EmailPasswordActivity.java. Version 2.0.//
// URL: https://github.com/firebase/snippets-android/blob/067d83afbe16fe078e0b68e511827f8cfcdd6467/auth/app/src/main/java/com/google/firebase/quickstart/auth/EmailPasswordActivity.java#L49-L57//

package com.example.knit.activities;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows a new user to register to the &Knit app with Firebase Authentication.
 * The new user added to the Firebase Firestore database.
 * After registering is the user sent to the MainActivity.class.
 */

public class RegisterActivity extends AppCompatActivity {
    // Objects
    private String userId, name, dateRegistered, email, password;

    // Widgets
    private EditText viewName, viewEmail, viewPassword;
    private TextInputLayout layoutName, layoutEmail, layoutPassword;
    private Button btnRegister, btnSwitch;
    private ProgressBar progressBar;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize widgets
        viewName = findViewById(R.id.register_edit_name);
        viewEmail = findViewById(R.id.register_edit_email);
        viewPassword = findViewById(R.id.register_edit_password);
        layoutName = findViewById(R.id.register_layout_name);
        layoutEmail = findViewById(R.id.register_layout_email);
        layoutPassword = findViewById(R.id.register_layout_password);
        btnRegister = findViewById(R.id.register_btn_register);
        btnSwitch = findViewById(R.id.register_btn_signin);
        progressBar = findViewById(R.id.register_progress_bar);

        // Click listener for registering new user
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Click listener for switching to the Sign in activity
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSignIn();
            }
        });
    }

    /**
     * Validates the user input and displays an error if the e-mail, name,
     * or password is missing. If the input is valid is a progress indicator displayed,
     * the user is registered with Firebase Authentication, a new database instance
     * is created, and the user is sent to the MainActivity.class.
     * If the registration fails is error messages displayed.
     */
    private void registerUser() {
        name = viewName.getText().toString().trim();
        email = viewEmail.getText().toString().trim();
        password = viewPassword.getText().toString().trim();
        // Validate input
        if (name.matches("")) {
            layoutName.setError(getResources().getString(R.string.enter_name));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError(getResources().getString(R.string.enter_valid_mail));
            return;
        }
        if (password.length() < 6) {
            layoutPassword.setError(getResources().getString(R.string.enter_valid_password));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                userId = mAuth.getCurrentUser().getUid();
                getCurrentDate();
                Map<String, Object> knitter = new HashMap<>();
                knitter.put("name", name);
                knitter.put("dateCreated", dateRegistered);
                knitter.put("completedProjects", 0);
                knitter.put("skeinsUsed", 0);
                knitter.put("gramsUsed", 0);
                knitter.put("metersKnitted", 0);

                db.collection("knitters").document(userId)
                        .set(knitter)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                progressBar.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                layoutEmail.setError(getResources().getString(R.string.went_wrong));
                layoutPassword.setError(getResources().getString(R.string.went_wrong));
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Gets the current date and stores it as a string.
     */
    private void getCurrentDate() {
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
        dateRegistered = date.format(new Date());
    }

    /**
     * Sends the user to the Sign in activity.
     */
    private void switchToSignIn() {
        Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}