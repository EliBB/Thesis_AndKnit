package com.example.knit.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knit.R;
import com.example.knit.classes.Needles;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
/**
 * Binds the data for the needles in the Storage screen with the layout
 * of the item_needles in a RecyclerView. The adapter displays and provides
 * methods for individual needle items at specified positions.
 */
public class NeedlesAdapter extends RecyclerView.Adapter<NeedlesAdapter.ViewHolder> {
    private final ArrayList<Needles> needlesArrayList;
    private final Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    public NeedlesAdapter(ArrayList<Needles> needlesArrayList, Context context) {
        this.needlesArrayList = needlesArrayList;
        this.context = context;
    }
    @NonNull
    @Override
    public NeedlesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_needles, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NeedlesAdapter.ViewHolder viewHolder, int position) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        Needles needles = needlesArrayList.get(position);
        if(needles.getType() != null){
            viewHolder.needleType.setText(needles.getType());
        }
        if(needles.getSizes() !=null) {
            viewHolder.needleSizes.setText(needles.getSizes());
            viewHolder.sizesRow.setVisibility(View.VISIBLE);
            viewHolder.editNeedleSizes.setText(needles.getSizes());
        }
        if(needles.getNeedleLen() !=null) {
            viewHolder.needleLengths.setText(needles.getNeedleLen());
            viewHolder.needleLengthRow.setVisibility(View.VISIBLE);
            viewHolder.editNeedleLength.setText(needles.getNeedleLen());
        }
        if(needles.getWireLen() != null){
            viewHolder.wireLengths.setText(needles.getWireLen());
            viewHolder.wireRow.setVisibility(View.VISIBLE);
            viewHolder.editWireLengths.setText(needles.getWireLen());
        }
        if(needles.getMaterial() != null){
            viewHolder.material.setText(needles.getMaterial());
            viewHolder.materialRow.setVisibility(View.VISIBLE);
            viewHolder.editMaterial.setText(needles.getMaterial());
        }
        if(!needles.getNotes().matches("")){
            viewHolder.notes.setText(needles.getNotes());
            viewHolder.notesRow.setVisibility(View.VISIBLE);
            viewHolder.editNotes.setText(needles.getNotes());
        }

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If cardview is not expanded
                if (viewHolder.expandView.getVisibility() == View.VISIBLE || viewHolder.editLayout.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(viewHolder.cardView, new AutoTransition());
                    viewHolder.expandView.setVisibility(View.GONE);
                    viewHolder.editLayout.setVisibility(View.GONE);
                    viewHolder.viewLayout.setVisibility(View.VISIBLE);
                } else {
                    TransitionManager.beginDelayedTransition(viewHolder.cardView, new AutoTransition());
                    viewHolder.expandView.setVisibility(View.VISIBLE);
                }
            }
        });

        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.viewLayout.setVisibility(View.GONE);
                viewHolder.editLayout.setVisibility(View.VISIBLE);
            }
        });

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setCancelable(true);
                builder.setTitle(R.string.alert_title_delete_needles);
                builder.setMessage(view.getResources().getString(R.string.alert_message_delete_needles));

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("knitters").document(userId).collection("needles").document(needles.getDocumentId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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

        viewHolder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.viewLayout.setVisibility(View.VISIBLE);
                viewHolder.expandView.setVisibility(View.VISIBLE);
                viewHolder.editLayout.setVisibility(View.GONE);

                db.collection("knitters").document(userId).collection("needles").document(needles.getDocumentId()).update(
                        "material", viewHolder.editMaterial.getText().toString(),
                        "needleLen", viewHolder.editNeedleLength.getText().toString(),
                        "notes", viewHolder.editNotes.getText().toString(),
                        "sizes", viewHolder.editNeedleSizes.getText().toString(),
                        "wireLen", viewHolder.editWireLengths.getText().toString());
            }
        });
    }
    @Override
    public int getItemCount() {
        return needlesArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView needleType, needleSizes, needleLengths, wireLengths, material, notes;
        EditText editNeedleSizes, editNeedleLength, editWireLengths, editMaterial, editNotes;
        TableRow sizesRow, needleLengthRow, wireRow, materialRow, notesRow;
        ImageView btnEdit, btnDelete;
        Button btnSave;
        LinearLayout expandView, editLayout, viewLayout;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Text views
            needleType = itemView.findViewById(R.id.item_needles_type);
            needleSizes = itemView.findViewById(R.id.item_needles_size);
            needleLengths = itemView.findViewById(R.id.item_needles_lengths);
            wireLengths = itemView.findViewById(R.id.item_needles_wire_lengths);
            material = itemView.findViewById(R.id.item_needles_material);
            notes = itemView.findViewById(R.id.item_needles_notes);

            // Edit texts
            editNeedleSizes = itemView.findViewById(R.id.item_needles_edit_needle_sizes);
            editNeedleLength = itemView.findViewById(R.id.item_needles_edit_needle_length);
            editMaterial = itemView.findViewById(R.id.item_needles_edit_material);
            editWireLengths = itemView.findViewById(R.id.item_needles_edit_wire_length);
            editNotes = itemView.findViewById(R.id.item_needles_edit_notes);

            // Tablerows
            sizesRow = itemView.findViewById(R.id.sizes_row);
            needleLengthRow = itemView.findViewById(R.id.needle_lengths_row);
            wireRow = itemView.findViewById(R.id.wire_row);
            materialRow = itemView.findViewById(R.id.material_row);
            notesRow = itemView.findViewById(R.id.notes_row);

            // Linear layouts
            expandView = itemView.findViewById(R.id.item_needles_expand_view);
            viewLayout = itemView.findViewById(R.id.item_needles_view_layout);
            editLayout = itemView.findViewById(R.id.item_needles_edit_layout);

            // Buttons
            btnDelete = itemView.findViewById(R.id.item_needles_btn_delete);
            btnEdit = itemView.findViewById(R.id.item_needles_btn_edit);
            btnSave = itemView.findViewById(R.id.item_needles_btn_save);

            // Cardview
            cardView = itemView.findViewById(R.id.item_needles_card);
        }
    }
}
