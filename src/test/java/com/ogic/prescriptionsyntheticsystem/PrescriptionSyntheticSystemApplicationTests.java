package com.ogic.prescriptionsyntheticsystem;

import com.ogic.prescriptionsyntheticsystem.component.CheckImportTool;
import com.ogic.prescriptionsyntheticsystem.component.ExcelImportTool;
import com.ogic.prescriptionsyntheticsystem.component.SampleCleanTool;
import com.ogic.prescriptionsyntheticsystem.component.SampleImportTool;
import com.ogic.prescriptionsyntheticsystem.entity.Sample;
import com.ogic.prescriptionsyntheticsystem.service.Apriori;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.sql.In;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@SpringBootTest
class PrescriptionSyntheticSystemApplicationTests {

//    @Resource
//    CheckMapper checkMapper;
//
//    @Resource
//    DrugMapper drugMapper;
//
//    @Test
//    void contextLoads() throws IOException, ParseException {
////        ExcelImportTool tool = new CheckImportTool("/home/ogic/Desktop/data.xls");
////        List<Check> dmCheckList = tool.readExcel(0);
////        int i = 0;
////        for (Check check:dmCheckList){
////            checkMapper.insertCheck(check);
////            System.out.println(i);
////            i++;
////        }
////        List<Check> anemiaCheckList = tool.readExcel(2);
////        checkMapper.insertCheckList(anemiaCheckList);
////        tool.close();
//        ExcelImportTool tool = new DrugImportTool("/home/ogic/Desktop/data.xls");
////        List<Drug> dmDrugList = tool.readExcel(1);
////        drugMapper.insertDrugList(dmDrugList);
//
//        List<Drug> anemiaDrugList = tool.readExcel(3);
//        drugMapper.insertDrugList(anemiaDrugList);
//    }

    @Autowired
    SampleCleanTool sampleCleanTool;

    @Test
    public void cleanTest() throws IOException, ParseException {
        SampleImportTool tool = new SampleImportTool("/home/ogic/Desktop/data.xls");
        List<Sample> sampleList = tool.readExcel(1);
        sampleCleanTool.clean(sampleList);
        System.out.println(sampleList);
//        System.out.println("sampleList size:"+sampleList.size());
//        System.out.println(tool.printDiagnosisList());
//        System.out.println(tool.printDrugList());
    }

    @Test
    public void aprioriTest() throws IOException, ParseException {
        SampleImportTool sampleImportTool = new SampleImportTool("/home/ogic/Desktop/data.xls");
        List<Sample> sampleList = sampleImportTool.readExcel(1);
        sampleCleanTool.clean(sampleList);
        Apriori apriori = new Apriori(sampleList, sampleImportTool.getDiagnosisList(), sampleImportTool.getDrugList());
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
