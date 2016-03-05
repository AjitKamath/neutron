package com.finappl.models;

import java.util.Date;

/**
 * Created by ajit on 31/8/15.
 */
public class ConsolidatedTransferModel {

    private Integer count;
    private Double amount;
    private String fromAccountStr;
    private String toAccountStr;
    private Date date;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getFromAccountStr() {
        return fromAccountStr;
    }

    public void setFromAccountStr(String fromAccountStr) {
        this.fromAccountStr = fromAccountStr;
    }

    public String getToAccountStr() {
        return toAccountStr;
    }

    public void setToAccountStr(String toAccountStr) {
        this.toAccountStr = toAccountStr;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
