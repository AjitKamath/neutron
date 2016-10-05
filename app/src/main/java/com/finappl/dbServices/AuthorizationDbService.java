package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.CountryMO;
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

    public List<CountryMO> getAllCountries(){
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CNTRY_ID, ");
        sqlQuerySB.append(" CNTRY_CODE, ");
        sqlQuerySB.append(" CNTRY_NAME, ");
        sqlQuerySB.append(" CUR_CODE, ");
        sqlQuerySB.append(" CUR ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_COUNTRY);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<CountryMO> countryModelList = new ArrayList<>();
        while (cursor.moveToNext()){
            CountryMO countryMO = new CountryMO();
            countryMO.setCNTRY_ID(ColumnFetcher.loadString(cursor, "CNTRY_ID"));
            countryMO.setCNTRY_CODE(ColumnFetcher.loadString(cursor, "CNTRY_CODE"));
            countryMO.setCNTRY_NAME(ColumnFetcher.loadString(cursor, "CNTRY_NAME"));
            countryMO.setCUR_CODE(ColumnFetcher.loadString(cursor, "CUR_CODE"));
            countryMO.setCUR(ColumnFetcher.loadString(cursor, "CUR"));
            countryModelList.add(countryMO);
        }
        cursor.close();
        db.close();
        return countryModelList;
    }

    public boolean addNewUser(UserMO userModelObj){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("USER_ID", userModelObj.getUSER_ID());
            values.put("CNTRY_ID", userModelObj.getCNTRY_ID());
            values.put("NAME", userModelObj.getNAME());
            values.put("PASS", EncryptionUtil.encrypt(userModelObj.getPASS()));
            values.put("EMAIL", userModelObj.getEMAIL());
            values.put("DOB", DB_DATE_FORMAT_SDF.format(userModelObj.getDOB()));
            values.put("TELEPHONE", userModelObj.getTELEPHONE());
            values.put("DEV_ID", userModelObj.getDEV_ID());
            values.put("CREAT_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

            long result = db.insert(DB_TABLE_USER, null, values);

            if (result == -1) {
                Log.e(CLASS_NAME, "Something went wrong while registering the user");
                return false;
            }

            //inserting a new row into SETTINGS table
            values.clear();
            values.put("SET_ID", IdGenerator.getInstance().generateUniqueId("SET"));
            values.put("USER_ID", userModelObj.getUSER_ID());
            values.put("SET_NOTIF_TIME", DB_DEFAULT_NOTIF_TIME);
            values.put("SET_NOTIF_BUZZ", DB_NONAFFIRMATIVE);
            values.put("CREAT_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

            return db.insert(DB_TABLE_SETTING, null, values) != -1;
        }
        catch(Exception e){
            Log.i(CLASS_NAME, "ERROR !! While adding a new User");
        }
        db.close();
        return false;
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
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" COUNT(USER_ID) AS COUNT ");

        sqlQuerySB.append(" FROM ");

        sqlQuerySB.append(DB_TABLE_USER);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '" + usernameStr + "' ");

        Log.i(CLASS_NAME, "Query to know if user already exists  :" + sqlQuerySB);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        if (cursor.moveToNext()){
            return ColumnFetcher.getInstance().loadInt(cursor, "COUNT") > 0;
        }
        cursor.close();
        db.close();

        Log.i(CLASS_NAME, "This username is unique in the whole universe..go ahead.");
        return false;
    }

    public UserMO getActiveUser(String userIdStr){
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" USR.USER_ID, ");
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
        sqlQuerySB.append(DB_TABLE_SETTING+" SETT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" SETT.USER_ID = USR.USER_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USR.USER_ID = '"+userIdStr+"' ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);
        UserMO userModelObject = null;
        if (cursor.moveToNext()){
            userModelObject = new UserMO();
            userModelObject.setUSER_ID(ColumnFetcher.loadString(cursor, "USER_ID"));
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
