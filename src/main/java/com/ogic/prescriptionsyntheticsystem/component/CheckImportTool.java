package com.ogic.prescriptionsyntheticsystem.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ogic.prescriptionsyntheticsystem.entity.CheckTable;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author ogic
 */
public class CheckImportTool extends AbstractExcelImportTool {

    public CheckImportTool(String fileName) throws IOException {
        super(fileName);
    }

    private List<CheckTable> checkTableList;

    @Override
    public void readExcel(int sheetId){
        Sheet sheet = workbook.getSheetAt(sheetId);
        int rowNum = sheet.getPhysicalNumberOfRows();
        checkTableList = new ArrayList<>(rowNum-1);
        for (int i = 1; i < rowNum; i++){
            Row row = sheet.getRow(i);
            CheckTable checkTable = new CheckTable();
            try {
                checkTable.setId(i)
                        .setPatientId((int) (3 * Double.parseDouble(row.getCell(0).toString())))
                        .setVisits((int) Double.parseDouble(row.getCell(1).toString()))
                        .setMainDiagnosis(row.getCell(2).toString())
                        .setMinorDiagnosis1(row.getCell(3).toString())
                        .setMinorDiagnosis2(row.getCell(4).toString())
                        .setMinorDiagnosis3(row.getCell(5).toString())
                        .setSex(row.getCell(6).toString())
                        .setAge((short) Double.parseDouble(row.getCell(7).toString()))
                        .setSample(row.getCell(9).toString())
                        .setGroupName(row.getCell(12).toString())
                        .setProjectName(row.getCell(13).toString())
                        .setProjectCode(row.getCell(14).toString())
                        .setResult(row.getCell(15).toString())
                        .setResultUnit(row.getCell(16).toString());

            }catch (NullPointerException e){
                checkTable.setResult("NULL")
                        .setResultUnit("NULL");
            }finally {
                checkTableList.add(checkTable);
            }
        }
    }

    public List<CheckTable> getCheckTableList() {
        return checkTableList;
    }
}
