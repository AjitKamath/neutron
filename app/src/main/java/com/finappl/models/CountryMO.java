package com.finappl.models;

import java.io.Serializable;

/**
 * Created by ajit on 27/11/16.
 */

public class CountryMO implements Serializable {
    private String CNTRY_ID;
    private String CNTRY_NAME;
    private String CNTRY_CODE;
    private String CUR;
    private String CUR_CODE;
    private String CNTRY_IMG;

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

    public String getCNTRY_IMG() {
        return CNTRY_IMG;
    }

    public void setCNTRY_IMG(String CNTRY_IMG) {
        this.CNTRY_IMG = CNTRY_IMG;
    }
}
