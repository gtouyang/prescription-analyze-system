package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author ogic
 * 糖尿病检验
 */
@Data
//@NoArgsConstructor
//@Accessors(chain = true)
public class CheckTable {

    /**
     * 数据ID
     */
    private int id;

    /**
     * 病人编号
     */
    private int patientId;

    /**
     * 就诊次数
     */
    private int visits;

    /**
     * 主要诊断
     */
    private String mainDiagnosis;

    /**
     * 次要诊断１
     */
    private String minorDiagnosis1;

    /**
     * 次要诊断２
     */
    private String minorDiagnosis2;

    /**
     * 次要诊断３
     */
    private String minorDiagnosis3;

    /**
     * 性别
     */
    private String sex;

    /**
     * 年龄
     */
    private short age;

    /**
     * 标本
     */
    private String sample;

    /**
     * 组合名称
     */
    private String groupName;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 结果值
     */
    private String result;

    /**
     * 结果单位
     */
    private String resultUnit;

    public CheckTable() {
    }

    public int getId() {
        return id;
    }

    public CheckTable setId(int id) {
        this.id = id;
        return this;
    }

    public int getPatientId() {
        return patientId;
    }

    public CheckTable setPatientId(int patientId) {
        this.patientId = patientId;
        return this;
    }

    public int getVisits() {
        return visits;
    }

    public CheckTable setVisits(int visits) {
        this.visits = visits;
        return this;
    }

    public String getMainDiagnosis() {
        return mainDiagnosis;
    }

    public CheckTable setMainDiagnosis(String mainDiagnosis) {
        this.mainDiagnosis = mainDiagnosis;
        return this;
    }

    public String getMinorDiagnosis1() {
        return minorDiagnosis1;
    }

    public CheckTable setMinorDiagnosis1(String minorDiagnosis1) {
        this.minorDiagnosis1 = minorDiagnosis1;
        return this;
    }

    public String getMinorDiagnosis2() {
        return minorDiagnosis2;
    }

    public CheckTable setMinorDiagnosis2(String minorDiagnosis2) {
        this.minorDiagnosis2 = minorDiagnosis2;
        return this;
    }

    public String getMinorDiagnosis3() {
        return minorDiagnosis3;
    }

    public CheckTable setMinorDiagnosis3(String minorDiagnosis3) {
        this.minorDiagnosis3 = minorDiagnosis3;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public CheckTable setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public short getAge() {
        return age;
    }

    public CheckTable setAge(short age) {
        this.age = age;
        return this;
    }

    public String getSample() {
        return sample;
    }

    public CheckTable setSample(String sample) {
        this.sample = sample;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public CheckTable setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String getProjectName() {
        return projectName;
    }

    public CheckTable setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public CheckTable setProjectCode(String projectCode) {
        this.projectCode = projectCode;
        return this;
    }

    public String getResult() {
        return result;
    }

    public CheckTable setResult(String result) {
        this.result = result;
        return this;
    }

    public String getResultUnit() {
        return resultUnit;
    }

    public CheckTable setResultUnit(String resultUnit) {
        this.resultUnit = resultUnit;
        return this;
    }
}
