package com.example.knit.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.knit.MainActivity;
import com.example.knit.R;
import com.example.knit.activities.AddPatternActivity;
import com.example.knit.activities.AddProjectActivity;
import com.example.knit.adapters.PatternsAdapter;
import com.example.knit.classes.Patterns;
import com.example.knit.interfaces.PatternsListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Tabsection that displays the patterns from the 'patterns' subcollection in a recyclerview.
 * Implements the PatternsListener.
 */
public class StoragePatternsFragment extends Fragment implements PatternsListener {
    // Objects
    private static final String TAG = "Patterns";
    private String userId;
    private ArrayList<Patterns> patternsArrayList;

    // Widgets
    private TextView noDataView;
    private ExtendedFloatingActionButton btnAddPattern;
    private RecyclerView patternsRecyclerView;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Adapter
    private PatternsAdapter adapter;

    public StoragePatternsFragment() {
        // Required empty public constructor
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
        final View v = inflater.inflate(R.layout.fragment_storage_patterns, container, false);
        // Initialize widgets
        btnAddPattern = v.findViewById(R.id.storage_patterns_btn_add);
        noDataView = v.findViewById(R.id.view_no_yarn);
        progressBar = v.findViewById(R.id.storage_patterns_progress_bar);
        patternsRecyclerView = v.findViewById(R.id.storage_patterns_recycler_view);

        // Arraylist and adapter for the recyclerview
        patternsArrayList = new ArrayList<>();
        adapter = new PatternsAdapter(patternsArrayList, getContext(), this);

        // Click listener starting the activity of adding a new pattern
        btnAddPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddPatternActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), btnAddPattern, "transition_fab");
                startActivity(intent, options.toBundle());
            }
        });

        displayPatterns();

        return v;
    }

    /**
     * Retrieves the patterns from the database and displays them in a recyclerview
     */
    private void displayPatterns() {
        db.collection("knitters").document(userId).collection("patterns").orderBy("patternName").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    patternsArrayList.clear();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        Patterns patterns = documentSnapshot.toObject(Patterns.class);
                        patternsArrayList.add(patterns);
                        noDataView.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    patternsRecyclerView.setHasFixedSize(true);
                    patternsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    patternsRecyclerView.setAdapter(adapter);
                } else {
                    noDataView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Listens to user clicks and sends data via a bundle object to
     * a StoragePatternItemFragment to display the clicked pattern
     */
    @Override
    public void onItemClick(Patterns patterns) {
        // If the pattern is a pdf
        String patternUrl = patterns.getPdfUrl();

        // If the pattern is images
        String imagesPath = patterns.getPatternName();

        AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
        Bundle mBundle = new Bundle();
        mBundle.putString("patternUrl", patternUrl);
        mBundle.putString("imagesPath", imagesPath);
        StoragePatternsItemFragment fragment = new StoragePatternsItemFragment();
        fragment.setArguments(mBundle);
        appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }
}