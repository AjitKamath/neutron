package com.finappl.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ajit on 4/4/15.
 */
public class BudgetMO implements Serializable{
    private String BUDGET_ID;
    private String USER_ID;
    private String BUDGET_NAME;
    private String BUDGET_GRP_ID;
    private String BUDGET_TYPE;
    private Double BUDGET_AMT;
    private String BUDGET_NOTE;
    private Date CREAT_DTM;
    private Date MOD_DTM;
    private String BUDGET_GRP_TYPE;

    private Double budgetRangeTotal;
    private String accountNameStr;
    private String categoryNameStr;
    private String spentOnNameStr;

    private String budgetGroupImage;
    private String budgetGroupName;

    public String getBUDGET_ID() {
        return BUDGET_ID;
    }

    public void setBUDGET_ID(String BUDGET_ID) {
        this.BUDGET_ID = BUDGET_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getBUDGET_NAME() {
        return BUDGET_NAME;
    }

    public void setBUDGET_NAME(String BUDGET_NAME) {
        this.BUDGET_NAME = BUDGET_NAME;
    }

    public String getBUDGET_GRP_ID() {
        return BUDGET_GRP_ID;
    }

    public void setBUDGET_GRP_ID(String BUDGET_GRP_ID) {
        this.BUDGET_GRP_ID = BUDGET_GRP_ID;
    }

    public String getBUDGET_TYPE() {
        return BUDGET_TYPE;
    }

    public void setBUDGET_TYPE(String BUDGET_TYPE) {
        this.BUDGET_TYPE = BUDGET_TYPE;
    }

    public Double getBUDGET_AMT() {
        return BUDGET_AMT;
    }

    public void setBUDGET_AMT(Double BUDGET_AMT) {
        this.BUDGET_AMT = BUDGET_AMT;
    }

    public String getBUDGET_NOTE() {
        return BUDGET_NOTE;
    }

    public void setBUDGET_NOTE(String BUDGET_NOTE) {
        this.BUDGET_NOTE = BUDGET_NOTE;
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

    public String getBUDGET_GRP_TYPE() {
        return BUDGET_GRP_TYPE;
    }

    public Double getBudgetRangeTotal() {
        return budgetRangeTotal;
    }

    public void setBudgetRangeTotal(Double budgetRangeTotal) {
        this.budgetRangeTotal = budgetRangeTotal;
    }

    public void setBUDGET_GRP_TYPE(String BUDGET_GRP_TYPE) {
        this.BUDGET_GRP_TYPE = BUDGET_GRP_TYPE;
    }

    public String getAccountNameStr() {
        return accountNameStr;
    }

    public void setAccountNameStr(String accountNameStr) {
        this.accountNameStr = accountNameStr;
    }

    public String getCategoryNameStr() {
        return categoryNameStr;
    }

    public void setCategoryNameStr(String categoryNameStr) {
        this.categoryNameStr = categoryNameStr;
    }

    public String getSpentOnNameStr() {
        return spentOnNameStr;
    }

    public void setSpentOnNameStr(String spentOnNameStr) {
        this.spentOnNameStr = spentOnNameStr;
    }

    public String getBudgetGroupImage() {
        return budgetGroupImage;
    }

    public void setBudgetGroupImage(String budgetGroupImage) {
        this.budgetGroupImage = budgetGroupImage;
    }

    public String getBudgetGroupName() {
        return budgetGroupName;
    }

    public void setBudgetGroupName(String budgetGroupName) {
        this.budgetGroupName = budgetGroupName;
    }
}
