package com.ogic.prescriptionsyntheticsystem.component;

import com.ogic.prescriptionsyntheticsystem.entity.DrugDetail;
import com.ogic.prescriptionsyntheticsystem.entity.Sample;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * 样本清洗工具
 * @author ogic
 */
public class SampleCleanTool {
    public void clean(List<Sample> data){
        /*1. 清洗药物数量不大于0的样本，即删除药物量小于等于0的药物，如果所有的药物被删除则删除该样本*/
        for (int i = 0; i < data.size(); i ++){
            List<DrugDetail> drugDetails = data.get(i).getDrugDetails();
            for (int j = 0; j < drugDetails.size(); j++){
                DrugDetail drugDetail = drugDetails.get(j);
                if (drugDetail.getAmount() <= 0){
                    data.get(i).getDrugs().remove(j);
                    drugDetails.remove(j);
                    j--;
                }
            }
            if (drugDetails.isEmpty()){
                data.remove(i);
                i--;
            }
        }


        /*2. 对症状和用药列表进行排序*/
        for (Sample datum : data) {
            List<Integer> diagnosis = datum.getDiagnoses();
            diagnosis.sort(Comparator.comparingInt(o -> o));
            List<Integer> drugs = datum.getDrugs();
            drugs.sort((Comparator.comparingInt(o -> o)));
            List<DrugDetail> drugDetails = datum.getDrugDetails();
            drugDetails.sort((Comparator.comparingInt(DrugDetail::getDrugId)));
        }


    }
}
