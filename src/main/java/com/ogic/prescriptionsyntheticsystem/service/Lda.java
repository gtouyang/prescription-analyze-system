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
 */
public class Lda {
    int [][] data;//drug index array
    int drugNumber, allDiagnosisSize, sampleNumber;//vocabulary size, diagnosis number, sample number
    int [][] z;//diagnosis label array
    float alpha; //sample-diagnosis dirichlet prior parameter
    float beta; //diagnosis-drug dirichlet prior parameter
    int [][] nmk;//given sample m, count times of diagnosis k. M*K
    int [][] nkt;//given diagnosis k, count times of term t. K*V
    int [] nmkSum;//Sum for each row in nmk
    int [] nktSum;//Sum for each row in nkt
    double [][] phi;//Parameters for diagnosis-drug distribution K*V
    double [][] theta;//Parameters for sample-diagnosis distribution M*K
    int iterations;//Times of iterations
    int saveStep;//The number of iterations between two saving
    int beginSaveIters;//Begin save model at this iteration
    List<Sample> samples;
    List<String> diagnosisStrList;
    List<String> drugStrList;

    public Lda(int allDiagnosisSize, float alpha, float beta, int iterations, int saveStep, int beginSaveIters) {
        this.allDiagnosisSize = allDiagnosisSize;
        this.alpha = alpha;
        this.beta = beta;
        this.iterations = iterations;
        this.saveStep = saveStep;
        this.beginSaveIters = beginSaveIters;
    }

    public void initializeModel(List<Sample> samples, List<String> diagnosisStrList, List<String> drugStrList) {
        // TODO Auto-generated method stub
        sampleNumber = samples.size();
        this.samples = samples;
        this.diagnosisStrList = diagnosisStrList;
        this.drugStrList = drugStrList;
        drugNumber = drugStrList.size();
        nmk = new int [sampleNumber][allDiagnosisSize];
        nkt = new int[allDiagnosisSize][drugNumber];
        nmkSum = new int[sampleNumber];
        nktSum = new int[allDiagnosisSize];
        phi = new double[allDiagnosisSize][drugNumber];
        theta = new double[sampleNumber][allDiagnosisSize];

        //initialize samples index array
        data = new int[sampleNumber][];
        for(int m = 0; m < sampleNumber; m++){
            //Notice the limit of memory
            int N = samples.get(m).getDrugs().size();
            data[m] = new int[N];
            for(int n = 0; n < N; n++){
                data[m][n] = samples.get(m).getDrugs().get(n) - 20000;
            }
        }

        //initialize diagnosis lable z for each drug
        z = new int[sampleNumber][];
        for(int m = 0; m < sampleNumber; m++){
            int N = samples.get(m).getDrugs().size();
            z[m] = new int[N];
            List<Integer> thisSampleDiagnoses = samples.get(m).getDiagnoses();
            for(int n = 0; n < N; n++){
                int initDiagnosis = (int)(Math.random() * thisSampleDiagnoses.size());// From 0 to K - 1
                initDiagnosis = thisSampleDiagnoses.get(initDiagnosis) - 10000;
                z[m][n] = initDiagnosis;
                //number of drugs in sample m assigned to diagnosis initDiagnosis add 1
                nmk[m][initDiagnosis]++;
                //number of terms sample[m][n] assigned to diagnosis initDiagnosis add 1
                nkt[initDiagnosis][data[m][n]]++;
                // total number of drugs assigned to diagnosis initDiagnosis add 1
                nktSum[initDiagnosis]++;
            }
            // total number of drugs in sample m is N
            nmkSum[m] = N;
        }
    }

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

            //Use Gibbs Sampling to update z[][]
            for(int m = 0; m < sampleNumber; m++){
                int N = samples.get(m).getDrugs().size();
                for(int n = 0; n < N; n++){
                    // Sample from p(z_i|z_-i, w)
                    int newDiagnosis = sampleDiagnosisZ(m, n);
                    z[m][n] = newDiagnosis;
                }
            }
        }
    }

    private void updateEstimatedParameters() {
        // TODO Auto-generated method stub
        for(int k = 0; k < allDiagnosisSize; k++){
            for(int t = 0; t < drugNumber; t++){
                phi[k][t] = (nkt[k][t] + beta) / (nktSum[k] + drugNumber * beta);
            }
        }

        for(int m = 0; m < sampleNumber; m++){
            for(int k = 0; k < allDiagnosisSize; k++){
                theta[m][k] = (nmk[m][k] + alpha) / (nmkSum[m] + allDiagnosisSize * alpha);
            }
        }
    }

    private int sampleDiagnosisZ(int m, int n) {
        // TODO Auto-generated method stub
        // Sample from p(z_i|z_-i, w) using Gibbs upde rule

        //Remove diagnosis label for w_{m,n}
        int oldDiagnosis = z[m][n];
        nmk[m][oldDiagnosis]--;
        nkt[oldDiagnosis][data[m][n]]--;
        nmkSum[m]--;
        nktSum[oldDiagnosis]--;

        //Compute p(z_i = k|z_-i, w)
        List<Integer> thisSampleDiagnoses = samples.get(m).getDiagnoses();
        double [] p = new double[thisSampleDiagnoses.size()];
        for(int k = 0; k < thisSampleDiagnoses.size(); k++){
            p[k] = (nkt[thisSampleDiagnoses.get(k)-10000][data[m][n]] + beta) / (nktSum[thisSampleDiagnoses.get(k)-10000] + drugNumber * beta) * (nmk[m][thisSampleDiagnoses.get(k)-10000] + alpha) / (nmkSum[m] + allDiagnosisSize * alpha);
        }

        //Sample a new diagnosis label for w_{m, n} like roulette
        //Compute cumulated probability for p
        for(int k = 1; k < thisSampleDiagnoses.size(); k++){
            p[k] += p[k - 1];
        }
        double u = Math.random() * p[thisSampleDiagnoses.size() - 1]; //p[] is unnormalised
        int newDiagnosis;
        for(newDiagnosis = 0; newDiagnosis < thisSampleDiagnoses.size(); newDiagnosis++){
            if(u < p[newDiagnosis]){
                break;
            }
        }

        newDiagnosis = thisSampleDiagnoses.get(newDiagnosis)-10000;

        //Add new diagnosis label for w_{m, n}
        nmk[m][newDiagnosis]++;
        nkt[newDiagnosis][data[m][n]]++;
        nmkSum[m]++;
        nktSum[newDiagnosis]++;
        return newDiagnosis;
    }

    public void saveIteratedModel(int iters) throws IOException {
        // TODO Auto-generated method stub
        //lda.params lda.phi lda.theta lda.tassign lda.tdrugs
        //lda.params
        String resPath = "/home/ogic/Desktop/";
        String modelName = "lda_" + iters;
        List<String> lines = new ArrayList<String>();
        lines.add("alpha = " + alpha);
        lines.add("beta = " + beta);
        lines.add("diagnosisNum = " + allDiagnosisSize);
        lines.add("sampleNum = " + sampleNumber);
        lines.add("termNum = " + drugNumber);
        lines.add("iterations = " + iterations);
        lines.add("saveStep = " + saveStep);
        lines.add("beginSaveIters = " + beginSaveIters);
//        FileUtil.writeLines(resPath + modelName + ".params", lines);

        //lda.phi K*V
        BufferedWriter writer = new BufferedWriter(new FileWriter(resPath + modelName + ".phi"));
        for (int i = 0; i < allDiagnosisSize; i++){
            for (int j = 0; j < drugNumber; j++){
                writer.write(phi[i][j] + "\t");
            }
            writer.write("\n");
        }
        writer.close();

        //lda.theta M*K
        writer = new BufferedWriter(new FileWriter(resPath + modelName + ".theta"));
        for(int i = 0; i < sampleNumber; i++){
            for(int j = 0; j < allDiagnosisSize; j++){
                writer.write(theta[i][j] + "\t");
            }
            writer.write("\n");
        }
        writer.close();

        //lda.tassign
        writer = new BufferedWriter(new FileWriter(resPath + modelName + ".tassign"));
        for(int m = 0; m < sampleNumber; m++){
            for(int n = 0; n < data[m].length; n++){
                writer.write(data[m][n] + ":" + z[m][n] + "\t");
            }
            writer.write("\n");
        }
        writer.close();

        //lda.tdrugs phi[][] K*V
        writer = new BufferedWriter(new FileWriter(resPath + modelName + ".tdrugs"));
        int topNum = 20; //Find the top 20 diagnosis drugs in each diagnosis
        for(int i = 0; i < allDiagnosisSize; i++){
            List<Integer> tDrugsIndexArray = new ArrayList<Integer>();
            for(int j = 0; j < drugNumber; j++){
                tDrugsIndexArray.add(new Integer(j));
            }
            Collections.sort(tDrugsIndexArray, new Lda.TdrugsComparable(phi[i]));
            writer.write("diagnosis " + i + "\t" + diagnosisStrList.get(i) +"\t:\n");
            for(int t = 0; t < topNum; t++){
                writer.write(drugStrList.get(tDrugsIndexArray.get(t)) + " " + phi[i][tDrugsIndexArray.get(t)] + "\n");
            }
            writer.write("\n");
        }
        writer.close();
    }

    public class TdrugsComparable implements Comparator<Integer> {

        public double [] sortProb; // Store probability of each drug in diagnosis k

        public TdrugsComparable (double[] sortProb){
            this.sortProb = sortProb;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            // TODO Auto-generated method stub
            //Sort diagnosis drug index according to the probability of each drug in diagnosis k
            if(sortProb[o1] > sortProb[o2]) {
                return -1;
            } else if(sortProb[o1] < sortProb[o2]) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
