package com.example.knit.interfaces;

import com.example.knit.classes.Counters;
/** Provides callback methods for the counter items in the Tools screen
 * and for the counter items in specific projects */
public interface CountersListener {
    void openMicrophone(Counters counters);
    void increment(Counters counters);
    void decrement(Counters counters);
    void saveName (Counters counters);
    void delete (Counters counters);
}
