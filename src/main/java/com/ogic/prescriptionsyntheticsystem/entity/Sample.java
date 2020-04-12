package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

@Data
//@NoArgsConstructor
//@Accessors(chain = true)
public class Sample {

    /**
     * 案例ID
     */
    private int id;

    /**
     * 患者ID
     */
    private int patientId;

    /**
     * 诊断
     */
    private List<Integer> diagnosis;

    /**
     * 用药
     */
    private List<Integer> drugs;

    /**
     * 具体用量及单位
     */
    private List<DrugDetail> drugDetails;

    public Sample() {
    }

    public int getId() {
        return id;
    }

    public Sample setId(int id) {
        this.id = id;
        return this;
    }

    public int getPatientId() {
        return patientId;
    }

    public Sample setPatientId(int patientId) {
        this.patientId = patientId;
        return this;
    }

    public List<Integer> getDiagnosis() {
        return diagnosis;
    }

    public Sample setDiagnosis(List<Integer> diagnosis) {
        this.diagnosis = diagnosis;
        return this;
    }

    public List<Integer> getDrugs() {
        return drugs;
    }

    public Sample setDrugs(List<Integer> drugs) {
        this.drugs = drugs;
        return this;
    }

    public List<DrugDetail> getDrugDetails() {
        return drugDetails;
    }

    public Sample setDrugDetails(List<DrugDetail> drugDetails) {
        this.drugDetails = drugDetails;
        return this;
    }

    @Override
    public String toString() {
        return "\n\nSample{" +
                "id=" + id +
                ", \npatientId=" + patientId +
                ", \ndiagnosis=" + Arrays.toString(diagnosis.toArray()) +
                ", \ndrugs=" + Arrays.toString(drugs.toArray()) +
                ", \ndrugDetails=" + Arrays.toString(drugDetails.toArray()) +
                "}";
    }
}
