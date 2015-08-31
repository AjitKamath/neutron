package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.BudgetModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;
import com.finappl.utils.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddUpdateBudgetsDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    //db tables
    private static final String USERS_TABLE = Constants.DB_TABLE_USERSTABLE;
    private static final String ACCOUNT_TABLE = Constants.DB_TABLE_ACCOUNTTABLE;
    private static final String CATEGORY_TABLE = Constants.DB_TABLE_CATEGORYTABLE;
    private static final String SPENT_ON_TABLE = Constants.DB_TABLE_SPENTONTABLE;
    private static final String TRANSACTION_TABLE = Constants.DB_TABLE_TRANSACTIONTABLE;
    private static final String SCHEDULED_TRANSACTIONS_TABLE = Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE;
    private static final String BUDGET_TABLE = Constants.DB_TABLE_BUDGETTABLE;

	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = Constants.DB_VERSION;

    //	method to update an already created budget.. returns 0 for fail, 1 for success
    public long updateOldBudget(BudgetModel budgetModel){
		SQLiteDatabase db = this.getWritableDatabase();

        //check whether a budget with same grp type and type exists
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(*) AS COUNT ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(BUDGET_TABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '"+budgetModel.getUSER_ID()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" BUDGET_IS_DEL = '"+ Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" BUDGET_GRP_ID = '"+budgetModel.getBUDGET_GRP_ID()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" BUDGET_TYPE = '"+budgetModel.getBUDGET_TYPE()+"'");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        int count = 0;

        while (cursor.moveToNext()){
            count = ColumnFetcher.getInstance().loadInt(cursor, "COUNT");
        }

        if(count > 0){
            return -2;
        }

        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        values.put("BUDGET_NAME", budgetModel.getBUDGET_NAME());
        values.put("BUDGET_GRP_ID", budgetModel.getBUDGET_GRP_ID());
        values.put("BUDGET_GRP_TYPE", budgetModel.getBUDGET_GRP_TYPE());
        values.put("BUDGET_TYPE", budgetModel.getBUDGET_TYPE());
        values.put("BUDGET_AMT", budgetModel.getBUDGET_AMT());
        values.put("BUDGET_NOTE", budgetModel.getBUDGET_NOTE());
        values.put("MOD_DTM", sdf.format(new Date()));

        // Updating an old Row
        return db.update(BUDGET_TABLE, values,	"BUDGET_ID = '" + budgetModel.getBUDGET_ID() + "'", null);
    }

    //	method to add a new budget..returns -1 on fail to add new budget. 1 on success
    public long addNewBudget(BudgetModel budgetModel){
		SQLiteDatabase db = this.getWritableDatabase();

        //check whether a budget with same grp type and type exists
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(*) AS COUNT ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(BUDGET_TABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '"+budgetModel.getUSER_ID()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" BUDGET_IS_DEL = '"+ Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" BUDGET_GRP_ID = '"+budgetModel.getBUDGET_GRP_ID()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" BUDGET_TYPE = '"+budgetModel.getBUDGET_TYPE()+"' ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        int count = 0;

        while (cursor.moveToNext()){
            count = ColumnFetcher.getInstance().loadInt(cursor, "COUNT");
        }

        if(count > 0){
            return -2;
        }

        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        values.put("BUDGET_ID", IdGenerator.getInstance().generateUniqueId("BUD"));
		values.put("USER_ID", budgetModel.getUSER_ID());
		values.put("BUDGET_NAME", budgetModel.getBUDGET_NAME());
		values.put("BUDGET_GRP_ID", budgetModel.getBUDGET_GRP_ID());
		values.put("BUDGET_GRP_TYPE", budgetModel.getBUDGET_GRP_TYPE());
		values.put("BUDGET_TYPE", budgetModel.getBUDGET_TYPE());
		values.put("BUDGET_IS_DEL", Constants.DB_NONAFFIRMATIVE);
		values.put("BUDGET_AMT", budgetModel.getBUDGET_AMT());
		values.put("BUDGET_NOTE", budgetModel.getBUDGET_NOTE());
		values.put("CREAT_DTM", sdf.format(new Date()));
		values.put("MOD_DTM", "");

		// Inserting a new Row
		long result =  db.insert(BUDGET_TABLE, null, values);

        //do not continue if insert failed
        if(result == -1){
            Log.e(CLASS_NAME, "New budget couldnt be saved in db");
        }
        return result;
    }

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	//constructors
	public AddUpdateBudgetsDbService(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
