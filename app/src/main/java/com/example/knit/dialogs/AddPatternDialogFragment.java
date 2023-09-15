package com.example.knit.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knit.R;
import com.example.knit.activities.AddPatternActivity;
import com.example.knit.adapters.PatternInProjectAdapter;
import com.example.knit.classes.Patterns;
import com.example.knit.interfaces.PatternsListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
/**
 * Opens as a full screen dialog and allows the user to assign a pattern from the
 * stash to a new project. This fragment implements the
 * PatternsListener to invoke methods on individual yarn items in the recyclerview.
 */
public class AddPatternDialogFragment extends DialogFragment implements PatternsListener {
    // Objects
    private static final String TAG = "DialogFragment";
    private String userId, patternId;
    private ArrayList<Patterns> patternsArrayList;

    // Widgets
    private TextView noDataView, actionbarTitle;
    private Button btnAddPattern;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Toolbar actionbar;

    // Adapter
    private PatternInProjectAdapter adapter;

    // Interface
    private InputListener inputListener;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public AddPatternDialogFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_add_pattern_dialog, container, false);
        // Initialize widgets
        btnAddPattern = v.findViewById(R.id.pattern_dialog_btn_add_new_pattern);
        noDataView = v.findViewById(R.id.view_no_yarn);
        progressBar = v.findViewById(R.id.fragment_add_pattern_progress_bar);
        recyclerView = v.findViewById(R.id.recyclerView);

        // Action bar
        actionbar = v.findViewById(R.id.action_bar);
        actionbarTitle = v.findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.add_pattern);
        actionbar.setNavigationIcon(R.drawable.icon_close);


        displayPatterns();

        // Initialize arraylist and adapter for the recycler view
        patternsArrayList = new ArrayList<>();
        adapter = new PatternInProjectAdapter(patternsArrayList, getContext(), this);

        // Click listener for closing dialog
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // Click listener for starting new AddPattern activity
        btnAddPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddPatternActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    /**
     * Displays the user's 'pattern' collection in a recyclerview in alphabetical order.
     * To avoid duplication the list of is cleared before adding a new item.
     * The adapter notifies any changes in case of the user adding a new pattern to the stash.
     * If the user's pattern stash is empty is the text "Your pattern stash is empty" displayed.
     */
    private void displayPatterns(){
        db.collection("knitters").document(userId).collection("patterns").orderBy("patternName").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                patternsArrayList.clear();
                if(!value.isEmpty()){
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for(DocumentSnapshot documentSnapshot : documentSnapshotList){
                        Patterns pattern = documentSnapshot.toObject(Patterns.class);
                        pattern.setDocumentId(documentSnapshot.getId());
                        patternsArrayList.add(pattern);
                    }
                    noDataView.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                } else {
                    noDataView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Handles the input from the user which is send to the activity.
     */
    public interface InputListener {
        /**
         * Sends a string that holds the pattern ID to the listener.
         */
        void sendPatternInput(String input);
    }

    /**
     * Called when the dialog is attached to the AddProject activity.
     * It casts the activity to an instance of the InputListener interface, which handles
     * the input from the user. If the cast is successful, the inputListener variable
     * references the activity. If it fails a ClassCastException is thrown and caught,
     * and an error is logged.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            inputListener = (AddPatternDialogFragment.InputListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    /**
     * Called when a user clicks a pattern item in the recyclerview.
     */
    @Override
    public void onItemClick(Patterns patterns) {
        patternId = patterns.getDocumentId();
        inputListener.sendPatternInput(patternId);
        dismiss();
    }
}