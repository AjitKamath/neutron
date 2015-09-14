package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.CountryModel;
import com.finappl.models.CurrencyModel;
import com.finappl.models.SettingsNotificationModel;
import com.finappl.models.UsersModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SettingsDbService extends SQLiteOpenHelper {

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

	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = Constants.DB_VERSION;

    public void logoutAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("USER_IS_DEL", Constants.DB_AFFIRMATIVE);

        // Updating an old Row
        db.update(USERS_TABLE, values,	"USER_IS_DEL = '" + Constants.DB_NONAFFIRMATIVE + "'", null);
    }

    public void saveNotificationSetting(SettingsNotificationModel settingsNotificationModelObj){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("SET_NOTIF_TIME", settingsNotificationModelObj.getSET_NOTIF_TIME());
        values.put("SET_NOTIF_BUZZ", settingsNotificationModelObj.getSET_NOTIF_BUZZ());

        // Updating an old Row
        db.update(Constants.DB_TABLE_SETTINGS_NOTIFICATIONS, values,	"USER_ID = '" + settingsNotificationModelObj.getUSER_ID() + "'", null);
    }

    public void enableDisableNotificationOnUserId(String userIdStr, boolean isEnabled){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if(isEnabled){
            values.put("SET_NOTIF_ACTIVE", Constants.DB_AFFIRMATIVE);
        }
        else{
            values.put("SET_NOTIF_ACTIVE", Constants.DB_NONAFFIRMATIVE);
        }

        // Updating an old Row
        db.update(Constants.DB_TABLE_SETTINGS_NOTIFICATIONS, values,	"USER_ID = '" + userIdStr + "'", null);
    }

    public void enableDisableNotificationVibrationsOnUserId(String userIdStr, boolean isEnabled){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if(isEnabled){
            values.put("SET_NOTIF_BUZZ", Constants.DB_AFFIRMATIVE);
        }
        else{
            values.put("SET_NOTIF_BUZZ", Constants.DB_NONAFFIRMATIVE);
        }

        // Updating an old Row
        db.update(Constants.DB_TABLE_SETTINGS_NOTIFICATIONS, values,	"USER_ID = '" + userIdStr + "'", null);
    }

    //returns 0 on fail and 1 on success
    public int savePersonalProfile(UsersModel userModelObj){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        values.put("NAME", userModelObj.getNAME());
        values.put("DOB", sdf.format(userModelObj.getDOB()));
        values.put("CNTRY_ID", userModelObj.getCNTRY_ID());
        values.put("CUR_ID", userModelObj.getCUR_ID());
        values.put("MOD_DTM", sdf.format(new Date()));

        // Updating an old Row
        return db.update(USERS_TABLE, values,	"USER_ID = '" + userModelObj.getUSER_ID() + "'", null);
    }

    public UsersModel getUserProfile(UsersModel userModelObj){
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" NAME, ");
        sqlQuerySB.append(" EMAIL, ");
        sqlQuerySB.append(" DOB, ");
        sqlQuerySB.append(" CNTRY.CNTRY_ID, ");
        sqlQuerySB.append(" CNTRY.CNTRY_NAME AS countryName, ");
        sqlQuerySB.append(" TELEPHONE, ");
        sqlQuerySB.append(" CUR.CUR_ID, ");
        sqlQuerySB.append(" CUR.CUR_NAME AS currencyName, ");
        sqlQuerySB.append(" USER.CREAT_DTM AS userCreatDtm, ");
        sqlQuerySB.append(" USER.MOD_DTM AS userModDtm, ");
        sqlQuerySB.append(" WORK_TYPE, ");
        sqlQuerySB.append(" COMPANY, ");
        sqlQuerySB.append(" SALARY, ");
        sqlQuerySB.append(" SAL_FREQ, ");
        sqlQuerySB.append(" WORK.MOD_DTM AS workCreatDtm, ");
        sqlQuerySB.append(" WORK.MOD_DTM AS workModDtm ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(USERS_TABLE+ " USER ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(COUNTRY_TABLE+" CNTRY ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CNTRY.CNTRY_ID = USER.CNTRY_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(CURRENCY_TABLE+" CUR ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CUR.CUR_ID = USER.CUR_ID ");

        sqlQuerySB.append(" LEFT OUTER JOIN ");
        sqlQuerySB.append(WORK_TIMELINE_TABLE+" WORK ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" WORK.USER_ID = USER.USER_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER.USER_ID = '"+userModelObj.getUSER_ID()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" USER_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
        UsersModel usersModelObject = null;

        while (cursor.moveToNext()){
            usersModelObject = new UsersModel();
            usersModelObject.setNAME(ColumnFetcher.getInstance().loadString(cursor, "NAME"));
            usersModelObject.setEMAIL(ColumnFetcher.getInstance().loadString(cursor, "EMAIL"));
            usersModelObject.setDOB(ColumnFetcher.getInstance().loadDate(cursor, "DOB"));
            usersModelObject.setCNTRY_ID(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_ID"));
            usersModelObject.setCountryName(ColumnFetcher.getInstance().loadString(cursor, "countryName"));
            usersModelObject.setTELEPHONE(ColumnFetcher.getInstance().loadString(cursor, "TELEPHONE"));
            usersModelObject.setCUR_ID(ColumnFetcher.getInstance().loadString(cursor, "CUR_ID"));
            usersModelObject.setCurrencyName(ColumnFetcher.getInstance().loadString(cursor, "currencyName"));
            usersModelObject.setUserCreatDtm(ColumnFetcher.getInstance().loadDateTime(cursor, "userCreatDtm"));
            usersModelObject.setUserModDtm(ColumnFetcher.getInstance().loadDateTime(cursor, "userModDtm"));
            usersModelObject.setWORK_TYPE(ColumnFetcher.getInstance().loadString(cursor, "WORK_TYPE"));
            usersModelObject.setCOMPANY(ColumnFetcher.getInstance().loadString(cursor, "COMPANY"));
            usersModelObject.setSALARY(ColumnFetcher.getInstance().loadDouble(cursor, "SALARY"));
            usersModelObject.setSAL_FREQ(ColumnFetcher.getInstance().loadString(cursor, "SAL_FREQ"));
            usersModelObject.setWorkCreatDtm(ColumnFetcher.getInstance().loadDateTime(cursor, "workCreatDtm"));
            usersModelObject.setWorkModDtm(ColumnFetcher.getInstance().loadDateTime(cursor, "workModDtm"));
        }
        cursor.close();

        return usersModelObject;
    }

    public List<CountryModel> getAllCountry(){
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CNTRY_ID, ");
        sqlQuerySB.append(" CNTRY_NAME, ");
        sqlQuerySB.append(" CNTRY_FLAG, ");
        sqlQuerySB.append(" CUR.CUR_ID, ");
        sqlQuerySB.append(" CUR_NAME AS curNameStr ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(COUNTRY_TABLE+ " CNTRY ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(CURRENCY_TABLE+" CUR ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CNTRY.CUR_ID = CUR.CUR_ID ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
        CountryModel countryModelObj = null;
        List<CountryModel> countryModelList = new ArrayList<CountryModel>();

        while (cursor.moveToNext()){
            countryModelObj = new CountryModel();
            countryModelObj.setCNTRY_ID(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_ID"));
            countryModelObj.setCNTRY_NAME(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_NAME"));
            countryModelObj.setCNTRY_FLAG(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_FLAG"));
            countryModelObj.setCUR_ID(ColumnFetcher.getInstance().loadString(cursor, "CUR_ID"));
            countryModelObj.setCurNameStr(ColumnFetcher.getInstance().loadString(cursor, "curNameStr"));
            countryModelList.add(countryModelObj);
        }
        cursor.close();

        return countryModelList;
    }

    public SettingsNotificationModel getNotifSettingsOnUserId(String userIdStr){
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SET_NOTIF_ACTIVE, ");
        sqlQuerySB.append(" SET_NOTIF_TIME, ");
        sqlQuerySB.append(" SET_NOTIF_BUZZ ");

        sqlQuerySB.append(" FROM ");

        sqlQuerySB.append(Constants.DB_TABLE_SETTINGS_NOTIFICATIONS);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '" + userIdStr + "' ");

        Log.i(CLASS_NAME, "Query to know users notification settings  :" + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        SettingsNotificationModel notificationModelObj = null;
        while (cursor.moveToNext()){
            String notifIsActiveStr = ColumnFetcher.getInstance().loadString(cursor, "SET_NOTIF_ACTIVE");
            String notifTimeStr = ColumnFetcher.getInstance().loadString(cursor, "SET_NOTIF_TIME");
            String notifBuzzStr = ColumnFetcher.getInstance().loadString(cursor, "SET_NOTIF_BUZZ");

            notificationModelObj = new SettingsNotificationModel();
            notificationModelObj.setSET_NOTIF_ACTIVE(notifIsActiveStr);
            notificationModelObj.setSET_NOTIF_TIME(notifTimeStr);
            notificationModelObj.setSET_NOTIF_BUZZ(notifBuzzStr);
        }
        cursor.close();

        return notificationModelObj;
    }

    public void enableDisableSoundsOnUserId(String userIdStr, boolean isEnabled){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if(isEnabled){
            values.put("SET_SND_ACTIVE", Constants.DB_AFFIRMATIVE);
        }
        else{
            values.put("SET_SND_ACTIVE", Constants.DB_NONAFFIRMATIVE);
        }

        // Updating an old Row
        db.update(Constants.DB_TABLE_SETTINGS_SOUNDS, values,	"USER_ID = '" + userIdStr + "'", null);
    }

    public void saveSecurityKeyUserId(String userIdStr, String keyStr){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("SET_SEC_KEY", keyStr);

        // Updating an old Row
        db.update(Constants.DB_TABLE_SETTINGS_SECURITY, values,	"USER_ID = '" + userIdStr + "'", null);
    }

    public boolean isSoundsEnabledOnUserId(String userIdStr) {
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SET_SND_ACTIVE ");

        sqlQuerySB.append(" FROM ");

        sqlQuerySB.append(Constants.DB_TABLE_SETTINGS_SOUNDS);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '" + userIdStr + "' ");

        Log.i(CLASS_NAME, "Query to know if sound is enabled  :" + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            if(Constants.DB_AFFIRMATIVE.equalsIgnoreCase(ColumnFetcher.getInstance().loadString(cursor, "SET_SND_ACTIVE"))){
                Log.i(CLASS_NAME, "This user has enabled sounds");
                return true;
            }
        }
        cursor.close();

        Log.i(CLASS_NAME, "This user has not enabled sounds");
        return false;
    }

    public boolean isNotifEnabledOnUserId(String userIdStr) {
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SET_NOTIF_ACTIVE ");

        sqlQuerySB.append(" FROM ");

        sqlQuerySB.append(Constants.DB_TABLE_SETTINGS_NOTIFICATIONS);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '" + userIdStr + "' ");

        Log.i(CLASS_NAME, "Query to know if notification is enabled  :" + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            if(Constants.DB_AFFIRMATIVE.equalsIgnoreCase(ColumnFetcher.getInstance().loadString(cursor, "SET_NOTIF_ACTIVE"))){
                Log.i(CLASS_NAME, "This user has enabled notifications");
                return true;
            }
        }
        cursor.close();

        Log.i(CLASS_NAME, "This user has not enabled notifications");
        return false;
    }

    public String getUserSecurityKeyOnUserId(String userIdStr){
        String keyStr = "";

        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SET_SEC_KEY ");

        sqlQuerySB.append(" FROM ");

        sqlQuerySB.append(Constants.DB_TABLE_SETTINGS_SECURITY);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '" + userIdStr + "' ");

        Log.i(CLASS_NAME, "Query to get Security Key on user id  :" + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            keyStr = ColumnFetcher.getInstance().loadString(cursor, "SET_SEC_KEY");
        }
        cursor.close();

        if(keyStr == null){
            keyStr = "";
        }

        return keyStr;
    }

    public boolean authenticateOnUserIdAndKey(String userIdStr, String keyStr) {
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SET_NOTIF_ACTIVE ");

        sqlQuerySB.append(" FROM ");

        sqlQuerySB.append(Constants.DB_TABLE_SETTINGS_NOTIFICATIONS);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '" + userIdStr + "' ");

        Log.i(CLASS_NAME, "Query to know if notification is enabled  :" + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            if(Constants.DB_AFFIRMATIVE.equalsIgnoreCase(ColumnFetcher.getInstance().loadString(cursor, "SET_NOTIF_ACTIVE"))){
                Log.i(CLASS_NAME, "This user has enabled notifications");
                return true;
            }
        }
        cursor.close();

        Log.i(CLASS_NAME, "This user has not enabled notifications");
        return false;
    }

    public List<CurrencyModel> getAllCurrency(){
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CUR_ID, ");
        sqlQuerySB.append(" CUR_NAME, ");
        sqlQuerySB.append(" CUR_SYMB ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(CURRENCY_TABLE);

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
        CurrencyModel currencyModelObj = null;
        List<CurrencyModel> currencyModelList = new ArrayList<CurrencyModel>();

        while (cursor.moveToNext()){
            currencyModelObj = new CurrencyModel();
            currencyModelObj.setCUR_ID(ColumnFetcher.getInstance().loadString(cursor, "CUR_ID"));
            currencyModelObj.setCUR_NAME(ColumnFetcher.getInstance().loadString(cursor, "CUR_NAME"));
            currencyModelObj.setCUR_SYMB(ColumnFetcher.getInstance().loadString(cursor, "CUR_SYMB"));
            currencyModelList.add(currencyModelObj);
        }
        cursor.close();

        return currencyModelList;
    }

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	//constructors
	public SettingsDbService(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
