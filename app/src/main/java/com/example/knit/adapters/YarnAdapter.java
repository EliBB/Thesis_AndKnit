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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knit.R;
import com.example.knit.classes.Yarn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
/**
 * Binds the data for the yarn in the Storage screen with the layout
 * of the item_yarn in a RecyclerView. The adapter displays and provides
 * methods for individual yarn items at specified positions.
 */
public class YarnAdapter extends RecyclerView.Adapter<YarnAdapter.ViewHolder> {
    private static final String TAG = "Yarn adapter item";
    private final ArrayList<Yarn> yarnArrayList;
    private final Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    public YarnAdapter(ArrayList<Yarn> yarnArrayList, Context context) {
        this.yarnArrayList = yarnArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public YarnAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_yarn, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull YarnAdapter.ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        Yarn yarn = yarnArrayList.get(position);

        holder.viewName.setText(yarn.getName());
        holder.viewBrand.setText(yarn.getBrand());
        holder.viewColor.setText(yarn.getColor());
        holder.viewBrand.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icon_expand_more, 0);


        if (!yarn.getLot().isEmpty()) {
            holder.viewLot.setText(yarn.getLot());
        }
        holder.viewSkeins.setText(String.valueOf(yarn.getAmountSkeins()));
        holder.viewAmountGrams.setText(String.valueOf(yarn.getAmountGrams()));
        holder.viewAmountMeters.setText(String.valueOf(yarn.getAmountMeters()));


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If cardview is not expanded
                if (holder.expandLayout.getVisibility() == View.VISIBLE || holder.editLayout.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.expandLayout.setVisibility(View.GONE);
                    holder.editLayout.setVisibility(View.GONE);
                    holder.viewLayout.setVisibility(View.VISIBLE);
                    holder.viewBrand.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icon_expand_more, 0);

                } else {
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.expandLayout.setVisibility(View.VISIBLE);
                    holder.viewSkeinWeight.setText(yarn.getSkeinWeight());
                    holder.viewSkeinLength.setText(String.valueOf(yarn.getSkeinLength()));
                    if (!yarn.getNotes().matches("")) {
                        holder.viewNotes.setText(yarn.getNotes());
                        holder.viewNotes.setVisibility(View.VISIBLE);
                    }
                    holder.viewBrand.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.icon_expand_less, 0);

                }
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.viewImage.setVisibility(View.GONE);
                holder.editName.setText(yarn.getName());
                holder.editBrand.setText(yarn.getBrand());
                holder.editColor.setText(yarn.getColor());
                holder.editLot.setText(yarn.getLot());
                holder.editSkeins.setText(String.valueOf(yarn.getAmountSkeins()));
                holder.editAmountGrams.setText(String.valueOf(yarn.getAmountGrams()));
                holder.editAmountMeters.setText(String.valueOf(yarn.getAmountMeters()));
                holder.editSkeinWeight.setText(yarn.getSkeinWeight());
                holder.editSkeinLength.setText(String.valueOf(yarn.getSkeinLength()));
                holder.editNotes.setText(yarn.getNotes());

                holder.viewLayout.setVisibility(View.GONE);
                holder.editLayout.setVisibility(View.VISIBLE);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setCancelable(true);
                builder.setTitle(R.string.alert_title_delete_yarn);
                builder.setMessage(view.getResources().getString(R.string.alert_message_delete_yarn) + " " + yarn.getName() + "?");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("knitters").document(userId).collection("yarn").document(yarn.getDocumentId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "Yarn deleted successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.getMessage());
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

        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.viewLayout.setVisibility(View.VISIBLE);
                holder.expandLayout.setVisibility(View.VISIBLE);
                holder.viewImage.setVisibility(View.VISIBLE);
                holder.editLayout.setVisibility(View.GONE);

                db.collection("knitters").document(userId).collection("yarn").document(yarn.getDocumentId()).update(
                        "name", holder.editName.getText().toString(),
                        "brand", holder.editBrand.getText().toString(),
                        "color", holder.editColor.getText().toString(),
                        "lot", holder.editLot.getText().toString(),
                        "amountSkeins", Integer.valueOf(holder.editSkeins.getText().toString()),
                        "amountGrams", Integer.valueOf(holder.editAmountGrams.getText().toString()),
                        "amountMeters", Integer.valueOf(holder.editAmountMeters.getText().toString()),
                        "notes", holder.editNotes.getText().toString(),
                        "skeinLength", Integer.valueOf(holder.editSkeinLength.getText().toString()),
                        "skeinWeight", holder.editSkeinWeight.getText().toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return yarnArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView viewName, viewBrand, viewColor, viewLot, viewSkeins, viewAmountGrams, viewAmountMeters, viewSkeinWeight, viewSkeinLength, viewNotes;
        EditText editName, editBrand, editColor, editLot, editSkeins, editAmountGrams, editAmountMeters, editSkeinWeight, editSkeinLength, editNotes;
        ImageView btnEdit, btnDelete, viewImage;
        Button btnSave;
        LinearLayout expandLayout, editLayout, viewLayout;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Text Views
            viewName = itemView.findViewById(R.id.item_yarn_name);
            viewBrand = itemView.findViewById(R.id.item_yarn_brand);
            viewColor = itemView.findViewById(R.id.item_yarn_color);
            viewLot = itemView.findViewById(R.id.item_yarn_lot);
            viewSkeins = itemView.findViewById(R.id.item_yarn_skeins);
            viewAmountGrams = itemView.findViewById(R.id.item_yarn_grams);
            viewAmountMeters = itemView.findViewById(R.id.item_yarn_meters);
            viewSkeinWeight = itemView.findViewById(R.id.item_yarn_skein_weight);
            viewSkeinLength = itemView.findViewById(R.id.item_yarn_skein_length);
            viewNotes = itemView.findViewById(R.id.item_yarn_notes);

            // Edit texts
            editName = itemView.findViewById(R.id.item_yarn_edit_name);
            editBrand = itemView.findViewById(R.id.item_yarn_edit_brand);
            editColor = itemView.findViewById(R.id.item_yarn_edit_color);
            editLot = itemView.findViewById(R.id.item_yarn_edit_lot);
            editSkeins = itemView.findViewById(R.id.item_yarn_edit_skeins);
            editAmountGrams = itemView.findViewById(R.id.item_yarn_edit_grams);
            editAmountMeters = itemView.findViewById(R.id.item_yarn_edit_meters);
            editSkeinWeight = itemView.findViewById(R.id.item_yarn_edit_skein_weight);
            editSkeinLength = itemView.findViewById(R.id.item_yarn_edit_skein_length);
            editNotes = itemView.findViewById(R.id.item_yarn_edit_notes);

            // Buttons
            btnDelete = itemView.findViewById(R.id.item_yarn_btn_delete);
            btnEdit = itemView.findViewById(R.id.item_yarn_btn_edit);
            btnSave = itemView.findViewById(R.id.item_yarn_btn_save);

            // Layouts
            expandLayout = itemView.findViewById(R.id.item_yarn_layout_expand);
            editLayout = itemView.findViewById(R.id.item_yarn_layout_edit);
            viewLayout = itemView.findViewById(R.id.item_yarn_layout_view);

            // Card view
            cardView = itemView.findViewById(R.id.item_yarn_card);

            // Default image
            viewImage = itemView.findViewById(R.id.item_yarn_image);
        }
    }

}
