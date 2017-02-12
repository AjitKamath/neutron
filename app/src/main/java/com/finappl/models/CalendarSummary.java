package com.finappl.models;

import android.widget.Adapter;

import com.finappl.adapters.CalendarSummaryTransactionsListViewAdapter;

/**
 * Created by ajit on 9/2/17.
 */

public class CalendarSummary {

    private String heading;
    private Double amount;
    private Object listViewAdapter;

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Object getListViewAdapter() {
        return listViewAdapter;
    }

    public void setListViewAdapter(Object listViewAdapter) {
        this.listViewAdapter = listViewAdapter;
    }
}
