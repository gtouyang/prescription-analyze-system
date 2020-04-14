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

    public final double MIN_SUPPORT_DEGREE = 0.01;

    public final double MIN_BELIEVE_DEGREE = 0.1;

    private final List<Sample> sampleList;

    private final List<String> diagnosisList;

    private final List<String> drugList;

    private final List<List<Integer>> data;

    private Map<String, Double> supportDegreeResult;

    private Map<String, Double> believeDegreeResult;

    private Map<String, Double> fixableRuleMap;

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
        int kindNum = diagnosisList.size() + drugList.size();
        supportDegreeResult = new HashMap<>();
        believeDegreeResult = new HashMap<>();
        fixableRuleMap = new HashMap<>();
        List<List<Integer>> targetList = new ArrayList<>(kindNum);
        List<List<Integer>> fixableList = new ArrayList<>(kindNum);
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
            logger.info("this turn's target:" + targetList + "\n");
            for (List<Integer> target : targetList) {
                double temp = supportDegree(target);
                supportDegreeResult.put(Arrays.toString(target.toArray()), temp);
                if (temp > MIN_SUPPORT_DEGREE) {
                    fixableList.add(target);
                }
            }
            targetList.clear();
            targetSize++;
            for (int i = 0; i < fixableList.size(); i++){
                if (targetSize <= 5) {
                    for (int j = i; j < fixableList.size(); j++) {
                        List<Integer> temp = pair(fixableList.get(i), fixableList.get(j));
                        if (temp != null) {
                            targetList.add(temp);
                        }
                    }
                }
                believeDegreeMission++;
                believeDegreeAnalyze(fixableList.get(i));
            }
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
     * 获得可靠的关联规则表
     * @return
     */
    public Map<String, Double> getFixableRuleMap() {
        return fixableRuleMap;
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
    @Async
    void believeDegreeAnalyze(List<Integer> list){
        try {
            for (int i = 1; i < list.size(); i++) {
                /*获得长度为i的所有子集*/
                List<List<Integer>> partList = getPartList(list, i);

                if (partList == null){
                    partList = null;
                }

                for (List<Integer> part : partList) {
                    double tempBelieveDegree = believeDegree(list, part);
                    List<Integer> rest = new ArrayList<>(list.size() - part.size());
                    for (int j : list){
                        if (!part.contains(j)){
                            rest.add(j);
                        }
                    }
                    String str = Arrays.toString(part.toArray()) + "->" + Arrays.toString(rest.toArray());
                    believeDegreeResult.put(str, tempBelieveDegree);
                    if (tempBelieveDegree >= MIN_BELIEVE_DEGREE){
                        boolean flag = true;
                        for (int temp : part){
                            if (temp > 20000) {
                                flag = false;
                                break;
                            }
                        }
                        for (int temp : rest){
                            if (temp < 20000) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            str = Arrays.toString(id2Name(part).toArray()) + "->" + Arrays.toString(id2Name(rest).toArray());
                            fixableRuleMap.put(str, tempBelieveDegree);
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

    public boolean isBelieveDegreeFinished(){
        return (believeDegreeMission == 0);
    }

}