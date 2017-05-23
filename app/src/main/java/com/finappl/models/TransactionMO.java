package com.finappl.models;

import java.io.Serializable;
import java.util.Date;

public class TransactionMO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String TRAN_ID;
	private String USER_ID;
	private String CAT_ID;
	private String SPNT_ON_ID;
	private String ACC_ID;
	private String REPEAT_ID;
	private String PARENT_TRAN_ID;
	private Double TRAN_AMT;
	private String TRAN_NAME;
	private String TRAN_TYPE;
	private String TRAN_NOTE;
	private Date TRAN_DATE;
	private String NOTIFY;
	private String NOTIFY_TIME;
	private Date CREAT_DTM;
	private Date MOD_DTM;
	private String SCHD_UPTO_DATE;

    private String category;
    private String spentOn;
    private String account;

	private String repeat;

	private String categoryImg;
	private String accountImg;
	private String spentOnImg;
	private String repeatImg;

    private String currency;

    private CategoryMO categoryObj;
    private SpentOnMO spentOnObj;
    private AccountMO accountObj;

    private Date schCreateDate;

	private String transactionDate;
	private String creatDtm;

	public Date getSchCreateDate() {
		return schCreateDate;
	}

	public void setSchCreateDate(Date schCreateDate) {
		this.schCreateDate = schCreateDate;
	}

	//	constructors
	public TransactionMO() {}


	//getters setters


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

	public CategoryMO getCategoryObj() {
		return categoryObj;
	}

	public void setCategoryObj(CategoryMO categoryObj) {
		this.categoryObj = categoryObj;
	}

	public SpentOnMO getSpentOnObj() {
		return spentOnObj;
	}

	public void setSpentOnObj(SpentOnMO spentOnObj) {
		this.spentOnObj = spentOnObj;
	}

	public AccountMO getAccountObj() {
        return accountObj;
    }

    public void setAccountObj(AccountMO accountObj) {
        this.accountObj = accountObj;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpentOn() {
        return spentOn;
    }

    public void setSpentOn(String spentOn) {
        this.spentOn = spentOn;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTRAN_ID() {
		return TRAN_ID;
	}
	public void setTRAN_ID(String tRAN_ID) {
		TRAN_ID = tRAN_ID;
	}
	public String getUSER_ID() {
		return USER_ID;
	}
	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}
	public String getCAT_ID() {
		return CAT_ID;
	}
	public void setCAT_ID(String cAT_ID) {
		CAT_ID = cAT_ID;
	}
	public String getSPNT_ON_ID() {
		return SPNT_ON_ID;
	}
	public void setSPNT_ON_ID(String sPNT_ON_ID) {
		SPNT_ON_ID = sPNT_ON_ID;
	}
	public String getACC_ID() {
		return ACC_ID;
	}
	public void setACC_ID(String aCC_ID) {
		ACC_ID = aCC_ID;
	}
	public Double getTRAN_AMT() {
		return TRAN_AMT;
	}
	public void setTRAN_AMT(Double tRAN_AMT) {
		TRAN_AMT = tRAN_AMT;
	}
	public String getTRAN_NAME() {
		return TRAN_NAME;
	}
	public void setTRAN_NAME(String tRAN_NAME) {
		TRAN_NAME = tRAN_NAME;
	}
	public String getTRAN_TYPE() {
		return TRAN_TYPE;
	}
	public void setTRAN_TYPE(String tRAN_TYPE) {
		TRAN_TYPE = tRAN_TYPE;
	}
	public String getTRAN_NOTE() {
		return TRAN_NOTE;
	}
	public void setTRAN_NOTE(String tRAN_NOTE) {
		TRAN_NOTE = tRAN_NOTE;
	}

	public Date getTRAN_DATE() {
		return TRAN_DATE;
	}

	public void setTRAN_DATE(Date TRAN_DATE) {
		this.TRAN_DATE = TRAN_DATE;
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

	public String getREPEAT_ID() {
		return REPEAT_ID;
	}

	public void setREPEAT_ID(String REPEAT_ID) {
		this.REPEAT_ID = REPEAT_ID;
	}

	public String getNOTIFY() {
		return NOTIFY;
	}

	public void setNOTIFY(String NOTIFY) {
		this.NOTIFY = NOTIFY;
	}

	public String getNOTIFY_TIME() {
		return NOTIFY_TIME;
	}

	public void setNOTIFY_TIME(String NOTIFY_TIME) {
		this.NOTIFY_TIME = NOTIFY_TIME;
	}

	public String getCategoryImg() {
		return categoryImg;
	}

	public void setCategoryImg(String categoryImg) {
		this.categoryImg = categoryImg;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getCreatDtm() {

		return creatDtm;
	}

	public void setCreatDtm(String creatDtm) {
		this.creatDtm = creatDtm;
	}

	public String getAccountImg() {
		return accountImg;
	}

	public void setAccountImg(String accountImg) {
		this.accountImg = accountImg;
	}

	public String getSpentOnImg() {
		return spentOnImg;
	}

	public void setSpentOnImg(String spentOnImg) {
		this.spentOnImg = spentOnImg;
	}

	public String getRepeatImg() {
		return repeatImg;
	}

	public void setRepeatImg(String repeatImg) {
		this.repeatImg = repeatImg;
	}

	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}

	public String getSCHD_UPTO_DATE() {
		return SCHD_UPTO_DATE;
	}

	public void setSCHD_UPTO_DATE(String SCHD_UPTO_DATE) {
		this.SCHD_UPTO_DATE = SCHD_UPTO_DATE;
	}

	public String getPARENT_TRAN_ID() {
		return PARENT_TRAN_ID;
	}

	public void setPARENT_TRAN_ID(String PARENT_TRAN_ID) {
		this.PARENT_TRAN_ID = PARENT_TRAN_ID;
	}
}
