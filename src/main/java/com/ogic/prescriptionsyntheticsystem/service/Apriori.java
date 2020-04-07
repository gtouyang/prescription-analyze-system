package com.ogic.prescriptionsyntheticsystem.service;

import com.ogic.prescriptionsyntheticsystem.entity.Sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ogic
 */
public class Apriori {
    public double supportDegree(List<Sample> samples, Sample a){
        int includeNum = 0;
        for (Sample s: samples){
            if (s.diagnosisInclude(a) && s.drugInclude(a)){
                includeNum++;
            }
        }
        return (double) includeNum / samples.size();
    }

    public double believeDegree(List<Sample> samples, Sample a){
        int diagnosisIncludeNum = 0;
        int medicineIncludeNum = 0;
        for (Sample s: samples){
            if (s.diagnosisInclude(a)){
                diagnosisIncludeNum ++;
                if (s.drugInclude(a)){
                    medicineIncludeNum++;
                }
            }
        }
        if (diagnosisIncludeNum == 0){
            return 0;
        }
        return (double) medicineIncludeNum / diagnosisIncludeNum;
    }

    public Map<String,Double> analyze(List<Sample> samples, Sample a){
        int diagnosisIncludeNum = 0;
        int medicineIncludeNum = 0;
        for (Sample s: samples){
            if (s.diagnosisInclude(a)){
                diagnosisIncludeNum ++;
                if (s.drugInclude(a)){
                    medicineIncludeNum++;
                }
            }
        }
        Map<String, Double> result = new HashMap<>(2);
        result.put("supportDegree", (double)medicineIncludeNum / samples.size());
        if (diagnosisIncludeNum == 0){
            result.put("medicineDegree", 0.0);
        }else {
            result.put("medicineDegree", (double)medicineIncludeNum / diagnosisIncludeNum);
        }
        return result;
    }
}