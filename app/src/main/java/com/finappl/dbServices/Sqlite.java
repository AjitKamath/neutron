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

public class Sqlite extends SQLiteOpenHelper{

	private final String CLASS_NAME = this.getClass().getName();

	private final String USER = Constants.ADMIN_USERID;
	
	private static Sqlite sInstance = null;

	// Database Version
	private static final int DATABASE_VERSION = Constants.DB_VERSION;

	// Database Name
	private static final String DATABASE_NAME = Constants.DB_NAME;

	//new db design
	private static final String USERS_TABLE = Constants.DB_TABLE_USERSTABLE;
	private static final String ACCOUNT_TABLE = Constants.DB_TABLE_ACCOUNTTABLE;
	private static final String CATEGORY_TABLE = Constants.DB_TABLE_CATEGORYTABLE;
	private static final String SPENT_ON_TABLE = Constants.DB_TABLE_SPENTONTABLE;
	private static final String TRANSACTION_TABLE = Constants.DB_TABLE_TRANSACTIONTABLE;
	private static final String SCHEDULED_TRANSACTION_TABLE = Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE;
    private static final String BUDGET_TABLE = Constants.DB_TABLE_BUDGETTABLE;
    private static final String CATEGORY_TAGS_TABLE = Constants.DB_TABLE_CATEGORYTAGSTABLE;
    private static final String SCHEDULED_TRANSFER_TABLE = Constants.DB_TABLE_SHEDULEDTRANSFERSTABLE;
    private static final String TRANSFERS_TABLE = Constants.DB_TABLE_TRANSFERSTABLE;
	private static final String COUNTRY_TABLE = Constants.DB_TABLE_COUNTRYTABLE;
	private static final String CURRENCY_TABLE = Constants.DB_TABLE_CURRENCYTABLE;
	private static final String WORK_TIMELINE_TABLE = Constants.DB_TABLE_WORK_TIMELINETABLE;
	private static final String NOTIFICATIONS_TABLE = Constants.DB_TABLE_NOTIFICATIONSTABLE;

	public Sqlite(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
		Log.i(CLASS_NAME, DATABASE_NAME+" seems to not exist..so creating it");

		createCurrencyMastertable(db);
		Log.i(CLASS_NAME, CURRENCY_TABLE + " table created successfully");

		createCountryMastertable(db);
		Log.i(CLASS_NAME, COUNTRY_TABLE + " table created successfully");

		createUsersTable(db);
		Log.i(CLASS_NAME, USERS_TABLE + " table created successfully");

		createSettingsNotificationsTable(db);
		Log.i(CLASS_NAME, Constants.DB_TABLE_SETTINGS_NOTIFICATIONS + " table created successfully");

		createSettingsSoundsTable(db);
		Log.i(CLASS_NAME, Constants.DB_TABLE_SETTINGS_SOUNDS + " table created successfully");

        createSettingsSecurityTable(db);
        Log.i(CLASS_NAME, Constants.DB_TABLE_SETTINGS_SECURITY + " table created successfully");

        createWorkTimelineTable(db);
        Log.i(CLASS_NAME, WORK_TIMELINE_TABLE + " table created successfully");
		
		createAccountMasterTable(db);
		Log.i(CLASS_NAME, ACCOUNT_TABLE+" table created successfully");
		
		createCategoryMasterTable(db);
		Log.i(CLASS_NAME, CATEGORY_TABLE+" table created successfully");
		
		createSpentOnTable(db);
		Log.i(CLASS_NAME, SPENT_ON_TABLE+" table created successfully");
		
		createTransactionTable(db);
		Log.i(CLASS_NAME, TRANSACTION_TABLE+" table created successfully");
		
		createScheduledTransactionsTable(db);
		Log.i(CLASS_NAME, SCHEDULED_TRANSACTION_TABLE+" table created successfully");

        createBudgetTable(db);
        Log.i(CLASS_NAME, BUDGET_TABLE+" table created successfully");

        createCategoryTagsTable(db);
        Log.i(CLASS_NAME, CATEGORY_TAGS_TABLE+" table created successfully");

        createScheduledTransfersTable(db);
        Log.i(CLASS_NAME, SCHEDULED_TRANSFER_TABLE+" table created successfully");

        createTransfersTable(db);
        Log.i(CLASS_NAME, TRANSFERS_TABLE+" table created successfully");

		createNotificationsTable(db);
		Log.i(CLASS_NAME, NOTIFICATIONS_TABLE+" table created successfully");

		//check and add defaults
		
		//categories
		String categoryDefaultsStrArr[] = Constants.DEFAULTS_CATEGORIES.split(",");
		checkAndAddDefault(db, categoryDefaultsStrArr, CATEGORY_TABLE, "CAT_NAME");
		
		//spentOns
		String spentOnDefaultsStrArr[] = Constants.DEFAULTS_SPENTON.split(",");
		checkAndAddDefault(db, spentOnDefaultsStrArr, SPENT_ON_TABLE, "SPNT_ON_NAME");
		
		//accounts
		String accountsDefaultsStrArr[] = Constants.DEFAULTS_ACCOUNTS.split(",");
		checkAndAddDefault(db, accountsDefaultsStrArr, ACCOUNT_TABLE, "ACC_NAME");

		//currency
		String currencyDefaultsStrArr[] = Constants.DEFAULTS_CURRENCIES.split(",");
		checkAndAddDefault(db, currencyDefaultsStrArr, CURRENCY_TABLE, "CUR_NAME");

		//country
		String countryDefaultsStrArr[] = Constants.DEFAULTS_COUNTRIES.split(",");
		checkAndAddDefault(db, countryDefaultsStrArr, COUNTRY_TABLE, "CNTRY_NAME");
	}

    private void createSettingsSecurityTable(SQLiteDatabase db) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(Constants.DB_TABLE_SETTINGS_SECURITY);
        sqlQuerySB.append(" (SET_SEC_ID TEXT PRIMARY KEY, ");	            //pk
        sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");          		    //fk1
        sqlQuerySB.append(" SET_SEC_ACTIVE TEXT NOT NULL, ");
        sqlQuerySB.append(" SET_SEC_KEY TEXT, ");
        sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
        sqlQuerySB.append(" MOD_DTM DATETIME, ");

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID)) ");

        Log.i(CLASS_NAME, "Create " + Constants.DB_TABLE_SETTINGS_SECURITY + " Table query:" + sqlQuerySB.toString());

        db.execSQL(sqlQuerySB.toString());
    }

    private void createSettingsSoundsTable(SQLiteDatabase db) {
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(Constants.DB_TABLE_SETTINGS_SOUNDS);
		sqlQuerySB.append(" (SET_SND_ID TEXT PRIMARY KEY, ");	            //pk
		sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");          		    //fk1
		sqlQuerySB.append(" SET_SND_ACTIVE TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");

		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + Constants.DB_TABLE_SETTINGS_SOUNDS + " Table query:" + sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}

	private void createSettingsNotificationsTable(SQLiteDatabase db) {
		StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(Constants.DB_TABLE_SETTINGS_NOTIFICATIONS);
		sqlQuerySB.append(" (SET_NOTIF_ID TEXT PRIMARY KEY, ");	            //pk
		sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");          		    //fk1
		sqlQuerySB.append(" SET_NOTIF_ACTIVE TEXT NOT NULL, ");
		sqlQuerySB.append(" SET_NOTIF_TIME TEXT NOT NULL, ");
        sqlQuerySB.append(" SET_NOTIF_BUZZ TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");

		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create " + Constants.DB_TABLE_SETTINGS_NOTIFICATIONS + " Table query:" + sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}

	private void createNotificationsTable(SQLiteDatabase db) {
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(NOTIFICATIONS_TABLE);
		sqlQuerySB.append(" (CNCL_NOTIF_ID TEXT PRIMARY KEY, ");	    //pk
		sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");          		//fk1
		sqlQuerySB.append(" CNCL_NOTIF_TYPE, ");
		sqlQuerySB.append(" CNCL_NOTIF_EVNT_ID TEXT NOT NULL, ");
		sqlQuerySB.append(" CNCL_NOTIF_RSN TEXT NOT NULL, ");
		sqlQuerySB.append(" CNCL_NOTIF_DATE TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");

		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID)) ");

		Log.i(CLASS_NAME, "Create "+NOTIFICATIONS_TABLE+" Table query:\n"+sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}

	private void createWorkTimelineTable(SQLiteDatabase db) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(WORK_TIMELINE_TABLE);
        sqlQuerySB.append(" (WORK_ID TEXT PRIMARY KEY, ");	    //pk
        sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");          //fk1
        sqlQuerySB.append(" WORK_TYPE TEXT NOT NULL, ");
        sqlQuerySB.append(" COMPANY TEXT NOT NULL, ");
        sqlQuerySB.append(" SALARY TEXT NOT NULL, ");
        sqlQuerySB.append(" SAL_FREQ TEXT NOT NULL, ");
        sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
        sqlQuerySB.append(" MOD_DTM DATETIME, ");

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID)) ");

        Log.i(CLASS_NAME, "Create "+WORK_TIMELINE_TABLE+" Table query:\n"+sqlQuerySB.toString());

        db.execSQL(sqlQuerySB.toString());
    }

    private void createCurrencyMastertable(SQLiteDatabase db) {
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(CURRENCY_TABLE);
		sqlQuerySB.append(" (CUR_ID TEXT PRIMARY KEY, ");	//pk
		sqlQuerySB.append(" CUR_TXT TEXT NOT NULL, ");
		sqlQuerySB.append(" CUR_NAME TEXT NOT NULL, ");
		sqlQuerySB.append(" CUR_SYMB TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME) ");

		Log.i(CLASS_NAME, "Create "+CURRENCY_TABLE+" Table query:\n"+sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}

	private void createCountryMastertable(SQLiteDatabase db) {
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(COUNTRY_TABLE);
		sqlQuerySB.append(" (CNTRY_ID TEXT PRIMARY KEY, ");	//pk
		sqlQuerySB.append(" CUR_ID TEXT NOT NULL, ");		//fk 1
		sqlQuerySB.append(" CNTRY_NAME TEXT NOT NULL, ");
		sqlQuerySB.append(" CNTRY_FLAG TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");

		sqlQuerySB.append(" FOREIGN KEY (CUR_ID) REFERENCES "+CURRENCY_TABLE+" (CUR_ID)) ");

		Log.i(CLASS_NAME, "Create "+COUNTRY_TABLE+" Table query:\n"+sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}

	private void createScheduledTransfersTable(SQLiteDatabase db) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(SCHEDULED_TRANSFER_TABLE);
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

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRNFR_ACC_ID_FRM) REFERENCES "+ACCOUNT_TABLE+" (ACC_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRNFR_ACC_ID_TO) REFERENCES "+ACCOUNT_TABLE+" (ACC_ID)) ");

        Log.i(CLASS_NAME, "Create "+SCHEDULED_TRANSFER_TABLE+" Table query:\n"+sqlQuerySB.toString());

        db.execSQL(sqlQuerySB.toString());
    }

    private void createTransfersTable(SQLiteDatabase db) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(TRANSFERS_TABLE);
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

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (ACC_ID_FRM) REFERENCES "+ACCOUNT_TABLE+" (ACC_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (ACC_ID_TO) REFERENCES "+ACCOUNT_TABLE+" (ACC_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRNFR_ID) REFERENCES "+SCHEDULED_TRANSFER_TABLE+" (SCH_TRNFR_ID) ");
        sqlQuerySB.append(" ) ");

        Log.i(CLASS_NAME, "Create "+TRANSFERS_TABLE+" Table query:\n"+sqlQuerySB.toString());

        db.execSQL(sqlQuerySB.toString());
    }

    private void createCategoryTagsTable(SQLiteDatabase db) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(CATEGORY_TAGS_TABLE);
        sqlQuerySB.append(" ( ");

        sqlQuerySB.append(" TAG_ID TEXT PRIMARY KEY, ");	//pk
        sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");		//fk1
        sqlQuerySB.append(" CAT_ID TEXT NOT NULL, ");		//fk2
        sqlQuerySB.append(" CAT_TAGS TEXT NOT NULL, ");
        sqlQuerySB.append(" CAT_TAG_IS_DEL TEXT NOT NULL, ");
        sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
        sqlQuerySB.append(" MOD_DTM DATETIME, ");

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (CAT_ID) REFERENCES "+CATEGORY_TABLE+" (CAT_ID) ");
        sqlQuerySB.append(" ) ");

        Log.i(CLASS_NAME, "Create "+CATEGORY_TAGS_TABLE+" Table query:\n"+sqlQuerySB.toString());

        db.execSQL(sqlQuerySB.toString());
    }
    //------------END OF CREATE TABLE-----------------------//
	
	//TODO: remove this on prod
	//	temporary method to add a test USER
	/*public void addTestUsers()
	{
		String userIdStr = Constants.TEST_USERID;
		String userNameStr = Constants.TEST_USERNAME;
		String userPasswordStr = Constants.TEST_USERPASS;
		String userEmailStr = Constants.TEST_USEREMAIL;
		String userGenderStr = Constants.TEST_USERGEND;
		String userDobStr = Constants.TEST_USERDOB;
		String userCountryStr = Constants.TEST_USERCOUNTRY;
		String userCurrencyStr = Constants.TEST_USERCURRENCY;
		String userDeviceIdStr = Constants.TEST_USERDEVICEID;
		String userIsDeletedStr = Constants.TEST_USERISDELETED;
	
		SQLiteDatabase db = this.getWritableDatabase();
		
		if(db == null)
		{
			Log.e(CLASS_NAME, "SQLiteDatabase object is null");
			return;
		}
		
		//	check whether his USER is already there..if not
		StringBuilder sqlQuerySB = new StringBuilder(50);
		
		sqlQuerySB.append(" SELECT ");
		sqlQuerySB.append(" COUNT(USER_ID) ");
		sqlQuerySB.append(" AS ");
		sqlQuerySB.append(" COUNT ");
		
		sqlQuerySB.append(" FROM ");
		sqlQuerySB.append(USERS_TABLE);
		
		sqlQuerySB.append(" WHERE ");
		sqlQuerySB.append(" USER_ID ");
		sqlQuerySB.append(" = ");
		sqlQuerySB.append(" '"+userIdStr+"' ");
		
		Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
		
		int count = 0;
		while (cursor.moveToNext())
		{
			count = ColumnFetcher.getInstance().loadInt(cursor, "COUNT");
		}

		//continue only if count == 0
		if(count != 0)
		{
			Log.i(CLASS_NAME, "USER_ID:"+userIdStr+" is already added in db");
			return;
		}
		
		Log.i(CLASS_NAME, "USER_ID:"+userIdStr+" seems to be a new USER. Adding him/her to db");
		
		ContentValues values = new ContentValues();
		
		values.put("USER_ID", userIdStr);
		values.put("NAME", userNameStr);
		values.put("PASS", userPasswordStr);
		values.put("EMAIL", userEmailStr);
		values.put("DOB", DateTimeUtil.getInstance().uiDateStringToDbDateString(userDobStr));
		values.put("CNTRY_ID", userCountryStr);
		values.put("CUR_ID", userCurrencyStr);
		values.put("DEV_ID", userDeviceIdStr);
		values.put("USER_IS_DEL", userIsDeletedStr);
		values.put("CREAT_DTM", DateTimeUtil.getInstance().dateDateToDbDateString1(new Date()));
		values.put("MOD_DTM", "");
		
		// Inserting a new Row
		if(db.insert(USERS_TABLE, null, values) != -1)
		{
			Log.i(CLASS_NAME, "USER_ID:"+userIdStr+" has been added into DB");
		}
		//	if error
		else
		{
			Log.e(CLASS_NAME, "Error while adding a new USER to db");
		}
		
	}*/
	
	public void createTransactionTable(SQLiteDatabase db){
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(TRANSACTION_TABLE);
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
		
		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID), ");
		sqlQuerySB.append(" FOREIGN KEY (CAT_ID) REFERENCES "+CATEGORY_TABLE+" (CAT_ID), ");
		sqlQuerySB.append(" FOREIGN KEY (SPNT_ON_ID) REFERENCES "+SPENT_ON_TABLE+" (SPNT_ON_ID), ");
		sqlQuerySB.append(" FOREIGN KEY (ACC_ID) REFERENCES "+ACCOUNT_TABLE+" (ACC_ID), ");
		sqlQuerySB.append(" FOREIGN KEY (SCH_TRAN_ID) REFERENCES "+SCHEDULED_TRANSACTION_TABLE+" (SCH_TRAN_ID) ");
		sqlQuerySB.append(" ) ");
		
		Log.i(CLASS_NAME, "Create TRANSACTIONS Table query:\n"+sqlQuerySB.toString());

		db.execSQL(sqlQuerySB.toString());
	}
	
	public void createSpentOnTable(SQLiteDatabase db){
		//-----------------SPNT_ON_MSTR TABLE------------------------//
		StringBuilder sqlQuerySB = new StringBuilder(50);
		
		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(SPENT_ON_TABLE);
		sqlQuerySB.append(" ( ");
		sqlQuerySB.append(" SPNT_ON_ID TEXT PRIMARY KEY, ");	//pk
		sqlQuerySB.append(" USER_ID TEXT, ");	//fk1
		sqlQuerySB.append(" SPNT_ON_NAME TEXT NOT NULL, ");
		sqlQuerySB.append(" SPNT_ON_IS_DEFAULT TEXT NOT NULL, ");
        sqlQuerySB.append(" SPNT_ON_NOTE TEXT, ");
		sqlQuerySB.append(" SPNT_ON_IS_DEL TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");
		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID) ");
		sqlQuerySB.append(" ) ");
		
		Log.i(CLASS_NAME, "Create SPENT_ON_MASTER Table query:"+sqlQuerySB.toString());
			
		db.execSQL(sqlQuerySB.toString());
	}
	
	public void createCategoryMasterTable(SQLiteDatabase db){
		//-----------------CATEGORY_MASTER TABLE------------------------//
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(CATEGORY_TABLE);
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
		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID) ");
        //sqlQuerySB.append(" FOREIGN KEY (BUDGET_ID) REFERENCES "+BUDGET_TABLE+" (USER_ID) ");
		sqlQuerySB.append(" ) ");
		
		Log.i(CLASS_NAME, "Create CATEGORY_MASTER Table query:"+sqlQuerySB.toString());
				
		db.execSQL(sqlQuerySB.toString());
	}
	
	public void createAccountMasterTable(SQLiteDatabase db){
		//----------------CREATE ACCOUNT_MASTER TABLE--------------//
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(ACCOUNT_TABLE);
		sqlQuerySB.append(" ( ");
		sqlQuerySB.append(" ACC_ID TEXT PRIMARY KEY, ");	//pk
		sqlQuerySB.append(" USER_ID TEXT NOT NULL, ");	//fk1
		sqlQuerySB.append(" ACC_NAME TEXT NOT NULL, ");
		sqlQuerySB.append(" ACC_IS_DEFAULT TEXT NOT NULL, ");
        //sqlQuerySB.append(" BUDGET_ID TEXT, ");
        sqlQuerySB.append(" ACC_TOTAL TEXT, ");			//TODO: This column is no longer required. Get rid of this
        sqlQuerySB.append(" ACC_NOTE TEXT, ");
		sqlQuerySB.append(" ACC_IS_DEL TEXT NOT NULL, ");
		sqlQuerySB.append(" CREAT_DTM DATETIME NOT NULL, ");
		sqlQuerySB.append(" MOD_DTM DATETIME, ");
		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID) ");
        //sqlQuerySB.append(" FOREIGN KEY (BUDGET_ID) REFERENCES "+BUDGET_TABLE+" (BUDGET_ID) ");
		sqlQuerySB.append(" ) ");
		
		Log.i(CLASS_NAME, "Create ACCOUNT_MASTER Table query:"+sqlQuerySB.toString());
				
		db.execSQL(sqlQuerySB.toString());
	}
	
	public void createUsersTable(SQLiteDatabase db){
		StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(USERS_TABLE);
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
		sqlQuerySB.append(" FOREIGN KEY (CNTRY_ID) REFERENCES "+COUNTRY_TABLE+" (CNTRY_ID), ");
		sqlQuerySB.append(" FOREIGN KEY (CUR_ID) REFERENCES "+CURRENCY_TABLE+" (CUR_ID) ");
		sqlQuerySB.append(" ) ");

		Log.i(CLASS_NAME, "Create USERS Table Query:" + sqlQuerySB.toString());
				
		db.execSQL(sqlQuerySB.toString());
	}
	
	public void createScheduledTransactionsTable(SQLiteDatabase db){
        StringBuilder sqlQuerySB = new StringBuilder(50);

		sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
		sqlQuerySB.append(SCHEDULED_TRANSACTION_TABLE);
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
		
		sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID) ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRAN_CAT_ID) REFERENCES "+CATEGORY_TABLE+" (CAT_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRAN_SPNT_ON_ID) REFERENCES "+SPENT_ON_TABLE+" (SPNT_ON_ID), ");
        sqlQuerySB.append(" FOREIGN KEY (SCH_TRAN_ACC_ID) REFERENCES "+ACCOUNT_TABLE+" (ACC_ID) ");
		sqlQuerySB.append(" ) ");
		
		Log.i(CLASS_NAME, "Create SCHEDULED_TRANASCTIONS Table Query:" + sqlQuerySB.toString());
				
		db.execSQL(sqlQuerySB.toString());
	}

    public void createBudgetTable(SQLiteDatabase db){
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" CREATE TABLE IF NOT EXISTS ");
        sqlQuerySB.append(BUDGET_TABLE);
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

        sqlQuerySB.append(" FOREIGN KEY (USER_ID) REFERENCES "+USERS_TABLE+" (USER_ID) ");
        sqlQuerySB.append(" ) ");

        Log.i(CLASS_NAME, "Create "+BUDGET_TABLE+" Table Query:" + sqlQuerySB.toString());

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

			StringBuilder sqlQuerySB = null;
			sqlQuerySB = new StringBuilder(50);
			
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
					if(tableStr.equalsIgnoreCase(CATEGORY_TABLE)){
						String itemType = iterStrArr.split("-")[2];

						values.put("CAT_ID", itemId);
						values.put("USER_ID", USER);
						values.put("CAT_NAME", itemName);
						values.put("CAT_TYPE", itemType);
						values.put("CAT_IS_DEFAULT", Constants.DB_AFFIRMATIVE);
						values.put("CAT_IS_DEL", Constants.DB_NONAFFIRMATIVE);
						values.put("CREAT_DTM", DateTimeUtil.getInstance().getDateTimeStamp());
					}
					else if(tableStr.equalsIgnoreCase(SPENT_ON_TABLE)){
						values.put("SPNT_ON_ID", itemId);
						values.put("USER_ID", USER);
						values.put("SPNT_ON_NAME", itemName);
						values.put("SPNT_ON_IS_DEFAULT", Constants.DB_AFFIRMATIVE);
						values.put("SPNT_ON_IS_DEL", Constants.DB_NONAFFIRMATIVE);
						values.put("CREAT_DTM", DateTimeUtil.getInstance().getDateTimeStamp());
					}
					else if(tableStr.equalsIgnoreCase(ACCOUNT_TABLE)){
						values.put("ACC_ID", itemId);
						values.put("USER_ID", USER);
						values.put("ACC_NAME", itemName);
						values.put("ACC_IS_DEFAULT", Constants.DB_AFFIRMATIVE);
						values.put("ACC_IS_DEL", Constants.DB_NONAFFIRMATIVE);
						values.put("CREAT_DTM", DateTimeUtil.getInstance().getDateTimeStamp());
					}

					else if(tableStr.equalsIgnoreCase(COUNTRY_TABLE)){
						values.put("CNTRY_ID", itemId);
						values.put("CNTRY_NAME", itemName);
						values.put("CUR_ID", itemExtra);
						values.put("CNTRY_FLAG", itemAnotherExtra);
						values.put("CREAT_DTM", DateTimeUtil.getInstance().getDateTimeStamp());
					}

					else if(tableStr.equalsIgnoreCase(CURRENCY_TABLE)){
						values.put("CUR_ID", itemId);
						values.put("CUR_TXT", itemAnotherExtra);
						values.put("CUR_NAME", itemName);
						values.put("CUR_SYMB", itemExtra);
						values.put("CREAT_DTM", DateTimeUtil.getInstance().getDateTimeStamp());
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
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_USERSTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_ACCOUNTTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_CATEGORYTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_SPENTONTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_TRANSACTIONTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_BUDGETTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_CATEGORYTAGSTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_SHEDULEDTRANSFERSTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_TRANSFERSTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_COUNTRYTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_CURRENCYTABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_WORK_TIMELINETABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_NOTIFICATIONSTABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_SETTINGS_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_SETTINGS_SOUNDS);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DB_TABLE_SETTINGS_SECURITY);

                // Create tables again
                onCreate(db);
	}
}