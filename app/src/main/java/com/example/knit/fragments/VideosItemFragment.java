package com.example.knit.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knit.R;

/**
 * Provides the functionality of playing a video from Youtube in a WebView. The unique URL
 * of the video that is displayed is retrieved by a Bundle instance.
 */
public class VideosItemFragment extends Fragment {
    private Toolbar actionbar;

    public VideosItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_videos_item, container, false);
        // Action bar
        actionbar = v.findViewById(R.id.action_bar);
        actionbar.setNavigationIcon(R.drawable.icon_arrow_back);

        // Get data from previous fragment
        Bundle bundle = getArguments();
        String videoId = bundle.getString("videoUrl");

        // Defines the settings of the webview
        WebView webView = v.findViewById(R.id.item_video_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\" allowfullscreen></iframe>", "text/html", "utf-8");

        // Click listener for navigating to previous screen
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        return v;
    }
}