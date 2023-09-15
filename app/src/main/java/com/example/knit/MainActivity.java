package com.example.knit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.knit.activities.AddGaugeSwatchActivity;
import com.example.knit.activities.AddNeedlesActivity;
import com.example.knit.activities.AddPatternActivity;
import com.example.knit.activities.AddPersonActivity;
import com.example.knit.activities.AddProjectActivity;
import com.example.knit.activities.AddYarnActivity;
import com.example.knit.activities.VoiceCommandActivity;
import com.example.knit.fragments.ProfileFragment;
import com.example.knit.fragments.ProjectsFragment;
import com.example.knit.fragments.StorageFragment;
import com.example.knit.fragments.ToolsFragment;
import com.example.knit.fragments.VideosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Is the main landing page after signing in or registering to the &Knit app.
 * This activity holds the bottom navigation bar and the fragment that are access by it.
 * Any activity that is started from within a fragment is defined here.
 */
public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_NavigationView);
        bottomNavigationView.setOnItemSelectedListener(navbarListener);

        // First fragment to display
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProjectsFragment()).commit();
    }

    private final NavigationBarView.OnItemSelectedListener navbarListener = item -> {
        Fragment displayFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.projects) {
            displayFragment = new ProjectsFragment();
        } else if (itemId == R.id.storage) {
            displayFragment = new StorageFragment();
        } else if (itemId == R.id.tools) {
            displayFragment = new ToolsFragment();
        } else if (itemId == R.id.videos) {
            displayFragment = new VideosFragment();
        } else if (itemId == R.id.profile) {
            displayFragment = new ProfileFragment();
        }

        // Replace fragments
        if (displayFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, displayFragment).commit();
        }
        return true;
    };
}