package com.finapple.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finapple.model.SpentOnModel;
import com.finapple.util.ColumnFetcher;
import com.finapple.util.Constants;
import com.finapple.util.DateTimeUtil;
import com.finapple.util.IdGenerator;

import java.util.Date;


public class AddUpdateSpentOnDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();

	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = 1;
	private static AddUpdateSpentOnDbService sInstance = null;

	//db tables
    private static final String USERS_TABLE = Constants.DB_TABLE_USERSTABLE;
    private static final String ACCOUNT_TABLE = Constants.DB_TABLE_ACCOUNTTABLE;
    private static final String CATEGORY_TABLE = Constants.DB_TABLE_CATEGORYTABLE;
    private static final String SPENT_ON_TABLE = Constants.DB_TABLE_SPENTONTABLE;
    private static final String TRANSACTION_TABLE = Constants.DB_TABLE_TRANSACTIONTABLE;
    private static final String SCHEDULED_TRANSACTION_TABLE = Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE;
    private static final String BUDGET_TABLE = Constants.DB_TABLE_BUDGETTABLE;
    private static final String CATEGORY_TAGS_TABLE = Constants.DB_TABLE_CATEGORYTAGSTABLE;

	public long addNewSpentOn(SpentOnModel spntOnObject){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(*) AS COUNT ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(SPENT_ON_TABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" SPNT_ON_NAME ");
        sqlQuerySB.append(" = ");
        sqlQuerySB.append(" '"+spntOnObject.getSPNT_ON_NAME()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" SPNT_ON_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"'");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" USER_ID = '"+spntOnObject.getUSER_ID()+"'");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        int count = 0;
        while (cursor.moveToNext())
        {
            //	get account count
            count = ColumnFetcher.getInstance().loadInt(cursor, "COUNT");
        }

        if(count > 0){
            return -2;
        }

        ContentValues values = new ContentValues();

        String spntOnIdStr = IdGenerator.getInstance().generateUniqueId("SPNT");

        values.put("SPNT_ON_ID", spntOnIdStr);
        values.put("USER_ID", spntOnObject.getUSER_ID());
        values.put("SPNT_ON_NAME", spntOnObject.getSPNT_ON_NAME());
        values.put("SPNT_ON_IS_DEFAULT", Constants.DB_NONAFFIRMATIVE);
        values.put("SPNT_ON_NOTE", spntOnObject.getSPNT_ON_NOTE());
        values.put("SPNT_ON_IS_DEL", Constants.DB_NONAFFIRMATIVE);
        values.put("CREAT_DTM", DateTimeUtil.getInstance().dateDateToDbDateString(new Date()));

        // Inserting a new Row in spent on table
        long result =  db.insert(SPENT_ON_TABLE, null, values);

        //if result is not -1 insertion failed
        if(result == -1){
            Log.e(CLASS_NAME, "Aye Aye Android developer !! A simple insert operation failed !! You shalt die now !");
            return result;
        }
        return result;
    }


	@Override
	public void onCreate(SQLiteDatabase db) {
        //unnecessary
    }

	// get class instance
	public static AddUpdateSpentOnDbService getInstance(Context context)
	{
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null)
		{
			sInstance = new AddUpdateSpentOnDbService(context.getApplicationContext());
		}
		return sInstance;
	}

	// constructors
	public AddUpdateSpentOnDbService(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        //unnecessary
    }

}
