package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.SpinnerItemModel;
import com.finappl.utils.Constants;
import com.finappl.utils.DateTimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.finappl.utils.Constants.DB_DATE_FORMAT;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_TABLE_ACCOUNT;
import static com.finappl.utils.Constants.DB_TABLE_CATEGORY;
import static com.finappl.utils.Constants.DB_TABLE_SPENTON;
import static com.finappl.utils.Constants.DB_VERSION;


public class AddSpinnerItemDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();
	
	private static AddSpinnerItemDbService sInstance = null;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
	private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

	//	method to update an already created transaction.. returns 0 for fail, 1 for success
	public int updateOldSpinnerItem(SpinnerItemModel spnItemModel){
		SQLiteDatabase db = this.getWritableDatabase();
		int result;

		ContentValues values = new ContentValues();
		values.put("MOD_DTM", simpleDateTimeFormat.format(new Date()));
		
		String temp;
		if((temp = spnItemModel.getSpinnerCatType()).equalsIgnoreCase("category")){
			values.put("CAT_NAME", spnItemModel.getSpinnerItemName());
			values.put("CAT_TYPE", spnItemModel.getSpinnerItemType());
			
			// Updating an old Row
			result = db.update(DB_TABLE_CATEGORY, values,	"CAT_ID = '" + spnItemModel.getSpinnerItemTypeId() + "'", null);
		}
		else if(temp.equalsIgnoreCase("spent on")){
			values.put("SPNT_ON_NAME", spnItemModel.getSpinnerItemName());
			
			// Updating an old Row
			result =  db.update(DB_TABLE_SPENTON, values,	"SPNT_ON_ID = '" + spnItemModel.getSpinnerItemTypeId() + "'", null);
		}
		else if(temp.equalsIgnoreCase("account")){
			values.put("ACC_NAME", spnItemModel.getSpinnerItemName());
			
			// Updating an old Row
			result =  db.update(DB_TABLE_ACCOUNT, values,	"ACC_ID = '" + spnItemModel.getSpinnerItemTypeId() + "'", null);
		}
		else {
			Log.e(CLASS_NAME, "Error : Couldnt update an old Spinner Item");
			result = -1;
		}
		db.close();
		return result;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	// get class instance
	public static AddSpinnerItemDbService getInstance(Context context) {
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null){
			sInstance = new AddSpinnerItemDbService(context.getApplicationContext());
		}
		return sInstance;
	}

	// constructors
	public AddSpinnerItemDbService(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}
}
