package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

@Data
public class LDAView {
    private int diagnosisId;
    private String diagnosisName;
    private List<Integer> drugIDList;
    private List<String> drugNameList;
    private List<Double> drugPhiList;

    private static DecimalFormat format = new DecimalFormat("0.00%");

    public LDAView(int diagnosisId, String diagnosisName, int drugSize) {
        this.diagnosisId = diagnosisId;
        this.diagnosisName = diagnosisName;
        drugIDList = new ArrayList<>(drugSize);
        drugNameList = new ArrayList<>(drugSize);
        drugPhiList = new ArrayList<>(drugSize);
    }

    public int getDiagnosisId() {
        return diagnosisId;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }

    public List<Integer> getDrugIDList() {
        return drugIDList;
    }

    public List<String> getDrugNameList() {
        return drugNameList;
    }

    public List<Double> getDrugPhiList() {
        return drugPhiList;
    }

    public void addDrug(int drugId, String drugName, double drugPhi){
        drugIDList.add(drugId);
        drugNameList.add(drugName);
        drugPhiList.add(drugPhi);
    }

    public String getFormatDrugPhi(int index){
        return format.format(drugPhiList.get(index));
    }
}
