package com.finappl.models;

import java.util.Date;
import java.util.List;

public class CategoryModel {

	private String CAT_ID;
	private String USER_ID;
	private String CAT_NAME;
	private String CAT_IS_DEFAULT;
	private String CAT_IS_DEL;
	private Date CREAT_DTM;
	private Date MOD_DTM;
    private String CAT_TYPE;
    private String CAT_NOTE;

    private List<TagModel> categoryTagList;

    public String getCAT_NOTE() {
        return CAT_NOTE;
    }

    public void setCAT_NOTE(String CAT_NOTE) {
        this.CAT_NOTE = CAT_NOTE;
    }

    public String getCAT_TYPE() {
        return CAT_TYPE;
    }

    public void setCAT_TYPE(String CAT_TYPE) {
        this.CAT_TYPE = CAT_TYPE;
    }

    public List<TagModel> getCategoryTagList() {
        return categoryTagList;
    }

    public void setCategoryTagList(List<TagModel> categoryTagList) {
        this.categoryTagList = categoryTagList;
    }

    public String getCAT_ID() {
		return CAT_ID;
	}
	public void setCAT_ID(String cAT_ID) {
		CAT_ID = cAT_ID;
	}
	public String getUSER_ID() {
		return USER_ID;
	}
	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}
	public String getCAT_NAME() {
		return CAT_NAME;
	}
	public void setCAT_NAME(String cAT_NAME) {
		CAT_NAME = cAT_NAME;
	}
	public String getCAT_IS_DEFAULT() {
		return CAT_IS_DEFAULT;
	}
	public void setCAT_IS_DEFAULT(String cAT_IS_DEFAULT) {
		CAT_IS_DEFAULT = cAT_IS_DEFAULT;
	}
	public String getCAT_IS_DEL() {
		return CAT_IS_DEL;
	}
	public void setCAT_IS_DEL(String cAT_IS_DEL) {
		CAT_IS_DEL = cAT_IS_DEL;
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
