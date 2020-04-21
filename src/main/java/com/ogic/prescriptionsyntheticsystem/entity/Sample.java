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

    /**
     * 给样本添加诊断，如果诊断已存在则跳过
     * @param diagnosisId 诊断ID
     * @return  该样本
     */
    public Sample addDiagnosis(int diagnosisId){
        if (!diagnoses.contains(diagnosisId)){
            diagnoses.add(diagnosisId);
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

    /**
     * 添加药物，如果药物已存在则跳过
     * @param drugId    药物ID
     * @return          样本中是否已经含有该药物
     */
    public boolean addDrug(int drugId) {
        if (!drugs.contains(drugId)){
            drugs.add(drugId);
            drugs.sort(Comparator.comparingInt(o -> o));
            return false;
        }
        return true;
    }

    public List<DrugDetail> getDrugDetails() {
        return drugDetails;
    }

    /**
     * 添加药物详细信息，如果药物已存在则数量合并
     * @param newOne    新药物
     * @return          样本中是否已经含有该药物
     */
    public boolean addDrugDetail(DrugDetail newOne) {
        for (DrugDetail drugDetail:drugDetails){
            if (drugDetail.getDrugId() == newOne.getDrugId() && drugDetail.getUnit().equals(newOne.getUnit())){
                drugDetail.addAmount(newOne.getAmount());
                return true;
            }
        }
        drugDetails.add(newOne);
        return false;
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
