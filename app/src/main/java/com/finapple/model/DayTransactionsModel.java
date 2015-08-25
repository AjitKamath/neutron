package com.finapple.model;

import java.util.List;

/**
 * Created by ajit on 25/4/15.
 */
public class DayTransactionsModel {
    private List<TransactionModel> dayTransactionsList;
    private Double dayTotal;
    private String dateStr;

    public List<TransactionModel> getDayTransactionsList() {
        return dayTransactionsList;
    }

    public void setDayTransactionsList(List<TransactionModel> dayTransactionsList) {
        this.dayTransactionsList = dayTransactionsList;
    }

    public Double getDayTotal() {
        return dayTotal;
    }

    public void setDayTotal(Double dayTotal) {
        this.dayTotal = dayTotal;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
