package com.ogic.prescriptionsyntheticsystem.service;

import com.ogic.prescriptionsyntheticsystem.entity.Sample;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Mr.chen
 * 以下注释所说的数量不是指药方中该药物开了多少g或多少片,指代的是在样本中的出现次数
 */
public class Lda {

    /**
     * diagnosisData 第m个处方样本中的第n个诊断的诊断ID
     */
    private int [][] diagnosisData;

    /**
     * drugData[m][n] 第m个处方样本中的第n个药物的药物ID
     */
    private int [][] drugData;

    /**
     * 总药物种类数量，总诊断种类数量，总样本数量
     */
    private int allDrugSize;
    private int allDiagnosisSize;
    private int sampleSize;

    /**
     * z[m][n] 第m个处方样本中的第n个药物所属的诊断的诊断ID
     */
    private int [][] z;

    /**
     * 计算dirichlet分布的参数之一
     */
    private final float alpha;

    /**
     * 计算dirichlet分布的参数之一
     */
    private final float beta;

    /**
     * nmk[m][n] 在第m个样本处方中，属于第n个诊断的药物的数量
     */
    private int [][] nmk;

    /**
     * nkt[m][n] 属于第m个诊断的第n种药物的数量
     */
    private int [][] nkt;

    /**
     * nmkSum[m] 第m个处方样本下药物的数量
     */
    private int [] nmkSum;

    /**
     * nktSum[m] 第m个诊断下药物的数量
     */
    private int [] nktSum;

    /**
     * phi[k][t] = (nkt[k][t] + beta) / (nktSum[k] + allDrugSize * beta);
     */
    private double [][] phi;

    /**
     * theta[m][k] = (nmk[m][k] + alpha) / (nmkSum[m] + allDiagnosisSize * alpha);
     */
    private double [][] theta;

    /**
     * 迭代的次数
     */
    private final int iterations;

    /**
     * 每隔saveStep代保存一次
     */
    private final int saveStep;

    /**
     * 在第beginSaveIters代开始保存
     */
    private final int beginSaveIters;

    /**
     * 总样本列表
     */
    private List<Sample> samples;

    /**
     * 诊断ID-诊断名 列表
     */
    private List<String> diagnosisStrList;

    /**
     * 药物ID-药物名 列表
     */
    private List<String> drugStrList;

    public Lda(float alpha, float beta, int iterations, int saveStep, int beginSaveIters) {

        this.alpha = alpha;
        this.beta = beta;
        this.iterations = iterations;
        this.saveStep = saveStep;
        this.beginSaveIters = beginSaveIters;
    }

    /**
     * 初始化模型
     * @param samples   总样本列表
     * @param diagnosisStrList 诊断ID-诊断名 列表
     * @param drugStrList 药物ID-药物名 列表
     */
    public void initializeModel(List<Sample> samples, List<String> diagnosisStrList, List<String> drugStrList) {
        // TODO Auto-generated method stub
        sampleSize = samples.size();
        this.samples = samples;
        this.diagnosisStrList = diagnosisStrList;
        this.drugStrList = drugStrList;
        allDrugSize = drugStrList.size();
        allDiagnosisSize = diagnosisStrList.size();
        nmk = new int [sampleSize][allDiagnosisSize];
        nkt = new int[allDiagnosisSize][allDrugSize];
        nmkSum = new int[sampleSize];
        nktSum = new int[allDiagnosisSize];
        phi = new double[allDiagnosisSize][allDrugSize];
        theta = new double[sampleSize][allDiagnosisSize];

        /* 初始化模型的数据集 */
        drugData = new int[sampleSize][];
        diagnosisData = new int[sampleSize][];
        for(int m = 0; m < sampleSize; m++){
            int N = samples.get(m).getDrugs().size();
            drugData[m] = new int[N];
            for(int n = 0; n < N; n++){
                /*将处方样本-药物数据保存在drugData*/
                drugData[m][n] = samples.get(m).getDrugs().get(n) - 20000;
            }
            int T = samples.get(m).getDiagnoses().size();
            diagnosisData[m] = new int[T];
            for (int t = 0; t < T; t++){
                /*将处方样本-诊断数据保存在diagnosisData*/
                diagnosisData[m][t] = samples.get(m).getDiagnoses().get(t) - 10000;
            }
        }

        z = new int[sampleSize][];
        for(int m = 0; m < sampleSize; m++){
            int N = samples.get(m).getDrugs().size();
            z[m] = new int[N];
            for(int n = 0; n < N; n++){
                /*随机给处方样本中的药物分配诊断,该诊断在该样本中随机选取*/
                int initDiagnosis = (int)(Math.random() * diagnosisData[m].length);
                initDiagnosis =diagnosisData[m][initDiagnosis];
                z[m][n] = initDiagnosis;
                nmk[m][initDiagnosis]++;
                nkt[initDiagnosis][drugData[m][n]]++;
                nktSum[initDiagnosis]++;
            }
            nmkSum[m] = N;
        }
    }

    /**
     * 模型迭代
     * @throws IOException  写文件异常
     */
    public void inferenceModel() throws IOException {
        // TODO Auto-generated method stub
        if(iterations < saveStep + beginSaveIters){
            System.err.println("Error: the number of iterations should be larger than " + (saveStep + beginSaveIters));
            System.exit(0);
        }
        for(int i = 0; i < iterations; i++){
            System.out.println("Iteration " + i);
            if((i >= beginSaveIters) && (((i - beginSaveIters) % saveStep) == 0)){
                //Saving the model
                System.out.println("Saving model at iteration " + i +" ... ");
                //Firstly update parameters
                updateEstimatedParameters();
                //Secondly print model variables
                saveIteratedModel(i);
            }

            //使用 Gibbs Sampling 更新 z[][]
            for(int m = 0; m < sampleSize; m++){
                int N = samples.get(m).getDrugs().size();
                for(int n = 0; n < N; n++){
                    // 更新z[m][n]
                    int newDiagnosis = sampleDiagnosisZ(m, n);
                    z[m][n] = newDiagnosis;
                }
            }
        }
    }

    /**
     * 更新phi和theta参数,这两个参数不参与模型迭代,只是作为观察模型迭代情况存在
     */
    private void updateEstimatedParameters() {
        // TODO Auto-generated method stub
        for(int k = 0; k < allDiagnosisSize; k++){
            for(int t = 0; t < allDrugSize; t++){
                phi[k][t] = (nkt[k][t] + beta) / (nktSum[k] + allDrugSize * beta);
            }
        }

        for(int m = 0; m < sampleSize; m++){
            for(int k = 0; k < allDiagnosisSize; k++){
                theta[m][k] = (nmk[m][k] + alpha) / (nmkSum[m] + allDiagnosisSize * alpha);
            }
        }
    }

    /**
     * 更新z[m][n],即重新给第m个处方样本中的第n个药物分配一个新的诊断,并更新相关数据集
     * @param m 第m个处方样本
     * @param n 第n个药物
     * @return  新诊断ID
     */
    private int sampleDiagnosisZ(int m, int n) {
        // TODO Auto-generated method stub

        /*先将该药物从它所属的诊断中移除*/
        int oldDiagnosis = z[m][n];
        nmk[m][oldDiagnosis]--;
        nkt[oldDiagnosis][drugData[m][n]]--;
        nmkSum[m]--;
        nktSum[oldDiagnosis]--;

        /*计算Dirichlet分布*/
        int[] thisSampleDiagnoses = diagnosisData[m];
        double [] p = new double[thisSampleDiagnoses.length];
        for(int k = 0; k < thisSampleDiagnoses.length; k++){
            p[k] = (nkt[thisSampleDiagnoses[k]][drugData[m][n]] + beta)
                    / (nktSum[thisSampleDiagnoses[k]] + allDrugSize * beta)
                    * (nmk[m][thisSampleDiagnoses[k]] + alpha)
                    / (nmkSum[m] + allDiagnosisSize * alpha);
        }

        /*根据Dirichlet分布计算该药物分别属于处方样本中各个诊断的概率*/
        for(int k = 1; k < thisSampleDiagnoses.length; k++){
            p[k] += p[k - 1];
        }

        /*根据概率给药物分配一个新诊断,该诊断有可能是该样本下的任一诊断,即也有可能是原来该药物所属的诊断*/
        double u = Math.random() * p[thisSampleDiagnoses.length - 1];
        int newDiagnosis;
        for(newDiagnosis = 0; newDiagnosis < thisSampleDiagnoses.length; newDiagnosis++){
            if(u < p[newDiagnosis]){
                break;
            }
        }
        newDiagnosis = thisSampleDiagnoses[newDiagnosis];

        /*更新相关的数据集*/
        nmk[m][newDiagnosis]++;
        nkt[newDiagnosis][drugData[m][n]]++;
        nmkSum[m]++;
        nktSum[newDiagnosis]++;
        return newDiagnosis;
    }

    /**
     * 保存模型
     * @param iters 模型年龄
     * @throws IOException  写文件异常
     */
    public void saveIteratedModel(int iters) throws IOException {
        // TODO Auto-generated method stub
        //lda.params lda.phi lda.theta lda.tassign lda.tdrugs
        //lda.params
        String resPath = "/home/ogic/Desktop/";
        String modelName = "lda_" + iters;

        /*保存phi参数*/
        BufferedWriter writer = new BufferedWriter(new FileWriter(resPath + modelName + ".phi"));
        for (int i = 0; i < allDiagnosisSize; i++){
            for (int j = 0; j < allDrugSize; j++){
                writer.write(phi[i][j] + "\t");
            }
            writer.write("\n");
        }
        writer.close();

        /*保存theta参数*/
        writer = new BufferedWriter(new FileWriter(resPath + modelName + ".theta"));
        for(int i = 0; i < sampleSize; i++){
            for(int j = 0; j < allDiagnosisSize; j++){
                writer.write(theta[i][j] + "\t");
            }
            writer.write("\n");
        }
        writer.close();

        /*保存drugData表和z表*/
        writer = new BufferedWriter(new FileWriter(resPath + modelName + ".tassign"));
        for(int m = 0; m < sampleSize; m++){
            for(int n = 0; n < drugData[m].length; n++){
                writer.write(drugData[m][n] + ":" + z[m][n] + "\t");
            }
            writer.write("\n");
        }
        writer.close();

        /*保存所有主题下的药物*/
        writer = new BufferedWriter(new FileWriter(resPath + modelName + ".tdrugs"));

        /*只保存该主题下出现次数最多的前20种药物*/
        int topNum = 20;
        for(int i = 0; i < allDiagnosisSize; i++){
            List<Integer> tDrugsIndexArray = new ArrayList<>();
            for(int j = 0; j < allDrugSize; j++){
                tDrugsIndexArray.add(j);
            }

            /*排序,将出现次数多的药物排在前面*/
            tDrugsIndexArray.sort(new TdrugsComparable(phi[i]));
            writer.write("diagnosis " + i + "\t" + diagnosisStrList.get(i) +"\t:\n");
            for(int t = 0; t < topNum; t++){
                writer.write(drugStrList.get(tDrugsIndexArray.get(t)) + " " + phi[i][tDrugsIndexArray.get(t)] + "\n");
            }
            writer.write("\n");
        }
        writer.close();
    }

    /*药物排序辅助方法*/
    public static class TdrugsComparable implements Comparator<Integer> {

        public double [] sortProb; // Store probability of each drug in diagnosis k

        public TdrugsComparable (double[] sortProb){
            this.sortProb = sortProb;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            // TODO Auto-generated method stub
            //Sort diagnosis drug index according to the probability of each drug in diagnosis k
            return Double.compare(sortProb[o2], sortProb[o1]);
        }
    }
}
