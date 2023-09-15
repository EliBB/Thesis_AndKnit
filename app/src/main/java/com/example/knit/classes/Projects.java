package com.example.knit.classes;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
/**
 * Object class for retrieving data from the 'projects' subcollection in the database
 */
public class Projects {
    private String category, designer, endDate, imgName, imgUrl, needleSize, notes, patternId, projectName, projectType, rows, size, startDate, stitchType, sts, documentId;
    private ArrayList<String> yarnIds;
    public Projects() {
        // Empty constructor required for Firebase
    }
    public Projects(ArrayList<String> yarnIds, String category, String designer, String endDate, String imgPath, String imgUrl, String needleSize, String notes, String patternId, String projectName, String projectType, String rows, String size, String startDate, String stitchType, String sts) {
        this.category = category;
        this.designer = designer;
        this.endDate = endDate;
        this.imgName = imgPath;
        this.imgUrl = imgUrl;
        this.needleSize = needleSize;
        this.notes = notes;
        this.patternId = patternId;
        this.projectName = projectName;
        this.projectType = projectType;
        this.rows = rows;
        this.size = size;
        this.startDate = startDate;
        this.stitchType = stitchType;
        this.sts = sts;
        this.yarnIds = yarnIds;
    }

    // Getter methods for all variables

    public ArrayList<String> getYarnIds() {
        return yarnIds;
    }

    public void setYarnIds(ArrayList<String> yarnIds) {
        this.yarnIds = yarnIds;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesigner() {
        return designer;
    }

    public void setDesigner(String designer) {
        this.designer = designer;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String img) {
        this.imgUrl = img;
    }

    public String getNeedleSize() {
        return needleSize;
    }

    public void setNeedleSize(String needleSize) {
        this.needleSize = needleSize;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPatternId() {
        return patternId;
    }

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStitchType() {
        return stitchType;
    }

    public void setStitchType(String stitchType) {
        this.stitchType = stitchType;
    }

    public String getSts() {
        return sts;
    }

    public void setSts(String sts) {
        this.sts = sts;
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
