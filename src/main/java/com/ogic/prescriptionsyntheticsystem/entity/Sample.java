package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
     * 案例标识，用于区分是不是同一个案例，这里用的是就诊次数
     */
    private int flag;

    /**
     * 诊断
     */
    private final List<Integer> diagnoses;

    /**
     * 用药
     */
    private List<Integer> drugs;

    /**
     * 具体用量及单位
     */
    private List<DrugDetail> drugDetails;

    private static int ID_COUNT = 0;

    public Sample() {
        id = ID_COUNT++;
        diagnoses = new ArrayList<>(4);
        drugs = new ArrayList<>(10);
        drugDetails = new ArrayList<>(10);
    }

    public int getId() {
        return id;
    }

    public int getPatientId() {
        return patientId;
    }

    public Sample setPatientId(int patientId) {
        this.patientId = patientId;
        return this;
    }

    public List<Integer> getDiagnoses() {
        return diagnoses;
    }

    public Sample addDiagnosis(int diagnosis){
        if (!diagnoses.contains(diagnosis)){
            diagnoses.add(diagnosis);
            diagnoses.sort(Comparator.comparingInt(o -> o));
        }
        return this;
    }

    public int getFlag() {
        return flag;
    }

    public Sample setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public List<Integer> getDrugs() {
        return drugs;
    }

    public Sample addDrug(int drug) {
        if (!drugs.contains(drug)){
            drugs.add(drug);
            drugs.sort(Comparator.comparingInt(o -> o));
        }
        return this;
    }

    public List<DrugDetail> getDrugDetails() {
        return drugDetails;
    }

    public Sample addDrugDetail(int drugId, int amount, String unit) {
        for (DrugDetail drugDetail:drugDetails){
            if (drugDetail.getDrugId() == drugId){
                drugDetail.addAmount(amount);
                return this;
            }
        }
        drugDetails.add(new DrugDetail(drugId, amount, unit));
        return this;
    }

    @Override
    public String toString() {
        return "\n\nSample{" +
                "id=" + id +
                ", \npatientId=" + patientId +
                ", \ndiagnosis=" + Arrays.toString(diagnoses.toArray()) +
                ", \ndrugs=" + Arrays.toString(drugs.toArray()) +
                ", \ndrugDetails=" + Arrays.toString(drugDetails.toArray()) +
                "}";
    }
}
