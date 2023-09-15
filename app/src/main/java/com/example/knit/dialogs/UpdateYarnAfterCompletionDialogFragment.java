package com.example.knit.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.knit.R;
import com.example.knit.adapters.YarnMoveAdapter;
import com.example.knit.classes.Knitters;
import com.example.knit.classes.Projects;
import com.example.knit.classes.Yarn;
import com.example.knit.fragments.ProjectsFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UpdateYarnAfterCompletionDialogFragment extends DialogFragment {
    // Objects
    private static final String TAG = "MoveDialogFragment:  ";
    private String userId, documentId, endDate;
    private Integer newGramAmount, skeinLength, skeinWeight, newMeterAmount, newSkeinAmount, completedProjectsInProfile, alreadySkeinsUsed, alreadyGramsUsed, alreadyMetersKnitted, newCompletedProjects, newSkeinsUsed, newGramsUsed, newMetersKnitted;
    private Double newDoubleSkeinAmount;
    private ArrayList<Yarn> arrayList;
    private ArrayList<String> stringIds;
    private Bundle bundle;

    // Widgets
    private TextView actionbarTitle;
    private Button btnConfirm;
    private RecyclerView recyclerView;
    private Toolbar actionbar;

    // Adapter
    private YarnMoveAdapter adapter;

    // Classes
    private Yarn yarn;
    private Knitters knitter;
    private Projects projects;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public UpdateYarnAfterCompletionDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
    }

    /**
     * Defines the layout for the dialog.
     */
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_move_to_completed_dialog, container, false);

        // Get data from previous fragment
        bundle = getArguments();
        documentId = bundle.getString("documentId");
        endDate = bundle.getString("endDate");

        // Action bar
        actionbar = v.findViewById(R.id.action_bar);
        actionbarTitle = v.findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.dialog_register_leftover_yarn);
        actionbar.setNavigationIcon(R.drawable.icon_close);

        // Initialize widgets
        // Button
        btnConfirm = v.findViewById(R.id.btn_confirm_move);

        // Recycler view
        recyclerView = v.findViewById(R.id.move_yarn_recyclerView);

        // Initialize arraylists
        arrayList = new ArrayList<>();
        stringIds = new ArrayList<>();

        getAssignedYarn();

        // Click listener for closing dialog
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // Click listener for saving the input
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateYarnInDatabase();
                updateProject();
                updateProfileStatistics();
                navigateToProjectsScreen();
            }
        });

        return v;
    }

    /**
     * Displays the assigned yarn for a specific project.
     * The user is able to enter the amount of leftover yarn for
     * each yarn that has been used for the project.
     */
    private void getAssignedYarn() {
        db.collection("knitters").document(userId).collection("projects").document(documentId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                projects = value.toObject(Projects.class);
                if (projects != null) {
                    for (String yarnId : projects.getYarnIds()) {
                        db.collection("knitters").document(userId).collection("yarn").whereEqualTo("yarnId", yarnId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                                        yarn = documentSnapshot.toObject(Yarn.class);
                                        arrayList.add(yarn);
                                    }
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    adapter = new YarnMoveAdapter(arrayList, getContext());
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Updates the new amount of yarn for each yarn item, that has been assigned
     * to the project, in the 'yarn' collection.
     */
    private void updateYarnInDatabase() {
        for (Yarn yarn : arrayList) {
            if (yarn.getAmountGrams() != null && yarn.getAmountMeters() != null && yarn.getAmountSkeins() != null) {

                newGramAmount = yarn.getYarnLeftover();

                skeinLength = yarn.getSkeinLength();
                skeinWeight = Integer.valueOf(yarn.getSkeinWeight().replaceAll("\\s....", "").trim());

                newMeterAmount = (skeinLength / skeinWeight) * newGramAmount;

                newDoubleSkeinAmount = Math.ceil(Double.valueOf(newGramAmount) / Double.valueOf(skeinWeight));
                newSkeinAmount = newDoubleSkeinAmount.intValue();

                db.collection("knitters").document(userId).collection("yarn").document(yarn.getDocumentId()).update(
                        "amountGrams", newGramAmount,
                        "amountMeters", newMeterAmount,
                        "amountSkeins", newSkeinAmount);
            }

        }
    }

    /** Registers the project to 'completed' and updates the end date */
    private void updateProject() {
        db.collection("knitters").document(userId).collection("projects").document(documentId).
                update("projectType", "2",
                        "endDate", endDate);
    }

    /**
     * Called when the user has entered new yarn amounts.
     * The used yarn amount is added to the user's statistics on the Profile screen.
     */
    private void updateProfileStatistics() {
        db.collection("knitters").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (yarn.getAmountGrams() != null && yarn.getAmountMeters() != null && yarn.getAmountSkeins() != null) {
                    knitter = documentSnapshot.toObject(Knitters.class);

                    alreadySkeinsUsed = knitter.getSkeinsUsed();
                    alreadyGramsUsed = knitter.getGramsUsed();
                    alreadyMetersKnitted = knitter.getMetersKnitted();

                    newSkeinsUsed = alreadySkeinsUsed + (yarn.getAmountSkeins() - newSkeinAmount);
                    newGramsUsed = alreadyGramsUsed + (yarn.getAmountGrams() - newGramAmount);
                    newMetersKnitted = alreadyMetersKnitted + (yarn.getAmountMeters() - newMeterAmount);

                    db.collection("knitters").document(userId).update(
                            "completedProjects", FieldValue.increment(1),
                            "skeinsUsed", newSkeinsUsed,
                            "gramsUsed", newGramsUsed,
                            "metersKnitted", newMetersKnitted);
                } else {
                    db.collection("knitters").document(userId).update("completedProjects", FieldValue.increment(1));
                }
            }
        });
    }

    /** Leads the user to the Projects screen */
    private void navigateToProjectsScreen() {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
        ProjectsFragment fragment = new ProjectsFragment();
        appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }
}