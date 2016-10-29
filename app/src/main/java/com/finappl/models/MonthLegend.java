package com.finappl.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajit on 18/1/15.
 */
public class MonthLegend {
    private String date;
    private SummaryModel summaryModel;

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

    public SummaryModel getSummaryModel() {
        return summaryModel;
    }

    public void setSummaryModel(SummaryModel summaryModel) {
        this.summaryModel = summaryModel;
    }
}
