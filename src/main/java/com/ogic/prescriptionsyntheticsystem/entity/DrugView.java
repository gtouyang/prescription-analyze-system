package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;

/**
 * @author ogic
 */
@Data
public class DrugView {

    private int id;

    private String name;

    private int amount;

    private String unit;

    public DrugView(int id, String name, int amount, String unit) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }
}
