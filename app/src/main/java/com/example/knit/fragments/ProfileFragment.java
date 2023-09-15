package com.example.knit.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knit.MainActivity;
import com.example.knit.activities.AddPatternActivity;
import com.example.knit.activities.AddPersonActivity;
import com.example.knit.adapters.PersonAdapter;
import com.example.knit.activities.RegisterActivity;
import com.example.knit.activities.SignInActivity;
import com.example.knit.R;
import com.example.knit.classes.Knitters;
import com.example.knit.classes.Persons;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment that holds the functionality and layout for the 'Profile' item in
 * the bottom navigation bar. Gets data from the 'Knitters' and 'Persons' collections in
 * the database. Keeps track of the users statistics, body measurements, notes, and
 * persons.
 */

public class ProfileFragment extends Fragment {
    // Objects
    private static final String TAG = "Profile";
    private String userId;
    private ArrayList<Persons> personsArrayList;

    // Widgets
    private TextView actionbarTitle, viewName, viewBust, viewWaist, viewHips, viewHandLen, viewHandCir, viewFootLen, viewFootCir, viewNotes, statsCompletedProjects, statsSkeinsUsed, statsGramsUsed, statsMetersKnitted;
    private EditText editName, editBust, editWaist, editHips, editHandLen, editHandCir, editFootLen, editFootCir, editNotes;
    private TextInputLayout layoutEditName;
    private LinearLayout viewLayout, editLayout;
    private Button btnSave, btnEdit, btnAddPerson, btnLogout, btnDeleteUser;
    private RecyclerView personRecyclerView;
    private Toolbar actionbar;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Adapter
    private PersonAdapter adapter;

    // Classes
    private Knitters knitter;

    public ProfileFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);
        // Action bar
        actionbar = v.findViewById(R.id.action_bar);
        actionbarTitle = v.findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.profile);
        btnEdit = v.findViewById(R.id.actionbar_edit);
        btnEdit.setVisibility(View.VISIBLE);
        btnSave = v.findViewById(R.id.actionbar_save);

        // Initialize widgets
        // Statistics text views
        statsCompletedProjects = v.findViewById(R.id.profile_completed_projects);
        statsSkeinsUsed = v.findViewById(R.id.profile_skeins_used);
        statsGramsUsed = v.findViewById(R.id.profile_grams_used);
        statsMetersKnitted = v.findViewById(R.id.profile_meters_knitted);

        // Body measurements text views
        viewName = v.findViewById(R.id.profile_view_name);
        viewBust = v.findViewById(R.id.view_knitter_bust);
        viewWaist = v.findViewById(R.id.profile_view_waist);
        viewHips = v.findViewById(R.id.profile_view_hips);
        viewHandCir = v.findViewById(R.id.profile_view_hand_cir);
        viewHandLen = v.findViewById(R.id.profile_view_hand_len);
        viewFootCir = v.findViewById(R.id.profile_view_foot_cir);
        viewFootLen = v.findViewById(R.id.profile_view_foot_len);
        viewNotes = v.findViewById(R.id.profile_view_notes);

        // Body measurements edits texts
        editName = v.findViewById(R.id.profile_edit_name);
        layoutEditName = v.findViewById(R.id.profile_layout_edit_name);
        editBust = v.findViewById(R.id.profile_edit_bust);
        editWaist = v.findViewById(R.id.profile_edit_waist);
        editHips = v.findViewById(R.id.profile_edit_hips);
        editHandCir = v.findViewById(R.id.profile_edit_hand_cir);
        editHandLen = v.findViewById(R.id.profile_edit_hand_len);
        editFootCir = v.findViewById(R.id.profile_edit_foot_cir);
        editFootLen = v.findViewById(R.id.profile_edit_foot_len);
        editNotes = v.findViewById(R.id.profile_edit_notes);

        // Layouts
        viewLayout = v.findViewById(R.id.item_person_layout_view);
        editLayout = v.findViewById(R.id.item_person_layout_edit);

        // Buttons
        btnAddPerson = v.findViewById(R.id.profile_btn_add_person);
        btnLogout = v.findViewById(R.id.profile_btn_logout);
        btnDeleteUser = v.findViewById(R.id.profile_btn_delete);

        // RecyclerView
        personRecyclerView = v.findViewById(R.id.profile_recycler_view_persons);

        // Init arraylist and adapter for recycler view
        personsArrayList = new ArrayList<>();
        adapter = new PersonAdapter(personsArrayList, getContext());

        // Click listener for editing the data inputs
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make layouts visible and hide others
                viewLayout.setVisibility(View.GONE);
                editLayout.setVisibility(View.VISIBLE);
                viewName.setVisibility(View.GONE);
                layoutEditName.setVisibility(View.VISIBLE);
                viewNotes.setVisibility(View.GONE);
                editNotes.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);

                // Display body measurements in edit texts
                editName.setText(knitter.getName());
                editBust.setText(knitter.getBust());
                editWaist.setText(knitter.getWaist());
                editHips.setText(knitter.getHips());
                editHandLen.setText(knitter.getHandLen());
                editHandCir.setText(knitter.getHandCir());
                editFootLen.setText(knitter.getFootLen());
                editFootCir.setText(knitter.getFootCir());
                editNotes.setText(knitter.getNotes());
            }
        });

        // Click listener for saving input in edit mode
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewLayout.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.GONE);
                viewName.setVisibility(View.VISIBLE);
                layoutEditName.setVisibility(View.GONE);
                editNotes.setVisibility(View.GONE);
                viewNotes.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.GONE);

                updateKnitter();
            }
        });

        // Click listener for starting activity of adding a new person
        btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddPersonActivity.class);
                startActivity(intent);
            }
        });

        // Click listener for logging out of the &Knit app
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmLogout();
            }
        });

        // Click listener for deleting the account
        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteUser();
            }
        });

        // Display data
        displayKnitter();
        displayPersons();

        return v;
    }

    /**
     * Gets the user information in the root collection 'knitters'
     * and displays it in text viees.
     */
    private void displayKnitter() {
        db.collection("knitters").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                knitter = value.toObject(Knitters.class);

                // Display stats
                statsCompletedProjects.setText(String.valueOf(knitter.getCompletedProjects()));
                statsSkeinsUsed.setText(String.valueOf(knitter.getSkeinsUsed()));
                statsGramsUsed.setText(String.valueOf(knitter.getGramsUsed()));
                statsMetersKnitted.setText(String.valueOf(knitter.getMetersKnitted()));

                // Display body measurements and notes
                viewName.setText(knitter.getName());
                viewBust.setText(knitter.getBust());
                viewWaist.setText(knitter.getWaist());
                viewHips.setText(knitter.getHips());
                viewHandLen.setText(knitter.getHandLen());
                viewHandCir.setText(knitter.getHandCir());
                viewFootLen.setText(knitter.getFootLen());
                viewFootCir.setText(knitter.getFootCir());
                viewNotes.setText(knitter.getNotes());
            }
        });
    }

    /**
     * Gets the information for added persons from the subcollection 'persons'
     * and display all persons in a recyclerview by the PersonsAdapter.
     */
    private void displayPersons() {
        db.collection("knitters").document(userId).collection("persons").orderBy("name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    personsArrayList.clear();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        Persons person = documentSnapshot.toObject(Persons.class);
                        personsArrayList.add(person);
                        personRecyclerView.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                    personRecyclerView.setHasFixedSize(true);
                    personRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    personRecyclerView.setAdapter(adapter);
                }
            }
        });
    }

    /**
     * Posts new data to the 'knitters' collection.
     */
    private void updateKnitter() {
        db.collection("knitters").document(userId).update(
                "name", editName.getText().toString(),
                "bust", editBust.getText().toString(),
                "waist", editWaist.getText().toString(),
                "hips", editHips.getText().toString(),
                "handCir", editHandCir.getText().toString(),
                "handLen", editHandLen.getText().toString(),
                "footCir", editFootCir.getText().toString(),
                "footLen", editFootLen.getText().toString(),
                "notes", editNotes.getText().toString());
    }


    /**
     * Alert dialog that shows when the user wants to log out. If yes the user is
     * send to the SignIn activity.
     */
    private void confirmLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(R.string.alert_title_logout);
        builder.setMessage(getResources().getString(R.string.alert_message_logout));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Alert dialog that shows when the user wants to delete the account. If yes the user is
     * send to the Register activity.
     */
    private void confirmDeleteUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(R.string.alert_title_delete_user);
        builder.setMessage(getResources().getString(R.string.alert_message_delete_user));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUser user = mAuth.getCurrentUser();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            db.collection("knitters").document(userId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "Deletion successful");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "Deletion failed" + e.getMessage());
                                }
                            });
                            Intent intent = new Intent(getActivity(), RegisterActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, task.getException().getMessage());
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}