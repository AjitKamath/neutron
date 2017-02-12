package com.finappl.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ajit on 18/1/15.
 */
public class DayLedger implements Serializable  {
    private String date;
    private ActivitiesMO activities;
    private Double transactionsAmountTotal = 0.0;
    private Double transfersAmountTotal = 0.0;
    private boolean hasTransactions;
    private boolean hasTransfers;

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

        if(activities != null){
            if(activities.getTransactionsList() != null && !activities.getTransactionsList().isEmpty()){
                this.hasTransactions = true;
            }
            if(activities.getTransfersList() != null && !activities.getTransfersList().isEmpty()){
                this.hasTransfers = true;
            }
        }
    }

    public Double getTransactionsAmountTotal() {
        return transactionsAmountTotal;
    }

    public void setTransactionsAmountTotal(Double transactionsAmountTotal) {
        this.transactionsAmountTotal = transactionsAmountTotal;
    }

    public Double getTransfersAmountTotal() {
        return transfersAmountTotal;
    }

    public void setTransfersAmountTotal(Double transfersAmountTotal) {
        this.transfersAmountTotal = transfersAmountTotal;
    }

    public boolean isHasTransactions() {
        return hasTransactions;
    }

    public void setHasTransactions(boolean hasTransactions) {
        this.hasTransactions = hasTransactions;
    }

    public boolean isHasTransfers() {
        return hasTransfers;
    }

    public void setHasTransfers(boolean hasTransfers) {
        this.hasTransfers = hasTransfers;
    }
}
