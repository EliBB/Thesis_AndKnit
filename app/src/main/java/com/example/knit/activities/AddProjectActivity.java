package com.example.knit.activities;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.knit.R;
import com.example.knit.adapters.YarnInProjectAdapter;
import com.example.knit.classes.Knitters;
import com.example.knit.classes.Yarn;
import com.example.knit.dialogs.AddPatternDialogFragment;
import com.example.knit.dialogs.AddYarnDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * Lets the user to add a new project to the 'projects' collection
 * in Firebase. The image is stored in Firebase Storage.
 */

public class AddProjectActivity extends AppCompatActivity implements AddYarnDialogFragment.InputListener, AddPatternDialogFragment.InputListener {
    // Objects
    private static final String TAG = "Projects";
    private String userId, projectName, imgUrl, category, projectType, projectId, patternId, imgPath, patternUrl;
    private Integer projectTypeId;
    private Uri imageUri;
    public ArrayList<String> yarnId;
    private ArrayList<Yarn> chosenYarnArraylist;

    // Widgets & Layouts
    private ImageView imageView, btnAddImage;
    private TextView actionbarTitle, viewAfterPatternAdded;
    private EditText editProjectName, editSize, editDesigner, editSts, editRows, editNeedleSize, editStitchType, editStartDate, editEndDate, editProjectNotes;
    private ChipGroup projectTypeChipGroup;
    private Button btnSave, btnAddYarn, btnAddPattern;
    private FloatingActionButton btnAddImageAfter;
    private RecyclerView yarnInProjectRecyclerView;
    private Toolbar actionbar;
    private AutoCompleteTextView autoCompleteTVCat;
    private TextInputLayout endDateLayout, nameLayout;
    private CoordinatorLayout layoutAfterImage;

    // Firebase Firestore, Firebase Authentication & Firebase Storage
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    // Classes & Fragments
    private Knitters knitter;
    private YarnInProjectAdapter adapter;
    private AddYarnDialogFragment addYarnDialogFragment;
    private AddPatternDialogFragment addPatternDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        // Initialize Firebase Firestore, Firebase Authentication & Firebase Storage
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        // Action bar
        actionbar = findViewById(R.id.action_bar);
        actionbarTitle = findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.add_project);
        btnSave = findViewById(R.id.actionbar_save);
        btnSave.setVisibility(View.VISIBLE);
        actionbar.setNavigationIcon(R.drawable.icon_close);

        // Initialize widgets
        // Text view
        viewAfterPatternAdded = findViewById(R.id.add_project_view_after_added_pattern);
        // Edit texts
        editProjectName = findViewById(R.id.add_project_edit_name);
        editSize = findViewById(R.id.add_project_edit_size);
        editDesigner = findViewById(R.id.add_project_edit_designer);
        editSts = findViewById(R.id.add_project_edit_stitches);
        editRows = findViewById(R.id.add_project_edit_rows);
        editNeedleSize = findViewById(R.id.add_project_edit_needle_size);
        editStitchType = findViewById(R.id.add_project_edit_stitch_type);
        editStartDate = findViewById(R.id.add_project_edit_start_date);
        editEndDate = findViewById(R.id.add_project_edit_end_date);
        editProjectNotes = findViewById(R.id.add_project_edit_notes);

        // Image view
        imageView = findViewById(R.id.add_project_imageView);

        // Chip group
        projectTypeChipGroup = findViewById(R.id.add_project_chipgroup_project_type);

        // Buttons
        btnAddImage = findViewById(R.id.add_project_btn_add_image);
        btnAddPattern = findViewById(R.id.add_project_btn_add_pattern);
        btnAddYarn = findViewById(R.id.add_project_btn_add_yarn);
        btnAddImageAfter = findViewById(R.id.add_project_btn_add_image_after);

        // Layouts
        layoutAfterImage = findViewById(R.id.add_project_layout_after_image);
        endDateLayout = findViewById(R.id.add_project_layout_end_date);
        nameLayout = findViewById(R.id.add_project_layout_name);

        // Dropdown
        autoCompleteTVCat = findViewById(R.id.add_project_auto_complete_view_categories);

        // Recycler view
        yarnInProjectRecyclerView = findViewById(R.id.add_project_recycler_view_yarn);

        // Initialize lists and adapter for recycler view
        yarnId = new ArrayList<>();
        chosenYarnArraylist = new ArrayList<>();
        adapter = new YarnInProjectAdapter(chosenYarnArraylist, getApplicationContext());

        // Initialize category dropdown
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, R.layout.item_dropdown, getResources().getStringArray(R.array.categories));
        adapterCat.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        autoCompleteTVCat.setAdapter(adapterCat);

        // Initialize default projectType 1 = active // 2 = completed
        projectType = "1";

        // Click listener to navigate to previous screen. Checks whether the project name is filled.
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editProjectName.getText().toString().matches("")) {
                    onBackPressed();
                } else {
                    confirmGoBack();
                }
            }
        });

        // Click listener for saving the project. Requires project name to be filled.
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editProjectName.getText().toString().matches("")) {
                    addImageToDatabase();
                    onBackPressed();
                } else {
                    nameLayout.setError(getResources().getString(R.string.require_name));
                }
            }
        });

        // Click listener adding an image.
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
                chooseImage(AddProjectActivity.this);
            }
        });

        // Click listener for adding new image if an image is already displayed.
        btnAddImageAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(AddProjectActivity.this);
            }
        });

        // Click listener for opening the AddPattern dialog fragment to assign a pattern.
        btnAddPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPatternDialogFragment = new AddPatternDialogFragment();
                addPatternDialogFragment.show(getSupportFragmentManager(), "Add pattern dialog");
            }
        });

        // Click listener for starting the AddPattern dialog fragment to assign pattern,
        // if a pattern have been assigned.
        viewAfterPatternAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPatternDialogFragment = new AddPatternDialogFragment();
                addPatternDialogFragment.show(getSupportFragmentManager(), "Add pattern dialog");
            }
        });

        // Click listener to notice the project type.
        projectTypeChipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                projectTypeId = group.getCheckedChipId();
                Chip chip = group.findViewById(projectTypeId);
                projectType = chip.getText().toString();

                if (projectType.matches(getResources().getString(R.string.active_project))) {
                    endDateLayout.setVisibility(View.INVISIBLE);
                    projectType = "1";
                    editEndDate.setText(null);
                } else if (projectType.matches(getResources().getString(R.string.completed_project))) {
                    endDateLayout.setVisibility(View.VISIBLE);
                    projectType = "2";
                }
            }
        });

        // Click listener for displaying dropdown menu with categories.
        autoCompleteTVCat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                category = autoCompleteTVCat.getText().toString();
            }
        });

        // Click listener for starting Date picker dialog for start date.
        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStartDate();
            }
        });

        // Click listener for starting Date picker dialog for end date.
        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeEndDate();
            }
        });

        // Click listener for starting AddYarn dialog fragment to assign yarn to the project.
        btnAddYarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addYarnDialogFragment = new AddYarnDialogFragment();
                addYarnDialogFragment.show(getSupportFragmentManager(), "Add yarn dialog");
            }
        });
    }

    /**
     * Sends data between the activity and dialog fragment for selecting yarn.
     */
    @Override
    public void sendYarnInput(ArrayList<String> inputList) {
        yarnId = inputList;
        displayChosenYarn();
        swipeYarnItem();
    }

    /**
     * Sends data between the activity and dialog fragment for selecting a pattern.
     */
    @Override
    public void sendPatternInput(String input) {
        patternId = input;
        displayChosenPattern();
    }

    /**
     * Uploads the image to Firebase Storage (if assigned)
     * by the name of the project. Additionally, it gets a download-URL
     * which is stored in a string value and saved with the project
     * in the database.
     */
    private void addImageToDatabase() {
        projectName = editProjectName.getText().toString().trim();
        projectId = projectName + System.currentTimeMillis();

        if (imageUri != null) {
            imgPath = "users/" + userId + "/projects/" + projectId;
            storageReference.child(imgPath).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(imgPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgUrl = uri.toString();
                            addProjectToDatabase();
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
            addProjectToDatabase();
        }
    }

    /**
     * Uploads the details for the project to the 'projects' collection
     * in Firebase.
     * If the project is registered as 'completed' is the number of
     * completed projects in the user's profile section incremented by 1.
     */
    private void addProjectToDatabase() {
        Map<String, Object> project = new HashMap<>();
        project.put("projectName", projectName);
        project.put("size", editSize.getText().toString().trim());
        project.put("designer", editDesigner.getText().toString().trim());
        project.put("sts", editSts.getText().toString().trim());
        project.put("rows", editRows.getText().toString().trim());
        project.put("needleSize", editNeedleSize.getText().toString().trim());
        project.put("stitchType", editStitchType.getText().toString().trim());
        project.put("startDate", editStartDate.getText().toString().trim());
        project.put("endDate", editEndDate.getText().toString().trim());
        project.put("category", category);
        project.put("notes", editProjectNotes.getText().toString().trim());
        project.put("imgUrl", imgUrl);
        project.put("projectType", projectType);
        project.put("imgName", projectId);
        project.put("patternId", patternId);
        project.put("yarnIds", yarnId);

        db.collection("knitters").document(userId).collection("projects").document(projectId).set(project).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (projectType.matches("2")) {
                    db.collection("knitters").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            knitter = documentSnapshot.toObject(Knitters.class);
                            Integer completedProjects = knitter.getCompletedProjects();
                            completedProjects++;
                            db.collection("knitters").document(userId).update("completedProjects", completedProjects);
                        }
                    });
                }
            }
        });
    }

    /**
     * Displays the selected yarn items, that have been chosen at the
     * AddYarnDialogFragment, in a RecyclerView. The ID for the chosen yarn items is stored
     * in a list and by each ID in the list are the yarn items retrieved
     * from the 'yarn' collection.
     */
    private void displayChosenYarn() {
        for (String yarnId : yarnId) {
            db.collection("knitters").document(userId).collection("yarn").whereEqualTo("yarnId", yarnId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                            Yarn yarn = documentSnapshot.toObject(Yarn.class);
                            chosenYarnArraylist.add(yarn);
                        }
                        adapter.notifyDataSetChanged();
                        yarnInProjectRecyclerView.setHasFixedSize(true);
                        yarnInProjectRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        yarnInProjectRecyclerView.setAdapter(adapter);
                    }
                }
            });
        }
    }

    /**
     * Displays the chosen pattern from the AddPatternDialog fragment.
     */
    private void displayChosenPattern() {
        db.collection("knitters").document(userId).collection("patterns").document(patternId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                viewAfterPatternAdded.setText(documentSnapshot.get("patternName").toString());
                viewAfterPatternAdded.setVisibility(View.VISIBLE);
                btnAddPattern.setVisibility(View.GONE);
                editDesigner.setText(documentSnapshot.get("patternDesigner").toString());
                if (documentSnapshot.get("pdfUrl") != null) {
                    patternUrl = documentSnapshot.get("pdfUrl").toString();
                }
            }
        });

    }

    /**
     * Lets user choose a start date.
     */
    private void changeStartDate() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddProjectActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        editStartDate.setText(d + "/" + (m + 1) + "/" + y);
                    }
                },
                // pass y, m d to date picker.
                year, month, day
        );
        datePickerDialog.show();
    }

    /**
     * Lets user choose an end date. Only visible is the project type
     * is chosen to be 'completed'.
     */
    private void changeEndDate() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddProjectActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        editEndDate.setText(d + "/" + (m + 1) + "/" + y);
                    }
                },
                // pass y, m d to date picker.
                year, month, day
        );
        datePickerDialog.show();
    }


    /**
     * Makes the assigned yarn items swipeable to delete the yarn item from the list.
     */
    private void swipeYarnItem() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Yarn yarn = chosenYarnArraylist.get(viewHolder.getAdapterPosition());
                int position = viewHolder.getAdapterPosition();
                chosenYarnArraylist.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                // Undo swipe
                Snackbar.make(yarnInProjectRecyclerView, yarn.getName() + " is deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chosenYarnArraylist.add(position, yarn);
                        adapter.notifyItemInserted(position);
                    }
                }).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // Styling from library
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.error))
                        .addSwipeLeftActionIcon(R.drawable.icon_delete)
                        .addSwipeLeftCornerRadius(0, 20)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(yarnInProjectRecyclerView);
    }

    /**
     * Opens when the user wants to exit the activity, but the
     * project name field is filled.
     */
    private void confirmGoBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProjectActivity.this);
        builder.setCancelable(true);
        builder.setTitle(getResources().getString(R.string.save_changes));
        builder.setMessage(getResources().getString(R.string.dont_save_project));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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
                if (optionsMenu[i].equals(getResources().getString(R.string.take_photo))) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New image");
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    cameraActivityResultLauncher.launch(cameraIntent);
                } else if (optionsMenu[i].equals(getResources().getString(R.string.choose_from_gallery))) {
                    Intent chooseImg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryActivityResultLauncher.launch(chooseImg);
                }
            }
        });
        builder.show();
    }

    /** The source code for the camera functionality is inspired from:
     * Title: Android Capturing Images from Camera or Gallery as Bitmaps using ActivityResultLauncher
     * Author: Asif, H
     * Date: 2022
     * Availability: www.medium.com */

    /**
     * Captures image and displays it in the ImageView object.
     */
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap inputImage = uriToBitmap(imageUri);
                        Bitmap rotated = rotateBitmap(inputImage);
                        imageView.setImageBitmap(rotated);
                        layoutAfterImage.setVisibility(View.VISIBLE);
                        btnAddImage.setVisibility(View.GONE);
                    }
                }
            });

    /**
     * Retrieves image from gallery and displays it in the ImageView object
     */
    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
                        layoutAfterImage.setVisibility(View.VISIBLE);
                        btnAddImage.setVisibility(View.GONE);
                    }
                }
            });


    /**
     * Converts the captured image to bitmap format.
     */
    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedFileUri, "r");
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
        Cursor cur = getContentResolver().query(imageUri, orientationColumn, null, null, null);
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
     * Checks runtime permissions for camera and external media access in Manifest file
     */
    private void requestPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, 112);
        }
    }
}