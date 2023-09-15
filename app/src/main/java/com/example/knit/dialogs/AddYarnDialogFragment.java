package com.example.knit.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.knit.R;
import com.example.knit.activities.AddYarnActivity;
import com.example.knit.adapters.YarnInDialogFragmentAdapter;
import com.example.knit.classes.Yarn;
import com.example.knit.interfaces.YarnListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Opens as a full screen dialog and allows the user to assign yarn from the
 * stash to a new project or new gauge swatch. This fragment implements the
 * YarnListener to invoke methods on individual yarn items in the recyclerview.
 */

public class AddYarnDialogFragment extends DialogFragment implements YarnListener {
    // Objects
    private static final String TAG = "YarnDialogFragment    ";
    private String userId;
    private ArrayList<Yarn> yarnArrayList;
    private ArrayList<String> yarnId;

    // Widgets
    private TextView noDataView, actionbarTitle;
    private Button btnAdd, btnAddYarn;
    private RecyclerView yarnRecyclerView;
    private Toolbar actionbar;
    private ProgressBar progressBar;

    // Adapter
    private YarnInDialogFragmentAdapter adapter;

    // Interfae
    private InputListener inputListener;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public AddYarnDialogFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_add_yarn_dialog, container, false);
        // Initialize widgets
        noDataView = v.findViewById(R.id.view_no_yarn);
        btnAddYarn = v.findViewById(R.id.yarn_dialog_btn_add_new_yarn);
        yarnRecyclerView = v.findViewById(R.id.recyclerView);
        progressBar = v.findViewById(R.id.fragment_add_yarn_progress_bar);

        // Action bar
        actionbar = v.findViewById(R.id.action_bar);
        actionbarTitle = v.findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.add_yarn);
        actionbar.setNavigationIcon(R.drawable.icon_close);
        btnAdd = v.findViewById(R.id.actionbar_add);
        btnAdd.setVisibility(View.VISIBLE);

        // Click listener for closing dialog
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // Click listener for saving chosen yarn. Sends data via the interface InputListener
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!yarnId.isEmpty()) {
                    inputListener.sendYarnInput(yarnId);
                    dismiss();
                }
            }
        });

        // Click listener for starting new AddYarn activity
        btnAddYarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddYarnActivity.class);
                startActivity(intent);
            }
        });

        // Display yarn items
        displayYarn();

        // Initialize arraylist and adapter for the recycler view
        yarnId = new ArrayList<>();
        adapter = new YarnInDialogFragmentAdapter(yarnArrayList, getContext(), this);

        return v;
    }


    /**
     * Displays the user's 'yarn' collection in a recyclerview in alphabetical order.
     * To avoid duplication the list of Yarn objects is cleared before adding a new object.
     * The adapter notifies any changes in case of the user adding a new yarn to the stash.
     * If the user's yarn stash is empty is the text "Your yarn stash is empty" displayed.
     */
    private void displayYarn() {
        yarnArrayList = new ArrayList<>();
        db.collection("knitters").document(userId).collection("yarn").orderBy("name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                yarnArrayList.clear();
                if (!value.isEmpty()) {
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        Yarn yarn = documentSnapshot.toObject(Yarn.class);
                        yarn.setDocumentId(documentSnapshot.getId());
                        yarnArrayList.add(yarn);
                    }
                    noDataView.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    yarnRecyclerView.setHasFixedSize(true);
                    yarnRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    yarnRecyclerView.setAdapter(adapter);
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
         * Sends a list of yarn ID's to the listener.
         */
        void sendYarnInput(ArrayList<String> inputList);
    }

    /**
     * Called when the dialog is attached to the AddProject or AddGaugeSwatch activity.
     * It casts the activity to an instance of the InputListener interface, which handles
     * the input from the user. If the cast is successful, the inputListener variable
     * references the activity. If it fails a ClassCastException is thrown and caught,
     * and an error is logged.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            inputListener = (InputListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    /**
     * Called when a user clicks a yarn item in the recyclerview.
     * If the yarn is not currently selected is the document id from Firebase item stored in
     * an array list, and if the yarn item is selected is the document id removed from the array list.
     */
    @Override
    public void onItemClick(Yarn yarn) {
        if (!yarnId.contains(yarn.getDocumentId())) {
            yarnId.add(yarn.getDocumentId());
        } else {
            yarnId.remove(yarn.getDocumentId());
        }
    }
}