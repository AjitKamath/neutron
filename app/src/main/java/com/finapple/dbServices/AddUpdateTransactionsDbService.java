package com.finapple.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finapple.model.SpinnerModel;
import com.finapple.model.TransactionModel;
import com.finapple.util.ColumnFetcher;
import com.finapple.util.Constants;
import com.finapple.util.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddUpdateTransactionsDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    //db tables
    private static final String usersTable = Constants.DB_TABLE_USERSTABLE;
    private static final String accountTable = Constants.DB_TABLE_ACCOUNTTABLE;
    private static final String categoryTable = Constants.DB_TABLE_CATEGORYTABLE;
    private static final String spentOnTable = Constants.DB_TABLE_SPENTONTABLE;
    private static final String transactionTable = Constants.DB_TABLE_TRANSACTIONTABLE;
    private static final String scheduledTransactionsTable = Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE;
    private static final String budgetTable = Constants.DB_TABLE_BUDGETTABLE;

	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = 1;

    //	method to update an already created transaction.. returns 0 for fail, 1 for success
    public int updateOldTransaction(TransactionModel transactionModel){
		SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        values.put("CAT_ID", transactionModel.getCAT_ID());
        values.put("SPNT_ON_ID", transactionModel.getSPNT_ON_ID());
        values.put("ACC_ID", transactionModel.getACC_ID());
        values.put("TRAN_AMT", transactionModel.getTRAN_AMT());
        values.put("TRAN_NAME", transactionModel.getTRAN_NAME());
        values.put("TRAN_TYPE", transactionModel.getTRAN_TYPE());
        values.put("TRAN_NOTE", transactionModel.getTRAN_NOTE());
        values.put("TRAN_DATE", transactionModel.getTRAN_DATE());
        values.put("MOD_DTM", sdf.format(new Date()));

		// Updating an old Row
		return db.update(transactionTable, values,	"TRAN_ID = '" + transactionModel.getTRAN_ID() + "'", null);
    }

    //	method to add a new transaction..returns -1 on fail to add new transaction. row id on success
    public long addNewTransaction(TransactionModel transactionModel){
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

		values.put("TRAN_ID", IdGenerator.getInstance().generateUniqueId("TRAN"));
		values.put("USER_ID", transactionModel.getUSER_ID());
		values.put("CAT_ID", transactionModel.getCAT_ID());
		values.put("SPNT_ON_ID", transactionModel.getSPNT_ON_ID());
		values.put("ACC_ID", transactionModel.getACC_ID());
		values.put("SCH_TRAN_ID", transactionModel.getSCH_TRAN_ID());
		values.put("TRAN_AMT", transactionModel.getTRAN_AMT());
		values.put("TRAN_NAME", transactionModel.getTRAN_NAME());
		values.put("TRAN_TYPE", transactionModel.getTRAN_TYPE());
		values.put("TRAN_NOTE", transactionModel.getTRAN_NOTE());
		values.put("TRAN_DATE", transactionModel.getTRAN_DATE());
		values.put("TRAN_IS_DEL", Constants.DB_NONAFFIRMATIVE);
		values.put("CREAT_DTM", sdf.format(new Date()));
		values.put("MOD_DTM", "");

        long result = 0;
        try {
            // Inserting a new Row
            result =  db.insertOrThrow(transactionTable, null, values);
        }
        catch(Exception e){
            Log.e(CLASS_NAME, "Error while adding transaction:"+e);
        }
        return result;
    }

    public List<SpinnerModel> getAllCategories(String userId){
        List<SpinnerModel> catList = new ArrayList<SpinnerModel>();
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CAT_ID, ");
        sqlQuerySB.append(" CAT_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(categoryTable);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" CAT_IS_DEFAULT ");
        sqlQuerySB.append(" = ");
        sqlQuerySB.append(" '"+Constants.DB_AFFIRMATIVE+"' ");

        sqlQuerySB.append(" OR ");
        sqlQuerySB.append(" (USER_ID = '"+userId+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" CAT_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"') ");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" CAT_NAME ");
        sqlQuerySB.append(" ASC ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            SpinnerModel spnObj = new SpinnerModel();
            spnObj.setItemId(ColumnFetcher.getInstance().loadString(cursor, "CAT_ID"));
            spnObj.setItemName(ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME"));

            catList.add(spnObj);
        }
        cursor.close();

        return catList;
    }

    public List<SpinnerModel> getAllSpentOn(String userId){
        List<SpinnerModel> spntList = new ArrayList<SpinnerModel>();
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SPNT_ON_ID, ");
        sqlQuerySB.append(" SPNT_ON_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(spentOnTable);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" SPNT_ON_IS_DEFAULT = '"+Constants.DB_AFFIRMATIVE+"' ");
        sqlQuerySB.append(" OR ");
        sqlQuerySB.append(" (USER_ID = '"+userId+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" SPNT_ON_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"') ");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" SPNT_ON_NAME ");
        sqlQuerySB.append(" ASC ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            SpinnerModel spnObj = new SpinnerModel();
            spnObj.setItemId(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_ID"));
            spnObj.setItemName(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME"));

            spntList.add(spnObj);
        }
        cursor.close();

        return spntList;
    }
    //--------------------- end of method to get all spent on type--------------------------//

    //---------------------method to get all accounts--------------------------//
    public List<SpinnerModel> getAllAccounts(String userId){
        List<SpinnerModel> accList = new ArrayList<SpinnerModel>();
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" ACC_ID, ");
        sqlQuerySB.append(" ACC_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(accountTable);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_IS_DEFAULT = '"+Constants.DB_AFFIRMATIVE+"' ");
        sqlQuerySB.append(" OR ");
        sqlQuerySB.append(" (USER_ID = '"+userId+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"') ");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" ACC_NAME ");
        sqlQuerySB.append(" ASC ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            SpinnerModel spnObj = new SpinnerModel();
            spnObj.setItemId(ColumnFetcher.getInstance().loadString(cursor, "ACC_ID"));
            spnObj.setItemName(ColumnFetcher.getInstance().loadString(cursor, "ACC_NAME"));
            accList.add(spnObj);
        }
        cursor.close();

        return accList;
    }
    //--------------------- end of method to get all pay type--------------------------//

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	//constructors
	public AddUpdateTransactionsDbService(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	//getters setters
}
