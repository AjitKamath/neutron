package com.finappl.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by ajit on 9/4/15.
 */
public class ActivityModel implements Serializable{

    private Date fromDate;
    private Date toDate;
    private Date date;
    private String whichActivityStr;
    private Map<String, DayTransactionsModel> transactionsMap;
    private Map<String, DayTransfersModel> transfersMap;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWhichActivityStr() {
        return whichActivityStr;
    }

    public void setWhichActivityStr(String whichActivityStr) {
        this.whichActivityStr = whichActivityStr;
    }

    public Map<String, DayTransactionsModel> getTransactionsMap() {
        return transactionsMap;
    }

    public void setTransactionsMap(Map<String, DayTransactionsModel> transactionsMap) {
        this.transactionsMap = transactionsMap;
    }

    public Map<String, DayTransfersModel> getTransfersMap() {
        return transfersMap;
    }

    public void setTransfersMap(Map<String, DayTransfersModel> transfersMap) {
        this.transfersMap = transfersMap;
    }
}
