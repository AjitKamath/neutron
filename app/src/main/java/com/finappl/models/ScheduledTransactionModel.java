package com.finappl.models;

import java.io.Serializable;

/**
 * Created by ajit on 8/2/15.
 */
public class ScheduledTransactionModel implements Serializable {
    private String SCH_TRAN_ID;
    private String USER_ID;
    private String SCH_TRAN_DATE;
    private String SCH_TRAN_FREQ;
    private String SCH_TRAN_TYPE;
    private String SCH_TRAN_AUTO;
    private String SCH_TRAN_CAT_ID;
    private String SCH_TRAN_SPNT_ON_ID;
    private String SCH_TRAN_ACC_ID;
    private String SCH_TRAN_IS_DEL;
    private String CREAT_DTM;
    private String MOD_DTM;
    private Double SCH_TRAN_AMT;
    private String SCH_TRAN_NOTE;
    private String SCH_TRAN_NAME;

    private String categoryNameStr;
    private String accountNameStr;
    private String spentOnNameStr;

    private String status;
    private String scheduledDateStr;

    public String getSCH_TRAN_CAT_ID() {
        return SCH_TRAN_CAT_ID;
    }

    public void setSCH_TRAN_CAT_ID(String SCH_TRAN_CAT_ID) {
        this.SCH_TRAN_CAT_ID = SCH_TRAN_CAT_ID;
    }

    public String getSCH_TRAN_SPNT_ON_ID() {
        return SCH_TRAN_SPNT_ON_ID;
    }

    public void setSCH_TRAN_SPNT_ON_ID(String SCH_TRAN_SPNT_ON_ID) {
        this.SCH_TRAN_SPNT_ON_ID = SCH_TRAN_SPNT_ON_ID;
    }

    public String getSCH_TRAN_ACC_ID() {
        return SCH_TRAN_ACC_ID;
    }

    public void setSCH_TRAN_ACC_ID(String SCH_TRAN_ACC_ID) {
        this.SCH_TRAN_ACC_ID = SCH_TRAN_ACC_ID;
    }

    public String getCategoryNameStr() {
        return categoryNameStr;
    }

    public void setCategoryNameStr(String categoryNameStr) {
        this.categoryNameStr = categoryNameStr;
    }

    public String getAccountNameStr() {
        return accountNameStr;
    }

    public void setAccountNameStr(String accountNameStr) {
        this.accountNameStr = accountNameStr;
    }

    public String getSpentOnNameStr() {
        return spentOnNameStr;
    }

    public void setSpentOnNameStr(String spentOnNameStr) {
        this.spentOnNameStr = spentOnNameStr;
    }

    public String getSCH_TRAN_NAME() {
        return SCH_TRAN_NAME;
    }

    public void setSCH_TRAN_NAME(String SCH_TRAN_NAME) {
        this.SCH_TRAN_NAME = SCH_TRAN_NAME;
    }

    public String getSCH_TRAN_ID() {
        return SCH_TRAN_ID;
    }

    public void setSCH_TRAN_ID(String SCH_TRAN_ID) {
        this.SCH_TRAN_ID = SCH_TRAN_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getSCH_TRAN_DATE() {
        return SCH_TRAN_DATE;
    }

    public void setSCH_TRAN_DATE(String SCH_TRAN_DATE) {
        this.SCH_TRAN_DATE = SCH_TRAN_DATE;
    }

    public String getSCH_TRAN_FREQ() {
        return SCH_TRAN_FREQ;
    }

    public void setSCH_TRAN_FREQ(String SCH_TRAN_FREQ) {
        this.SCH_TRAN_FREQ = SCH_TRAN_FREQ;
    }

    public String getSCH_TRAN_TYPE() {
        return SCH_TRAN_TYPE;
    }

    public void setSCH_TRAN_TYPE(String SCH_TRAN_TYPE) {
        this.SCH_TRAN_TYPE = SCH_TRAN_TYPE;
    }

    public String getSCH_TRAN_AUTO() {
        return SCH_TRAN_AUTO;
    }

    public void setSCH_TRAN_AUTO(String SCH_TRAN_AUTO) {
        this.SCH_TRAN_AUTO = SCH_TRAN_AUTO;
    }

    public String getSCH_TRAN_IS_DEL() {
        return SCH_TRAN_IS_DEL;
    }

    public void setSCH_TRAN_IS_DEL(String SCH_TRAN_IS_DEL) {
        this.SCH_TRAN_IS_DEL = SCH_TRAN_IS_DEL;
    }

    public String getCREAT_DTM() {
        return CREAT_DTM;
    }

    public void setCREAT_DTM(String CREAT_DTM) {
        this.CREAT_DTM = CREAT_DTM;
    }

    public String getMOD_DTM() {
        return MOD_DTM;
    }

    public void setMOD_DTM(String MOD_DTM) {
        this.MOD_DTM = MOD_DTM;
    }

    public Double getSCH_TRAN_AMT() {
        return SCH_TRAN_AMT;
    }

    public void setSCH_TRAN_AMT(Double SCH_TRAN_AMT) {
        this.SCH_TRAN_AMT = SCH_TRAN_AMT;
    }

    public String getSCH_TRAN_NOTE() {
        return SCH_TRAN_NOTE;
    }

    public void setSCH_TRAN_NOTE(String SCH_TRAN_NOTE) {
        this.SCH_TRAN_NOTE = SCH_TRAN_NOTE;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScheduledDateStr() {
        return scheduledDateStr;
    }

    public void setScheduledDateStr(String scheduledDateStr) {
        this.scheduledDateStr = scheduledDateStr;
    }
}
