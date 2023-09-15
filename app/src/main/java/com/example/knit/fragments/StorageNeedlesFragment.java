package com.example.knit.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.knit.R;
import com.example.knit.activities.AddNeedlesActivity;
import com.example.knit.adapters.NeedlesAdapter;
import com.example.knit.classes.Needles;
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
 * Tabsection that displays the needles from the 'needles' subcollection in a recyclerview
 */
public class StorageNeedlesFragment extends Fragment {
    // Objects
    private String userId;
    private ArrayList<Needles> needlesArrayList;

    // Widgets
    private TextView noDataView;
    private RecyclerView needlesRecyclerView;
    private ExtendedFloatingActionButton btnAddNeedles;
    private ProgressBar progressBar;

    // Database
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Adapter
    private NeedlesAdapter adapter;

    public StorageNeedlesFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_storage_needles, container, false);

        // Initialize widgets
        btnAddNeedles = v.findViewById(R.id.storage_needles_btn_add);
        noDataView = v.findViewById(R.id.view_no_yarn);
        progressBar = v.findViewById(R.id.storage_needles_progress_bar);
        needlesRecyclerView = v.findViewById(R.id.recyclerView);


        // Arraylist and adapter for the recyclerview
        needlesArrayList = new ArrayList<>();
        adapter = new NeedlesAdapter(needlesArrayList, getContext());


        // Click listener starting the activity of adding new needles
        btnAddNeedles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddNeedlesActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), btnAddNeedles, "transition_fab");
                startActivity(intent, options.toBundle());
            }
        });

        displayNeedles();

        return v;
    }

    /**
     * Retrieves the needles from the database and displays them in a recyclerview
     */
    private void displayNeedles() {
        db.collection("knitters").document(userId).collection("needles").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    needlesArrayList.clear();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        Needles needles = documentSnapshot.toObject(Needles.class);
                        needles.setDocumentId(documentSnapshot.getId());
                        needlesArrayList.add(needles);
                        noDataView.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    needlesRecyclerView.setHasFixedSize(true);
                    needlesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    needlesRecyclerView.setAdapter(adapter);
                } else {
                    noDataView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}