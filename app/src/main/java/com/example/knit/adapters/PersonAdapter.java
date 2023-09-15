package com.example.knit.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knit.R;
import com.example.knit.classes.Persons;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
/**
 * Binds the data for the persons in the Profile screen with the layout
 * of the item_person in a RecyclerView. The adapter displays and provides
 * methods for individual counter items at specified positions.
 */
public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {
    private static final String TAG = "Person item:  ";
    private final ArrayList<Persons> personsArrayList;
    private final Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    public PersonAdapter(ArrayList<Persons> personsArrayList, Context context) {
        this.personsArrayList = personsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PersonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_person, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PersonAdapter.ViewHolder viewHolder, int position) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        Persons person = personsArrayList.get(position);

        viewHolder.viewName.setText(person.getName());
        viewHolder.viewBust.setText(person.getBust());
        viewHolder.viewWaist.setText(person.getWaist());
        viewHolder.viewHips.setText(person.getWaist());
        viewHolder.viewHandCir.setText(person.getHandCir());
        viewHolder.viewHandLen.setText(person.getHandLen());
        viewHolder.viewFootCir.setText(person.getFootCir());
        viewHolder.viewFootLen.setText(person.getFootLen());
        viewHolder.viewNotes.setText(person.getNotes());

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If cardview is not expanded
                if (viewHolder.layoutView.getVisibility() == View.VISIBLE || viewHolder.layoutEdit.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(viewHolder.cardView, new AutoTransition());
                    viewHolder.layoutView.setVisibility(View.GONE);
                    viewHolder.layoutEdit.setVisibility(View.GONE);
                    viewHolder.viewName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icon_expand_more, 0);
                } else {
                    TransitionManager.beginDelayedTransition(viewHolder.cardView, new AutoTransition());
                    viewHolder.layoutView.setVisibility(View.VISIBLE);
                    viewHolder.btnEdit.setVisibility(View.VISIBLE);
                    viewHolder.viewName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icon_expand_less, 0);
                }
            }
        });

        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.layoutView.setVisibility(View.GONE);
                viewHolder.layoutEdit.setVisibility(View.VISIBLE);

                viewHolder.editName.setText(person.getName());
                viewHolder.editBust.setText(person.getBust());
                viewHolder.editWaist.setText(person.getWaist());
                viewHolder.editHips.setText(person.getWaist());
                viewHolder.editHandCir.setText(person.getHandCir());
                viewHolder.editHandLen.setText(person.getHandLen());
                viewHolder.editFootCir.setText(person.getFootCir());
                viewHolder.editFootLen.setText(person.getFootLen());
                viewHolder.editNotes.setText(person.getNotes());
            }
        });

        viewHolder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.btnEdit.setVisibility(View.VISIBLE);
                viewHolder.layoutView.setVisibility(View.VISIBLE);
                viewHolder.btnSave.setVisibility(View.GONE);
                viewHolder.layoutEdit.setVisibility(View.GONE);

                db.collection("knitters").document(userId).collection("persons").document(person.getDocumentId()).update(
                        "name", viewHolder.editName.getText().toString(),
                        "bust", viewHolder.editBust.getText().toString(),
                        "waist", viewHolder.editWaist.getText().toString(),
                        "hips", viewHolder.editHips.getText().toString(),
                        "handCir", viewHolder.editHandCir.getText().toString(),
                        "handLen", viewHolder.editHandLen.getText().toString(),
                        "footCir", viewHolder.editFootCir.getText().toString(),
                        "footLen", viewHolder.editFootLen.getText().toString(),
                        "notes", viewHolder.editNotes.getText().toString());
            }
        });

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setCancelable(true);
                builder.setTitle(R.string.alert_title_delete_person);
                builder.setMessage(view.getResources().getString(R.string.alert_message_delete_person) + " " + person.getName());
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("knitters").document(userId).collection("persons").document(person.getDocumentId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "Person deleted successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Error    " + e.getMessage());
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
        });
    }

    @Override
    public int getItemCount() {
        return personsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView viewName, viewBust, viewWaist, viewHips, viewHandCir, viewHandLen, viewFootCir, viewFootLen, viewNotes;
        EditText editName, editBust, editWaist, editHips, editHandCir, editHandLen, editFootCir, editFootLen, editNotes;
        ImageView btnEdit, btnDelete;
        Button btnSave;
        LinearLayout layoutView, layoutEdit;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Layouts
            layoutView = itemView.findViewById(R.id.item_person_layout_view);
            layoutEdit = itemView.findViewById(R.id.item_person_layout_edit);

            // Text fields
            viewName = itemView.findViewById(R.id.item_person_view_name);
            viewBust = itemView.findViewById(R.id.item_person_view_bust);
            viewWaist = itemView.findViewById(R.id.item_person_view_waist);
            viewHips = itemView.findViewById(R.id.item_person_view_hips);
            viewHandCir = itemView.findViewById(R.id.item_person_view_hand_cir);
            viewHandLen = itemView.findViewById(R.id.item_person_view_hand_len);
            viewFootCir = itemView.findViewById(R.id.item_person_view_foot_cir);
            viewFootLen = itemView.findViewById(R.id.item_person_view_foot_len);
            viewNotes = itemView.findViewById(R.id.item_person_view_notes);

            // Edit texts
            editName = itemView.findViewById(R.id.item_person_edit_name);
            editBust = itemView.findViewById(R.id.item_person_edit_bust);
            editWaist = itemView.findViewById(R.id.item_person_edit_waist);
            editHips = itemView.findViewById(R.id.item_person_edit_hips);
            editHandCir = itemView.findViewById(R.id.item_person_edit_hand_cir);
            editHandLen = itemView.findViewById(R.id.item_person_edit_hand_len);
            editFootCir = itemView.findViewById(R.id.item_person_edit_foot_cir);
            editFootLen = itemView.findViewById(R.id.item_person_edit_foot_len);
            editNotes = itemView.findViewById(R.id.item_person_edit_notes);

            // Buttons
            btnEdit = itemView.findViewById(R.id.item_person_btn_edit);
            btnDelete = itemView.findViewById(R.id.item_person_btn_delete);
            btnSave = itemView.findViewById(R.id.item_person_btn_save);

            // Card item
            cardView = itemView.findViewById(R.id.item_person_card);

        }
    }

}
