package com.finappl.models;

import java.io.Serializable;

/**
 * Created by ajit on 28/7/15.
 */
public class ConsolidatedSchedulesModel implements Serializable {
    //common for transaction & transfer
    private String scheduleId;
    private String scheduleFrequencyStr;
    private String scheduleDateStr;
    private Double scheduleAmt;
    private String schedTypeStr;

    //for transaction
    private String scheduleTransactionCategoryStr;
    private String scheduleTransactionTypeStr;

    //for transfer
    private String scheduleTransferFromStr;
    private String scheduleTransferToStr;

    public String getScheduleTransactionTypeStr() {
        return scheduleTransactionTypeStr;
    }

    public String getSchedTypeStr() {
        return schedTypeStr;
    }

    public void setSchedTypeStr(String schedTypeStr) {
        this.schedTypeStr = schedTypeStr;
    }

    public void setScheduleTransactionTypeStr(String scheduleTransactionTypeStr) {
        this.scheduleTransactionTypeStr = scheduleTransactionTypeStr;
    }

    public String getScheduleFrequencyStr() {
        return scheduleFrequencyStr;
    }

    public void setScheduleFrequencyStr(String scheduleFrequencyStr) {
        this.scheduleFrequencyStr = scheduleFrequencyStr;
    }

    public String getScheduleDateStr() {
        return scheduleDateStr;
    }

    public void setScheduleDateStr(String scheduleDateStr) {
        this.scheduleDateStr = scheduleDateStr;
    }

    public Double getScheduleAmt() {
        return scheduleAmt;
    }

    public void setScheduleAmt(Double scheduleAmt) {
        this.scheduleAmt = scheduleAmt;
    }

    public String getScheduleTransactionCategoryStr() {
        return scheduleTransactionCategoryStr;
    }

    public void setScheduleTransactionCategoryStr(String scheduleTransactionCategoryStr) {
        this.scheduleTransactionCategoryStr = scheduleTransactionCategoryStr;
    }

    public String getScheduleTransferFromStr() {
        return scheduleTransferFromStr;
    }

    public void setScheduleTransferFromStr(String scheduleTransferFromStr) {
        this.scheduleTransferFromStr = scheduleTransferFromStr;
    }

    public String getScheduleTransferToStr() {
        return scheduleTransferToStr;
    }

    public void setScheduleTransferToStr(String scheduleTransferToStr) {
        this.scheduleTransferToStr = scheduleTransferToStr;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }
}
