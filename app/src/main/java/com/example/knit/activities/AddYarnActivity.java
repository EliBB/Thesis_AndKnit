package com.example.knit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knit.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lets the user add new yarn to the 'yarn' collection in the database.
 */
public class AddYarnActivity extends AppCompatActivity {
    // Objects
    private static final String TAG = "Yarn";
    private String userId, documentId, yarnName, yarnBrand, yarnColor, yarnLot, chipWeight, yarnNotes, amountType, getIntegerFromString;
    private Integer amountGrams, amountSkeins, amountMeters, skeinLength, skeinWeight, amountChipId, integerSkeins;

    // Widgets
    private TextView actionbarTitle;
    private EditText editYarnName, editYarnBrand, editColor, editLot, editSkeinLen, editNumberOfSkeins, editAmountGrams, editYarnNotes, viewAmountGrams, viewAmountSkeins, viewAmountMeters;
    private Button btnSave;
    private ChipGroup chipGroupWeightSkein, chipGroupAmount;
    private Toolbar actionbar;
    private TextInputLayout amountMetersLayout, amountSkeinsLayout, amountGramsLayout, viewAmountGramsLayout, viewSkeinAmountLayout;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_yarn);
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        // Action bar
        actionbar = findViewById(R.id.action_bar);
        actionbarTitle = findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.add_yarn);
        actionbar.setNavigationIcon(R.drawable.icon_close);
        btnSave = findViewById(R.id.actionbar_save);
        btnSave.setVisibility(View.VISIBLE);

        // Initialize widgets
        // Edit Texts
        editYarnName = findViewById(R.id.add_yarn_edit_name);
        editYarnBrand = findViewById(R.id.add_yarn_edit_brand);
        editColor = findViewById(R.id.add_yarn_edit_color);
        editLot = findViewById(R.id.add_yarn_edit_lot);
        editSkeinLen = findViewById(R.id.add_yarn_edit_skein_length);
        editNumberOfSkeins = findViewById(R.id.add_yarn_edit_amount_skeins);
        editAmountGrams = findViewById(R.id.add_yarn_edit_calculated_grams);
        editYarnNotes = findViewById(R.id.add_yarn_edit_notes);
        viewAmountGrams = findViewById(R.id.add_yarn_edit_amount_grams);

        // Layout
        viewSkeinAmountLayout = findViewById(R.id.add_yarn_layout_calculated_skeins);
        amountMetersLayout = findViewById(R.id.add_yarn_layout_calculated_meters);
        amountSkeinsLayout = findViewById(R.id.add_yarn_layout_amount_skeins);
        amountGramsLayout = findViewById(R.id.add_yarn_layout_calculated_grams);
        viewAmountGramsLayout = findViewById(R.id.add_yarn_layout_amount_grams);
        viewAmountSkeins = findViewById(R.id.add_yarn_edit_calculated_skeins);
        viewAmountMeters = findViewById(R.id.add_yarn_edit_calculated_meters);

        // Chip Groups
        chipGroupWeightSkein = findViewById(R.id.add_yarn_chipgroup_skein_weight);
        chipGroupAmount = findViewById(R.id.add_yarn_chipGroup_amount);

        // Init default yarn amount unit
        amountType = getResources().getString(R.string.cSkeins);

        // Click listener for saving input
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addYarnToDatabase();
                onBackPressed();
            }
        });

        // Click listener to navigate to previous screen
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Click listener for noticing the skein weight. Filters the
        // chip by removing characters that defines the unit (grams)
        chipGroupWeightSkein.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                skeinWeight = group.getCheckedChipId();
                Chip chip = group.findViewById(skeinWeight);
                if (chip != null) {
                    chipWeight = chip.getText().toString();
                    getIntegerFromString = chipWeight.replaceAll("\\s....", "");
                    integerSkeins = Integer.parseInt(getIntegerFromString);
                }
            }
        });

        // Click listener for noticing the unit of yarn amount.
        chipGroupAmount.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                amountChipId = group.getCheckedChipId();
                Chip chip = group.findViewById(amountChipId);
                amountType = chip.getText().toString();

                if (amountType.matches(getResources().getString(R.string.cSkeins))) {
                    amountGramsLayout.setVisibility(View.GONE);
                    amountSkeinsLayout.setVisibility(View.VISIBLE);
                } else if (amountType.matches(getResources().getString(R.string.cGrams))) {
                    amountSkeinsLayout.setVisibility(View.GONE);
                    amountGramsLayout.setVisibility(View.VISIBLE);
                    viewAmountGramsLayout.setVisibility(View.GONE);
                    amountMetersLayout.setVisibility(View.GONE);
                }
            }
        });

        // Click listener for calculating the yarn amount in balls, meters, and grams
        // when the user edits the input. Based on number of skeins.
        editNumberOfSkeins.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (chipWeight != null && !editNumberOfSkeins.getText().toString().matches("")) {
                    // Get input
                    amountSkeins = Integer.parseInt(editNumberOfSkeins.getText().toString());
                    //Remove last characters from chip weight
                    String getIntegerFromString = chipWeight.replaceAll("\\s....", "");
                    Integer integerSkeins = Integer.parseInt(getIntegerFromString);
                    // Calculation
                    amountGrams = amountSkeins * integerSkeins;
                    // Display result
                    viewAmountGrams.setText(String.valueOf(amountGrams));
                    viewAmountGramsLayout.setVisibility(View.VISIBLE);
                    // Calculate meters
                    if (!editSkeinLen.getText().toString().matches("")) {
                        String skeinLen = editSkeinLen.getText().toString();
                        skeinLength = Integer.parseInt(skeinLen);
                        amountMeters = amountSkeins * skeinLength;
                        viewAmountMeters.setText(amountMeters.toString());
                        amountMetersLayout.setVisibility(View.VISIBLE);
                    }
                }
                // If input is deleted
                if (editNumberOfSkeins.getText().toString().matches("")) {
                    viewAmountGramsLayout.setVisibility(View.GONE);
                    amountMetersLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Click listener for calculating the yarn amount in balls, meters, and grams
        // when the user edits the input. Based on amount of grams.
        editAmountGrams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (chipWeight != null && !editAmountGrams.getText().toString().matches("")) {
                    // Get input
                    amountGrams = Integer.parseInt(editAmountGrams.getText().toString());
                    // Calculate number of skeins
                    amountSkeins = amountGrams / integerSkeins;
                    // Display result
                    viewAmountSkeins.setText(amountSkeins.toString());
                    viewSkeinAmountLayout.setVisibility(View.VISIBLE);
                    // Calculate meters
                    if (!editSkeinLen.getText().toString().matches("")) {
                        String skeinLen = editSkeinLen.getText().toString();
                        skeinLength = Integer.parseInt(skeinLen);
                        amountMeters = amountSkeins * skeinLength;
                        viewAmountMeters.setText(amountMeters.toString());
                        amountMetersLayout.setVisibility(View.VISIBLE);
                    }
                }
                // Clear if input is deleted
                if (editAmountGrams.getText().toString().matches("")) {
                    amountSkeinsLayout.setVisibility(View.GONE);
                    amountMetersLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * Adds the details for the yarn to the 'yarn' collection in the database.
     */
    private void addYarnToDatabase() {
        yarnName = editYarnName.getText().toString();
        yarnBrand = editYarnBrand.getText().toString();
        yarnColor = editColor.getText().toString();
        yarnLot = editLot.getText().toString();
        yarnNotes = editYarnNotes.getText().toString();

        Map<String, Object> yarn = new HashMap<>();
        yarn.put("name", yarnName);
        yarn.put("brand", yarnBrand);
        yarn.put("color", yarnColor);
        yarn.put("lot", yarnLot);
        yarn.put("skeinWeight", getIntegerFromString);
        yarn.put("skeinLength", skeinLength);
        yarn.put("amountSkeins", amountSkeins);
        yarn.put("amountGrams", amountGrams);
        yarn.put("amountMeters", amountMeters);
        yarn.put("notes", yarnNotes);

        DocumentReference newYarn = db.collection("knitters").document(userId).collection("yarn").document();
        yarn.put("yarnId", newYarn.getId());
        newYarn.set(yarn);
    }
}