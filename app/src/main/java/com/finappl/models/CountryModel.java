package com.finappl.models;

import java.util.Date;

/**
 * Created by ajit on 18/6/15.
 */
public class CountryModel {
    private String CNTRY_ID;
    private String CUR_ID;
    private String CNTRY_NAME;
    private String CNTRY_FLAG;
    private Date CREAT_DTM;
    private Date MOD_DTM;

    private String curNameStr;

    public String getCurNameStr() {
        return curNameStr;
    }

    public void setCurNameStr(String curNameStr) {
        this.curNameStr = curNameStr;
    }

    public String getCNTRY_ID() {
        return CNTRY_ID;
    }

    public void setCNTRY_ID(String CNTRY_ID) {
        this.CNTRY_ID = CNTRY_ID;
    }

    public String getCUR_ID() {
        return CUR_ID;
    }

    public void setCUR_ID(String CUR_ID) {
        this.CUR_ID = CUR_ID;
    }

    public String getCNTRY_NAME() {
        return CNTRY_NAME;
    }

    public void setCNTRY_NAME(String CNTRY_NAME) {
        this.CNTRY_NAME = CNTRY_NAME;
    }

    public String getCNTRY_FLAG() {
        return CNTRY_FLAG;
    }

    public void setCNTRY_FLAG(String CNTRY_FLAG) {
        this.CNTRY_FLAG = CNTRY_FLAG;
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
