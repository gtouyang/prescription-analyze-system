package com.ogic.prescriptionsyntheticsystem.mapper;

import com.ogic.prescriptionsyntheticsystem.entity.AprioriRuleWithBelieveDegree;
import com.ogic.prescriptionsyntheticsystem.entity.CheckTable;
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
public interface AprioriRuleWithBelieveDegreeMapper {
    /**
     * 插入关联列表到表中
     * @param list 关联列表
     * @return  插入结果
     */
    @Insert("<script>" +
            "INSERT INTO AprioriRuleWithBelieveDegree(" +
            "rule," +
            "believeDegree)" +
            "VALUES" +
            "<foreach collection = 'list' item='list' separator=',' >" +
            "(#{list.rule}," +
            "#{list.believeDegree})" +
            "</foreach>" +
            "</script>")
    Integer insertAprioriRuleWithBelieveDegreeList(@Param(value = "list") List<AprioriRuleWithBelieveDegree> list);

    /**
     * 获得所有关联规则
     * @return  关联规则列表
     */
    @Select("select * from AprioriRuleWithBelieveDegree")
    List<AprioriRuleWithBelieveDegree> getAllAprioriRuleWithBelieveDegreeList();
}
