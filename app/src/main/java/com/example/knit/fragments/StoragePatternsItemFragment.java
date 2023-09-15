package com.example.knit.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.knit.R;
import com.example.knit.adapters.GalleryAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
/**
 * Displays a pattern in PDF-format or in images. PDF's are displays in a WebView object
 * and images are displayed in a recyclerview.
 */
public class StoragePatternsItemFragment extends Fragment {
    // Objects
    private static final String TAG = "Patterns";
    private String userId, pdfUrl, filePath, imagesPath, patternUrl;
    private ArrayList<Uri> imageList;

    // Widgets
    private Toolbar actionbar;
    private WebView webView;
    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private ProgressBar progressBar;

    // Firebase Firestore, Firebase Authentication & Firebase Storage
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public StoragePatternsItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_storage_patterns_item, container, false);
        // Initialize widgets
        actionbar = v.findViewById(R.id.storage_patterns_item_action_bar);
        webView = v.findViewById(R.id.storage_patterns_item_web_view);
        recyclerView = v.findViewById(R.id.storage_patterns_item_recyclerView);
        progressBar = v.findViewById(R.id.storage_patterns_item_progress_bar);

        // Action bar
        actionbar.setNavigationIcon(R.drawable.icon_arrow_back);

        // Click listener for navigating to previous screen
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        // Get data from previous fragment
        Bundle bundle = getArguments();
        patternUrl = bundle.getString("patternUrl");
        imagesPath = bundle.getString("imagesPath");
        Log.d(TAG, "Item PDF " + patternUrl);
        Log.d(TAG, "Item Images " + imagesPath);

        // Initialize list, adapter & recycler view to display pattern in images
        imageList = new ArrayList<>();
        adapter = new GalleryAdapter(imageList);

        displayPattern();

        return v;
    }

    /**
     * Retrieves the pattern from the database by the data that was send via the
     * bundle object.
     */
    private void displayPattern(){
        // If the pattern is a pdf
        if (patternUrl != null) {
            Log.d(TAG, "Shows PDF");
            webView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);

            webView.setWebViewClient(new WebViewClient());
            pdfUrl = "";
            try {
                pdfUrl = URLEncoder.encode(patternUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfUrl);
            progressBar.setVisibility(View.GONE);
        }

        // if the pattern consists of images
        else {
            Log.d(TAG, "Shows images");
            webView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            filePath = "users/" + userId + "/patterns/" + imagesPath + " /";
            storageReference.child(filePath).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference image : listResult.getItems()){
                        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageList.add(uri);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setHasFixedSize(true);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }
    }
}