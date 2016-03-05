package com.finappl.models;

import java.io.Serializable;
import java.util.Date;

public class TransactionModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String TRAN_ID;
	private String USER_ID;
	private String CAT_ID;
	private String SPNT_ON_ID;
	private String ACC_ID;
	private String SCH_TRAN_ID;
	private Double TRAN_AMT;
	private String TRAN_NAME;
	private String TRAN_TYPE;
	private String TRAN_NOTE;
	private Date TRAN_DATE;
	private String TRAN_IS_DEL;
	private Date CREAT_DTM;
	private Date MOD_DTM;

    private String category;
    private String spentOn;
    private String account;

    private String currency;

    private CategoryModel categoryObj;
    private SpentOnModel spentOnObj;
    private AccountsModel accountObj;

    private Date schCreateDate;

	public Date getSchCreateDate() {
		return schCreateDate;
	}

	public void setSchCreateDate(Date schCreateDate) {
		this.schCreateDate = schCreateDate;
	}

	//	constructors
	public TransactionModel() {}


	//getters setters


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public CategoryModel getCategoryObj() {
        return categoryObj;
    }

    public void setCategoryObj(CategoryModel categoryObj) {
        this.categoryObj = categoryObj;
    }

    public SpentOnModel getSpentOnObj() {
        return spentOnObj;
    }

    public void setSpentOnObj(SpentOnModel spentOnObj) {
        this.spentOnObj = spentOnObj;
    }

    public AccountsModel getAccountObj() {
        return accountObj;
    }

    public void setAccountObj(AccountsModel accountObj) {
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
	public String getSCH_TRAN_ID() {
		return SCH_TRAN_ID;
	}
	public void setSCH_TRAN_ID(String sCH_TRAN_ID) {
		SCH_TRAN_ID = sCH_TRAN_ID;
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

	public String getTRAN_IS_DEL() {
		return TRAN_IS_DEL;
	}

	public void setTRAN_IS_DEL(String TRAN_IS_DEL) {
		this.TRAN_IS_DEL = TRAN_IS_DEL;
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
