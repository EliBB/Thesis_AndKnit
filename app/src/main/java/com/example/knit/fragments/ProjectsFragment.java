package com.example.knit.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.knit.activities.AddProjectActivity;
import com.example.knit.adapters.ViewPagerProjectsAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import com.example.knit.R;

/**
 * Holds the layout and data for the 'Projects' item in the navigation bar.
 * The active and completed projects are displayed in a tabsection and a new project can be
 * added via the FAB.
 */
public class ProjectsFragment extends Fragment {
    //Objects
    private static final String TAG = "Projects";

    // Widgets
    private ExtendedFloatingActionButton btnAddProject;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerProjectsAdapter viewPagerProjectsAdapter;

    public ProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_projects, container, false);
        // FAB
        btnAddProject = v.findViewById(R.id.projects_btn_add);

        //Tab section
        tabLayout = v.findViewById(R.id.projects_tab_layout);
        viewPager = v.findViewById(R.id.projects_view_pager);
        viewPagerProjectsAdapter = new ViewPagerProjectsAdapter(getActivity());
        viewPager.setAdapter(viewPagerProjectsAdapter);

        // Display tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Swipe functionality
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });


        // Click listener for adding new project
        btnAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddProjectActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), btnAddProject, "transition_fab");
                startActivity(intent, options.toBundle());
            }
        });

        return v;
    }
}