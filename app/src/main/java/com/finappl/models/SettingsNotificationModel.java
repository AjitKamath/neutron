package com.finappl.models;

import java.util.Date;

/**
 * Created by ajit on 1/9/15.
 */
public class SettingsNotificationModel {
    private String SET_NOTIF_ID;
    private String USER_ID;
    private String SET_NOTIF_ACTIVE;
    private String SET_NOTIF_TIME;
    private String SET_NOTIF_BUZZ;
    private Date CREAT_DTM;
    private Date MOD_DTM;

    public String getSET_NOTIF_ID() {
        return SET_NOTIF_ID;
    }

    public void setSET_NOTIF_ID(String SET_NOTIF_ID) {
        this.SET_NOTIF_ID = SET_NOTIF_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getSET_NOTIF_ACTIVE() {
        return SET_NOTIF_ACTIVE;
    }

    public void setSET_NOTIF_ACTIVE(String SET_NOTIF_ACTIVE) {
        this.SET_NOTIF_ACTIVE = SET_NOTIF_ACTIVE;
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
