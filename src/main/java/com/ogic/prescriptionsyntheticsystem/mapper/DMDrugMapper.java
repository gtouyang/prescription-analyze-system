package com.ogic.prescriptionsyntheticsystem.mapper;

import com.ogic.prescriptionsyntheticsystem.entity.DrugTable;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ogic
 */
@Mapper
public interface DMDrugMapper {
    /**
     * 插入单个开药单到表中
     * @param drugTable 开药单
     * @return  插入结果
     */
    @Insert("INSERT INTO DMDrug(" +
            "patientId," +
            "visits," +
            "mainDiagnosis," +
            "minorDiagnosis1," +
            "minorDiagnosis2," +
            "minorDiagnosis3," +
            "sex," +
            "age," +
            "project," +
            "amount," +
            "unit," +
            "`usage`," +
            "`dosage`," +
            "frequency)" +
            "VALUES(#{patientId}," +
            "#{visits}," +
            "#{mainDiagnosis}," +
            "#{minorDiagnosis1}," +
            "#{minorDiagnosis2}," +
            "#{minorDiagnosis3}," +
            "#{sex}," +
            "#{age}," +
            "#{project}," +
            "#{amount}," +
            "#{unit}," +
            "#{usage}," +
            "#{dosage}," +
            "#{frequency})")
    Integer insertDMDrug(DrugTable drugTable);


    /**
     * 插入用药单列表到表中
     * @param list 用药单列表
     * @return  插入结果
     */
    @Insert("<script>" +
            "INSERT INTO DMDrug(" +
            "patientId," +
            "visits," +
            "mainDiagnosis," +
            "minorDiagnosis1," +
            "minorDiagnosis2," +
            "minorDiagnosis3," +
            "sex," +
            "age," +
            "project," +
            "`amount`," +
            "`unit`," +
            "`usage`," +
            "dosage," +
            "frequency)" +
            "VALUES" +
            "<foreach collection = 'list' item='list' separator=','>" +
            "(#{list.patientId}," +
            "#{list.visits}," +
            "#{list.mainDiagnosis}," +
            "#{list.minorDiagnosis1}," +
            "#{list.minorDiagnosis2}," +
            "#{list.minorDiagnosis3}," +
            "#{list.sex}," +
            "#{list.age}," +
            "#{list.project}," +
            "#{list.amount}," +
            "#{list.unit}," +
            "#{list.usage}," +
            "#{list.dosage}," +
            "#{list.frequency})" +
            "</foreach>" +
            "</script>")
    Integer insertDMDrugList(@Param(value = "list") List<DrugTable> list);

    /**
     * 获得所有的糖尿病用药单
     * @return  糖尿病用药单列表
     */
    @Select("select * from DMDrug")
    List<DrugTable> getAllDMDrug();

    /**
     * 获得指定患者ID的所有糖尿病用药单
     * @param patientId 患者ID
     * @return  糖尿病用药单列表
     */
    @Select("select * from DMDrug where patientId = #{patientId}")
    List<DrugTable> getAllDMDrugForPatient(int patientId);

}
