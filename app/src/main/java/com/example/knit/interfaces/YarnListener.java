package com.example.knit.interfaces;

import androidx.cardview.widget.CardView;

import com.example.knit.classes.Yarn;
/** Provides a callback method for the yarn items in the Storage screen. */
public interface YarnListener {
    void onItemClick(Yarn yarn);
}
