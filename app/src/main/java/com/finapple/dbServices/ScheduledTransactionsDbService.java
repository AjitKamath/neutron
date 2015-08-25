package com.finapple.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finapple.model.CountryModel;
import com.finapple.model.CurrencyModel;
import com.finapple.model.ScheduledTransactionModel;
import com.finapple.model.UsersModel;
import com.finapple.util.ColumnFetcher;
import com.finapple.util.Constants;
import com.finapple.util.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduledTransactionsDbService extends SQLiteOpenHelper {

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
    private static final String WORK_TIMELINE_TABLE = Constants.DB_TABLE_WORK_TIMELINETABLE;

	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = 1;

    //this method creates a new transaction based on the users input
    public Long createNewScheduledTransaction(ScheduledTransactionModel scheduledTransactionModelObj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        values.put("SCH_TRAN_ID", IdGenerator.getInstance().generateUniqueId("SCH_TRAN"));
        values.put("USER_ID", scheduledTransactionModelObj.getUSER_ID());
        values.put("SCH_TRAN_CAT_ID", scheduledTransactionModelObj.getSCH_TRAN_CAT_ID());
        values.put("SCH_TRAN_SPNT_ON_ID", scheduledTransactionModelObj.getSCH_TRAN_SPNT_ON_ID());
        values.put("SCH_TRAN_ACC_ID", scheduledTransactionModelObj.getSCH_TRAN_ACC_ID());
        values.put("SCH_TRAN_NAME", scheduledTransactionModelObj.getSCH_TRAN_NAME());
        values.put("SCH_TRAN_DATE", scheduledTransactionModelObj.getSCH_TRAN_DATE());
        values.put("SCH_TRAN_FREQ", scheduledTransactionModelObj.getSCH_TRAN_FREQ());
        values.put("SCH_TRAN_TYPE", scheduledTransactionModelObj.getSCH_TRAN_TYPE());
        values.put("SCH_TRAN_AMT", String.valueOf(scheduledTransactionModelObj.getSCH_TRAN_AMT()));
        values.put("SCH_TRAN_NOTE", scheduledTransactionModelObj.getSCH_TRAN_NOTE());
        values.put("SCH_TRAN_AUTO", scheduledTransactionModelObj.getSCH_TRAN_AUTO());
        values.put("SCH_TRAN_IS_DEL", Constants.DB_NONAFFIRMATIVE);
        values.put("CREAT_DTM", sdf.format(new Date()));

        // inserting a new row
        return db.insert(SCHEDULED_TRANSACTIONS_TABLE, null, values);
    }

    //Gets the Scheduled Transaction using the Scheduled Transaction Id & User Id and returns back the ScheduledTransactionModel object
    public ScheduledTransactionModel getScheduledTransactionOnScheduledTransactionId(ScheduledTransactionModel scheduledTransactionModelObj){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SCH_TRAN_DATE, ");
        sqlQuerySB.append(" SCH_TRAN_FREQ, ");
        sqlQuerySB.append(" SCH_TRAN_TYPE, ");
        sqlQuerySB.append(" SCH_TRAN_AMT, ");
        sqlQuerySB.append(" SCH_TRAN_NOTE, ");
        sqlQuerySB.append(" SCH_TRAN_AUTO, ");
        sqlQuerySB.append(" SCH.CREAT_DTM, ");
        sqlQuerySB.append(" SCH.MOD_DTM, ");
        sqlQuerySB.append(" CAT.CAT_NAME, ");
        sqlQuerySB.append(" ACC.ACC_NAME, ");
        sqlQuerySB.append(" SPNT.SPNT_ON_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(SCHEDULED_TRANSACTIONS_TABLE + " SCH ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(CATEGORY_TABLE+" CAT ");
        sqlQuerySB.append(" ON CAT.CAT_ID = SCH.SCH_TRAN_CAT_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(ACCOUNT_TABLE+" ACC ");
        sqlQuerySB.append(" ON ACC.ACC_ID = SCH.SCH_TRAN_ACC_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(SPENT_ON_TABLE+" SPNT ");
        sqlQuerySB.append(" ON SPNT.SPNT_ON_ID = SCH.SCH_TRAN_SPNT_ON_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" SCH.USER_ID = '"+scheduledTransactionModelObj.getUSER_ID()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" SCH_TRAN_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" SCH_TRAN_ID = '"+scheduledTransactionModelObj.getSCH_TRAN_ID()+"' ");

        Log.i(CLASS_NAME, "Query to fetch Scheduled Transactions using Scheduled Transaction ID  :"+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            String schTranDateStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_DATE");
            String schTranFreq = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_FREQ");
            String schTranTypeStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_TYPE");
            Double schTranAmt = ColumnFetcher.getInstance().loadDouble(cursor, "SCH_TRAN_AMT");
            String schTranNoteStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_NOTE");
            String schTranAutoStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRAN_AUTO");
            String schTranCreateDtmStr = ColumnFetcher.getInstance().loadString(cursor, "CREAT_DTM");
            String schTranModDtmStr = ColumnFetcher.getInstance().loadString(cursor, "MOD_DTM");
            String schTranCatStr = ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME");
            String schTranAccStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_NAME");
            String schTranSpntOnStr = ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME");

            scheduledTransactionModelObj.setCREAT_DTM(schTranCreateDtmStr);
            scheduledTransactionModelObj.setMOD_DTM(schTranModDtmStr);
            scheduledTransactionModelObj.setSCH_TRAN_AMT(schTranAmt);
            scheduledTransactionModelObj.setSCH_TRAN_AUTO(schTranAutoStr);
            scheduledTransactionModelObj.setSCH_TRAN_DATE(schTranDateStr);
            scheduledTransactionModelObj.setSCH_TRAN_FREQ(schTranFreq);
            scheduledTransactionModelObj.setSCH_TRAN_TYPE(schTranTypeStr);
            scheduledTransactionModelObj.setSCH_TRAN_NOTE(schTranNoteStr);
            scheduledTransactionModelObj.setAccountNameStr(schTranAccStr);
            scheduledTransactionModelObj.setCategoryNameStr(schTranCatStr);
            scheduledTransactionModelObj.setSpentOnNameStr(schTranSpntOnStr);

            return scheduledTransactionModelObj;
        }
        cursor.close();

        Log.e(CLASS_NAME, "If i'm printing, you must have screwed up. scheduledTransactionModelObj shouldnt be null at this point");
        return null;
    }

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	public ScheduledTransactionsDbService(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
