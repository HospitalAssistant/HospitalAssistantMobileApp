package com.fmi.fcmtestapp;

public class PatientIntentRecyclerViewItem {

    private String patientIntentName;
    private String patientIntentHidden;
    private int patientIntentImageId;

    public PatientIntentRecyclerViewItem(String patientIntentHidden, String patientIntentName, int patientIntentImageId) {
        this.patientIntentHidden = patientIntentHidden;
        this.patientIntentName = patientIntentName;
        this.patientIntentImageId = patientIntentImageId;
    }

    public String getPatientIntentHidden() {
        return patientIntentHidden;
    }

    public void setPatientIntentHidden(String patientIntentHidden) {
        this.patientIntentHidden = patientIntentHidden;
    }

    public String getPatientIntentName() {
        return patientIntentName;
    }

    public void setPatientIntentName(String patientIntentName) {
        this.patientIntentName = patientIntentName;
    }

    public int getPatientIntentImageId() {
        return patientIntentImageId;
    }

    public void setPatientIntentImageId(int patientIntentImageId) {
        this.patientIntentImageId = patientIntentImageId;
    }
}
