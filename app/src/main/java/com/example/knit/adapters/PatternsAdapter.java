package com.example.knit.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knit.R;
import com.example.knit.classes.Patterns;
import com.example.knit.interfaces.PatternsListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
/**
 * Binds the data for the patterns in the Storage screen with the layout
 * of the item_pattern in a RecyclerView. The adapter displays and provides
 * methods for individual pattern items at specified positions.
 */
public class PatternsAdapter extends RecyclerView.Adapter<PatternsAdapter.ViewHolder>{
    private static final String TAG = "Pattern adapter item";
    private final ArrayList<Patterns> patternsArrayList;
    private final Context context;
    private final PatternsListener listener;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public PatternsAdapter(ArrayList<Patterns> patternsArrayList, Context context, PatternsListener listener){
        this.patternsArrayList = patternsArrayList;
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public PatternsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pattern, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PatternsAdapter.ViewHolder viewHolder, int position) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Patterns patterns = patternsArrayList.get(position);
        viewHolder.patternName.setText(patterns.getPatternName());
        viewHolder.patternDesigner.setText(patterns.getPatternDesigner());

        viewHolder.patternName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, viewHolder.patternName);
                popupMenu.inflate(R.menu.item_pattern_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setCancelable(true);
                                builder.setTitle(R.string.alert_title_delete_pattern);
                                builder.setMessage(view.getResources().getString(R.string.alert_message_delete_pattern) + " " + patterns.getPatternName() + "?");

                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        db.collection("knitters").document(userId).collection("patterns").document(patterns.getDocumentId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                    storageReference.child("users").child(userId).child("patterns").child(patterns.getPatternName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(patterns);
            }
        });
    }

    @Override
    public int getItemCount() {
        return patternsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pdfIcon;
        TextView patternName, patternDesigner;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfIcon = itemView.findViewById(R.id.pattern_img);
            patternName = itemView.findViewById(R.id.viewPatternName);
            patternDesigner = itemView.findViewById(R.id.viewPatternDesigner);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
