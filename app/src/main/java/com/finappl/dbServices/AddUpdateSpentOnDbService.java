package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.SpentOnModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;
import com.finappl.utils.DateTimeUtil;
import com.finappl.utils.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.finappl.utils.Constants.DB_DATE_FORMAT;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_TABLE_SPENTONTABLE;
import static com.finappl.utils.Constants.DB_VERSION;


public class AddUpdateSpentOnDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();

	private static AddUpdateSpentOnDbService sInstance = null;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

	public long addNewSpentOn(SpentOnModel spntOnObject){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(*) AS COUNT ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_SPENTONTABLE);

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
        while (cursor.moveToNext()){
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
        values.put("CREAT_DTM", simpleDateFormat.format(new Date()));

        // Inserting a new Row in spent on table
        long result =  db.insert(DB_TABLE_SPENTONTABLE, null, values);

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
	public static AddUpdateSpentOnDbService getInstance(Context context){
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null){
			sInstance = new AddUpdateSpentOnDbService(context.getApplicationContext());
		}
		return sInstance;
	}

	// constructors
	public AddUpdateSpentOnDbService(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        //unnecessary
    }
}
