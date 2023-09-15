package com.example.knit.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knit.R;
import com.example.knit.classes.Patterns;
import com.example.knit.interfaces.PatternsListener;

import java.util.ArrayList;
/**
 * Binds the data for the patterns in the AddPatternDialogFragment with the layout
 * of the item_pattern in a RecyclerView. The adapter displays and provides
 * methods for individual pattern items at specified positions.
 */
public class PatternInProjectAdapter extends RecyclerView.Adapter<PatternsAdapter.ViewHolder> {
    private static final String TAG = "Pattern adapter item";
    private final ArrayList<Patterns> patternsArrayList;
    private final Context context;
    private final PatternsListener listener;

    public PatternInProjectAdapter(ArrayList<Patterns> patternsArrayList, Context context, PatternsListener listener){
        this.patternsArrayList = patternsArrayList;
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public PatternsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PatternsAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pattern, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PatternsAdapter.ViewHolder viewHolder, int position) {
        Patterns patterns = patternsArrayList.get(position);
        viewHolder.patternName.setText(patterns.getPatternName());
        viewHolder.patternDesigner.setText(patterns.getPatternDesigner());

        patterns.setIsClicked(false);
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

