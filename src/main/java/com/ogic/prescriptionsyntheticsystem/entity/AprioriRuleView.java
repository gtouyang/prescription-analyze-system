package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;

import java.text.DecimalFormat;

/**
 * @author ogic
 */
@Data
public class AprioriRuleView {

    private final String ruleInId;

    private final String ruleInName;

    private final String ruleValue;

    private static DecimalFormat format = new DecimalFormat("0.00%");

    public AprioriRuleView(String ruleInId, String ruleInName, Double ruleValue) {
        this.ruleInId = ruleInId;
        this.ruleInName = ruleInName;
        this.ruleValue = format.format(ruleValue);
    }

    public String getRuleInId() {
        return ruleInId;
    }

    public String getRuleInName() {
        return ruleInName;
    }

    public String getRuleValue() {
        return ruleValue;
    }
}
