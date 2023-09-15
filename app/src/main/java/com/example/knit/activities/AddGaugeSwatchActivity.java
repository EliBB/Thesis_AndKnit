package com.example.knit.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knit.R;
import com.example.knit.adapters.YarnInProjectAdapter;
import com.example.knit.classes.Yarn;
import com.example.knit.dialogs.AddYarnDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lets the user add a new gauge swatch to their stash. The input data is stored
 * in the 'gaugeSwatches' collection in Firebase Cloud Firestore.
 * The image is stored in Firebase Storage.
 */

public class AddGaugeSwatchActivity extends AppCompatActivity implements AddYarnDialogFragment.InputListener {
    // Objects
    private static final String TAG = "Add gauge swatch: ";
    private String userId, imgUrl, imgName, sts, rows, needleSize, stitchType, notes;
    private Uri imageUri;
    public ArrayList<String> yarnId;
    private ArrayList<Yarn> chosenYarnArraylist;
    private RecyclerView yarnInGaugeSwatchRecyclerView;

    // Widgets
    private TextView actionbarTitle;
    private EditText editSts, editRows, editNeedleSize, editStsType, editNotes;
    private Button btnSave, btnAddYarn;
    private FloatingActionButton btnAddImageAfter;
    private ImageView btnAddImage, imageView;
    private Toolbar actionbar;

    // Layout
    private CoordinatorLayout layoutAfterImage;

    // Dialogs
    private AddYarnDialogFragment addYarnDialogFragment;

    // Adapter
    private YarnInProjectAdapter adapter;

    // Firebase Firestore, Firebase Authentication & Firebase Storage
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gauge_swatch);
        // Initialize Firebase Authentication, Firestore, Storage, and userId
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Action bar
        actionbar = findViewById(R.id.action_bar);
        actionbarTitle = findViewById(R.id.actionbar_title);
        actionbarTitle.setText(R.string.add_gauge);
        actionbar.setNavigationIcon(R.drawable.icon_close);
        btnSave = findViewById(R.id.actionbar_save);
        btnSave.setVisibility(View.VISIBLE);

        // Initialize widgets
        // Image
        imageView = findViewById(R.id.add_gauge_swatch_imageView);
        layoutAfterImage = findViewById(R.id.add_gauge_swatch_layout_after_image);

        // Edit Texts
        editSts = findViewById(R.id.add_gauge_swatch_edit_stitches);
        editRows = findViewById(R.id.add_gauge_swatch_edit_rows);
        editNeedleSize = findViewById(R.id.add_gauge_swatch_edit_needle_size);
        editStsType = findViewById(R.id.add_gauge_swatch_edit_stitch_type);
        editNotes = findViewById(R.id.add_gauge_swatch_edit_notes);

        // Buttons
        btnAddImage = findViewById(R.id.add_gauge_swatch_btn_add_image);
        btnAddImageAfter = findViewById(R.id.add_gauge_swatch_btn_add_image_after);
        btnAddYarn = findViewById(R.id.add_gauge_swatch_btn_add_yarn);

        // Recycler view
        yarnInGaugeSwatchRecyclerView = findViewById(R.id.add_gauge_swatch_recycler_view_yarn);

        // Initialize lists and adapter for recycler view
        yarnId = new ArrayList<>();
        chosenYarnArraylist = new ArrayList<>();
        adapter = new YarnInProjectAdapter(chosenYarnArraylist, getApplicationContext());

        // Click listener to navigate to previous screen
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Click listener to save input
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageToDatabase();
                onBackPressed();
            }
        });

        // Click listener to add image
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
                chooseImage(AddGaugeSwatchActivity.this);
            }
        });

        // Click listener to add another image
        btnAddImageAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(AddGaugeSwatchActivity.this);
            }
        });

        // Click listener to open dialog fragment to assign yarn
        btnAddYarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addYarnDialogFragment = new AddYarnDialogFragment();
                addYarnDialogFragment.show(getSupportFragmentManager(), "Add yarn dialog");
            }
        });
    }

    /**
     * Uploads assigned image to Firebase Storage. The image name is given by the current
     * time. If the upload succeeds or if no image has been assigned the
     * addGaugeSwatchToDatabase() is called.
     */
    private void addImageToDatabase() {
        imgName = String.valueOf(System.currentTimeMillis());

        if (imageUri != null) {
            String imgPath = "users/" + userId + "/gaugeSwatches/" + imgName;
            storageReference.child(imgPath).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(imgPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgUrl = uri.toString();
                            addGaugeSwatchToDatabase();
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
            addGaugeSwatchToDatabase();
        }
    }

    /**
     * Uploads the user input to the 'gaugeSwatch' collection in Cloud Firestore.
     **/
    private void addGaugeSwatchToDatabase() {
        sts = editSts.getText().toString().trim();
        rows = editRows.getText().toString().trim();
        needleSize = editNeedleSize.getText().toString().trim();
        stitchType = editStsType.getText().toString().trim();
        notes = editNotes.getText().toString().trim();

        Map<String, Object> gaugeSwatch = new HashMap<>();
        gaugeSwatch.put("stitches", sts);
        gaugeSwatch.put("rows", rows);
        gaugeSwatch.put("needleSize", needleSize);
        gaugeSwatch.put("stitchType", stitchType);
        gaugeSwatch.put("notes", notes);
        gaugeSwatch.put("imgUrl", imgUrl);
        gaugeSwatch.put("imgName", imgName);
        gaugeSwatch.put("yarnIds", yarnId);

        db.collection("knitters").document(userId).collection("gauge swatches").document(imgName).set(gaugeSwatch).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Gauge swatch uploaded successfully");
            }
        });
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
                    values.put(MediaStore.Images.Media.TITLE, "Image from camera");
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

    /**
     * Sends data between the activity and dialog fragment for selecting yarn
     */
    @Override
    public void sendYarnInput(ArrayList<String> inputList) {
        yarnId = inputList;
        displayChosenYarn();
    }

    /**
     * Displays the selected yarn items that have been chosen at the
     * AddYarnDialogFragment in a RecyclerView. The ID's for the chosen yarn item are stored
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
                        yarnInGaugeSwatchRecyclerView.setHasFixedSize(true);
                        yarnInGaugeSwatchRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        yarnInGaugeSwatchRecyclerView.setAdapter(adapter);
                    }
                }
            });
        }
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
                        Bitmap rotatedImage = rotateBitmap(inputImage);
                        imageView.setImageBitmap(rotatedImage);
                        btnAddImage.setVisibility(View.GONE);
                        layoutAfterImage.setVisibility(View.VISIBLE);
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
                        btnAddImage.setVisibility(View.GONE);
                        layoutAfterImage.setVisibility(View.VISIBLE);
                    }
                }
            });

    /**
     * Converts the captured image to bitmap format.
     */
    private Bitmap uriToBitmap(Uri selectedImageUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
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
            requestPermissions(permission, 1);
        }
    }
}