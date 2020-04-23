package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;

/**
 * @author ogic
 */
@Data
public class Selection {
    private int id;
    private String name;

    public Selection(int id, String name) {
        this.id = id;
        this.name = String.format("%05d", id) + "-" + name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
