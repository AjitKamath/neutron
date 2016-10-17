package com.finappl.models;

import java.io.Serializable;
import java.util.Date;

public class AccountsMO implements Serializable {

    private String ACC_ID;
    private String USER_ID;
    private String ACC_NAME;
    private String ACC_IS_DEL;
    private String ACC_IS_DEF;
    private Double ACC_BUDGET;
    private Date CREAT_DTM;
    private Date MOD_DTM;
    private Double ACC_TOTAL;
    private String ACC_NOTE;

    private String currency;
    private Double initialAmount;

    public Double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getACC_NOTE() {
        return ACC_NOTE;
    }

    public void setACC_NOTE(String ACC_NOTE) {
        this.ACC_NOTE = ACC_NOTE;
    }

    public Double getACC_TOTAL() {
        return ACC_TOTAL;
    }

    public void setACC_TOTAL(Double ACC_TOTAL) {
        this.ACC_TOTAL = ACC_TOTAL;
    }

    public String getACC_ID() {
        return ACC_ID;
    }

    public void setACC_ID(String aCC_ID) {
        ACC_ID = aCC_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String uSER_ID) {
        USER_ID = uSER_ID;
    }

    public String getACC_NAME() {
        return ACC_NAME;
    }

    public void setACC_NAME(String aCC_NAME) {
        ACC_NAME = aCC_NAME;
    }

    public String getACC_IS_DEL() {
        return ACC_IS_DEL;
    }

    public void setACC_IS_DEL(String aCC_IS_DEL) {
        ACC_IS_DEL = aCC_IS_DEL;
    }

    public Double getACC_BUDGET() {
        return ACC_BUDGET;
    }

    public void setACC_BUDGET(Double aCC_BUDGET) {
        ACC_BUDGET = aCC_BUDGET;
    }

    public Date getCREAT_DTM() {
        return CREAT_DTM;
    }

    public void setCREAT_DTM(Date cREAT_DTM) {
        CREAT_DTM = cREAT_DTM;
    }

    public Date getMOD_DTM() {
        return MOD_DTM;
    }

    public void setMOD_DTM(Date mOD_DTM) {
        MOD_DTM = mOD_DTM;
    }

    public String getACC_IS_DEF() {
        return ACC_IS_DEF;
    }

    public void setACC_IS_DEF(String ACC_IS_DEF) {
        this.ACC_IS_DEF = ACC_IS_DEF;
    }
}
