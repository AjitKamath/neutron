package com.finappl.models;

import java.util.Date;

/**
 * Created by ajit on 12/8/15.
 */
public class NotificationModel {

    private String CNCL_NOTIF_ID;
    private String USER_ID;
    private String CNCL_NOTIF_TYPE;
    private String CNCL_NOTIF_EVNT_ID;
    private String CNCL_NOTIF_RSN;
    private String CNCL_NOTIF_DATE;
    private Date CREAT_DTM;
    private Date MOD_DTM;

    public String getCNCL_NOTIF_TYPE() {
        return CNCL_NOTIF_TYPE;
    }

    public void setCNCL_NOTIF_TYPE(String CNCL_NOTIF_TYPE) {
        this.CNCL_NOTIF_TYPE = CNCL_NOTIF_TYPE;
    }

    public String getCNCL_NOTIF_ID() {
        return CNCL_NOTIF_ID;
    }

    public void setCNCL_NOTIF_ID(String CNCL_NOTIF_ID) {
        this.CNCL_NOTIF_ID = CNCL_NOTIF_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getCNCL_NOTIF_EVNT_ID() {
        return CNCL_NOTIF_EVNT_ID;
    }

    public void setCNCL_NOTIF_EVNT_ID(String CNCL_NOTIF_EVNT_ID) {
        this.CNCL_NOTIF_EVNT_ID = CNCL_NOTIF_EVNT_ID;
    }

    public String getCNCL_NOTIF_RSN() {
        return CNCL_NOTIF_RSN;
    }

    public void setCNCL_NOTIF_RSN(String CNCL_NOTIF_RSN) {
        this.CNCL_NOTIF_RSN = CNCL_NOTIF_RSN;
    }

    public String getCNCL_NOTIF_DATE() {
        return CNCL_NOTIF_DATE;
    }

    public void setCNCL_NOTIF_DATE(String CNCL_NOTIF_DATE) {
        this.CNCL_NOTIF_DATE = CNCL_NOTIF_DATE;
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
