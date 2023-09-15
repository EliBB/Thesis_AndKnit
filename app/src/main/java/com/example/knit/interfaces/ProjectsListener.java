package com.example.knit.interfaces;

import com.example.knit.classes.Projects;
/** Provides a callback method for the project items in the Projects screen. */

public interface ProjectsListener {
    void onItemClick(Projects projects);
}
