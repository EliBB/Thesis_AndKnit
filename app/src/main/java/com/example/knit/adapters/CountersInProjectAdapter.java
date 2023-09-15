package com.example.knit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knit.R;
import com.example.knit.classes.Counters;
import com.example.knit.interfaces.CountersListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Binds the data for the counters in a project item with the layout
 * of the item_counter_in_project in a RecyclerView. The adapter displays and provides
 * methods for individual counter items at specified positions.
 */

public class CountersInProjectAdapter extends RecyclerView.Adapter<CountersInProjectAdapter.ViewHolder> {
    private static final String TAG = "CountersInProject";
    private final ArrayList<Counters> countersArrayList;
    private final Context context;
    private final CountersListener listener;

    public CountersInProjectAdapter(ArrayList<Counters> countersArrayList, Context context, CountersListener listener) {
        this.countersArrayList = countersArrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CountersInProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CountersInProjectAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_counter_in_project, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CountersInProjectAdapter.ViewHolder holder, int position) {
        Counters counters = countersArrayList.get(position);
        if (counters.getName() != null) {
            holder.viewName.setText(counters.getName());
        }
        holder.viewCount.setText(String.valueOf(counters.getCount()));
        holder.addCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.increment(counters);
            }
        });
        holder.subtractCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.decrement(counters);
            }
        });
        holder.btnUseMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.openMicrophone(counters);
            }
        });
        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.viewName);
                popupMenu.inflate(R.menu.item_counter_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit:

                                holder.editName.setVisibility(View.VISIBLE);
                                holder.viewName.setVisibility(View.GONE);

                                holder.viewMore.setVisibility(View.GONE);
                                holder.viewDone.setVisibility(View.VISIBLE);
                                return true;
                            case R.id.delete:
                                listener.delete(counters);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        holder.viewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.editName.setVisibility(View.GONE);
                holder.viewDone.setVisibility(View.GONE);
                holder.viewName.setVisibility(View.VISIBLE);
                holder.viewMore.setVisibility(View.VISIBLE);

                counters.setName(holder.editName.getText().toString());
                holder.viewName.setText(counters.getName());
                listener.saveName(counters);
            }
        });
    }

    @Override
    public int getItemCount() {
        return countersArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView viewCount, viewName;
        private final EditText editName;
        private final ImageView addCountBtn, subtractCountBtn, viewMore, viewDone;
        private final Button btnUseMic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewCount = itemView.findViewById(R.id.item_counter_in_project_count);
            viewName = itemView.findViewById(R.id.item_counter_in_project_view_name);
            editName = itemView.findViewById(R.id.item_counter_in_project_edit_name);
            addCountBtn = itemView.findViewById(R.id.item_counter_in_project_increment);
            subtractCountBtn = itemView.findViewById(R.id.item_counter_in_project_decrement);
            btnUseMic = itemView.findViewById(R.id.item_counter_in_project_btn_microphone);
            viewMore = itemView.findViewById(R.id.item_counter_in_project_view_more);
            viewDone = itemView.findViewById(R.id.item_counter_in_project_view_done);
        }
    }
}