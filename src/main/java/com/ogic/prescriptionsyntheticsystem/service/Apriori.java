package com.ogic.prescriptionsyntheticsystem.service;

import com.ogic.prescriptionsyntheticsystem.entity.Sample;
import com.ogic.prescriptionsyntheticsystem.exception.ResultMissException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.*;

/**
 * @author ogic
 */
public class Apriori {

    /**
     * 最小支持度(支持度阈值)
     */
    public final double MIN_SUPPORT_DEGREE = 0.005;

    /**
     * 最小置信度(置信度阈值)
     */
    public final double MIN_BELIEVE_DEGREE = 0.25;

    /**
     * 总样本列表
     */
    private final List<Sample> sampleList;

    /**
     * 诊断列表
     */
    private final List<String> diagnosisList;

    /**
     * 药物列表
     */
    private final List<String> drugList;

    /**
     * 简化后的样本列表
     */
    private final List<List<Integer>> data;

    /**
     * 支持度表
     */
    private Map<String, Double> supportDegreeResult;

    /**
     * 置信度表
     */
    private Map<String, Double> believeDegreeResult;

    /**
     * 符合诊断=>药物的关联规则表
     */
    private Map<String, Double> diagnosis2DrugRuleMap;

    /**
     * 符合药物=>诊断的关联规则表
     */
    private Map<String, Double> drug2DiagnosisRuleMap;

    /**
     * 正在进行的置信度计算任务
     */
    private int believeDegreeMission = 0;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Apriori(List<Sample> sampleList, List<String> diagnosisList, List<String> drugList) {
        this.sampleList = sampleList;
        this.diagnosisList = diagnosisList;
        this.drugList = drugList;
        data = new ArrayList<>(sampleList.size());
        for (int i = 0; i < sampleList.size(); i++){
            List<Integer> temp = new ArrayList<>(sampleList.get(i).getDiagnoses().size() + sampleList.get(i).getDrugs().size());
            temp.addAll(sampleList.get(i).getDiagnoses());
            temp.addAll(sampleList.get(i).getDrugs());
            data.add(temp);
        }
        for (List<Integer> list : data){
            logger.debug(Arrays.toString(list.toArray()));
        }
    }

    /**
     * 运行Apriori算法，结果保存在supportDegreeResult和believeDegreeResult
     */
    public void run(){

        /*初始化*/
        int kindNum = diagnosisList.size() + drugList.size();
        supportDegreeResult = new HashMap<>();
        believeDegreeResult = new HashMap<>();
        diagnosis2DrugRuleMap = new HashMap<>();
        drug2DiagnosisRuleMap = new HashMap<>();
        List<List<Integer>> targetList = new ArrayList<>(kindNum);
        List<List<Integer>> fixableList = new ArrayList<>(kindNum);

        /*将所有诊断和所有药物分别作为本次支持度计算的目标*/
        for (int i = 0; i < diagnosisList.size(); i++){
            List<Integer> list = new ArrayList<>(1);
            list.add(10000 + i);
            targetList.add(list);
        }
        for (int i = 0; i < drugList.size(); i++){
            List<Integer> list = new ArrayList<>(1);
            list.add(20000 + i);
            targetList.add(list);
        }
        int targetSize = 1;

        while (!targetList.isEmpty()) {
            logger.info("this turn's target size:" + targetList.size() + "\n");
            for (List<Integer> target : targetList) {
                double temp = supportDegree(target);
                supportDegreeResult.put(Arrays.toString(target.toArray()), temp);

                /*如果支持度大于支持度阈值则加入频繁集列表*/
                if (temp > MIN_SUPPORT_DEGREE) {
                    fixableList.add(target);
                }
            }

            /*清除目标列表，重新生成下次循环的目标列表*/
            targetList.clear();

            /*下次循环的目标集合的长度*/
            targetSize++;

            /*通过本次循环的频繁集列表生成下次循环的目标列表*/
            for (int i = 0; i < fixableList.size(); i++){
                for (int j = i; j < fixableList.size(); j++) {
                    if(targetSize < 6) {
                        List<Integer> temp = pair(fixableList.get(i), fixableList.get(j));
                        if (temp != null) {
                            targetList.add(temp);
                        }
                    }
                }
                if (targetSize > 1) {
                    believeDegreeMission++;
                    believeDegreeAnalyze(fixableList.get(i));
                }
            }

            /*清除频繁集列表*/
            fixableList.clear();
        }

    }

    /**
     * 获得支持度表
     * @return  支持度表
     */
    public Map<String, Double> getSupportDegreeResult() {
        return supportDegreeResult;
    }

    /**
     * 获得置信度表
     * @return  置信度表
     */
    public Map<String, Double> getBelieveDegreeResult() {
        return believeDegreeResult;
    }

    /**
     * 获得诊断=>药物的关联规则表
     * @return 关联规则表
     */
    public Map<String, Double> getDiagnosis2DrugRuleMap() {
        return diagnosis2DrugRuleMap;
    }

    /**
     * 获得药物=>诊断的关联规则表
     * @return 关联规则表
     */
    public Map<String, Double> getDrug2DiagnosisRuleMap() {
        return drug2DiagnosisRuleMap;
    }

    /**
     * 计算该集合的支持度
     * @param list  集合
     * @return  支持度
     */
    private double supportDegree(List<Integer> list){
        int includeNum = 0;
        boolean flag;
        for (List<Integer> s: data){
            flag = true;
            for (int i:list){
                if (!s.contains(i)){
                    flag = false;
                    break;
                }
            }
            if (flag){
                includeNum ++;
            }
        }
        return (double) includeNum / sampleList.size();
    }

    /**
     * 获得该子集对于该集合的置信度
     * @param whole 集合
     * @param part  子集
     * @return  置信度
     * @throws ResultMissException  子集的支持度丢失
     */
    private double believeDegree(List<Integer> whole, List<Integer> part) throws ResultMissException {
        Double wholeListSupportDegree = supportDegreeResult.get(Arrays.toString(whole.toArray()));
        Double partListSupportDegree = supportDegreeResult.get(Arrays.toString(part.toArray()));
        if (wholeListSupportDegree == null){
            logger.error("The support degree (" + Arrays.toString(whole.toArray()) + ") is missing");
        }
        if (partListSupportDegree == null){
            logger.error("The support degree (" + Arrays.toString(part.toArray()) + ") is missing");
        }
        if (partListSupportDegree >= 0.0){
            return wholeListSupportDegree / partListSupportDegree;
        }
        else {
            throw new ResultMissException("The Result of" + Arrays.toString(part.toArray()) + "is Missing");
        }
    }

    /**
     * 分析该集合的所有子集的置信度，结果加入believeDegreeResult
     * @param list  集合
     */
//    @Async
    void believeDegreeAnalyze(List<Integer> list){
        try {
            for (int i = 1; i < list.size(); i++) {
                /*获得长度为i的所有子集*/
                List<List<Integer>> partList = getPartList(list, i);

                for (List<Integer> part : partList) {
                    double tempBelieveDegree = believeDegree(list, part);
                    List<Integer> rest = new ArrayList<>(list.size() - part.size());
                    for (int j : list){
                        if (!part.contains(j)){
                            rest.add(j);
                        }
                    }
                    String str = Arrays.toString(part.toArray()) + "=>" + Arrays.toString(rest.toArray());
                    believeDegreeResult.put(str, tempBelieveDegree);
                    if (tempBelieveDegree >= MIN_BELIEVE_DEGREE){
                        boolean isDiagnosis2Drug = true;
                        boolean isDrug2Diagnosis = true;

                        for (int temp : part){
                            if (temp >= 20000) {
                                isDiagnosis2Drug = false;
                                break;
                            } else {
                                isDrug2Diagnosis = false;
                                break;
                            }
                        }
                        for (int temp : rest){
                            if (temp < 20000) {
                                isDiagnosis2Drug = false;
                                break;
                            }else {
                                isDrug2Diagnosis = false;
                                break;
                            }
                        }
                        if (isDiagnosis2Drug) {
                            str = Arrays.toString(part.toArray()) + "=>" + Arrays.toString(rest.toArray());
                            diagnosis2DrugRuleMap.put(str, tempBelieveDegree);
                        }
                        if (isDrug2Diagnosis) {
                            str = Arrays.toString(part.toArray()) + "=>" + Arrays.toString(rest.toArray());
                            drug2DiagnosisRuleMap.put(str, tempBelieveDegree);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            believeDegreeMission--;
        }
    }

    /**
     * 获得list里面所有子集
     * @param list  需要被取子集的list
     * @param size  当前递归所取的子集的长度
     * @return  子集列表
     */
    private List<List<Integer>> getPartList(List<Integer> list, int size){
        if (list.size() < size){
            return null;
        }
        if (list.size() == size){
            List<List<Integer>> result = new ArrayList<>(1);
            result.add(list);
            return result;
        }
        if (size == 1){
            List<List<Integer>> result = new ArrayList<>(list.size());
            for (int i : list){
                List<Integer> tempList = new ArrayList<>(5);
                tempList.add(i);
                result.add(tempList);
            }
            return result;
        }
        if (size > 1){
            List<List<Integer>> result = new ArrayList<>();
            for (int i = 0; i < list.size()-size+1; i++){
                int tempNum = list.get(i);
                List<List<Integer>> tempResult = getPartList(list.subList(i+1, list.size()-1), size-1);
                if (tempResult != null){
                    for (List<Integer> tempList : tempResult){
                        List<Integer> newList = new ArrayList<>(5);
                        newList.add(tempNum);
                        newList.addAll(tempList);
                        newList.sort(Comparator.comparingInt(o -> o));
                        result.add(newList);
                    }
                }
            }
            if (result.isEmpty()){
                return null;
            }
            return result;
        }
        return null;
    }

    /**
     * 判断集合a和集合b是否有且只有一个元素不同，是则合并集合
     * @param a 集合a
     * @param b 集合b
     * @return  并集或null
     */
    private List<Integer> pair(List<Integer> a, List<Integer> b){
        int differentNum = 0;
        if (a.size() == b.size()){
            List<Integer> result = new ArrayList<>(a.size()+1);
            result.addAll(a);
            for (int i:b){
                if (!result.contains(i)){
                    result.add(i);
                    differentNum++;
                }
                if (differentNum > 1){
                    break;
                }
            }
            if (differentNum == 1){
                result.sort(Comparator.comparingInt(o -> o));
                return result;
            }
        }
        return null;
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
                result.add(drugList.get(i - 20000));
            }else{
                result.add(diagnosisList.get(i-10000));
            }
        }
        return result;
    }

    /**
     * 判断置信度是否计算完毕
     * @return  置信度是否计算完毕
     */
    public boolean isBelieveDegreeFinished(){
        return (believeDegreeMission == 0);
    }

}