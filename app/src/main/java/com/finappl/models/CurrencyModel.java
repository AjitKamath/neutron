package com.finappl.models;

import java.util.Date;

/**
 * Created by ajit on 18/6/15.
 */
public class CurrencyModel {
    private String CUR_ID;
    private String CUR_NAME;
    private String CUR_SYMB;
    private Date CREAT_DTM;
    private Date MOD_DTM;

    public String getCUR_ID() {
        return CUR_ID;
    }

    public void setCUR_ID(String CUR_ID) {
        this.CUR_ID = CUR_ID;
    }

    public String getCUR_NAME() {
        return CUR_NAME;
    }

    public void setCUR_NAME(String CUR_NAME) {
        this.CUR_NAME = CUR_NAME;
    }

    public String getCUR_SYMB() {
        return CUR_SYMB;
    }

    public void setCUR_SYMB(String CUR_SYMB) {
        this.CUR_SYMB = CUR_SYMB;
    }

    public Date getCREAT_DTM() {
        return CREAT_DTM;
    }

    public void setCREAT_DTM(Date CREAT_DTM) {
        this.CREAT_DTM = CREAT_DTM;
    }

    public Date getMOD_DTM() {
        return MOD_DTM;
    }

    public void setMOD_DTM(Date MOD_DTM) {
        this.MOD_DTM = MOD_DTM;
    }
}
