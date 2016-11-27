package com.finappl.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ajit on 31/8/15.
 */
public class ActivitiesMO implements Serializable {

    private List<TransactionMO> transactionsList;
    private List<TransferMO> transfersList;

    public List<TransactionMO> getTransactionsList() {
        return transactionsList;
    }

    public void setTransactionsList(List<TransactionMO> transactionsList) {
        this.transactionsList = transactionsList;
    }

    public List<TransferMO> getTransfersList() {
        return transfersList;
    }

    public void setTransfersList(List<TransferMO> transfersList) {
        this.transfersList = transfersList;
    }
}
