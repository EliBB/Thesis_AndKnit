package com.example.knit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knit.R;
import com.example.knit.classes.Yarn;
import com.example.knit.interfaces.YarnListener;

import java.util.ArrayList;

/**
 * Binds the data for the yarn items in a specific project with the layout
 * of the item_yarn_in_project in a RecyclerView. The adapter displays individual
 * yarn items at specified positions.
 */
public class YarnInProjectAdapter extends RecyclerView.Adapter<YarnInProjectAdapter.ViewHolder> {
    private static final String TAG = "Yarn in project adapter item";
    private final ArrayList<Yarn> yarnArrayList;
    private final Context context;

    public YarnInProjectAdapter(ArrayList<Yarn> yarnArrayList, Context context) {
        this.yarnArrayList = yarnArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public YarnInProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new YarnInProjectAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_yarn_in_project, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull YarnInProjectAdapter.ViewHolder holder, int position) {
        Yarn yarn = yarnArrayList.get(position);

        holder.viewName.setText(yarn.getName());
        holder.viewBrand.setText(yarn.getBrand());
        holder.viewColor.setText(yarn.getColor());
    }

    @Override
    public int getItemCount() {
        return yarnArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView viewName, viewBrand, viewColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewName = itemView.findViewById(R.id.item_yarn_in_project_name);
            viewBrand = itemView.findViewById(R.id.item_yarn_in_project_brand);
            viewColor = itemView.findViewById(R.id.item_yarn_in_project_color);
        }
    }
}
