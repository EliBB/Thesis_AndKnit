package com.example.knit.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.knit.R;
import com.example.knit.activities.VoiceCommandActivity;
import com.example.knit.adapters.CountersAdapter;
import com.example.knit.adapters.CountersInProjectAdapter;
import com.example.knit.adapters.YarnInProjectAdapter;
import com.example.knit.classes.Counters;
import com.example.knit.classes.Knitters;
import com.example.knit.classes.Patterns;
import com.example.knit.classes.Projects;
import com.example.knit.classes.Yarn;
import com.example.knit.dialogs.UpdateYarnAfterCompletionDialogFragment;
import com.example.knit.interfaces.CountersListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays a project item from the 'projects' subcollection. The project is retrieved by getting
 * the document ID from the adapter that holds the projects. The fragment extends the CountersListener
 * to listen to the user's clicks on counters items.
 */
public class ProjectItemFragment extends Fragment implements CountersListener {
    // Objects
    private static final String TAG = "Project item:  ";
    private String userId, documentId, imagesPath, imgUrl, patternId, pdfUrl, imgName, projectType, endDate;
    private Uri imageUri;

    // Array lists
    private ArrayList<Patterns> patternsArrayList;
    private ArrayList<Counters> countersArrayList;
    private ArrayList<Yarn> yarnArrayList;
    private ArrayList<String> yarnIdsArrayList;

    // Adapters
    private CountersInProjectAdapter countersAdapter;
    private YarnInProjectAdapter yarnAdapter;

    // Classes
    private Projects projects;
    private Patterns patterns;
    private Yarn yarn;

    // Widgets & layouts
    private TextView viewProjectName, viewPattern, titlePattern, viewSize, viewDesigner, viewSts, viewRows, viewNeedleSize, viewStitchType, viewStartDate, viewEndDate, viewEndDateTitle, viewCategory, titleCounters, titleYarn, viewNotes;
    private EditText editProjectName, editSize, editDesigner, editSts, editRows, editNeedleSize, editStitchType, editCategory, editStartDate, editEndDate, editNotes;
    private LinearLayout countersLayout;
    private TextInputLayout layoutEditProjectName, layoutEditEndDate;
    private TableLayout layoutView, layoutEdit;
    private ImageView imageView, btnAddCounter;
    private FloatingActionButton btnAddNewImage;
    private Button btnEdit, btnSave, btnCompleted, btnDelete;
    private RecyclerView recyclerViewCounters, recyclerViewYarn;
    private Toolbar actionbar;

    // Dialogs
    private UpdateYarnAfterCompletionDialogFragment updateYarnAfterCompletionDialogFragment;

    // Firebase Firestore & Firebase Storage
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public ProjectItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_project_item, container, false);
        // Action bar
        actionbar = v.findViewById(R.id.action_bar);
        actionbar.setNavigationIcon(R.drawable.icon_arrow_back);
        btnSave = v.findViewById(R.id.actionbar_save);
        btnEdit = v.findViewById(R.id.actionbar_edit);
        btnEdit.setVisibility(View.VISIBLE);

        // Text Views
        viewProjectName = v.findViewById(R.id.view_project_name);
        viewSize = v.findViewById(R.id.project_item_view_size);
        viewDesigner = v.findViewById(R.id.project_item_view_designer);
        viewSts = v.findViewById(R.id.project_item_view_stitches);
        viewRows = v.findViewById(R.id.project_item_view_rows);
        viewNeedleSize = v.findViewById(R.id.project_item_view_needle_size);
        viewStitchType = v.findViewById(R.id.project_item_view_stitch_type);
        viewCategory = v.findViewById(R.id.project_item_view_category);
        viewStartDate = v.findViewById(R.id.project_item_view_start_date);
        viewEndDate = v.findViewById(R.id.project_item_view_end_date);
        viewEndDateTitle = v.findViewById(R.id.project_item_view_end_date_title);
        viewPattern = v.findViewById(R.id.project_item_view_pattern);
        titleCounters = v.findViewById(R.id.project_item_title_counters);
        titlePattern = v.findViewById(R.id.project_item_title_pattern);
        titleYarn = v.findViewById(R.id.project_item_title_yarn);
        viewNotes = v.findViewById(R.id.project_item_view_notes);

        // Edit Views
        layoutEditProjectName = v.findViewById(R.id.layout_edit_project_name);
        editProjectName = v.findViewById(R.id.edit_project_name);
        editSize = v.findViewById(R.id.project_item_edit_size);
        editDesigner = v.findViewById(R.id.project_item_edit_designer);
        editSts = v.findViewById(R.id.project_item_edit_stitches);
        editRows = v.findViewById(R.id.project_item_edit_rows);
        editNeedleSize = v.findViewById(R.id.project_item_edit_needle_size);
        editStitchType = v.findViewById(R.id.project_item_edit_stitch_type);
        editCategory = v.findViewById(R.id.project_item_edit_category);
        editStartDate = v.findViewById(R.id.project_item_edit_start_date);
        editEndDate = v.findViewById(R.id.project_item_edit_end_date);
        editNotes = v.findViewById(R.id.project_item_edit_notes);

        // Layouts
        layoutView = v.findViewById(R.id.project_item_tablelayout_view);
        layoutEdit = v.findViewById(R.id.project_item_tablelayout_edit);
        countersLayout = v.findViewById(R.id.project_item_layout_counters);
        layoutEditEndDate = v.findViewById(R.id.project_item_layout_edit_end_date);

        // Image view
        imageView = v.findViewById(R.id.project_item_imageView);

        // Buttons
        btnAddCounter = v.findViewById(R.id.project_item_btn_add_counter);
        btnCompleted = v.findViewById(R.id.project_item_btn_completed_project);
        btnDelete = v.findViewById(R.id.project_item_btn_delete_project);
        btnAddNewImage = v.findViewById(R.id.project_item_fab_add_image_after);

        // Recycler view
        recyclerViewYarn = v.findViewById(R.id.project_item_recyclerview_yarn);
        recyclerViewCounters = v.findViewById(R.id.project_item_recyclerview_counters);

        // Initialize arraylists and adapters for recycler view
        yarnIdsArrayList = new ArrayList<>();
        yarnArrayList = new ArrayList<>();
        countersArrayList = new ArrayList<>();
        yarnAdapter = new YarnInProjectAdapter(yarnArrayList, getContext());
        countersAdapter = new CountersInProjectAdapter(countersArrayList, getContext(), this);

        // Click listener for navigating back to previous screen
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        // Click listener to enable editing the project information
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProject();
            }
        });

        // Click listener to save user input from edit texts
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProject();
            }
        });

        // Click listener to assign new image to project
        btnAddNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
                chooseImage(getActivity());
            }
        });

        // Click listener for displaying assigned pattern
        viewPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPattern();
            }
        });

        // Click listener for adding a counter to the project
        btnAddCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCounterToProject();
            }
        });

        // Click listener for moving the project to the completed projects section
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDateOfCompletion();
            }
        });

        // Click listener for deleting the project
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteProject();
            }
        });

        // Get document id from previous fragment
        Bundle bundle = getArguments();
        documentId = bundle.getString("documentId");

        // Display project item by its document id
        displayProjectItem();

        return v;
    }

    /**
     * Switches the user interface to editing mode by hiding the text views
     * and making the edit text view visible. The project's information
     * is assigned to the edit layout.
     */
    private void editProject() {
        // Hide view UI
        btnEdit.setVisibility(View.GONE);
        layoutView.setVisibility(View.GONE);
        viewProjectName.setVisibility(View.GONE);
        viewNotes.setVisibility(View.GONE);

        // Show edit UI
        btnSave.setVisibility(View.VISIBLE);
        layoutEdit.setVisibility(View.VISIBLE);
        layoutEditProjectName.setVisibility(View.VISIBLE);
        editNotes.setVisibility(View.VISIBLE);

        if (projectType.matches("1")) {
            layoutEditEndDate.setVisibility(View.INVISIBLE);
        }

        // Display data in edits texts
        editProjectName.setText(projects.getProjectName());
        editSize.setText(projects.getSize());
        editDesigner.setText(projects.getDesigner());
        editSts.setText(projects.getSts());
        editRows.setText(projects.getRows());
        editNeedleSize.setText(projects.getNeedleSize());
        editStitchType.setText(projects.getStitchType());
        editCategory.setText(projects.getCategory());
        editStartDate.setText(projects.getStartDate());
        editEndDate.setText(projects.getEndDate());
        editNotes.setText(projects.getNotes());
    }

    /**
     * Switches the user interface back to view mode by hiding editable
     * layout and making the view layout visible.
     * The text views are updated with the new data.
     */
    private void saveProject() {
        // Hide editable UI
        btnSave.setVisibility(View.GONE);
        layoutEdit.setVisibility(View.GONE);
        layoutEditProjectName.setVisibility(View.GONE);
        editNotes.setVisibility(View.GONE);

        // Show view UI
        btnEdit.setVisibility(View.VISIBLE);
        layoutView.setVisibility(View.VISIBLE);
        viewProjectName.setVisibility(View.VISIBLE);
        viewNotes.setVisibility(View.VISIBLE);

        updateProject();
    }

    /**
     * Gets the pattern by the pattern ID that is
     * saved within the project item from the 'Patterns' collection in the database.
     * Afterwards, it starts the StoragePatternsItem fragment which displays the pattern whether
     * it is in PDF format or in images by sending information by a Bundle object.
     */
    private void openPattern() {
        patternsArrayList = new ArrayList<>();
        db.collection("knitters").document(userId).collection("patterns").whereEqualTo("patternId", patternId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                    patterns = documentSnapshot.toObject(Patterns.class);
                    patternsArrayList.add(patterns);
                }

                // If the pattern is a pdf
                pdfUrl = patterns.getPdfUrl();

                // If the pattern is images
                imagesPath = patterns.getPatternName();

                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("patternUrl", pdfUrl);
                mBundle.putString("imagesPath", imagesPath);

                StoragePatternsItemFragment fragment = new StoragePatternsItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });
    }

    /**
     * Reads data from the 'projects' subcollection by the document id that has been sent via a Bundle object.
     * If the project has an image is it casted to the image view and if a pattern is
     * assigned is the button for viewing the pattern made visible. All details about the
     * project is placed in text views. The user interface depends on the project type
     * - Active projects displays counters and the 'Completed button'.
     * - Completed projects displays the end date.
     */
    private void displayProjectItem() {
        if (documentId != null) {
            db.collection("knitters").document(userId).collection("projects").document(documentId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.toObject(Projects.class) != null) {
                        projects = value.toObject(Projects.class);
                        // If image assigned
                        if (projects.getImgUrl() != null) {
                            imgUrl = projects.getImgUrl();
                            Glide.with(getContext()).load(imgUrl).into(imageView);
                            imgName = projects.getImgName();
                        }

                        // If pattern assigned
                        patternId = projects.getPatternId();
                        if (patternId != null) {
                            viewPattern.setVisibility(View.VISIBLE);
                            viewPattern.setText(projects.getProjectName());
                            titlePattern.setVisibility(View.VISIBLE);
                        }

                        // Active or completed project
                        projectType = projects.getProjectType();
                        if (projectType.matches("1")) {
                            countersLayout.setVisibility(View.VISIBLE);
                            titleCounters.setVisibility(View.VISIBLE);
                            btnCompleted.setVisibility(View.VISIBLE);
                            getCounters();
                        } else {
                            viewEndDate.setVisibility(View.VISIBLE);
                            viewEndDateTitle.setVisibility(View.VISIBLE);
                        }

                        // Display data
                        viewProjectName.setText(projects.getProjectName());
                        viewSize.setText(projects.getSize());
                        viewDesigner.setText(projects.getDesigner());
                        viewSts.setText(projects.getSts());
                        viewRows.setText(projects.getRows());
                        viewNeedleSize.setText(projects.getNeedleSize());
                        viewStitchType.setText(projects.getStitchType());
                        viewCategory.setText(projects.getCategory());
                        viewStartDate.setText(projects.getStartDate());
                        viewEndDate.setText(projects.getEndDate());
                        viewNotes.setText(projects.getNotes());

                        getYarn();
                    }
                }
            });
        }
    }

    /**
     * Called when the project is active to show the counters that is assigned to the project.
     * The counters are get from the 'projects counters' subcollection of the project, and displayed
     * in a horizontal recycler view.
     */
    private void getCounters() {
        db.collection("knitters").document(userId).collection("projects").document(documentId).collection("project counters").orderBy("added", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    countersArrayList.clear();
                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                        Counters counters = documentSnapshot.toObject(Counters.class);
                        counters.setDocumentId(documentSnapshot.getId());
                        countersArrayList.add(counters);
                    }
                    countersAdapter.notifyDataSetChanged();
                    recyclerViewCounters.setHasFixedSize(true);
                    recyclerViewCounters.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    recyclerViewCounters.setAdapter(countersAdapter);
                }
            }
        });

    }

    /**
     * Gets the assigned yarn from the project from the 'yarn' subcollection
     * and displays the yarn items in a recycler view.
     */
    private void getYarn() {
        for (String yarnId : projects.getYarnIds()) {
            db.collection("knitters").document(userId).collection("yarn").whereEqualTo("yarnId", yarnId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                            yarn = documentSnapshot.toObject(Yarn.class);
                            yarnArrayList.add(yarn);
                        }
                        yarnAdapter.notifyDataSetChanged();
                        titleYarn.setVisibility(View.VISIBLE);
                        recyclerViewYarn.setVisibility(View.VISIBLE);
                        recyclerViewYarn.setHasFixedSize(true);
                        recyclerViewYarn.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerViewYarn.setAdapter(yarnAdapter);
                    }
                }
            });
        }
    }

    /**
     * Updates the database with new values.
     */
    private void updateProject() {
        Map<String, Object> project = new HashMap<>();
        project.put("projectName", editProjectName.getText().toString());
        project.put("size", editSize.getText().toString());
        project.put("designer", editDesigner.getText().toString());
        project.put("sts", editSts.getText().toString());
        project.put("rows", editRows.getText().toString());
        project.put("needleSize", editNeedleSize.getText().toString());
        project.put("stitchType", editStitchType.getText().toString());
        project.put("category", editCategory.getText().toString());
        project.put("startDate", editStartDate.getText().toString());
        project.put("endDate", editEndDate.getText().toString());
        project.put("notes", editNotes.getText().toString());

        if (documentId != null) {
            db.collection("knitters").document(userId).collection("projects").document(documentId).update(project).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "Project updated successfully");
                    yarnIdsArrayList.clear();
                    yarnArrayList.clear();
                    getYarn();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Adds a new counter to the 'project counters' subcollection.
     */
    private void addCounterToProject() {
        Map<String, Object> counters = new HashMap<>();
        counters.put("name", null);
        counters.put("count", 0);
        counters.put("added", System.currentTimeMillis());

        db.collection("knitters").document(userId).collection("projects").document(documentId).collection("project counters").add(counters).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Counter updated successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Called when the user clicks the 'I completed this project' button
     * and allows the user to enter an end date.
     * On OK is the full screen dialog UpdateYarnAfterCompletion opened if the project
     * has been assigned any yarn. Else is the database updated and the user is
     * send to the Projects screen.
     */
    private void selectDateOfCompletion() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                endDate = d + "/" + (m + 1) + "/" + y;

                Bundle mBundle = new Bundle();
                mBundle.putString("documentId", documentId);
                mBundle.putString("endDate", endDate);

                if (!yarnArrayList.isEmpty()) {
                    updateYarnAfterCompletionDialogFragment = new UpdateYarnAfterCompletionDialogFragment();
                    updateYarnAfterCompletionDialogFragment.setArguments(mBundle);
                    updateYarnAfterCompletionDialogFragment.show(getChildFragmentManager(), "Update yarn");
                } else {
                    updateProjectTypeAndEndDate();
                    updateProfileStatistics();
                    navigateToProjectsScreen();
                }
            }
        },
                // Pass y, m d to date picker.
                year, month, day);
        datePickerDialog.show();
    }

    /**
     * Changes project type to 2 = completed project and the end date is saved in
     * the database.
     */
    private void updateProjectTypeAndEndDate() {
        db.collection("knitters").document(userId).collection("projects").document(documentId).update("projectType", "2", "endDate", endDate);
    }

    /**
     * Increments the number of completed projects, which is displayed in the profile page.
     */
    private void updateProfileStatistics() {
        db.collection("knitters").document(userId).update("completedProjects", FieldValue.increment(1));
    }

    /**
     * Navigates the user to the Projects screen.
     */
    private void navigateToProjectsScreen() {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
        ProjectsFragment fragment = new ProjectsFragment();
        appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    /**
     * Alert dialog to confirm deletion of a project.
     * If positive is the project deleted from the 'projects' subcollection, the image
     * is deleted from Firebase Storage, and the user is navigated back to the Projects screen.
     */
    private void confirmDeleteProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(R.string.alert_title_delete_project);
        builder.setMessage(getResources().getString(R.string.alert_message_delete_project) + " " + projects.getProjectName());
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.collection("knitters").document(userId).collection("projects").document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (imgUrl != null) {
                            storageReference.child("users").child(userId).child("projects").child(imgName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "Projects image is deleted successfully");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Project image: " + e.getMessage());
                                }
                            });
                        }
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Opens dialog to let user to take a photo or choose image from gallery.
     */
    private void chooseImage(Context context) {
        final CharSequence[] optionsMenu = {getResources().getString(R.string.take_photo), getResources().getString(R.string.choose_from_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (optionsMenu[i].equals(getResources().getText(R.string.take_photo))) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New image");
                    imageUri = getActivity().getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    cameraActivityResultLauncher.launch(cameraIntent);
                } else if (optionsMenu[i].equals(getResources().getText(R.string.choose_from_gallery))) {
                    Intent chooseImg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryActivityResultLauncher.launch(chooseImg);
                }
            }
        });
        builder.show();
    }

    /**
     * Uploads the image to Firebase Storage and saved with the same name as the
     * previous image to override it.
     * On successful upload is a new download URL get for the image, and
     * updated in the project item.
     */
    private void uploadNewPhoto() {
        storageReference.child("users").child(userId).child("projects").child(projects.getImgName()).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child("users").child(userId).child("projects").child(projects.getImgName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imgUrl = uri.toString();
                        db.collection("knitters").document(userId).collection("projects").document(documentId).update("imgUrl", imgUrl);
                    }
                });
            }
        });
    }


    /** The source code for the camera functionality is inspired from:
     * Title: Android Capturing Images from Camera or Gallery as Bitmaps using ActivityResultLauncher
     * Author: Asif, H
     * Date: 2022
     * Availability: www.medium.com */

    /**
     * Captures image and displays it in the ImageView object.
     */
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Bitmap inputImage = uriToBitmap(imageUri);
                Bitmap rotated = rotateBitmap(inputImage);
                imageView.setImageBitmap(rotated);

                uploadNewPhoto();
            }
        }
    });

    /**
     * Retrieves image from gallery and displays it in the ImageView object
     */
    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                imageUri = result.getData().getData();
                imageView.setImageURI(imageUri);

                uploadNewPhoto();
            }
        }
    });

    /**
     * Converts the captured image to bitmap format.
     */
    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getActivity().getApplicationContext().getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Rotates image to display it in portrait mode
     */
    @SuppressLint("Range")
    public Bitmap rotateBitmap(Bitmap input) {
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = getActivity().getApplicationContext().getContentResolver().query(imageUri, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }
        Log.d("tryOrientation", orientation + "");
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(orientation);
        Bitmap cropped = Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), rotationMatrix, true);
        return cropped;
    }

    /**
     * Checks runtime permission on MainActivity for camera and media access.
     * Requests permission if they are not granted.
     */
    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, 1);
        }
    }


    /**
     * Starts the VoiceCommand activity, and the id for the project and counter is
     * send with the intent.
     */
    @Override
    public void openMicrophone(Counters counters) {
        Intent intent = new Intent(getActivity(), VoiceCommandActivity.class);
        intent.putExtra("projectId", documentId);
        intent.putExtra("counterId", counters.getDocumentId());
        startActivity(intent);
    }

    /**
     * Increments the count in the database
     */
    @Override
    public void increment(Counters counters) {
        db.collection("knitters").document(userId).collection("projects").document(documentId).collection("project counters").document(counters.getDocumentId()).update("count", FieldValue.increment(1));
    }

    /**
     * Decrements the count in the database
     */
    @Override
    public void decrement(Counters counters) {
        int currentCount = counters.getCount();
        if (currentCount != 0) {
            currentCount--;
            db.collection("knitters").document(userId).collection("projects").document(documentId).collection("project counters").document(counters.getDocumentId()).update("count", currentCount);
        }
    }

    /**
     * Saves the name of the counter in the database
     */
    @Override
    public void saveName(Counters counters) {
        db.collection("knitters").document(userId).collection("projects").document(documentId).collection("project counters").document(counters.getDocumentId()).update("name", counters.getName());
    }

    /**
     * Deletes the counter in the database
     */
    @Override
    public void delete(Counters counters) {
        db.collection("knitters").document(userId).collection("projects").document(documentId).collection("project counters").document(counters.getDocumentId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Counter deleted successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Counter deletion failed    " + e.getMessage());
            }
        });
    }
}