package com.finappl.models;

import java.io.Serializable;
import java.util.Date;


public class UsersModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private String USER_ID;
	private String PASS;
	private String EMAIL;
	private String NAME;
	private Date DOB;
	private String TELEPHONE;
	private String CUR_ID;
	private String CNTRY_ID;
	private String DEV_ID;
	private String STAT;
	private Date CREATE_DTM;
	private Date UPDATE_DTM;

	private String countryName;
	private String currencyName;
	private String currencyText;
	private Date userCreatDtm;
	private Date userModDtm;
	private String WORK_TYPE;
	private String COMPANY;
	private Double SALARY;
	private String SAL_FREQ;
	private Date workCreatDtm;
	private Date workModDtm;

	private String SET_NOTIF_ACTIVE;
	private String SET_NOTIF_TIME;
	private String SET_NOTIF_BUZZ;
	private String SET_SND_ACTIVE;

	private String SET_SEC_ACTIVE;
	private String SET_SEC_PIN;

	//constructors
	public UsersModel(String nAME, String eMAIL, Date dOB,
			String cUR_ID, String cNTRY_ID, Date cREATE_DTM) {
		super();
		EMAIL = eMAIL;
		NAME = nAME;
		DOB = dOB;
		CUR_ID = cUR_ID;
		CNTRY_ID = cNTRY_ID;
		CREATE_DTM = cREATE_DTM;
	}
	
	public UsersModel(String uSER_ID, String pASS, String eMAIL, String nAME, Date dOB, String cUR_ID, String cNTRY_ID,
			String dEV_ID, String sTAT, Date cREATE_DTM, Date uPDATE_DTM) {
		super();
		USER_ID = uSER_ID;
		PASS = pASS;
		EMAIL = eMAIL;
		NAME = nAME;
		DOB = dOB;
		CUR_ID = cUR_ID;
		CNTRY_ID = cNTRY_ID;
		DEV_ID = dEV_ID;
		STAT = sTAT;
		CREATE_DTM = cREATE_DTM;
		UPDATE_DTM = uPDATE_DTM;
	}

	public UsersModel() {}

	//getters setters
	public String getCurrencyText() {
		return currencyText;
	}

	public void setCurrencyText(String currencyText) {
		this.currencyText = currencyText;
	}

	public String getUSER_ID() {
		return USER_ID;
	}
	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}
	public String getPASS() {
		return PASS;
	}
	public void setPASS(String pASS) {
		PASS = pASS;
	}
	public String getEMAIL() {
		return EMAIL;
	}
	public void setEMAIL(String eMAIL) {
		EMAIL = eMAIL;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public Date getDOB() {
		return DOB;
	}

	public void setDOB(Date DOB) {
		this.DOB = DOB;
	}

	public String getDEV_ID() {
		return DEV_ID;
	}
	public void setDEV_ID(String dEV_ID) {
		DEV_ID = dEV_ID;
	}
	public String getSTAT() {
		return STAT;
	}
	public void setSTAT(String sTAT) {
		STAT = sTAT;
	}
	public Date getCREATE_DTM() {
		return CREATE_DTM;
	}
	public void setCREATE_DTM(Date cREATE_DTM) {
		CREATE_DTM = cREATE_DTM;
	}
	public Date getUPDATE_DTM() {
		return UPDATE_DTM;
	}
	public void setUPDATE_DTM(Date uPDATE_DTM) {
		UPDATE_DTM = uPDATE_DTM;
	}

	public String getCUR_ID() {
		return CUR_ID;
	}

	public void setCUR_ID(String CUR_ID) {
		this.CUR_ID = CUR_ID;
	}

	public String getCNTRY_ID() {
		return CNTRY_ID;
	}

	public void setCNTRY_ID(String CNTRY_ID) {
		this.CNTRY_ID = CNTRY_ID;
	}

	public String getTELEPHONE() {
		return TELEPHONE;
	}

	public void setTELEPHONE(String TELEPHONE) {
		this.TELEPHONE = TELEPHONE;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public Date getUserCreatDtm() {
		return userCreatDtm;
	}

	public void setUserCreatDtm(Date userCreatDtm) {
		this.userCreatDtm = userCreatDtm;
	}

	public Date getUserModDtm() {
		return userModDtm;
	}

	public void setUserModDtm(Date userModDtm) {
		this.userModDtm = userModDtm;
	}

	public String getWORK_TYPE() {
		return WORK_TYPE;
	}

	public void setWORK_TYPE(String WORK_TYPE) {
		this.WORK_TYPE = WORK_TYPE;
	}

	public String getCOMPANY() {
		return COMPANY;
	}

	public void setCOMPANY(String COMPANY) {
		this.COMPANY = COMPANY;
	}

    public Double getSALARY() {
        return SALARY;
    }

    public void setSALARY(Double SALARY) {
        this.SALARY = SALARY;
    }

    public String getSAL_FREQ() {
		return SAL_FREQ;
	}

	public void setSAL_FREQ(String SAL_FREQ) {
		this.SAL_FREQ = SAL_FREQ;
	}

	public Date getWorkCreatDtm() {
		return workCreatDtm;
	}

	public void setWorkCreatDtm(Date workCreatDtm) {
		this.workCreatDtm = workCreatDtm;
	}

	public Date getWorkModDtm() {
		return workModDtm;
	}

	public void setWorkModDtm(Date workModDtm) {
		this.workModDtm = workModDtm;
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

	public String getSET_SND_ACTIVE() {
		return SET_SND_ACTIVE;
	}

	public void setSET_SND_ACTIVE(String SET_SND_ACTIVE) {
		this.SET_SND_ACTIVE = SET_SND_ACTIVE;
	}

	public String getSET_SEC_ACTIVE() {
		return SET_SEC_ACTIVE;
	}

	public void setSET_SEC_ACTIVE(String SET_SEC_ACTIVE) {
		this.SET_SEC_ACTIVE = SET_SEC_ACTIVE;
	}

	public String getSET_SEC_PIN() {
		return SET_SEC_PIN;
	}

	public void setSET_SEC_PIN(String SET_SEC_PIN) {
		this.SET_SEC_PIN = SET_SEC_PIN;
	}
}
