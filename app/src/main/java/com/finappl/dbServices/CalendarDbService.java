package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.AccountsMO;
import com.finappl.models.BudgetModel;
import com.finappl.models.CategoryMO;
import com.finappl.models.ConsolidatedTransactionModel;
import com.finappl.models.ConsolidatedTransferModel;
import com.finappl.models.MonthLegend;
import com.finappl.models.RepeatMO;
import com.finappl.models.ScheduledTransactionModel;
import com.finappl.models.ScheduledTransferModel;
import com.finappl.models.SpentOnMO;
import com.finappl.models.ActivitiesMO;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.DateTimeUtil;
import com.finappl.utils.IdGenerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.DB_DATE_FORMAT;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_NONAFFIRMATIVE;
import static com.finappl.utils.Constants.DB_TABLE_ACCOUNT;
import static com.finappl.utils.Constants.DB_TABLE_BUDGET;
import static com.finappl.utils.Constants.DB_TABLE_CATEGORY;
import static com.finappl.utils.Constants.DB_TABLE_NOTIFICATION;
import static com.finappl.utils.Constants.DB_TABLE_REPEAT;
import static com.finappl.utils.Constants.DB_TABLE_SPENTON;
import static com.finappl.utils.Constants.DB_TABLE_TRANSACTION;
import static com.finappl.utils.Constants.DB_TABLE_TRANSFER;
import static com.finappl.utils.Constants.DB_VERSION;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT;
import static com.finappl.utils.Constants.MONTHS_RANGE;


public class CalendarDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();

	private static CalendarDbService sInstance = null;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

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
        sqlQuerySB.append(DB_TABLE_TRANSACTION + " TRAN ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_CATEGORY + " CAT ");
        sqlQuerySB.append(" ON CAT.CAT_ID = TRAN.CAT_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_IS_DEL = '"+DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.ACC_ID = '" + accountIdStr + "' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN_DATE = ");
        sqlQuerySB.append(" (SELECT MAX(TRAN_DATE) FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION + ")");

        Log.i(CLASS_NAME, "Query to fetch Last Transaction made using the Account ID("+accountIdStr+") : "+sqlQuerySB);
        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        TransactionModel transactionModelObj = null;
        if(cursor.moveToNext()){
            transactionModelObj = new TransactionModel();
            transactionModelObj.setTRAN_ID(ColumnFetcher.loadString(cursor, "TRAN_ID"));
            transactionModelObj.setCategory(ColumnFetcher.loadString(cursor, "CAT_NAME"));
            transactionModelObj.setTRAN_AMT(ColumnFetcher.loadDouble(cursor, "TRAN_AMT"));
            transactionModelObj.setTRAN_TYPE(ColumnFetcher.loadString(cursor, "TRAN_TYPE"));
            transactionModelObj.setTRAN_DATE(ColumnFetcher.loadDate(cursor, "TRAN_DATE"));

            return transactionModelObj;
        }
        db.close();
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
        sqlQuerySB.append(DB_TABLE_TRANSFER + " TRNFR ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " TRNFR_FROM ");
        sqlQuerySB.append(" ON TRNFR_FROM.ACC_ID = TRNFR.ACC_ID_FRM ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " TRNFR_TO ");
        sqlQuerySB.append(" ON TRNFR_TO.ACC_ID = TRNFR.ACC_ID_TO ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRNFR_IS_DEL = '"+DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" (TRNFR_FROM.ACC_ID = '"+accountIdStr+"' ");
        sqlQuerySB.append(" OR ");
        sqlQuerySB.append(" TRNFR_TO.ACC_ID = '"+accountIdStr+"' )");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRNFR_DATE = ");
        sqlQuerySB.append(" (SELECT MAX(TRNFR_DATE) FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER + ")");

                Log.i(CLASS_NAME, "Query to fetch Last Transfer made using the Account ID(" + accountIdStr + ") : " + sqlQuerySB);
        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        TransferModel transferModelObj = null;
        if(cursor.moveToNext()){
            transferModelObj = new TransferModel();
            transferModelObj.setTRNFR_ID(ColumnFetcher.loadString(cursor, "TRNFR_ID"));
            transferModelObj.setFromAccName(ColumnFetcher.loadString(cursor, "FROM_ACC"));
            transferModelObj.setToAccName(ColumnFetcher.loadString(cursor, "TO_ACC"));
            transferModelObj.setTRNFR_AMT(ColumnFetcher.loadDouble(cursor, "TRNFR_AMT"));
            transferModelObj.setTRNFR_DATE(ColumnFetcher.loadDate(cursor, "TRNFR_DATE"));

            return transferModelObj;
        }
        db.close();
        return null;
    }

    public List<ScheduledTransferModel> getSchedTransfersListAfterCancelAddDelNotifsOnDate(List<ScheduledTransferModel> scheduledTransferModelObjList,
                                                                                        String userIdStr, String todayStr) {
        if(scheduledTransferModelObjList == null || (scheduledTransferModelObjList != null && scheduledTransferModelObjList.isEmpty())){
            return scheduledTransferModelObjList;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CNCL_NOTIF_EVNT_ID ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_NOTIFICATION);

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
            String thisEventIdStr = ColumnFetcher.loadString(cursor, "CNCL_NOTIF_EVNT_ID");
            alreadyNotifiedNotifsIdList.add(thisEventIdStr);
        }

        for(ScheduledTransferModel iterSchTransferModelList : scheduledTransferModelObjList){
            if(!alreadyNotifiedNotifsIdList.contains(iterSchTransferModelList.getSCH_TRNFR_ID())) {
                filteredSchTransfersObjList.add(iterSchTransferModelList);
            }
        }
        db.close();
        return filteredSchTransfersObjList;
    }

    public List<ScheduledTransactionModel> getSchedTransactionsListAfterCancelAddDelNotifsOnDate(List<ScheduledTransactionModel> schedTransactionModelObjList,
                                                                                              String userIdStr, String dateStr) {

        if(schedTransactionModelObjList == null || (schedTransactionModelObjList != null && schedTransactionModelObjList.isEmpty())){
            return schedTransactionModelObjList;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CNCL_NOTIF_EVNT_ID ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_NOTIFICATION);

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
            String thisEventIdStr = ColumnFetcher.loadString(cursor, "CNCL_NOTIF_EVNT_ID");
            alreadyNotifiedNotifsIdList.add(thisEventIdStr);
        }

        for(ScheduledTransactionModel iterSchTransactionModelList : schedTransactionModelObjList){
            if(!alreadyNotifiedNotifsIdList.contains(iterSchTransactionModelList.getSCH_TRAN_ID())) {
                filteredSchTransObjList.add(iterSchTransactionModelList);
            }
        }
        db.close();
        return filteredSchTransObjList;
    }

    public List<Object> getTransactionsOnDateAndCategory(TransactionModel transObj){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);
        List<Object> transactionList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(DB_DATE_FORMAT);

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
        sqlQuerySB.append(DB_TABLE_TRANSACTION + " TRAN ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_CATEGORY + " CAT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CAT.CAT_ID = TRAN.CAT_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_SPENTON + " SPNT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" SPNT.SPNT_ON_ID = TRAN.SPNT_ON_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" ACC.ACC_ID = TRAN.ACC_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" CAT.CAT_NAME = '"+transObj.getCategory()+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.TRAN_DATE = '"+sdf.format(transObj.getTRAN_DATE())+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.USER_ID = '"+transObj.getUSER_ID()+"'");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" TRAN.MOD_DTM, TRAN.CREAT_DTM ");
        sqlQuerySB.append(" DESC ");

        Log.i(CLASS_NAME, "query to get transactions : "+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            TransactionModel transactionModelObj = new TransactionModel();

            transactionModelObj.setTRAN_NAME(ColumnFetcher.loadString(cursor, "TRAN_NAME"));
            transactionModelObj.setCategory(ColumnFetcher.loadString(cursor, "CAT_NAME"));
            transactionModelObj.setAccount(ColumnFetcher.loadString(cursor, "ACC_NAME"));
            transactionModelObj.setSpentOn(ColumnFetcher.loadString(cursor, "SPNT_ON_NAME"));
            transactionModelObj.setTRAN_AMT(ColumnFetcher.loadDouble(cursor, "TRAN_AMT"));
            transactionModelObj.setTRAN_DATE(ColumnFetcher.loadDate(cursor, "TRAN_DATE"));
            transactionModelObj.setTRAN_NOTE(ColumnFetcher.loadString(cursor, "TRAN_NOTE"));
            transactionModelObj.setTRAN_ID(ColumnFetcher.loadString(cursor, "TRAN_ID"));
            transactionModelObj.setTRAN_TYPE(ColumnFetcher.loadString(cursor, "TRAN_TYPE"));
            transactionModelObj.setCREAT_DTM(ColumnFetcher.loadDateTime(cursor, "CREAT_DTM"));
            transactionModelObj.setMOD_DTM(ColumnFetcher.loadDateTime(cursor, "MOD_DTM"));

            transactionList.add(transactionModelObj);
        }
        db.close();
        return transactionList;
    }

    public List<Object> getTransfersOnDateAndAccounts(TransferModel trfrObj){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);
        List<Object> transfersList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(DB_DATE_FORMAT);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" TRFR.TRNFR_AMT, ");
        sqlQuerySB.append(" TRFR.TRNFR_ID, ");
        sqlQuerySB.append(" TRFR.TRNFR_DATE, ");
        sqlQuerySB.append(" TRFR.MOD_DTM, ");
        sqlQuerySB.append(" TRFR.CREAT_DTM ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER + " TRFR ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " FRM_ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" FRM_ACC.ACC_ID = TRFR.ACC_ID_FRM ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " TO_ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TO_ACC.ACC_ID = TRFR.ACC_ID_TO ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" FRM_ACC.ACC_NAME = '"+trfrObj.getFromAccName()+"'");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TO_ACC.ACC_NAME = '"+trfrObj.getToAccName()+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRFR.TRNFR_DATE = '"+sdf.format(trfrObj.getTRNFR_DATE())+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRFR.TRNFR_IS_DEL = '"+DB_NONAFFIRMATIVE+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRFR.USER_ID = '"+trfrObj.getUSER_ID()+"'");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" TRFR.MOD_DTM, TRFR.CREAT_DTM ");
        sqlQuerySB.append(" DESC ");

        Log.i(CLASS_NAME, "query to get transfers : " + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            TransferModel transferModelObj = new TransferModel();

            transferModelObj.setFromAccName(trfrObj.getFromAccName());
            transferModelObj.setToAccName(trfrObj.getToAccName());
            transferModelObj.setTRNFR_AMT(ColumnFetcher.loadDouble(cursor, "TRNFR_AMT"));
            transferModelObj.setCREAT_DTM(ColumnFetcher.loadDateTime(cursor, "CREAT_DTM"));
            transferModelObj.setMOD_DTM(ColumnFetcher.loadDateTime(cursor, "MOD_DTM"));
            transferModelObj.setTRNFR_DATE(ColumnFetcher.loadDate(cursor, "TRNFR_DATE"));
            transferModelObj.setTRNFR_ID(ColumnFetcher.loadString(cursor, "TRNFR_ID"));

            transfersList.add(transferModelObj);
        }
        db.close();
        return transfersList;
    }

    //method to get all the budgets_view for the particlar user
    public List<BudgetModel> getAllBudgets(Date date, String userId){
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" BUDGET_ID, ");
        sqlQuerySB.append(" BUDGET_NAME, ");
        sqlQuerySB.append(" BUDGET_GRP_ID, ");
        sqlQuerySB.append(" BUDGET_GRP_TYPE, ");
        sqlQuerySB.append(" BUDGET_TYPE, ");
        sqlQuerySB.append(" BUDGET_AMT, ");
        sqlQuerySB.append(" BUDGET_NOTE, ");
        sqlQuerySB.append(" CREAT_DTM, ");
        sqlQuerySB.append(" MOD_DTM ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_BUDGET);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '" + userId + "' ");

        Log.i(CLASS_NAME, "Query to get the budgets: "+String.valueOf(sqlQuerySB));
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);
        BudgetModel budgetModelObj = null;

        List<BudgetModel> budgetModelList = new ArrayList<>();
        while (cursor.moveToNext()){
            String budIdStr = ColumnFetcher.loadString(cursor, "BUDGET_ID");
            String budNmeStr = ColumnFetcher.loadString(cursor, "BUDGET_NAME");
            String budGrpId = ColumnFetcher.loadString(cursor, "BUDGET_GRP_ID");
            String budGrpTyp = ColumnFetcher.loadString(cursor, "BUDGET_GRP_TYPE");
            String budTypeStr = ColumnFetcher.loadString(cursor, "BUDGET_TYPE");
            Double budAmt = ColumnFetcher.loadDouble(cursor, "BUDGET_AMT");
            String budNoteStr = ColumnFetcher.loadString(cursor, "BUDGET_NOTE");
            Date creatDtm = ColumnFetcher.loadDateTime(cursor, "CREAT_DTM");
            Date modDtm = ColumnFetcher.loadDateTime(cursor, "MOD_DTM");

            budgetModelObj = new BudgetModel();
            budgetModelObj.setBUDGET_ID(budIdStr);
            budgetModelObj.setBUDGET_NAME(budNmeStr);
            budgetModelObj.setBUDGET_GRP_ID(budGrpId);
            budgetModelObj.setBUDGET_GRP_TYPE(budGrpTyp);
            budgetModelObj.setBUDGET_TYPE(budTypeStr);
            budgetModelObj.setBUDGET_NOTE(budNoteStr);
            budgetModelObj.setBUDGET_AMT(budAmt);
            budgetModelObj.setBudgetRangeTotal(getTotalExpenseOnBudget(budgetModelObj, date));
            budgetModelObj.setCREAT_DTM(creatDtm);
            budgetModelObj.setMOD_DTM(modDtm);

            if("CATEGORY".equalsIgnoreCase(budGrpTyp)){
                budgetModelObj.setCategoryNameStr(getBudgetGroupTypeNameOnBudgetGroupType(budGrpTyp, budGrpId));
            }
            else if("ACCOUNT".equalsIgnoreCase(budGrpTyp)){
                budgetModelObj.setAccountNameStr(getBudgetGroupTypeNameOnBudgetGroupType(budGrpTyp, budGrpId));
            }
            else if("SPENT ON".equalsIgnoreCase(budGrpTyp)){
                budgetModelObj.setSpentOnNameStr(getBudgetGroupTypeNameOnBudgetGroupType(budGrpTyp, budGrpId));
            }

            budgetModelList.add(budgetModelObj);
        }

        cursor.close();
        db.close();
        return budgetModelList;
    }

    private String getBudgetGroupTypeNameOnBudgetGroupType(String budgetGroupTypeStr, String budgetGroupId){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);
        String selectorStr = "";
        String tableStr = "";
        String whereStr = "";

        if("CATEGORY".equalsIgnoreCase(budgetGroupTypeStr)){
            selectorStr = " CAT_NAME ";
            tableStr = DB_TABLE_CATEGORY;
            whereStr = " CAT_ID ";
        }
        else if("ACCOUNT".equalsIgnoreCase(budgetGroupTypeStr)){
            selectorStr = " ACC_NAME ";
            tableStr = DB_TABLE_ACCOUNT;
            whereStr = " ACC_ID ";
        }
        else if("SPENT ON".equalsIgnoreCase(budgetGroupTypeStr)){
            selectorStr = " SPNT_ON_NAME ";
            tableStr = DB_TABLE_SPENTON;
            whereStr = " SPNT_ON_ID ";
        }

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(selectorStr);
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(tableStr);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(whereStr+" = '"+budgetGroupId+"'");

        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        if(cursor.moveToNext()){
            return ColumnFetcher.loadString(cursor, selectorStr.trim());
        }
        db.close();
        return null;
    }

    public Double getTotalExpenseOnBudget(BudgetModel budgetModelObj, Date date){
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

        SimpleDateFormat sdf = new SimpleDateFormat(DB_DATE_FORMAT);
        String dateStr = sdf.format(date);
        String dateStrArr[] = dateStr.split("-");
        String dateConnectorStr = "";
        if("DAILY".equalsIgnoreCase(budgetModelObj.getBUDGET_TYPE())){
            dateConnectorStr = " AND TRAN_DATE = '"+dateStrArr[0]+"-"+dateStrArr[1]+"-"+dateStrArr[2]+"' ";
        }
        else if("WEEKLY".equalsIgnoreCase(budgetModelObj.getBUDGET_TYPE())){
            List<String> weekDaysList = DateTimeUtil.getAllDatesInWeekOnDate(dateStr);

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
        sqlQuerySB.append(DB_TABLE_TRANSACTION);
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
        sqlQuerySB.append(DB_TABLE_TRANSACTION);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'INCOME' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN_IS_DEL = 'N' ");
        sqlQuerySB.append(groupConnectorStr);
        sqlQuerySB.append(dateConnectorStr);
        sqlQuerySB.append(" )) ");

        sqlQuerySB.append(" AS TOTAL ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION + " TRAN ");

        //sqlQuerySB.append(" WHERE 1=1 ");

        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        if(cursor.moveToNext()){
            return ColumnFetcher.loadDouble(cursor, "TOTAL");
        }
        db.close();
        return 0.0;
    }

    private Map<String, MonthLegend> getTransfers(Map<String, MonthLegend> monthLegendMap, String dateStrArr[], String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" FRM_ACC.ACC_NAME AS ACC_FROM, ");
        sqlQuerySB.append(" FRM_ACC.ACC_IMG AS ACC_FROM_IMG, ");
        sqlQuerySB.append(" TO_ACC.ACC_NAME AS ACC_TO, ");
        sqlQuerySB.append(" TO_ACC.ACC_IMG AS ACC_TO_IMG, ");
        sqlQuerySB.append(" TRFR.TRNFR_AMT, ");
        sqlQuerySB.append(" TRFR.TRNFR_ID, ");
        sqlQuerySB.append(" TRFR.CREAT_DTM, ");
        sqlQuerySB.append(" TRFR.TRNFR_DATE ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER + " TRFR ");

        sqlQuerySB.append(" JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " FRM_ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" FRM_ACC.ACC_ID = TRFR.ACC_ID_FRM ");

        sqlQuerySB.append(" JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " TO_ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TO_ACC.ACC_ID = TRFR.ACC_ID_TO ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRFR.TRNFR_DATE ");
        sqlQuerySB.append(" BETWEEN ");
        sqlQuerySB.append(" '"+dateStrArr[0]+"' AND '"+dateStrArr[1]+"' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRFR.USER_ID = '"+userId+"' ");

        Log.i(CLASS_NAME, "Query to fetch Transfers : " + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            String fromAccStr = ColumnFetcher.loadString(cursor, "ACC_FROM");
            String fromAccImgStr = ColumnFetcher.loadString(cursor, "ACC_FROM_IMG");
            String toAccStr = ColumnFetcher.loadString(cursor, "ACC_TO");
            String toAccImgStr = ColumnFetcher.loadString(cursor, "ACC_TO_IMG");
            Double amt = ColumnFetcher.loadDouble(cursor, "TRNFR_AMT");
            String tranDatetr = ColumnFetcher.loadString(cursor, "TRNFR_DATE");
            String tranIdStr = ColumnFetcher.loadString(cursor, "TRNFR_ID");
            String creatDtmStr = ColumnFetcher.loadString(cursor, "CREAT_DTM");
            String tempDateStrArr[] = tranDatetr.split("-");

            String transferDateStr = tempDateStrArr[2]+"-"+tempDateStrArr[1]+"-"+tempDateStrArr[0];

            MonthLegend monthLegendObj;
            ActivitiesMO activities;
            List<TransferModel> transfersList;
            TransferModel transfer;

            transfer = new TransferModel();
            transfer.setFromAccName(fromAccStr);
            transfer.setFromAccImg(fromAccImgStr);
            transfer.setToAccName(toAccStr);
            transfer.setToAccImg(toAccImgStr);
            transfer.setTRNFR_AMT(amt);
            transfer.setTransferDate(tranDatetr);
            transfer.setTRNFR_ID(tranIdStr);
            transfer.setCreatDtm(creatDtmStr);

            //if the legend map already contains an entry for this date
            if(monthLegendMap.containsKey(transferDateStr)){
                monthLegendObj = monthLegendMap.get(transferDateStr);

                if(monthLegendObj == null){
                    monthLegendObj = new MonthLegend();
                }

                activities = monthLegendObj.getActivities();

                if(activities == null){
                    activities = new ActivitiesMO();
                }

                transfersList = activities.getTransfersList();

                if(transfersList == null){
                    transfersList = new ArrayList<>();
                }
            }
            //if the legend map doesnt contains an entry for this date
            else{
                monthLegendObj = new MonthLegend();
                activities = new ActivitiesMO();
                transfersList = new ArrayList<>();
            }

            transfersList.add(transfer);
            activities.setTransfersList(transfersList);
            monthLegendObj.setActivities(activities);

            monthLegendMap.put(transferDateStr, monthLegendObj);
        }
        cursor.close();
        db.close();
        return monthLegendMap;
    }

    public Map<String, MonthLegend> getTransactions(Map<String, MonthLegend> monthLegendMap, String[] dateStrArr, String userId){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CAT.CAT_ID, ");
        sqlQuerySB.append(" CAT.CAT_NAME, ");
        sqlQuerySB.append(" CAT.CAT_IMG, ");
        sqlQuerySB.append(" TRAN.TRAN_AMT, ");
        sqlQuerySB.append(" TRAN.TRAN_ID, ");
        sqlQuerySB.append(" TRAN.TRAN_TYPE, ");
        sqlQuerySB.append(" TRAN.TRAN_DATE, ");
        sqlQuerySB.append(" TRAN.TRAN_NAME, ");
        sqlQuerySB.append(" TRAN.CREAT_DTM, ");
        sqlQuerySB.append(" TRAN.SCHD_UPTO_DATE, ");
        sqlQuerySB.append(" TRAN.NOTIFY, ");
        sqlQuerySB.append(" TRAN.NOTIFY_TIME, ");
        sqlQuerySB.append(" TRAN.TRAN_NOTE, ");
        sqlQuerySB.append(" ACC.ACC_ID, ");
        sqlQuerySB.append(" ACC.ACC_NAME, ");
        sqlQuerySB.append(" ACC.ACC_IMG, ");
        sqlQuerySB.append(" SPN.SPNT_ON_ID, ");
        sqlQuerySB.append(" SPN.SPNT_ON_NAME, ");
        sqlQuerySB.append(" SPN.SPNT_ON_IMG, ");
        sqlQuerySB.append(" RPT.REPEAT_ID, ");
        sqlQuerySB.append(" RPT.REPEAT_NAME, ");
        sqlQuerySB.append(" RPT.REPEAT_IMG ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION + " TRAN ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_CATEGORY + " CAT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CAT.CAT_ID = TRAN.CAT_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" ACC.ACC_ID = TRAN.ACC_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_SPENTON + " SPN ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" SPN.SPNT_ON_ID = TRAN.SPNT_ON_ID ");

        sqlQuerySB.append(" LEFT OUTER JOIN ");
        sqlQuerySB.append(DB_TABLE_REPEAT + " RPT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" RPT.REPEAT_ID = TRAN.REPEAT_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN.TRAN_DATE ");
        sqlQuerySB.append(" BETWEEN ");
        sqlQuerySB.append(" '"+dateStrArr[0]+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" '"+dateStrArr[1]+"' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.USER_ID = '"+userId+"' ");

        Log.i(CLASS_NAME, "Query to fetch Transactions : " + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            String tranNoteStr = ColumnFetcher.loadString(cursor, "TRAN_NOTE");
            String schedDateStr = ColumnFetcher.loadString(cursor, "SCHD_UPTO_DATE");
            String notifyStr = ColumnFetcher.loadString(cursor, "NOTIFY");
            String notifyTimeStr = ColumnFetcher.loadString(cursor, "NOTIFY_TIME");
            String repeatIdStr = ColumnFetcher.loadString(cursor, "REPEAT_ID");
            String repeatStr = ColumnFetcher.loadString(cursor, "REPEAT_NAME");
            String repeatImgStr = ColumnFetcher.loadString(cursor, "REPEAT_IMG");
            String spntOnIdStr = ColumnFetcher.loadString(cursor, "SPNT_ON_ID");
            String spntOnStr = ColumnFetcher.loadString(cursor, "SPNT_ON_NAME");
            String spntImgStr = ColumnFetcher.loadString(cursor, "SPNT_ON_IMG");
            String accountIdStr = ColumnFetcher.loadString(cursor, "ACC_ID");
            String accountStr = ColumnFetcher.loadString(cursor, "ACC_NAME");
            String accountImgStr = ColumnFetcher.loadString(cursor, "ACC_IMG");
            String nameStr = ColumnFetcher.loadString(cursor, "TRAN_NAME");
            String catIdStr = ColumnFetcher.loadString(cursor, "CAT_ID");
            String catNameStr = ColumnFetcher.loadString(cursor, "CAT_NAME");
            String catImgStr = ColumnFetcher.loadString(cursor, "CAT_IMG");
            Double amt = ColumnFetcher.loadDouble(cursor, "TRAN_AMT");
            String tranTypeStr = ColumnFetcher.loadString(cursor, "TRAN_TYPE");
            String tranIdStr = ColumnFetcher.loadString(cursor, "TRAN_ID");
            String transactionDateStr = ColumnFetcher.loadString(cursor, "TRAN_DATE");
            Date transactionDate = ColumnFetcher.loadDate(cursor, "TRAN_DATE");
            String creatDtmStr = ColumnFetcher.loadString(cursor, "CREAT_DTM");
            String tempDateStrArr[] = transactionDateStr.split("-");

            String tranDateStr = tempDateStrArr[2]+"-"+tempDateStrArr[1]+"-"+tempDateStrArr[0];

            TransactionModel transaction = new TransactionModel();
            transaction.setCategory(catNameStr);
            transaction.setCAT_ID(catIdStr);
            transaction.setACC_ID(accountIdStr);
            transaction.setSPNT_ON_ID(spntOnIdStr);
            transaction.setREPEAT_ID(repeatIdStr);
            transaction.setTRAN_AMT(amt);
            transaction.setTRAN_NAME(nameStr);
            transaction.setCategoryImg(catImgStr);
            transaction.setAccount(accountStr);
            transaction.setTRAN_TYPE(tranTypeStr);
            transaction.setTRAN_ID(tranIdStr);
            transaction.setTransactionDate(transactionDateStr);
            transaction.setTRAN_DATE(transactionDate);
            transaction.setCreatDtm(creatDtmStr);
            transaction.setSCHD_UPTO_DATE(schedDateStr);
            transaction.setNOTIFY(notifyStr);
            transaction.setNOTIFY_TIME(notifyTimeStr);
            transaction.setRepeat(repeatStr);
            transaction.setRepeatImg(repeatImgStr);
            transaction.setSpentOn(spntOnStr);
            transaction.setSpentOnImg(spntImgStr);
            transaction.setAccountImg(accountImgStr);
            transaction.setTRAN_NOTE(tranNoteStr);

            MonthLegend monthLegendObj;
            Double totalAmount;
            ActivitiesMO activities;
            List<TransactionModel> transactionsList;

            //if the legend map already contains an entry for this date
            if(monthLegendMap.containsKey(tranDateStr)){
                monthLegendObj = monthLegendMap.get(tranDateStr);

                if(monthLegendObj == null){
                    monthLegendObj = new MonthLegend();
                }

                totalAmount = monthLegendObj.getTotalAmount();
                if("EXPENSE".equalsIgnoreCase(tranTypeStr)){
                    totalAmount -= amt;
                }
                else{
                    totalAmount += amt;
                }

                activities = monthLegendObj.getActivities();

                if(activities == null){
                    activities = new ActivitiesMO();
                }

                transactionsList = activities.getTransactionsList();

                if(transactionsList == null){
                    transactionsList = new ArrayList<>();
                }
            }
            //if the legend map doesnt contains an entry for this date
            else{
                totalAmount = 0.0;
                if("EXPENSE".equalsIgnoreCase(tranTypeStr)){
                    totalAmount -= amt;
                }
                else{
                    totalAmount += amt;
                }

                monthLegendObj = new MonthLegend();
                activities = new ActivitiesMO();
                transactionsList = new ArrayList<>();
            }

            transactionsList.add(transaction);
            activities.setTransactionsList(transactionsList);
            monthLegendObj.setActivities(activities);
            monthLegendObj.setTotalAmount(totalAmount);

            monthLegendMap.put(tranDateStr, monthLegendObj);
        }
        cursor.close();
        db.close();
        return monthLegendMap;
    }

    public Map<String, MonthLegend> getMonthLegendOnDate(String dateStr, String userId){
        Map<String, MonthLegend> monthLegendMap = new HashMap<>();

        //get start and end dates based on the passed date
        String dateStrArr[] = DateTimeUtil.getStartAndEndMonthDates(dateStr, MONTHS_RANGE/2);

        //get all the transactions on the passed date
        monthLegendMap = getTransactions(monthLegendMap, dateStrArr, userId);

        //get all the transfers on the passed date
        monthLegendMap = getTransfers(monthLegendMap, dateStrArr, userId);

        return monthLegendMap;
    }

    public Map<String, MonthLegend> getScheduledTransfers(Map<String, MonthLegend> monthLegendMap, String dateStrArr[], String userId) {
        /*SQLiteDatabase db = this.getWritableDatabase();
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
        sqlQuerySB.append(" SCH.USER_ID, ");
        sqlQuerySB.append(" ACC_FRM.ACC_NAME AS FRM_ACC, ");
        sqlQuerySB.append(" ACC_TO.ACC_NAME AS TO_ACC, ");
        sqlQuerySB.append(" SCH.CREAT_DTM, ");
        sqlQuerySB.append(" SCH.MOD_DTM ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_SHEDULEDTRANSFERSTABLE+ " SCH ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT+" ACC_FRM ");
        sqlQuerySB.append(" ON ACC_FRM.ACC_ID = SCH.SCH_TRNFR_ACC_ID_FRM ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT+" ACC_TO ");
        sqlQuerySB.append(" ON ACC_TO.ACC_ID = SCH.SCH_TRNFR_ACC_ID_TO ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" SCH_TRNFR_IS_DEL = '"+DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" SCH.USER_ID = '" + userId + "' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" SCH.SCH_TRNFR_DATE BETWEEN '"+dateStrArr[0]+"' AND '"+dateStrArr[1]+"' ");

        Log.i(CLASS_NAME, "Query to fetch Scheduled Transfers : " + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<String> allDatesInRange = DateTimeUtil.getAllDatesBetweenRange(dateStrArr);
        List<ScheduledTransferModel> scheduledTransferModelList;
        MonthLegend tempMonthLegend;
        ScheduledTransferModel scheduledTransferModelObj;

        SimpleDateFormat sdf2 = new SimpleDateFormat(JAVA_DATE_FORMAT);
        Date now = null;

        try{
            now = sdf2.parse(sdf2.format(new Date()));
        }
        catch (ParseException e){
            Log.e(CLASS_NAME, "PARSE EXCEPTION : "+e);
        }

        while (cursor.moveToNext()){
            String schTransferIdStr = ColumnFetcher.loadString(cursor, "SCH_TRNFR_ID");
            Date schTransfersDate = ColumnFetcher.loadDate(cursor, "SCH_TRNFR_DATE");
            String schTransferFreqStr = ColumnFetcher.loadString(cursor, "SCH_TRNFR_FREQ");
            String schTransferAutoStr = ColumnFetcher.loadString(cursor, "SCH_TRNFR_AUTO");
            Double schTransferAmt = ColumnFetcher.loadDouble(cursor, "SCH_TRNFR_AMT");
            String schTransferFrmAccIdStr = ColumnFetcher.loadString(cursor, "SCH_TRNFR_ACC_ID_FRM");
            String schTransferToAccIdStr = ColumnFetcher.loadString(cursor, "SCH_TRNFR_ACC_ID_TO");
            String schTransferFrmAccStr = ColumnFetcher.loadString(cursor, "FRM_ACC");
            String schTransferToAccStr = ColumnFetcher.loadString(cursor, "TO_ACC");
            Date schTransferCreateDtm = ColumnFetcher.loadDateTime(cursor, "CREAT_DTM");
            Date schTransferModDtm = ColumnFetcher.loadDateTime(cursor, "MOD_DTM");
            String schTransferNoteStr = ColumnFetcher.loadString(cursor, "SCH_TRNFR_NOTE");
            String schUserIdStr = ColumnFetcher.loadString(cursor, "USER_ID");

            String schTransfersDateStrArr[] = sdf2.format(schTransfersDate).split("-");

            //check the status of the notif (added, scheduled or cancelled)
            sqlQuerySB.setLength(0);
            sqlQuerySB.append(" SELECT ");
            sqlQuerySB.append(" CNCL_NOTIF_RSN, ");
            sqlQuerySB.append(" CNCL_NOTIF_DATE ");

            sqlQuerySB.append(" FROM ");
            sqlQuerySB.append(DB_TABLE_NOTIFICATION);

            sqlQuerySB.append(" WHERE ");
            sqlQuerySB.append(" CNCL_NOTIF_EVNT_ID = '"+schTransferIdStr+"' ");
            sqlQuerySB.append(" AND ");
            sqlQuerySB.append(" USER_ID = '"+userId+"' ");

            Cursor cursor2 = db.rawQuery(sqlQuerySB.toString(), null);

            //this map holds the the date and reason for the action for the particular scheduled transfer
            Map<String, String> notifActionMap = new HashMap<>();
            while (cursor2.moveToNext()) {
                String reasonStr = ColumnFetcher.loadString(cursor2, "CNCL_NOTIF_RSN");
                String notifActionDateStr = sdf2.format(ColumnFetcher.loadDate(cursor2, "CNCL_NOTIF_DATE"));

                notifActionMap.put(notifActionDateStr, reasonStr);
            }
            //check the status of the notif (added, scheduled or cancelled) ends--


            SimpleDateFormat sdf3 = new SimpleDateFormat(DB_DATE_FORMAT);
            String dateStr = "";

            for(String iterAllDatesInRangeList : allDatesInRange){
                //this flag allows us whether to proceed with adding this date into month legend as a scheduled transfer
                boolean isScheduledTransfer = false;

                String thisDateStr = iterAllDatesInRangeList;
                String thisDateStrArr[] = thisDateStr.split("-");

                Date thisDate = null;
                try{
                    thisDate = sdf3.parse(thisDateStr);
                    //to get only those scheduled transactions which are after the SCH_TRAN_DATE
                    if(!thisDate.equals(now) && thisDate.before(now)){
                        continue;
                    }

                    dateStr = sdf2.format(thisDate);
                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "ERROR !!"+e);
                    return monthLegendMap;
                }

                scheduledTransferModelObj = new ScheduledTransferModel();
                scheduledTransferModelObj.setSCH_TRNFR_ID(schTransferIdStr);
                scheduledTransferModelObj.setSCH_TRNFR_DATE(schTransfersDate);
                scheduledTransferModelObj.setSCH_TRNFR_FREQ(schTransferFreqStr);
                scheduledTransferModelObj.setSCH_TRNFR_AUTO(schTransferAutoStr);
                scheduledTransferModelObj.setSCH_TRNFR_AMT(schTransferAmt);
                scheduledTransferModelObj.setSCH_TRNFR_ACC_ID_FRM(schTransferFrmAccIdStr);
                scheduledTransferModelObj.setSCH_TRNFR_ACC_ID_TO(schTransferToAccIdStr);
                scheduledTransferModelObj.setFromAccountStr(schTransferFrmAccStr);
                scheduledTransferModelObj.setToAccountStr(schTransferToAccStr);
                scheduledTransferModelObj.setSCH_TRNFR_NOTE(schTransferNoteStr);
                scheduledTransferModelObj.setCREAT_DTM(schTransferCreateDtm);
                scheduledTransferModelObj.setMOD_DTM(schTransferModDtm);
                scheduledTransferModelObj.setUSER_ID(schUserIdStr);

                //if the date is present in the notifActionMap, that means there's an action for the scheduled transfer on that day(cancel). If it isn't, it means it is still scheduled.
                if(notifActionMap.containsKey(dateStr)){
                    //if its a deleted scheduled transfer then skip it from adding into month legend
                    if ("DELETE".equalsIgnoreCase(notifActionMap.get(dateStr))){
                        continue;
                    }

                    scheduledTransferModelObj.setStatus(notifActionMap.get(dateStr));
                }
                else{
                    scheduledTransferModelObj.setStatus("SCHEDULED");
                }

                if("ONCE".equalsIgnoreCase(schTransferFreqStr)){
                    //to check if sch tran date & month & year (01-11-2014,02-11-2015,03-11-2016...) is same as this date's day (01-11-2014,02-11-2015,03-11-2016...)
                    if(thisDate.equals(schTransfersDate)){
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
                    String schTranDayOfWeekStr = DateTimeUtil.getDayOfWeekFromDate(schTransfersDate);
                    String thisDateDayOfWeeksStr = DateTimeUtil.getDayOfWeekFromDate(thisDate);

                    if(schTranDayOfWeekStr.equalsIgnoreCase(thisDateDayOfWeeksStr)){
                        isScheduledTransfer = true;
                    }
                }
                //for monthly transfers
                else if("MONTHLY".equalsIgnoreCase(schTransferFreqStr)){
                    //to check if sch tran date (01,02,03...) is same as this date's day (01,02,03...)..here we're neglecting the month and year of both the dates
                    if(thisDateStrArr[2].equalsIgnoreCase(schTransfersDateStrArr[0])){
                        isScheduledTransfer = true;
                    }
                }

                //for yearly transfers
                else if("YEARLY".equalsIgnoreCase(schTransferFreqStr)){
                    //to check if sch tran date & month (01-11,02-11,03-11...) is same as this date's day (01-11,02-11,03-11...)..here we're neglecting only the year
                    if(thisDateStrArr[0].equalsIgnoreCase(schTransfersDateStrArr[2]) && thisDateStrArr[1].equalsIgnoreCase(schTransfersDateStrArr[1])){
                        isScheduledTransfer = true;
                    }
                }

                if(isScheduledTransfer){
                    //this date is the date on which the schedule transfer is supposed to happen
                    scheduledTransferModelObj.setScheduledDate(thisDate);
                }
                else{
                    continue;
                }

                if(monthLegendMap.containsKey(dateStr)){
                    tempMonthLegend = monthLegendMap.get(dateStr);
                    scheduledTransferModelList = tempMonthLegend.getScheduledTransferModelList();

                    if(scheduledTransferModelList == null){
                        scheduledTransferModelList = new ArrayList<>();
                    }
                } else{
                    tempMonthLegend = new MonthLegend();
                    scheduledTransferModelList = new ArrayList<>();
                }

                tempMonthLegend.setHasScheduledTransfer(true);
                scheduledTransferModelList.add(scheduledTransferModelObj);
                tempMonthLegend.setScheduledTransferModelList(scheduledTransferModelList);

                monthLegendMap.put(dateStr, tempMonthLegend);
            }
        }
        db.close();*/
        return monthLegendMap;
    }

    public Map<String, MonthLegend> getScheduledTransactions(Map<String, MonthLegend> monthLegendMap, String dateStrArr[], String userId) {
        /*SQLiteDatabase db = this.getWritableDatabase();
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
        sqlQuerySB.append(" SCH.USER_ID, ");
        sqlQuerySB.append(" CAT.CAT_NAME, ");
        sqlQuerySB.append(" ACC.ACC_NAME, ");
        sqlQuerySB.append(" SPNT.SPNT_ON_NAME, ");
        sqlQuerySB.append(" SCH.CREAT_DTM, ");
        sqlQuerySB.append(" SCH.MOD_DTM ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_SCHEDULEDTRANSACTION + " SCH ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_CATEGORY+" CAT ");
        sqlQuerySB.append(" ON CAT.CAT_ID = SCH.SCH_TRAN_CAT_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT+" ACC ");
        sqlQuerySB.append(" ON ACC.ACC_ID = SCH.SCH_TRAN_ACC_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_SPENTON+" SPNT ");
        sqlQuerySB.append(" ON SPNT.SPNT_ON_ID = SCH.SCH_TRAN_SPNT_ON_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" SCH.USER_ID = '" + userId + "' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" SCH.SCH_TRAN_DATE BETWEEN '" + dateStrArr[0] + "' AND '"+dateStrArr[1]+"' ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<String> allDatesInRange = DateTimeUtil.getAllDatesBetweenRange(dateStrArr);
        List<ScheduledTransactionModel> scheduledTransactionModelList;
        MonthLegend tempMonthLegend;
        ScheduledTransactionModel scheduledTransactionModelObj;

        SimpleDateFormat sdf2 = new SimpleDateFormat(JAVA_DATE_FORMAT);
        Date now = null;

        try{
            now = sdf2.parse(sdf2.format(new Date()));
        }
        catch (ParseException e){
            Log.e(CLASS_NAME, "PARSE EXCEPTION : "+e);
        }

        while (cursor.moveToNext()){
            String schTranIdStr = ColumnFetcher.loadString(cursor, "SCH_TRAN_ID");
            String schTranNameStr = ColumnFetcher.loadString(cursor, "SCH_TRAN_NAME");
            String schTranCatIdStr = ColumnFetcher.loadString(cursor, "SCH_TRAN_CAT_ID");
            String schTranSpntOnIdStr = ColumnFetcher.loadString(cursor, "SCH_TRAN_SPNT_ON_ID");
            String schTranSpntOnNameStr = ColumnFetcher.loadString(cursor, "SPNT_ON_NAME");
            String schTranAccIdStr = ColumnFetcher.loadString(cursor, "SCH_TRAN_ACC_ID");
            String schTranAccNameStr = ColumnFetcher.loadString(cursor, "ACC_NAME");
            Date schTransactionDate = ColumnFetcher.loadDate(cursor, "SCH_TRAN_DATE");
            String schTranFreqStr = ColumnFetcher.loadString(cursor, "SCH_TRAN_FREQ");
            String schTranAutoStr = ColumnFetcher.loadString(cursor, "SCH_TRAN_AUTO");
            String schTranCatStr = ColumnFetcher.loadString(cursor, "CAT_NAME");
            Double schTranAmt = ColumnFetcher.loadDouble(cursor, "SCH_TRAN_AMT");
            String schTranTypeStr = ColumnFetcher.loadString(cursor, "SCH_TRAN_TYPE");
            Date schTranCreateDtm = ColumnFetcher.loadDateTime(cursor, "CREAT_DTM");
            Date schTranModDtm = ColumnFetcher.loadDateTime(cursor, "MOD_DTM");
            String schTranNoteStr = ColumnFetcher.loadString(cursor, "SCH_TRAN_NOTE");
            String schUserIdStr = ColumnFetcher.loadString(cursor, "USER_ID");

            String schTransactionDateStrArr[] = sdf2.format(schTransactionDate).split("-");

            //check the status of the notif (added, scheduled or cancelled)
            sqlQuerySB.setLength(0);
            sqlQuerySB.append(" SELECT ");
            sqlQuerySB.append(" CNCL_NOTIF_RSN, ");
            sqlQuerySB.append(" CNCL_NOTIF_DATE ");

            sqlQuerySB.append(" FROM ");
            sqlQuerySB.append(DB_TABLE_NOTIFICATION);

            sqlQuerySB.append(" WHERE ");
            sqlQuerySB.append(" CNCL_NOTIF_EVNT_ID = '"+schTranIdStr+"' ");
            sqlQuerySB.append(" AND ");
            sqlQuerySB.append(" USER_ID = '"+userId+"' ");

            Cursor cursor2 = db.rawQuery(sqlQuerySB.toString(), null);

            //this map holds the the date and reason for the action for the particular scheduled transaction
            Map<String, String> notifActionMap = new HashMap<>();
            while (cursor2.moveToNext()) {
                String reasonStr = ColumnFetcher.loadString(cursor2, "CNCL_NOTIF_RSN");
                String notifActionDateStr = sdf2.format(ColumnFetcher.loadDate(cursor2, "CNCL_NOTIF_DATE"));

                notifActionMap.put(notifActionDateStr, reasonStr);
            }

            SimpleDateFormat sdf3 = new SimpleDateFormat(DB_DATE_FORMAT);
            String dateStr = "";

            //check the status of the notif (added, scheduled or cancelled) ends--
            for(String iterallDatesInRangeList : allDatesInRange){
                //this flag allows us whether to proceed with adding this date into month legend as a scheduled transaction
                boolean isScheduledTransaction = false;

                String thisDateStr = iterallDatesInRangeList;
                String thisDateStrArr[] = thisDateStr.split("-");

                Date thisDate = null;
                try{
                    thisDate = sdf3.parse(thisDateStr);
                    //to get only those scheduled transactions which are after today
                    if(!thisDate.equals(now) && thisDate.before(now)){
                        continue;
                    }

                    dateStr = sdf2.format(thisDate);
                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "ERROR !!"+e);
                    return monthLegendMap;
                }

                scheduledTransactionModelObj = new ScheduledTransactionModel();
                scheduledTransactionModelObj.setSCH_TRAN_ID(schTranIdStr);
                scheduledTransactionModelObj.setSCH_TRAN_NAME(schTranNameStr);
                scheduledTransactionModelObj.setSCH_TRAN_CAT_ID(schTranCatIdStr);
                scheduledTransactionModelObj.setSCH_TRAN_SPNT_ON_ID(schTranSpntOnIdStr);
                scheduledTransactionModelObj.setSpentOnNameStr(schTranSpntOnNameStr);
                scheduledTransactionModelObj.setSCH_TRAN_ACC_ID(schTranAccIdStr);
                scheduledTransactionModelObj.setAccountNameStr(schTranAccNameStr);
                scheduledTransactionModelObj.setSCH_TRAN_DATE(schTransactionDate);
                scheduledTransactionModelObj.setSCH_TRAN_FREQ(schTranFreqStr);
                scheduledTransactionModelObj.setSCH_TRAN_AUTO(schTranAutoStr);
                scheduledTransactionModelObj.setCategoryNameStr(schTranCatStr);
                scheduledTransactionModelObj.setSCH_TRAN_AMT(schTranAmt);
                scheduledTransactionModelObj.setSCH_TRAN_TYPE(schTranTypeStr);
                scheduledTransactionModelObj.setCREAT_DTM(schTranCreateDtm);
                scheduledTransactionModelObj.setMOD_DTM(schTranModDtm);
                scheduledTransactionModelObj.setSCH_TRAN_NOTE(schTranNoteStr);
                scheduledTransactionModelObj.setUSER_ID(schUserIdStr);

                //if the date is present in the notifActionMap, that means there's an action for the scheduled transaction on that day(cancel). If it isn't, it means it is still scheduled.
                if(notifActionMap.containsKey(dateStr)){
                    //if its a deleted scheduled transaction then skip it from adding into month legend
                    if ("DELETE".equalsIgnoreCase(notifActionMap.get(dateStr))){
                        continue;
                    }
                    scheduledTransactionModelObj.setStatus(notifActionMap.get(dateStr));
                }
                else{
                    scheduledTransactionModelObj.setStatus("SCHEDULED");
                }

                //For a Scheduled transaction which is scheduled to happen only once
                if("ONCE".equalsIgnoreCase(schTranFreqStr)){
                    //to check if sch tran date & month & year (01-11-2014,02-11-2015,03-11-2016...) is same as this date's day (01-11-2014,02-11-2015,03-11-2016...)
                    if(thisDate.equals(schTransactionDate)){
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
                    String schTranDayOfWeekStr = DateTimeUtil.getDayOfWeekFromDate(schTransactionDate);
                    String thisDateDayOfWeeksStr = DateTimeUtil.getDayOfWeekFromDate(thisDate);

                    if(schTranDayOfWeekStr.equalsIgnoreCase(thisDateDayOfWeeksStr)){
                        isScheduledTransaction = true;
                    }
                }
                //for monthly transactions
                else if("MONTHLY".equalsIgnoreCase(schTranFreqStr)){
                    //to check if sch tran date (01,02,03...) is same as this date's day (01,02,03...)..here we're neglecting the month and year of both the dates
                    if(thisDateStrArr[2].equalsIgnoreCase(schTransactionDateStrArr[0])){
                        isScheduledTransaction = true;
                    }
                }

                //for yearly transactions
                else if("YEARLY".equalsIgnoreCase(schTranFreqStr)){
                    //to check if sch tran date & month (01-11,02-11,03-11...) is same as this date's day (01-11,02-11,03-11...)..here we're neglecting only the year
                    if(thisDateStrArr[2].equalsIgnoreCase(schTransactionDateStrArr[0]) && thisDateStrArr[1].equalsIgnoreCase(schTransactionDateStrArr[1])){
                        isScheduledTransaction = true;
                    }
                }

                if(isScheduledTransaction){
                    //this date is the date on which the schedule transaction is supposed to happen
                    scheduledTransactionModelObj.setScheduledDate(thisDate);
                }
                else{
                    continue;
                }

                if(monthLegendMap.containsKey(dateStr)){
                    tempMonthLegend = monthLegendMap.get(dateStr);
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

                monthLegendMap.put(dateStr, tempMonthLegend);
            }
        }
        db.close();*/
        return monthLegendMap;
    }

    public boolean deleteAllSched(Object object){
        /*SQLiteDatabase db = this.getWritableDatabase();

        if(object instanceof ScheduledTransactionModel){
            return db.delete(DB_TABLE_SCHEDULEDTRANSACTION, "SCH_TRAN_ID = '" + ((ScheduledTransactionModel)object).getSCH_TRAN_ID()+"'", null) > 0;
        }
        else if(object instanceof ScheduledTransferModel){
            return db.delete(DB_TABLE_SHEDULEDTRANSFERSTABLE, "SCH_TRNFR_ID = '" + ((ScheduledTransferModel)object).getSCH_TRNFR_ID()+"'", null) > 0;
        }
        db.close();
        return false;*/

        return false;
    }

    public boolean deleteOneSched(Object object){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("CNCL_NOTIF_ID", IdGenerator.generateUniqueId("NOTIF"));
        values.put("CNCL_NOTIF_RSN", "DELETE");
        values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));

        if(object instanceof ScheduledTransactionModel){
            values.put("CNCL_NOTIF_TYPE", "TRANSACTION");
            values.put("CNCL_NOTIF_EVNT_ID", ((ScheduledTransactionModel)object).getSCH_TRAN_ID());
            values.put("USER_ID", ((ScheduledTransactionModel) object).getUSER_ID());
            Date date = ((ScheduledTransactionModel)object).getScheduledDate();
            values.put("CNCL_NOTIF_DATE", simpleDateFormat.format(date));
        }
        else if(object instanceof ScheduledTransferModel){
            values.put("CNCL_NOTIF_TYPE", "TRANSFER");
            values.put("CNCL_NOTIF_EVNT_ID", ((ScheduledTransferModel)object).getSCH_TRNFR_ID());
            values.put("USER_ID", ((ScheduledTransferModel)object).getUSER_ID());
            Date date = ((ScheduledTransferModel)object).getScheduledDate();
            values.put("CNCL_NOTIF_DATE", simpleDateFormat.format(date));
        }
        else{
            return false;
        }
        boolean result = db.insert(DB_TABLE_NOTIFICATION, null, values) > 0;
        db.close();
        return result;
    }

    public boolean deleteTransfer(String transferIdStr){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = db.delete(DB_TABLE_TRANSFER, "TRNFR_ID = '" + transferIdStr + "'", null) > 0;;
        db.close();
        return result;
    }

    public boolean deleteBudget(String budgetIdStr){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = db.delete(DB_TABLE_BUDGET, "BUDGET_ID = '" + budgetIdStr + "'", null) > 0;
        db.close();
        return result;
    }

    public boolean deleteAccount(AccountsMO accountsModelObj){
        SQLiteDatabase db = this.getWritableDatabase();

        //delete transactions which are using this account
        db.delete(DB_TABLE_TRANSACTION, "ACC_ID = '" + accountsModelObj.getACC_ID()+"'", null);

        //delete scheduled transactions which are using this account
        //db.delete(DB_TABLE_SCHEDULEDTRANSACTION, "SCH_TRAN_ACC_ID = '" + accountsModelObj.getACC_ID()+"'", null);

        //delete transfers which are using this account (either from or to)
        db.delete(DB_TABLE_TRANSFER, "ACC_ID_FRM = '" + accountsModelObj.getACC_ID()+"' OR ACC_ID_TO = '" + accountsModelObj.getACC_ID()+"'", null);

        //delete scheduled transfers which are using this account (either from or to)
        //db.delete(DB_TABLE_SHEDULEDTRANSFERSTABLE, "SCH_TRNFR_ACC_ID_FRM = '" + accountsModelObj.getACC_ID()+"' OR SCH_TRNFR_ACC_ID_TO = '" + accountsModelObj.getACC_ID()+"'", null);

        //delete budgets which are using this account
        db.delete(DB_TABLE_BUDGET, "BUDGET_GRP_ID = '" + accountsModelObj.getACC_ID()+"'", null);

        boolean result = db.delete(DB_TABLE_ACCOUNT, "ACC_ID = '" + accountsModelObj.getACC_ID()+"'", null) > 0;
        db.close();

        //delete the account
        return result;
    }

    //--------------------- end of method to get all accounts--------------------------//

    public List<RepeatMO> getAllRepeats(){
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" REPEAT_ID, ");
        sqlQuerySB.append(" REPEAT_NAME, ");
        sqlQuerySB.append(" REPEAT_IS_DEF, ");
        sqlQuerySB.append(" REPEAT_IMG ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_REPEAT);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<RepeatMO> repeatMOList = new ArrayList<>();
        while (cursor.moveToNext()){
            RepeatMO repeatMO = new RepeatMO();
            repeatMO.setREPEAT_ID(ColumnFetcher.getInstance().loadString(cursor, "REPEAT_ID"));
            repeatMO.setREPEAT_NAME(ColumnFetcher.getInstance().loadString(cursor, "REPEAT_NAME"));
            repeatMO.setREPEAT_IS_DEF(ColumnFetcher.getInstance().loadString(cursor, "REPEAT_IS_DEF"));
            repeatMO.setREPEAT_IMG(ColumnFetcher.getInstance().loadString(cursor, "REPEAT_IMG"));

            repeatMOList.add(repeatMO);
        }
        cursor.close();
        db.close();
        return repeatMOList;
    }


    public List<CategoryMO> getAllCategories(String userId){
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CAT_ID, ");
        sqlQuerySB.append(" CAT_NAME, ");
        sqlQuerySB.append(" CAT_IS_DEF, ");
        sqlQuerySB.append(" CAT_IMG ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_CATEGORY);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '"+ ADMIN_USERID +"' ");
        sqlQuerySB.append(" OR USER_ID = '"+userId+"' ");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" CAT_NAME ");
        sqlQuerySB.append(" ASC ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<CategoryMO> categoryList = new ArrayList<>();
        while (cursor.moveToNext()){
            CategoryMO categoryMO = new CategoryMO();
            categoryMO.setCAT_ID(ColumnFetcher.getInstance().loadString(cursor, "CAT_ID"));
            categoryMO.setCAT_NAME(ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME"));
            categoryMO.setCAT_IMG(ColumnFetcher.getInstance().loadString(cursor, "CAT_IMG"));
            categoryMO.setCAT_IS_DEF(ColumnFetcher.getInstance().loadString(cursor, "CAT_IS_DEF"));

            categoryList.add(categoryMO);
        }
        cursor.close();
        db.close();
        return categoryList;
    }

    public List<SpentOnMO> getAllSpentOn(String userId){
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SPNT_ON_ID, ");
        sqlQuerySB.append(" SPNT_ON_NAME, ");
        sqlQuerySB.append(" SPNT_ON_IMG, ");
        sqlQuerySB.append(" SPNT_ON_IS_DEF ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_SPENTON);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '"+ ADMIN_USERID +"' ");
        sqlQuerySB.append(" OR USER_ID = '"+userId+"' ");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" SPNT_ON_NAME ");
        sqlQuerySB.append(" ASC ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<SpentOnMO> spentOnList = new ArrayList<>();
        while (cursor.moveToNext()){
            SpentOnMO spentOnMO = new SpentOnMO();
            spentOnMO.setSPNT_ON_ID(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_ID"));
            spentOnMO.setSPNT_ON_NAME(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME"));
            spentOnMO.setSPNT_ON_IMG(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_IMG"));
            spentOnMO.setSPNT_ON_IS_DEF(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_IS_DEF"));

            spentOnList.add(spentOnMO);
        }
        cursor.close();
        db.close();
        return spentOnList;
    }

    //---------------------method to get all accounts--------------------------//
    public List<AccountsMO> getAllAccounts(String userId){
        List<AccountsMO> accountsList = new ArrayList<AccountsMO>();
        SQLiteDatabase db = this.getWritableDatabase();

        if(db == null){
            Log.e(CLASS_NAME, "SQLiteDatabase object is null");
            return null;
        }

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" ACC.ACC_NAME, ");
        sqlQuerySB.append(" ACC.ACC_ID, ");
        sqlQuerySB.append(" ACC.ACC_IS_DEF, ");
        sqlQuerySB.append(" ACC.ACC_IMG, ");

        sqlQuerySB.append(" (( SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'INCOME' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_ID = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId+"','"+ADMIN_USERID+"')) ");

        sqlQuerySB.append(" + ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID_TO = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId+"','"+ADMIN_USERID+"') ))");

        sqlQuerySB.append(" - ");

        sqlQuerySB.append(" ((SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'EXPENSE' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_ID = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId+"','"+ADMIN_USERID+"')) ");

        sqlQuerySB.append(" + ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID_FRM = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId+"','"+ADMIN_USERID+"') ))");

        sqlQuerySB.append(" AS ACC_TOTAL ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT+" ACC ");

        sqlQuerySB.append(" LEFT JOIN ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION+" TRAN");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TRAN.ACC_ID = ACC.ACC_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId+"','"+ADMIN_USERID+"') ");

        sqlQuerySB.append(" GROUP BY ACC.ACC_ID ");
        sqlQuerySB.append(" ORDER BY ACC.CREAT_DTM ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        String accountIdStr, accountNameStr, currecyStr, accountIsDefaultStr, accountImgStr;
        Double accountTotal;
        AccountsMO accountsModel;
        while (cursor.moveToNext()){
            accountIdStr = ColumnFetcher.loadString(cursor, "ACC_ID");
            accountNameStr = ColumnFetcher.loadString(cursor, "ACC_NAME");
            accountTotal = ColumnFetcher.loadDouble(cursor, "ACC_TOTAL");
            currecyStr = ColumnFetcher.loadString(cursor, "CUR_NAME");
            accountIsDefaultStr = ColumnFetcher.loadString(cursor, "ACC_IS_DEF");
            accountImgStr = ColumnFetcher.loadString(cursor, "ACC_IMG");

            accountsModel = new AccountsMO();
            accountsModel.setACC_ID(accountIdStr);
            accountsModel.setACC_NAME(accountNameStr);
            accountsModel.setACC_TOTAL(accountTotal);
            accountsModel.setCurrency(currecyStr);
            accountsModel.setACC_IS_DEF(accountIsDefaultStr);
            accountsModel.setACC_IMG(accountImgStr);
            accountsList.add(accountsModel);
        }
        cursor.close();
        db.close();
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
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}
}
