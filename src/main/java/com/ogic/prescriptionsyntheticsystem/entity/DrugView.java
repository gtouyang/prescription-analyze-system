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

    private JudgeState judgeResult;

    public enum JudgeState{

        /**
         * 初始化
         */
        INIT,

        /**
         * Apriori通过
         */
        APRIORI_PASS,

        /**
         * lda通过
         */
        LDA_PASS,

        /**
         * 不常见
         */
        UN_USUAL,

        /**
         * 偏多
         */
        MORE,

        /**
         * 过多
         */
        MUCH_MORE,

        /**
         * 通过
         */
        PASS
    }

    public DrugView(int id, String name, int amount, String unit) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.judgeResult = JudgeState.INIT;
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

    public DrugView setJudgeResult(JudgeState judgeResult) {
        this.judgeResult = judgeResult;
        return this;
    }

    public JudgeState getJudgeResult() {
        return judgeResult;
    }

    public String getJudgeResultStr() {
        switch (judgeResult){
            case INIT: return "未计算";
            case UN_USUAL: return "不常见用药";
            case MORE: return "偏多";
            case MUCH_MORE: return "过多";
            case PASS: return "通过";
            default: return "常见用药";
        }
    }
}
