package com.finappl.models;

import java.util.List;

/**
 * Created by ajit on 25/4/15.
 */
public class DayTransfersModel {
    private List<TransferModel> dayTransfersList;
    private String dateStr;
    private Double dayTotal;

    public List<TransferModel> getDayTransfersList() {
        return dayTransfersList;
    }

    public void setDayTransfersList(List<TransferModel> dayTransfersList) {
        this.dayTransfersList = dayTransfersList;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public Double getDayTotal() {
        return dayTotal;
    }

    public void setDayTotal(Double dayTotal) {
        this.dayTotal = dayTotal;
    }
}