package com.ogic.prescriptionsyntheticsystem.service;

import ch.qos.logback.core.util.FileUtil;
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
    int [][] doc;//word index array
    int drugNumber, diagnosisNumber, sampleNumber;//vocabulary size, topic number, document number
    int [][] z;//topic label array
    float alpha; //doc-topic dirichlet prior parameter
    float beta; //topic-word dirichlet prior parameter
    int [][] nmk;//given document m, count times of topic k. M*K
    int [][] nkt;//given topic k, count times of term t. K*V
    int [] nmkSum;//Sum for each row in nmk
    int [] nktSum;//Sum for each row in nkt
    double [][] phi;//Parameters for topic-word distribution K*V
    double [][] theta;//Parameters for doc-topic distribution M*K
    int iterations;//Times of iterations
    int saveStep;//The number of iterations between two saving
    int beginSaveIters;//Begin save model at this iteration

    public Lda(int diagnosisNumber, float alpha, float beta, int iterations, int saveStep, int beginSaveIters) {
        this.diagnosisNumber = diagnosisNumber;
        this.alpha = alpha;
        this.beta = beta;
        this.iterations = iterations;
        this.saveStep = saveStep;
        this.beginSaveIters = beginSaveIters;
    }

    public void initializeModel(List<Sample> samples, List<String> drugStrList) {
        // TODO Auto-generated method stub
        sampleNumber = samples.size();
        drugNumber = drugStrList.size();
        nmk = new int [sampleNumber][diagnosisNumber];
        nkt = new int[diagnosisNumber][drugNumber];
        nmkSum = new int[sampleNumber];
        nktSum = new int[diagnosisNumber];
        phi = new double[diagnosisNumber][drugNumber];
        theta = new double[sampleNumber][diagnosisNumber];

        //initialize documents index array
        doc = new int[sampleNumber][];
        for(int m = 0; m < sampleNumber; m++){
            //Notice the limit of memory
            int N = samples.get(m).getDrugs().size();
            doc[m] = new int[N];
            for(int n = 0; n < N; n++){
                doc[m][n] = samples.get(m).getDrugs().get(n);
            }
        }

        //initialize topic lable z for each word
        z = new int[sampleNumber][];
        for(int m = 0; m < sampleNumber; m++){
            int N = samples.get(m).getDrugs().size();
            z[m] = new int[N];
            for(int n = 0; n < N; n++){
                int initTopic = (int)(Math.random() * diagnosisNumber);// From 0 to K - 1
                z[m][n] = initTopic;
                //number of words in doc m assigned to topic initTopic add 1
                nmk[m][initTopic]++;
                //number of terms doc[m][n] assigned to topic initTopic add 1
                nkt[initTopic][doc[m][n]]++;
                // total number of words assigned to topic initTopic add 1
                nktSum[initTopic]++;
            }
            // total number of words in document m is N
            nmkSum[m] = N;
        }
    }

    public void inferenceModel(List<Sample> samples, List<String> drugStrList) throws IOException {
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
                saveIteratedModel(i, samples, drugStrList);
            }

            //Use Gibbs Sampling to update z[][]
            for(int m = 0; m < sampleNumber; m++){
                int N = samples.get(m).getDrugs().size();
                for(int n = 0; n < N; n++){
                    // Sample from p(z_i|z_-i, w)
                    int newTopic = sampleTopicZ(m, n);
                    z[m][n] = newTopic;
                }
            }
        }
    }

    private void updateEstimatedParameters() {
        // TODO Auto-generated method stub
        for(int k = 0; k < diagnosisNumber; k++){
            for(int t = 0; t < drugNumber; t++){
                phi[k][t] = (nkt[k][t] + beta) / (nktSum[k] + drugNumber * beta);
            }
        }

        for(int m = 0; m < sampleNumber; m++){
            for(int k = 0; k < diagnosisNumber; k++){
                theta[m][k] = (nmk[m][k] + alpha) / (nmkSum[m] + diagnosisNumber * alpha);
            }
        }
    }

    private int sampleTopicZ(int m, int n) {
        // TODO Auto-generated method stub
        // Sample from p(z_i|z_-i, w) using Gibbs upde rule

        //Remove topic label for w_{m,n}
        int oldTopic = z[m][n];
        nmk[m][oldTopic]--;
        nkt[oldTopic][doc[m][n]]--;
        nmkSum[m]--;
        nktSum[oldTopic]--;

        //Compute p(z_i = k|z_-i, w)
        double [] p = new double[diagnosisNumber];
        for(int k = 0; k < diagnosisNumber; k++){
            p[k] = (nkt[k][doc[m][n]] + beta) / (nktSum[k] + drugNumber * beta) * (nmk[m][k] + alpha) / (nmkSum[m] + diagnosisNumber * alpha);
        }

        //Sample a new topic label for w_{m, n} like roulette
        //Compute cumulated probability for p
        for(int k = 1; k < diagnosisNumber; k++){
            p[k] += p[k - 1];
        }
        double u = Math.random() * p[diagnosisNumber - 1]; //p[] is unnormalised
        int newTopic;
        for(newTopic = 0; newTopic < diagnosisNumber; newTopic++){
            if(u < p[newTopic]){
                break;
            }
        }

        //Add new topic label for w_{m, n}
        nmk[m][newTopic]++;
        nkt[newTopic][doc[m][n]]++;
        nmkSum[m]++;
        nktSum[newTopic]++;
        return newTopic;
    }

    public void saveIteratedModel(int iters, List<Sample> samples, List<String> drugStrList) throws IOException {
        // TODO Auto-generated method stub
        //lda.params lda.phi lda.theta lda.tassign lda.twords
        //lda.params
        String resPath = "/home/ogic/Desktop/";
        String modelName = "lda_" + iters;
        List<String> lines = new ArrayList<String>();
        lines.add("alpha = " + alpha);
        lines.add("beta = " + beta);
        lines.add("topicNum = " + diagnosisNumber);
        lines.add("docNum = " + sampleNumber);
        lines.add("termNum = " + drugNumber);
        lines.add("iterations = " + iterations);
        lines.add("saveStep = " + saveStep);
        lines.add("beginSaveIters = " + beginSaveIters);
//        FileUtil.writeLines(resPath + modelName + ".params", lines);

        //lda.phi K*V
        BufferedWriter writer = new BufferedWriter(new FileWriter(resPath + modelName + ".phi"));
        for (int i = 0; i < diagnosisNumber; i++){
            for (int j = 0; j < drugNumber; j++){
                writer.write(phi[i][j] + "\t");
            }
            writer.write("\n");
        }
        writer.close();

        //lda.theta M*K
        writer = new BufferedWriter(new FileWriter(resPath + modelName + ".theta"));
        for(int i = 0; i < sampleNumber; i++){
            for(int j = 0; j < diagnosisNumber; j++){
                writer.write(theta[i][j] + "\t");
            }
            writer.write("\n");
        }
        writer.close();

        //lda.tassign
        writer = new BufferedWriter(new FileWriter(resPath + modelName + ".tassign"));
        for(int m = 0; m < sampleNumber; m++){
            for(int n = 0; n < doc[m].length; n++){
                writer.write(doc[m][n] + ":" + z[m][n] + "\t");
            }
            writer.write("\n");
        }
        writer.close();

        //lda.twords phi[][] K*V
        writer = new BufferedWriter(new FileWriter(resPath + modelName + ".twords"));
        int topNum = 20; //Find the top 20 topic words in each topic
        for(int i = 0; i < diagnosisNumber; i++){
            List<Integer> tWordsIndexArray = new ArrayList<Integer>();
            for(int j = 0; j < drugNumber; j++){
                tWordsIndexArray.add(new Integer(j));
            }
            Collections.sort(tWordsIndexArray, new Lda.TwordsComparable(phi[i]));
            writer.write("topic " + i + "\t:\t");
            for(int t = 0; t < topNum; t++){
                writer.write(drugStrList.get(tWordsIndexArray.get(t)) + " " + phi[i][tWordsIndexArray.get(t)] + "\t");
            }
            writer.write("\n");
        }
        writer.close();
    }

    public class TwordsComparable implements Comparator<Integer> {

        public double [] sortProb; // Store probability of each word in topic k

        public TwordsComparable (double[] sortProb){
            this.sortProb = sortProb;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            // TODO Auto-generated method stub
            //Sort topic word index according to the probability of each word in topic k
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
