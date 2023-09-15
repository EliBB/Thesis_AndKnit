package com.example.knit.fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knit.R;
import com.example.knit.activities.VoiceCommandActivity;
import com.example.knit.adapters.CountersAdapter;
import com.example.knit.classes.Counters;
import com.example.knit.interfaces.CountersListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * Displays counters in a recycler view. Implements the CountersListener.
 */
public class ToolsCountersFragment extends Fragment implements CountersListener {
    // Objects
    private String userId;
    private ArrayList<Counters> countersArrayList;

    // Widgets
    private TextView noDataView, actionbarTitle;
    private FloatingActionButton btnAddCounter;
    private RecyclerView addCounterRecyclerView;
    private Toolbar actionbar;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Adapter
    private CountersAdapter adapter;

    public ToolsCountersFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_tools_counters, container, false);
        // Initialize widgets
        btnAddCounter = v.findViewById(R.id.tools_counter_btn_add_counter);
        noDataView = v.findViewById(R.id.view_no_yarn);
        addCounterRecyclerView = v.findViewById(R.id.tools_counters_recycler_view);

        // Action bar
        actionbar = v.findViewById(R.id.action_bar);
        actionbarTitle = v.findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.counter);
        actionbar.setNavigationIcon(R.drawable.icon_arrow_back);

        // Arraylist and adapter for recyclerview
        countersArrayList = new ArrayList<>();
        adapter = new CountersAdapter(countersArrayList, getContext(), this);

        displayCounters();


        //Swipe functionality for each counter item
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Counters deleteCounter = countersArrayList.get(viewHolder.getAdapterPosition());
                int position = viewHolder.getAdapterPosition();
                countersArrayList.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                // Delete from database
                db.collection("knitters").document(userId).collection("counters").document(deleteCounter.getDocumentId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //
                        }
                    }
                });
                // Undo swipe
                Snackbar.make(addCounterRecyclerView, deleteCounter.getName() + " is deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        countersArrayList.add(position, deleteCounter);
                        adapter.notifyItemInserted(position);
                        db.collection("knitters").document(userId).collection("counters").add(deleteCounter).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).show();
            }

            // Layout for the swipe
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.error))
                        .addSwipeLeftActionIcon(R.drawable.icon_delete)
                        .addSwipeLeftCornerRadius(0, 20)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(addCounterRecyclerView);

        // Click listener for navigating back to the previous screen
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        // Click listener for adding a new counter
        btnAddCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCounter();
            }
        });

        return v;
    }

    /**
     * Displays the counter item from the database in a recyclerview.
     */
    private void displayCounters() {
        db.collection("knitters").document(userId).collection("counters").orderBy("added", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    countersArrayList.clear();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        Counters counters = documentSnapshot.toObject(Counters.class);
                        countersArrayList.add(counters);
                        noDataView.setVisibility(View.INVISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                    addCounterRecyclerView.setHasFixedSize(true);
                    addCounterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    addCounterRecyclerView.setAdapter(adapter);
                } else {
                    noDataView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Adds new counter to the 'counters' subcollection.
     */
    private void addCounter() {
        Map<String, Object> counters = new HashMap<>();
        counters.put("name", null);
        counters.put("count", 0);
        counters.put("added", System.currentTimeMillis());

        db.collection("knitters").document(userId).collection("counters").add(counters).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Starts and sends ID to the VoiceCommand activity.
     */
    @Override
    public void openMicrophone(Counters counters) {
        Intent intent = new Intent(getActivity(), VoiceCommandActivity.class);
        intent.putExtra("documentId", counters.getDocumentId());
        startActivity(intent);
    }

    /**
     * Increments the count in the database
     */
    @Override
    public void increment(Counters counters) {
        int currentCount = counters.getCount();
        currentCount++;

        db.collection("knitters").document(userId).collection("counters").document(counters.getDocumentId()).update("count", currentCount);
    }

    /**
     * Decrements the count in the database
     */
    @Override
    public void decrement(Counters counters) {
        int currentCount = counters.getCount();
        if (currentCount != 0) {
            currentCount--;
        }
        db.collection("knitters").document(userId).collection("counters").document(counters.getDocumentId()).update("count", currentCount);
    }

    /**
     * Save the name of the counter in the database
     */
    @Override
    public void saveName(Counters counters) {
        db.collection("knitters").document(userId).collection("counters").document(counters.getDocumentId()).update("name", counters.getName());
    }

    @Override
    public void delete(Counters counters) {

    }
}