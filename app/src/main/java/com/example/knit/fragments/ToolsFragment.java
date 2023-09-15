package com.example.knit.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.knit.R;
/**
 * Holds the layout and data for the 'Tools' item in the navigation bar.
 */
public class ToolsFragment extends Fragment {
    private Button btnCounter, btnCalculateYarnAmount;
    public ToolsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_tools, container, false);

        btnCounter = v.findViewById(R.id.tools_btn_counter);
        btnCalculateYarnAmount = v.findViewById(R.id.tools_btn_calculate);

        // Click listener for navigating to the counters fragment
        btnCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                ToolsCountersFragment fragment = new ToolsCountersFragment();
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        // Click listener for navigating to the calculation tool fragment
        btnCalculateYarnAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                ToolsCalculateYarnAmountFragment fragment = new ToolsCalculateYarnAmountFragment();
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            }
        });

        return v;
    }
}