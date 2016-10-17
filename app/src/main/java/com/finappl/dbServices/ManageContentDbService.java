package com.finappl.dbServices;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.AccountsMO;
import com.finappl.models.CategoryModel;
import com.finappl.models.ManageContentModel;
import com.finappl.models.SpentOnModel;
import com.finappl.utils.ColumnFetcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.finappl.utils.Constants.*;


public class ManageContentDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();

	private static ManageContentDbService sInstance = null;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
	private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

	//method to get all categories, accounts and spent ons categorized as default and user created ones
	public ManageContentModel getAllContent(String userId){
		SQLiteDatabase db = this.getWritableDatabase();
		StringBuilder sqlQuerySB = new StringBuilder(50);

		//get all categories
		sqlQuerySB.append(" SELECT ");
		sqlQuerySB.append(" CAT.CAT_ID, ");
		sqlQuerySB.append(" CAT.CAT_NAME, ");
		sqlQuerySB.append(" CAT.CAT_TYPE, ");
		sqlQuerySB.append(" CAT.CAT_IS_DEFAULT, ");
		sqlQuerySB.append(" CAT.CAT_NOTE, ");
		sqlQuerySB.append(" USR.NAME, ");
		sqlQuerySB.append(" CAT.USER_ID ");

		sqlQuerySB.append(" FROM ");
		sqlQuerySB.append(DB_TABLE_USER+" USR ");

		sqlQuerySB.append(" LEFT OUTER JOIN ");
		sqlQuerySB.append(DB_TABLE_CATEGORY+" CAT ");
		sqlQuerySB.append(" ON USR.USER_ID IN ('"+ADMIN_USERID+"', '"+userId+"')");

		sqlQuerySB.append(" WHERE ");
		sqlQuerySB.append(" CAT.USER_ID IN ('"+ADMIN_USERID+"', '"+userId+"') ");

		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" CAT.CAT_IS_DEL = '"+DB_NONAFFIRMATIVE+"' ");

		sqlQuerySB.append(" GROUP BY ");
		sqlQuerySB.append(" CAT.CAT_ID ");

		sqlQuerySB.append(" ORDER BY ");
		sqlQuerySB.append(" CAT.CAT_IS_DEFAULT DESC, ");
		sqlQuerySB.append(" CAT.CAT_NAME ASC");

		Log.i(CLASS_NAME, "query to get all categories for manage content page : " + sqlQuerySB);
		Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

		ManageContentModel manageContentModelObj = new ManageContentModel();
		Map<String, List<CategoryModel>> catMap = new TreeMap<String, List<CategoryModel>>();

		while (cursor.moveToNext()) {
			String catIdStr = ColumnFetcher.getInstance().loadString(cursor, "CAT_ID");
			String catNameStr = ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME");
			String catIsDefStr = ColumnFetcher.getInstance().loadString(cursor, "CAT_IS_DEFAULT");
			String catNoteStr = ColumnFetcher.getInstance().loadString(cursor, "CAT_NOTE");
			String catUserNameStr = ColumnFetcher.getInstance().loadString(cursor, "NAME");
			String catTypeStr = ColumnFetcher.getInstance().loadString(cursor, "CAT_TYPE");

			if(manageContentModelObj.getUserNameStr() == null){
				manageContentModelObj.setUserNameStr(catUserNameStr);
			}
			else if("".equalsIgnoreCase(manageContentModelObj.getUserNameStr())){
				manageContentModelObj.setUserNameStr(catUserNameStr);
			}

			CategoryModel catModelObj = new CategoryModel();
			catModelObj.setCAT_ID(catIdStr);
			catModelObj.setCAT_NAME(catNameStr);
			catModelObj.setCAT_IS_DEFAULT(catIsDefStr);
			catModelObj.setCAT_NOTE(catNoteStr);
			catModelObj.setCAT_TYPE(catTypeStr);

			if("N".equalsIgnoreCase(catIsDefStr)){
				List<CategoryModel> catList = null;

				if(catMap.containsKey("USER")){
					catList = catMap.get("USER");
				}
				else{
					catList = new ArrayList<CategoryModel>();
				}

				catList.add(catModelObj);
				catMap.put("USER", catList);
			}
			else if("Y".equalsIgnoreCase(catIsDefStr)){
				List<CategoryModel> catList = null;

				if(catMap.containsKey("Y-DEFAULT")){
					catList = catMap.get("Y-DEFAULT");
				}
				else{
					catList = new ArrayList<CategoryModel>();
				}

				catList.add(catModelObj);
				catMap.put("Y-DEFAULT", catList);
			}
		}
		//end of get all categories

		//get all accounts
		sqlQuerySB.setLength(0);

		sqlQuerySB.append(" SELECT ");
		sqlQuerySB.append(" ACC.ACC_ID, ");
		sqlQuerySB.append(" ACC.ACC_NAME, ");
		sqlQuerySB.append(" ACC.ACC_IS_DEFAULT, ");
		sqlQuerySB.append(" ACC.ACC_NOTE, ");
		sqlQuerySB.append(" ACC.USER_ID, ");

		sqlQuerySB.append(" (SELECT ");
		sqlQuerySB.append(" IFNULL(SUM(ACC_TOTAL), '0')  ");
		sqlQuerySB.append(" FROM ");
		sqlQuerySB.append(DB_TABLE_ACCOUNT);
		sqlQuerySB.append(" WHERE  ");
		sqlQuerySB.append(" ACC_ID = ACC.ACC_ID ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" ACC_IS_DEL  =  '"+DB_NONAFFIRMATIVE+"' ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" USER_ID  =  '"+userId+"' ) ");

		sqlQuerySB.append(" + ");

		sqlQuerySB.append(" (( SELECT ");
		sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0')  ");
		sqlQuerySB.append(" FROM ");
		sqlQuerySB.append(DB_TABLE_TRANSACTION);
		sqlQuerySB.append(" WHERE  ");
		sqlQuerySB.append(" TRAN_TYPE = 'INCOME'  ");
		sqlQuerySB.append(" AND  ");
		sqlQuerySB.append(" ACC_ID = ACC.ACC_ID  ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" TRAN_IS_DEL  =  '"+DB_NONAFFIRMATIVE+"' ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" USER_ID  =  '"+userId+"' ) ");

		sqlQuerySB.append(" + ");

		sqlQuerySB.append(" (SELECT ");
		sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
		sqlQuerySB.append(" FROM ");
		sqlQuerySB.append(DB_TABLE_TRANSFER);
		sqlQuerySB.append(" WHERE ");
		sqlQuerySB.append(" ACC_ID_TO = ACC.ACC_ID ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" TRNFR_IS_DEL  =  '"+DB_NONAFFIRMATIVE+"' ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" USER_ID  =  '"+userId+"' )) ");

		sqlQuerySB.append(" - ");

		sqlQuerySB.append(" ((SELECT ");
		sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0') ");
		sqlQuerySB.append(" FROM ");
		sqlQuerySB.append(DB_TABLE_TRANSACTION);
		sqlQuerySB.append(" WHERE ");
		sqlQuerySB.append(" TRAN_TYPE = 'EXPENSE' ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" ACC_ID = ACC.ACC_ID ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" TRAN_IS_DEL  =  '"+DB_NONAFFIRMATIVE+"' ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" USER_ID  =  '"+userId+"' ) ");

		sqlQuerySB.append(" + ");

		sqlQuerySB.append(" (SELECT ");
		sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
		sqlQuerySB.append(" FROM ");
		sqlQuerySB.append(DB_TABLE_TRANSFER);
		sqlQuerySB.append(" WHERE ");
		sqlQuerySB.append(" ACC_ID_FRM = ACC.ACC_ID ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" TRNFR_IS_DEL  =  '"+DB_NONAFFIRMATIVE+"' ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" USER_ID  =  '"+userId+"' )) ");

		sqlQuerySB.append(" AS ");
		sqlQuerySB.append(" ACC_TOTAL ");

		sqlQuerySB.append(" FROM ");
		sqlQuerySB.append(DB_TABLE_ACCOUNT+" ACC ");

		sqlQuerySB.append(" LEFT JOIN ");
		sqlQuerySB.append(DB_TABLE_TRANSACTION+" TRAN ON ");
		sqlQuerySB.append(" TRAN.ACC_ID = ACC.ACC_ID ");

		sqlQuerySB.append(" WHERE ");
		sqlQuerySB.append(" (ACC.USER_ID = '"+userId+"' ");
		sqlQuerySB.append(" OR ");
		sqlQuerySB.append(" ACC.USER_ID = '"+ADMIN_USERID+"') ");
		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" ACC.ACC_IS_DEL = '"+DB_NONAFFIRMATIVE+"' ");

		sqlQuerySB.append(" GROUP BY ");
		sqlQuerySB.append(" ACC.ACC_ID ");

		sqlQuerySB.append(" ORDER BY ");
		sqlQuerySB.append(" ACC.ACC_IS_DEFAULT ");

		Log.i(CLASS_NAME, "query to get all accounts for manage content page : " + sqlQuerySB);
		cursor = db.rawQuery(sqlQuerySB.toString(), null);

		Map<String, List<AccountsMO>> accMap = new TreeMap<String, List<AccountsMO>>();

		while (cursor.moveToNext()) {
			String accIdStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_ID");
			String accNameStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_NAME");
			String accIsDefStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_IS_DEF");
			String accNoteStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_NOTE");
			Double accTotal = ColumnFetcher.getInstance().loadDouble(cursor, "ACC_TOTAL");

			AccountsMO accountsModelObj = new AccountsMO();
			accountsModelObj.setACC_ID(accIdStr);
			accountsModelObj.setACC_NAME(accNameStr);
			accountsModelObj.setACC_IS_DEF(accIsDefStr);
			accountsModelObj.setACC_NOTE(accNoteStr);
			accountsModelObj.setACC_TOTAL(accTotal);

			if("N".equalsIgnoreCase(accIsDefStr)){
				List<AccountsMO> accList = null;

				if(accMap.containsKey("USER")){
					accList = accMap.get("USER");
				}
				else{
					accList = new ArrayList<AccountsMO>();
				}

				accList.add(accountsModelObj);
				accMap.put("USER", accList);
			}
			else if("Y".equalsIgnoreCase(accIsDefStr)){
				List<AccountsMO> accList = null;

				if(accMap.containsKey("Y-DEFAULT")){
					accList = accMap.get("Y-DEFAULT");
				}
				else{
					accList = new ArrayList<AccountsMO>();
				}

				accList.add(accountsModelObj);
				accMap.put("Y-DEFAULT", accList);
			}
		}
		//end of get all accounts

		//get all spent ons
		sqlQuerySB.setLength(0);

		sqlQuerySB.append(" SELECT ");
		sqlQuerySB.append(" SPNT_ON_ID, ");
		sqlQuerySB.append(" SPNT_ON_NAME, ");
		sqlQuerySB.append(" SPNT_ON_IS_DEFAULT, ");
		sqlQuerySB.append(" SPNT_ON_NOTE ");

		sqlQuerySB.append(" FROM ");
		sqlQuerySB.append(DB_TABLE_SPENTON);

		sqlQuerySB.append(" WHERE ");
		sqlQuerySB.append(" (USER_ID = '"+userId+"' ");
		sqlQuerySB.append(" OR ");
		sqlQuerySB.append(" USER_ID = '"+ADMIN_USERID+"') ");

		sqlQuerySB.append(" GROUP BY ");
		sqlQuerySB.append(" SPNT_ON_ID ");

		sqlQuerySB.append(" ORDER BY ");
		sqlQuerySB.append(" SPNT_ON_IS_DEFAULT, ");
		sqlQuerySB.append(" SPNT_ON_NAME ");

		Log.i(CLASS_NAME, "query to get all spent ons for manage content page : " + sqlQuerySB);
		cursor = db.rawQuery(sqlQuerySB.toString(), null);

		Map<String, List<SpentOnModel>> spentOnMap = new TreeMap<String, List<SpentOnModel>>();

		while (cursor.moveToNext()) {
			String spentOnIdStr = ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_ID");
			String spentOnNameStr = ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME");
			String spentOnIsDefStr = ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_IS_DEFAULT");
			String spentOnNoteStr = ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NOTE");

			SpentOnModel spentOnModelObj = new SpentOnModel();
			spentOnModelObj.setSPNT_ON_ID(spentOnIdStr);
			spentOnModelObj.setSPNT_ON_NAME(spentOnNameStr);
			spentOnModelObj.setSPNT_ON_IS_DEFAULT(spentOnIsDefStr);
			spentOnModelObj.setSPNT_ON_NOTE(spentOnNoteStr);

			if("N".equalsIgnoreCase(spentOnIsDefStr)){
				List<SpentOnModel> spentOnList = null;

				if(spentOnMap.containsKey("USER")){
					spentOnList = spentOnMap.get("USER");
				}
				else{
					spentOnList = new ArrayList<SpentOnModel>();
				}

				spentOnList.add(spentOnModelObj);
				spentOnMap.put("USER", spentOnList);
			}
			else if("Y".equalsIgnoreCase(spentOnIsDefStr)){
				List<SpentOnModel> spentOnList = null;

				if(spentOnMap.containsKey("Y-DEFAULT")){
					spentOnList = spentOnMap.get("Y-DEFAULT");
				}
				else{
					spentOnList = new ArrayList<SpentOnModel>();
				}

				spentOnList.add(spentOnModelObj);
				spentOnMap.put("Y-DEFAULT", spentOnList);
			}
		}

		manageContentModelObj.setCategoriesMap(catMap);
		manageContentModelObj.setAccountsMap(accMap);
		manageContentModelObj.setSpentOnsMap(spentOnMap);

		db.close();
		return manageContentModelObj;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	// get class instance
	public static ManageContentDbService getInstance(Context context){
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null){
			sInstance = new ManageContentDbService(context.getApplicationContext());
		}
		return sInstance;
	}

	// constructors
	public ManageContentDbService(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}

}
