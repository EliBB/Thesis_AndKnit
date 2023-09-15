package com.example.knit.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.knit.fragments.StorageGaugeSwatchesFragment;
import com.example.knit.fragments.StorageNeedlesFragment;
import com.example.knit.fragments.StoragePatternsFragment;
import com.example.knit.fragments.StorageYarnFragment;
/**
 * Binds the the data for the yarn, needles, gauge swatches, and patterns fragments
 * with their respective layouts.
 */
public class ViewPagerStorageAdapter extends FragmentStateAdapter {
    public ViewPagerStorageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new StorageYarnFragment();
            case 1:
                return new StorageNeedlesFragment();
            case 2:
                return new StorageGaugeSwatchesFragment();
            case 3:
                return new StoragePatternsFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
