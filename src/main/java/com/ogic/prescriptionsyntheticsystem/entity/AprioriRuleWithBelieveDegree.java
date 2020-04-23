package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;

/**
 * @author ogic
 */
@Data
public class AprioriRuleWithBelieveDegree {
    private String rule;
    private Double believeDegree;

    public AprioriRuleWithBelieveDegree(String rule, Double believeDegree) {
        this.rule = rule;
        this.believeDegree = believeDegree;
    }

    public String getRule() {
        return rule;
    }

    public AprioriRuleWithBelieveDegree setRule(String rule) {
        this.rule = rule;
        return this;
    }

    public Double getBelieveDegree() {
        return believeDegree;
    }

    public AprioriRuleWithBelieveDegree setBelieveDegree(Double believeDegree) {
        this.believeDegree = believeDegree;
        return this;
    }
}
