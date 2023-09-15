package com.example.knit.classes;

import com.google.firebase.firestore.DocumentId;
/**
 * Object class for retrieving data from the 'persons' subcollection in the database
 */
public class Persons {
    private String name, bust, waist, hips, handCir, handLen, footCir, footLen, notes, documentId;
    public Persons() {
        // Empty constructor required for Firebase
    }
    public Persons(String name, String bust, String footCir, String footLen, String handCir, String handLen, String hips, String waist, String notes, String documentId) {
        this.name = name;
        this.bust = bust;
        this.footCir = footCir;
        this.footLen = footLen;
        this.handCir = handCir;
        this.handLen = handLen;
        this.hips = hips;
        this.waist = waist;
        this.notes = notes;
        this.documentId = documentId;
    }
    public String getName() {return name;}
    public void setName(String name) {
        this.name = name;
    }
    public String getBust() {
        return bust;
    }
    public void setBust(String bust) {
        this.bust = bust;
    }
    public String getWaist() {
        return waist;
    }
    public void setWaist(String waist) {
        this.waist = waist;
    }
    public String getHips() {
        return hips;
    }
    public void setHips(String hips) {
        this.hips = hips;
    }
    public String getHandLen() {
        return handLen;
    }
    public void setHandLen(String handLen) {
        this.handLen = handLen;
    }
    public String getHandCir() {
        return handCir;
    }
    public void setHandCir(String handCir) {
        this.handCir = handCir;
    }
    public String getFootLen() {
        return footLen;
    }
    public void setFootLen(String footLen) {
        this.footLen = footLen;
    }
    public String getFootCir() {
        return footCir;
    }
    public void setFootCir(String footCir) {
        this.footCir = footCir;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
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
