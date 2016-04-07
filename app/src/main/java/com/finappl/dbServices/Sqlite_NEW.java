package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.utils.ColumnFetcher;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.finappl.utils.Constants.*;

public class Sqlite_NEW extends SQLiteOpenHelper{

	private final String CLASS_NAME = this.getClass().getName();

	private static Sqlite_NEW sInstance = null;

	private void createNotificationsTable() {
		StringBuilder sb = new StringBuilder(50);
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
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USER+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_NOTIFICATION + " Table query:\n" + String.valueOf(sb));

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(String.valueOf(sb));
		db.close();
	}

	private void createSettingsTable() {
		StringBuilder sb = new StringBuilder(50);
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_SETTING);
		sb.append(" (SET_ID TEXT PRIMARY KEY, ");       //pk
		sb.append(" USER_ID TEXT NOT NULL, ");          //fk1
		sb.append(" SET_NOTIF_TIME TEXT NOT NULL, ");
		sb.append(" SET_NOTIF_BUZZ TEXT NOT NULL, ");
		sb.append(" SET_SEC_PIN TEXT, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USER+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_SETTING + " Table query:\n" + String.valueOf(sb));

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(String.valueOf(sb));
		db.close();
	}

	private void createBudgetTable() {
		StringBuilder sb = new StringBuilder(50);
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
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USER+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_BUDGET + " Table query:\n" + String.valueOf(sb));

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(String.valueOf(sb));
		db.close();
	}

	private void createTransferTable() {
		StringBuilder sb = new StringBuilder(50);
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_TRANSFER);
		sb.append(" (TRNFR_ID TEXT PRIMARY KEY, ");				//pk
		sb.append(" USER_ID TEXT NOT NULL, ");					//fk1
		sb.append(" ACC_ID_FRM TEXT NOT NULL, ");				//fk2
		sb.append(" ACC_ID_TO TEXT NOT NULL, ");				//fk3
		sb.append(" TRNFR_IS_SCHED TEXT NOT NULL, ");
		sb.append(" TRNFR_REPEAT TEXT NOT NULL, ");
		sb.append(" TRNFR_AMT TEXT NOT NULL, ");
		sb.append(" TRNFR_NAME TEXT NOT NULL, ");
		sb.append(" TRNFR_DATE DATE NOT NULL, ");
		sb.append(" TRNFR_NOTE TEXT, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USER+" (USER_ID), ");
		sb.append(" FOREIGN KEY (ACC_ID_FRM) REFERENCES "+DB_TABLE_ACCOUNT+" (ACC_ID), ");
		sb.append(" FOREIGN KEY (ACC_ID) REFERENCES "+DB_TABLE_ACCOUNT+" (ACC_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_TRANSFER + " Table query:\n" + String.valueOf(sb));

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(String.valueOf(sb));
		db.close();
	}

	private void createTransactionTable() {
		StringBuilder sb = new StringBuilder(50);
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_TRANSACTION);
		sb.append(" (TRAN_ID TEXT PRIMARY KEY, ");				//pk
		sb.append(" USER_ID TEXT NOT NULL, ");					//fk1
		sb.append(" CAT_ID TEXT NOT NULL, ");					//fk2
		sb.append(" ACC_ID TEXT NOT NULL, ");					//fk3
		sb.append(" SPNT_ON_ID TEXT NOT NULL, ");				//fk4
		sb.append(" TRAN_IS_SCHED TEXT NOT NULL, ");
		sb.append(" TRAN_REPEAT TEXT NOT NULL, ");
		sb.append(" TRAN_AMT TEXT NOT NULL, ");
		sb.append(" TRAN_NAME TEXT NOT NULL, ");
		sb.append(" TRAN_TYPE TEXT NOT NULL, ");
		sb.append(" TRAN_DATE DATE NOT NULL, ");
		sb.append(" TRAN_NOTE TEXT, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USER+" (USER_ID), ");
		sb.append(" FOREIGN KEY (CAT_ID) REFERENCES "+DB_TABLE_CATEGORY+" (CAT_ID), ");
		sb.append(" FOREIGN KEY (ACC_ID) REFERENCES "+DB_TABLE_ACCOUNT+" (ACC_ID), ");
		sb.append(" FOREIGN KEY (SPNT_ON_ID) REFERENCES "+DB_TABLE_SPENTON+" (SPNT_ON_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_TRANSACTION + " Table query:\n" + String.valueOf(sb));

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(String.valueOf(sb));
		db.close();
	}

	private void createSpentOnTable() {
		StringBuilder sb = new StringBuilder(50);
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_SPENTON);
		sb.append(" (SPNT_ON_ID TEXT PRIMARY KEY, ");			//pk
		sb.append(" USER_ID TEXT NOT NULL, ");					//fk1
		sb.append(" SPNT_ON_NAME TEXT NOT NULL, ");
		sb.append(" SPNT_ON_IS_DEF TEXT NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USER+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_SPENTON + " Table query:\n" + String.valueOf(sb));

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(String.valueOf(sb));
		db.close();
	}

	private void createAccountTable() {
		StringBuilder sb = new StringBuilder(50);
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_ACCOUNT);
		sb.append(" (ACC_ID TEXT PRIMARY KEY, ");			//pk
		sb.append(" USER_ID TEXT NOT NULL, ");				//fk1
		sb.append(" ACC_NAME TEXT NOT NULL, ");
		sb.append(" ACC_IS_DEF TEXT NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USER+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_ACCOUNT + " Table query:\n" + String.valueOf(sb));

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(String.valueOf(sb));
		db.close();
	}

	private void createCategoryTable() {
		StringBuilder sb = new StringBuilder(50);
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_CATEGORY);
		sb.append(" (CAT_ID TEXT PRIMARY KEY, ");			//pk
		sb.append(" USER_ID TEXT NOT NULL, ");				//FK1
		sb.append(" CAT_NAME TEXT NOT NULL, ");
		sb.append(" CAT_IS_DEF TEXT NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USER+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_CATEGORY + " Table query:\n" + String.valueOf(sb));

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(String.valueOf(sb));
		db.close();
	}

	private void createUserTable() {
		StringBuilder sb = new StringBuilder(50);
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_USER);
		sb.append(" (USER_ID TEXT PRIMARY KEY, ");			//pk
		sb.append(" CNTRY_ID TEXT NOT NULL, ");				//fk 1
		sb.append(" NAME TEXT NOT NULL, ");
		sb.append(" PASS TEXT NOT NULL, ");
		sb.append(" EMAIL TEXT NOT NULL, ");
		sb.append(" DOB DATE NOT NULL, ");
		sb.append(" TELEPHONE TEXT NOT NULL, ");
		sb.append(" DEV_ID TEXT NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME, ");
		sb.append(" FOREIGN KEY (CNTRY_ID) REFERENCES "+DB_TABLE_COUNTRY+" (CNTRY_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_USER + " Table query:\n" + String.valueOf(sb));

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(String.valueOf(sb));
		db.close();
	}

	private void createCountryTable() {
		StringBuilder sb = new StringBuilder(50);
		sb.append(" CREATE TABLE IF NOT EXISTS ");
		sb.append(DB_TABLE_COUNTRY);
		sb.append(" (CNTRY_ID TEXT PRIMARY KEY, ");			//pk
		sb.append(" CNTRY_NAME TEXT NOT NULL, ");
		sb.append(" CUR TEXT NOT NULL, ");
		sb.append(" CUR_CODE TEXT NOT NULL, ");
		sb.append(" CREAT_DTM DATETIME NOT NULL, ");
		sb.append(" MOD_DTM DATETIME) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_COUNTRY + " Table query:\n" + String.valueOf(sb));

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(String.valueOf(sb));
		db.close();
	}

	//------------CREATE TABLE-----------------------//
	@Override
	public void onCreate(SQLiteDatabase db){
		Log.i(CLASS_NAME, "Creating the tables for "+DB_NAME);

		createCountryTable();
		createUserTable();
		createCategoryTable();
		createAccountTable();
		createTransactionTable();
		createTransferTable();
		createBudgetTable();
		createNotificationsTable();
		createSettingsTable();

		Log.i(CLASS_NAME, "Creating the tables for " + DB_NAME+" is completed");

		//TODO: Yet to add defaults
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//TODO: check required

		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ACCOUNT);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CATEGORY);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SPENTON);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_TRANSACTION);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BUDGET);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_TRANSFER);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_COUNTRY);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SETTING);

		// Create tables again
        onCreate(db);
	}

	public Sqlite_NEW(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	public static Sqlite_NEW getInstance(Context context) {
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null) {
			sInstance = new Sqlite_NEW(context.getApplicationContext());
		}
		return sInstance;
	}
}