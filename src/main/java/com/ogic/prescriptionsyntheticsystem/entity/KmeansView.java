package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;

/**
 * @author ogic
 */
@Data
public class KmeansView {

    private int drugId;

    private String drugName;

    private String normal;

    private String more;

    private String muchMore;

    public KmeansView(int drugId, String drugName, String normal, String more, String muchMore) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.normal = normal;
        this.more = more;
        this.muchMore = muchMore;
    }

    public int getDrugId() {
        return drugId;
    }

    public KmeansView setDrugId(int drugId) {
        this.drugId = drugId;
        return this;
    }

    public String getDrugName() {
        return drugName;
    }

    public KmeansView setDrugName(String drugName) {
        this.drugName = drugName;
        return this;
    }

    public String getNormal() {
        return normal;
    }

    public KmeansView setNormal(String normal) {
        this.normal = normal;
        return this;
    }

    public String getMore() {
        return more;
    }

    public KmeansView setMore(String more) {
        this.more = more;
        return this;
    }

    public String getMuchMore() {
        return muchMore;
    }

    public KmeansView setMuchMore(String muchMore) {
        this.muchMore = muchMore;
        return this;
    }
}
