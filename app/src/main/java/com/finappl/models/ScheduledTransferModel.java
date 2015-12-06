package com.finappl.models;

import java.io.Serializable;

/**
 * Created by ajit on 8/2/15.
 */
public class ScheduledTransferModel implements Serializable {
    private String SCH_TRNFR_ID;
    private String USER_ID;
    private String SCH_TRNFR_ACC_ID_FRM;
    private String SCH_TRNFR_ACC_ID_TO;
    private String SCH_TRNFR_DATE;
    private String SCH_TRNFR_FREQ;
    private Double SCH_TRNFR_AMT;
    private String SCH_TRNFR_NOTE;
    private String SCH_TRNFR_AUTO;
    private String SCH_TRNFR_IS_DEL;
    private String CREAT_DTM;
    private String MOD_DTM;

    private String fromAccountStr;
    private String toAccountStr;

    private String status;
    private String ScheduledDateStr;

    public String getSCH_TRNFR_ID() {
        return SCH_TRNFR_ID;
    }

    public void setSCH_TRNFR_ID(String SCH_TRNFR_ID) {
        this.SCH_TRNFR_ID = SCH_TRNFR_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getSCH_TRNFR_ACC_ID_FRM() {
        return SCH_TRNFR_ACC_ID_FRM;
    }

    public void setSCH_TRNFR_ACC_ID_FRM(String SCH_TRNFR_ACC_ID_FRM) {
        this.SCH_TRNFR_ACC_ID_FRM = SCH_TRNFR_ACC_ID_FRM;
    }

    public String getSCH_TRNFR_ACC_ID_TO() {
        return SCH_TRNFR_ACC_ID_TO;
    }

    public void setSCH_TRNFR_ACC_ID_TO(String SCH_TRNFR_ACC_ID_TO) {
        this.SCH_TRNFR_ACC_ID_TO = SCH_TRNFR_ACC_ID_TO;
    }

    public String getSCH_TRNFR_DATE() {
        return SCH_TRNFR_DATE;
    }

    public void setSCH_TRNFR_DATE(String SCH_TRNFR_DATE) {
        this.SCH_TRNFR_DATE = SCH_TRNFR_DATE;
    }

    public String getSCH_TRNFR_FREQ() {
        return SCH_TRNFR_FREQ;
    }

    public void setSCH_TRNFR_FREQ(String SCH_TRNFR_FREQ) {
        this.SCH_TRNFR_FREQ = SCH_TRNFR_FREQ;
    }

    public Double getSCH_TRNFR_AMT() {
        return SCH_TRNFR_AMT;
    }

    public void setSCH_TRNFR_AMT(Double SCH_TRNFR_AMT) {
        this.SCH_TRNFR_AMT = SCH_TRNFR_AMT;
    }

    public String getSCH_TRNFR_NOTE() {
        return SCH_TRNFR_NOTE;
    }

    public void setSCH_TRNFR_NOTE(String SCH_TRNFR_NOTE) {
        this.SCH_TRNFR_NOTE = SCH_TRNFR_NOTE;
    }

    public String getSCH_TRNFR_AUTO() {
        return SCH_TRNFR_AUTO;
    }

    public void setSCH_TRNFR_AUTO(String SCH_TRNFR_AUTO) {
        this.SCH_TRNFR_AUTO = SCH_TRNFR_AUTO;
    }

    public String getSCH_TRNFR_IS_DEL() {
        return SCH_TRNFR_IS_DEL;
    }

    public void setSCH_TRNFR_IS_DEL(String SCH_TRNFR_IS_DEL) {
        this.SCH_TRNFR_IS_DEL = SCH_TRNFR_IS_DEL;
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

    public String getFromAccountStr() {
        return fromAccountStr;
    }

    public void setFromAccountStr(String fromAccountStr) {
        this.fromAccountStr = fromAccountStr;
    }

    public String getToAccountStr() {
        return toAccountStr;
    }

    public void setToAccountStr(String toAccountStr) {
        this.toAccountStr = toAccountStr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScheduledDateStr() {
        return ScheduledDateStr;
    }

    public void setScheduledDateStr(String scheduledDateStr) {
        ScheduledDateStr = scheduledDateStr;
    }
}
