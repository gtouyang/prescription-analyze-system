package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author ogic
 * 糖尿病检验
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Check {

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
}
