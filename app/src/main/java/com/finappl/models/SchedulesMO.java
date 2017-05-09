package com.finappl.models;

import java.util.List;

/**
 * Created by ajit on 29/3/17.
 */

public class SchedulesMO {
    private List<TransactionMO> scheduledTransactionsList;
    private List<TransferMO> scheduledTransfersList;

    public List<TransactionMO> getScheduledTransactionsList() {
        return scheduledTransactionsList;
    }

    public void setScheduledTransactionsList(List<TransactionMO> scheduledTransactionsList) {
        this.scheduledTransactionsList = scheduledTransactionsList;
    }

    public List<TransferMO> getScheduledTransfersList() {
        return scheduledTransfersList;
    }

    public void setScheduledTransfersList(List<TransferMO> scheduledTransfersList) {
        this.scheduledTransfersList = scheduledTransfersList;
    }
}
