package com.finappl.models;

import java.util.Date;
import java.util.List;

/**
 * Created by ajit on 25/4/15.
 */
public class DayTransactionsModel {
    private List<TransactionModel> dayTransactionsList;
    private Double dayTotal;
    private Date date;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
