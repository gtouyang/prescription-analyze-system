package com.ogic.prescriptionsyntheticsystem.mapper;


import com.ogic.prescriptionsyntheticsystem.entity.Check;
import com.ogic.prescriptionsyntheticsystem.entity.Check;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ogic
 */
@Mapper
public interface DMCheckMapper {
    /**
     * 插入单个检验单到表中
     * @param check 检验单
     * @return  插入结果
     */
    @Insert("INSERT INTO DMCheck(" +
            "patientId," +
            "visits," +
            "mainDiagnosis," +
            "minorDiagnosis1," +
            "minorDiagnosis2," +
            "minorDiagnosis3," +
            "sex," +
            "age," +
            "sample," +
            "groupName," +
            "projectName," +
            "projectCode," +
            "result," +
            "resultUnit)" +
            "VALUES(#{patientId}," +
            "#{visits}," +
            "#{mainDiagnosis}," +
            "#{minorDiagnosis1}," +
            "#{minorDiagnosis2}," +
            "#{minorDiagnosis3}," +
            "#{sex}," +
            "#{age}," +
            "#{sample}," +
            "#{groupName}," +
            "#{projectName}," +
            "#{projectCode}," +
            "#{result}," +
            "#{resultUnit})")
    Integer insertDMCheck(Check check);


    /**
     * 插入检验单列表到表中
     * @param list 检验单列表
     * @return  插入结果
     */
    @Insert("<script>" +
            "INSERT INTO DMCheck(" +
            "patientId," +
            "visits," +
            "mainDiagnosis," +
            "minorDiagnosis1," +
            "minorDiagnosis2," +
            "minorDiagnosis3," +
            "sex," +
            "age," +
            "sample," +
            "groupName," +
            "projectName," +
            "projectCode," +
            "result," +
            "resultUnit)" +
            "VALUES" +
            "<foreach collection = 'list' item='list' separator=',' >" +
            "(#{list.patientId}," +
            "#{list.visits}," +
            "#{list.mainDiagnosis}," +
            "#{list.minorDiagnosis1}," +
            "#{list.minorDiagnosis2}," +
            "#{list.minorDiagnosis3}," +
            "#{list.sex}," +
            "#{list.age}," +
            "#{list.sample}," +
            "#{list.groupName}," +
            "#{list.projectName}," +
            "#{list.projectCode}," +
            "#{list.result}," +
            "#{list.resultUnit})" +
            "</foreach>" +
            "</script>")
    Integer insertDMCheckList(@Param(value = "list") List<Check> list);


    /**
     * 获得所有的糖尿病检验单
     * @return  糖尿病检验单列表
     */
    @Select("select * from DMCheck")
    List<Check> getAllDMCheck();

    /**
     * 获得指定患者ID的所有糖尿病检验单
     * @param patientId 患者ID
     * @return  糖尿病检验单列表
     */
    @Select("select * from DMCheck where patientId = #{patientId}")
    List<Check> getAllDMCheckForPatient(int patientId);
}
