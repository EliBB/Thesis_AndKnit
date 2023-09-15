package com.example.knit.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.knit.fragments.ProjectsActiveFragment;
import com.example.knit.fragments.ProjectsCompletedFragment;
/**
 * Binds the the data for the active and completed projects fragments with
 * their respective layouts.
 */
public class ViewPagerProjectsAdapter extends FragmentStateAdapter {
    public ViewPagerProjectsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProjectsActiveFragment();
            case 1:
                return new ProjectsCompletedFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
