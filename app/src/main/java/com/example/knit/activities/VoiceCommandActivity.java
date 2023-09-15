package com.example.knit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.knit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class VoiceCommandActivity extends AppCompatActivity {
    // Objects
    private static final String TAG = "Voice command";
    private String userId, documentId, projectId, counterId, nameStr, increment, decrement;
    private Double countDb;
    private Integer countInt;
    private SpeechRecognizer speechRecognizer;

    // Widgets
    private TextView counterName, count, listenView, actionbarTitle;
    private EditText editName;
    private ImageView startMic, btnIncre, btnReset, btnDecre;
    private TextInputLayout editNameLayout;
    private ProgressBar listeningBar;
    private Toolbar actionbar;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_command);
        // Firbase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        // Action bar
        actionbar = findViewById(R.id.voice_command_action_bar);
        actionbarTitle = findViewById(R.id.actionbar_title);
        actionbar.setNavigationIcon(R.drawable.icon_arrow_back);

        // Initialize widgets
        // Text view
        counterName = findViewById(R.id.voice_command_view_name);
        count = findViewById(R.id.voice_command_view_count);
        listenView = findViewById(R.id.voice_command_view_listening);

        // Edit text
        editName = findViewById(R.id.voice_command_edit_name);
        editNameLayout = findViewById(R.id.voice_command_layout_name);

        // Buttons
        startMic = findViewById(R.id.voice_command_btn_microphone);
        btnDecre = findViewById(R.id.voice_command_btn_decrement);
        btnReset = findViewById(R.id.voice_command_btn_reset);
        btnIncre = findViewById(R.id.voice_command_btn_increment);

        // Progress bar
        listeningBar = findViewById(R.id.voice_command_progress_bar);

        // Get data from previous fragment
        documentId = getIntent().getStringExtra("documentId");
        projectId = getIntent().getStringExtra("projectId");
        counterId = getIntent().getStringExtra("counterId");

        // Display count & counter name
        displayData();

        // Request permission to use microphone
        requestPermissions();

        // Initialize speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Start listening
        speechRecognizer.startListening(intent);

        // Commands
        increment = "plus";
        decrement = "minus";

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                listeningBar.setVisibility(View.VISIBLE);
                listenView.setVisibility(View.VISIBLE);
                Log.d(TAG, "OnReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "OnBeginningOfSpeech");
                listeningBar.setVisibility(View.VISIBLE);
                listenView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
                listeningBar.setVisibility(View.INVISIBLE);
                listenView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> inputArr = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String input = inputArr.get(0).toLowerCase().trim();
                if (input.equals(increment)) {
                    incrementCount();
                } else if (input.equals(decrement)) {
                    decrementCount();
                }
                listeningBar.setVisibility(View.INVISIBLE);
                listenView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                Log.d(TAG, "OnPartialResults");
            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        // Click listener to navigate to previous screen
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Click listener to listen to input
        startMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechRecognizer.startListening(intent);
            }
        });

        // Click listener for increment count
        btnIncre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incrementCount();
            }
        });

        // Click listener for resetting count
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCount();
            }
        });

        // Click listener for decrement count
        btnDecre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrementCount();
            }
        });
    }

    /**
     * Displays the name of the counter and the count. Retrieves data from the database
     * based on whether the user entered the activity via the Tools screen or
     * via a specific project.
     */
    private void displayData() {
        // If accessed via Tools screen
        if (documentId != null) {
            db.collection("knitters").document(userId).collection("counters").document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        nameStr = documentSnapshot.getString("name");
                        countDb = documentSnapshot.getDouble("count");
                        // Convert double to int
                        if (countDb != null) {
                            countInt = countDb.intValue();
                            count.setText(countInt.toString());
                        }
                        counterName.setText(nameStr);
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            // Accessed via project item
            db.collection("knitters").document(userId).collection("projects").document(projectId).collection("project counters").document(counterId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        nameStr = documentSnapshot.getString("name");
                        countDb = documentSnapshot.getDouble("count");
                        // Convert double to int
                        if (countDb != null) {
                            countInt = countDb.intValue();
                            count.setText(countInt.toString());
                        }
                        counterName.setText(nameStr);
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /**
     * Increments the count and saves new count in database based on
     * the user's entry for the activity
     */
    private void incrementCount() {
        countInt++;
        count.setText(countInt.toString());
        if (documentId != null) {
            db.collection("knitters").document(userId).collection("counters").document(documentId).update("count", countInt);
        } else {
            db.collection("knitters").document(userId).collection("projects").document(projectId).collection("project counters").document(counterId).update("count", countInt);
        }
    }

    /**
     * Decrements the count and saves new count in database based on
     * the user's entry for the activity
     */
    private void decrementCount() {
        if (countInt != 0) {
            countInt--;
        }
        count.setText(countInt.toString());
        if (documentId != null) {
            db.collection("knitters").document(userId).collection("counters").document(documentId).update("count", countInt);
        } else {
            db.collection("knitters").document(userId).collection("projects").document(projectId).collection("project counters").document(counterId).update("count", countInt);
        }
    }

    /**
     * Resets the count and saves new count in database based on
     * the user's entry for the activity
     */
    private void resetCount() {
        countInt = 0;
        count.setText(countInt.toString());
        if (documentId != null) {
            db.collection("knitters").document(userId).collection("counters").document(documentId).update("count", countInt);
        } else {
            db.collection("knitters").document(userId).collection("projects").document(projectId).collection("project counters").document(counterId).update("count", countInt);

        }
    }

    /**
     * Checks runtime permissions for microphone in Manifest file
     */
    private void requestPermissions() {
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {android.Manifest.permission.RECORD_AUDIO};
            requestPermissions(permission, 1);
        }
    }
}