package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;

@Data
public class PrescriptionView {
    private int patientId;
    private int age;
    private int diagnosis1;
    private int diagnosis2;
    private int diagnosis3;
    private int diagnosis4;
    private DrugDetail drug1;
    private DrugDetail drug2;
    private DrugDetail drug3;
    private DrugDetail drug4;
    private DrugDetail drug5;
    private DrugDetail drug6;
    private DrugDetail drug7;
    private DrugDetail drug8;
    private DrugDetail drug9;
    private DrugDetail drug10;

    public PrescriptionView(int patientId, int age, int diagnosis1, int diagnosis2, int diagnosis3, int diagnosis4, DrugDetail drug1, DrugDetail drug2, DrugDetail drug3, DrugDetail drug4, DrugDetail drug5, DrugDetail drug6, DrugDetail drug7, DrugDetail drug8, DrugDetail drug9, DrugDetail drug10) {
        this.patientId = patientId;
        this.age = age;
        this.diagnosis1 = diagnosis1;
        this.diagnosis2 = diagnosis2;
        this.diagnosis3 = diagnosis3;
        this.diagnosis4 = diagnosis4;
        this.drug1 = drug1;
        this.drug2 = drug2;
        this.drug3 = drug3;
        this.drug4 = drug4;
        this.drug5 = drug5;
        this.drug6 = drug6;
        this.drug7 = drug7;
        this.drug8 = drug8;
        this.drug9 = drug9;
        this.drug10 = drug10;
    }

    public int getPatientId() {
        return patientId;
    }

    public PrescriptionView setPatientId(int patientId) {
        this.patientId = patientId;
        return this;
    }

    public int getAge() {
        return age;
    }

    public PrescriptionView setAge(int age) {
        this.age = age;
        return this;
    }

    public int getDiagnosis1() {
        return diagnosis1;
    }

    public PrescriptionView setDiagnosis1(int diagnosis1) {
        this.diagnosis1 = diagnosis1;
        return this;
    }

    public int getDiagnosis2() {
        return diagnosis2;
    }

    public PrescriptionView setDiagnosis2(int diagnosis2) {
        this.diagnosis2 = diagnosis2;
        return this;
    }

    public int getDiagnosis3() {
        return diagnosis3;
    }

    public PrescriptionView setDiagnosis3(int diagnosis3) {
        this.diagnosis3 = diagnosis3;
        return this;
    }

    public int getDiagnosis4() {
        return diagnosis4;
    }

    public PrescriptionView setDiagnosis4(int diagnosis4) {
        this.diagnosis4 = diagnosis4;
        return this;
    }

    public DrugDetail getDrug1() {
        return drug1;
    }

    public PrescriptionView setDrug1(DrugDetail drug1) {
        this.drug1 = drug1;
        return this;
    }

    public DrugDetail getDrug2() {
        return drug2;
    }

    public PrescriptionView setDrug2(DrugDetail drug2) {
        this.drug2 = drug2;
        return this;
    }

    public DrugDetail getDrug3() {
        return drug3;
    }

    public PrescriptionView setDrug3(DrugDetail drug3) {
        this.drug3 = drug3;
        return this;
    }

    public DrugDetail getDrug4() {
        return drug4;
    }

    public PrescriptionView setDrug4(DrugDetail drug4) {
        this.drug4 = drug4;
        return this;
    }

    public DrugDetail getDrug5() {
        return drug5;
    }

    public PrescriptionView setDrug5(DrugDetail drug5) {
        this.drug5 = drug5;
        return this;
    }

    public DrugDetail getDrug6() {
        return drug6;
    }

    public PrescriptionView setDrug6(DrugDetail drug6) {
        this.drug6 = drug6;
        return this;
    }

    public DrugDetail getDrug7() {
        return drug7;
    }

    public PrescriptionView setDrug7(DrugDetail drug7) {
        this.drug7 = drug7;
        return this;
    }

    public DrugDetail getDrug8() {
        return drug8;
    }

    public PrescriptionView setDrug8(DrugDetail drug8) {
        this.drug8 = drug8;
        return this;
    }

    public DrugDetail getDrug9() {
        return drug9;
    }

    public PrescriptionView setDrug9(DrugDetail drug9) {
        this.drug9 = drug9;
        return this;
    }

    public DrugDetail getDrug10() {
        return drug10;
    }

    public PrescriptionView setDrug10(DrugDetail drug10) {
        this.drug10 = drug10;
        return this;
    }

    public Sample toSample(){
        Sample sample = new Sample();
        sample.setPatientId(patientId);
        if (diagnosis1 > 0){
            sample.addDiagnosis(diagnosis1);
        }
        if (diagnosis2 > 0){
            sample.addDiagnosis(diagnosis2);
        }
        if (diagnosis3 > 0){
            sample.addDiagnosis(diagnosis3);
        }
        if (diagnosis4 > 0){
            sample.addDiagnosis(diagnosis4);
        }

        if (drug1.getDrugId() > 0 && drug1.getAmount() > 0){
            sample.addDrug(drug1.getDrugId());
            sample.addDrugDetail(drug1);
        }

        if (drug2.getDrugId() > 0 && drug2.getAmount() > 0){
            sample.addDrug(drug2.getDrugId());
            sample.addDrugDetail(drug2);
        }

        if (drug3.getDrugId() > 0 && drug3.getAmount() > 0){
            sample.addDrug(drug3.getDrugId());
            sample.addDrugDetail(drug3);
        }

        if (drug4.getDrugId() > 0 && drug4.getAmount() > 0){
            sample.addDrug(drug4.getDrugId());
            sample.addDrugDetail(drug4);
        }

        if (drug5.getDrugId() > 0 && drug5.getAmount() > 0){
            sample.addDrug(drug5.getDrugId());
            sample.addDrugDetail(drug5);
        }

        if (drug6.getDrugId() > 0 && drug6.getAmount() > 0){
            sample.addDrug(drug6.getDrugId());
            sample.addDrugDetail(drug6);
        }

        if (drug7.getDrugId() > 0 && drug7.getAmount() > 0){
            sample.addDrug(drug7.getDrugId());
            sample.addDrugDetail(drug7);
        }

        if (drug8.getDrugId() > 0 && drug8.getAmount() > 0){
            sample.addDrug(drug8.getDrugId());
            sample.addDrugDetail(drug8);
        }

        if (drug9.getDrugId() > 0 && drug9.getAmount() > 0){
            sample.addDrug(drug9.getDrugId());
            sample.addDrugDetail(drug9);
        }

        if (drug10.getDrugId() > 0 && drug10.getAmount() > 0){
            sample.addDrug(drug10.getDrugId());
            sample.addDrugDetail(drug10);
        }
        return sample;
    }
}
