package com.ogic.prescriptionsyntheticsystem.component;

import com.ogic.prescriptionsyntheticsystem.entity.DrugDetail;
import com.ogic.prescriptionsyntheticsystem.entity.Sample;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ogic
 */
public class SampleImportTool extends AbstractExcelImportTool {

    public SampleImportTool(String fileName) throws IOException {
        super(fileName);
    }

    private List<Sample> sampleList;

    @Override
    public void readExcel(int sheetId) throws IOException, ParseException {
        Sheet sheet = workbook.getSheetAt(sheetId);
        int rowNum = sheet.getPhysicalNumberOfRows();
        sampleList = new ArrayList<>(rowNum - 1);
        Sample thisSample = null;
        int id = 0;
        for (int i = 1; i < rowNum; i++) {
            Row row = sheet.getRow(i);

            /* 检查该行的患者ID和上一行的患者ID是否相同 */
            int thisPatientId = (int) (3 * Double.parseDouble(row.getCell(0).toString()));
            if (thisSample == null || thisPatientId != thisSample.getPatientId()) {
                if (thisSample != null) {
                    sampleList.add(thisSample);
                }
                /* 如果不同则创建一个新的sample */
                thisSample = new Sample()
                        .setId(id++)
                        .setPatientId(thisPatientId)
                        .setDiagnosis(new ArrayList<>())
                        .setDrugs(new ArrayList<>())
                        .setDrugDetails(new ArrayList<>());
            }

            for (int j = 2; j < 6; j++) {
                String tempStr = row.getCell(j).toString();
                if (tempStr != null && !tempStr.isEmpty() && !"NULL".equals(tempStr)) {
                    int tempDiagnosisId = countDiagnosis(tempStr);
                    if (!thisSample.getDiagnosis().contains(tempDiagnosisId)) {
                        thisSample.getDiagnosis().add(tempDiagnosisId);
                    }
                }
            }

            String tempStr = row.getCell(9).toString();
            if (tempStr != null && !tempStr.isEmpty()) {
                int tempDrugId = countDrug(tempStr);
                if (!thisSample.getDrugs().contains(tempDrugId)) {
                    thisSample.getDrugs().add(tempDrugId);
                    DrugDetail newDrugDetail = new DrugDetail(
                            tempDrugId,
                            (int) Double.parseDouble(row.getCell(10).toString()),
                            row.getCell(17).toString());
                    thisSample.getDrugDetails().add(newDrugDetail);
                } else {
                    DrugDetail thisDrugDetail = thisSample.getDrugDetails().get(thisSample.getDrugs().indexOf(tempDrugId));
                    if (thisDrugDetail.getDrugId() == tempDrugId) {
                        thisDrugDetail.addAmount((int) Double.parseDouble(row.getCell(10).toString()));
                    }
                }
            }
        }
        sampleList.add(thisSample);
    }

    public List<Sample> getSampleList() {
        return sampleList;
    }

    private List<String> diagnosisList = new ArrayList<String>();

    public List<String> getDiagnosisList(){
        return new ArrayList<>(diagnosisList);
    }

    private int countDiagnosis(String diagnosis) {

        if (diagnosisList.contains(diagnosis)) {
            return diagnosisList.indexOf(diagnosis) + 10000;
        } else {
            diagnosisList.add(diagnosis);
            return diagnosisList.size() - 1 + 10000;
        }
    }

    private List<String> drugList = new ArrayList<String>();

    public List<String> getDrugList(){
        return new ArrayList<>(drugList);
    }

    private int countDrug(String drug) {

        if (drugList.contains(drug)) {
            return drugList.indexOf(drug) + 20000;
        } else {
            drugList.add(drug);
            return drugList.size() - 1 + 20000;
        }
    }
}
