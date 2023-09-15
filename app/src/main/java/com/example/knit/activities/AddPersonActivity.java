package com.example.knit.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.knit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Lets the user add a new person to their account. The input data is stored
 * in the 'persons' collection in Firebase Cloud Firestore.
 */
public class AddPersonActivity extends AppCompatActivity {
    // Objects
    private String userId;
    //Widgets
    private TextView actionbarTitle;
    private EditText editName, editBust, editWaist, editHips, editHandLen, editHandCir, editFootLen, editFootCir, editNotes;
    private Toolbar actionbar;
    private Button btnSave;
    private ProgressBar progressBar;
    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        // Action bar
        actionbar = findViewById(R.id.action_bar);
        actionbarTitle = findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.add_person);
        btnSave = findViewById(R.id.actionbar_save);
        btnSave.setVisibility(View.VISIBLE);
        actionbar.setNavigationIcon(R.drawable.icon_close);

        // Initialize widgets
        // Edit texts
        editName = findViewById(R.id.person_edit_name);
        editBust = findViewById(R.id.person_edit_bust);
        editWaist = findViewById(R.id.person_edit_waist);
        editHips = findViewById(R.id.person_edit_hips);
        editHandLen = findViewById(R.id.person_edit_hand_len);
        editHandCir = findViewById(R.id.person_edit_hand_cir);
        editFootLen = findViewById(R.id.person_edit_foot_len);
        editFootCir = findViewById(R.id.person_edit_foot_cir);
        editNotes = findViewById(R.id.person_edit_notes);

        // Progress bar
        progressBar = findViewById(R.id.add_person_progress_bar);

        // Click listener to navigate to previous screen
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Click listener for saving input
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                addPersonToDatabase();
            }
        });
    }

    /**
     * Adds the details for a new person in the 'persons' collection
     * in Firebase
     */
    private void addPersonToDatabase() {
        Map<String, Object> person = new HashMap<>();
        person.put("name", editName.getText().toString());
        person.put("bust", editBust.getText().toString());
        person.put("waist", editWaist.getText().toString());
        person.put("hips", editHips.getText().toString());
        person.put("handLen", editHandLen.getText().toString());
        person.put("handCir", editHandCir.getText().toString());
        person.put("footLen", editFootLen.getText().toString());
        person.put("footCir", editFootCir.getText().toString());
        person.put("notes", editNotes.getText().toString());

        db.collection("knitters").document(userId).collection("persons").add(person).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
