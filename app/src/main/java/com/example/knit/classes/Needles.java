package com.example.knit.classes;

import com.google.firebase.firestore.DocumentId;

import java.util.List;
/**
 * Object class for retrieving data from the 'needles' subcollection in the database
 */
public class Needles {

    private String documentId, type, material, notes, needleLen, sizes, wireLen;

    public Needles(){}

    public Needles(String documentId, String needleType, String needleMaterial, String notes, String needleLen, String needleSize, String wireLen){
        this.type = needleType;
        this.material = needleMaterial;
        this.notes = notes;
        this.documentId = documentId;
        this.sizes = needleSize;
        this.needleLen = needleLen;
        this.wireLen = wireLen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getNeedleLen() {
        return needleLen;
    }
    public void setNeedleLen(String needleLen) {
        this.needleLen = needleLen;
    }

    public String getWireLen() {
        return wireLen;
    }

    public void setWireLen(String wireLen) {
        this.wireLen = wireLen;
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
