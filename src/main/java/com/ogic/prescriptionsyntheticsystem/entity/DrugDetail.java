package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class DrugDetail {

    /**
     * 药物ID
     */
    private int drugId;

    /**
     * 数量
     */
    private int amount;

    /**
     * 用药单位
     */
    private String unit;

    public int addAmount(int num){
        amount += num;
        return amount;
    }
}
