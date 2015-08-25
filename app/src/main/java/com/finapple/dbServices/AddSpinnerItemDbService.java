package com.finapple.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finapple.model.SpinnerItemModel;
import com.finapple.util.Constants;
import com.finapple.util.DateTimeUtil;

import java.util.Date;


public class AddSpinnerItemDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();
	
	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = 1;
	private static AddSpinnerItemDbService sInstance = null;

	private static final String AFFIRMATIVE =  Constants.DB_AFFIRMATIVE;
	private static final String NON_AFFIRMATIVE =  Constants.DB_NONAFFIRMATIVE;

	//db tables
	private static final String usersTable = Constants.DB_TABLE_USERSTABLE;
	private static final String accountTable = Constants.DB_TABLE_ACCOUNTTABLE;
	private static final String categoryTable = Constants.DB_TABLE_CATEGORYTABLE;
	private static final String spentOnTable = Constants.DB_TABLE_SPENTONTABLE;
	private static final String transactionTable = Constants.DB_TABLE_TRANSACTIONTABLE;
	private static final String scheduledTransactionsTable = Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE;
	
	//	method to add a new transaction
	/*public long addNewSpinnerItem(SpinnerItemModel spnItemModel)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		if(db == null)
		{
			Log.e(CLASS_NAME, "SQLiteDatabase object is null");
			return -1;
		}
		
		ContentValues values = new ContentValues();
		values.put("USER_ID", USER_ID);
		values.put("CREAT_DTM", DateTimeUtil.getInstance().dateDateToDbDateString(new Date()));
		values.put("MOD_DTM", "");
		
		String temp;
		if((temp = spnItemModel.getSpinnerItemType()).equalsIgnoreCase("category")){
			values.put("CAT_ID", IdGenerator.getInstance().generateUniqueId("CAT"));
			values.put("CAT_NAME", spnItemModel.getSpinnerItemName());
			values.put("CAT_TYPE", spnItemModel.getSpinnerCatType());
			values.put("CAT_IS_DEFAULT", NON_AFFIRMATIVE);
			values.put("CAT_IS_DEL", NON_AFFIRMATIVE);
			
			// Inserting a new Row
			return db.insert(categoryTable, null, values);
		}
		else if(temp.equalsIgnoreCase("spent on")){
			values.put("SPNT_ON_ID", IdGenerator.getInstance().generateUniqueId("SPNT"));
			values.put("SPNT_ON_NAME", spnItemModel.getSpinnerItemName());
			values.put("SPNT_ON_IS_DEFAULT", NON_AFFIRMATIVE);
			values.put("SPNT_ON_IS_DEL", NON_AFFIRMATIVE);
			
			// Inserting a new Row
			return db.insert(spentOnTable, null, values);
		}
		else if(temp.equalsIgnoreCase("account")){
			values.put("ACC_ID", IdGenerator.getInstance().generateUniqueId("ACC"));
			values.put("ACC_NAME", spnItemModel.getSpinnerItemName());
			values.put("ACC_IS_DEFAULT", NON_AFFIRMATIVE);
			values.put("ACC_IS_DEL", NON_AFFIRMATIVE);
			
			// Inserting a new Row
			return db.insert(accountTable, null, values);
		}
		Log.e(CLASS_NAME, "Error : Couldnt add a new Spinner Item");
		return -1;
	}*/
	
	//	method to update an already created transaction.. returns 0 for fail, 1 for success
	public int updateOldSpinnerItem(SpinnerItemModel spnItemModel)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		if(db == null)
		{
			Log.e(CLASS_NAME, "SQLiteDatabase object is null");
			return 0;
		}
		
		ContentValues values = new ContentValues();
		values.put("MOD_DTM", DateTimeUtil.getInstance().dateDateToDbDateString(new Date()));
		
		String temp;
		if((temp = spnItemModel.getSpinnerCatType()).equalsIgnoreCase("category")){
			values.put("CAT_NAME", spnItemModel.getSpinnerItemName());
			values.put("CAT_TYPE", spnItemModel.getSpinnerItemType());
			
			// Updating an old Row
			return db.update(categoryTable, values,	"CAT_ID = '" + spnItemModel.getSpinnerItemTypeId() + "'", null);
		}
		else if(temp.equalsIgnoreCase("spent on")){
			values.put("SPNT_ON_NAME", spnItemModel.getSpinnerItemName());
			
			// Updating an old Row
			return db.update(spentOnTable, values,	"SPNT_ON_ID = '" + spnItemModel.getSpinnerItemTypeId() + "'", null);
		}
		else if(temp.equalsIgnoreCase("account")){
			values.put("ACC_NAME", spnItemModel.getSpinnerItemName());
			
			// Updating an old Row
			return db.update(accountTable, values,	"ACC_ID = '" + spnItemModel.getSpinnerItemTypeId() + "'", null);
		}
		Log.e(CLASS_NAME, "Error : Couldnt update an old Spinner Item");
		return -1;
	}

	/*//	get category ID on category name
	public String getCategoryIdOnCategoryName(SQLiteDatabase db, String categoryNameStr)
	{
		StringBuilder sqlQuerySB = new StringBuilder(50);
		
		sqlQuerySB.append(" SELECT ");
		sqlQuerySB.append(" CAT_ID ");
		
		sqlQuerySB.append(" FROM ");
		sqlQuerySB.append(categoryTable);
		
		sqlQuerySB.append(" WHERE ");
		sqlQuerySB.append(" CAT_NAME ");
		sqlQuerySB.append(" = ");
		sqlQuerySB.append(" '"+categoryNameStr+"' ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" CAT_IS_DEFAULT ");
		sqlQuerySB.append(" = ");
		sqlQuerySB.append(" '"+NON_AFFIRMATIVE+"' ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" CAT_IS_DEL ");
		sqlQuerySB.append(" = ");
		sqlQuerySB.append(" '"+NON_AFFIRMATIVE+"' ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" USER_ID ");
		sqlQuerySB.append(" = ");
		sqlQuerySB.append(" '"+USER_ID+"' ");
		
		Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
		
		while (cursor.moveToNext())
		{
			//	get category ID 
			return ColumnFetcher.getInstance().loadString(cursor, "CAT_ID");
		}
		
		Log.e(CLASS_NAME, "Could not get CAT_ID for CAT_NAME="+categoryNameStr);
		return null;
	}*/
	
	//method to know whether type with same name already exists
	/*public boolean hasSpinnerItemType(SpinnerItemModel spinnerItemModel){
		SQLiteDatabase db = this.getWritableDatabase();
		StringBuilder sqlQuerySB = new StringBuilder(50);
		
		sqlQuerySB.append(" SELECT ");
		
		String temp;
		if((temp = spinnerItemModel.getSpinnerItemType()).equalsIgnoreCase("category")){
			
			sqlQuerySB.append(" COUNT(CAT_NAME) ");
			sqlQuerySB.append(" FROM ");
			sqlQuerySB.append(categoryTable);
			sqlQuerySB.append(" WHERE ");
			sqlQuerySB.append(" CAT_NAME = '"+spinnerItemModel.getSpinnerItemName()+"'");
			sqlQuerySB.append(" AND ");
			sqlQuerySB.append(" CAT_IS_DEFAULT ");
			sqlQuerySB.append(" = ");
			sqlQuerySB.append(" '"+NON_AFFIRMATIVE+"' ");
			sqlQuerySB.append(" AND ");
			sqlQuerySB.append(" CAT_IS_DEL ");
			sqlQuerySB.append(" = ");
			sqlQuerySB.append(" '"+NON_AFFIRMATIVE+"' ");
		}
		else if(temp.equalsIgnoreCase("spent on")){
			sqlQuerySB.append(" COUNT(SPNT_ON_NAME) ");
			sqlQuerySB.append(" FROM ");
			sqlQuerySB.append(spentOnTable);
			sqlQuerySB.append(" WHERE ");
			sqlQuerySB.append(" SPNT_ON_NAME = '"+spinnerItemModel.getSpinnerItemName()+"'");
			sqlQuerySB.append(" AND ");
			sqlQuerySB.append(" SPNT_ON_IS_DEFAULT ");
			sqlQuerySB.append(" = ");
			sqlQuerySB.append(" '"+NON_AFFIRMATIVE+"' ");
			sqlQuerySB.append(" AND ");
			sqlQuerySB.append(" SPNT_ON_IS_DEL ");
			sqlQuerySB.append(" = ");
			sqlQuerySB.append(" '"+NON_AFFIRMATIVE+"' ");
		}
		else if(temp.equalsIgnoreCase("account")){
			sqlQuerySB.append(" COUNT(ACC_NAME) ");
			sqlQuerySB.append(" FROM ");
			sqlQuerySB.append(accountTable);
			sqlQuerySB.append(" WHERE ");
			sqlQuerySB.append(" ACC_NAME = '"+spinnerItemModel.getSpinnerItemName()+"'");
			sqlQuerySB.append(" AND ");
			sqlQuerySB.append(" ACC_IS_DEFAULT ");
			sqlQuerySB.append(" = ");
			sqlQuerySB.append(" '"+NON_AFFIRMATIVE+"' ");
			sqlQuerySB.append(" AND ");
			sqlQuerySB.append(" ACC_IS_DEL ");
			sqlQuerySB.append(" = ");
			sqlQuerySB.append(" '"+NON_AFFIRMATIVE+"' ");
		}
		
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" USER_ID ");
		sqlQuerySB.append(" = ");
		sqlQuerySB.append(" '"+USER_ID+"' ");
		
		Log.i(CLASS_NAME, "hasSpinnerItemType DB Query :"+sqlQuerySB);
		Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

		if(cursor.moveToFirst()){
			if(cursor.getInt(0) > 0){
				return true;
			}
		}
		return false;
	}*/
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {}

	// get class instance
	public static AddSpinnerItemDbService getInstance(Context context) 
	{
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null) 
		{
			sInstance = new AddSpinnerItemDbService(context.getApplicationContext());
		}
		return sInstance;
	}

	// constructors
	public AddSpinnerItemDbService(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}
}
