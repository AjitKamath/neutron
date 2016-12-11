package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.BudgetMO;
import com.finappl.utils.IdGenerator;

import java.util.Date;

import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT_SDF;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_TABLE_BUDGET;
import static com.finappl.utils.Constants.DB_VERSION;

public class BudgetsDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    public boolean deleteBudget(String budgetIdStr){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DB_TABLE_BUDGET, "BUDGET_ID = '" + budgetIdStr+"'", null) > 0;
    }

    public long addNewBudget(BudgetMO budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("BUDGET_ID", IdGenerator.generateUniqueId("BDGT"));
        values.put("USER_ID", budget.getUSER_ID());
        values.put("BUDGET_NAME", budget.getBUDGET_NAME());
        values.put("BUDGET_GRP_TYPE", budget.getBUDGET_GRP_TYPE());
        values.put("BUDGET_GRP_ID", budget.getBUDGET_GRP_ID());
        values.put("BUDGET_TYPE", budget.getBUDGET_TYPE());
        values.put("BUDGET_AMT", budget.getBUDGET_AMT());
        values.put("BUDGET_NOTE", budget.getBUDGET_NOTE());
        values.put("CREAT_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

        try {
            // Inserting a new Row
            return db.insertOrThrow(DB_TABLE_BUDGET, null, values);
        }
        catch(Exception e){
            Log.e(CLASS_NAME, "Error while adding budget:"+e);
        }
        finally {
            db.close();
        }
        return 0;
    }

    public long updateOldBudget(BudgetMO budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("BUDGET_NAME", budget.getBUDGET_NAME());
        values.put("BUDGET_GRP_TYPE", budget.getBUDGET_GRP_TYPE());
        values.put("BUDGET_GRP_ID", budget.getBUDGET_GRP_ID());
        values.put("BUDGET_TYPE", budget.getBUDGET_TYPE());
        values.put("BUDGET_AMT", budget.getBUDGET_AMT());
        values.put("BUDGET_NOTE", budget.getBUDGET_NOTE());
        values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

        try {
            // Updating an old Row
            return db.update(DB_TABLE_BUDGET, values,	"BUDGET_ID = '" + budget.getBUDGET_ID() + "'", null);
        }
        catch(Exception e){
            Log.e(CLASS_NAME, "Error while updating budget:"+e);
        }
        finally {
            db.close();
        }
        return 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

	//constructors
	public BudgetsDbService(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
