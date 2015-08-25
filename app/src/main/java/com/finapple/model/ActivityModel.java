package com.finapple.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ajit on 9/4/15.
 */
public class ActivityModel implements Serializable{

    private String fromDateStr;
    private String toDateStr;
    private String dateStr;
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

    public String getFromDateStr() {
        return fromDateStr;
    }

    public void setFromDateStr(String fromDateStr) {
        this.fromDateStr = fromDateStr;
    }

    public String getToDateStr() {
        return toDateStr;
    }

    public void setToDateStr(String toDateStr) {
        this.toDateStr = toDateStr;
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

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
