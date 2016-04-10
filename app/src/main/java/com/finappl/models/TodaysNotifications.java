package com.finappl.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ajit on 15/8/15.
 */
public class TodaysNotifications implements Serializable {

    private List<ScheduledTransactionModel> todaysSchedTransactionsList;
    private List<ScheduledTransferModel> todaysSchedTransfersList;

    private UserMO loggedInUser;

    public List<ScheduledTransactionModel> getTodaysSchedTransactionsList() {
        return todaysSchedTransactionsList;
    }

    public void setTodaysSchedTransactionsList(List<ScheduledTransactionModel> todaysSchedTransactionsList) {
        this.todaysSchedTransactionsList = todaysSchedTransactionsList;
    }

    public List<ScheduledTransferModel> getTodaysSchedTransfersList() {
        return todaysSchedTransfersList;
    }

    public void setTodaysSchedTransfersList(List<ScheduledTransferModel> todaysSchedTransfersList) {
        this.todaysSchedTransfersList = todaysSchedTransfersList;
    }

    public UserMO getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(UserMO loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
}
