package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
//@AllArgsConstructor
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

    public DrugDetail() {
    }

    public DrugDetail(int drugId, int amount, String unit) {
        this.drugId = drugId;
        this.amount = amount;
        this.unit = unit;
    }

    public int getDrugId() {
        return drugId;
    }

    public int getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }

    public int addAmount(int num){
        amount += num;
        return amount;
    }

    public DrugDetail setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public DrugDetail setDrugId(int drugId) {
        this.drugId = drugId;
        return this;
    }

    public DrugDetail setAmount(int amount) {
        this.amount = amount;
        return this;
    }
}
