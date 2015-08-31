package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.CategoryModel;
import com.finappl.models.TagModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;
import com.finappl.utils.DateTimeUtil;
import com.finappl.utils.IdGenerator;

import java.util.Date;
import java.util.List;


public class AddUpdateCatDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();

	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = Constants.DB_VERSION;
	private static AddUpdateCatDbService sInstance = null;

	//db tables
    private static final String USERS_TABLE = Constants.DB_TABLE_USERSTABLE;
    private static final String ACCOUNT_TABLE = Constants.DB_TABLE_ACCOUNTTABLE;
    private static final String CATEGORY_TABLE = Constants.DB_TABLE_CATEGORYTABLE;
    private static final String SPENT_ON_TABLE = Constants.DB_TABLE_SPENTONTABLE;
    private static final String TRANSACTION_TABLE = Constants.DB_TABLE_TRANSACTIONTABLE;
    private static final String SCHEDULED_TRANSACTION_TABLE = Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE;
    private static final String BUDGET_TABLE = Constants.DB_TABLE_BUDGETTABLE;
    private static final String CATEGORY_TAGS_TABLE = Constants.DB_TABLE_CATEGORYTAGSTABLE;

	public long addNewCategory(CategoryModel catObject){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(*) AS COUNT ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(CATEGORY_TABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" CAT_NAME ");
        sqlQuerySB.append(" = ");
        sqlQuerySB.append(" '"+catObject.getCAT_NAME()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" CAT_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"'");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" USER_ID = '"+catObject.getUSER_ID()+"'");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        int count = 0;
        while (cursor.moveToNext())
        {
            //	get category ID
            count = ColumnFetcher.getInstance().loadInt(cursor, "COUNT");
        }

        if(count > 0){
            return -2;
        }

        ContentValues values = new ContentValues();

        String catIdStr = IdGenerator.getInstance().generateUniqueId("CAT");

        values.put("CAT_ID", catIdStr);
        values.put("USER_ID", catObject.getUSER_ID());
        values.put("CAT_NAME", catObject.getCAT_NAME());
        values.put("CAT_TYPE", catObject.getCAT_TYPE());
        values.put("CAT_IS_DEFAULT", Constants.DB_NONAFFIRMATIVE);
        values.put("CAT_NOTE", catObject.getCAT_NOTE());
        values.put("CAT_IS_DEL", Constants.DB_NONAFFIRMATIVE);
        values.put("CREAT_DTM", DateTimeUtil.getInstance().dateDateToDbDateString(new Date()));

        // Inserting a new Row in category tab;e
        long result =  db.insert(CATEGORY_TABLE, null, values);

        //if result is not -1 then continue inserting tags in category_tags table
        if(result == -1){
            return result;
        }

        //TODO:CATEGORY TAGS
        List<TagModel> tagList = catObject.getCategoryTagList();

        int count1 = 0;
        for(TagModel iterList : tagList){
            values = null;
            values = new ContentValues();

            String tagIdStr = IdGenerator.getInstance().generateUniqueId("TAG");

            values.put("TAG_ID", tagIdStr);
            values.put("USER_ID", catObject.getUSER_ID());
            values.put("CAT_ID", catIdStr);
            values.put("CAT_TAGS", iterList.getTag());
            values.put("CAT_TAG_IS_DEL", Constants.DB_NONAFFIRMATIVE);
            values.put("CREAT_DTM", DateTimeUtil.getInstance().dateDateToDbDateString(new Date()));

            result =  db.insert(CATEGORY_TAGS_TABLE, null, values);

            if(result == -1){
                Log.e(CLASS_NAME, "Error while inserting a new tag");
                break;
            }
            else{
                count1++;
            }
        }

        return result;
    }


	@Override
	public void onCreate(SQLiteDatabase db) {
        //unnecessary
    }

	// get class instance
	public static AddUpdateCatDbService getInstance(Context context)
	{
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null)
		{
			sInstance = new AddUpdateCatDbService(context.getApplicationContext());
		}
		return sInstance;
	}

	// constructors
	public AddUpdateCatDbService(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        //unnecessary
    }

}
