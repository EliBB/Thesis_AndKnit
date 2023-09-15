package com.example.knit.classes;

import java.util.List;
/**
 * Object class for retrieving data from the 'knitters' collection in the database
 */
public class Knitters {
    private String dateCreated, name, id, email, bust, waist, hips, handLen, handCir, footLen, footCir, notes;
    private Integer completedProjects, skeinsUsed, gramsUsed, metersKnitted;

    public Knitters(){
        // Empty constructor. Required from Firebase.
    }

    public Knitters(Integer completedProjects, Integer skeinsUsed, Integer gramsUsed, Integer metersKnitted, String name, String dateCreated, String id, String email, String bust, String waist, String hips, String handLen, String handCir, String footLen, String footCir, String notes){
        this.name = name;
        this.dateCreated = dateCreated;
        this.id = id;
        this.email = email;
        this.bust = bust;
        this.waist = waist;
        this.hips = hips;
        this.handLen = handLen;
        this.handCir = handCir;
        this.footLen = footLen;
        this.footCir = footCir;
        this.notes = notes;
        this.completedProjects = completedProjects;
        this.skeinsUsed = skeinsUsed;
        this.gramsUsed = gramsUsed;
        this.metersKnitted = metersKnitted;
    }

    // Getter methods for all variables
    public String getBust(){
        return bust;
    }
    public void setBust(String bust){
        this.bust = bust;
    }
    public String getWaist(){
        return waist;
    }
    public void setWaist(String waist){
        this.waist = waist;
    }
    public String getHips(){
        return hips;
    }
    public void setHips(String hips){
        this.hips = hips;
    }
    public String getHandLen(){
        return handLen;
    }
    public void setHandLen(String handLen){
        this.handLen = handLen;
    }
    public String getHandCir(){
        return handCir;
    }
    public void setHandCir(String handCir){
        this.handCir = handCir;
    }
    public String getFootLen(){
        return footLen;
    }
    public void setFootLen(String footLen){
        this.footLen = footLen;
    }
    public String getFootCir(){
        return footCir;
    }
    public void setFootCir(String footCir){
        this.footCir = footCir;
    }

    public Integer getCompletedProjects() {
        return completedProjects;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCompletedProjects(Integer completedProjects) {
        this.completedProjects = completedProjects;
    }

    public Integer getSkeinsUsed() {
        return skeinsUsed;
    }

    public void setSkeinsUsed(Integer skeinsUsed) {
        this.skeinsUsed = skeinsUsed;
    }

    public Integer getGramsUsed() {
        return gramsUsed;
    }

    public void setGramsUsed(Integer gramsUsed) {
        this.gramsUsed = gramsUsed;
    }

    public Integer getMetersKnitted() {
        return metersKnitted;
    }

    public void setMetersKnitted(Integer metersKnitted) {
        this.metersKnitted = metersKnitted;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

