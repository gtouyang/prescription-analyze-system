package com.ogic.prescriptionsyntheticsystem;

import com.ogic.prescriptionsyntheticsystem.component.DrugImportTool;
import com.ogic.prescriptionsyntheticsystem.component.ExcelImportTool;
import com.ogic.prescriptionsyntheticsystem.entity.Drug;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

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

}
