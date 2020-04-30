package com.ogic.prescriptionsyntheticsystem.service;

import com.ogic.prescriptionsyntheticsystem.entity.DrugDetail;
import com.ogic.prescriptionsyntheticsystem.entity.Sample;

import java.util.*;

/**
 * @author ogic
 */
public class Kmeans{

    /**
     * 总样本列表
     */
    private final List<Sample> originSampleList;

    /**
     * 以诊断组合为根据的样本分类表
     */
    private final Map<String, Map<String, List<Sample>>> dataMap;

    private final Map<Integer, List<Sample>> drug2SampleMap;

    private List<Integer> maxSamplesDiagnoses;

    public Kmeans(List<Sample> originSampleList) {
        int maxSamplesNumber = 0;
        this.originSampleList = originSampleList;
        dataMap = new HashMap<>();
        drug2SampleMap = new HashMap<>();
        for (Sample sample : originSampleList){
            String diagnosisStr = Arrays.toString(sample.getDiagnoses().toArray());
            String drugListStr = Arrays.toString(sample.getDrugs().toArray());
            if (dataMap.containsKey(diagnosisStr)){
                if (dataMap.get(diagnosisStr).containsKey(drugListStr)) {
                    dataMap.get(diagnosisStr).get(drugListStr).add(sample);
                }else {
                    List<Sample> newSampleList = new ArrayList<>();
                    newSampleList.add(sample);
                    dataMap.get(diagnosisStr).put(drugListStr, newSampleList);
                }
            }
            else {
                List<Sample> newSampleList = new ArrayList<>();
                newSampleList.add(sample);
                Map<String, List<Sample>> newMap = new HashMap<>();
                newMap.put(drugListStr, newSampleList);
                dataMap.put(diagnosisStr, newMap);
            }
            int size = dataMap.get(diagnosisStr).size();
            if (size > maxSamplesNumber){
                maxSamplesDiagnoses = sample.getDiagnoses();
                maxSamplesNumber = size;
            }
            for (Integer drug : sample.getDrugs()){
                if (drug2SampleMap.containsKey(drug)){
                    drug2SampleMap.get(drug).add(sample);
                }else {
                    List<Sample> newSampleList = new ArrayList<>();
                    newSampleList.add(sample);
                    drug2SampleMap.put(drug, newSampleList);
                }
            }
        }
    }


    public List<List<Sample>> analyzeDiagnosisDrug(List<Integer> diagnosis, List<Integer> drug){
        int sampleListSize = dataMap.get(Arrays.toString(diagnosis.toArray()))
                .get(Arrays.toString(drug.toArray())).size();
        return analyzeDiagnosisDrugByK(diagnosis, drug, 2);
    }

    /**
     * 将指定诊断和指定药物的处方使用K-means算法分成K组
     * @param diagnosis 指定的诊断ID列表
     * @param drug      指定的药物ID列表
     * @param k         分类的数量
     * @return          分类后的样本列表
     */
    public List<List<Sample>> analyzeDiagnosisDrugByK(List<Integer> diagnosis,
                                                      List<Integer> drug,
                                                      final int k){

        /*初始化*/
        List<List<Sample>> result = new ArrayList<>(k);
        List<Sample> samples = dataMap.get(Arrays.toString(diagnosis.toArray()))
                .get(Arrays.toString(drug.toArray()));
        int[] family = new int[samples.size()];
        int size = samples.get(0).getDrugDetails().size();

        /*中心点或者簇的数量必须大于0*/
        if (k < 1){
            return null;
        }

        /*如果中心点或簇的数量少于样本点的数量也没有分类的意义*/
        if (samples.size() < k){
            samples.sort((o1, o2) -> {
                int sum1 = 0;
                int sum2 = 0;
                for (DrugDetail drugDetail : o1.getDrugDetails()){
                    sum1 += drugDetail.getAmount();
                }
                for (DrugDetail drugDetail : o2.getDrugDetails()){
                    sum2 += drugDetail.getAmount();
                }
                return sum1 - sum2;
            });
            for (int i = 0; i < samples.size(); i++){
                result.add(samples.subList(i, i+1));
                return result;
            }
        }

        /*将算法中需要用到的药物的数量提取出来加快比对过程*/
        int[][] data = new int[samples.size()][];
        for (int i = 0; i < samples.size(); i++){
            Sample sample = samples.get(i);
            List<DrugDetail> drugDetails = sample.getDrugDetails();
            data[i] = new int[drugDetails.size()];
            for (int j = 0; j < drugDetails.size(); j++){
                data[i][j] = drugDetails.get(j).getAmount();
            }
        }

        /*随机选取样本点中的K个点作为初始中心点*/
        double[][] centrePoint = new double[k][];
        Random random = new Random();
        for (int i = 0; i < k; i++){
            centrePoint[i] = new double[size];
            for (int j = 0; j < size; j++){
                centrePoint[i][j] = samples.get(random.nextInt(samples.size())).getDrugDetails().get(j).getAmount();
            }
        }

        /*迭代运算*/
        boolean isConvergence = false;
        while (!isConvergence){
            isConvergence = true;

            /*遍历所有样本点，找到离这样本点最近的中心点*/
            for (int i = 0; i < samples.size(); i++){
                Sample sample = samples.get(i);
                int closedPointIndex = 0;
                double closedPointDistance = -1;
                double distance = 0;
                for (int j = 0; j < k; j++){
                    for (int m = 0; m < size; m++) {
                        distance = Math.pow(centrePoint[j][m] - sample.getDrugDetails().get(m).getAmount(), 2);
                        if (distance < closedPointDistance || closedPointDistance < 0) {
                            closedPointIndex = j;
                            closedPointDistance = distance;
                        }
                    }
                }

                /*如果该样本点之前所在的簇不是该中心点所在的簇，那说明算法还没有收敛，并将该样本点分类到新簇*/
                if (family[i] != closedPointIndex){
                    family[i] = closedPointIndex;
                    isConvergence = false;
                }
            }

            /*如果算法还没收敛则重新计算中心点的坐标*/
            if (!isConvergence){
                for (int i = 0; i < k; i++){
                    int[] temp = new int[size];
                    int familySize = 0;
                    for (int m = 0; m < size; m++){
                        temp[m] = 0;
                    }
                    for (int j = 0; j < family.length; j++){
                        if (family[j] == i) {
                            familySize++;
                            for (int m = 0; m < size; m++) {
                                temp[m] += samples.get(j).getDrugDetails().get(m).getAmount();
                            }
                        }
                    }

                    //如果该中心点所属的簇内没有任何样本点，则不变
                    for (int m = 0; m < size; m++){
                        if(familySize > 0) {
                            centrePoint[i][m] = temp[m] * 1.0 / familySize;
                        }
                    }
                }
            }

            /*如果已经收敛了则输出结果*/
            else {

                /*给中心点按照中心点到原点的距离升序排序*/
                double[] centrePointDistanceFromOriginSquared = new double[k];
                int[] centrePointSort = new int[k];
                for (int i = 0; i < k; i++){
                    centrePointDistanceFromOriginSquared[i] = 0.0;
                    for (int m = 0; m < size; m++){
                        centrePointDistanceFromOriginSquared[i] += Math.pow(centrePoint[i][m], 2);
                    }
                    boolean isInserted = false;
                    for (int j = 0; j < i; j++){
                        if (centrePointDistanceFromOriginSquared[i] < centrePointDistanceFromOriginSquared[centrePointSort[j]]){
                            System.arraycopy(centrePointSort, j, centrePointSort, j + 1, i - j);
                            centrePointSort[j] = i;
                            isInserted = true;
                            break;
                        }
                    }
                    if (!isInserted){
                        centrePointSort[i] = i;
                    }
                }

                for (int i : centrePointSort){
                    List<Sample> temp = new ArrayList<>();
                    for (int j = 0; j < family.length; j++){
                        if (family[j] == i){
                            temp.add(samples.get(j));
                        }
                    }
                    if (temp.size() > 0){
                        result.add(temp);
                    }
                }
            }

        }
        return result;
    }

    /**
     * 获取该诊断下的样本列表
     * @param diagnosisList 诊断列表
     * @return  过去的样本列表
     */
    public Map<String,List<Sample>> getDiagnosisDrugSampleMapByDiagnosis(List<Integer> diagnosisList){
        diagnosisList.sort(Comparator.comparingInt(o -> o));
        return dataMap.get(Arrays.toString(diagnosisList.toArray()));
    }

    /**
     * 获取样本数量最多的症状的样本
     * @return  样本列表
     */
    public Map<String,List<Sample>> getMaxDiagnosisDrugSampleMap(){
        return dataMap.get(Arrays.toString(maxSamplesDiagnoses.toArray()));
    }

    public List<Integer> getMaxSamplesDiagnoses() {
        return maxSamplesDiagnoses;
    }


    /**
     * 获得药物范围
     * @param drug      指定的药物ID
     * @return          范围
     */
    public List<Integer> getDrugRange(Integer drug){

        /*初始化*/
        List<Integer> result = new ArrayList<>(4);
        List<Sample> samples = drug2SampleMap.get(drug);


        int min = 999999;
        int max = 0;
        int k = 6;

        /*将算法中需要用到的药物的数量提取出来加快比对过程*/
        List<Integer> data = new ArrayList<>(samples.size());

        for (int i = 0; i < samples.size(); i++){
            Sample sample = samples.get(i);
            List<DrugDetail> drugDetails = sample.getDrugDetails();
            for (DrugDetail drugDetail : drugDetails){
                if (drugDetail.getDrugId() == drug && drugDetail.getAmount() > 0){
                    if (min > drugDetail.getAmount()){
                        min = drugDetail.getAmount();
                    }
                    if (max < drugDetail.getAmount()){
                        max = drugDetail.getAmount();
                    }
                    data.add(drugDetail.getAmount());
                    break;
                }
            }
        }

        int[] family = new int[data.size()];

        /*随机选取样本点中的K个点作为初始中心点*/
        double[] centrePoint = new double[k];
        Random random = new Random();
        for (int i = 0; i < k; i++){
            centrePoint[i] = random.nextDouble() * (max-min) + min;
        }

        /*迭代运算*/
        boolean isConvergence = false;

        while (!isConvergence){
            isConvergence = true;

            /*遍历所有样本点，找到离这样本点最近的中心点*/
            for (int i = 0; i < data.size(); i++){
                int closedPointIndex = 0;
                double closedPointDistance = -1;
                double distance = 0.0;
                for (int j = 0; j < k; j++){
                        distance = Math.abs(centrePoint[j] - data.get(i));
                        if (distance < closedPointDistance || closedPointDistance < 0) {
                            closedPointIndex = j;
                            closedPointDistance = distance;
                        }
                }

                /*如果该样本点之前所在的簇不是该中心点所在的簇，那说明算法还没有收敛，并将该样本点分类到新簇*/
                if (family[i] != closedPointIndex){
                    family[i] = closedPointIndex;
                    isConvergence = false;
                }
            }

            /*如果算法还没收敛则重新计算中心点的坐标*/
            if (!isConvergence){
                for (int i = 0; i < k; i++){
                    double temp = 0.0;
                    int familySize = 0;
                    for (int j = 0; j < family.length; j++){
                        if (family[j] == i) {
                            temp += data.get(j);
                            familySize ++;
                        }
                    }

                    //如果该中心点所属的簇内没有任何样本点，则不变
                    if(familySize > 0) {
                        centrePoint[i] = temp / familySize;
                    }
                }
            }

            /*如果已经收敛了则输出结果*/
            else {
                int lastMaxCentrePointIndex = 0;
                int maxCentrePointIndex = 0;
                double maxCentrePointValue = centrePoint[0];
                for (int i = 1; i < k; i++){
                    if (maxCentrePointValue < centrePoint[i]){
                        maxCentrePointValue = centrePoint[i];
                        lastMaxCentrePointIndex = maxCentrePointIndex;
                        maxCentrePointIndex = i;
                    }
                }
                int maxNormalAmount = 0;
                int minMoreAmount = 99999999;
                for (int i = 0; i < data.size(); i++){
                    if (family[i] == lastMaxCentrePointIndex || family[i] == maxCentrePointIndex){
                        if (minMoreAmount > data.get(i)){
                            minMoreAmount = data.get(i);
                        }
                    }else {
                        if (maxNormalAmount < data.get(i)){
                            maxNormalAmount = data.get(i);
                        }
                    }
                }
                result.add(min);
                result.add(maxNormalAmount);
                result.add(minMoreAmount);
                result.add(max);

            }

        }
        return result;
    }
}
