package com.finapple.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finapple.model.CountryModel;
import com.finapple.model.CurrencyModel;
import com.finapple.model.UsersModel;
import com.finapple.util.ColumnFetcher;
import com.finapple.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorizationDbService extends SQLiteOpenHelper {

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

    public long addNewUser(UsersModel userModelObj){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //logout all users first
        values.put("USER_IS_DEL", Constants.DB_AFFIRMATIVE);
        db.update(USERS_TABLE, values,	"USER_IS_DEL = '" + Constants.DB_NONAFFIRMATIVE + "'", null);

        values.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        values.put("USER_ID", userModelObj.getUSER_ID());
        values.put("NAME", userModelObj.getNAME());
        values.put("PASS", userModelObj.getPASS());
        values.put("EMAIL", userModelObj.getEMAIL());
        values.put("DOB", sdf.format(userModelObj.getDOB()));
        values.put("CNTRY_ID", userModelObj.getCNTRY_ID());
        values.put("TELEPHONE", userModelObj.getTELEPHONE());
        values.put("NOTIF_TIME", Constants.DB_DEFAULT_NOTIF_TIME);
        values.put("CUR_ID", userModelObj.getCUR_ID());
        values.put("DEV_ID", userModelObj.getDEV_ID());
        values.put("USER_IS_DEL", Constants.DB_NONAFFIRMATIVE);
        values.put("CREAT_DTM", sdf1.format(new Date()));

        // Inserting a new Row in USERS_TABLE
        return db.insert(USERS_TABLE, null, values);
    }

    public boolean isAuthenticUser(UsersModel usersModelObj) {
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(USER_ID) AS COUNT ");

        sqlQuerySB.append(" FROM ");

        sqlQuerySB.append(USERS_TABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '" + usersModelObj.getUSER_ID() + "' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" PASS = '"+usersModelObj.getPASS()+"' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" USER_IS_DEL = '"+Constants.DB_AFFIRMATIVE+"' ");

        Log.i(CLASS_NAME, "Query to know if user is authentic  :" + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            if(ColumnFetcher.getInstance().loadInt(cursor, "COUNT") > 0){
                Log.i(CLASS_NAME, "Aaahhh... the master !! How can i serve you Mr. User. Use finappl all you want to use");

                ContentValues values = new ContentValues();
                values.put("USER_IS_DEL", Constants.DB_NONAFFIRMATIVE);

                // Updating an old Row
                db.update(USERS_TABLE, values,	"USER_ID = '" + usersModelObj.getUSER_ID() + "'", null);

                return true;
            }
        }
        cursor.close();

        Log.i(CLASS_NAME, "Enemy at the gates trying to breach !!!! Code Red. Code Red.");
        return false;
    }

    public boolean isUserExists(String usernameStr) {
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(USER_ID) AS COUNT ");

        sqlQuerySB.append(" FROM ");

        sqlQuerySB.append(USERS_TABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '" + usernameStr + "' ");

        Log.i(CLASS_NAME, "Query to know if user already exists  :" + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            if(ColumnFetcher.getInstance().loadInt(cursor, "COUNT") > 0){
                Log.i(CLASS_NAME, "No no no...you shant use this username... this username hath been taketh already");
                return true;
            }
        }
        cursor.close();

        Log.i(CLASS_NAME, "This username is unique in the whole universe..go ahead.");
        return false;
    }

    public Map<Integer, UsersModel> getActiveUser(){
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" USER.USER_ID, ");
        sqlQuerySB.append(" NAME, ");
        sqlQuerySB.append(" NAME, ");
        sqlQuerySB.append(" EMAIL, ");
        sqlQuerySB.append(" DOB, ");
        sqlQuerySB.append(" NOTIF_TIME, ");
        sqlQuerySB.append(" CNTRY.CNTRY_ID, ");
        sqlQuerySB.append(" CNTRY.CNTRY_NAME AS countryName, ");
        sqlQuerySB.append(" TELEPHONE, ");
        sqlQuerySB.append(" CUR.CUR_ID, ");
        sqlQuerySB.append(" CUR.CUR_NAME AS currencyName, ");
        sqlQuerySB.append(" CUR.CUR_TXT AS currencyText, ");
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
        sqlQuerySB.append(" USER.USER_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
        UsersModel usersModelObject = null;
        Map<Integer, UsersModel> userModelMap = new HashMap<Integer, UsersModel>();

        while (cursor.moveToNext()){
            usersModelObject = new UsersModel();
            usersModelObject.setUSER_ID(ColumnFetcher.getInstance().loadString(cursor, "USER_ID"));
            usersModelObject.setNAME(ColumnFetcher.getInstance().loadString(cursor, "NAME"));
            usersModelObject.setEMAIL(ColumnFetcher.getInstance().loadString(cursor, "EMAIL"));
            usersModelObject.setDOB(ColumnFetcher.getInstance().loadDate(cursor, "DOB"));
            usersModelObject.setNOTIF_TIME(ColumnFetcher.getInstance().loadString(cursor, "NOTIF_TIME"));
            usersModelObject.setCNTRY_ID(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_ID"));
            usersModelObject.setCountryName(ColumnFetcher.getInstance().loadString(cursor, "countryName"));
            usersModelObject.setTELEPHONE(ColumnFetcher.getInstance().loadString(cursor, "TELEPHONE"));
            usersModelObject.setCUR_ID(ColumnFetcher.getInstance().loadString(cursor, "CUR_ID"));
            usersModelObject.setCurrencyText(ColumnFetcher.getInstance().loadString(cursor, "currencyText"));
            usersModelObject.setCurrencyName(ColumnFetcher.getInstance().loadString(cursor, "currencyName"));
            usersModelObject.setUserCreatDtm(ColumnFetcher.getInstance().loadDateTime(cursor, "userCreatDtm"));
            usersModelObject.setUserModDtm(ColumnFetcher.getInstance().loadDateTime(cursor, "userModDtm"));
            usersModelObject.setWORK_TYPE(ColumnFetcher.getInstance().loadString(cursor, "WORK_TYPE"));
            usersModelObject.setCOMPANY(ColumnFetcher.getInstance().loadString(cursor, "COMPANY"));
            usersModelObject.setSALARY(ColumnFetcher.getInstance().loadDouble(cursor, "SALARY"));
            usersModelObject.setSAL_FREQ(ColumnFetcher.getInstance().loadString(cursor, "SAL_FREQ"));
            usersModelObject.setWorkCreatDtm(ColumnFetcher.getInstance().loadDateTime(cursor, "workCreatDtm"));
            usersModelObject.setWorkModDtm(ColumnFetcher.getInstance().loadDateTime(cursor, "workModDtm"));

            userModelMap.put(cursor.getPosition(), usersModelObject);
        }
        cursor.close();

        return userModelMap;
    }

    //Gets the most recently used username from the db
    public String getRecentUsername(){
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" USER_ID, ");
        sqlQuerySB.append(" MAX(CREAT_DTM), ");
        sqlQuerySB.append(" MAX(MOD_DTM) ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(USERS_TABLE);

        Log.i(CLASS_NAME, "Query to fetch last used username  :"+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            return ColumnFetcher.getInstance().loadString(cursor, "USER_ID");
        }
        cursor.close();

        Log.e(CLASS_NAME, "If i'm printing, you must have screwed up. User_id cannot be null");
        return "";
    }


    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	public AuthorizationDbService(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
