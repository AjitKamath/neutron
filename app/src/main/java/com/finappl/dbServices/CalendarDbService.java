package com.finappl.dbServices;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.AccountMO;
import com.finappl.models.ActivitiesMO;
import com.finappl.models.BudgetMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.CountryMO;
import com.finappl.models.MonthLegend;
import com.finappl.models.RepeatMO;
import com.finappl.models.ScheduledTransactionModel;
import com.finappl.models.ScheduledTransferModel;
import com.finappl.models.SpentOnMO;
import com.finappl.models.TransactionMO;
import com.finappl.models.TransferMO;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.DateTimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.DB_DATE_FORMAT;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_TABLE_ACCOUNT;
import static com.finappl.utils.Constants.DB_TABLE_BUDGET;
import static com.finappl.utils.Constants.DB_TABLE_CATEGORY;
import static com.finappl.utils.Constants.DB_TABLE_COUNTRY;
import static com.finappl.utils.Constants.DB_TABLE_NOTIFICATION;
import static com.finappl.utils.Constants.DB_TABLE_REPEAT;
import static com.finappl.utils.Constants.DB_TABLE_SPENTON;
import static com.finappl.utils.Constants.DB_TABLE_TRANSACTION;
import static com.finappl.utils.Constants.DB_TABLE_TRANSFER;
import static com.finappl.utils.Constants.DB_VERSION;
import static com.finappl.utils.Constants.MONTHS_RANGE;


public class CalendarDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();

	private static CalendarDbService sInstance = null;

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

    //method to get all the budgets_view for the particlar user
    public List<BudgetMO> getAllBudgets(Date date, String userId){
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
        BudgetMO budgetModelObj = null;

        List<BudgetMO> budgetModelList = new ArrayList<>();
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

            budgetModelObj = new BudgetMO();
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

    public Double getTotalExpenseOnBudget(BudgetMO budgetModelObj, Date date){
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
        sqlQuerySB.append(" FRM_ACC.ACC_ID AS ACC_ID_FROM, ");
        sqlQuerySB.append(" FRM_ACC.ACC_IMG AS ACC_FROM_IMG, ");
        sqlQuerySB.append(" TO_ACC.ACC_NAME AS ACC_TO, ");
        sqlQuerySB.append(" TO_ACC.ACC_ID AS ACC_ID_TO, ");
        sqlQuerySB.append(" TO_ACC.ACC_IMG AS ACC_TO_IMG, ");
        sqlQuerySB.append(" TRFR.TRNFR_AMT, ");
        sqlQuerySB.append(" TRFR.TRNFR_ID, ");
        sqlQuerySB.append(" TRFR.CREAT_DTM, ");
        sqlQuerySB.append(" TRFR.TRNFR_DATE, ");
        sqlQuerySB.append(" RPT.REPEAT_ID, ");
        sqlQuerySB.append(" RPT.REPEAT_NAME, ");
        sqlQuerySB.append(" RPT.REPEAT_IMG, ");
        sqlQuerySB.append(" TRFR.SCHD_UPTO_DATE, ");
        sqlQuerySB.append(" TRFR.NOTIFY, ");
        sqlQuerySB.append(" TRFR.NOTIFY_TIME, ");
        sqlQuerySB.append(" TRFR.TRNFR_NOTE ");

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

        sqlQuerySB.append(" LEFT OUTER JOIN ");
        sqlQuerySB.append(DB_TABLE_REPEAT + " RPT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" RPT.REPEAT_ID = TRFR.REPEAT_ID ");

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
            String fromAccIdStr = ColumnFetcher.loadString(cursor, "ACC_ID_FROM");
            String fromAccImgStr = ColumnFetcher.loadString(cursor, "ACC_FROM_IMG");
            String toAccStr = ColumnFetcher.loadString(cursor, "ACC_TO");
            String toAccIdStr = ColumnFetcher.loadString(cursor, "ACC_ID_TO");
            String toAccImgStr = ColumnFetcher.loadString(cursor, "ACC_TO_IMG");
            Double amt = ColumnFetcher.loadDouble(cursor, "TRNFR_AMT");
            Date tranDate = ColumnFetcher.loadDate(cursor, "TRNFR_DATE");
            String tranDateStr = ColumnFetcher.loadString(cursor, "TRNFR_DATE");
            String tranIdStr = ColumnFetcher.loadString(cursor, "TRNFR_ID");
            String creatDtmStr = ColumnFetcher.loadString(cursor, "CREAT_DTM");
            String schedDateStr = ColumnFetcher.loadString(cursor, "SCHD_UPTO_DATE");
            String notifyStr = ColumnFetcher.loadString(cursor, "NOTIFY");
            String notifyTimeStr = ColumnFetcher.loadString(cursor, "NOTIFY_TIME");
            String repeatIdStr = ColumnFetcher.loadString(cursor, "REPEAT_ID");
            String repeatStr = ColumnFetcher.loadString(cursor, "REPEAT_NAME");
            String repeatImgStr = ColumnFetcher.loadString(cursor, "REPEAT_IMG");
            String noteStr = ColumnFetcher.loadString(cursor, "TRNFR_NOTE");

            String tempDateStrArr[] = tranDateStr.split("-");

            String transferDateStr = tempDateStrArr[2]+"-"+tempDateStrArr[1]+"-"+tempDateStrArr[0];

            MonthLegend monthLegendObj;
            ActivitiesMO activities;
            List<TransferMO> transfersList;
            TransferMO transfer;

            transfer = new TransferMO();
            transfer.setFromAccName(fromAccStr);
            transfer.setACC_ID_FRM(fromAccIdStr);
            transfer.setFromAccImg(fromAccImgStr);
            transfer.setToAccName(toAccStr);
            transfer.setACC_ID_TO(toAccIdStr);
            transfer.setToAccImg(toAccImgStr);
            transfer.setTRNFR_AMT(amt);
            transfer.setTRNFR_DATE(tranDate);
            transfer.setTransferDate(tranDateStr);
            transfer.setTRNFR_ID(tranIdStr);
            transfer.setCreatDtm(creatDtmStr);
            transfer.setREPEAT_ID(repeatIdStr);
            transfer.setSCHD_UPTO_DATE(schedDateStr);
            transfer.setNOTIFY(notifyStr);
            transfer.setNOTIFY_TIME(notifyTimeStr);
            transfer.setRepeat(repeatStr);
            transfer.setRepeatImg(repeatImgStr);
            transfer.setTRNFR_NOTE(noteStr);

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

            TransactionMO transaction = new TransactionMO();
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
            List<TransactionMO> transactionsList;

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

    //--------------------- end of method to get all accounts--------------------------//

    public List<CountryMO> getAllCountriesAndCurrencies(){
        StringBuilder sqlQuerySB = new StringBuilder();
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CNTRY_ID, ");
        sqlQuerySB.append(" CNTRY_NAME, ");
        sqlQuerySB.append(" CNTRY_CODE, ");
        sqlQuerySB.append(" CUR, ");
        sqlQuerySB.append(" CUR_CODE, ");
        sqlQuerySB.append(" CNTRY_IMG ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_COUNTRY);

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" CNTRY_NAME ");
        sqlQuerySB.append(" ASC ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<CountryMO> countriesList = new ArrayList<>();
        while (cursor.moveToNext()){
            CountryMO country = new CountryMO();
            country.setCNTRY_ID(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_ID"));
            country.setCNTRY_NAME(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_NAME"));
            country.setCNTRY_CODE(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_CODE"));
            country.setCUR(ColumnFetcher.getInstance().loadString(cursor, "CUR"));
            country.setCUR_CODE(ColumnFetcher.getInstance().loadString(cursor, "CUR_CODE"));
            country.setCNTRY_IMG(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_IMG"));
            countriesList.add(country);
        }
        cursor.close();
        db.close();
        return countriesList;
    }

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
    public List<AccountMO> getAllAccounts(String userId){
        List<AccountMO> accountsList = new ArrayList<AccountMO>();
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

        sqlQuerySB.append(" ((SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION+" TRAN ");
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'INCOME' ");
        sqlQuerySB.append(" AND TRAN.ACC_ID = ACC.ACC_ID ");
        sqlQuerySB.append(" AND TRIM(TRAN.USER_ID) = '"+userId+"') ");

        sqlQuerySB.append(" + ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER+" TRNFR ");
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRNFR.ACC_ID_TO = ACC.ACC_ID ");
        sqlQuerySB.append(" AND TRIM(TRNFR.USER_ID) = '"+userId+"')) ");

        sqlQuerySB.append(" - ");

        sqlQuerySB.append(" ((SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION+" TRAN ");
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'EXPENSE' ");
        sqlQuerySB.append(" AND TRAN.ACC_ID = ACC.ACC_ID ");
        sqlQuerySB.append(" AND TRIM(TRAN.USER_ID) = '"+userId+"') ");

        sqlQuerySB.append(" + ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER+" TRNFR ");
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRNFR.ACC_ID_FRM = ACC.ACC_ID ");
        sqlQuerySB.append(" AND TRIM(TRNFR.USER_ID) = '"+userId+"')) ");

        sqlQuerySB.append(" AS ACC_TOTAL ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT+" ACC ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId+"','"+ADMIN_USERID+"') ");

        sqlQuerySB.append(" GROUP BY ACC.ACC_ID ");
        sqlQuerySB.append(" ORDER BY ACC.CREAT_DTM ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        String accountIdStr, accountNameStr, currecyStr, accountIsDefaultStr, accountImgStr;
        Double accountTotal;
        AccountMO accountsModel;
        while (cursor.moveToNext()){
            accountIdStr = ColumnFetcher.loadString(cursor, "ACC_ID");
            accountNameStr = ColumnFetcher.loadString(cursor, "ACC_NAME");
            accountTotal = ColumnFetcher.loadDouble(cursor, "ACC_TOTAL");
            accountIsDefaultStr = ColumnFetcher.loadString(cursor, "ACC_IS_DEF");
            accountImgStr = ColumnFetcher.loadString(cursor, "ACC_IMG");

            accountsModel = new AccountMO();
            accountsModel.setACC_ID(accountIdStr);
            accountsModel.setACC_NAME(accountNameStr);
            accountsModel.setACC_TOTAL(accountTotal);
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
