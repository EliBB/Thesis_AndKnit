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
import com.example.knit.activities.AddGaugeSwatchActivity;
import com.example.knit.adapters.GaugeSwatchesAdapter;
import com.example.knit.classes.GaugeSwatches;
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
 * Tabsection that displays the gauge swatches from the 'gaugeSwatches' subcollection in a recyclerview
 */
public class StorageGaugeSwatchesFragment extends Fragment {
    // Objects
    private String userId;
    private ArrayList<GaugeSwatches> gaugeSwatchesArrayList;

    // Widgets
    private TextView noDataView;
    private ExtendedFloatingActionButton btnAddGaugeSwatch;
    private RecyclerView gaugeSwatchRecyclerView;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Adapter
    private GaugeSwatchesAdapter adapter;

    public StorageGaugeSwatchesFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_storage_gauge_swatches, container, false);
        // Initialize widgets
        btnAddGaugeSwatch = v.findViewById(R.id.storage_gauge_swatches_btn_add);
        gaugeSwatchRecyclerView = v.findViewById(R.id.recyclerView);
        noDataView = v.findViewById(R.id.view_no_yarn);
        progressBar = v.findViewById(R.id.storage_gauge_swatches_progress_bar);
        gaugeSwatchRecyclerView = v.findViewById(R.id.recyclerView);

        // Arraylist and adapter for the recyclerview
        gaugeSwatchesArrayList = new ArrayList<>();
        adapter = new GaugeSwatchesAdapter(gaugeSwatchesArrayList, getContext());


        // Click listeners for starting the activity of adding a new gauge swatch
        btnAddGaugeSwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddGaugeSwatchActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), btnAddGaugeSwatch, "transition_fab");
                startActivity(intent, options.toBundle());
            }
        });

        displayGaugeSwatches();

        return v;
    }

    /**
     * Retrieves the gauge swatches from the database and displays them in a recyclerview
     */
    private void displayGaugeSwatches() {

        db.collection("knitters").document(userId).collection("gauge swatches").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    gaugeSwatchesArrayList.clear();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        GaugeSwatches gaugeSwatches = documentSnapshot.toObject(GaugeSwatches.class);
                        gaugeSwatches.setDocumentId(documentSnapshot.getId());
                        gaugeSwatchesArrayList.add(gaugeSwatches);
                        noDataView.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    gaugeSwatchRecyclerView.setHasFixedSize(true);
                    gaugeSwatchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    gaugeSwatchRecyclerView.setAdapter(adapter);
                } else {
                    noDataView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}