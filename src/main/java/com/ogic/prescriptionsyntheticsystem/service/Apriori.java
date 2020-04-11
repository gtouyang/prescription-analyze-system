package com.ogic.prescriptionsyntheticsystem.service;

import com.ogic.prescriptionsyntheticsystem.entity.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author ogic
 */
public class Apriori {

    private final double MIN_SUPPORT_DEGREE = 0.0001;

    private final List<Sample> sampleList;

    private final List<String> diagnosisList;

    private final List<String> drugList;

    private final List<List<Integer>> data;

    private Map<List<Integer>, Double> result;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Apriori(List<Sample> sampleList, List<String> diagnosisList, List<String> drugList) {
        this.sampleList = sampleList;
        this.diagnosisList = diagnosisList;
        this.drugList = drugList;
        data = new ArrayList<>(sampleList.size());
        for (int i = 0; i < sampleList.size(); i++){
            List<Integer> temp = new ArrayList<>(sampleList.get(i).getDiagnosis().size() + sampleList.get(i).getDrugs().size());
            temp.addAll(sampleList.get(i).getDiagnosis());
            temp.addAll(sampleList.get(i).getDrugs());
            data.add(temp);
        }
        for (List<Integer> list : data){
            logger.debug(Arrays.toString(list.toArray()));
        }
    }

    public void run(){
        int kindNum = diagnosisList.size() + drugList.size();
        result = new HashMap<>();
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
        while (!targetList.isEmpty()) {
            for (List<Integer> target : targetList) {
                double temp = supportDegree(target);
                result.put(target, temp);
                if (temp > MIN_SUPPORT_DEGREE) {
                    fixableList.add(target);
                }
            }
            targetList.clear();
            for (int i = 0; i < fixableList.size(); i++){
                for (int j = i; j < fixableList.size(); j++){
                    List<Integer> temp = pair(fixableList.get(i), fixableList.get(j));
                    if (temp != null){
                        targetList.add(temp);
                    }
                }
            }
        }

    }

    public Map<List<Integer>, Double> getResult() {
        return result;
    }

    private double supportDegree(List<Integer> a){
        int includeNum = 0;
        boolean flag;
        for (List<Integer> s: data){
            flag = true;
            for (int i:a){
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
                return result;
            }
        }
        return null;
    }

}