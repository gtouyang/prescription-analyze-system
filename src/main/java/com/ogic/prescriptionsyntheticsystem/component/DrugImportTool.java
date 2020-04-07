package com.ogic.prescriptionsyntheticsystem.component;

import com.ogic.prescriptionsyntheticsystem.entity.DrugTable;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ogic
 */
public class DrugImportTool extends ExcelImportTool{
    public DrugImportTool(String fileName) throws IOException {
        super(fileName);
    }

    @Override
    public List readExcel(int sheetId){
        Sheet sheet = workbook.getSheetAt(sheetId);
        int rowNum = sheet.getPhysicalNumberOfRows();
        List<DrugTable> drugTableList = new ArrayList<>(rowNum-1);
        for (int i = 1; i < rowNum; i++){
            Row row = sheet.getRow(i);
            DrugTable drugTable = new DrugTable();
            try {
                drugTable.setId(i)
                        .setPatientId((int) (3 * Double.parseDouble(row.getCell(0).toString())))
                        .setVisits((int) Double.parseDouble(row.getCell(1).toString()))
                        .setMainDiagnosis(row.getCell(2).toString())
                        .setMinorDiagnosis1(row.getCell(3).toString())
                        .setMinorDiagnosis2(row.getCell(4).toString())
                        .setMinorDiagnosis3(row.getCell(5).toString())
                        .setSex(row.getCell(6).toString())
                        .setAge((short) Double.parseDouble(row.getCell(7).toString()))
                        .setProject(row.getCell(9).toString())
                        .setAmount((int)Double.parseDouble(row.getCell(10).toString()))
                        .setUnit(row.getCell(12).toString())
                        .setUsage(row.getCell(13).toString())
                        .setFrequency(row.getCell(15).toString())
                        .setDosage(Double.parseDouble(row.getCell(14).toString()));

            }catch (NumberFormatException e){
                drugTable.setDosage(0.0);
            }finally {
                drugTableList.add(drugTable);
            }
        }
        return drugTableList;
    }
}
