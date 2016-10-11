package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.ActivityModel;
import com.finappl.models.DayTransactionsModel;
import com.finappl.models.DayTransfersModel;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.finappl.utils.Constants.*;

public class ViewActivitiesDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

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
        int result = db.update(DB_TABLE_TRANSACTION, values,	"TRAN_ID = '" + transactionIdStr + "'", null);
        db.close();
        return result;
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
        sqlQuerySB.append(" SCH.CREAT_DTM  AS SCH_CREAT_DTM ");

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

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_USER + " USER ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.USER_ID = TRAN.USER_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN.TRAN_ID = '"+transIdStr+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.TRAN_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"'");

        Log.i(CLASS_NAME, "query to get transactions for transaction details popper : "+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        TransactionModel transactionModelObj = new TransactionModel();

        while (cursor.moveToNext()){
            transactionModelObj.setTRAN_NAME(ColumnFetcher.loadString(cursor, "TRAN_NAME"));
            transactionModelObj.setCategory(ColumnFetcher.loadString(cursor, "CAT_NAME"));
            transactionModelObj.setAccount(ColumnFetcher.loadString(cursor, "ACC_NAME"));
            transactionModelObj.setSpentOn(ColumnFetcher.loadString(cursor, "SPNT_ON_NAME"));
            transactionModelObj.setTRAN_AMT(ColumnFetcher.loadDouble(cursor, "TRAN_AMT"));
            transactionModelObj.setTRAN_DATE(ColumnFetcher.loadDate(cursor, "TRAN_DATE"));
            transactionModelObj.setTRAN_NOTE(ColumnFetcher.loadString(cursor, "TRAN_NOTE"));
            transactionModelObj.setTRAN_ID(ColumnFetcher.loadString(cursor, "TRAN_ID"));
            transactionModelObj.setTRAN_TYPE(ColumnFetcher.loadString(cursor, "TRAN_TYPE"));
            transactionModelObj.setCREAT_DTM(ColumnFetcher.loadDateTime(cursor, "TRAN_CREAT_DTM"));
            transactionModelObj.setMOD_DTM(ColumnFetcher.loadDateTime(cursor, "MOD_DTM"));
            transactionModelObj.setSchCreateDate(ColumnFetcher.loadDateTime(cursor, "SCH_CREAT_DTM"));
        }
        db.close();
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
        sqlQuerySB.append(" SCHTRF.CREAT_DTM AS SCH_CREAT_DTM ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER + " TFR ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_USER + " USER ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.USER_ID = TFR.USER_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " FRMACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" FRMACC.ACC_ID = TFR.ACC_ID_FRM ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " TOACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TOACC.ACC_ID = TFR.ACC_ID_TO ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TFR.TRNFR_ID = '"+transferIdStr+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TFR.TRNFR_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"'");

        Log.i(CLASS_NAME, "query to get transfer for transfers details popper : "+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        TransferModel transferModelObj = null;

        while (cursor.moveToNext()){
            transferModelObj = new TransferModel();
            transferModelObj.setTRNFR_ID(ColumnFetcher.loadString(cursor, "TRNFR_ID"));
            transferModelObj.setFromAccName(ColumnFetcher.loadString(cursor, "FROM_ACC_NAME"));
            transferModelObj.setToAccName(ColumnFetcher.loadString(cursor, "TO_ACC_NAME"));
            transferModelObj.setTRNFR_AMT(ColumnFetcher.loadDouble(cursor, "TRNFR_AMT"));
            transferModelObj.setTRNFR_DATE(ColumnFetcher.loadDate(cursor, "TRNFR_DATE"));
            transferModelObj.setTRNFR_NOTE(ColumnFetcher.loadString(cursor, "TRNFR_NOTE"));
            transferModelObj.setCREAT_DTM(ColumnFetcher.loadDateTime(cursor, "CREAT_DTM"));
            transferModelObj.setMOD_DTM(ColumnFetcher.loadDateTime(cursor, "MOD_DTM"));
            transferModelObj.setSchCreateDate(ColumnFetcher.loadDateTime(cursor, "SCH_CREAT_DTM"));
        }
        db.close();
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
        sqlQuerySB.append(" TRAN.SCH_TRAN_ID ");

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

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_USER + " USER ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.USER_ID = TRAN.USER_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN.TRAN_DATE >= '"+activityModelObj.getFromDate()+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.TRAN_DATE <= '"+activityModelObj.getToDate()+"'");

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

            transactionModelObj.setTRAN_NAME(ColumnFetcher.loadString(cursor, "TRAN_NAME"));
            transactionModelObj.setCategory(ColumnFetcher.loadString(cursor, "CAT_NAME"));
            transactionModelObj.setAccount(ColumnFetcher.loadString(cursor, "ACC_NAME"));
            transactionModelObj.setSpentOn(ColumnFetcher.loadString(cursor, "SPNT_ON_NAME"));
            transactionModelObj.setTRAN_AMT(ColumnFetcher.loadDouble(cursor, "TRAN_AMT"));
            transactionModelObj.setTRAN_DATE(ColumnFetcher.loadDate(cursor, "TRAN_DATE"));
            transactionModelObj.setTRAN_NOTE(ColumnFetcher.loadString(cursor, "TRAN_NOTE"));
            transactionModelObj.setTRAN_ID(ColumnFetcher.loadString(cursor, "TRAN_ID"));
            transactionModelObj.setTRAN_TYPE(ColumnFetcher.loadString(cursor, "TRAN_TYPE"));
            transactionModelObj.setSCH_TRAN_ID(ColumnFetcher.loadString(cursor, "SCH_TRAN_ID"));

            Double totAmt = 0.0;
            if(transactionsMap.containsKey(transactionModelObj.getTRAN_DATE())){
                dayTransactionsModel = transactionsMap.get(transactionModelObj.getTRAN_DATE());
                transactList = dayTransactionsModel.getDayTransactionsList();
                totAmt = dayTransactionsModel.getDayTotal();
            }
            else{
                transactList =  new ArrayList<>();
                dayTransactionsModel = new DayTransactionsModel();
                dayTransactionsModel.setDate(transactionModelObj.getTRAN_DATE());
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
            transactionsMap.put(simpleDateFormat.format(transactionModelObj.getTRAN_DATE()), dayTransactionsModel);
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
        sqlQuerySB.append(" TFR.SCH_TRNFR_ID ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER + " TFR ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_USER + " USER ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" USER.USER_ID = TFR.USER_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " FRMACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" FRMACC.ACC_ID = TFR.ACC_ID_FRM ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " TOACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TOACC.ACC_ID = TFR.ACC_ID_TO ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TFR.TRNFR_DATE >= '"+simpleDateFormat.format(activityModelObj.getFromDate())+"'");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TFR.TRNFR_DATE <= '"+simpleDateFormat.format(activityModelObj.getToDate())+"'");

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

            transferModelObj.setTRNFR_ID(ColumnFetcher.loadString(cursor, "TRNFR_ID"));
            transferModelObj.setFromAccName(ColumnFetcher.loadString(cursor, "FROM_ACC_NAME"));
            transferModelObj.setToAccName(ColumnFetcher.loadString(cursor, "TO_ACC_NAME"));
            transferModelObj.setTRNFR_AMT(ColumnFetcher.loadDouble(cursor, "TRNFR_AMT"));
            transferModelObj.setTRNFR_DATE(ColumnFetcher.loadDate(cursor, "TRNFR_DATE"));
            transferModelObj.setCurrency(ColumnFetcher.loadString(cursor, "SCH_TRNFR_ID"));

            Double totalAmt;

            if(transfersMap.containsKey(transferModelObj.getTRNFR_DATE())){
                dayTransfersModel = transfersMap.get(transferModelObj.getTRNFR_DATE());
                transfersList = dayTransfersModel.getDayTransfersList();
                totalAmt = dayTransfersModel.getDayTotal() + transferModelObj.getTRNFR_AMT();
            }
            else{
                transfersList = new ArrayList<>();
                dayTransfersModel = new DayTransfersModel();
                totalAmt = transferModelObj.getTRNFR_AMT();
                dayTransfersModel.setDate(transferModelObj.getTRNFR_DATE());
            }

            dayTransfersModel.setDayTotal(totalAmt);
            transfersList.add(transferModelObj);
            dayTransfersModel.setDayTransfersList(transfersList);
            transfersMap.put(simpleDateFormat.format(transferModelObj.getTRNFR_DATE()), dayTransfersModel);
        }
        //for transfers ends--

        //fill transfers & transactions map in object and return
        ActivityModel actsModelObj = new ActivityModel();
        actsModelObj.setTransactionsMap(transactionsMap);
        actsModelObj.setTransfersMap(transfersMap);
        db.close();
        return actsModelObj;
    }


    @Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	//constructors
	public ViewActivitiesDbService(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	//getters setters
}
