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
     * 诊断
     */
    private List<Integer> diagnosis;

    /**
     * 用药
     */
    private List<Integer> medicine;

    public boolean diagnosisInclude(Sample sample){
        for (Integer i : sample.diagnosis){
            if (!diagnosis.contains(i)){
                return false;
            }
        }
        return true;
    }

    public boolean medicineInclude(Sample sample){
        for (Integer i : sample.medicine){
            if (!medicine.contains(i)){
                return false;
            }
        }
        return true;
    }
}
