package com.ogic.prescriptionsyntheticsystem.entity;

import lombok.Data;

import java.util.List;

@Data
public class BaggingResult {
    private List<DrugView> drugViews;
    private List<AprioriRuleView> aprioriRuleViews;
    private List<LDAView> ldaViews;
    private List<KmeansView> kmeansViews;

    public BaggingResult() {
    }

    public List<DrugView> getDrugViews() {
        return drugViews;
    }

    public BaggingResult setDrugViews(List<DrugView> drugViews) {
        this.drugViews = drugViews;
        return this;
    }

    public List<AprioriRuleView> getAprioriRuleViews() {
        return aprioriRuleViews;
    }

    public BaggingResult setAprioriRuleViews(List<AprioriRuleView> aprioriRuleViews) {
        this.aprioriRuleViews = aprioriRuleViews;
        return this;
    }

    public List<LDAView> getLdaViews() {
        return ldaViews;
    }

    public BaggingResult setLdaViews(List<LDAView> ldaViews) {
        this.ldaViews = ldaViews;
        return this;
    }

    public List<KmeansView> getKmeansViews() {
        return kmeansViews;
    }

    public BaggingResult setKmeansViews(List<KmeansView> kmeansViews) {
        this.kmeansViews = kmeansViews;
        return this;
    }
}
