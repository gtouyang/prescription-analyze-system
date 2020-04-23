package com.ogic.prescriptionsyntheticsystem.service;

import com.ogic.prescriptionsyntheticsystem.component.SampleCleanTool;
import com.ogic.prescriptionsyntheticsystem.component.SampleImportTool;
import com.ogic.prescriptionsyntheticsystem.entity.AprioriRuleView;
import com.ogic.prescriptionsyntheticsystem.entity.AprioriRuleWithBelieveDegree;
import com.ogic.prescriptionsyntheticsystem.entity.Selection;
import com.ogic.prescriptionsyntheticsystem.entity.Sample;
import com.ogic.prescriptionsyntheticsystem.exception.UnitUnfixedException;
import com.ogic.prescriptionsyntheticsystem.mapper.AprioriRuleWithBelieveDegreeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ogic
 */
@Service
public class BaggingService {

    private List<Sample> totalSamples;

    private List<String> diagnosisStrList;

    private List<String> drugStrList;

    private List<Selection> diagnosisSelectionList;

    private List<Selection> drugSelectionList;

    private List<AprioriRuleWithBelieveDegree> diagnosis2DrugList;

    private List<AprioriRuleWithBelieveDegree> drug2DiagnosisList;

    @Resource
    private AprioriRuleWithBelieveDegreeMapper aprioriRuleWithBelieveDegreeMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public BaggingService() throws IOException {
        SampleImportTool sampleImportTool = new SampleImportTool("/home/ogic/Desktop/data.xls");
        try {
            sampleImportTool.readExcel(1);
        }catch (UnitUnfixedException e){
            System.out.println(e.getUnitError());
            e.printStackTrace();
        }
        totalSamples = sampleImportTool.getSampleList();
        diagnosisStrList = sampleImportTool.getDiagnosisList();
        drugStrList = sampleImportTool.getDrugList();
        sampleImportTool.close();
        SampleCleanTool sampleCleanTool = new SampleCleanTool();
        sampleCleanTool.clean(totalSamples);
        diagnosisSelectionList = new ArrayList<>(diagnosisStrList.size()+1);
        diagnosisSelectionList.add(new Selection(00000, "无"));
        for (int i = 0; i < diagnosisStrList.size(); i++){
            diagnosisSelectionList.add(new Selection(10000+i, diagnosisStrList.get(i)));
        }
        drugSelectionList = new ArrayList<>(drugStrList.size()+1);
        drugSelectionList.add(new Selection(00000, "无"));
        for (int i = 0; i < drugStrList.size(); i++){
            drugSelectionList.add(new Selection(20000+i, drugStrList.get(i)));
        }
    }

    public List<Selection> getDiagnosisSelectionList() {
        return diagnosisSelectionList;
    }

    public List<Selection> getDrugSelectionList() {
        return drugSelectionList;
    }


    public void aprioriAnalyze(List<Integer> targetElements){
        List<AprioriRuleWithBelieveDegree> list = aprioriRuleWithBelieveDegreeMapper.getAllAprioriRuleWithBelieveDegreeList();
        diagnosis2DrugList = new ArrayList<>();
        drug2DiagnosisList = new ArrayList<>();
        boolean flag;
        for (int i = 0; i < list.size(); i++){
            flag = false;
            for (int element: targetElements){
                if (list.get(i).getRule().contains(String.valueOf(element))){
                    flag = true;
                    String rule = list.get(i).getRule();
                    String[] parts = rule.split("=>");
                    String[] part1 = parts[0].substring(1, parts[0].length()-1).split(", ");
                    String[] part2 = parts[1].substring(1, parts[1].length()-1).split(", ");
                    if (isAllDiagnoses(part1) && isAllDrugs(part2)){
                        diagnosis2DrugList.add(list.get(i));
                    }
                    else if (isAllDrugs(part1) && isAllDiagnoses(part2)){
                        drug2DiagnosisList.add(list.get(i));
                    }
                    break;
                }
            }
            if (!flag){
                list.remove(i);
                i--;
            }
        }
    }

    private boolean isAllDiagnoses(String[] part){
        for (String str:part){
            int temp = Integer.parseInt(str);
            if (temp >= 20000 || temp < 10000){
                return false;
            }
        }
        return true;
    }

    public boolean isAllDrugs(String[] part){
        for (String str:part){
            int temp = Integer.parseInt(str);
            if (temp < 20000 || temp >= 30000){
                return false;
            }
        }
        return true;
    }

    /**
     * 将诊断ID或药物ID转换成诊断名或药物名
     * @param idList    ID列表
     * @return  名字列表
     */
    private List<String> id2Name(List<Integer> idList){
        List<String> result = new ArrayList<>(idList.size());
        for (int i : idList){
            if (i >= 20000){
                result.add(drugStrList.get(i - 20000));
            }else{
                result.add(diagnosisStrList.get(i-10000));
            }
        }
        return result;
    }

    /**
     * 将诊断ID或药物ID转换成诊断名或药物名
     * @param idList    ID列表
     * @return  名字列表
     */
    private String[] id2Name(int[] idList){
        String[] result = new String[idList.length];
        for (int i = 0; i < idList.length; i++){
            if (idList[i] >= 20000){
                result[i] = drugStrList.get(idList[i] - 20000);
            }else{
                result[i] = diagnosisStrList.get(idList[i] - 10000);
            }
        }
        return result;
    }

    /**
     * 将诊断ID或药物ID转换成诊断名或药物名
     * @param idList    ID列表
     * @return  名字列表
     */
    private String[] id2Name(String[] idList){
        String[] result = new String[idList.length];
        for (int i = 0; i < idList.length; i++){
            int temp = Integer.parseInt(idList[i]);
            if (temp >= 20000){
                result[i] = drugStrList.get(temp - 20000);
            }else{
                result[i] = diagnosisStrList.get(temp - 10000);
            }
        }
        return result;
    }

    public List<AprioriRuleView> getDiagnosis2DrugAprioriRuleViewList() {
        List<AprioriRuleView> result = new ArrayList<>(diagnosis2DrugList.size());
        for (AprioriRuleWithBelieveDegree rule : diagnosis2DrugList){
            String[] parts = rule.getRule().split("=>");
            String[] part1 = parts[0].substring(1, parts[0].length()-1).split(", ");
            String[] part2 = parts[1].substring(1, parts[1].length()-1).split(", ");
            result.add(new AprioriRuleView(rule.getRule(), Arrays.toString(id2Name(part1)) + "=>" + Arrays.toString(id2Name(part2)), rule.getBelieveDegree()));
        }
        return result;
    }
}
