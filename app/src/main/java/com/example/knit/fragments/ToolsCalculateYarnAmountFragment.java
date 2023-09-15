package com.example.knit.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knit.R;
import com.google.android.material.textfield.TextInputLayout;
/** Allows the user to use the calculation tool to calculate
 * the correct amount of yarn for a new project */
public class ToolsCalculateYarnAmountFragment extends Fragment {
    // Objects
    private String skeinLen, skeinWeight, skeinAmount, myYarnLen, myYarnWeight;

    // Widgets
    private TextView result, actionbarTitle;
    private EditText yarnLen, yarnWeight, yarnAmount, myLen, myWeight;
    private Button btnCalculate, btnNewCal;
    private Toolbar actionbar;
    private TextInputLayout skeinLenLayout, skeinWeightLayout, skeinAmountLayout, myLenLayout, myWeightLayout;

    public ToolsCalculateYarnAmountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_tools_calculate_yarn_amount, container, false);
        // Action bar
        actionbar = v.findViewById(R.id.action_bar);
        actionbarTitle = v.findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.calculate_yarn_amount);
        actionbar.setNavigationIcon(R.drawable.icon_arrow_back);

        // Initialize widgets
        result = v.findViewById(R.id.tools_calculation_view_result);
        yarnLen = v.findViewById(R.id.tools_calculation_edit_pattern_yarn_length);
        yarnWeight = v.findViewById(R.id.tools_calculation_edit_pattern_skein_weight);
        yarnAmount = v.findViewById(R.id.tools_calculation_edit_pattern_yarn_amount);
        myLen = v.findViewById(R.id.tools_calculation_edit_my_skein_length);
        myWeight = v.findViewById(R.id.tools_calculation_edit_my_skein_weight);
        btnCalculate = v.findViewById(R.id.tools_calculation_btn_calculate);
        btnNewCal = v.findViewById(R.id.tools_calculation_btn_new_calculation);
        skeinLenLayout = v.findViewById(R.id.tools_calculation_layout_pattern_skein_length);
        skeinWeightLayout = v.findViewById(R.id.tools_calculation_layout_pattern_skein_weight);
        skeinAmountLayout = v.findViewById(R.id.tools_calculation_layout_pattern_skein_amount);
        myLenLayout = v.findViewById(R.id.tools_calculation_layout_my_skein_length);
        myWeightLayout = v.findViewById(R.id.tools_calculation_layout_my_skein_weight);

        // Click listener for navigating to the previous screen
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        // Click listener for calculating yarn amount
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateYarnAmount();
            }
        });

        // Click listener for resetting input fields
        btnNewCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newCalculation();
            }
        });

        return v;
    }

    /** Calculates yarn amount based on user input. Checks if all fields are filled.  */
    private void calculateYarnAmount() {

        skeinLen = yarnLen.getText().toString();
        skeinWeight = yarnWeight.getText().toString();
        skeinAmount = yarnAmount.getText().toString();
        myYarnLen = myLen.getText().toString();
        myYarnWeight = myWeight.getText().toString();

        if (skeinLen.matches("") || skeinWeight.matches("") || skeinAmount.matches("")|| myYarnLen.matches("") || myYarnWeight.matches("")) {

            if(skeinLen.matches("")){
                skeinLenLayout.setError(getString(R.string.error));
            }
            if(skeinWeight.matches("")) {
                skeinWeightLayout.setError(getString(R.string.error));
            }
            if (skeinAmount.matches("")) {
                skeinAmountLayout.setError(getString(R.string.error));
            }
            if(myYarnLen.matches("")) {
                myLenLayout.setError(getString(R.string.error));
            }
            if(myYarnWeight.matches("")){
                myWeightLayout.setError(getString(R.string.error));
            }

        } else {
            double patternMeters = Double.parseDouble(skeinLen);
            double patternWeight = Double.parseDouble(skeinWeight);
            double patternAmount = Double.parseDouble(skeinAmount);
            double myYarnAmount = Double.parseDouble(myYarnLen);
            double myYarnWeightDb = Double.parseDouble(myYarnWeight);

            double calculation = Math.ceil(((patternAmount / patternWeight) * patternMeters) / (myYarnAmount));

            int calInt = (int) calculation;

            String calculationResult = String.valueOf(calInt);

            btnCalculate.setVisibility(View.GONE);

            String text = getString(R.string.calculation_result, calculationResult);
            result.setText(text);
            result.setVisibility(View.VISIBLE);

            btnNewCal.setVisibility(View.VISIBLE);

            skeinLenLayout.setError(null);
            skeinWeightLayout.setError(null);
            skeinAmountLayout.setError(null);
            myLenLayout.setError(null);
            myWeightLayout.setError(null);
        }
    }

    /** Clears the input fields to allow the user enter new values */
    private void newCalculation() {
        btnCalculate.setVisibility(View.VISIBLE);
        btnNewCal.setVisibility(View.GONE);
        result.setVisibility(View.GONE);
        yarnLen.setText(null);
        yarnWeight.setText(null);
        yarnAmount.setText(null);
        myLen.setText(null);
        myWeight.setText(null);
    }
}