package com.example.knit.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.knit.R;
import com.example.knit.adapters.ViewPagerStorageAdapter;
import com.google.android.material.tabs.TabLayout;

/**
 * Holds the layout and data for the 'Storage' item in the navigation bar.
 * The yarn, needles, gauge swatches, and patterns are displayed in a tabsection.
 */
public class StorageFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerStorageAdapter viewPagerStorageAdapter;

    public StorageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_storage, container, false);
        // Tab section
        tabLayout = v.findViewById(R.id.storage_tab_layout);
        viewPager = v.findViewById(R.id.storage_view_pager);
        viewPagerStorageAdapter = new ViewPagerStorageAdapter(getActivity());
        viewPager.setAdapter(viewPagerStorageAdapter);

        //Display tabs
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

        return v;
    }
}