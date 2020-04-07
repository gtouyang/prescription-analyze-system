package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@Accessors(chain = true)
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


    /**
     * 是否包含other中的诊断
     * @param other
     * @return
     */
    public boolean diagnosisInclude(Sample other){
        for (Integer i : other.diagnosis){
            if (!diagnosis.contains(i)){
                return false;
            }
        }
        return true;
    }

    /**
     * 是否包含other中的用药
     * @param other
     * @return
     */
    public boolean drugInclude(Sample other){
        for (Integer i : other.drugs){
            if (!drugs.contains(i)){
                return false;
            }
        }
        return true;
    }
}
