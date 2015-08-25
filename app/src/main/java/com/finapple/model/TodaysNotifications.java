package com.finapple.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ajit on 15/8/15.
 */
public class TodaysNotifications implements Serializable {

    private List<ScheduledTransactionModel> todaysSchedTransactionsList;
    private List<ScheduledTransferModel> todaysSchedTransfersList;

    private UsersModel loggedInUser;

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

    public UsersModel getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(UsersModel loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
}
