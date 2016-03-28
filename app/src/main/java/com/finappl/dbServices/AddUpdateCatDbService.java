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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.finappl.utils.Constants.DB_DATE_FORMAT;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_TABLE_CATEGORYTABLE;
import static com.finappl.utils.Constants.DB_TABLE_CATEGORYTAGSTABLE;
import static com.finappl.utils.Constants.DB_VERSION;


public class AddUpdateCatDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();

	private static AddUpdateCatDbService sInstance = null;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

	public long addNewCategory(CategoryModel catObject){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(*) AS COUNT ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_CATEGORYTABLE);

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
        while (cursor.moveToNext()){
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
        values.put("CREAT_DTM", simpleDateFormat.format(new Date()));

        // Inserting a new Row in category table
        long result =  db.insert(DB_TABLE_CATEGORYTABLE, null, values);

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
            values.put("CREAT_DTM", simpleDateFormat.format(new Date()));

            Log.i(CLASS_NAME, "TAG ID:" + tagIdStr);

            result =  db.insert(DB_TABLE_CATEGORYTAGSTABLE, null, values);

            if(result == -1){
                Log.e(CLASS_NAME, "Error while inserting a new tag");
                break;
            }
            else{
                count1++;
            }
        }

        db.close();
        return result;
    }


	@Override
	public void onCreate(SQLiteDatabase db) {
        //unnecessary
    }

	// get class instance
	public static AddUpdateCatDbService getInstance(Context context){
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null){
			sInstance = new AddUpdateCatDbService(context.getApplicationContext());
		}
		return sInstance;
	}

	// constructors
	public AddUpdateCatDbService(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        //unnecessary
    }

}
