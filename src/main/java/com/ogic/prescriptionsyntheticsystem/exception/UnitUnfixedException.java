package com.ogic.prescriptionsyntheticsystem.exception;

import com.ogic.prescriptionsyntheticsystem.entity.DrugDetail;
import com.ogic.prescriptionsyntheticsystem.entity.Sample;

import java.util.List;

public class UnitUnfixedException extends Exception {
    private Sample sample;
    private DrugDetail drugDetail;

    public UnitUnfixedException(Sample sample, DrugDetail drugDetail) {
        this.sample = sample;
        this.drugDetail = drugDetail;
    }

    public String getUnitError(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unit error: " + sample + "\n\n");
        stringBuilder.append("Conflicted drugDetail: " + drugDetail);
        return stringBuilder.toString();
    }
}
