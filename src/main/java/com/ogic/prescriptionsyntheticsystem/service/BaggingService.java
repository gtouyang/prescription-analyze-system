package com.ogic.prescriptionsyntheticsystem.service;

import com.ogic.prescriptionsyntheticsystem.component.SampleCleanTool;
import com.ogic.prescriptionsyntheticsystem.component.SampleImportTool;
import com.ogic.prescriptionsyntheticsystem.entity.*;
import com.ogic.prescriptionsyntheticsystem.exception.UnitUnfixedException;
import com.ogic.prescriptionsyntheticsystem.mapper.AprioriRuleWithBelieveDegreeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

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

    private List<AprioriRuleWithBelieveDegree> totalAprioriRule;

    private List<LDAView> totalLdaResult;

    @Value("${bagging.setting.excel.path}")
    private String excelFilePath = "/home/ogic/Desktop/data.xls";

    @Value("${bagging.setting.apriori.minBelieveDegree}")
    private double MIN_BELIEVE_DEGREE = 0.2;

    @Value("${bagging.setting.lda.alpha}")
    private float alpha = 0.5f;

    @Value("${bagging.setting.lda.beta}")
    private float beta = 0.1f;

    @Value("${bagging.setting.lda.iteration}")
    private int iteration = 1000;

    @Value("${bagging.setting.lda.saveStep}")
    private int saveStep = 0;

    @Value("${bagging.setting.lda.beginSaveIters}")
    private int beginSaveIters = 0;

    @Resource
    private AprioriRuleWithBelieveDegreeMapper aprioriRuleWithBelieveDegreeMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public BaggingService() throws IOException {
        SampleImportTool sampleImportTool = new SampleImportTool(excelFilePath);
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

        Lda model = new Lda(alpha, beta, iteration, saveStep, beginSaveIters);
        System.out.println("1 Initialize the lda model ...");
        model.initializeModel(totalSamples, diagnosisStrList ,drugStrList);
        System.out.println("2 Learning and Saving the lda model ...");
        model.inferenceModel();
        totalLdaResult = model.getNormalDrug();
        System.out.println("Done!");
    }

    public void aprioriInit(){
        totalAprioriRule = aprioriRuleWithBelieveDegreeMapper.getAllAprioriRuleWithBelieveDegreeList();
    }

    public List<Selection> getDiagnosisSelectionList() {
        return diagnosisSelectionList;
    }

    public List<Selection> getDrugSelectionList() {
        return drugSelectionList;
    }


    public void aprioriAnalyzeByDiagnoses(List<Integer> targetElements, List<AprioriRuleView> aprioriRuleViews){
        List<AprioriRuleWithBelieveDegree> diagnosis2DrugList = new ArrayList<>();
        List<AprioriRuleWithBelieveDegree> drug2DiagnosisList = new ArrayList<>();
        boolean flag;
        for (int i = 0; i < totalAprioriRule.size(); i++){
            flag = false;
            for (int element: targetElements){
                if (totalAprioriRule.get(i).getRule().contains(String.valueOf(element))){
                    flag = true;
                    String rule = totalAprioriRule.get(i).getRule();
                    String[] parts = rule.split("=>");
                    String[] part1 = parts[0].substring(1, parts[0].length()-1).split(", ");
                    String[] part2 = parts[1].substring(1, parts[1].length()-1).split(", ");
                    if (isAllDiagnoses(part1) && isAllDrugs(part2)){
                        diagnosis2DrugList.add(totalAprioriRule.get(i));
                    }
                    else if (isAllDrugs(part1) && isAllDiagnoses(part2)){
                        drug2DiagnosisList.add(totalAprioriRule.get(i));
                    }
                    break;
                }
            }
            if (!flag){
                totalAprioriRule.remove(i);
                i--;
            }
        }

    }

    private void aprioriAnalyzeBySamples(Sample sample, List<AprioriRuleView> aprioriRuleViews, List<DrugView> drugViews){

        List<AprioriRuleWithBelieveDegree> diagnosis2DrugList = new ArrayList<>();
        List<AprioriRuleWithBelieveDegree> drug2DiagnosisList = new ArrayList<>();
        boolean containsDiagnosis;
        boolean containsDrug;
        for (int i = 0; i < totalAprioriRule.size(); i++) {
            containsDiagnosis = false;
            containsDrug = false;
            for (Integer diagnosis : sample.getDiagnoses()) {
                if (totalAprioriRule.get(i).getRule().contains(diagnosis.toString())) {
                    containsDiagnosis = true;
                    break;
                }
            }
            for (Integer drug : sample.getDrugs()){
                if (totalAprioriRule.get(i).getRule().contains(drug.toString())){
                    containsDrug = true;
                    break;
                }
            }
            if (containsDiagnosis && containsDrug) {
                String rule = totalAprioriRule.get(i).getRule();
                String[] parts = rule.split("=>");
                String[] part1 = parts[0].substring(1, parts[0].length() - 1).split(", ");
                String[] part2 = parts[1].substring(1, parts[1].length() - 1).split(", ");
                if (isAllDiagnoses(part1) && isAllDrugs(part2)) {
                    diagnosis2DrugList.add(totalAprioriRule.get(i));
                } else if (isAllDrugs(part1) && isAllDiagnoses(part2)) {
                    drug2DiagnosisList.add(totalAprioriRule.get(i));
                }
            }
        }

        for (AprioriRuleWithBelieveDegree aprioriRule : diagnosis2DrugList){
            if (aprioriRule.getBelieveDegree() >= MIN_BELIEVE_DEGREE){
                aprioriRuleViews.add(changeAprioriRule2AprioriRuleView(aprioriRule));
                for (DrugView drugView : drugViews){
                    if (aprioriRule.getRule().contains(String.valueOf(drugView.getId()))){
                        drugView.setJudgeResult(DrugView.JudgeState.APRIORI_PASS);
                    }
                }
            }
        }

        for (AprioriRuleWithBelieveDegree aprioriRule : drug2DiagnosisList){
            if (aprioriRule.getBelieveDegree() >= MIN_BELIEVE_DEGREE){
                aprioriRuleViews.add(changeAprioriRule2AprioriRuleView(aprioriRule));
                for (DrugView drugView : drugViews){
                    if (aprioriRule.getRule().contains(String.valueOf(drugView.getId()))){
                        drugView.setJudgeResult(DrugView.JudgeState.APRIORI_PASS);
                    }
                }
            }
        }
    }

    private void ldaAnalyze(Sample sample, List<LDAView> ldaViews, List<DrugView> drugViews){
        for (Integer diagnosis: sample.getDiagnoses()){
            ldaViews.add(totalLdaResult.get(diagnosis-10000));
        }
        for (LDAView ldaView : ldaViews){
            for (DrugView drugView: drugViews){
                if (ldaView.getDrugIDList().contains(drugView.getId())){
                    drugView.setJudgeResult(DrugView.JudgeState.LDA_PASS);
                }
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

    private AprioriRuleView changeAprioriRule2AprioriRuleView(AprioriRuleWithBelieveDegree rule) {
            String[] parts = rule.getRule().split("=>");
            String[] part1 = parts[0].substring(1, parts[0].length()-1).split(", ");
            String[] part2 = parts[1].substring(1, parts[1].length()-1).split(", ");
            return new  AprioriRuleView(rule.getRule(), Arrays.toString(id2Name(part1)) + "=>" + Arrays.toString(id2Name(part2)), rule.getBelieveDegree());
    }


    public BaggingResult judge(Sample sample){
        BaggingResult result = new BaggingResult();
        List<AprioriRuleView> aprioriRuleViews = new ArrayList<>();
        List<DrugView> drugViews = new ArrayList<>(sample.getDrugs().size());
        List<LDAView> ldaViews = new ArrayList<>(sample.getDiagnoses().size());
        for (DrugDetail drugDetail : sample.getDrugDetails()){
            drugViews.add(new DrugView(drugDetail.getDrugId(),
                    drugStrList.get(drugDetail.getDrugId()-20000),
                    drugDetail.getAmount(),
                    drugDetail.getUnit()));
        }
        aprioriAnalyzeBySamples(sample, aprioriRuleViews, drugViews);
        result.setAprioriRuleViews(aprioriRuleViews);

        ldaAnalyze(sample, ldaViews, drugViews);
        result.setLdaViews(ldaViews);
        result.setDrugViews(drugViews);
        return result;
    }
}
