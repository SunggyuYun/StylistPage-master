package com.example.angela.stylistpage.models;

public class StylistModel {

    private String stylistName;
    private float stylistRating;
    private int caseFinished;
    private String stylistIcon;

    public String getStylistName(){
        return stylistName;
    }
    public void setStylistName(String stylistName){
        this.stylistName = stylistName;
    }

    public float getStylistRating() {
        return stylistRating;
    }
    public void setStylistRating(float stylistRating) {
        this.stylistRating = stylistRating;
    }

    public int getCaseFinished() { return caseFinished;}
    public void setCaseFinished(int caseFinished) {
        this.caseFinished = caseFinished;
    }

    public String getStylistIcon(){ return stylistIcon;}
    public void setStylistIcon(String stylistIcon) { this.stylistIcon = stylistIcon; }

}
