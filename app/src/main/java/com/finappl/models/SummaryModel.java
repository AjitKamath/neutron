package com.finappl.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajit on 31/8/15.
 */
public class SummaryModel {

    private Map<String, ConsolidatedTransactionModel> consolidatedTransactionModelMap = new HashMap<>();
    private Map<String, ConsolidatedTransferModel> consolidatedTransferModelMap = new HashMap<>();

    public Map<String, ConsolidatedTransactionModel> getConsolidatedTransactionModelMap() {
        return consolidatedTransactionModelMap;
    }

    public void setConsolidatedTransactionModelMap(Map<String, ConsolidatedTransactionModel> consolidatedTransactionModelMap) {
        this.consolidatedTransactionModelMap = consolidatedTransactionModelMap;
    }

    public Map<String, ConsolidatedTransferModel> getConsolidatedTransferModelMap() {
        return consolidatedTransferModelMap;
    }

    public void setConsolidatedTransferModelMap(Map<String, ConsolidatedTransferModel> consolidatedTransferModelMap) {
        this.consolidatedTransferModelMap = consolidatedTransferModelMap;
    }
}
