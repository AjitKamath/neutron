package com.finapple.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajit on 18/1/15.
 */
public class MonthLegend {
    private String date;
    private List<ConsolidatedTransactionModel> consolidatedTransactionModelList;
    private Map<String, ConsolidatedTransactionModel> consolTransMap = new HashMap<String, ConsolidatedTransactionModel>();

    private boolean hasTransfer;

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

    public boolean isHasTransfer() {
        return hasTransfer;
    }

    public void setHasTransfer(boolean hasTransfer) {
        this.hasTransfer = hasTransfer;
    }

    public Map<String, ConsolidatedTransactionModel> getConsolTransMap() {
        return consolTransMap;
    }

    public void setConsolTransMap(Map<String, ConsolidatedTransactionModel> consolTransMap) {
        this.consolTransMap = consolTransMap;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ConsolidatedTransactionModel> getConsolidatedTransactionModelList() {
        return consolidatedTransactionModelList;
    }

    public void setConsolidatedTransactionModelList(List<ConsolidatedTransactionModel> consolidatedTransactionModelList) {
        this.consolidatedTransactionModelList = consolidatedTransactionModelList;
    }
}
