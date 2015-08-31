package com.finappl.models;

import java.util.Date;

public class SpentOnModel 
{
	private String SPNT_ON_ID;
	private String USER_ID;
	private String SPNT_ON_NAME;
	private String SPNT_ON_IS_DEFAULT;
	private String SPNT_ON_IS_DEL;
	private Date CREAT_DTM;
	private Date MOD_DTM;
    private String SPNT_ON_NOTE;

    public String getSPNT_ON_NOTE() {
        return SPNT_ON_NOTE;
    }

    public void setSPNT_ON_NOTE(String SPNT_ON_NOTE) {
        this.SPNT_ON_NOTE = SPNT_ON_NOTE;
    }

    public String getSPNT_ON_ID() {
		return SPNT_ON_ID;
	}
	public void setSPNT_ON_ID(String sPNT_ON_ID) {
		SPNT_ON_ID = sPNT_ON_ID;
	}
	public String getUSER_ID() {
		return USER_ID;
	}
	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}
	public String getSPNT_ON_NAME() {
		return SPNT_ON_NAME;
	}
	public void setSPNT_ON_NAME(String sPNT_ON_NAME) {
		SPNT_ON_NAME = sPNT_ON_NAME;
	}
	public String getSPNT_ON_IS_DEFAULT() {
		return SPNT_ON_IS_DEFAULT;
	}
	public void setSPNT_ON_IS_DEFAULT(String sPNT_ON_IS_DEFAULT) {
		SPNT_ON_IS_DEFAULT = sPNT_ON_IS_DEFAULT;
	}
	public String getSPNT_ON_IS_DEL() {
		return SPNT_ON_IS_DEL;
	}
	public void setSPNT_ON_IS_DEL(String sPNT_ON_IS_DEL) {
		SPNT_ON_IS_DEL = sPNT_ON_IS_DEL;
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
}
