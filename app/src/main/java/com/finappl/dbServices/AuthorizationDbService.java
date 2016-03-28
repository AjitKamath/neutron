package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.CountryModel;
import com.finappl.models.CurrencyModel;
import com.finappl.models.UsersModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;
import com.finappl.utils.EncryptionUtil;
import com.finappl.utils.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.finappl.utils.Constants.*;

public class AuthorizationDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

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
        sqlQuerySB.append(DB_TABLE_COUNTRYTABLE+ " CNTRY ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_CURRENCYTABLE+" CUR ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CNTRY.CUR_ID = CUR.CUR_ID ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
        CountryModel countryModelObj;
        List<CountryModel> countryModelList = new ArrayList<>();

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
        db.close();
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
        sqlQuerySB.append(DB_TABLE_CURRENCYTABLE);

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
        CurrencyModel currencyModelObj = null;
        List<CurrencyModel> currencyModelList = new ArrayList<>();

        while (cursor.moveToNext()){
            currencyModelObj = new CurrencyModel();
            currencyModelObj.setCUR_ID(ColumnFetcher.getInstance().loadString(cursor, "CUR_ID"));
            currencyModelObj.setCUR_NAME(ColumnFetcher.getInstance().loadString(cursor, "CUR_NAME"));
            currencyModelObj.setCUR_SYMB(ColumnFetcher.getInstance().loadString(cursor, "CUR_SYMB"));
            currencyModelList.add(currencyModelObj);
        }
        cursor.close();
        db.close();
        return currencyModelList;
    }

    public long addNewUser(UsersModel userModelObj){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();

            //logout all users first
            values.put("USER_IS_DEL", DB_AFFIRMATIVE);
            db.update(DB_TABLE_USERSTABLE, values, "USER_IS_DEL = '" + DB_NONAFFIRMATIVE + "'", null);

            values.clear();

            values.put("USER_ID", userModelObj.getUSER_ID());
            values.put("NAME", userModelObj.getNAME());
            values.put("PASS", EncryptionUtil.encrypt(userModelObj.getPASS()));
            values.put("EMAIL", userModelObj.getEMAIL());
            values.put("DOB", simpleDateFormat.format(userModelObj.getDOB()));
            values.put("CNTRY_ID", userModelObj.getCNTRY_ID());
            values.put("TELEPHONE", userModelObj.getTELEPHONE());
            values.put("CUR_ID", userModelObj.getCUR_ID());
            values.put("DEV_ID", userModelObj.getDEV_ID());
            values.put("USER_IS_DEL", DB_NONAFFIRMATIVE);
            values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));

            // Inserting a new Row in USERS_TABLE
            long result = db.insert(DB_TABLE_USERSTABLE, null, values);

            if (result == -1) {
                Log.e(CLASS_NAME, "Something went wrong while registering the user");
                return result;
            }

            //inserting a new row into SETTINGS_NOTIFICATIONS table
            values.clear();
            values.put("SET_NOTIF_ID", IdGenerator.getInstance().generateUniqueId("SET_NOTIF"));
            values.put("SET_NOTIF_ACTIVE", DB_AFFIRMATIVE);
            values.put("SET_NOTIF_TIME", DB_DEFAULT_NOTIF_TIME);
            values.put("SET_NOTIF_BUZZ", DB_NONAFFIRMATIVE);
            values.put("USER_ID", userModelObj.getUSER_ID());
            values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));

            result = db.insert(DB_TABLE_SETTINGS_NOTIFICATIONS, null, values);

            if (result == -1) {
                Log.e(CLASS_NAME, "Something went wrong while registering the user");
                return result;
            }

            //inserting a new row into SETTINGS_SOUNDS table
            values.clear();
            values.put("SET_SND_ID", IdGenerator.getInstance().generateUniqueId("SET_SND"));
            values.put("SET_SND_ACTIVE", DB_AFFIRMATIVE);
            values.put("USER_ID", userModelObj.getUSER_ID());
            values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));

            result = db.insert(DB_TABLE_SETTINGS_SOUNDS, null, values);

            if (result == -1) {
                Log.e(CLASS_NAME, "Something went wrong while registering the user");
                return result;
            }

            //inserting a new row into SETTINGS_SECURITY table
            values.clear();
            values.put("SET_SEC_ID", IdGenerator.getInstance().generateUniqueId("SET_SEC"));
            //values.put("SET_SEC_ACTIVE", DB_AFFIRMATIVE);
            values.put("SET_SEC_ACTIVE", DB_NONAFFIRMATIVE);
            values.put("USER_ID", userModelObj.getUSER_ID());
            values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));

            result = db.insert(DB_TABLE_SETTINGS_SECURITY, null, values);

            return result;
        }
        catch(Exception e){
            Log.i(CLASS_NAME, "ERROR !! While adding a new User");
        }
        db.close();
        return -1;
    }

    public boolean isAuthenticUser(UsersModel usersModelObj) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            StringBuilder sqlQuerySB = new StringBuilder(50);

            sqlQuerySB.append(" SELECT ");
            sqlQuerySB.append(" COUNT(USER_ID) AS COUNT ");

            sqlQuerySB.append(" FROM ");

            sqlQuerySB.append(DB_TABLE_USERSTABLE);

            sqlQuerySB.append(" WHERE ");
            sqlQuerySB.append(" USER_ID = '" + usersModelObj.getUSER_ID() + "' ");

            sqlQuerySB.append(" AND ");
            sqlQuerySB.append(" PASS = '" + EncryptionUtil.encrypt(usersModelObj.getPASS()) + "' ");

            sqlQuerySB.append(" AND ");
            sqlQuerySB.append(" USER_IS_DEL = '" + DB_AFFIRMATIVE + "' ");

            Log.i(CLASS_NAME, "Query to know if user is authentic  :" + sqlQuerySB);
            Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

            while (cursor.moveToNext()) {
                if (ColumnFetcher.getInstance().loadInt(cursor, "COUNT") > 0) {
                    Log.i(CLASS_NAME, "Aaahhh... the master !! How can i serve you Mr. User. Use finappl all you want to use");

                    ContentValues values = new ContentValues();
                    values.put("USER_IS_DEL", DB_NONAFFIRMATIVE);

                    // Updating an old Row
                    db.update(DB_TABLE_USERSTABLE, values, "USER_ID = '" + usersModelObj.getUSER_ID() + "'", null);

                    return true;
                }
            }
            cursor.close();
        }
        catch(Exception e){
            Log.e(CLASS_NAME, "ERROR !! While authenticating the user");
        }

        Log.i(CLASS_NAME, "Enemy at the gates trying to breach !!!! Code Red. Code Red.");
        db.close();
        return false;
    }

    public boolean isUserExists(String usernameStr) {
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(USER_ID) AS COUNT ");

        sqlQuerySB.append(" FROM ");

        sqlQuerySB.append(DB_TABLE_USERSTABLE);

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
        db.close();
        return false;
    }

    public Map<Integer, UsersModel> getActiveUser(){
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" USER.USER_ID, ");
        sqlQuerySB.append(" NAME, ");
        sqlQuerySB.append(" EMAIL, ");
        sqlQuerySB.append(" DOB, ");
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
        sqlQuerySB.append(" WORK.MOD_DTM AS workModDtm, ");
        sqlQuerySB.append(" SET_NOTIF_ACTIVE, ");
        sqlQuerySB.append(" SET_NOTIF_TIME, ");
        sqlQuerySB.append(" SET_NOTIF_BUZZ, ");
        sqlQuerySB.append(" SET_SND_ACTIVE, ");
        sqlQuerySB.append(" SET_SEC_ACTIVE, ");
        sqlQuerySB.append(" SET_SEC_PIN ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_USERSTABLE+ " USER ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_COUNTRYTABLE+" CNTRY ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CNTRY.CNTRY_ID = USER.CNTRY_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_CURRENCYTABLE+" CUR ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CUR.CUR_ID = USER.CUR_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_SETTINGS_NOTIFICATIONS+" NOTIF ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" NOTIF.USER_ID = USER.USER_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_SETTINGS_SOUNDS+" SND ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" SND.USER_ID = USER.USER_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_SETTINGS_SECURITY+" SEC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" SEC.USER_ID = USER.USER_ID ");

        sqlQuerySB.append(" LEFT OUTER JOIN ");
        sqlQuerySB.append(DB_TABLE_WORK_TIMELINETABLE+" WORK ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" WORK.USER_ID = USER.USER_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER.USER_IS_DEL = '"+DB_NONAFFIRMATIVE+"' ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
        UsersModel usersModelObject;
        Map<Integer, UsersModel> userModelMap = new HashMap<>();

        while (cursor.moveToNext()){
            usersModelObject = new UsersModel();
            usersModelObject.setUSER_ID(ColumnFetcher.getInstance().loadString(cursor, "USER_ID"));
            usersModelObject.setNAME(ColumnFetcher.getInstance().loadString(cursor, "NAME"));
            usersModelObject.setEMAIL(ColumnFetcher.getInstance().loadString(cursor, "EMAIL"));
            usersModelObject.setDOB(ColumnFetcher.getInstance().loadDate(cursor, "DOB"));
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
            usersModelObject.setSET_NOTIF_TIME(ColumnFetcher.getInstance().loadString(cursor, "SET_NOTIF_TIME"));
            usersModelObject.setSET_NOTIF_ACTIVE(ColumnFetcher.getInstance().loadString(cursor, "SET_NOTIF_ACTIVE"));
            usersModelObject.setSET_NOTIF_BUZZ(ColumnFetcher.getInstance().loadString(cursor, "SET_NOTIF_BUZZ"));
            usersModelObject.setSET_SND_ACTIVE(ColumnFetcher.getInstance().loadString(cursor, "SET_SND_ACTIVE"));
            usersModelObject.setSET_SEC_ACTIVE(ColumnFetcher.getInstance().loadString(cursor, "SET_SEC_ACTIVE"));
            usersModelObject.setSET_SEC_PIN(ColumnFetcher.getInstance().loadString(cursor, "SET_SEC_PIN"));

            userModelMap.put(cursor.getPosition(), usersModelObject);
        }
        cursor.close();
        db.close();
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
        sqlQuerySB.append(DB_TABLE_USERSTABLE);

        Log.i(CLASS_NAME, "Query to fetch last used username  :"+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            return ColumnFetcher.getInstance().loadString(cursor, "USER_ID");
        }
        cursor.close();

        Log.e(CLASS_NAME, "If i'm printing, you must have screwed up. User_id cannot be null");
        db.close();
        return "";
    }

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	public AuthorizationDbService(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}
}
