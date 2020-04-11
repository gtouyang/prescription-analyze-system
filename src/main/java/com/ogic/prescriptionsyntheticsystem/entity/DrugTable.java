package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author ogic
 * 糖尿病用药
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DrugTable {

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
     * 项目
     */
    private String project;

    /**
     * 数量
     */
    private int amount;

    /**
     * 单位
     */
    private String unit;

    /**
     * 用法
     */
    private String usage;

    /**
     * 用量
     */
    private double dosage;

    /**
     * 频率
     */
    private String frequency;

    public DrugTable() {
    }

    public int getId() {
        return id;
    }

    public DrugTable setId(int id) {
        this.id = id;
        return this;
    }

    public int getPatientId() {
        return patientId;
    }

    public DrugTable setPatientId(int patientId) {
        this.patientId = patientId;
        return this;
    }

    public int getVisits() {
        return visits;
    }

    public DrugTable setVisits(int visits) {
        this.visits = visits;
        return this;
    }

    public String getMainDiagnosis() {
        return mainDiagnosis;
    }

    public DrugTable setMainDiagnosis(String mainDiagnosis) {
        this.mainDiagnosis = mainDiagnosis;
        return this;
    }

    public String getMinorDiagnosis1() {
        return minorDiagnosis1;
    }

    public DrugTable setMinorDiagnosis1(String minorDiagnosis1) {
        this.minorDiagnosis1 = minorDiagnosis1;
        return this;
    }

    public String getMinorDiagnosis2() {
        return minorDiagnosis2;
    }

    public DrugTable setMinorDiagnosis2(String minorDiagnosis2) {
        this.minorDiagnosis2 = minorDiagnosis2;
        return this;
    }

    public String getMinorDiagnosis3() {
        return minorDiagnosis3;
    }

    public DrugTable setMinorDiagnosis3(String minorDiagnosis3) {
        this.minorDiagnosis3 = minorDiagnosis3;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public DrugTable setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public short getAge() {
        return age;
    }

    public DrugTable setAge(short age) {
        this.age = age;
        return this;
    }

    public String getProject() {
        return project;
    }

    public DrugTable setProject(String project) {
        this.project = project;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public DrugTable setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public DrugTable setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public String getUsage() {
        return usage;
    }

    public DrugTable setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public double getDosage() {
        return dosage;
    }

    public DrugTable setDosage(double dosage) {
        this.dosage = dosage;
        return this;
    }

    public String getFrequency() {
        return frequency;
    }

    public DrugTable setFrequency(String frequency) {
        this.frequency = frequency;
        return this;
    }
}
