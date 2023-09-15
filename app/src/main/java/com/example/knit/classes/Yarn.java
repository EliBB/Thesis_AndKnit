package com.example.knit.classes;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
/**
 * Object class for retrieving data from the 'yarn' subcollection in the database
 */
public class Yarn {

    private String name, brand, color, lot, notes, skeinWeight, documentId, yarnId;
    private Integer skeinLength, amountGrams, amountMeters, amountSkeins, yarnLeftover;
    private Boolean isClicked;
    public Yarn(){
        // Empty constructor required for Firebase
    }
    public Yarn(String name, String brand, String color, String lot, String skeinWeight, Integer skeinLength, Integer amountSkeins, Integer amountGrams, Integer amountMeters, String notes, String documentId, Boolean isClicked, String yarnId, Integer yarnLeftover){
        this.name = name;
        this.brand = brand;
        this.color = color;
        this.lot = lot;
        this.skeinWeight = skeinWeight;
        this.skeinLength = skeinLength;
        this.amountSkeins = amountSkeins;
        this.amountGrams = amountGrams;
        this.amountMeters = amountMeters;
        this.notes = notes;
        this.documentId = documentId;
        this.isClicked = isClicked;
        this.yarnId = yarnId;
        this.yarnLeftover = yarnLeftover;
    }

    public String getName(){
        return name;
    }
    public void setName(){
        this.name = name;
    }

    public String getBrand(){
        return brand;
    }
    public void setBrand(){
        this.brand = brand;
    }

    public String getColor(){
        return color;
    }
    public void setColor(){
        this.color = color;
    }

    public String getLot(){
        return lot;
    }
    public void setLot(){
        this.lot = lot;
    }

    public String getSkeinWeight(){
        return skeinWeight;
    }
    public void setSkeinWeight(){
        this.skeinWeight = skeinWeight;
    }
    public Integer getSkeinLength(){
        return skeinLength;
    }
    public void setSkeinLength(){
        this.skeinLength = skeinLength;
    }

    public Integer getAmountSkeins(){
        return amountSkeins;
    }
    public void setAmountSkeins(){
        this.amountSkeins = amountSkeins;
    }

    public Integer getAmountGrams() {
        return amountGrams;
    }

    public void setAmountGrams() {
        this.amountGrams = amountGrams;
    }

    public Integer getAmountMeters() {
        return amountMeters;
    }

    public void setAmountMeters() {
        this.amountMeters = amountMeters;
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

    public Boolean getClicked() {
        return isClicked;
    }
    public void setClicked(Boolean clicked) {
        isClicked = clicked;
    }

    public String getYarnId() {
        return yarnId;
    }

    public void setYarnId(String yarnId) {
        this.yarnId = yarnId;
    }

    public Integer getYarnLeftover() {
        return yarnLeftover;
    }

    public void setYarnLeftover(Integer yarnLeftover) {
        this.yarnLeftover = yarnLeftover;
    }
}
