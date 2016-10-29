package com.finappl.models;

import java.util.Date;

/**
 * Created by ajit on 15/4/16.
 */
public class CountryMO {
    private String CNTRY_ID;
    private String CNTRY_NAME;
    private String CNTRY_CODE;
    private String CUR;
    private String CUR_CODE;
    private Date CREAT_DTM;
    private Date MOD_DTM;

    public String getCNTRY_ID() {
        return CNTRY_ID;
    }

    public void setCNTRY_ID(String CNTRY_ID) {
        this.CNTRY_ID = CNTRY_ID;
    }

    public String getCNTRY_NAME() {
        return CNTRY_NAME;
    }

    public void setCNTRY_NAME(String CNTRY_NAME) {
        this.CNTRY_NAME = CNTRY_NAME;
    }

    public String getCNTRY_CODE() {
        return CNTRY_CODE;
    }

    public void setCNTRY_CODE(String CNTRY_CODE) {
        this.CNTRY_CODE = CNTRY_CODE;
    }

    public String getCUR() {
        return CUR;
    }

    public void setCUR(String CUR) {
        this.CUR = CUR;
    }

    public String getCUR_CODE() {
        return CUR_CODE;
    }

    public void setCUR_CODE(String CUR_CODE) {
        this.CUR_CODE = CUR_CODE;
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
