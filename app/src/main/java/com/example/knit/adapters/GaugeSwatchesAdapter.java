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

import com.bumptech.glide.Glide;
import com.example.knit.R;
import com.example.knit.classes.GaugeSwatches;
import com.example.knit.classes.Yarn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
/**
 * Binds the data for the gauge swatches in the Storage screen with the layout
 * of the item_gauge_swatch in a RecyclerView. The adapter displays and provides
 * methods for individual gauge swatch items at specified positions.
 */
public class GaugeSwatchesAdapter extends RecyclerView.Adapter<GaugeSwatchesAdapter.ViewHolder> {
    private static final String TAG = "Gauge swatch adapter";
    private final ArrayList<GaugeSwatches> arrayList;
    private final Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String userId, yarns;
    private Yarn yarn;
    private ArrayList<String> yarnIdsArrayList;
    private ArrayList<Yarn> yarnArrayList;



    public GaugeSwatchesAdapter(ArrayList<GaugeSwatches> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public GaugeSwatchesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gauge_swatch, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GaugeSwatchesAdapter.ViewHolder holder, int position) {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        GaugeSwatches gaugeSwatches = arrayList.get(position);

        if(gaugeSwatches.getImgUrl() != null){
            Glide.with(holder.itemView.getContext()).load(gaugeSwatches.getImgUrl()).into(holder.img);
        }

        holder.sts.setText(gaugeSwatches.getStitches());
        holder.rows.setText(gaugeSwatches.getRows());
        holder.needleSize.setText(gaugeSwatches.getNeedleSize());
        holder.stitchType.setText(gaugeSwatches.getStitchType());
        if(!gaugeSwatches.getNotes().matches("")) {
            holder.notes.setText(gaugeSwatches.getNotes());
            holder.notes.setVisibility(View.VISIBLE);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If cardview is not expanded
                if (holder.expandLayout.getVisibility() == View.VISIBLE || holder.editLayout.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.expandLayout.setVisibility(View.GONE);
                    holder.editLayout.setVisibility(View.GONE);
                    holder.viewLayout.setVisibility(View.VISIBLE);
                    holder.expandImageLayout.setVisibility(View.GONE);
                    holder.img.setVisibility(View.VISIBLE);

                } // Expand view
                else {
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.img.setVisibility(View.GONE);
                    holder.expandImageLayout.setVisibility(View.VISIBLE);
                    if (gaugeSwatches.getImgUrl() != null) {
                        Glide.with(holder.itemView.getContext()).load(gaugeSwatches.getImgUrl()).into(holder.expandImg);
                    }
                    holder.expandLayout.setVisibility(View.VISIBLE);

                    yarnIdsArrayList = new ArrayList<>();
                    yarnArrayList = new ArrayList<>();
                    db.collection("knitters").document(userId).collection("gauge swatches").document(gaugeSwatches.getDocumentId()).collection("yarn").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            yarns = "";
                            StringBuilder strBuilder = new StringBuilder(yarns);

                            for (String yarnId : gaugeSwatches.getYarnIds()) {
                                db.collection("knitters").document(userId).collection("yarn").whereEqualTo("yarnId", yarnId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                                            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                                                yarn = documentSnapshot.toObject(Yarn.class);
                                                String name = yarn.getName() + " " + yarn.getBrand();
                                                strBuilder.append(name);
                                                strBuilder.append("\n");
                                            }
                                            holder.yarn.setText(strBuilder.toString());
                                            holder.yarn.setVisibility(View.VISIBLE);
                                            holder.yarnTitle.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.editSts.setText(gaugeSwatches.getStitches());
                holder.editRows.setText(gaugeSwatches.getRows());
                holder.editNeedleSize.setText(gaugeSwatches.getNeedleSize());
                holder.editStitchType.setText(gaugeSwatches.getStitchType());
                holder.editNotes.setText(gaugeSwatches.getNotes());

                holder.viewLayout.setVisibility(View.GONE);
                holder.editLayout.setVisibility(View.VISIBLE);
                holder.expandLayout.setVisibility(View.GONE);
                holder.expandImageLayout.setVisibility(View.VISIBLE);
            }
        });

        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.viewLayout.setVisibility(View.VISIBLE);
                holder.editLayout.setVisibility(View.GONE);
                holder.expandLayout.setVisibility(View.VISIBLE);

                db.collection("knitters").document(userId).collection("gauge swatches").document(gaugeSwatches.getDocumentId()).update(
                        "needleSize", holder.editNeedleSize.getText().toString(),
                        "notes", holder.editNotes.getText().toString(),
                        "rows", holder.editRows.getText().toString(),
                        "stitchType", holder.editStitchType.getText().toString(),
                        "stitches", holder.editSts.getText().toString());
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setCancelable(true);
                builder.setTitle(R.string.alert_title_delete_gauge);
                builder.setMessage(view.getResources().getString(R.string.alert_message_delete_gauge));

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("knitters").document(userId).collection("gauge swatches").document(gaugeSwatches.getDocumentId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Delete img from Firebase Storage
                                storageReference.child("users").child(userId).child("gaugeSwatches").child(gaugeSwatches.getImgName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Gauge swatch image is deleted successfully");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Gauge swatch image " + e.getMessage());
                                    }
                                });
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
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img, expandImg,btnEdit, btnDelete;
        Button btnSave;
        TextView sts, rows, needleSize, stitchType, notes, yarn, yarnTitle;
        EditText editSts, editRows, editNeedleSize, editStitchType, editNotes;
        LinearLayout viewLayout, expandImageLayout, expandLayout, editLayout;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Image view
            img = itemView.findViewById(R.id.item_gauge_swatch_img);
            expandImg = itemView.findViewById(R.id.item_gauge_expand_image);

            // Text views
            sts = itemView.findViewById(R.id.item_gauge_swatch_view_stitches);
            rows = itemView.findViewById(R.id.item_gauge_swatch_view_rows);
            needleSize = itemView.findViewById(R.id.item_gauge_swatch_view_needle_size);
            stitchType = itemView.findViewById(R.id.item_gauge_swatch_view_stitch_type);
            notes = itemView.findViewById(R.id.item_gauge_swatch_notes);
            yarn = itemView.findViewById(R.id.item_gauge_swatch_yarn);
            yarnTitle = itemView.findViewById(R.id.item_gauge_swatch_yarn_title);

            // Edit texts
            editSts = itemView.findViewById(R.id.item_gauge_edit_sts);
            editRows = itemView.findViewById(R.id.item_gauge_edit_rows);
            editNeedleSize = itemView.findViewById(R.id.item_gauge_edit_needle_size);
            editStitchType = itemView.findViewById(R.id.item_gauge_edit_stitch_type);
            editNotes = itemView.findViewById(R.id.item_gauge_edit_notes);

            // Buttons
            btnEdit = itemView.findViewById(R.id.item_gauge_swatch_btn_edit);
            btnDelete = itemView.findViewById(R.id.item_gauge_swatch_btn_delete);
            btnSave = itemView.findViewById(R.id.item_gauge_btn_save);

            // Layouts
            viewLayout = itemView.findViewById(R.id.item_gauge_layout_view);
            expandLayout = itemView.findViewById(R.id.item_gauge_layout_expand_view);
            editLayout = itemView.findViewById(R.id.item_gauge_edit_layout);
            expandImageLayout = itemView.findViewById(R.id.item_gauge_expand_image_layout);

            // Card view
            cardView = itemView.findViewById(R.id.item_gauge_swatch_card);
        }
    }
}
