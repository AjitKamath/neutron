package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;
import com.finappl.utils.DateTimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.finappl.utils.Constants.*;

public class Sqlite extends SQLiteOpenHelper{

	private final String CLASS_NAME = this.getClass().getName();

	private static Sqlite sInstance = null;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
	private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

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


	//------------CREATE TABLE-----------------------//
	@Override
	public void onCreate(SQLiteDatabase db){
		Log.i(CLASS_NAME, DB_NAME+" seems to not exist..so creating it");

		createCurrencyMastertable(db);
		Log.i(CLASS_NAME, DB_TABLE_CURRENCYTABLE + " table created successfully");

		createCountryMastertable(db);
		Log.i(CLASS_NAME, DB_TABLE_COUNTRYTABLE + " table created successfully");

		createUsersTable(db);
		Log.i(CLASS_NAME, DB_TABLE_USERSTABLE + " table created successfully");

		createSettingsNotificationsTable(db);
		Log.i(CLASS_NAME, DB_TABLE_SETTINGS_NOTIFICATIONS + " table created successfully");

		createSettingsSoundsTable(db);
		Log.i(CLASS_NAME, DB_TABLE_SETTINGS_SOUNDS + " table created successfully");

        createSettingsSecurityTable(db);
        Log.i(CLASS_NAME, DB_TABLE_SETTINGS_SECURITY + " table created successfully");

        createWorkTimelineTable(db);
        Log.i(CLASS_NAME, DB_TABLE_WORK_TIMELINETABLE + " table created successfully");
		
		createAccountMasterTable(db);
		Log.i(CLASS_NAME, DB_TABLE_ACCOUNTTABLE+" table created successfully");
		
		createCategoryMasterTable(db);
		Log.i(CLASS_NAME, DB_TABLE_CATEGORYTABLE+" table created successfully");
		
		createSpentOnTable(db);
		Log.i(CLASS_NAME, DB_TABLE_SPENTONTABLE+" table created successfully");
		
		createTransactionTable(db);
		Log.i(CLASS_NAME, DB_TABLE_TRANSACTIONTABLE+" table created successfully");
		
		createScheduledTransactionsTable(db);
		Log.i(CLASS_NAME, DB_TABLE_SCHEDULEDTRANSACTIONSTABLE+" table created successfully");

        createBudgetTable(db);
        Log.i(CLASS_NAME, DB_TABLE_BUDGETTABLE+" table created successfully");

        createCategoryTagsTable(db);
        Log.i(CLASS_NAME, DB_TABLE_CATEGORYTAGSTABLE+" table created successfully");

        createScheduledTransfersTable(db);
        Log.i(CLASS_NAME, DB_TABLE_SHEDULEDTRANSFERSTABLE+" table created successfully");

        createTransfersTable(db);
        Log.i(CLASS_NAME, DB_TABLE_TRANSFERSTABLE+" table created successfully");

		createNotificationsTable(db);
		Log.i(CLASS_NAME, DB_TABLE_NOTIFICATIONSTABLE+" table created successfully");

		//check and add defaults
		
		//categories
		String categoryDefaultsStrArr[] = DEFAULTS_CATEGORIES.split(",");
		checkAndAddDefault(db, categoryDefaultsStrArr, DB_TABLE_CATEGORYTABLE, "CAT_NAME");
		
		//spentOns
		String spentOnDefaultsStrArr[] = DEFAULTS_SPENTON.split(",");
		checkAndAddDefault(db, spentOnDefaultsStrArr, DB_TABLE_SPENTONTABLE, "SPNT_ON_NAME");
		
		//accounts
		String accountsDefaultsStrArr[] = DEFAULTS_ACCOUNTS.split(",");
		checkAndAddDefault(db, accountsDefaultsStrArr, DB_TABLE_ACCOUNTTABLE, "ACC_NAME");

		//currency
		String currencyDefaultsStrArr[] = DEFAULTS_CURRENCIES.split(",");
		checkAndAddDefault(db, currencyDefaultsStrArr, DB_TABLE_CURRENCYTABLE, "CUR_NAME");

		//country
		String countryDefaultsStrArr[] = DEFAULTS_COUNTRIES.split(",");
		checkAndAddDefault(db, countryDefaultsStrArr, DB_TABLE_COUNTRYTABLE, "CNTRY_NAME");
	}

    private void createSettingsSecurityTable(SQLiteDatabase db) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(DB_TABLE_SETTINGS_SECURITY);
        sqlQuerySB.append(" (SET_SEC_ID TEXT PRIMARY KEY, ");	            //pk
        sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");          		    //fk1
        sqlQuerySB.append(" SET_SEC_ACTIVE TEXT NOT NULL, ");
		sqlQuerySB.append(" SET_SEC_PIN, ");
        sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
        sqlQuerySB.append(" MOD_DTM DATETIME, ");

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID)) ");

        Log.i(CLASS_NAME, "Create " + DB_TABLE_SETTINGS_SECURITY + " Table query:" + sqlQuerySB.toString());

        db.execSQL(sqlQuerySB.toString());
    }

    private void createSettingsSoundsTable(SQLiteDatabase db) {
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(DB_TABLE_SETTINGS_SOUNDS);
		sqlQuerySB.append(" (SET_SND_ID TEXT PRIMARY KEY, ");	            //pk
		sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");          		    //fk1
		sqlQuerySB.append(" SET_SND_ACTIVE TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");

		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_SETTINGS_SOUNDS + " Table query:" + sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}

	private void createSettingsNotificationsTable(SQLiteDatabase db) {
		StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(DB_TABLE_SETTINGS_NOTIFICATIONS);
		sqlQuerySB.append(" (SET_NOTIF_ID TEXT PRIMARY KEY, ");	            //pk
		sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");          		    //fk1
		sqlQuerySB.append(" SET_NOTIF_ACTIVE TEXT NOT NULL, ");
		sqlQuerySB.append(" SET_NOTIF_TIME TEXT NOT NULL, ");
        sqlQuerySB.append(" SET_NOTIF_BUZZ TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");

		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + DB_TABLE_SETTINGS_NOTIFICATIONS + " Table query:" + sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}

	private void createNotificationsTable(SQLiteDatabase db) {
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(DB_TABLE_NOTIFICATIONSTABLE);
		sqlQuerySB.append(" (CNCL_NOTIF_ID TEXT PRIMARY KEY, ");	    //pk
		sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");          		//fk1
		sqlQuerySB.append(" CNCL_NOTIF_TYPE, ");
		sqlQuerySB.append(" CNCL_NOTIF_EVNT_ID TEXT NOT NULL, ");
		sqlQuerySB.append(" CNCL_NOTIF_RSN TEXT NOT NULL, ");
		sqlQuerySB.append(" CNCL_NOTIF_DATE TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");

		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create "+DB_TABLE_NOTIFICATIONSTABLE+" Table query:\n"+sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}

	private void createWorkTimelineTable(SQLiteDatabase db) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(DB_TABLE_WORK_TIMELINETABLE);
        sqlQuerySB.append(" (WORK_ID TEXT PRIMARY KEY, ");	    //pk
        sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");          //fk1
        sqlQuerySB.append(" WORK_TYPE TEXT NOT NULL, ");
        sqlQuerySB.append(" COMPANY TEXT NOT NULL, ");
        sqlQuerySB.append(" SALARY TEXT NOT NULL, ");
        sqlQuerySB.append(" SAL_FREQ TEXT NOT NULL, ");
        sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
        sqlQuerySB.append(" MOD_DTM DATETIME, ");

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID)) ");

        Log.i(CLASS_NAME, "Create "+DB_TABLE_WORK_TIMELINETABLE+" Table query:\n"+sqlQuerySB.toString());

        db.execSQL(sqlQuerySB.toString());
    }

    private void createCurrencyMastertable(SQLiteDatabase db) {
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(DB_TABLE_CURRENCYTABLE);
		sqlQuerySB.append(" (CUR_ID TEXT PRIMARY KEY, ");	//pk
		sqlQuerySB.append(" CUR_TXT TEXT NOT NULL, ");
		sqlQuerySB.append(" CUR_NAME TEXT NOT NULL, ");
		sqlQuerySB.append(" CUR_SYMB TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME) ");

		Log.i(CLASS_NAME, "Create "+DB_TABLE_CURRENCYTABLE+" Table query:\n"+sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}

	private void createCountryMastertable(SQLiteDatabase db) {
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(DB_TABLE_COUNTRYTABLE);
		sqlQuerySB.append(" (CNTRY_ID TEXT PRIMARY KEY, ");	//pk
		sqlQuerySB.append(" CUR_ID TEXT NOT NULL, ");		//fk 1
		sqlQuerySB.append(" CNTRY_NAME TEXT NOT NULL, ");
		sqlQuerySB.append(" CNTRY_FLAG TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");

		sqlQuerySB.append(" FOREIGN KEY (CUR_ID) REFERENCES "+DB_TABLE_CURRENCYTABLE+" (CUR_ID)) ");

		Log.i(CLASS_NAME, "Create "+DB_TABLE_COUNTRYTABLE+" Table query:\n"+sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}

	private void createScheduledTransfersTable(SQLiteDatabase db) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(DB_TABLE_SHEDULEDTRANSFERSTABLE);
		sqlQuerySB.append(" (SCH_TRNFR_ID TEXT PRIMARY KEY, ");	//pk
        sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");		//fk1
        sqlQuerySB.append(" SCH_TRNFR_ACC_ID_FRM TEXT NOT NULL, ");	//fk2
        sqlQuerySB.append(" SCH_TRNFR_ACC_ID_TO TEXT NOT NULL, ");	//fk3
        sqlQuerySB.append(" SCH_TRNFR_DATE TEXT NOT NULL, ");
        sqlQuerySB.append(" SCH_TRNFR_FREQ TEXT NOT NULL, ");
        sqlQuerySB.append(" SCH_TRNFR_AMT TEXT NOT NULL, ");
        sqlQuerySB.append(" SCH_TRNFR_NOTE TEXT, ");
		sqlQuerySB.append(" SCH_TRNFR_AUTO TEXT NOT NULL, ");
        sqlQuerySB.append(" SCH_TRNFR_IS_DEL TEXT NOT NULL, ");
        sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
        sqlQuerySB.append(" MOD_DTM DATETIME, ");

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRNFR_ACC_ID_FRM) REFERENCES "+DB_TABLE_ACCOUNTTABLE+" (ACC_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRNFR_ACC_ID_TO) REFERENCES "+DB_TABLE_ACCOUNTTABLE+" (ACC_ID)) ");

        Log.i(CLASS_NAME, "Create "+DB_TABLE_SHEDULEDTRANSFERSTABLE+" Table query:\n"+sqlQuerySB.toString());

        db.execSQL(sqlQuerySB.toString());
    }

    private void createTransfersTable(SQLiteDatabase db) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(DB_TABLE_TRANSFERSTABLE);
        sqlQuerySB.append(" ( ");

        sqlQuerySB.append(" TRNFR_ID TEXT PRIMARY KEY, ");	//pk
        sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");		//fk1
        sqlQuerySB.append(" ACC_ID_FRM TEXT NOT NULL, ");	//fk2
        sqlQuerySB.append(" ACC_ID_TO TEXT NOT NULL, ");	//fk3
        sqlQuerySB.append(" SCH_TRNFR_ID TEXT, ");          //fk4
        sqlQuerySB.append(" TRNFR_AMT TEXT NOT NULL, ");
        sqlQuerySB.append(" TRNFR_IS_DEL TEXT NOT NULL, ");
        sqlQuerySB.append(" TRNFR_NOTE TEXT, ");
        sqlQuerySB.append(" TRNFR_DATE TEXT NOT NULL, ");
        sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
        sqlQuerySB.append(" MOD_DTM DATETIME, ");

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (ACC_ID_FRM) REFERENCES "+DB_TABLE_ACCOUNTTABLE+" (ACC_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (ACC_ID_TO) REFERENCES "+DB_TABLE_ACCOUNTTABLE+" (ACC_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRNFR_ID) REFERENCES "+DB_TABLE_SHEDULEDTRANSFERSTABLE+" (SCH_TRNFR_ID) ");
        sqlQuerySB.append(" ) ");

        Log.i(CLASS_NAME, "Create "+DB_TABLE_TRANSFERSTABLE+" Table query:\n"+sqlQuerySB.toString());

        db.execSQL(sqlQuerySB.toString());
    }

    private void createCategoryTagsTable(SQLiteDatabase db) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(DB_TABLE_CATEGORYTAGSTABLE);
        sqlQuerySB.append(" ( ");

        sqlQuerySB.append(" TAG_ID TEXT PRIMARY KEY, ");	//pk
        sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");		//fk1
        sqlQuerySB.append(" CAT_ID TEXT NOT NULL, ");		//fk2
        sqlQuerySB.append(" CAT_TAGS TEXT NOT NULL, ");
        sqlQuerySB.append(" CAT_TAG_IS_DEL TEXT NOT NULL, ");
        sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
        sqlQuerySB.append(" MOD_DTM DATETIME, ");

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (CAT_ID) REFERENCES "+DB_TABLE_CATEGORYTABLE+" (CAT_ID) ");
        sqlQuerySB.append(" ) ");

        Log.i(CLASS_NAME, "Create "+DB_TABLE_CATEGORYTAGSTABLE+" Table query:\n"+sqlQuerySB.toString());

        db.execSQL(sqlQuerySB.toString());
    }
    //------------END OF CREATE TABLE-----------------------//
	
	public void createTransactionTable(SQLiteDatabase db){
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(DB_TABLE_TRANSACTIONTABLE);
		sqlQuerySB.append(" ( ");
		
		sqlQuerySB.append(" TRAN_ID TEXT PRIMARY KEY, ");	//pk
		sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");		//fk1
		sqlQuerySB.append(" CAT_ID TEXT NOT NULL, ");		//fk2
		sqlQuerySB.append(" SPNT_ON_ID TEXT NOT NULL, ");	//fk3
		sqlQuerySB.append(" ACC_ID TEXT NOT NULL, ");		//fk4
		sqlQuerySB.append(" SCH_TRAN_ID TEXT, ");	//fk5
		sqlQuerySB.append(" TRAN_AMT TEXT NOT NULL, ");
		sqlQuerySB.append(" TRAN_NAME TEXT NOT NULL, ");
		sqlQuerySB.append(" TRAN_TYPE TEXT NOT NULL, ");
		sqlQuerySB.append(" TRAN_NOTE TEXT, ");
		sqlQuerySB.append(" TRAN_DATE DATE NOT NULL, ");
		sqlQuerySB.append(" TRAN_IS_DEL TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");
		
		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID), ");
		sqlQuerySB.append(" FOREIGN KEY (CAT_ID) REFERENCES "+DB_TABLE_CATEGORYTABLE+" (CAT_ID), ");
		sqlQuerySB.append(" FOREIGN KEY (SPNT_ON_ID) REFERENCES "+DB_TABLE_SPENTONTABLE+" (SPNT_ON_ID), ");
		sqlQuerySB.append(" FOREIGN KEY (ACC_ID) REFERENCES "+DB_TABLE_ACCOUNTTABLE+" (ACC_ID), ");
		sqlQuerySB.append(" FOREIGN KEY (SCH_TRAN_ID) REFERENCES "+DB_TABLE_SCHEDULEDTRANSACTIONSTABLE+" (SCH_TRAN_ID) ");
		sqlQuerySB.append(" ) ");
		
		Log.i(CLASS_NAME, "Create "+DB_TABLE_TRANSACTIONTABLE+" Table query:\n"+sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}
	
	public void createSpentOnTable(SQLiteDatabase db){
		//-----------------SPNT_ON_MSTR TABLE------------------------//
		StringBuilder sqlQuerySB = new StringBuilder(50);
		
		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(DB_TABLE_SPENTONTABLE);
		sqlQuerySB.append(" ( ");
		sqlQuerySB.append(" SPNT_ON_ID TEXT PRIMARY KEY, ");	//pk
		sqlQuerySB.append(" USER_ID TEXT, ");	//fk1
		sqlQuerySB.append(" SPNT_ON_NAME TEXT NOT NULL, ");
		sqlQuerySB.append(" SPNT_ON_IS_DEFAULT TEXT NOT NULL, ");
        sqlQuerySB.append(" SPNT_ON_NOTE TEXT, ");
		sqlQuerySB.append(" SPNT_ON_IS_DEL TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");
		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID) ");
		sqlQuerySB.append(" ) ");
		
		Log.i(CLASS_NAME, "Create SPENT_ON_MASTER Table query:"+sqlQuerySB.toString());
			
		db.execSQL(sqlQuerySB.toString());
	}
	
	public void createCategoryMasterTable(SQLiteDatabase db){
		//-----------------CATEGORY_MASTER TABLE------------------------//
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(DB_TABLE_CATEGORYTABLE);
		sqlQuerySB.append(" ( ");
		sqlQuerySB.append(" CAT_ID TEXT PRIMARY KEY, ");	//pk
		sqlQuerySB.append(" USER_ID TEXT, ");	//fk1
		sqlQuerySB.append(" CAT_NAME TEXT NOT NULL, ");
		sqlQuerySB.append(" CAT_TYPE TEXT NOT NULL, ");
		//sqlQuerySB.append(" BUDGET_ID TEXT, ");
        sqlQuerySB.append(" CAT_IS_DEFAULT TEXT NOT NULL, ");
        sqlQuerySB.append(" CAT_NOTE TEXT, ");
		sqlQuerySB.append(" CAT_IS_DEL DATETIME NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");
		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID) ");
        //sqlQuerySB.append(" FOREIGN KEY (BUDGET_ID) REFERENCES "+DB_TABLE_BUDGETTABLE+" (USER_ID) ");
		sqlQuerySB.append(" ) ");
		
		Log.i(CLASS_NAME, "Create CATEGORY_MASTER Table query:"+sqlQuerySB.toString());
				
		db.execSQL(sqlQuerySB.toString());
	}
	
	public void createAccountMasterTable(SQLiteDatabase db){
		//----------------CREATE ACCOUNT_MASTER TABLE--------------//
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(DB_TABLE_ACCOUNTTABLE);
		sqlQuerySB.append(" ( ");
		sqlQuerySB.append(" ACC_ID TEXT PRIMARY KEY, ");	//pk
		sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");	//fk1
		sqlQuerySB.append(" ACC_NAME TEXT NOT NULL, ");
		sqlQuerySB.append(" ACC_IS_DEFAULT TEXT NOT NULL, ");
        //sqlQuerySB.append(" BUDGET_ID TEXT, ");
        sqlQuerySB.append(" ACC_TOTAL TEXT, ");			//This column is only to save the intial amount
        sqlQuerySB.append(" ACC_NOTE TEXT, ");
		sqlQuerySB.append(" ACC_IS_DEL TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");
		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID) ");
        //sqlQuerySB.append(" FOREIGN KEY (BUDGET_ID) REFERENCES "+DB_TABLE_BUDGETTABLE+" (BUDGET_ID) ");
		sqlQuerySB.append(" ) ");
		
		Log.i(CLASS_NAME, "Create ACCOUNT_MASTER Table query:"+sqlQuerySB.toString());
				
		db.execSQL(sqlQuerySB.toString());
	}
	
	public void createUsersTable(SQLiteDatabase db){
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(DB_TABLE_USERSTABLE);
		sqlQuerySB.append(" ( ");
		
		sqlQuerySB.append(" USER_ID TEXT PRIMARY KEY, "); 	// pk
		sqlQuerySB.append(" NAME TEXT NOT NULL, ");
		sqlQuerySB.append(" PASS TEXT, ");
		sqlQuerySB.append(" EMAIL TEXT NOT NULL, ");
		sqlQuerySB.append(" DOB DATE NOT NULL, ");
		sqlQuerySB.append(" CNTRY_ID TEXT NOT NULL, ");		//fk 1
		sqlQuerySB.append(" CUR_ID TEXT NOT NULL, ");		//fk 2
		sqlQuerySB.append(" TELEPHONE TEXT, ");
		sqlQuerySB.append(" DEV_ID TEXT NOT NULL, ");
		sqlQuerySB.append(" USER_IS_DEL TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");
		sqlQuerySB.append(" FOREIGN KEY (CNTRY_ID) REFERENCES "+DB_TABLE_COUNTRYTABLE+" (CNTRY_ID), ");
		sqlQuerySB.append(" FOREIGN KEY (CUR_ID) REFERENCES "+DB_TABLE_CURRENCYTABLE+" (CUR_ID) ");
		sqlQuerySB.append(" ) ");

		Log.i(CLASS_NAME, "Create USERS Table Query:" + sqlQuerySB.toString());
				
		db.execSQL(sqlQuerySB.toString());
	}
	
	public void createScheduledTransactionsTable(SQLiteDatabase db){
        StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(DB_TABLE_SCHEDULEDTRANSACTIONSTABLE);
		sqlQuerySB.append(" ( ");
		sqlQuerySB.append(" SCH_TRAN_ID TEXT PRIMARY KEY, ");	    //pk
		sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");	            //fk1
		sqlQuerySB.append(" SCH_TRAN_CAT_ID TEXT NOT NULL, ");      //fk2
		sqlQuerySB.append(" SCH_TRAN_SPNT_ON_ID TEXT NOT NULL, ");       //fk3
		sqlQuerySB.append(" SCH_TRAN_ACC_ID TEXT NOT NULL, ");           //fk4
        sqlQuerySB.append(" SCH_TRAN_NAME TEXT NOT NULL, ");
        sqlQuerySB.append(" SCH_TRAN_DATE DATE NOT NULL, ");
        sqlQuerySB.append(" SCH_TRAN_FREQ TEXT NOT NULL, ");
        sqlQuerySB.append(" SCH_TRAN_TYPE TEXT NOT NULL, ");
        sqlQuerySB.append(" SCH_TRAN_AMT TEXT NOT NULL, ");
        sqlQuerySB.append(" SCH_TRAN_NOTE TEXT, ");
		sqlQuerySB.append(" SCH_TRAN_AUTO TEXT NOT NULL, ");
        sqlQuerySB.append(" SCH_TRAN_IS_DEL TEXT NOT NULL, ");
        sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
        sqlQuerySB.append(" MOD_DTM DATETIME, ");
		
		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID) ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRAN_CAT_ID) REFERENCES "+DB_TABLE_CATEGORYTABLE+" (CAT_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRAN_SPNT_ON_ID) REFERENCES "+DB_TABLE_SPENTONTABLE+" (SPNT_ON_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRAN_ACC_ID) REFERENCES "+DB_TABLE_ACCOUNTTABLE+" (ACC_ID) ");
		sqlQuerySB.append(" ) ");
		
		Log.i(CLASS_NAME, "Create SCHEDULED_TRANASCTIONS Table Query:" + sqlQuerySB.toString());
				
		db.execSQL(sqlQuerySB.toString());
	}

    public void createBudgetTable(SQLiteDatabase db){
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(DB_TABLE_BUDGETTABLE);
        sqlQuerySB.append(" ( ");

        sqlQuerySB.append(" BUDGET_ID TEXT PRIMARY KEY, ");	//pk
        sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");	//fk1
        sqlQuerySB.append(" BUDGET_NAME TEXT NOT NULL, ");
        sqlQuerySB.append(" BUDGET_GRP_ID TEXT NOT NULL, ");
        sqlQuerySB.append(" BUDGET_GRP_TYPE TEXT NOT NULL, ");
        sqlQuerySB.append(" BUDGET_TYPE TEXT NOT NULL, ");
        sqlQuerySB.append(" BUDGET_IS_DEL TEXT NOT NULL, ");
        sqlQuerySB.append(" BUDGET_AMT TEXT NOT NULL, ");
        sqlQuerySB.append(" BUDGET_NOTE TEXT, ");
        sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
        sqlQuerySB.append(" MOD_DTM DATETIME, ");

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+DB_TABLE_USERSTABLE+" (USER_ID) ");
        sqlQuerySB.append(" ) ");

        Log.i(CLASS_NAME, "Create "+DB_TABLE_BUDGETTABLE+" Table Query:" + sqlQuerySB.toString());

        db.execSQL(sqlQuerySB.toString());
    }
	
	//generic method to check and add defaults only if they are not in db
	public void checkAndAddDefault(SQLiteDatabase db, String itemStrArr[], String tableStr, String columnStr){
		for(String iterStrArr : itemStrArr){
			String[] iterStrArray = iterStrArr.split("-");

			String itemName = iterStrArray[0];
			String itemId = iterStrArray[1];
			String itemExtra = null;
			String itemAnotherExtra = null;

			if(iterStrArray.length > 2) {
				itemExtra = iterStrArray[2];    //applicable only for country and currency tables
			}

			if(iterStrArray.length > 3) {
				itemAnotherExtra = iterStrArray[3];	//applicable only for currency tables
			}

			StringBuilder sqlQuerySB = new StringBuilder(50);
			
			sqlQuerySB.append(" SELECT ");
			sqlQuerySB.append(" COUNT("+columnStr+") ");
			sqlQuerySB.append(" AS COUNT ");
			
			sqlQuerySB.append(" FROM ");
			sqlQuerySB.append(tableStr);
			
			sqlQuerySB.append(" WHERE ");
			sqlQuerySB.append(columnStr +" = '"+itemName+"'");
			
			Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
			
			while (cursor.moveToNext()){
				String countStr = ColumnFetcher.getInstance().loadString(cursor, "COUNT");
				
				//if 0 then that default item is not in DB..insert it
				if(Integer.parseInt(countStr) == 0){
					ContentValues values = new ContentValues();
					
					//depending on table
					if(tableStr.equalsIgnoreCase(DB_TABLE_CATEGORYTABLE)){
						String itemType = iterStrArr.split("-")[2];

						values.put("CAT_ID", itemId);
						values.put("USER_ID", ADMIN_USERID);
						values.put("CAT_NAME", itemName);
						values.put("CAT_TYPE", itemType);
						values.put("CAT_IS_DEFAULT", DB_AFFIRMATIVE);
						values.put("CAT_IS_DEL", DB_NONAFFIRMATIVE);
						values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));
					}
					else if(tableStr.equalsIgnoreCase(DB_TABLE_SPENTONTABLE)){
						values.put("SPNT_ON_ID", itemId);
						values.put("USER_ID", ADMIN_USERID);
						values.put("SPNT_ON_NAME", itemName);
						values.put("SPNT_ON_IS_DEFAULT", DB_AFFIRMATIVE);
						values.put("SPNT_ON_IS_DEL", DB_NONAFFIRMATIVE);
						values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));
					}
					else if(tableStr.equalsIgnoreCase(DB_TABLE_ACCOUNTTABLE)){
						values.put("ACC_ID", itemId);
						values.put("USER_ID", ADMIN_USERID);
						values.put("ACC_NAME", itemName);
						values.put("ACC_IS_DEFAULT", DB_AFFIRMATIVE);
						values.put("ACC_IS_DEL", DB_NONAFFIRMATIVE);
						values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));
					}

					else if(tableStr.equalsIgnoreCase(DB_TABLE_COUNTRYTABLE)){
						values.put("CNTRY_ID", itemId);
						values.put("CNTRY_NAME", itemName);
						values.put("CUR_ID", itemExtra);
						values.put("CNTRY_FLAG", itemAnotherExtra);
						values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));
					}

					else if(tableStr.equalsIgnoreCase(DB_TABLE_CURRENCYTABLE)){
						values.put("CUR_ID", itemId);
						values.put("CUR_TXT", itemAnotherExtra);
						values.put("CUR_NAME", itemName);
						values.put("CUR_SYMB", itemExtra);
						values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));
					}

					db.insert(tableStr, null, values);
				}
		    } 
			cursor.close();
		}
	}
	
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_USERSTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ACCOUNTTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CATEGORYTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SPENTONTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_TRANSACTIONTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SCHEDULEDTRANSACTIONSTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BUDGETTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CATEGORYTAGSTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SHEDULEDTRANSFERSTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_TRANSFERSTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_COUNTRYTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CURRENCYTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_WORK_TIMELINETABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NOTIFICATIONSTABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SETTINGS_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SETTINGS_SOUNDS);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SETTINGS_SECURITY);

		// Create tables again
        onCreate(db);
	}
}