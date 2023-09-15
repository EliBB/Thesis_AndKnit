package com.example.knit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knit.R;
import com.example.knit.classes.Yarn;
import com.example.knit.interfaces.YarnListener;

import java.util.ArrayList;

/**
 * Binds the data for the yarn in the AddYarnDialogFragment with the layout
 * of the item_yarn in a RecyclerView. The adapter displays and provides
 * methods for individual counter items at specified positions.
 */
public class YarnInDialogFragmentAdapter extends RecyclerView.Adapter<YarnAdapter.ViewHolder> {
    private final ArrayList<Yarn> yarnArrayList;
    private final Context context;
    private final YarnListener listener;


    public YarnInDialogFragmentAdapter(ArrayList<Yarn> yarnArrayList, Context context, YarnListener listener) {
        this.yarnArrayList = yarnArrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public YarnAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new YarnAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_yarn, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull YarnAdapter.ViewHolder viewHolder, int position) {
        Yarn yarn = yarnArrayList.get(position);
        viewHolder.viewName.setText(yarn.getName());
        viewHolder.viewBrand.setText(yarn.getBrand());
        viewHolder.viewColor.setText(yarn.getColor());

        yarn.setClicked(false);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(yarn);
                if (yarn.getClicked()) {
                    viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.secondary_color_50));
                    yarn.setClicked(false);

                    // If patterns is not clicked - change to darker background color
                } else {
                    viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.primary_color_400));
                    yarn.setClicked(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return yarnArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView yarnImg;
        TextView viewName, viewBrand, viewColor;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            yarnImg = itemView.findViewById(R.id.item_yarn_image);
            viewName = itemView.findViewById(R.id.item_yarn_name);
            viewBrand = itemView.findViewById(R.id.item_yarn_brand);
            viewColor = itemView.findViewById(R.id.item_yarn_color);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}

