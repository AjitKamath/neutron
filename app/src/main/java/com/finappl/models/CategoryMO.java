package com.finappl.models;

import java.util.Date;
import java.util.List;

public class CategoryMO {

	private String CAT_ID;
	private String USER_ID;
	private String CAT_NAME;
	private String CAT_IS_DEF;
	private Date CREAT_DTM;
	private Date MOD_DTM;

	public String getCAT_ID() {
		return CAT_ID;
	}

	public void setCAT_ID(String CAT_ID) {
		this.CAT_ID = CAT_ID;
	}

	public String getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(String USER_ID) {
		this.USER_ID = USER_ID;
	}

	public String getCAT_NAME() {
		return CAT_NAME;
	}

	public void setCAT_NAME(String CAT_NAME) {
		this.CAT_NAME = CAT_NAME;
	}

	public String getCAT_IS_DEF() {
		return CAT_IS_DEF;
	}

	public void setCAT_IS_DEF(String CAT_IS_DEF) {
		this.CAT_IS_DEF = CAT_IS_DEF;
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
