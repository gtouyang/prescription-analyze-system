package com.ogic.prescriptionsyntheticsystem.component;

import com.ogic.prescriptionsyntheticsystem.entity.DrugDetail;
import com.ogic.prescriptionsyntheticsystem.entity.Patient;
import com.ogic.prescriptionsyntheticsystem.entity.Sample;
import com.ogic.prescriptionsyntheticsystem.exception.UnitUnfixedException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 样本读取工具
 * @author ogic
 */
public class SampleImportTool extends AbstractExcelImportTool {

    public SampleImportTool(String fileName) throws IOException {
        super(fileName);
    }

    /**
     * 总样本列表
     */
    private List<Sample> sampleList;

    /**
     * 患者表
     */
    private Map<Integer, Patient> patientMap;

    /**
     * 诊断列表
     */
    private final List<String> diagnosisList = new ArrayList<String>();

    /**
     * 药物列表
     */
    private final List<String> drugList = new ArrayList<String>();

    @Override
    public void readExcel(int sheetId) throws UnitUnfixedException {
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
                /*如果已经记录过该患者则提取他的信息*/
                thisPatient = new Patient(thisPatientId);
                patientMap.put(thisPatientId, thisPatient);
            }else {
                /*如果没有则记录该新患者*/
                thisPatient = patientMap.get(thisPatientId);
            }

            /*读取就诊次数，作为分开同一个患者不同批次就诊的标识*/
            int thisFlag = (int) Double.parseDouble(row.getCell(1).toString());

            /*获取该患者的样本列表*/
            List<Sample> thisPatientSamples = thisPatient.getSamples();

            /*遍历该患者的样本列表，如果有相同标识的样本则说明是同一次就诊*/
            if (thisPatientSamples.size() > 0){
                for (Sample sample : thisPatientSamples){
                    if (thisFlag == sample.getFlag()){
                        thisSample = sample;
                        break;
                    }
                }
            }

            /*如果没有相同标识的样本说明是一次新的就诊，则新建一个样本，加入到该患者的样本列表和总样本列表*/
            if (thisSample == null){
                thisSample = new Sample().setPatientId(thisPatientId).setFlag(thisFlag);
                thisPatient.addSample(thisSample);
                sampleList.add(thisSample);
            }

            /*读取该行数据的诊断，加入到样本中*/
            for (int j = 2; j < 6; j++) {
                String tempStr = row.getCell(j).toString();
                if (tempStr != null && !tempStr.isEmpty() && !"NULL".equals(tempStr)) {
                    int tempDiagnosisId = countDiagnosis(tempStr);
                    thisSample.addDiagnosis(tempDiagnosisId);
                }
            }

            /*读取该行数据使用的药物，加入到样本中*/
            String tempStr = row.getCell(9).toString();
            if (tempStr != null && !tempStr.isEmpty()) {
                int tempDrugId = countDrug(tempStr);
                DrugDetail tempDrugDetail = new DrugDetail(tempDrugId, (int) Double.parseDouble(row.getCell(10).toString()), row.getCell(17).toString());
                unifiedUnit(tempDrugDetail);
                if (thisSample.addDrug(tempDrugId) != thisSample.addDrugDetail(tempDrugDetail)){
                    throw new UnitUnfixedException(thisSample,tempDrugDetail);
                }

            }
        }
    }

    /**
     * 获取总样本列表
     * @return  总样本列表
     */
    public List<Sample> getSampleList() {
        return sampleList;
    }

    /**
     * 获取患者表
     * @return  患者表
     */
    public Map<Integer, Patient> getPatientMap() {
        return patientMap;
    }

    /**
     * 获取诊断列表，用于根据诊断ID获取诊断名
     * @return  诊断列表
     */
    public List<String> getDiagnosisList(){
        return new ArrayList<>(diagnosisList);
    }

    /**
     * 根据诊断名返回诊断ID，为与其他ID区分开来该ID从10000数起
     * @param diagnosis 诊断名
     * @return  诊断ID
     */
    private int countDiagnosis(String diagnosis) {

        if (diagnosisList.contains(diagnosis)) {
            return diagnosisList.indexOf(diagnosis) + 10000;
        } else {
            diagnosisList.add(diagnosis);
            return diagnosisList.size() - 1 + 10000;
        }
    }

    /**
     * 获取药物列表，用于根据药物ID获取药物名
     * @return  药物列表
     */
    public List<String> getDrugList(){
        return new ArrayList<>(drugList);
    }

    /**
     * 根据药物名返回药物ID，为与其他ID区分开来该ID从20000数起
     * @param drug  药物名
     * @return  药物ID
     */
    private int countDrug(String drug) {

        if (drugList.contains(drug)) {
            return drugList.indexOf(drug) + 20000;
        } else {
            drugList.add(drug);
            return drugList.size() - 1 + 20000;
        }
    }

    private void unifiedUnit(DrugDetail drugDetail){

        String unit = drugDetail.getUnit();

        if (unit != null) {
            /* 修改不规范的单位 */
            if ("1g".equals(unit)) {
                drugDetail.setUnit("g");
            } else if ("/g".equals(unit)) {
                drugDetail.setUnit("g");
            } else if ("/支".equals(unit)) {
                drugDetail.setUnit("支");
            } else if ("4NU单位 30片/盒".equals(unit)) {
                drugDetail.setUnit("30片/盒");
            }

            /* 将瓶、盒、版等单位换算成片、丸等 */
            boolean flag = false;
            if (!flag) {
                flag = unifiedUnitHelper(drugDetail, "片");
            }
            if (!flag) {
                flag = unifiedUnitHelper(drugDetail, "丸");
            }
            if (!flag) {
                flag = unifiedUnitHelper(drugDetail, "支");
            }
            if (!flag){
                flag = unifiedUnitHelper(drugDetail, "粒");
            }
            if (!flag) {
                flag = unifiedUnitHelper(drugDetail, "袋");
            }

        }
    }

    private boolean unifiedUnitHelper(DrugDetail drugDetail, String param){
        int multiple = 1;
        String unit = drugDetail.getUnit();
        if (unit.contains(param)) {
            try {

                if (unit.contains("×")) {
                    multiple = Integer.parseInt(unit.substring(unit.indexOf("×") + 1, unit.indexOf(param)));
                } else if (unit.contains("x")) {
                    if ("2片 x5袋/盒".equals(unit)){
                        multiple = 10;
                    }else {
                        multiple = Integer.parseInt(unit.substring(unit.indexOf("x") + 1, unit.indexOf(param)));
                    }
                } else if (unit.contains("*")) {
                    multiple = Integer.parseInt(unit.substring(unit.indexOf("*") + 1, unit.indexOf(param)));
                } else if (unit.indexOf(param) > 0) {
                    try {
                        multiple = Integer.parseInt(unit.substring(0, unit.indexOf(param)));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }catch (StringIndexOutOfBoundsException e){
                System.out.println(unit);
                e.printStackTrace();
            }
            drugDetail.setUnit(param);
            drugDetail.addAmount(drugDetail.getAmount() * (multiple - 1));
            return true;
        }
        return false;
    }
}
