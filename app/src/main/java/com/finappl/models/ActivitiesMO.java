package com.finappl.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajit on 31/8/15.
 */
public class ActivitiesMO {

    private List<TransactionModel> transactionsList;
    private List<TransferModel> transfersList;

    public List<TransactionModel> getTransactionsList() {
        return transactionsList;
    }

    public void setTransactionsList(List<TransactionModel> transactionsList) {
        this.transactionsList = transactionsList;
    }

    public List<TransferModel> getTransfersList() {
        return transfersList;
    }

    public void setTransfersList(List<TransferModel> transfersList) {
        this.transfersList = transfersList;
    }
}
