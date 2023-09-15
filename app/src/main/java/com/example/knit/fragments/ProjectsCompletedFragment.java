package com.example.knit.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.knit.R;
import com.example.knit.adapters.ProjectsAdapter;
import com.example.knit.classes.Projects;
import com.example.knit.interfaces.ProjectsListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
/**
 * Displays the completed projects in a recycler view in a grid layout. Implements the ProjectsListener
 * interface.
 */
public class ProjectsCompletedFragment extends Fragment implements ProjectsListener {
    // Object
    private String userId;
    private ArrayList<Projects> arrayList;

    //Widgets
    private TextView noDataView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Adapter
    private ProjectsAdapter adapter;

    public ProjectsCompletedFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_projects_completed, container, false);
        // Initialize widgets
        noDataView = v.findViewById(R.id.view_no_yarn);
        progressBar = v.findViewById(R.id.projects_completed_progress_bar);
        recyclerView = v.findViewById(R.id.projects_completed_recycler_view);

        // Adapter and arraylist for recyclerview
        arrayList = new ArrayList<>();
        adapter = new ProjectsAdapter(arrayList, getContext(), this);

        displayCompletedProjects();

        return v;
    }

    /**
     * Retrieves projects that are completed from the 'projects' subcollection in a recyclerview
     */
    private void displayCompletedProjects() {
        db.collection("knitters").document(userId).collection("projects").whereEqualTo("projectType", "2").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    arrayList.clear();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        Projects projects = documentSnapshot.toObject(Projects.class);
                        projects.setDocumentId(documentSnapshot.getId());
                        arrayList.add(projects);
                        noDataView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    recyclerView.setAdapter(adapter);
                } else {
                    noDataView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Listens to user clicks for each project item. When clicked the ID of the project item
     * is send to the ProjectItemFragment.
     */
    @Override
    public void onItemClick(Projects projects) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
        Bundle mBundle = new Bundle();
        mBundle.putString("documentId", projects.getDocumentId());
        ProjectItemFragment fragment = new ProjectItemFragment();
        fragment.setArguments(mBundle);
        appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

    }
}