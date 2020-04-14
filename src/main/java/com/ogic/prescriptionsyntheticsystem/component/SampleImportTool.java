package com.ogic.prescriptionsyntheticsystem.component;

import com.ogic.prescriptionsyntheticsystem.entity.DrugDetail;
import com.ogic.prescriptionsyntheticsystem.entity.Patient;
import com.ogic.prescriptionsyntheticsystem.entity.Sample;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
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

    private Map<Integer, Patient> patientMap;

    @Override
    public void readExcel(int sheetId) throws IOException, ParseException {
        Sheet sheet = workbook.getSheetAt(sheetId);
        int rowNum = sheet.getPhysicalNumberOfRows();
        sampleList = new ArrayList<>(rowNum - 1);
        patientMap = new HashMap<>();
        Sample thisSample;
        Patient thisPatient;
        for (int i = 1; i < rowNum; i++) {

            /*初始化*/
            thisSample = null;
            thisPatient = null;

            Row row = sheet.getRow(i);

            /*获取该行患者ID*/
            int thisPatientId = (int) (3 * Double.parseDouble(row.getCell(0).toString()));

            /* 检查该患者是否曾经记录过 */
            if (!patientMap.containsKey(thisPatientId)){
                thisPatient = new Patient(thisPatientId);
                patientMap.put(thisPatientId, thisPatient);
            }else {
                thisPatient = patientMap.get(thisPatientId);
            }
            int thisFlag = (int) Double.parseDouble(row.getCell(1).toString());

            List<Sample> thisPatientSamples = thisPatient.getSamples();

            if (thisPatientSamples.size() > 0){
                for (Sample sample : thisPatientSamples){
                    if (thisFlag == sample.getFlag()){
                        thisSample = sample;
                        break;
                    }
                }
            }
            if (thisSample == null){
                thisSample = new Sample().setPatientId(thisPatientId).setFlag(thisFlag);
                thisPatient.addSample(thisSample);
                sampleList.add(thisSample);
            }

            for (int j = 2; j < 6; j++) {
                String tempStr = row.getCell(j).toString();
                if (tempStr != null && !tempStr.isEmpty() && !"NULL".equals(tempStr)) {
                    int tempDiagnosisId = countDiagnosis(tempStr);
                    thisSample.addDiagnosis(tempDiagnosisId);
                }
            }

            String tempStr = row.getCell(9).toString();
            if (tempStr != null && !tempStr.isEmpty()) {
                int tempDrugId = countDrug(tempStr);
                thisSample.addDrug(tempDrugId)
                        .addDrugDetail(tempDrugId, (int) Double.parseDouble(row.getCell(10).toString()), row.getCell(17).toString());

            }
        }
    }

    public List<Sample> getSampleList() {
        return sampleList;
    }

    public Map<Integer, Patient> getPatientMap() {
        return patientMap;
    }

    private final List<String> diagnosisList = new ArrayList<String>();

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

    private final List<String> drugList = new ArrayList<String>();

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
