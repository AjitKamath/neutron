package com.finappl.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ajit on 14/2/15.
 */
public class TransferModel  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String TRNFR_ID;
    private String USER_ID;
    private String ACC_ID_FRM;
    private String ACC_ID_TO;
    private String SCH_TRNFR_ID;
    private Double TRNFR_AMT;
    private String TRNFR_IS_DEL;
    private String TRNFR_NOTE;
    private Date TRNFR_DATE;
    private Date CREAT_DTM;
    private Date MOD_DTM;

    private String fromAccName;
    private String toAccName;
    private String currency;
    private Date schCreateDate;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFromAccName() {
        return fromAccName;
    }

    public void setFromAccName(String fromAccName) {
        this.fromAccName = fromAccName;
    }

    public String getToAccName() {
        return toAccName;
    }

    public void setToAccName(String toAccName) {
        this.toAccName = toAccName;
    }

    public String getTRNFR_ID() {
        return TRNFR_ID;
    }

    public void setTRNFR_ID(String TRNFR_ID) {
        this.TRNFR_ID = TRNFR_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getACC_ID_FRM() {
        return ACC_ID_FRM;
    }

    public void setACC_ID_FRM(String ACC_ID_FRM) {
        this.ACC_ID_FRM = ACC_ID_FRM;
    }

    public String getACC_ID_TO() {
        return ACC_ID_TO;
    }

    public void setACC_ID_TO(String ACC_ID_TO) {
        this.ACC_ID_TO = ACC_ID_TO;
    }

    public String getSCH_TRNFR_ID() {
        return SCH_TRNFR_ID;
    }

    public void setSCH_TRNFR_ID(String SCH_TRNFR_ID) {
        this.SCH_TRNFR_ID = SCH_TRNFR_ID;
    }

    public Double getTRNFR_AMT() {
        return TRNFR_AMT;
    }

    public void setTRNFR_AMT(Double TRNFR_AMT) {
        this.TRNFR_AMT = TRNFR_AMT;
    }

    public String getTRNFR_IS_DEL() {
        return TRNFR_IS_DEL;
    }

    public void setTRNFR_IS_DEL(String TRNFR_IS_DEL) {
        this.TRNFR_IS_DEL = TRNFR_IS_DEL;
    }

    public String getTRNFR_NOTE() {
        return TRNFR_NOTE;
    }

    public void setTRNFR_NOTE(String TRNFR_NOTE) {
        this.TRNFR_NOTE = TRNFR_NOTE;
    }

    public Date getSchCreateDate() {
        return schCreateDate;
    }

    public void setSchCreateDate(Date schCreateDate) {
        this.schCreateDate = schCreateDate;
    }

    public Date getMOD_DTM() {
        return MOD_DTM;
    }

    public void setMOD_DTM(Date MOD_DTM) {
        this.MOD_DTM = MOD_DTM;
    }

    public Date getCREAT_DTM() {
        return CREAT_DTM;
    }

    public void setCREAT_DTM(Date CREAT_DTM) {
        this.CREAT_DTM = CREAT_DTM;
    }

    public Date getTRNFR_DATE() {
        return TRNFR_DATE;
    }

    public void setTRNFR_DATE(Date TRNFR_DATE) {
        this.TRNFR_DATE = TRNFR_DATE;
    }
}
