package com.finappl.models;

import java.util.Date;
import java.util.List;

/**
 * Created by ajit on 25/4/15.
 */
public class DayTransfersModel {
    private List<TransferModel> dayTransfersList;
    private Date date;
    private Double dayTotal;

    public List<TransferModel> getDayTransfersList() {
        return dayTransfersList;
    }

    public void setDayTransfersList(List<TransferModel> dayTransfersList) {
        this.dayTransfersList = dayTransfersList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getDayTotal() {
        return dayTotal;
    }

    public void setDayTotal(Double dayTotal) {
        this.dayTotal = dayTotal;
    }
}