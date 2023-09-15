package com.example.knit.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knit.R;
import com.example.knit.classes.Yarn;

import java.util.ArrayList;

/**
 * Binds the data for the yarn items in a specific project with the layout
 * of the item_yarn_move in a RecyclerView in the UpdateYarnAfterCompletionDialogFragment.
 * The adapter displays and provides methods for individual counter items at specified
 * positions.
 */
public class YarnMoveAdapter extends RecyclerView.Adapter<YarnMoveAdapter.ViewHolder> {
    private static final String TAG = "Move yarn adapter item";
    private final ArrayList<Yarn> yarnArrayList;
    private final Context context;

    public YarnMoveAdapter(ArrayList<Yarn> yarnArrayList, Context context) {
        this.yarnArrayList = yarnArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public YarnMoveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new YarnMoveAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_yarn_move, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull YarnMoveAdapter.ViewHolder viewHolder, int position) {
        Yarn yarn = yarnArrayList.get(position);
        viewHolder.name.setText(yarn.getName());
        viewHolder.brand.setText(yarn.getBrand());
        viewHolder.color.setText(yarn.getColor());

        viewHolder.editGrams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String yarnStr = viewHolder.editGrams.getText().toString();

                if (!yarnStr.matches("")) {
                    Integer yarnInt = Integer.valueOf(yarnStr);

                    yarn.setYarnLeftover(yarnInt);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return yarnArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, brand, color;
        EditText editGrams;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_yarn_move_name);
            brand = itemView.findViewById(R.id.item_yarn_move_brand);
            color = itemView.findViewById(R.id.item_yarn_move_color);
            editGrams = itemView.findViewById(R.id.item_yarn_move_edit_amount_grams);
        }
    }

}

