package com.ogic.prescriptionsyntheticsystem;

import com.ogic.prescriptionsyntheticsystem.component.CheckImportTool;
import com.ogic.prescriptionsyntheticsystem.component.DrugImportTool;
import com.ogic.prescriptionsyntheticsystem.component.SampleCleanTool;
import com.ogic.prescriptionsyntheticsystem.component.SampleImportTool;
import com.ogic.prescriptionsyntheticsystem.entity.CheckTable;
import com.ogic.prescriptionsyntheticsystem.entity.DrugTable;
import com.ogic.prescriptionsyntheticsystem.entity.Sample;
import com.ogic.prescriptionsyntheticsystem.exception.UnitUnfixedException;
import com.ogic.prescriptionsyntheticsystem.mapper.DMCheckMapper;
import com.ogic.prescriptionsyntheticsystem.mapper.DMDrugMapper;
import com.ogic.prescriptionsyntheticsystem.service.Apriori;
import com.ogic.prescriptionsyntheticsystem.service.Kmeans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

@EnableAsync
@SpringBootTest
class PrescriptionSyntheticSystemApplicationTests {

    @Resource
    DMCheckMapper dmCheckMapper;

    @Resource
    DMDrugMapper dmDrugMapper;

    @Test
    void mysqlTest() throws IOException, ParseException {

        CheckImportTool checkImportTool = new CheckImportTool("/home/ogic/Desktop/data.xls");
        checkImportTool.readExcel(0);
        List<CheckTable> dmCheckList = checkImportTool.getCheckTableList();
        dmCheckMapper.insertDMCheckList(dmCheckList);
        checkImportTool.close();

        DrugImportTool drugImportTool = new DrugImportTool("/home/ogic/Desktop/data.xls");
        drugImportTool.readExcel(1);
        List<DrugTable> dmDrugList = drugImportTool.getDrugTableList();
        dmDrugMapper.insertDMDrugList(dmDrugList);
        drugImportTool.close();

    }

    @Autowired
    SampleCleanTool sampleCleanTool;

    @Test
    public void cleanTest() throws IOException, ParseException {
        SampleImportTool tool = new SampleImportTool("/home/ogic/Desktop/data.xls");
        try {
            tool.readExcel(1);
        }catch (UnitUnfixedException e){
            System.out.println(e.getUnitError());
            e.printStackTrace();
        }
        List<Sample> sampleList = tool.getSampleList();

        sampleCleanTool.clean(sampleList);
        System.out.println(sampleList);
//        System.out.println("sampleList size:"+sampleList.size());
//        System.out.println(tool.printDiagnosisList());
//        System.out.println(tool.printDrugList());
    }

    @Test
    public void aprioriTest() throws IOException, InterruptedException {
        SampleImportTool sampleImportTool = new SampleImportTool("/home/ogic/Desktop/data.xls");
        try {
            sampleImportTool.readExcel(1);
        }catch (UnitUnfixedException e){
            System.out.println(e.getUnitError());
            e.printStackTrace();
        }
        List<Sample> sampleList = sampleImportTool.getSampleList();
        sampleCleanTool.clean(sampleList);
        Apriori apriori = new Apriori(sampleList, sampleImportTool.getDiagnosisList(), sampleImportTool.getDrugList());
        apriori.run();
        Map<String, Double> supportDegreeResult = apriori.getSupportDegreeResult();
        Iterator<Map.Entry<String, Double>> iterator = supportDegreeResult.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Double> entry = iterator.next();
            double temp = entry.getValue();
            if (temp > apriori.MIN_SUPPORT_DEGREE) {
                System.out.println(entry.getKey() + " = " + String.format("%.6f", temp));
            }
        }
        Map<String, Double> believeDegreeResult = apriori.getBelieveDegreeResult();
        iterator = believeDegreeResult.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Double> entry = iterator.next();
            double temp = entry.getValue();
            if (temp > apriori.MIN_SUPPORT_DEGREE) {
                System.out.println(entry.getKey() + " = " + String.format("%.6f", temp));
            }
        }
        while (!apriori.isBelieveDegreeFinished()){
            wait(1000l);
        }
        Map<String, Double> fixableRuleMap = apriori.getFixableRuleMap();
        iterator = fixableRuleMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Double> entry = iterator.next();
            double temp = entry.getValue();
            DecimalFormat format = new DecimalFormat("0.00%");
            if (temp > apriori.MIN_SUPPORT_DEGREE) {
                System.out.println(entry.getKey() + " = " + format.format(temp));
            }
        }
    }

    @Test
    public void KmeansTest() throws IOException {
        SampleImportTool sampleImportTool = new SampleImportTool("/home/ogic/Desktop/data.xls");
        try {
            sampleImportTool.readExcel(1);
        }catch (UnitUnfixedException e){
            System.out.println(e.getUnitError());
            e.printStackTrace();
        }
        List<Sample> sampleList = sampleImportTool.getSampleList();
        sampleCleanTool.clean(sampleList);
//        System.out.println("sample size = " + sampleList.size());
//        System.out.println("diagnosis size = " + sampleImportTool.getDiagnosisList().size());
//        System.out.println("drug size = " + sampleImportTool.getDrugList().size());
        Kmeans kmeans = new Kmeans(sampleList);
//        Random random = new Random();
//        List<Integer> randomDiagnosis = sampleList.get(random.nextInt(sampleList.size())).getDiagnoses();
//        List<Sample> samples = kmeans.getSampleListByDiagnosis(randomDiagnosis);
        Map<String, List<Sample>>  diagnosisDrugSampleMap= kmeans.getMaxDiagnosisDrugSampleMap();
        System.out.println("诊断ID\t" + kmeans.getMaxSamplesDiagnoses());
        List<String> randomDiagnosisName = new ArrayList<>( kmeans.getMaxSamplesDiagnoses().size());
        for (int i :  kmeans.getMaxSamplesDiagnoses()){
            randomDiagnosisName.add(sampleImportTool.getDiagnosisList().get(i - 10000));
        }
        System.out.println("诊断名:\t" + randomDiagnosisName);
        Iterator iterator = diagnosisDrugSampleMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String drugIdStr = (String) entry.getKey();
            List<Sample> samples = (List<Sample>) entry.getValue();
            System.out.println("药物ID:\t" + drugIdStr);
            String[] strings = drugIdStr.substring(1, drugIdStr.length()-1).split(", ");
            List<Integer> drugId = new ArrayList<>(strings.length);
            for (String str : strings){
                drugId.add(Integer.parseInt(str));
            }
            List<String> drugName = new ArrayList<>(drugId.size());
            for (int i : drugId){
                drugName.add(sampleImportTool.getDrugList().get(i-20000));
            }
            System.out.println("药物名:\t" + drugName);
            System.out.println("样本量:\t" + samples.size());
            for (Sample sample : samples) {
                System.out.println(sample.getDrugDetails());
            }
            System.out.println();
        }
//        for (Map.Entry<String, Integer> stringIntegerEntry : drugTimeMap.entrySet()) {
//            if(stringIntegerEntry.getValue() >= 1){
//                System.out.println(stringIntegerEntry);
//                String str = stringIntegerEntry.getKey();
//                for (Sample sample:samples){
//                    if (str.equals(Arrays.toString(sample.getDrugs().toArray()))){
//                        System.out.println(sample.getDrugDetails());
//                    }
//                }
//                System.out.println();
//            }
//        }

    }


    @Test
    public void kmeansTest2() throws IOException {
        SampleImportTool sampleImportTool = new SampleImportTool("/home/ogic/Desktop/data.xls");
        try {
            sampleImportTool.readExcel(1);
        }catch (UnitUnfixedException e){
            System.out.println(e.getUnitError());
            e.printStackTrace();
        }
        List<Sample> sampleList = sampleImportTool.getSampleList();
        sampleCleanTool.clean(sampleList);
        Kmeans kmeans = new Kmeans(sampleList);
        List<Integer> diagnosis = new ArrayList<>(1);
        diagnosis.add(10000);
        List<Integer> drug = new ArrayList<>(1);
        drug.add(20001);
        List<List<Sample>> result = kmeans.analyzeDiagnosisDrugByK(diagnosis, drug, 5);
        for (int i = 0; i < result.size(); i++){
            System.out.println("Group\t" + i + "\t:");
            for (int j = 0; j < result.get(i).size(); j++){
                System.out.println(result.get(i).get(j).getDrugDetails());
            }
            System.out.println("\n===================================================================================================================================\n");
        }
    }


    @Test
    public void sortTest(){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(3);
        list.add(2);
        list.add(9);
        list.add(7);
        list.add(5);
        list.sort(Comparator.comparingInt(o -> o));
        System.out.println(Arrays.toString(list.toArray()));
    }

}
