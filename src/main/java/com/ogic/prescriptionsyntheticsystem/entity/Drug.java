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
public class Drug {

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

}
