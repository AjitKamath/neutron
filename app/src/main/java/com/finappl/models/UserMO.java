package com.finappl.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ajit on 9/4/16.
 */
public class UserMO implements Serializable {
    private String USER_ID;
    private String CNTRY_ID;
    private String METRIC;
    private String NAME;
    private String PASS;
    private String EMAIL;
    private Date DOB;
    private String TELEPHONE;
    private String DEV_ID;
    private Date CREAT_DTM;
    private Date MOD_DTM;

    private String CNTRY_NAME;
    private String CNTRY_CODE;
    private String CUR;
    private String CUR_CODE;
    private String SET_NOTIF_TIME;
    private String SET_NOTIF_BUZZ;
    private String SET_SEC_PIN;

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getCNTRY_ID() {
        return CNTRY_ID;
    }

    public void setCNTRY_ID(String CNTRY_ID) {
        this.CNTRY_ID = CNTRY_ID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getPASS() {
        return PASS;
    }

    public void setPASS(String PASS) {
        this.PASS = PASS;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public Date getDOB() {
        return DOB;
    }

    public void setDOB(Date DOB) {
        this.DOB = DOB;
    }

    public String getTELEPHONE() {
        return TELEPHONE;
    }

    public void setTELEPHONE(String TELEPHONE) {
        this.TELEPHONE = TELEPHONE;
    }

    public String getDEV_ID() {
        return DEV_ID;
    }

    public void setDEV_ID(String DEV_ID) {
        this.DEV_ID = DEV_ID;
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

    public String getCNTRY_NAME() {
        return CNTRY_NAME;
    }

    public void setCNTRY_NAME(String CNTRY_NAME) {
        this.CNTRY_NAME = CNTRY_NAME;
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

    public String getSET_NOTIF_TIME() {
        return SET_NOTIF_TIME;
    }

    public void setSET_NOTIF_TIME(String SET_NOTIF_TIME) {
        this.SET_NOTIF_TIME = SET_NOTIF_TIME;
    }

    public String getSET_NOTIF_BUZZ() {
        return SET_NOTIF_BUZZ;
    }

    public void setSET_NOTIF_BUZZ(String SET_NOTIF_BUZZ) {
        this.SET_NOTIF_BUZZ = SET_NOTIF_BUZZ;
    }

    public String getSET_SEC_PIN() {
        return SET_SEC_PIN;
    }

    public void setSET_SEC_PIN(String SET_SEC_PIN) {
        this.SET_SEC_PIN = SET_SEC_PIN;
    }

    public String getCNTRY_CODE() {
        return CNTRY_CODE;
    }

    public void setCNTRY_CODE(String CNTRY_CODE) {
        this.CNTRY_CODE = CNTRY_CODE;
    }

    public String getMETRIC() {
        return METRIC;
    }

    public void setMETRIC(String METRIC) {
        this.METRIC = METRIC;
    }
}
