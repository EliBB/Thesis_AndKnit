package com.example.knit.classes;
import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
/**
 * Object class for retrieving data from the 'gauge swatches' subcollection in the database
 */
public class GaugeSwatches {
    private String documentId, stitches, rows, needleSize, stitchType, notes, imgUrl, imgName;
    private ArrayList<String> yarnIds;
    public GaugeSwatches(){}
    public GaugeSwatches(ArrayList<String> yarnIds, String documentId, String stitches, String rows, String needleSize, String stitchType, String notes, String imgUrl, String imgName){
        this.documentId = documentId;
        this.stitches = stitches;
        this.rows = rows;
        this.needleSize = needleSize;
        this.stitchType = stitchType;
        this.notes = notes;
        this.imgUrl = imgUrl;
        this.imgName = imgName;
        this.yarnIds = yarnIds;
    }

    public ArrayList<String> getYarnIds() {
        return yarnIds;
    }

    public void setYarnIds(ArrayList<String> yarnIds) {
        this.yarnIds = yarnIds;
    }

    public String getStitches(){return stitches;}

    public void setStitches(String stitches) {
        this.stitches = stitches;
    }

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public String getNeedleSize() {
        return needleSize;
    }

    public void setNeedleSize(String needleSize) {
        this.needleSize = needleSize;
    }

    public String getStitchType() {
        return stitchType;
    }

    public void setStitchType(String stitchType) {
        this.stitchType = stitchType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String img) {
        this.imgUrl = img;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
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
