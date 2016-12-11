package com.finappl.models;

import java.io.Serializable;
import java.util.Date;

public class SpentOnMO implements Serializable {
	private String SPNT_ON_ID;
	private String USER_ID;
	private String SPNT_ON_NAME;
	private String SPNT_ON_IS_DEF;
	private String SPNT_ON_IMG;
	private Date CREAT_DTM;
	private Date MOD_DTM;

	private String defaultSpentonId;
	private String tagsStr;

	public String getSPNT_ON_ID() {
		return SPNT_ON_ID;
	}

	public void setSPNT_ON_ID(String SPNT_ON_ID) {
		this.SPNT_ON_ID = SPNT_ON_ID;
	}

	public String getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(String USER_ID) {
		this.USER_ID = USER_ID;
	}

	public String getSPNT_ON_NAME() {
		return SPNT_ON_NAME;
	}

	public void setSPNT_ON_NAME(String SPNT_ON_NAME) {
		this.SPNT_ON_NAME = SPNT_ON_NAME;
	}

	public String getSPNT_ON_IS_DEF() {
		return SPNT_ON_IS_DEF;
	}

	public void setSPNT_ON_IS_DEF(String SPNT_ON_IS_DEF) {
		this.SPNT_ON_IS_DEF = SPNT_ON_IS_DEF;
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

	public String getSPNT_ON_IMG() {
		return SPNT_ON_IMG;
	}

	public void setSPNT_ON_IMG(String SPNT_ON_IMG) {
		this.SPNT_ON_IMG = SPNT_ON_IMG;
	}

	public String getDefaultSpentonId() {
		return defaultSpentonId;
	}

	public void setDefaultSpentonId(String defaultSpentonId) {
		this.defaultSpentonId = defaultSpentonId;
	}

	public String getTagsStr() {
		return tagsStr;
	}

	public void setTagsStr(String tagsStr) {
		this.tagsStr = tagsStr;
	}
}
