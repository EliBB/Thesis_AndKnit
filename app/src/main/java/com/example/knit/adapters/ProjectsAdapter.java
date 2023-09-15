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

import com.bumptech.glide.Glide;
import com.example.knit.R;
import com.example.knit.interfaces.ProjectsListener;
import com.example.knit.classes.Projects;

import java.util.ArrayList;

/**
 * Binds the data for the projects in the Projects screen with the layout
 * of the item_project in a RecyclerView. The adapter displays and provides
 * methods for individual counter items at specified positions.
 */
public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> {
    private final ArrayList<Projects> projectsArrayList;
    private final Context context;
    private final ProjectsListener listener;

    public ProjectsAdapter(ArrayList<Projects> projectsArrayList, Context context, ProjectsListener listener) {
        this.projectsArrayList = projectsArrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_project, parent, false));
    }

    public void onBindViewHolder(@NonNull ProjectsAdapter.ViewHolder viewHolder, int position) {
        Projects projects = projectsArrayList.get(position);
        viewHolder.viewTitle.setText(projects.getProjectName());

        if (!projectsArrayList.isEmpty()) {
            if (projects.getImgUrl() != null) {
                Glide.with(viewHolder.itemView.getContext()).load(projects.getImgUrl()).into(viewHolder.projectImg);
            }
        }
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(projects);
            }
        });
    }

    public int getItemCount() {
        return projectsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView viewTitle;
        CardView cardView;
        ImageView projectImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewTitle = itemView.findViewById(R.id.viewProjectName);
            cardView = itemView.findViewById(R.id.cardView);
            projectImg = itemView.findViewById(R.id.projectImg);
        }
    }
}
