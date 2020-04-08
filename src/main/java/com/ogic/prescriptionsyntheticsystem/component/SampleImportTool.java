package com.ogic.prescriptionsyntheticsystem.component;

import com.ogic.prescriptionsyntheticsystem.entity.DrugDetail;
import com.ogic.prescriptionsyntheticsystem.entity.Sample;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ogic
 */
public class SampleImportTool extends ExcelImportTool {

    public SampleImportTool(String fileName) throws IOException {
        super(fileName);
    }

    @Override
    public List readExcel(int sheetId) throws IOException, ParseException {
        Sheet sheet = workbook.getSheetAt(sheetId);
        int rowNum = sheet.getPhysicalNumberOfRows();
        List<Sample> sampleList = new ArrayList<>(rowNum - 1);
        Sample thisOne = null;
        int id = 0;
        for (int i = 1; i < rowNum; i++) {
            Row row = sheet.getRow(i);

            /* 检查该行的患者ID和上一行的患者ID是否相同 */
            int tempId = (int) (3 * Double.parseDouble(row.getCell(0).toString()));
            if (thisOne == null || tempId != thisOne.getPatientId()) {
                if (thisOne != null) {
                    sampleList.add(thisOne);
                }
                /* 如果不同则创建一个新的sample */
                thisOne = new Sample()
                        .setId(id++)
                        .setPatientId(tempId)
                        .setDiagnosis(new ArrayList<Integer>())
                        .setDrugs(new ArrayList<Integer>())
                        .setDrugDetails(new ArrayList<DrugDetail>());
            }

            for (int j = 3; j < 6; j++) {
                String tempStr = row.getCell(j).toString();
                if (tempStr != null && !tempStr.isEmpty() && !"NULL".equals(tempStr)) {
                    int tempDiagnosisId = countDiagnosis(tempStr);
                    if (!thisOne.getDiagnosis().contains(tempDiagnosisId)) {
                        thisOne.getDiagnosis().add(tempDiagnosisId);
                    }
                }
            }

            String tempStr = row.getCell(9).toString();
            if (tempStr != null && !tempStr.isEmpty()) {
                int tempDrugId = countDrug(tempStr);
                if (!thisOne.getDrugs().contains(tempDrugId)) {
                    thisOne.getDrugs().add(tempDrugId);
                    DrugDetail newDrugDetail = new DrugDetail(
                            tempDrugId,
                            (int) Double.parseDouble(row.getCell(10).toString()),
                            row.getCell(17).toString());
                    thisOne.getDrugDetails().add(newDrugDetail);
                } else {
                    DrugDetail thisDrugDetail = thisOne.getDrugDetails().get(thisOne.getDrugs().indexOf(tempDrugId));
                    if (thisDrugDetail.getDrugId() == tempDrugId) {
                        thisDrugDetail.addAmount((int) Double.parseDouble(row.getCell(10).toString()));
                    }
                }
            }
        }
        sampleList.add(thisOne);
        return sampleList;
    }

    private List<String> diagnosisList = new ArrayList<String>();

    public String printDiagnosisList(){
        return Arrays.toString(diagnosisList.toArray());
    }

    private int countDiagnosis(String diagnosis) {
        if (diagnosisList.contains(diagnosis)) {
            return diagnosisList.indexOf(diagnosis);
        } else {
            diagnosisList.add(diagnosis);
            return diagnosisList.size() - 1;
        }
    }

    private List<String> drugList = new ArrayList<String>();

    public String printDrugList(){
        return Arrays.toString(drugList.toArray());
    }

    private int countDrug(String drug) {
        if (drugList.contains(drug)) {
            return drugList.indexOf(drug);
        } else {
            drugList.add(drug);
            return drugList.size() - 1;
        }
    }
}
