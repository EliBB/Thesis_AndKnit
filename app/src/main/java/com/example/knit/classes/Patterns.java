package com.example.knit.classes;
import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
/**
 * Object class for retrieving data from the 'patterns' subcollection in the database
 */
public class Patterns {
    private String documentId, patternName, patternDesigner, pdfUrl, category, patternId;
    private Boolean isClicked;
    public Patterns(){
    }

    public Patterns(String documentId, String patternName, String patternDesigner, String pdfUrl, String category, String patternId, Boolean isClicked){
        this.documentId = documentId;
        this.patternName = patternName;
        this.patternDesigner = patternDesigner;
        this.pdfUrl = pdfUrl;
        this.category = category;
        this.patternId = patternId;
        this.isClicked = isClicked;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName){
        this.patternName = patternName;
    }

    public String getPatternDesigner(){
        return patternDesigner;
    }

    public void setPatternDesigner(String patternDesigner){
        this.patternDesigner = patternDesigner;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPatternId() {
        return patternId;
    }

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }

    public Boolean getIsClicked() {
        return isClicked;
    }

    public void setIsClicked(Boolean clicked) {
        isClicked = clicked;
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
