package com.example.knit.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knit.R;
import com.example.knit.adapters.GalleryAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Lets the user add a new pattern to their stash. The pattern
 * can be in either PDF-format or images, which is stored
 * in Firebase Storage. The details for the pattern is
 * stored in the 'patterns' collection.
 */
public class AddPatternActivity extends AppCompatActivity {
    // Objects
    private static final String TAG = "Patterns";
    private String userId, urlPath, name, designer, category, pdfName, pdfFromStorage, patternId;
    private ArrayList<String> imageUrl, pdfInStorage, folderNames;
    private ArrayList<Uri> uriArrayList;

    private Uri pdf;

    // Widgets
    private TextView pdfView, galleryCount, actionbarTitle;
    private EditText patternName, patternDesigner;
    private Button addPdf, addImages, btnSave;
    private RecyclerView galleryRecyclerview;
    private ProgressBar progressBar;
    private Toolbar actionbar;
    private AutoCompleteTextView autoCompleteTextView;

    // Layout
    private TextInputLayout layoutEditPatternName;

    // Adapter
    private GalleryAdapter adapter;

    // Firebase Firestore, Firebase Storage &Firebase Authentication
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // File launcher
    private ActivityResultLauncher<Intent> fileLauncher, galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pattern);
        // Initialize Firebase
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        storageReference = storage.getReference();

        // Action bar
        actionbar = findViewById(R.id.action_bar);
        actionbarTitle = findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.add_pattern);
        actionbar.setNavigationIcon(R.drawable.icon_close);
        btnSave = findViewById(R.id.actionbar_save);
        btnSave.setVisibility(View.VISIBLE);

        // Initialize widgets
        // TextView
        pdfView = findViewById(R.id.add_patterns_view_pdf);
        galleryCount = findViewById(R.id.add_patterns_view_images_count);

        // EditText
        patternName = findViewById(R.id.add_pattern_edit_name);
        patternDesigner = findViewById(R.id.add_pattern_edit_designer);
        layoutEditPatternName = findViewById(R.id.add_pattern_layout_name);

        // Buttons
        addPdf = findViewById(R.id.add_pattern_btn_add_pdf);
        addImages = findViewById(R.id.add_patterns_btn_add_images);

        // Arraylists
        uriArrayList = new ArrayList<>();
        imageUrl = new ArrayList<>();
        pdfInStorage = new ArrayList<>();
        folderNames = new ArrayList<>();

        // Adapter
        adapter = new GalleryAdapter(uriArrayList);

        // Recyclerview
        galleryRecyclerview = findViewById(R.id.add_pattern_recycler_view_images);

        // Progress bar
        progressBar = findViewById(R.id.add_pattern_progress_bar);

        // Dropdown menu
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        // Launcher for choosing PDF-file
        fileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                if (data != null) {
                    pdf = data.getData();

                    Cursor returnCursor = getContentResolver().query(pdf, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    returnCursor.moveToFirst();

                    pdfName = returnCursor.getString(nameIndex);

                    pdfView.setText(pdfName);
                    pdfView.setVisibility(View.VISIBLE);
                    patternName.setText(pdfName);

                    addPdf.setVisibility(View.GONE);
                    addImages.setVisibility(View.GONE);
                }
            }
        });

        // Launcher for selecting multiple images in gallery
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                if (data != null) {
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            uriArrayList.add(data.getClipData().getItemAt(i).getUri());
                            addPdf.setVisibility(View.GONE);
                            galleryRecyclerview.setVisibility(View.VISIBLE);
                            galleryCount.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                        galleryRecyclerview.setHasFixedSize(true);
                        galleryRecyclerview.setLayoutManager(new LinearLayoutManager(AddPatternActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        galleryRecyclerview.setAdapter(adapter);
                        galleryCount.setText(getResources().getString(R.string.photos) + " " + uriArrayList.size());
                        addImages.setText(getResources().getString(R.string.uploadMorePhotos));

                    } else if (data.getData() != null) {
                        String imgUrl = data.getData().getPath();
                        uriArrayList.add(Uri.parse(imgUrl));
                    }
                }
            }
        });

        // Click listener to navigate to previous screen
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Click listener for selecting a PDF
        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
                choosePdf();
            }
        });

        // Click listener for selecting images
        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
                chooseImages();
            }
        });

        // Click listener for displaying dropdown with categories
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, R.layout.item_dropdown, getResources().getStringArray(R.array.categories));
        adapterCat.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setAdapter(adapterCat);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                category = autoCompleteTextView.getText().toString();
            }
        });

        // Click listener for saving input
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pdf != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    checkIfPdfIsInDatabase();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    checkIfImageFolderExists();
                }
            }
        });
    }

    /**
     * Lets the user to choose a PDF on their local device.
     */
    private void choosePdf() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        fileLauncher.launch(intent);
    }

    /**
     * Lets the user choose multiple images from their gallery.
     */
    private void chooseImages() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryLauncher.launch(intent);
    }

    /**
     * Checks whether the chosen PDF-file is already in the
     * user's stash to avoid duplicates.
     */
    private void checkIfPdfIsInDatabase() {
        if (pdf != null) {
            StorageReference listRef = storageReference.child("users/" + userId + "/patterns/");
            listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    if (!listResult.getItems().isEmpty()) {
                        for (StorageReference item : listResult.getItems()) {
                            pdfFromStorage = item.getName();
                            pdfInStorage.add(pdfFromStorage);
                        }
                        if (pdfInStorage.contains(pdfName)) {
                            Toast.makeText(getApplicationContext(), patternName.getText().toString() + " is already in your stash", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            addPdf.setVisibility(View.VISIBLE);
                            addImages.setVisibility(View.VISIBLE);
                            pdfView.setVisibility(View.GONE);
                            patternName.setText("");
                            patternDesigner.setText("");
                            autoCompleteTextView.setText(null);
                        } else {
                            uploadPdf(pdf);
                        }
                    } else {
                        uploadPdf(pdf);
                    }
                }
            });
        }
    }

    /**
     * Uploads PDF to the Firebase Storage.
     */
    private void uploadPdf(Uri pdf) {
        name = patternName.getText().toString().trim();
        designer = patternDesigner.getText().toString().trim();

        String filePath = "users/" + userId + "/patterns/" + pdfName;

        if (!name.matches("")) {
            storageReference.child(filePath).putFile(pdf).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(filePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urlPath = uri.toString();
                            addPatternToDatabase();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            layoutEditPatternName.setError(getResources().getString(R.string.enterPatternName));
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Checks whether the entered pattern name is already in the
     * user's stash to avoid duplicates.
     */
    private void checkIfImageFolderExists() {
        name = patternName.getText().toString().trim();
        if (!uriArrayList.isEmpty()) {
            StorageReference listRef = storageReference.child("users/" + userId + "/patterns/");
            listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference folder : listResult.getPrefixes()) {
                        folderNames.add(folder.getName().trim());
                    }
                    if (folderNames.contains(name)) {
                        confirmOverride();
                    } else {
                        uploadImages();
                    }
                }
            });
        } else {
            Toast.makeText(AddPatternActivity.this, getResources().getString(R.string.noImagesOrPDF), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Displayed when a user uploads a duplicate pattern name
     * to confirm the new pattern should be overwritten.
     */
    private void confirmOverride() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(getResources().getString(R.string.replacePattern));
        builder.setMessage(name + " " + getResources().getString(R.string.patternAlreadyExists));

        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadImages();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressBar.setVisibility(View.GONE);
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Uploads images to Firebase Storage. The images are named
     * after the pattern name and incrementally given a number.
     */
    private void uploadImages() {
        name = patternName.getText().toString().trim();
        designer = patternDesigner.getText().toString().trim();

        if (!name.matches("")) {
            int count = 1;
            for (Uri image : uriArrayList) {
                String filePath = "users/" + userId + "/patterns" + "/" + name + " /" + name + count;
                count++;
                storageReference.child(filePath).putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.child(filePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                imageUrl.add(url);
                            }
                        });
                    }
                });
            }
            addPatternToDatabase();
        } else {
            layoutEditPatternName.setError(getResources().getString(R.string.enterPatternName));
        }
    }

    /**
     * Adds the details for the pattern in the database
     */
    private void addPatternToDatabase() {
        Map<String, Object> pattern = new HashMap<>();
        pattern.put("patternName", name);
        pattern.put("patternDesigner", designer);
        pattern.put("pdfUrl", urlPath);
        pattern.put("category", category);

        DocumentReference newPattern = db.collection("knitters").document(userId).collection("patterns").document();
        patternId = newPattern.getId();
        pattern.put("patternId", patternId);
        newPattern.set(pattern);

        progressBar.setVisibility(View.GONE);
        onBackPressed();
    }

    /**
     * Checks runtime permissions for external media access in Manifest file
     */
    private void requestPermissions() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, 1);
        }
    }
}