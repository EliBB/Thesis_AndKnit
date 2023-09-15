package com.example.knit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lets the user add new needles to their stash. The input data is stored
 * in the 'needles' collection in Firebase Cloud Firestore.
 */
public class AddNeedlesActivity extends AppCompatActivity {
    // Objects
    private static final String TAG = "Needles";
    private String userId, notes, needleType, needleSize, needleLen, wireLen, needleMaterial, needleSizes;
    private Integer needleTypeId, needleMaterialId;
    private List<Integer> needleSizeIds, needleLenIds, wireLenIds;
    private List<String> listNeedleSizes, listNeedleLens, listWireLens;

    // Widgets
    private EditText needlesNotes;
    private TextView wireLenTitle, actionbarTitle;
    private ChipGroup chipGroupType, chipGroupSizes, chipGroupNeedleLength, chipGroupWireLength, chipGroupMaterial;
    private Button btnSave;
    private Toolbar actionbar;

    // Firebase Firestore & Firebase Authentication
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_needles);
        // Initialize Firebase Authentication & Cloud Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        // Action bar
        actionbar = findViewById(R.id.action_bar);
        actionbarTitle = findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.add_needles);
        btnSave = findViewById(R.id.actionbar_save);
        btnSave.setVisibility(View.VISIBLE);
        actionbar.setNavigationIcon(R.drawable.icon_close);

        // Initialize widgets
        // Text Views
        wireLenTitle = findViewById(R.id.add_needles_view_wireLen);

        // EditText
        needlesNotes = findViewById(R.id.needles_notes);

        // ChipGroups
        chipGroupType = findViewById(R.id.chipGroupNeedleType);
        chipGroupSizes = findViewById(R.id.chipGroupNeedleSizes);
        chipGroupNeedleLength = findViewById(R.id.chipGroupNeedleLen);
        chipGroupWireLength = findViewById(R.id.chipGroupWireLen);
        chipGroupMaterial = findViewById(R.id.chipGroupMaterial);

        // Click listener to navigate to previous screen
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Click listener for noticing type of needle
        chipGroupType.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                needleTypeId = group.getCheckedChipId();
                Chip chip = group.findViewById(needleTypeId);
                if (chip != null) {
                    needleType = chip.getText().toString();

                    if (needleType.matches(getResources().getString(R.string.circular)) || needleType.matches(getResources().getString(R.string.interchangeable))) {
                        wireLenTitle.setVisibility(View.VISIBLE);
                        chipGroupWireLength.setVisibility(View.VISIBLE);
                    } else {
                        wireLenTitle.setVisibility(View.GONE);
                        chipGroupWireLength.setVisibility(View.GONE);
                    }
                }
            }
        });

        // Click listener for noticing sizes of needle
        chipGroupSizes.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                needleSizeIds = group.getCheckedChipIds();
                listNeedleSizes = new ArrayList();
                for (Integer sizeId : needleSizeIds) {
                    Chip chip = group.findViewById(sizeId);
                    needleSize = chip.getText().toString();
                    listNeedleSizes.add(needleSize);
                }
            }
        });

        // Click listener for noticing length of needle
        chipGroupNeedleLength.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                needleLenIds = group.getCheckedChipIds();
                listNeedleLens = new ArrayList();
                for (Integer needleLenId : needleLenIds) {
                    Chip chip = group.findViewById(needleLenId);
                    needleLen = chip.getText().toString();
                    listNeedleLens.add(needleLen);
                }
            }
        });

        // Click listener for noticing type of needle
        chipGroupWireLength.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                wireLenIds = group.getCheckedChipIds();
                listWireLens = new ArrayList();
                for (Integer needleWireId : wireLenIds) {
                    Chip chip = group.findViewById(needleWireId);
                    wireLen = chip.getText().toString();
                    listWireLens.add(wireLen);
                }
            }
        });

        // Click listener for noticing needle material
        chipGroupMaterial.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                needleMaterialId = group.getCheckedChipId();
                Chip chip = group.findViewById(needleMaterialId);
                if (chip != null) {
                    needleMaterial = chip.getText().toString();
                }
            }
        });

        // Click listener for saving data
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNeedlesToDatabase();
                onBackPressed();
            }
        });
    }

    /**
     * Adds the new needle instance to the database. The ChipGroups
     * that stores chips with units (mm, cmm) are filtered to
     * save the number only.
     */
    private void addNeedlesToDatabase() {
        notes = needlesNotes.getText().toString();

        if (listNeedleSizes != null) {
            needleSizes = listNeedleSizes.toString();
            needleSizes = needleSizes.substring(1, needleSizes.length() - 1);
        }

        if (needleLen != null) {
            needleLen = listNeedleLens.toString();
            needleLen = needleLen.substring(1, needleLen.length() - 1);
        }
        if (listWireLens != null) {
            wireLen = listWireLens.toString();
            wireLen = wireLen.substring(1, wireLen.length() - 1);
        }

        Map<String, Object> needles = new HashMap<>();
        needles.put("type", needleType);
        needles.put("sizes", needleSizes);
        needles.put("needleLen", needleLen);
        needles.put("wireLen", wireLen);
        needles.put("material", needleMaterial);
        needles.put("notes", notes);

        db.collection("knitters").document(userId).collection("needles").add(needles).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //Toast.makeText(getApplicationContext(), "Needles saved", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}