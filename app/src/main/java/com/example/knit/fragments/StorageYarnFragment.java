package com.example.knit.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knit.MainActivity;
import com.example.knit.R;
import com.example.knit.activities.AddProjectActivity;
import com.example.knit.activities.AddYarnActivity;
import com.example.knit.adapters.YarnAdapter;
import com.example.knit.classes.Yarn;
import com.example.knit.interfaces.YarnListener;
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
 * Tabsection that displays the yarn from the 'yarn' subcollection in a recyclerview.
 */
public class StorageYarnFragment extends Fragment {
    // Objects
    private static final String TAG = "Yarn adapter";
    private String userId, documentId;
    private ArrayList<Yarn> yarnArrayList;

    // Widgets
    private TextView noDataView;
    private ProgressBar progressBar;
    private ExtendedFloatingActionButton addYarn;
    private RecyclerView yarnRecyclerView;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Adapter
    private YarnAdapter adapter;

    public StorageYarnFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_storage_yarn, container, false);
        // Initialize widgets
        noDataView = v.findViewById(R.id.view_no_yarn);
        addYarn = v.findViewById(R.id.extended_fab);
        progressBar = v.findViewById(R.id.storage_yarn_progress_bar);
        yarnRecyclerView = v.findViewById(R.id.recyclerViewYarn);

        // Arraylist and adapter for the recyclerview
        yarnArrayList = new ArrayList<>();
        adapter = new YarnAdapter(yarnArrayList, getContext());

        // Click listeners for starting the activity of adding new yarn
        addYarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddYarnActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), addYarn, "transition_fab");
                startActivity(intent, options.toBundle());
            }
        });

        displayYarn();

        return v;
    }

    /**
     * Retrieves the yarn from the database and displays them in a recyclerview
     */
    private void displayYarn(){
        db.collection("knitters").document(userId).collection("yarn").orderBy("name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    yarnArrayList.clear();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for(DocumentSnapshot documentSnapshot : documentSnapshotList){
                        Yarn yarn = documentSnapshot.toObject(Yarn.class);
                        yarn.setDocumentId(documentSnapshot.getId());
                        yarnArrayList.add(yarn);
                        noDataView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    yarnRecyclerView.setHasFixedSize(true);
                    yarnRecyclerView.setLayoutManager(new LinearLayoutManager (getContext()));
                    yarnRecyclerView.setAdapter(adapter);
                } else{
                    noDataView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}