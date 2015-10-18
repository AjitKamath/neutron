package com.finappl.dbServices;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.AccountsModel;
import com.finappl.models.BudgetModel;
import com.finappl.models.ConsolidatedTransactionModel;
import com.finappl.models.ConsolidatedTransferModel;
import com.finappl.models.MonthLegend;
import com.finappl.models.ScheduledTransactionModel;
import com.finappl.models.ScheduledTransferModel;
import com.finappl.models.SummaryModel;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;
import com.finappl.utils.DateTimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalendarDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();

	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = Constants.DB_VERSION;
	private static CalendarDbService sInstance = null;

    //new db design
    private static final String USERS_TABLE = Constants.DB_TABLE_USERSTABLE;
    private static final String ACCOUNT_TABLE = Constants.DB_TABLE_ACCOUNTTABLE;
    private static final String CATEGORY_TABLE = Constants.DB_TABLE_CATEGORYTABLE;
    private static final String SPENT_ON_TABLE = Constants.DB_TABLE_SPENTONTABLE;
    private static final String TRANSACTION_TABLE = Constants.DB_TABLE_TRANSACTIONTABLE;
    private static final String SCHEDULED_TRANSACTION_TABLE = Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE;
    private static final String BUDGET_TABLE = Constants.DB_TABLE_BUDGETTABLE;
    private static final String CATEGORY_TAGS_TABLE = Constants.DB_TABLE_CATEGORYTAGSTABLE;
    private static final String SCHEDULED_TRANSFER_TABLE = Constants.DB_TABLE_SHEDULEDTRANSFERSTABLE;
    private static final String TRANSFERS_TABLE = Constants.DB_TABLE_TRANSFERSTABLE;
    private static final String COUNTRY_TABLE = Constants.DB_TABLE_COUNTRYTABLE;
    private static final String CURRENCY_TABLE = Constants.DB_TABLE_CURRENCYTABLE;
    private static final String WORK_TIMELINE_TABLE = Constants.DB_TABLE_WORK_TIMELINETABLE;
    private static final String NOTIFICATIONS_TABLE = Constants.DB_TABLE_NOTIFICATIONSTABLE;


    public TransactionModel getLastTransactionOnAccountId(String accountIdStr){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" TRAN_ID, ");
        sqlQuerySB.append(" CAT_NAME, ");
        sqlQuerySB.append(" TRAN_AMT, ");
        sqlQuerySB.append(" TRAN_TYPE, ");
        sqlQuerySB.append(" TRAN_DATE ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_TRANSACTIONTABLE + " TRAN ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(Constants.DB_TABLE_CATEGORYTABLE + " CAT ");
        sqlQuerySB.append(" ON CAT.CAT_ID = TRAN.CAT_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.ACC_ID = '" + accountIdStr + "' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN_DATE = ");
        sqlQuerySB.append(" (SELECT MAX(TRAN_DATE) FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_TRANSACTIONTABLE + ")");

        Log.i(CLASS_NAME, "Query to fetch Last Transaction made using the Account ID("+accountIdStr+") : "+sqlQuerySB);
        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        TransactionModel transactionModelObj = null;
        if(cursor.moveToNext()){
            transactionModelObj = new TransactionModel();
            transactionModelObj.setTRAN_ID(ColumnFetcher.getInstance().loadString(cursor, "TRAN_ID"));
            transactionModelObj.setCategory(ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME"));
            transactionModelObj.setTRAN_AMT(ColumnFetcher.getInstance().loadDouble(cursor, "TRAN_AMT"));
            transactionModelObj.setTRAN_TYPE(ColumnFetcher.getInstance().loadString(cursor, "TRAN_TYPE"));
            transactionModelObj.setTRAN_DATE(ColumnFetcher.getInstance().loadString(cursor, "TRAN_DATE"));

            return transactionModelObj;
        }

        return null;
    }

    public TransferModel getLastTransferOnAccountId(String accountIdStr){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" TRNFR_ID, ");
        sqlQuerySB.append(" TRNFR_AMT, ");
        sqlQuerySB.append(" TRNFR_FROM.ACC_NAME AS FROM_ACC, ");
        sqlQuerySB.append(" TRNFR_TO.ACC_NAME AS TO_ACC, ");
        sqlQuerySB.append(" TRNFR_DATE ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_TRANSFERSTABLE + " TRNFR ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(Constants.DB_TABLE_ACCOUNTTABLE + " TRNFR_FROM ");
        sqlQuerySB.append(" ON TRNFR_FROM.ACC_ID = TRNFR.ACC_ID_FRM ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(Constants.DB_TABLE_ACCOUNTTABLE + " TRNFR_TO ");
        sqlQuerySB.append(" ON TRNFR_TO.ACC_ID = TRNFR.ACC_ID_TO ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRNFR_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" (TRNFR_FROM.ACC_ID = '"+accountIdStr+"' ");
        sqlQuerySB.append(" OR ");
        sqlQuerySB.append(" TRNFR_TO.ACC_ID = '"+accountIdStr+"' )");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRNFR_DATE = ");
        sqlQuerySB.append(" (SELECT MAX(TRNFR_DATE) FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_TRANSFERSTABLE + ")");

                Log.i(CLASS_NAME, "Query to fetch Last Transfer made using the Account ID(" + accountIdStr + ") : " + sqlQuerySB);
        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        TransferModel transferModelObj = null;
        if(cursor.moveToNext()){
            transferModelObj = new TransferModel();
            transferModelObj.setTRNFR_ID(ColumnFetcher.getInstance().loadString(cursor, "TRNFR_ID"));
            transferModelObj.setFromAccName(ColumnFetcher.getInstance().loadString(cursor, "FROM_ACC"));
            transferModelObj.setToAccName(ColumnFetcher.getInstance().loadString(cursor, "TO_ACC"));
            transferModelObj.setTRNFR_AMT(ColumnFetcher.getInstance().loadDouble(cursor, "TRNFR_AMT"));
            transferModelObj.setTRNFR_DATE(ColumnFetcher.getInstance().loadString(cursor, "TRNFR_DATE"));

            return transferModelObj;
        }

        return null;
    }

    public List<ScheduledTransferModel> getSchedTransfersListAfterCancelledNotifsOnDate(List<ScheduledTransferModel> scheduledTransferModelObjList,
                                                                                        String userIdStr, String todayStr) {
        if(scheduledTransferModelObjList == null || (scheduledTransferModelObjList != null && scheduledTransferModelObjList.isEmpty())){
            return scheduledTransferModelObjList;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CNCL_NOTIF_EVNT_ID ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(NOTIFICATIONS_TABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '"+userIdStr+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" CNCL_NOTIF_DATE = '"+todayStr+"' ");

        String eventIdStr = "";
        for(ScheduledTransferModel iterSchTransferModelList : scheduledTransferModelObjList){
            eventIdStr += "'"+iterSchTransferModelList.getSCH_TRNFR_ID()+"', ";
        }

        if(eventIdStr.contains(", ")){
            eventIdStr = eventIdStr.substring(0, eventIdStr.lastIndexOf(", "));
        }

        sqlQuerySB.append(" AND CNCL_NOTIF_EVNT_ID IN ("+eventIdStr+")");

        Log.i(CLASS_NAME, "Query to get all the cancelled notifications on a particular day : " + String.valueOf(sqlQuerySB));

        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        if(cursor.getCount() == 0){
            return scheduledTransferModelObjList;
        }

        List<ScheduledTransferModel> filteredSchTransfersObjList = new ArrayList<>();
        List<String> alreadyNotifiedNotifsIdList = new ArrayList<>();
        while (cursor.moveToNext()){
            String thisEventIdStr = ColumnFetcher.getInstance().loadString(cursor, "CNCL_NOTIF_EVNT_ID");
            alreadyNotifiedNotifsIdList.add(thisEventIdStr);
        }

        for(ScheduledTransferModel iterSchTransferModelList : scheduledTransferModelObjList){
            if(!alreadyNotifiedNotifsIdList.contains(iterSchTransferModelList.getSCH_TRNFR_ID())) {
                filteredSchTransfersObjList.add(iterSchTransferModelList);
            }
        }

        return filteredSchTransfersObjList;
    }

    public List<ScheduledTransactionModel> getSchedTransactionsListAfterCancelledNotifsOnDate(List<ScheduledTransactionModel> schedTransactionModelObjList,
                                                                                              String userIdStr, String dateStr) {

        if(schedTransactionModelObjList == null || (schedTransactionModelObjList != null && schedTransactionModelObjList.isEmpty())){
            return schedTransactionModelObjList;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CNCL_NOTIF_EVNT_ID ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(NOTIFICATIONS_TABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '"+userIdStr+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" CNCL_NOTIF_DATE = '"+dateStr+"' ");

        String eventIdStr = "";
        for(ScheduledTransactionModel iterSchTransactionModelList : schedTransactionModelObjList){
            eventIdStr += "'"+iterSchTransactionModelList.getSCH_TRAN_ID()+"', ";
        }

        if(eventIdStr.contains(", ")){
            eventIdStr = eventIdStr.substring(0, eventIdStr.lastIndexOf(", "));
        }

        sqlQuerySB.append(" AND CNCL_NOTIF_EVNT_ID IN ("+eventIdStr+")");

        Log.i(CLASS_NAME, "Query to get all the cancelled notifications on a particular day : " + String.valueOf(sqlQuerySB));

        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        if(cursor.getCount() == 0){
            return schedTransactionModelObjList;
        }

        List<ScheduledTransactionModel> filteredSchTransObjList = new ArrayList<>();
        List<String> alreadyNotifiedNotifsIdList = new ArrayList<>();
        while (cursor.moveToNext()){
            String thisEventIdStr = ColumnFetcher.getInstance().loadString(cursor, "CNCL_NOTIF_EVNT_ID");
            alreadyNotifiedNotifsIdList.add(thisEventIdStr);
        }

        for(ScheduledTransactionModel iterSchTransactionModelList : schedTransactionModelObjList){
            if(!alreadyNotifiedNotifsIdList.contains(iterSchTransactionModelList.getSCH_TRAN_ID())) {
                filteredSchTransObjList.add(iterSchTransactionModelList);
            }
        }

        return filteredSchTransObjList;
    }

    public List<Object> getTransactionsOnDateAndCategory(TransactionModel transObj){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);
        List<Object> transactionList = new ArrayList<>();

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" TRAN.TRAN_NAME, ");
        sqlQuerySB.append(" CAT.CAT_NAME, ");
        sqlQuerySB.append(" ACC.ACC_NAME, ");
        sqlQuerySB.append(" SPNT.SPNT_ON_NAME, ");
        sqlQuerySB.append(" TRAN.TRAN_AMT, ");
        sqlQuerySB.append(" TRAN.TRAN_DATE, ");
        sqlQuerySB.append(" TRAN.TRAN_NOTE, ");
        sqlQuerySB.append(" TRAN.TRAN_ID, ");
        sqlQuerySB.append(" TRAN.TRAN_TYPE, ");
        sqlQuerySB.append(" TRAN.CREAT_DTM, ");
        sqlQuerySB.append(" TRAN.MOD_DTM ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSACTION_TABLE + " TRAN ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(CATEGORY_TABLE + " CAT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CAT.CAT_ID = TRAN.CAT_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(SPENT_ON_TABLE + " SPNT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" SPNT.SPNT_ON_ID = TRAN.SPNT_ON_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE + " ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" ACC.ACC_ID = TRAN.ACC_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" CAT.CAT_NAME = '"+transObj.getCategory()+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.TRAN_DATE = '"+transObj.getTRAN_DATE()+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.TRAN_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.USER_ID = '"+transObj.getUSER_ID()+"'");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" TRAN.MOD_DTM, TRAN.CREAT_DTM ");
        sqlQuerySB.append(" DESC ");

        Log.i(CLASS_NAME, "query to get transactions : "+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            TransactionModel transactionModelObj = new TransactionModel();

            transactionModelObj.setTRAN_NAME(ColumnFetcher.getInstance().loadString(cursor, "TRAN_NAME"));
            transactionModelObj.setCategory(ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME"));
            transactionModelObj.setAccount(ColumnFetcher.getInstance().loadString(cursor, "ACC_NAME"));
            transactionModelObj.setSpentOn(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME"));
            transactionModelObj.setTRAN_AMT(ColumnFetcher.getInstance().loadDouble(cursor, "TRAN_AMT"));
            transactionModelObj.setTRAN_DATE(ColumnFetcher.getInstance().loadString(cursor, "TRAN_DATE"));
            transactionModelObj.setTRAN_NOTE(ColumnFetcher.getInstance().loadString(cursor, "TRAN_NOTE"));
            transactionModelObj.setTRAN_ID(ColumnFetcher.getInstance().loadString(cursor, "TRAN_ID"));
            transactionModelObj.setTRAN_TYPE(ColumnFetcher.getInstance().loadString(cursor, "TRAN_TYPE"));
            transactionModelObj.setCREAT_DTM(ColumnFetcher.getInstance().loadString(cursor, "CREAT_DTM"));
            transactionModelObj.setMOD_DTM(ColumnFetcher.getInstance().loadString(cursor, "MOD_DTM"));

            transactionList.add(transactionModelObj);
        }
        return transactionList;
    }

    public List<Object> getTransfersOnDateAndAccounts(TransferModel trfrObj){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);
        List<Object> transfersList = new ArrayList<>();

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" TRFR.TRNFR_AMT, ");
        sqlQuerySB.append(" TRFR.TRNFR_ID, ");
        sqlQuerySB.append(" TRFR.TRNFR_DATE, ");
        sqlQuerySB.append(" TRFR.MOD_DTM, ");
        sqlQuerySB.append(" TRFR.CREAT_DTM ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSFERS_TABLE + " TRFR ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE + " FRM_ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" FRM_ACC.ACC_ID = TRFR.ACC_ID_FRM ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE + " TO_ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TO_ACC.ACC_ID = TRFR.ACC_ID_TO ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" FRM_ACC.ACC_NAME = '"+trfrObj.getFromAccName()+"'");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TO_ACC.ACC_NAME = '"+trfrObj.getToAccName()+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRFR.TRNFR_DATE = '"+trfrObj.getTRNFR_DATE()+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRFR.TRNFR_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRFR.USER_ID = '"+trfrObj.getUSER_ID()+"'");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" TRFR.MOD_DTM, TRFR.CREAT_DTM ");
        sqlQuerySB.append(" DESC ");

        Log.i(CLASS_NAME, "query to get transfers : "+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            TransferModel transferModelObj = new TransferModel();

            transferModelObj.setFromAccName(trfrObj.getFromAccName());
            transferModelObj.setToAccName(trfrObj.getToAccName());
            transferModelObj.setTRNFR_AMT(ColumnFetcher.getInstance().loadDouble(cursor, "TRNFR_AMT"));
            transferModelObj.setCREAT_DTM(ColumnFetcher.getInstance().loadString(cursor, "CREAT_DTM"));
            transferModelObj.setMOD_DTM(ColumnFetcher.getInstance().loadString(cursor, "MOD_DTM"));
            transferModelObj.setTRNFR_DATE(ColumnFetcher.getInstance().loadString(cursor, "TRNFR_DATE"));
            transferModelObj.setTRNFR_ID(ColumnFetcher.getInstance().loadString(cursor, "TRNFR_ID"));

            transfersList.add(transferModelObj);
        }
        return transfersList;
    }

    //method to get all the budgets_view for the particlar user
    public List<BudgetModel> getAllBudgets(String dateStr, String userId){
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" BUDGET_ID, ");
        sqlQuerySB.append(" BUDGET_NAME, ");
        sqlQuerySB.append(" BUDGET_GRP_ID, ");
        sqlQuerySB.append(" BUDGET_GRP_TYPE, ");
        sqlQuerySB.append(" BUDGET_TYPE, ");
        sqlQuerySB.append(" BUDGET_AMT, ");
        sqlQuerySB.append(" BUDGET_NOTE ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(BUDGET_TABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" BUDGET_IS_DEL = '" + Constants.DB_NONAFFIRMATIVE + "' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" USER_ID = '" + userId + "' ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
        BudgetModel budgetModelObj = null;

        List<BudgetModel> budgetModelList = new ArrayList<BudgetModel>();
        while (cursor.moveToNext()){
            String budIdStr = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_ID");
            String budNmeStr = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_NAME");
            String budGrpId = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_GRP_ID");
            String budGrpTyp = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_GRP_TYPE");
            String budTypeStr = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_TYPE");
            Double budAmt = ColumnFetcher.getInstance().loadDouble(cursor, "BUDGET_AMT");
            String budNoteStr = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_NOTE");

            budgetModelObj = new BudgetModel();
            budgetModelObj.setBUDGET_ID(budIdStr);
            budgetModelObj.setBUDGET_NAME(budNmeStr);
            budgetModelObj.setBUDGET_GRP_ID(budGrpId);
            budgetModelObj.setBUDGET_GRP_TYPE(budGrpTyp);
            budgetModelObj.setBUDGET_TYPE(budTypeStr);
            budgetModelObj.setBUDGET_NOTE(budNoteStr);
            budgetModelObj.setBUDGET_AMT(budAmt);
            budgetModelObj.setMonthExpenseTotal(getTotalExpenseOnBudget(budgetModelObj, dateStr));

            budgetModelList.add(budgetModelObj);
        }

        cursor.close();
        return budgetModelList;
    }

    public Double getTotalExpenseOnBudget(BudgetModel budgetModelObj, String dateStr){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        String groupConnectorStr = "";
        if("CATEGORY".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
            groupConnectorStr =" AND CAT_ID = '"+budgetModelObj.getBUDGET_GRP_ID()+"' ";
        }
        else if("ACCOUNT".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
            groupConnectorStr =" AND ACC_ID = '"+budgetModelObj.getBUDGET_GRP_ID()+"' ";
        }
        if("SPENT ON".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
            groupConnectorStr =" AND SPNT_ON_ID = '"+budgetModelObj.getBUDGET_GRP_ID()+"' ";
        }

        String dateStrArr[] = dateStr.split("-");
        String dateConnectorStr = "";
        if("DAILY".equalsIgnoreCase(budgetModelObj.getBUDGET_TYPE())){
            dateConnectorStr = " AND TRAN_DATE = '"+dateStrArr[0]+"-"+dateStrArr[1]+"-"+dateStrArr[2]+"' ";
        }
        else if("WEEKLY".equalsIgnoreCase(budgetModelObj.getBUDGET_TYPE())){
            List<String> weekDaysList = DateTimeUtil.getInstance().getAllDatesInWeekOnDate(dateStr);

            dateConnectorStr += " AND TRAN_DATE IN (";
            for(String iterDatesList : weekDaysList){
                dateConnectorStr += "'"+iterDatesList+"',";
            }

            if(dateConnectorStr.contains(",")){
                dateConnectorStr = dateConnectorStr.substring(0, dateConnectorStr.lastIndexOf(",")) + ") ";
            }

        }
        else if("MONTHLY".equalsIgnoreCase(budgetModelObj.getBUDGET_TYPE())){
            dateConnectorStr = " AND TRAN_DATE LIKE '%-"+dateStrArr[1]+"-"+dateStrArr[2]+"' ";
        }
        else if("YEARLY".equalsIgnoreCase(budgetModelObj.getBUDGET_TYPE())){
            dateConnectorStr = " AND TRAN_DATE LIKE '%-"+dateStrArr[2]+"' ";
        }

        sqlQuerySB.append(" SELECT ");

        sqlQuerySB.append(" (( ");
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), 0) ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSACTION_TABLE);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'EXPENSE' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN_IS_DEL = 'N' ");
        sqlQuerySB.append(groupConnectorStr);
        sqlQuerySB.append(dateConnectorStr);
        sqlQuerySB.append(" ) ");

        sqlQuerySB.append(" - ");

        sqlQuerySB.append(" ( ");
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), 0) ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSACTION_TABLE);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'INCOME' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN_IS_DEL = 'N' ");
        sqlQuerySB.append(groupConnectorStr);
        sqlQuerySB.append(dateConnectorStr);
        sqlQuerySB.append(" )) ");

        sqlQuerySB.append(" AS TOTAL ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSACTION_TABLE + " TRAN ");

        //sqlQuerySB.append(" WHERE 1=1 ");

        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        if(cursor.moveToNext()){
            return ColumnFetcher.getInstance().loadDouble(cursor, "TOTAL");
        }

        return 0.0;
    }

    public Map<String, MonthLegend> getConsolidatedTransactions(Map<String, MonthLegend> monthLegendMap, String dateStr, String userId){
        String dateStrArr[] = dateStr.split("-");

        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CAT.CAT_NAME, ");
        sqlQuerySB.append(" TRAN.TRAN_AMT, ");
        sqlQuerySB.append(" TRAN.TRAN_TYPE, ");
        sqlQuerySB.append(" TRAN.TRAN_DATE ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSACTION_TABLE + " TRAN ");
        sqlQuerySB.append(" JOIN ");
        sqlQuerySB.append(CATEGORY_TABLE + " CAT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CAT.CAT_ID = TRAN.CAT_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN.TRAN_DATE ");
        sqlQuerySB.append(" LIKE ");
        sqlQuerySB.append(" '%"+dateStrArr[1]+"-"+dateStrArr[2]+"' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.TRAN_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.USER_ID = '"+userId+"' ");

        Log.i(CLASS_NAME, "Query to fetch Transactions : " + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            String catNameStr = ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME");
            Double amt = ColumnFetcher.getInstance().loadDouble(cursor, "TRAN_AMT");
            String tranTypeStr = ColumnFetcher.getInstance().loadString(cursor, "TRAN_TYPE");
            String tempDateStrArr[] = ColumnFetcher.getInstance().loadString(cursor, "TRAN_DATE").split("-");

            if(tempDateStrArr[0].length() == 1){
                tempDateStrArr[0] = "0"+tempDateStrArr[0];
            }

            String tranDateStr = tempDateStrArr[0]+"-"+tempDateStrArr[1]+"-"+tempDateStrArr[2];

            MonthLegend monthLegendObj = null;
            SummaryModel summaryModel = null;
            Map<String, ConsolidatedTransactionModel> consolidatedTransactionModelMap = null;
            ConsolidatedTransactionModel consolidatedTransactionModel = null;

            //if the legend map already contains an entry for this date
            if(monthLegendMap.containsKey(tranDateStr)){
                monthLegendObj = monthLegendMap.get(tranDateStr);
                summaryModel = monthLegendObj.getSummaryModel();
                consolidatedTransactionModelMap = summaryModel.getConsolidatedTransactionModelMap();

                //consolidation of repeated same category transactions
                if(consolidatedTransactionModelMap.containsKey(catNameStr)){
                    consolidatedTransactionModel = consolidatedTransactionModelMap.get(catNameStr);
                    consolidatedTransactionModel.setCount(consolidatedTransactionModel.getCount()+1);

                    if(tranTypeStr.equalsIgnoreCase("EXPENSE")){
                        consolidatedTransactionModel.setTotal(consolidatedTransactionModel.getTotal() - amt);
                    }
                    else if(tranTypeStr.equalsIgnoreCase("INCOME")){
                        consolidatedTransactionModel.setTotal(consolidatedTransactionModel.getTotal() + amt);
                    }
                    else{
                        Log.e(CLASS_NAME, "TRAN_TYPE in db expected to be either 'EXPENSE' or 'INCOME'..but found neither");
                    }
                }
                else{
                    consolidatedTransactionModel = new ConsolidatedTransactionModel();
                    int count = 0;
                    Double total = 0.0;

                    if(tranTypeStr.equalsIgnoreCase("EXPENSE")){
                        total -= amt;
                    }
                    else if(tranTypeStr.equalsIgnoreCase("INCOME")){
                        total += amt;
                    }
                    else{
                        Log.e(CLASS_NAME,"TRAN_TYPE in db expected to be either 'EXPENSE' or 'INCOME'..but found neither");
                    }

                    consolidatedTransactionModel.setCategory(catNameStr);
                    consolidatedTransactionModel.setDate(tranDateStr);
                    consolidatedTransactionModel.setTotal(total);
                    consolidatedTransactionModel.setCount(++count);
                }
            }
            //if the legend map doesnt contains an entry for this date
            else{
                consolidatedTransactionModel = new ConsolidatedTransactionModel();
                monthLegendObj = new MonthLegend();
                summaryModel = new SummaryModel();
                consolidatedTransactionModelMap = new HashMap<String, ConsolidatedTransactionModel>();

                Double total = 0.0;

                if(tranTypeStr.equalsIgnoreCase("EXPENSE")){
                    total -= amt;
                }
                else if(tranTypeStr.equalsIgnoreCase("INCOME")){
                    total += amt;
                }
                else{
                    Log.e(CLASS_NAME,"TRAN_TYPE in db expected to be either 'EXPENSE' or 'INCOME'..but found neither");
                }

                consolidatedTransactionModel.setCategory(catNameStr);
                consolidatedTransactionModel.setDate(tranDateStr);
                consolidatedTransactionModel.setTotal(total);
                consolidatedTransactionModel.setCount(1);
            }
            consolidatedTransactionModelMap.put(catNameStr, consolidatedTransactionModel);
            summaryModel.setConsolidatedTransactionModelMap(consolidatedTransactionModelMap);
            monthLegendObj.setSummaryModel(summaryModel);
            monthLegendMap.put(tranDateStr, monthLegendObj);
        }
        cursor.close();

        return monthLegendMap;
    }

    public Map<String, MonthLegend> getMonthLegendOnDate(String dateStr, String userId){
        Map<String, MonthLegend> monthLegendMap = new HashMap<>();

        //get consolidated transactions for the monh
        monthLegendMap = getConsolidatedTransactions(monthLegendMap, dateStr, userId);

        //get all the transfers for the month
        monthLegendMap = getConsolidatedTransfers(monthLegendMap, dateStr, userId);

        //get the all the scheduled transaction (daily, weekly, monthly & yearly) scheduled to be happening in this month
        monthLegendMap = getScheduledTransactions(monthLegendMap, dateStr, userId);

        //get the all the scheduled transfers (daily, weekly, monthly & yearly) scheduled to be happening in this month
        monthLegendMap = getScheduledTransfers(monthLegendMap, dateStr, userId);

        return monthLegendMap;
    }

    private Map<String, MonthLegend> getConsolidatedTransfers(Map<String, MonthLegend> monthLegendMap, String dateStr, String userId) {
        String dateStrArr[] = dateStr.split("-");

        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" FRM_ACC.ACC_NAME AS ACC_FROM, ");
        sqlQuerySB.append(" TO_ACC.ACC_NAME AS ACC_TO, ");
        sqlQuerySB.append(" TRFR.TRNFR_AMT, ");
        sqlQuerySB.append(" TRFR.TRNFR_DATE ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSFERS_TABLE + " TRFR ");

        sqlQuerySB.append(" JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE + " FRM_ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" FRM_ACC.ACC_ID = TRFR.ACC_ID_FRM ");

        sqlQuerySB.append(" JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE + " TO_ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TO_ACC.ACC_ID = TRFR.ACC_ID_TO ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRFR.TRNFR_DATE ");
        sqlQuerySB.append(" LIKE ");
        sqlQuerySB.append(" '%"+dateStrArr[1]+"-"+dateStrArr[2]+"' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRFR.TRNFR_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRFR.USER_ID = '"+userId+"' ");

        Log.i(CLASS_NAME, "Query to fetch Transfers : " + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            String fromAccStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_FROM");
            String toAccStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_TO");
            Double amt = ColumnFetcher.getInstance().loadDouble(cursor, "TRNFR_AMT");
            String tempDateStrArr[] = ColumnFetcher.getInstance().loadString(cursor, "TRNFR_DATE").split("-");

            String transferCombinationStr = fromAccStr+"-"+toAccStr;

            if(tempDateStrArr[0].length() == 1){
                tempDateStrArr[0] = "0"+tempDateStrArr[0];
            }

            String tranDateStr = tempDateStrArr[0]+"-"+tempDateStrArr[1]+"-"+tempDateStrArr[2];

            MonthLegend monthLegendObj = null;
            SummaryModel summaryModel = null;
            Map<String, ConsolidatedTransferModel> consolidatedTransferModelMap = null;
            ConsolidatedTransferModel consolidatedTransferModel = null;

            //if the legend map already contains an entry for this date
            if(monthLegendMap.containsKey(tranDateStr)){
                monthLegendObj = monthLegendMap.get(tranDateStr);
                summaryModel = monthLegendObj.getSummaryModel();
                consolidatedTransferModelMap = summaryModel.getConsolidatedTransferModelMap();

                //consolidation of repeated same transfers
                if(consolidatedTransferModelMap.containsKey(transferCombinationStr)){
                    consolidatedTransferModel = consolidatedTransferModelMap.get(transferCombinationStr);
                    consolidatedTransferModel.setCount(consolidatedTransferModel.getCount() + 1);
                    consolidatedTransferModel.setAmount(consolidatedTransferModel.getAmount() + amt);
                }
                else{
                    consolidatedTransferModel = new ConsolidatedTransferModel();
                    consolidatedTransferModel.setFromAccountStr(fromAccStr);
                    consolidatedTransferModel.setToAccountStr(toAccStr);
                    consolidatedTransferModel.setAmount(amt);
                    consolidatedTransferModel.setCount(1);
                    consolidatedTransferModel.setDateStr(tranDateStr);
                }
            }
            //if the legend map doesnt contains an entry for this date
            else{
                consolidatedTransferModel = new ConsolidatedTransferModel();
                monthLegendObj = new MonthLegend();
                summaryModel = new SummaryModel();
                consolidatedTransferModelMap = new HashMap<>();

                consolidatedTransferModel.setFromAccountStr(fromAccStr);
                consolidatedTransferModel.setToAccountStr(toAccStr);
                consolidatedTransferModel.setAmount(amt);
                consolidatedTransferModel.setCount(1);
                consolidatedTransferModel.setDateStr(tranDateStr);
            }
            consolidatedTransferModelMap.put(transferCombinationStr, consolidatedTransferModel);
            summaryModel.setConsolidatedTransferModelMap(consolidatedTransferModelMap);
            monthLegendObj.setSummaryModel(summaryModel);
            monthLegendMap.put(tranDateStr, monthLegendObj);
        }
        cursor.close();

        return monthLegendMap;
    }

    public Map<String, MonthLegend> getScheduledTransfers(Map<String, MonthLegend> monthLegendMap, String dateStr, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SCH_TRNFR_ID, ");
        sqlQuerySB.append(" SCH_TRNFR_DATE, ");
        sqlQuerySB.append(" SCH_TRNFR_FREQ, ");
        sqlQuerySB.append(" SCH_TRNFR_AUTO, ");
        sqlQuerySB.append(" SCH_TRNFR_AMT, ");
        sqlQuerySB.append(" SCH_TRNFR_ACC_ID_FRM, ");
        sqlQuerySB.append(" SCH_TRNFR_ACC_ID_TO, ");
        sqlQuerySB.append(" SCH_TRNFR_NOTE, ");
        sqlQuerySB.append(" ACC_FRM.ACC_NAME AS FRM_ACC, ");
        sqlQuerySB.append(" ACC_TO.ACC_NAME AS TO_ACC, ");
        sqlQuerySB.append(" SCH.CREAT_DTM, ");
        sqlQuerySB.append(" SCH.MOD_DTM ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(SCHEDULED_TRANSFER_TABLE+ " SCH ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE+" ACC_FRM ");
        sqlQuerySB.append(" ON ACC_FRM.ACC_ID = SCH.SCH_TRNFR_ACC_ID_FRM ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE+" ACC_TO ");
        sqlQuerySB.append(" ON ACC_TO.ACC_ID = SCH.SCH_TRNFR_ACC_ID_TO ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" SCH_TRNFR_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" SCH.USER_ID = '"+userId+"' ");

        Log.i(CLASS_NAME, "Query to fetch Scheduled Transfers : " + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        String dateStrArr[] = dateStr.split("-");
        int lastDayOfMonth = DateTimeUtil.getInstance().getLastDayOfTheMonth(dateStr);
        List<ScheduledTransferModel> scheduledTransferModelList;
        MonthLegend tempMonthLegend;
        ScheduledTransferModel scheduledTransferModelObj;

        while (cursor.moveToNext()){
            String schTransferIdStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_ID");
            String schTransfersDateStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_DATE");
            String schTransferFreqStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_FREQ");
            String schTransferAutoStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_AUTO");
            Double schTransferAmt = ColumnFetcher.getInstance().loadDouble(cursor, "SCH_TRNFR_AMT");
            String schTransferFrmAccIdStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_ACC_ID_FRM");
            String schTransferToAccIdStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_ACC_ID_TO");
            String schTransferFrmAccStr = ColumnFetcher.getInstance().loadString(cursor, "FRM_ACC");
            String schTransferToAccStr = ColumnFetcher.getInstance().loadString(cursor, "TO_ACC");
            String schTransferCreateDtmStr = ColumnFetcher.getInstance().loadString(cursor, "CREAT_DTM");
            String schTransferModDtmStr = ColumnFetcher.getInstance().loadString(cursor, "MOD_DTM");
            String schTransferNoteStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_NOTE");

            scheduledTransferModelObj = new ScheduledTransferModel();
            scheduledTransferModelObj.setSCH_TRNFR_ID(schTransferIdStr);
            scheduledTransferModelObj.setSCH_TRNFR_DATE(schTransfersDateStr);
            scheduledTransferModelObj.setSCH_TRNFR_FREQ(schTransferFreqStr);
            scheduledTransferModelObj.setSCH_TRNFR_AUTO(schTransferAutoStr);
            scheduledTransferModelObj.setSCH_TRNFR_AMT(schTransferAmt);
            scheduledTransferModelObj.setSCH_TRNFR_ACC_ID_FRM(schTransferFrmAccIdStr);
            scheduledTransferModelObj.setSCH_TRNFR_ACC_ID_TO(schTransferToAccIdStr);
            scheduledTransferModelObj.setFromAccountStr(schTransferFrmAccStr);
            scheduledTransferModelObj.setToAccountStr(schTransferToAccStr);
            scheduledTransferModelObj.setSCH_TRNFR_NOTE(schTransferNoteStr);
            scheduledTransferModelObj.setCREAT_DTM(schTransferCreateDtmStr);
            scheduledTransferModelObj.setMOD_DTM(schTransferModDtmStr);

            String schTransfersDateStrArr[] = schTransfersDateStr.split("-");

            for(int i=1; i<=lastDayOfMonth; i++){
                //this flag allows us whether to proceed with adding this date into month legend as a scheduled transfer
                boolean isScheduledTransfer = false;

                String dayStr = String.valueOf(i);
                if(i<10){
                    dayStr = "0" + dayStr;
                }

                String thisDateStr = dayStr+"-"+dateStrArr[1]+"-"+dateStrArr[2];

                //to get only those scheduled transactions which are after the SCH_TRAN_DATE
                if(schTransfersDateStrArr[1].equals(dateStrArr[1]) && schTransfersDateStrArr[2].equals(dateStrArr[2])
                        && Integer.parseInt(dayStr) < Integer.parseInt(schTransfersDateStrArr[0])){
                    continue;
                }

                //to avoid showing schedule transaction before todays date
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date thisIterDate = null;
                Date todaysDate = null;

                try{
                    thisIterDate = sdf.parse(thisDateStr);
                    todaysDate = sdf.parse(sdf.format(new Date()));

                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "ERROR !!"+e);
                    return monthLegendMap;
                }
                if(thisIterDate.compareTo(todaysDate) < 0){
                    continue;
                }

                if("ONCE".equalsIgnoreCase(schTransferFreqStr)){
                    //to check if sch tran date & month & year (01-11-2014,02-11-2015,03-11-2016...) is same as this date's day (01-11-2014,02-11-2015,03-11-2016...)
                    if(dayStr.equalsIgnoreCase(schTransfersDateStrArr[0]) && dateStrArr[1].equalsIgnoreCase(schTransfersDateStrArr[1])
                            && dateStrArr[2].equalsIgnoreCase(schTransfersDateStrArr[2])){
                        isScheduledTransfer = true;
                    }
                }
                //for daily transfers
                else if("DAILY".equalsIgnoreCase(schTransferFreqStr)){
                    isScheduledTransfer = true;
                }
                //for weekly transfers
                else if("WEEKLY".equalsIgnoreCase(schTransferFreqStr)){
                    //to check if the day(sun, mon) of the schedule transfer date is same as thisDateStr's day(sun, mon ..)
                    String schTranDayOfWeekStr = DateTimeUtil.getInstance().getDayOfWeekFromDate(schTransfersDateStr);
                    String thisDateDayOfWeeksStr = DateTimeUtil.getInstance().getDayOfWeekFromDate(thisDateStr);

                    if(schTranDayOfWeekStr.equalsIgnoreCase(thisDateDayOfWeeksStr)){
                        isScheduledTransfer = true;
                    }
                }
                //for monthly transfers
                else if("MONTHLY".equalsIgnoreCase(schTransferFreqStr)){
                    //to check if sch tran date (01,02,03...) is same as this date's day (01,02,03...)..here we're neglecting the month and year of both the dates
                    if(dayStr.equalsIgnoreCase(schTransfersDateStrArr[0])){
                        isScheduledTransfer = true;
                    }
                }

                //for yearly transfers
                else if("YEARLY".equalsIgnoreCase(schTransferFreqStr)){
                    //to check if sch tran date & month (01-11,02-11,03-11...) is same as this date's day (01-11,02-11,03-11...)..here we're neglecting only the year
                    if(dayStr.equalsIgnoreCase(schTransfersDateStrArr[0]) && dateStrArr[1].equalsIgnoreCase(schTransfersDateStrArr[1])){
                        isScheduledTransfer = true;
                    }
                }

                if(!isScheduledTransfer){
                    continue;
                }

                if(monthLegendMap.containsKey(thisDateStr)){
                    tempMonthLegend = monthLegendMap.get(thisDateStr);
                    scheduledTransferModelList = tempMonthLegend.getScheduledTransferModelList();

                    if(scheduledTransferModelList == null){
                        scheduledTransferModelList = new ArrayList<>();
                    }
                }
                else{
                    tempMonthLegend = new MonthLegend();
                    scheduledTransferModelList = new ArrayList<>();
                }

                tempMonthLegend.setHasScheduledTransfer(true);
                scheduledTransferModelList.add(scheduledTransferModelObj);
                tempMonthLegend.setScheduledTransferModelList(scheduledTransferModelList);

                monthLegendMap.put(thisDateStr, tempMonthLegend);
            }
        }
        return monthLegendMap;
    }

    public Map<String, MonthLegend> getScheduledTransactions(Map<String, MonthLegend> monthLegendMap, String dateStr, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SCH_TRAN_ID, ");
        sqlQuerySB.append(" SCH_TRAN_NAME, ");
        sqlQuerySB.append(" SCH_TRAN_CAT_ID, ");
        sqlQuerySB.append(" SCH_TRAN_SPNT_ON_ID, ");
        sqlQuerySB.append(" SCH_TRAN_ACC_ID, ");
        sqlQuerySB.append(" SCH_TRAN_DATE, ");
        sqlQuerySB.append(" SCH_TRAN_FREQ, ");
        sqlQuerySB.append(" SCH_TRAN_AUTO, ");
        sqlQuerySB.append(" SCH_TRAN_TYPE, ");
        sqlQuerySB.append(" SCH_TRAN_NOTE, ");
        sqlQuerySB.append(" SCH_TRAN_AMT, ");
        sqlQuerySB.append(" CAT.CAT_NAME, ");
        sqlQuerySB.append(" ACC.ACC_NAME, ");
        sqlQuerySB.append(" SPNT.SPNT_ON_NAME, ");
        sqlQuerySB.append(" SCH.CREAT_DTM, ");
        sqlQuerySB.append(" SCH.MOD_DTM ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE+ " SCH ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(Constants.DB_TABLE_CATEGORYTABLE+" CAT ");
        sqlQuerySB.append(" ON CAT.CAT_ID = SCH.SCH_TRAN_CAT_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(Constants.DB_TABLE_ACCOUNTTABLE+" ACC ");
        sqlQuerySB.append(" ON ACC.ACC_ID = SCH.SCH_TRAN_ACC_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(Constants.DB_TABLE_SPENTONTABLE+" SPNT ");
        sqlQuerySB.append(" ON SPNT.SPNT_ON_ID = SCH.SCH_TRAN_SPNT_ON_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" SCH_TRAN_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" SCH.USER_ID = '"+userId+"' ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        String dateStrArr[] = dateStr.split("-");
        int lastDayOfMonth = DateTimeUtil.getInstance().getLastDayOfTheMonth(dateStr);
        List<ScheduledTransactionModel> scheduledTransactionModelList;
        MonthLegend tempMonthLegend;
        ScheduledTransactionModel scheduledTransactionModelObj;

        while (cursor.moveToNext()){
            String schTranIdStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_ID");
            String schTranNameStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_NAME");
            String schTranCatIdStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_CAT_ID");
            String schTranSpntOnIdStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_SPNT_ON_ID");
            String schTranSpntOnNameStr = ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME");
            String schTranAccIdStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_ACC_ID");
            String schTranAccNameStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_NAME");
            String schTransactionDateStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_DATE");
            String schTranFreqStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_FREQ");
            String schTranAutoStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_AUTO");
            String schTranCatStr = ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME");
            Double schTranAmt = ColumnFetcher.getInstance().loadDouble(cursor, "SCH_TRAN_AMT");
            String schTranTypeStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_TYPE");
            String schTranCreateDtmStr = ColumnFetcher.getInstance().loadString(cursor, "CREAT_DTM");
            String schTranModDtmStr = ColumnFetcher.getInstance().loadString(cursor, "MOD_DTM");
            String schTranNoteStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_NOTE");

            scheduledTransactionModelObj = new ScheduledTransactionModel();
            scheduledTransactionModelObj.setSCH_TRAN_ID(schTranIdStr);
            scheduledTransactionModelObj.setSCH_TRAN_NAME(schTranNameStr);
            scheduledTransactionModelObj.setSCH_TRAN_CAT_ID(schTranCatIdStr);
            scheduledTransactionModelObj.setSCH_TRAN_SPNT_ON_ID(schTranSpntOnIdStr);
            scheduledTransactionModelObj.setSpentOnNameStr(schTranSpntOnNameStr);
            scheduledTransactionModelObj.setSCH_TRAN_ACC_ID(schTranAccIdStr);
            scheduledTransactionModelObj.setAccountNameStr(schTranAccNameStr);
            scheduledTransactionModelObj.setSCH_TRAN_DATE(schTransactionDateStr);
            scheduledTransactionModelObj.setSCH_TRAN_FREQ(schTranFreqStr);
            scheduledTransactionModelObj.setSCH_TRAN_AUTO(schTranAutoStr);
            scheduledTransactionModelObj.setCategoryNameStr(schTranCatStr);
            scheduledTransactionModelObj.setSCH_TRAN_AMT(schTranAmt);
            scheduledTransactionModelObj.setSCH_TRAN_TYPE(schTranTypeStr);
            scheduledTransactionModelObj.setCREAT_DTM(schTranCreateDtmStr);
            scheduledTransactionModelObj.setMOD_DTM(schTranModDtmStr);
            scheduledTransactionModelObj.setSCH_TRAN_NOTE(schTranNoteStr);

            String schTransactionDateStrArr[] = schTransactionDateStr.split("-");

            for(int i=1; i<=lastDayOfMonth; i++){
                //this flag allows us whether to proceed with adding this date into month legend as a scheduled transaction
                boolean isScheduledTransaction = false;

                String dayStr = String.valueOf(i);
                if(i<10){
                    dayStr = "0" + dayStr;
                }

                String thisDateStr = dayStr+"-"+dateStrArr[1]+"-"+dateStrArr[2];

                //to get only those scheduled transactions which are after the SCH_TRAN_DATE
                if(schTransactionDateStrArr[1].equals(dateStrArr[1]) && schTransactionDateStrArr[2].equals(dateStrArr[2])
                        && Integer.parseInt(dayStr) < Integer.parseInt(schTransactionDateStrArr[0])){
                    continue;
                }

                //to avoid showing schedule transaction before todays date
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date thisIterDate = null;
                Date todaysDate = null;

                try{
                    thisIterDate = sdf.parse(thisDateStr);
                    todaysDate = sdf.parse(sdf.format(new Date()));

                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "ERROR !!"+e);
                    return monthLegendMap;
                }
                if(thisIterDate.compareTo(todaysDate) < 0){
                    continue;
                }

                //For a Scheduled transaction which is scheduled to happen only once
                if("ONCE".equalsIgnoreCase(schTranFreqStr)){
                    //to check if sch tran date & month & year (01-11-2014,02-11-2015,03-11-2016...) is same as this date's day (01-11-2014,02-11-2015,03-11-2016...)
                    if(dayStr.equalsIgnoreCase(schTransactionDateStrArr[0]) && dateStrArr[1].equalsIgnoreCase(schTransactionDateStrArr[1])
                            && dateStrArr[2].equalsIgnoreCase(schTransactionDateStrArr[2])){
                        isScheduledTransaction = true;
                    }
                }
                //for daily transactions
                else if("DAILY".equalsIgnoreCase(schTranFreqStr)){
                    isScheduledTransaction = true;
                }
                //for weekly transactions
                else if("WEEKLY".equalsIgnoreCase(schTranFreqStr)){
                    //to check if the day(sun, mon) of the schedule transaction date is same as thisDateStr's day(sun, mon ..)
                    String schTranDayOfWeekStr = DateTimeUtil.getInstance().getDayOfWeekFromDate(schTransactionDateStr);
                    String thisDateDayOfWeeksStr = DateTimeUtil.getInstance().getDayOfWeekFromDate(thisDateStr);

                    if(schTranDayOfWeekStr.equalsIgnoreCase(thisDateDayOfWeeksStr)){
                        isScheduledTransaction = true;
                    }
                }
                //for monthly transactions
                else if("MONTHLY".equalsIgnoreCase(schTranFreqStr)){
                    //to check if sch tran date (01,02,03...) is same as this date's day (01,02,03...)..here we're neglecting the month and year of both the dates
                    if(dayStr.equalsIgnoreCase(schTransactionDateStrArr[0])){
                        isScheduledTransaction = true;
                    }
                }

                //for yearly transactions
                else if("YEARLY".equalsIgnoreCase(schTranFreqStr)){
                    //to check if sch tran date & month (01-11,02-11,03-11...) is same as this date's day (01-11,02-11,03-11...)..here we're neglecting only the year
                    if(dayStr.equalsIgnoreCase(schTransactionDateStrArr[0]) && dateStrArr[1].equalsIgnoreCase(schTransactionDateStrArr[1])){
                        isScheduledTransaction = true;
                    }
                }

                if(!isScheduledTransaction){
                    continue;
                }

                if(monthLegendMap.containsKey(thisDateStr)){
                    tempMonthLegend = monthLegendMap.get(thisDateStr);
                    scheduledTransactionModelList = tempMonthLegend.getScheduledTransactionModelList();

                    if(scheduledTransactionModelList == null){
                        scheduledTransactionModelList = new ArrayList<>();
                    }
                }
                else{
                    tempMonthLegend = new MonthLegend();
                    scheduledTransactionModelList = new ArrayList<>();
                }

                tempMonthLegend.setHasScheduledTransaction(true);
                scheduledTransactionModelList.add(scheduledTransactionModelObj);
                tempMonthLegend.setScheduledTransactionModelList(scheduledTransactionModelList);

                monthLegendMap.put(thisDateStr, tempMonthLegend);
            }

        }
        return monthLegendMap;
    }

    //---------------------method to get all accounts--------------------------//
    public List<AccountsModel> getAllAccounts(String userId){
        List<AccountsModel> accountsList = new ArrayList<AccountsModel>();
        SQLiteDatabase db = this.getWritableDatabase();

        if(db == null){
            Log.e(CLASS_NAME, "SQLiteDatabase object is null");
            return null;
        }

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" ACC.ACC_NAME, ");
        sqlQuerySB.append(" ACC.ACC_ID, ");
        sqlQuerySB.append(" ACC.ACC_IS_DEFAULT, ");

        sqlQuerySB.append(" (( SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSACTION_TABLE);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'INCOME' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_ID = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN_IS_DEL ");
        sqlQuerySB.append(" = ");
        sqlQuerySB.append(" '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId.trim().toUpperCase()+"','"+Constants.ADMIN_USERID.trim().toUpperCase()+"')) ");

        sqlQuerySB.append(" + ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSFERS_TABLE);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID_TO = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRNFR_IS_DEL ");
        sqlQuerySB.append(" = ");
        sqlQuerySB.append(" '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId.trim().toUpperCase()+"','"+Constants.ADMIN_USERID.trim().toUpperCase()+"') ))");

        sqlQuerySB.append(" - ");

        sqlQuerySB.append(" ((SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSACTION_TABLE);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'EXPENSE' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_ID = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN_IS_DEL ");
        sqlQuerySB.append(" = ");
        sqlQuerySB.append(" '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId.trim().toUpperCase()+"','"+Constants.ADMIN_USERID.trim().toUpperCase()+"')) ");

        sqlQuerySB.append(" + ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSFERS_TABLE);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID_FRM = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRNFR_IS_DEL ");
        sqlQuerySB.append(" = ");
        sqlQuerySB.append(" '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId.trim().toUpperCase()+"','"+Constants.ADMIN_USERID.trim().toUpperCase()+"') ))");

        sqlQuerySB.append(" AS ACC_TOTAL ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(ACCOUNT_TABLE+" ACC ");

        sqlQuerySB.append(" LEFT JOIN ");
        sqlQuerySB.append(TRANSACTION_TABLE+" TRAN");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TRAN.ACC_ID = ACC.ACC_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId.trim().toUpperCase()+"','"+Constants.ADMIN_USERID.trim().toUpperCase()+"') ");

        sqlQuerySB.append(" GROUP BY ACC.ACC_ID ");
        sqlQuerySB.append(" ORDER BY ACC.CREAT_DTM ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        String accountIdStr, accountNameStr, currecyStr, accountIsDefaultStr;
        Double accountTotal;
        AccountsModel accountsModel = null;
        while (cursor.moveToNext()){
            accountIdStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_ID");
            accountNameStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_NAME");
            accountTotal = ColumnFetcher.getInstance().loadDouble(cursor, "ACC_TOTAL");
            currecyStr = ColumnFetcher.getInstance().loadString(cursor, "CUR_NAME");
            accountIsDefaultStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_IS_DEFAULT");

            accountsModel = new AccountsModel();
            accountsModel.setACC_ID(accountIdStr);
            accountsModel.setACC_NAME(accountNameStr);
            accountsModel.setACC_TOTAL(accountTotal);
            accountsModel.setCurrency(currecyStr);
            accountsModel.setACC_IS_DEFAULT(accountIsDefaultStr);
            accountsList.add(accountsModel);
        }
        cursor.close();

        return accountsList;
    }
    //--------------------- end of method to get all accounts--------------------------//

	@Override
	public void onCreate(SQLiteDatabase db) {

    }

	// get class instance
	public static CalendarDbService getInstance(Context context){
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null){
			sInstance = new CalendarDbService(context.getApplicationContext());
		}
		return sInstance;
	}

	// constructors
	public CalendarDbService(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}
}
