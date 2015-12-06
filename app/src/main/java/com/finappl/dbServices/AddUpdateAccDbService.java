package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.AccountsModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;
import com.finappl.utils.DateTimeUtil;
import com.finappl.utils.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AddUpdateAccDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();

	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = Constants.DB_VERSION;
	private static AddUpdateAccDbService sInstance = null;

    public AccountsModel getAccountDetailsOnAccountId(String accountIdStr) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" ACC_NAME, ");
        sqlQuerySB.append(" ACC_NOTE, ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(ACC_TOTAL), '0')  ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_ACCOUNTTABLE);
        sqlQuerySB.append(" WHERE  ");
        sqlQuerySB.append(" ACC_ID = '"+accountIdStr+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_IS_DEL  =  '"+Constants.DB_NONAFFIRMATIVE+"') ");

        sqlQuerySB.append(" + ");

        sqlQuerySB.append(" (( SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0')  ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_TRANSACTIONTABLE);
        sqlQuerySB.append(" WHERE  ");
        sqlQuerySB.append(" TRAN_TYPE = 'INCOME'  ");
        sqlQuerySB.append(" AND  ");
        sqlQuerySB.append(" ACC_ID = '"+accountIdStr+"'  ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN_IS_DEL  =  '"+Constants.DB_NONAFFIRMATIVE+"') ");

        sqlQuerySB.append(" + ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_TRANSFERSTABLE);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID_TO = '"+accountIdStr+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRNFR_IS_DEL  =  '"+Constants.DB_NONAFFIRMATIVE+"')) ");

        sqlQuerySB.append(" - ");

        sqlQuerySB.append(" ((SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_TRANSACTIONTABLE);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'EXPENSE' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_ID = '"+accountIdStr+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN_IS_DEL  =  '"+Constants.DB_NONAFFIRMATIVE+"') ");

        sqlQuerySB.append(" + ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_TRANSFERSTABLE);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID_FRM = '"+accountIdStr+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRNFR_IS_DEL  =  '"+Constants.DB_NONAFFIRMATIVE+"')) ");

        sqlQuerySB.append(" AS ");
        sqlQuerySB.append(" ACC_TOTAL ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_ACCOUNTTABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID  = '" + accountIdStr + "' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_IS_DEL = '" + Constants.DB_NONAFFIRMATIVE + "'");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        AccountsModel accountsModelObj = null;
        if(cursor.moveToNext()) {
            accountsModelObj = new AccountsModel();
            accountsModelObj.setACC_NAME(ColumnFetcher.getInstance().loadString(cursor, "ACC_NAME"));
            accountsModelObj.setACC_NOTE(ColumnFetcher.getInstance().loadString(cursor, "ACC_NOTE"));
            accountsModelObj.setACC_TOTAL(ColumnFetcher.getInstance().loadDouble(cursor, "ACC_TOTAL"));
            accountsModelObj.setACC_ID(accountIdStr);

            return accountsModelObj;
        }

        Log.e(CLASS_NAME, "If this is printed, Something went wrong while fetching Account details from db");
        return null;
    }

    public int updateOldAccount(AccountsModel accObj) {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        ContentValues values = new ContentValues();
        values.put("ACC_NAME", accObj.getACC_NAME());
        values.put("ACC_NOTE", accObj.getACC_NOTE());
        values.put("MOD_DTM", sdf.format(new Date()));

        // Updating an old Row
        return db.update(Constants.DB_TABLE_ACCOUNTTABLE, values,	"ACC_ID = '" + accObj.getACC_ID() + "'", null);
    }

	public long addNewAccount(AccountsModel accObject){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(*) AS COUNT ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_ACCOUNTTABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_NAME ");
        sqlQuerySB.append(" = ");
        sqlQuerySB.append(" '"+accObject.getACC_NAME()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"'");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" USER_ID = '"+accObject.getUSER_ID()+"'");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        int count = 0;
        while (cursor.moveToNext()){
            //	get account count
            count = ColumnFetcher.getInstance().loadInt(cursor, "COUNT");
        }

        if(count > 0){
            return -2;
        }

        ContentValues values = new ContentValues();

        String accIdStr = IdGenerator.getInstance().generateUniqueId("ACC");

        values.put("ACC_ID", accIdStr);
        values.put("USER_ID", accObject.getUSER_ID());
        values.put("ACC_NAME", accObject.getACC_NAME());
        values.put("ACC_IS_DEFAULT", Constants.DB_NONAFFIRMATIVE);
        values.put("ACC_NOTE", accObject.getACC_NOTE());
        values.put("ACC_IS_DEL", Constants.DB_NONAFFIRMATIVE);
        values.put("CREAT_DTM", DateTimeUtil.getInstance().dateDateToDbDateString(new Date()));

        // Inserting a new Row in account table
        long result =  db.insert(Constants.DB_TABLE_ACCOUNTTABLE, null, values);

        //if result is not -1 insertion failed
        if(result == -1){
            Log.e(CLASS_NAME, "Aye Aye Android developer !! A simple insert operation failed !! You shalt die now !");
            return result;
        }

        //if the initial amount is 0, no need of creating a auto transaction
        if(accObject.getInitialAmount() == 0.0){
            return result;
        }

        //add a transaction saying initial deposit on this particular account
        values.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");

        values.put("TRAN_ID", IdGenerator.getInstance().generateUniqueId("TRAN"));
        values.put("USER_ID", accObject.getUSER_ID());
        values.put("CAT_ID", "CAT_6");
        values.put("SPNT_ON_ID", "SPNT_1");
        values.put("ACC_ID", accIdStr);
        values.put("SCH_TRAN_ID", "");
        values.put("TRAN_AMT", accObject.getInitialAmount());
        values.put("TRAN_NAME", "New Account Created");
        values.put("TRAN_TYPE", "INCOME");
        values.put("TRAN_NOTE", "Auto Transaction For Initial Deposit");
        values.put("TRAN_DATE", sdf1.format(new Date()));
        values.put("TRAN_IS_DEL", Constants.DB_NONAFFIRMATIVE);
        values.put("CREAT_DTM", sdf.format(new Date()));

        long result2 = 0;
        try {
            // Inserting a new Row
            result2 =  db.insertOrThrow(Constants.DB_TABLE_TRANSACTIONTABLE, null, values);
        }
        catch(Exception e){
            Log.e(CLASS_NAME, "Error while adding transaction after creating account:"+e);
        }
        return result2;
    }


	@Override
	public void onCreate(SQLiteDatabase db) {
        //unnecessary
    }

	// get class instance
	public static AddUpdateAccDbService getInstance(Context context)
	{
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null){
			sInstance = new AddUpdateAccDbService(context.getApplicationContext());
		}
		return sInstance;
	}

	// constructors
	public AddUpdateAccDbService(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        //unnecessary
    }
}
