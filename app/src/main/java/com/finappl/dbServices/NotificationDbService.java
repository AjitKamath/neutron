package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.finappl.models.NotificationModel;
import com.finappl.utils.Constants;
import com.finappl.utils.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    //db tables
    private static final String USERS_TABLE = Constants.DB_TABLE_USERSTABLE;
    private static final String ACCOUNT_TABLE = Constants.DB_TABLE_ACCOUNTTABLE;
    private static final String CATEGORY_TABLE = Constants.DB_TABLE_CATEGORYTABLE;
    private static final String SPENT_ON_TABLE = Constants.DB_TABLE_SPENTONTABLE;
    private static final String TRANSACTION_TABLE = Constants.DB_TABLE_TRANSACTIONTABLE;
    private static final String SCHEDULED_TRANSACTIONS_TABLE = Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE;
    private static final String SCHEDULED_TRANSFER_TABLE = Constants.DB_TABLE_SHEDULEDTRANSFERSTABLE;
    private static final String BUDGET_TABLE = Constants.DB_TABLE_BUDGETTABLE;
    private static final String TRANSFERS_TABLE = Constants.DB_TABLE_TRANSFERSTABLE;
    private static final String CURRENCY_TABLE = Constants.DB_TABLE_CURRENCYTABLE;
    private static final String COUNTRY_TABLE = Constants.DB_TABLE_COUNTRYTABLE;
    private static final String WORK_TIMELINE_TABLE = Constants.DB_TABLE_WORK_TIMELINETABLE;
    private static final String NOTIFICATIONS_TABLE = Constants.DB_TABLE_NOTIFICATIONSTABLE;

	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = Constants.DB_VERSION;

    public long cancelOrAddNotif(NotificationModel notificationModelObj){
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        ContentValues values = new ContentValues();

        values.put("CNCL_NOTIF_ID", IdGenerator.getInstance().generateUniqueId("NOTIF"));
        values.put("USER_ID", notificationModelObj.getUSER_ID());
        values.put("CNCL_NOTIF_TYPE", notificationModelObj.getCNCL_NOTIF_TYPE());
        values.put("CNCL_NOTIF_EVNT_ID", notificationModelObj.getCNCL_NOTIF_EVNT_ID());
        values.put("CNCL_NOTIF_RSN", notificationModelObj.getCNCL_NOTIF_RSN());
        values.put("CNCL_NOTIF_DATE", notificationModelObj.getCNCL_NOTIF_DATE());
        values.put("CREAT_DTM", sdf.format(new Date()));

        return db.insert(NOTIFICATIONS_TABLE, null, values);
    }

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	public NotificationDbService(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
