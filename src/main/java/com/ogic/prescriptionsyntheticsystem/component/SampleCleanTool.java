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
@Component
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

//        /*2. 统一单位*/
//        for (Sample datum : data){
//            for (DrugDetail drugDetail : datum.getDrugDetails()){
//
//                if (drugDetail.getUnit() != null) {
//
//                    String unit = drugDetail.getUnit();
//
//                    /* 修改不规范的单位 */
//                    if ("1g".equals(unit)) {
//                        drugDetail.setUnit("g");
//                    } else if ("/g".equals(unit)) {
//                        drugDetail.setUnit("g");
//                    } else if ("/支".equals(unit)) {
//                        drugDetail.setUnit("支");
//                    } else if ("4NU单位 30片/盒".equals(unit)) {
//                        drugDetail.setUnit("30片/盒");
//                    }
//
//                    /* 将瓶、盒、版等单位换算成片 */
//
//                    int multiple = 1;
//
//                    if (unit.contains("片")) {
//                        if (unit.contains("x")){
//                            multiple = Integer.parseInt(unit.substring(unit.indexOf("x")+1, unit.indexOf("片")));
//                        }else {
//                            multiple = Integer.parseInt(unit.substring(0, unit.indexOf("片")));
//                        }
//                        drugDetail.setUnit("片");
//                        drugDetail.addAmount(drugDetail.getAmount() * (multiple-1));
//                    }
//
//                    if (unit.contains("丸")) {
//                        if (unit.contains("x")){
//                            multiple = Integer.parseInt(unit.substring(unit.indexOf("x")+1, unit.indexOf("丸")));
//                        }else {
//                            multiple = Integer.parseInt(unit.substring(0, unit.indexOf("丸")));
//                        }
//                        drugDetail.setUnit("丸");
//                        drugDetail.addAmount(drugDetail.getAmount() * (multiple-1));
//                    }
//
//                    if (unit.contains("袋")) {
//                        if (unit.contains("x")){
//                            multiple = Integer.parseInt(unit.substring(unit.indexOf("x")+1, unit.indexOf("袋")));
//                        }else {
//                            multiple = Integer.parseInt(unit.substring(0, unit.indexOf("袋")));
//                        }
//                        drugDetail.setUnit("袋");
//                        drugDetail.addAmount(drugDetail.getAmount() * (multiple-1));
//                    }
//                }
//            }
//        }

        /*3. 对症状和用药列表进行排序*/
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
