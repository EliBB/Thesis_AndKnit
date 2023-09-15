package com.example.knit.classes;

import com.google.firebase.firestore.DocumentId;

/**
 * Object class for retrieving data from the 'counters' and 'project counters'
 * subcollections in the database
 */
public class Counters {
    private String documentId, name;
    private Integer count;
    private Float added;

    public Counters() {
    }

    public Counters(Integer count, String documentId, String name, Float added) {
        this.count = count;
        this.documentId = documentId;
        this.name = name;
        this.added = added;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getAdded() {
        return added;
    }

    public void setAdded(Float added) {
        this.added = added;
    }

    @DocumentId
    public String getDocumentId() {
        return documentId;
    }

    @DocumentId
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}

