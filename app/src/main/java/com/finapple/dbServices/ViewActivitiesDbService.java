package com.finapple.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finapple.model.ActivityModel;
import com.finapple.model.DayTransactionsModel;
import com.finapple.model.DayTransfersModel;
import com.finapple.model.TransactionModel;
import com.finapple.model.TransferModel;
import com.finapple.util.ColumnFetcher;
import com.finapple.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewActivitiesDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    //db tables
    private static final String USERS_TABLE = Constants.DB_TABLE_USERSTABLE;
    private static final String ACCOUNT_TABLE = Constants.DB_TABLE_ACCOUNTTABLE;
    private static final String CATEGORY_TABLE = Constants.DB_TABLE_CATEGORYTABLE;
    private static final String SPENT_ON_TABLE = Constants.DB_TABLE_SPENTONTABLE;
    private static final String TRANSACTION_TABLE = Constants.DB_TABLE_TRANSACTIONTABLE;
    private static final String SCHEDULED_TRANSACTIONS_TABLE = Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE;
    private static final String SCHEDULED_TRANSFER_TABLE = Constants.DB_TABLE_SHEDULEDTRANSFERSTABLE;
    private static final String BUDGET_TABLE = Constants.DB_TABLE_BUDGETTABLE;
    private static final String TRANSFERS_TABLE = Constants.DB_TABLE_TRANSFERSTABLE;
    private static final String CURRENCY_TABLE = Constants.DB_TABLE_CURRENCYTABLE;
    private static final String COUNTRY_TABLE = Constants.DB_TABLE_COUNTRYTABLE;
	
	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = 1;

    //method to soft delete a transaction
    public int deleteTransaction(String transactionIdStr){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        values.put("TRAN_IS_DEL", Constants.DB_AFFIRMATIVE);
        values.put("MOD_DTM", sdf.format(new Date()));

        Log.i(CLASS_NAME, "The user dude is trying to delete a transaction... hope that he's doing it to delete a faulty transaction.. Deleting something from your app doesnt " +
                    "serve the purpose of this app.. hope you know what i mean. Peace.");

        // Updating an old Row
        return db.update(TRANSACTION_TABLE, values,	"TRAN_ID = '" + transactionIdStr + "'", null);
    }

    public TransactionModel getTransactionOnTransactionID(String transIdStr){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

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
        sqlQuerySB.append(" TRAN.CREAT_DTM AS TRAN_CREAT_DTM, ");
        sqlQuerySB.append(" TRAN.MOD_DTM, ");
        sqlQuerySB.append(" SCH.CREAT_DTM  AS SCH_CREAT_DTM, ");
        sqlQuerySB.append(" CUR.CUR_NAME ");

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

        sqlQuerySB.append(" LEFT OUTER JOIN ");
        sqlQuerySB.append(SCHEDULED_TRANSACTIONS_TABLE + " SCH ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" SCH.SCH_TRAN_ID = TRAN.SCH_TRAN_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(USERS_TABLE + " USER ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.USER_ID = TRAN.USER_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(CURRENCY_TABLE + " CUR ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.CUR_ID = CUR.CUR_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN.TRAN_ID = '"+transIdStr+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.TRAN_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"'");

        Log.i(CLASS_NAME, "query to get transactions for transaction details popper : "+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        TransactionModel transactionModelObj = new TransactionModel();

        while (cursor.moveToNext()){
            transactionModelObj.setTRAN_NAME(ColumnFetcher.getInstance().loadString(cursor, "TRAN_NAME"));
            transactionModelObj.setCategory(ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME"));
            transactionModelObj.setAccount(ColumnFetcher.getInstance().loadString(cursor, "ACC_NAME"));
            transactionModelObj.setSpentOn(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME"));
            transactionModelObj.setTRAN_AMT(ColumnFetcher.getInstance().loadDouble(cursor, "TRAN_AMT"));
            transactionModelObj.setTRAN_DATE(ColumnFetcher.getInstance().loadString(cursor, "TRAN_DATE"));
            transactionModelObj.setTRAN_NOTE(ColumnFetcher.getInstance().loadString(cursor, "TRAN_NOTE"));
            transactionModelObj.setTRAN_ID(ColumnFetcher.getInstance().loadString(cursor, "TRAN_ID"));
            transactionModelObj.setTRAN_TYPE(ColumnFetcher.getInstance().loadString(cursor, "TRAN_TYPE"));
            transactionModelObj.setCurrency(ColumnFetcher.getInstance().loadString(cursor, "CUR_NAME"));
            transactionModelObj.setCREAT_DTM(ColumnFetcher.getInstance().loadString(cursor, "TRAN_CREAT_DTM"));
            transactionModelObj.setMOD_DTM(ColumnFetcher.getInstance().loadString(cursor, "MOD_DTM"));
            transactionModelObj.setSchCreateDate(ColumnFetcher.getInstance().loadString(cursor, "SCH_CREAT_DTM"));
        }
        return transactionModelObj;
    }

    public TransferModel getTransferOnTransferID(String transferIdStr){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" FRMACC.ACC_NAME AS FROM_ACC_NAME, ");
        sqlQuerySB.append(" TOACC.ACC_NAME AS TO_ACC_NAME, ");
        sqlQuerySB.append(" TFR.TRNFR_ID, ");
        sqlQuerySB.append(" TFR.TRNFR_AMT, ");
        sqlQuerySB.append(" TFR.TRNFR_NOTE, ");
        sqlQuerySB.append(" TFR.TRNFR_DATE, ");
        sqlQuerySB.append(" TFR.CREAT_DTM, ");
        sqlQuerySB.append(" TFR.MOD_DTM, ");
        sqlQuerySB.append(" SCHTRF.CREAT_DTM AS SCH_CREAT_DTM, ");
        sqlQuerySB.append(" CUR.CUR_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSFERS_TABLE + " TFR ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(USERS_TABLE + " USER ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.USER_ID = TFR.USER_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE + " FRMACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" FRMACC.ACC_ID = TFR.ACC_ID_FRM ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE + " TOACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TOACC.ACC_ID = TFR.ACC_ID_TO ");

        sqlQuerySB.append(" LEFT OUTER JOIN ");
        sqlQuerySB.append(SCHEDULED_TRANSFER_TABLE + " SCHTRF ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" SCHTRF.SCH_TRNFR_ID = TFR.SCH_TRNFR_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(CURRENCY_TABLE + " CUR ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.CUR_ID = CUR.CUR_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TFR.TRNFR_ID = '"+transferIdStr+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TFR.TRNFR_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"'");

        Log.i(CLASS_NAME, "query to get transfer for transfers details popper : "+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        TransferModel transferModelObj = null;

        while (cursor.moveToNext()){
            transferModelObj = new TransferModel();
            transferModelObj.setTRNFR_ID(ColumnFetcher.getInstance().loadString(cursor, "TRNFR_ID"));
            transferModelObj.setFromAccName(ColumnFetcher.getInstance().loadString(cursor, "FROM_ACC_NAME"));
            transferModelObj.setToAccName(ColumnFetcher.getInstance().loadString(cursor, "TO_ACC_NAME"));
            transferModelObj.setTRNFR_AMT(ColumnFetcher.getInstance().loadDouble(cursor, "TRNFR_AMT"));
            transferModelObj.setTRNFR_DATE(ColumnFetcher.getInstance().loadString(cursor, "TRNFR_DATE"));
            transferModelObj.setCurrency(ColumnFetcher.getInstance().loadString(cursor, "CUR_NAME"));
            transferModelObj.setTRNFR_NOTE(ColumnFetcher.getInstance().loadString(cursor, "TRNFR_NOTE"));
            transferModelObj.setCREAT_DTM(ColumnFetcher.getInstance().loadString(cursor, "CREAT_DTM"));
            transferModelObj.setMOD_DTM(ColumnFetcher.getInstance().loadString(cursor, "MOD_DTM"));
            transferModelObj.setSchCreateDate(ColumnFetcher.getInstance().loadString(cursor, "SCH_CREAT_DTM"));
        }
        return transferModelObj;
    }

    public ActivityModel getActivitesForDate(ActivityModel activityModelObj){
        SQLiteDatabase db = this.getWritableDatabase();

        //for transactions
        StringBuilder sqlQuerySB = new StringBuilder(50);

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
        sqlQuerySB.append(" TRAN.SCH_TRAN_ID, ");
        sqlQuerySB.append(" CUR.CUR_NAME ");

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

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(USERS_TABLE + " USER ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.USER_ID = TRAN.USER_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(CURRENCY_TABLE + " CUR ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.CUR_ID = CUR.CUR_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN.TRAN_DATE >= '"+activityModelObj.getFromDateStr()+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.TRAN_DATE <= '"+activityModelObj.getToDateStr()+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.TRAN_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.USER_ID = '"+activityModelObj.getUserId()+"'");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" TRAN.CREAT_DTM DESC ");

        Log.i(CLASS_NAME, "query to get transactions for view activities: "+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        Map<String, DayTransactionsModel> transactionsMap = new HashMap<>();
        TransactionModel transactionModelObj = null;
        List<TransactionModel> transactList = null;
        DayTransactionsModel dayTransactionsModel = null;
        while (cursor.moveToNext()){
            transactionModelObj = new TransactionModel();

            transactionModelObj.setTRAN_NAME(ColumnFetcher.getInstance().loadString(cursor, "TRAN_NAME"));
            transactionModelObj.setCategory(ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME"));
            transactionModelObj.setAccount(ColumnFetcher.getInstance().loadString(cursor, "ACC_NAME"));
            transactionModelObj.setSpentOn(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME"));
            transactionModelObj.setTRAN_AMT(ColumnFetcher.getInstance().loadDouble(cursor, "TRAN_AMT"));
            transactionModelObj.setTRAN_DATE(ColumnFetcher.getInstance().loadString(cursor, "TRAN_DATE"));
            transactionModelObj.setTRAN_NOTE(ColumnFetcher.getInstance().loadString(cursor, "TRAN_NOTE"));
            transactionModelObj.setTRAN_ID(ColumnFetcher.getInstance().loadString(cursor, "TRAN_ID"));
            transactionModelObj.setTRAN_TYPE(ColumnFetcher.getInstance().loadString(cursor, "TRAN_TYPE"));
            transactionModelObj.setCurrency(ColumnFetcher.getInstance().loadString(cursor, "CUR_NAME"));
            transactionModelObj.setSCH_TRAN_ID(ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_ID"));

            Double totAmt = 0.0;
            if(transactionsMap.containsKey(transactionModelObj.getTRAN_DATE())){
                dayTransactionsModel = transactionsMap.get(transactionModelObj.getTRAN_DATE());
                transactList = dayTransactionsModel.getDayTransactionsList();
                totAmt = dayTransactionsModel.getDayTotal();
            }
            else{
                transactList =  new ArrayList<>();
                dayTransactionsModel = new DayTransactionsModel();
                dayTransactionsModel.setDateStr(transactionModelObj.getTRAN_DATE());
                totAmt = 0.0;
            }

            if("EXPENSE".equalsIgnoreCase(transactionModelObj.getTRAN_TYPE())){
                totAmt -= transactionModelObj.getTRAN_AMT();
            }
            else{
                totAmt += transactionModelObj.getTRAN_AMT();
            }

            dayTransactionsModel.setDayTotal(totAmt);
            transactList.add(transactionModelObj);
            dayTransactionsModel.setDayTransactionsList(transactList);
            transactionsMap.put(transactionModelObj.getTRAN_DATE(), dayTransactionsModel);
        }

        //for transactions ends--

        //for transfers
        sqlQuerySB = null;
        sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" TFR.TRNFR_ID, ");
        sqlQuerySB.append(" FRMACC.ACC_NAME AS FROM_ACC_NAME, ");
        sqlQuerySB.append(" TOACC.ACC_NAME AS TO_ACC_NAME, ");
        sqlQuerySB.append(" TFR.TRNFR_AMT, ");
        sqlQuerySB.append(" TFR.TRNFR_DATE, ");
        sqlQuerySB.append(" TFR.SCH_TRNFR_ID, ");
        sqlQuerySB.append(" CUR.CUR_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSFERS_TABLE + " TFR ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(USERS_TABLE + " USER ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.USER_ID = TFR.USER_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE + " FRMACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" FRMACC.ACC_ID = TFR.ACC_ID_FRM ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE + " TOACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TOACC.ACC_ID = TFR.ACC_ID_TO ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(CURRENCY_TABLE + " CUR ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.CUR_ID = CUR.CUR_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TFR.TRNFR_DATE >= '"+activityModelObj.getFromDateStr()+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TFR.TRNFR_DATE <= '"+activityModelObj.getToDateStr()+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TFR.TRNFR_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TFR.USER_ID = '"+activityModelObj.getUserId()+"'");

        Log.i(CLASS_NAME, "query to get transfers for view activity page : " + sqlQuerySB);
        cursor = db.rawQuery(sqlQuerySB.toString(), null);

        TransferModel transferModelObj = null;
        List<TransferModel> transfersList = null;
        Map<String, DayTransfersModel> transfersMap = new HashMap<String, DayTransfersModel>();
        DayTransfersModel dayTransfersModel = null;

        while (cursor.moveToNext()){
            transferModelObj = new TransferModel();

            transferModelObj.setTRNFR_ID(ColumnFetcher.getInstance().loadString(cursor, "TRNFR_ID"));
            transferModelObj.setFromAccName(ColumnFetcher.getInstance().loadString(cursor, "FROM_ACC_NAME"));
            transferModelObj.setToAccName(ColumnFetcher.getInstance().loadString(cursor, "TO_ACC_NAME"));
            transferModelObj.setTRNFR_AMT(ColumnFetcher.getInstance().loadDouble(cursor, "TRNFR_AMT"));
            transferModelObj.setTRNFR_DATE(ColumnFetcher.getInstance().loadString(cursor, "TRNFR_DATE"));
            transferModelObj.setCurrency(ColumnFetcher.getInstance().loadString(cursor, "CUR_NAME"));
            transferModelObj.setCurrency(ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_ID"));

            Double totalAmt = 0.0;

            if(transfersMap.containsKey(transferModelObj.getTRNFR_DATE())){
                dayTransfersModel = transfersMap.get(transferModelObj.getTRNFR_DATE());
                transfersList = dayTransfersModel.getDayTransfersList();
                totalAmt = dayTransfersModel.getDayTotal() + transferModelObj.getTRNFR_AMT();
            }
            else{
                transfersList = new ArrayList<TransferModel>();
                dayTransfersModel = new DayTransfersModel();
                totalAmt = transferModelObj.getTRNFR_AMT();
                dayTransfersModel.setDateStr(transferModelObj.getTRNFR_DATE());
            }

            dayTransfersModel.setDayTotal(totalAmt);
            transfersList.add(transferModelObj);
            dayTransfersModel.setDayTransfersList(transfersList);
            transfersMap.put(transferModelObj.getTRNFR_DATE(), dayTransfersModel);
        }
        //for transfers ends--

        //fill transfers & transactions map in object and return
        ActivityModel actsModelObj = new ActivityModel();
        actsModelObj.setTransactionsMap(transactionsMap);
        actsModelObj.setTransfersMap(transfersMap);

        return actsModelObj;
    }


    @Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	//constructors
	public ViewActivitiesDbService(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	//getters setters
}
