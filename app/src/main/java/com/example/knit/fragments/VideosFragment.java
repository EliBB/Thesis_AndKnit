package com.example.knit.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.knit.R;
/**
 * Holds the layout and data for the 'Videos' item in the navigation bar.
 */
public class VideosFragment extends Fragment {
    private CardView basicCastOn, italianCastOn, judysMagicCastOn, mOneR, mOneL, ssk, kTwoTog, basicBindOff, italianBindOff, germanShortRows, sewElasticThread, weaveInEnds;

    public VideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_videos, container, false);
        // Initialize widgets
        basicCastOn = v.findViewById(R.id.basic_cast_on);
        italianCastOn = v.findViewById(R.id.italian_cast_on);
        judysMagicCastOn = v.findViewById(R.id.judys_magic_cast_on);
        mOneR = v.findViewById(R.id.m_one_r);
        mOneL = v.findViewById(R.id.m_one_l);
        ssk = v.findViewById(R.id.ssk);
        kTwoTog = v.findViewById(R.id.k2tog);
        basicBindOff = v.findViewById(R.id.basic_bind_off);
        italianBindOff = v.findViewById(R.id.italian_bind_off);
        germanShortRows = v.findViewById(R.id.german_short_rows);
        sewElasticThread = v.findViewById(R.id.sew_elastic_thread);
        weaveInEnds = v.findViewById(R.id.weave_in_ends);

        // Click listeners for each video. Each video has a unique URL which is send
        // to the VideosItemFragment when clicked.
        basicCastOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "itPw3_F8oFU");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        italianCastOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "GJgglwBGqgk");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        judysMagicCastOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "rAEDPfEpwv8");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        mOneR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "RgSVoTGzxdM");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        mOneL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "hG3cM74RNaY");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            }
        });

        ssk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "PIqAC_3TBlI");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            }
        });

        kTwoTog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "FVP0da99E1M");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            }
        });

        basicBindOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "Z1tqjz9DOn8");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            }
        });

        italianBindOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "r_FahQrBIk0");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            }
        });

        germanShortRows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "z-E3YSHPQYs");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            }
        });

        sewElasticThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "J9wOGU1o9FM");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            }
        });

        weaveInEnds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
                Bundle mBundle = new Bundle();
                mBundle.putString("videoUrl", "7doio1qv7MM");
                VideosItemFragment fragment = new VideosItemFragment();
                fragment.setArguments(mBundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            }
        });

        return v;
    }
}