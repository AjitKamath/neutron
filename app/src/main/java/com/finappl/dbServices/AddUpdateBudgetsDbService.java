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

import static com.finappl.utils.Constants.DB_DATE_FORMAT;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_TABLE_BUDGET;
import static com.finappl.utils.Constants.DB_VERSION;

public class AddUpdateBudgetsDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

    //	method to update an already created budget.. returns 0 for fail, 1 for success
    public long updateOldBudget(BudgetModel budgetModel){
		SQLiteDatabase db = this.getWritableDatabase();

        //check whether a budget with same grp type and type exists
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(*) AS COUNT ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_BUDGET);

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

        values.put("BUDGET_NAME", budgetModel.getBUDGET_NAME());
        values.put("BUDGET_GRP_ID", budgetModel.getBUDGET_GRP_ID());
        values.put("BUDGET_GRP_TYPE", budgetModel.getBUDGET_GRP_TYPE());
        values.put("BUDGET_TYPE", budgetModel.getBUDGET_TYPE());
        values.put("BUDGET_AMT", budgetModel.getBUDGET_AMT());
        values.put("BUDGET_NOTE", budgetModel.getBUDGET_NOTE());
        values.put("MOD_DTM", simpleDateTimeFormat.format(new Date()));

        // Updating an old Row
        int result = db.update(DB_TABLE_BUDGET, values,	"BUDGET_ID = '" + budgetModel.getBUDGET_ID() + "'", null);
        db.close();
        return result;
    }

    //	method to add a new budget..returns -1 on fail to add new budget. 1 on success
    public long addNewBudget(BudgetModel budgetModel){
		SQLiteDatabase db = this.getWritableDatabase();

        //check whether a budget with same grp type and type exists
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(*) AS COUNT ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_BUDGET);

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

        values.put("BUDGET_ID", IdGenerator.getInstance().generateUniqueId("BUD"));
		values.put("USER_ID", budgetModel.getUSER_ID());
		values.put("BUDGET_NAME", budgetModel.getBUDGET_NAME());
		values.put("BUDGET_GRP_ID", budgetModel.getBUDGET_GRP_ID());
		values.put("BUDGET_GRP_TYPE", budgetModel.getBUDGET_GRP_TYPE());
		values.put("BUDGET_TYPE", budgetModel.getBUDGET_TYPE());
		values.put("BUDGET_IS_DEL", Constants.DB_NONAFFIRMATIVE);
		values.put("BUDGET_AMT", budgetModel.getBUDGET_AMT());
		values.put("BUDGET_NOTE", budgetModel.getBUDGET_NOTE());
		values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));

		// Inserting a new Row
		long result =  db.insert(DB_TABLE_BUDGET, null, values);

        //do not continue if insert failed
        if(result == -1){
            Log.e(CLASS_NAME, "New budget couldnt be saved in db");
        }
        db.close();
        return result;
    }

    @Override
	public void onCreate(SQLiteDatabase db) {}

	//constructors
	public AddUpdateBudgetsDbService(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
