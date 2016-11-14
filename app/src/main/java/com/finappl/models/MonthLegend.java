package com.finappl.models;

import java.util.List;

/**
 * Created by ajit on 18/1/15.
 */
public class MonthLegend {
    private String date;
    private ActivitiesMO activities;
    private Double totalAmount;

    private List<ScheduledTransactionModel> scheduledTransactionModelList;
    private boolean hasScheduledTransaction;

    private boolean hasScheduledTransfer;
    private List<ScheduledTransferModel> scheduledTransferModelList;

    //getters setters
    public boolean isHasScheduledTransfer() {
        return hasScheduledTransfer;
    }

    public void setHasScheduledTransfer(boolean hasScheduledTransfer) {
        this.hasScheduledTransfer = hasScheduledTransfer;
    }

    public List<ScheduledTransferModel> getScheduledTransferModelList() {
        return scheduledTransferModelList;
    }

    public void setScheduledTransferModelList(List<ScheduledTransferModel> scheduledTransferModelList) {
        this.scheduledTransferModelList = scheduledTransferModelList;
    }

    public boolean isHasScheduledTransaction() {
        return hasScheduledTransaction;
    }

    public void setHasScheduledTransaction(boolean hasScheduledTransaction) {
        this.hasScheduledTransaction = hasScheduledTransaction;
    }

    public List<ScheduledTransactionModel> getScheduledTransactionModelList() {
        return scheduledTransactionModelList;
    }

    public void setScheduledTransactionModelList(List<ScheduledTransactionModel> scheduledTransactionModelList) {
        this.scheduledTransactionModelList = scheduledTransactionModelList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ActivitiesMO getActivities() {
        return activities;
    }

    public void setActivities(ActivitiesMO activities) {
        this.activities = activities;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
