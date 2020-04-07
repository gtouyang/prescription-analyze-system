package com.ogic.prescriptionsyntheticsystem.mapper;


import com.ogic.prescriptionsyntheticsystem.entity.CheckTable;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ogic
 */
@Mapper
public interface AnemiaCheckMapper {
    /**
     * 插入单个检验单到表中
     * @param checkTable 检验单
     * @return  插入结果
     */
    @Insert("INSERT INTO AnemiaCheck(" +
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
    Integer insertAnemiaCheck(CheckTable checkTable);


    /**
     * 插入检验单列表到表中
     * @param list 检验单列表
     * @return  插入结果
     */
    @Insert("<script>" +
            "INSERT INTO AnemiaCheck(" +
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
    Integer insertAnemiaCheckList(@Param(value = "list") List<CheckTable> list);


    /**
     * 获得所有的贫血检验单
     * @return  贫血检验单列表
     */
    @Select("select * from AnemiaCheck")
    List<CheckTable> getAllAnemiaCheck();

    /**
     * 获得指定患者ID的所有贫血检验单
     * @param patientId 患者ID
     * @return  贫血检验单列表
     */
    @Select("select * from AnemiaCheck where patientId = #{patientId}")
    List<CheckTable> getAllAnemiaCheckForPatient(int patientId);
}
