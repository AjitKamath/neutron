package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.CountryModel;
import com.finappl.models.CurrencyModel;
import com.finappl.models.UserMO;
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

    public boolean isAuthenticUser(UserMO usersModelObj) {
        try {
            StringBuilder sqlQuerySB = new StringBuilder(50);

            sqlQuerySB.append(" SELECT ");
            sqlQuerySB.append(" COUNT(USER_ID) AS COUNT ");

            sqlQuerySB.append(" FROM ");

            sqlQuerySB.append(DB_TABLE_USER);

            sqlQuerySB.append(" WHERE ");
            sqlQuerySB.append(" USER_ID = '" + usersModelObj.getUSER_ID() + "' ");

            sqlQuerySB.append(" AND ");
            sqlQuerySB.append(" PASS = '" + EncryptionUtil.encrypt(usersModelObj.getPASS()) + "' ");

            Log.i(CLASS_NAME, "Query to know if user is authentic  :" + sqlQuerySB);
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

            if (cursor.moveToNext()) {
                if (ColumnFetcher.getInstance().loadInt(cursor, "COUNT") > 0) {
                    return true;
                }
            }
            db.close();
            cursor.close();
        }
        catch(Exception e){
            Log.e(CLASS_NAME, "ERROR !! While authenticating the user");
        }

        Log.i(CLASS_NAME, "Enemy at the gates trying to breach !!!! Code Red. Code Red.");
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

    public UserMO getActiveUser(String userIdStr){
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" NAME, ");
        sqlQuerySB.append(" EMAIL, ");
        sqlQuerySB.append(" DOB, ");
        sqlQuerySB.append(" TELEPHONE, ");
        sqlQuerySB.append(" DEV_ID, ");
        sqlQuerySB.append(" CNTRY_NAME, ");
        sqlQuerySB.append(" CUR, ");
        sqlQuerySB.append(" CUR_CODE, ");
        sqlQuerySB.append(" SET_NOTIF_TIME, ");
        sqlQuerySB.append(" SET_NOTIF_BUZZ, ");
        sqlQuerySB.append(" SET_SEC_PIN ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_USER+ " USR ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_COUNTRY+" CNTRY ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CNTRY.CNTRY_ID = USR.CNTRY_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_SETTING+" SET ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" SET.USER_ID = USR.USER_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USR.USER_ID = '"+userIdStr+"' ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
        UserMO userModelObject = null;
        if (cursor.moveToNext()){
            userModelObject = new UserMO();
            userModelObject.setNAME(ColumnFetcher.loadString(cursor, "NAME"));
            userModelObject.setEMAIL(ColumnFetcher.loadString(cursor, "EMAIL"));
            userModelObject.setDOB(ColumnFetcher.loadDate(cursor, "DOB"));
            userModelObject.setTELEPHONE(ColumnFetcher.loadString(cursor, "TELEPHONE"));
            userModelObject.setDEV_ID(ColumnFetcher.loadString(cursor, "DEV_ID"));
            userModelObject.setCNTRY_NAME(ColumnFetcher.loadString(cursor, "CNTRY_NAME"));
            userModelObject.setCUR(ColumnFetcher.loadString(cursor, "CUR"));
            userModelObject.setCUR_CODE(ColumnFetcher.loadString(cursor, "CUR_CODE"));
            userModelObject.setSET_NOTIF_TIME(ColumnFetcher.loadString(cursor, "SET_NOTIF_TIME"));
            userModelObject.setSET_NOTIF_BUZZ(ColumnFetcher.loadString(cursor, "SET_NOTIF_BUZZ"));
            userModelObject.setSET_SEC_PIN(ColumnFetcher.loadString(cursor, "SET_SEC_PIN"));
        }
        cursor.close();
        db.close();
        return userModelObject;
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
