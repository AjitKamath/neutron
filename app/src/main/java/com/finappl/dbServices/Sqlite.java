package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

import static com.finappl.utils.Constants.ACCOUNTS;
import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.CATEGORIES;
import static com.finappl.utils.Constants.COUNTRIES_CURRENCIES;
import static com.finappl.utils.Constants.DB_AFFIRMATIVE;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT_SDF;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_NONAFFIRMATIVE;
import static com.finappl.utils.Constants.DB_TABLE_ACCOUNT;
import static com.finappl.utils.Constants.DB_TABLE_BUDGET;
import static com.finappl.utils.Constants.DB_TABLE_CATEGORY;
import static com.finappl.utils.Constants.DB_TABLE_COUNTRY;
import static com.finappl.utils.Constants.DB_TABLE_NOTIFICATION;
import static com.finappl.utils.Constants.DB_TABLE_REPEAT;
import static com.finappl.utils.Constants.DB_TABLE_SETTING;
import static com.finappl.utils.Constants.DB_TABLE_SPENTON;
import static com.finappl.utils.Constants.DB_TABLE_TAGS;
import static com.finappl.utils.Constants.DB_TABLE_TRANSACTION;
import static com.finappl.utils.Constants.DB_TABLE_TRANSFER;
import static com.finappl.utils.Constants.DB_TABLE_USER;
import static com.finappl.utils.Constants.DB_VERSION;
import static com.finappl.utils.Constants.DEFAULT_ACCOUNT;
import static com.finappl.utils.Constants.DEFAULT_CATEGORY;
import static com.finappl.utils.Constants.DEFAULT_REPEAT;
import static com.finappl.utils.Constants.DEFAULT_SPENTON;
import static com.finappl.utils.Constants.REPEATS;
import static com.finappl.utils.Constants.SPENT_ONS;

public class Sqlite extends SQLiteOpenHelper{

	private final String CLASS_NAME = this.getClass().getName();
	private static Sqlite sInstance = null;

	private SQLiteDatabase db;

	private void addDefaults(){
		ContentValues values;
		String nowStr = DB_DATE_TIME_FORMAT_SDF.format(new Date());

		//Country
		Log.i(CLASS_NAME, "Inserting defaults into " + DB_TABLE_COUNTRY);
		String countriesStrArr[] = COUNTRIES_CURRENCIES.split(",");
		values = new ContentValues();
		for(String iterCountriesStrArr : countriesStrArr){
			String countryCurrencyStrArr[] = iterCountriesStrArr.split("-");
			values.put("CNTRY_ID", countryCurrencyStrArr[0]);
			values.put("CNTRY_NAME", countryCurrencyStrArr[0]);
			values.put("CNTRY_CODE", countryCurrencyStrArr[1]);
			values.put("CUR", countryCurrencyStrArr[2]);
			values.put("CUR_CODE", countryCurrencyStrArr[3]);
			values.put("METRIC", countryCurrencyStrArr[4]);
			values.put("CNTRY_IMG", countryCurrencyStrArr[5]);
			values.put("CREAT_DTM", nowStr);
			db.insert(DB_TABLE_COUNTRY, null, values);
		}
		Log.i(CLASS_NAME, "Inserted defaults("+countriesStrArr.length+") into " + DB_TABLE_COUNTRY);

		//Categories
		Log.i(CLASS_NAME, "Inserting defaults into " + DB_TABLE_CATEGORY);
		String categoriesStrArr[] = CATEGORIES.split(",");
		values = new ContentValues();
		for(String iterCategoriesStrArr : categoriesStrArr){
			String iterCategoriesStrArrArr[] = iterCategoriesStrArr.split("-");

			String categoryImageStr = iterCategoriesStrArrArr[1];
			String categoryNameStr = iterCategoriesStrArrArr[0];

			values.put("CAT_ID", categoryNameStr);
			values.put("USER_ID", ADMIN_USERID);
			values.put("CAT_NAME", categoryNameStr);
			values.put("CAT_IMG", categoryImageStr);

			if(categoryNameStr.equalsIgnoreCase(DEFAULT_CATEGORY)){
				values.put("CAT_IS_DEF", DB_AFFIRMATIVE);
			}
			else{
				values.put("CAT_IS_DEF", DB_NONAFFIRMATIVE);
			}

			values.put("CREAT_DTM", nowStr);
			db.insert(DB_TABLE_CATEGORY, null, values);
		}
		Log.i(CLASS_NAME, "Inserted defaults("+categoriesStrArr.length+") into " + DB_TABLE_CATEGORY);

		//Account
		Log.i(CLASS_NAME, "Inserting defaults into " + DB_TABLE_ACCOUNT);
		String accountStrArr[] = ACCOUNTS.split(",");
		values = new ContentValues();
		for(String iterAccountStrArr : accountStrArr){
			String iterAccountStrArrArr[] = iterAccountStrArr.split("-");

			String accountImageStr = iterAccountStrArrArr[1];
			String accountNameStr = iterAccountStrArrArr[0];

			values.put("ACC_ID", accountNameStr);
			values.put("USER_ID", ADMIN_USERID);
			values.put("ACC_NAME", accountNameStr);
			values.put("ACC_IMG", accountImageStr);

			if(accountNameStr.equalsIgnoreCase(DEFAULT_ACCOUNT)){
				values.put("ACC_IS_DEF", DB_AFFIRMATIVE);
			}
			else{
				values.put("ACC_IS_DEF", DB_NONAFFIRMATIVE);
			}

			values.put("CREAT_DTM", nowStr);
			db.insert(DB_TABLE_ACCOUNT, null, values);
		}
		Log.i(CLASS_NAME, "Inserted defaults("+accountStrArr.length+") into " + DB_TABLE_ACCOUNT);

		//Spent On
		Log.i(CLASS_NAME, "Inserting defaults into " + DB_TABLE_SPENTON);
		String spentOnStrArr[] = SPENT_ONS.split(",");
		values = new ContentValues();
		for(String iterSpentOnStrArr : spentOnStrArr){
			String iterSpentOnStrArrArr[] = iterSpentOnStrArr.split("-");

			String spentOnImageStr = iterSpentOnStrArrArr[1];
			String spentOnNameStr = iterSpentOnStrArrArr[0];

			values.put("SPNT_ON_ID", spentOnNameStr);
			values.put("USER_ID", ADMIN_USERID);
			values.put("SPNT_ON_NAME", spentOnNameStr);
			values.put("SPNT_ON_IMG", spentOnImageStr);

			if(spentOnNameStr.equalsIgnoreCase(DEFAULT_SPENTON)){
				values.put("SPNT_ON_IS_DEF", DB_AFFIRMATIVE);
			}
			else{
				values.put("SPNT_ON_IS_DEF", DB_NONAFFIRMATIVE);
			}

			values.put("CREAT_DTM", nowStr);
			db.insert(DB_TABLE_SPENTON, null, values);
		}
		Log.i(CLASS_NAME, "Inserted defaults(" + spentOnStrArr.length + ") into " + DB_TABLE_SPENTON);

		//Repeats
		Log.i(CLASS_NAME, "Inserting defaults into " + DB_TABLE_REPEAT);
		String repeatsStrArr[] = REPEATS.split(",");
		values = new ContentValues();
		for(String iterArr : repeatsStrArr){
			String iterArrArr[] = iterArr.split("-");

			String repeatImageStr = iterArrArr[1];
			String repeatNameStr = iterArrArr[0];

			values.put("REPEAT_ID", repeatNameStr);
			values.put("REPEAT_NAME", repeatNameStr);
			values.put("REPEAT_IMG", repeatImageStr);

			if(repeatNameStr.equalsIgnoreCase(DEFAULT_REPEAT)){
				values.put("REPEAT_IS_DEF", DB_AFFIRMATIVE);
			}
			else{
				values.put("REPEAT_IS_DEF", DB_NONAFFIRMATIVE);
			}

			values.put("CREAT_DTM", nowStr);
			db.insert(DB_TABLE_REPEAT, null, values);
		}
		Log.i(CLASS_NAME, "Inserted defaults("+repeatsStrArr.length+") into " + DB_TABLE_REPEAT);
	}

	private void createTagsTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_TAGS);
		sb.append(" (TAG_ID TEXT PRIMARY KEY, ");		//pk
		sb.append(" USER_ID TEXT NOT NULL, ");          //fk1
		sb.append(" TAG_TYPE TEXT NOT NULL, ");
		sb.append(" TAG_TYPE_ID TEXT NOT NULL, ");
		sb.append(" TAGS TEXT NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES " + DB_TABLE_USER + " (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_TAGS + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	private void createNotificationsTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_NOTIFICATION);
		sb.append(" (NOTIF_ID TEXT PRIMARY KEY, ");		//pk
		sb.append(" USER_ID TEXT NOT NULL, ");          //fk1
		sb.append(" NOTIF_TYPE TEXT NOT NULL, ");
		sb.append(" NOTIF_EVNT_ID TEXT NOT NULL, ");
		sb.append(" NOTIF_RSN TEXT NOT NULL, ");
		sb.append(" NOTIF_DATE DATE NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES " + DB_TABLE_USER + " (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_NOTIFICATION + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	private void createSettingsTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_SETTING);
		sb.append(" (SET_ID TEXT PRIMARY KEY, ");       //pk
		sb.append(" USER_ID TEXT NOT NULL, ");          //fk1
		sb.append(" SET_NOTIF_TIME TEXT NOT NULL, ");
		sb.append(" SET_NOTIF_BUZZ TEXT NOT NULL, ");
		sb.append(" SET_SEC_PIN TEXT, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES " + DB_TABLE_USER + " (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_SETTING + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	private void createBudgetTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_BUDGET);
		sb.append(" (BUDGET_ID TEXT PRIMARY KEY, ");    //pk
		sb.append(" USER_ID TEXT NOT NULL, ");          //fk1
		sb.append(" BUDGET_NAME TEXT NOT NULL, ");
		sb.append(" BUDGET_GRP_TYPE TEXT NOT NULL, ");
		sb.append(" BUDGET_GRP_ID TEXT NOT NULL, ");
		sb.append(" BUDGET_TYPE TEXT NOT NULL, ");
		sb.append(" BUDGET_AMT TEXT NOT NULL, ");
		sb.append(" BUDGET_NOTE TEXT, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES " + DB_TABLE_USER + " (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_BUDGET + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	private void createRepeatTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_REPEAT);
		sb.append(" (REPEAT_ID TEXT PRIMARY KEY, ");				//pk
		sb.append(" REPEAT_NAME TEXT NOT NULL, ");
		sb.append(" REPEAT_IS_DEF TEXT NOT NULL, ");
		sb.append(" REPEAT_IMG TEXT NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_REPEAT + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	private void createTransferTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_TRANSFER);
		sb.append(" (TRNFR_ID TEXT PRIMARY KEY, ");				//pk
		sb.append(" USER_ID TEXT NOT NULL, ");					//fk1
		sb.append(" ACC_ID_FRM TEXT NOT NULL, ");				//fk2
		sb.append(" ACC_ID_TO TEXT NOT NULL, ");				//fk3
		sb.append(" REPEAT_ID TEXT, ");							//fk4
		sb.append(" PARENT_TRNFR_ID TEXT, ");
		sb.append(" NOTIFY TEXT, ");
		sb.append(" NOTIFY_TIME TEXT, ");
		sb.append(" SCHD_UPTO_DATE TEXT, ");
		sb.append(" TRNFR_AMT TEXT NOT NULL, ");
		sb.append(" TRNFR_DATE DATE NOT NULL, ");
		sb.append(" TRNFR_NOTE TEXT, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USER+" (USER_ID), ");
		sb.append(" FOREIGN KEY (REPEAT_ID) REFERENCES "+DB_TABLE_REPEAT+" (REPEAT_ID), ");
		sb.append(" FOREIGN KEY (ACC_ID_FRM) REFERENCES "+DB_TABLE_ACCOUNT+" (ACC_ID), ");
		sb.append(" FOREIGN KEY (ACC_ID_TO) REFERENCES " + DB_TABLE_ACCOUNT + " (ACC_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_TRANSFER + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	private void createTransactionTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_TRANSACTION);
		sb.append(" (TRAN_ID TEXT PRIMARY KEY, ");				//pk
		sb.append(" USER_ID TEXT NOT NULL, ");					//fk1
		sb.append(" CAT_ID TEXT NOT NULL, ");					//fk2
		sb.append(" ACC_ID TEXT NOT NULL, ");					//fk3
		sb.append(" SPNT_ON_ID TEXT NOT NULL, ");				//fk4
		sb.append(" REPEAT_ID TEXT, ");							//fk5
		sb.append(" PARENT_TRAN_ID TEXT, ");
		sb.append(" TRAN_AMT TEXT NOT NULL, ");
		sb.append(" TRAN_NAME TEXT NOT NULL, ");
		sb.append(" TRAN_TYPE TEXT NOT NULL, ");
		sb.append(" TRAN_DATE DATE NOT NULL, ");
		sb.append(" TRAN_NOTE TEXT, ");
		sb.append(" NOTIFY TEXT, ");
		sb.append(" NOTIFY_TIME TEXT, ");
		sb.append(" SCHD_UPTO_DATE TEXT, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USER+" (USER_ID), ");
		sb.append(" FOREIGN KEY (CAT_ID) REFERENCES "+DB_TABLE_CATEGORY+" (CAT_ID), ");
		sb.append(" FOREIGN KEY (ACC_ID) REFERENCES "+DB_TABLE_ACCOUNT+" (ACC_ID), ");
		sb.append(" FOREIGN KEY (SPNT_ON_ID) REFERENCES " + DB_TABLE_SPENTON + " (SPNT_ON_ID), ");
		sb.append(" FOREIGN KEY (REPEAT_ID) REFERENCES " + DB_TABLE_REPEAT + " (REPEAT_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_TRANSACTION + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	private void createSpentOnTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_SPENTON);
		sb.append(" (SPNT_ON_ID TEXT PRIMARY KEY, ");			//pk
		sb.append(" USER_ID TEXT NOT NULL, ");					//fk1
		sb.append(" SPNT_ON_NAME TEXT NOT NULL, ");
		sb.append(" SPNT_ON_IS_DEF TEXT NOT NULL, ");
		sb.append(" SPNT_ON_IMG TEXT NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES " + DB_TABLE_USER + " (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_SPENTON + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	private void createAccountTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_ACCOUNT);
		sb.append(" (ACC_ID TEXT PRIMARY KEY, ");			//pk
		sb.append(" USER_ID TEXT NOT NULL, ");				//fk1
		sb.append(" ACC_NAME TEXT NOT NULL, ");
		sb.append(" ACC_IS_DEF TEXT NOT NULL, ");
		sb.append(" ACC_IMG TEXT NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES " + DB_TABLE_USER + " (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_ACCOUNT + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	private void createCategoryTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_CATEGORY);
		sb.append(" (CAT_ID TEXT PRIMARY KEY, ");			//pk
		sb.append(" USER_ID TEXT NOT NULL, ");				//FK1
		sb.append(" CAT_NAME TEXT NOT NULL, ");
		sb.append(" CAT_IMG TEXT NOT NULL, ");
		sb.append(" CAT_IS_DEF TEXT NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES " + DB_TABLE_USER + " (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_CATEGORY + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	private void createUserTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_USER);
		sb.append(" (USER_ID TEXT PRIMARY KEY, ");			//pk
		sb.append(" CNTRY_ID TEXT NOT NULL, ");				//fk 1
		sb.append(" NAME TEXT, ");
		sb.append(" PASS TEXT, ");
		sb.append(" EMAIL TEXT NOT NULL, ");
		sb.append(" TELEPHONE TEXT, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (CNTRY_ID) REFERENCES " + DB_TABLE_COUNTRY + " (CNTRY_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_USER + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	private void createCountryTable() {
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_COUNTRY);
		sb.append(" (CNTRY_ID TEXT PRIMARY KEY, ");			//pk
		sb.append(" CNTRY_NAME TEXT NOT NULL, ");
		sb.append(" CNTRY_CODE TEXT NOT NULL, ");
		sb.append(" CUR TEXT NOT NULL, ");
		sb.append(" CUR_CODE TEXT NOT NULL, ");
        sb.append(" CNTRY_IMG TEXT NOT NULL, ");
		sb.append(" METRIC TEXT NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_COUNTRY + " Table query:\n" + String.valueOf(sb));

		db.execSQL(String.valueOf(sb));
	}

	//------------CREATE TABLE-----------------------//
	@Override
	public void onCreate(SQLiteDatabase db){
		this.db = db;

		Log.i(CLASS_NAME, "Creating the tables for "+DB_NAME);
		createCountryTable();
		createUserTable();
		createCategoryTable();
		createAccountTable();
		createSpentOnTable();
		createTransactionTable();
		createTransferTable();
		createRepeatTable();
		createBudgetTable();
		createTagsTable();
		createNotificationsTable();
		createSettingsTable();
		Log.i(CLASS_NAME, "Creating the tables for " + DB_NAME + " is completed");

		Log.i(CLASS_NAME, "Inserting Defaults");
		addDefaults();
		Log.i(CLASS_NAME, "Inserted Defaults");

		//this.db.close();
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(CLASS_NAME, DB_NAME+" version has upgraded from("+oldVersion+") to("+newVersion+")");

		// Drop older table if existed
		Log.i(CLASS_NAME, "Dropping all the tables");
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ACCOUNT);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CATEGORY);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SPENTON);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_TRANSACTION);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BUDGET);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_TRANSFER);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_REPEAT);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_COUNTRY);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SETTING);
		Log.i(CLASS_NAME, "Dropping all the tables completed");

		// Create tables again
		Log.i(CLASS_NAME, "Recreating all the tables");
        onCreate(db);
		Log.i(CLASS_NAME, "Recreating all the tables completed");
	}

	public Sqlite(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	public static Sqlite getInstance(Context context) {
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null) {
			sInstance = new Sqlite(context.getApplicationContext());
		}
		return sInstance;
	}
}