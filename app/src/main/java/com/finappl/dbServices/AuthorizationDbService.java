package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.UserMO;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.EncryptionUtil;

import java.util.Date;

import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT_SDF;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_TABLE_COUNTRY;
import static com.finappl.utils.Constants.DB_TABLE_USER;
import static com.finappl.utils.Constants.DB_VERSION;
import static com.finappl.utils.Constants.DEFAULT_COUNTRY;

public class AuthorizationDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    public boolean addNewUser(UserMO userModelObj){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if(isUserExists(userModelObj.getUSER_ID())){
               Log.i(CLASS_NAME, "User("+userModelObj.getUSER_ID()+") already exists in local DB. So not adding a duplicate");
                return true;
            }

            ContentValues values = new ContentValues();
            values.put("USER_ID", userModelObj.getUSER_ID());
            values.put("CNTRY_ID", DEFAULT_COUNTRY);
            values.put("NAME", userModelObj.getNAME());
            values.put("EMAIL", userModelObj.getEMAIL());
            values.put("CREAT_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

            long result = db.insert(DB_TABLE_USER, null, values);

            if (result == -1) {
                Log.e(CLASS_NAME, "Something went wrong while registering the user");
                return false;
            }
        }
        catch(Exception e){
            Log.i(CLASS_NAME, "ERROR !! While adding a new User");
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public boolean updateUser(UserMO userModelObj){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("CNTRY_ID", userModelObj.getCNTRY_ID());
            values.put("NAME", userModelObj.getNAME());
            values.put("EMAIL", userModelObj.getEMAIL());
            values.put("TELEPHONE", userModelObj.getTELEPHONE());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

            long result = db.update(DB_TABLE_USER, values, "USER_ID = '" + userModelObj.getUSER_ID() + "'", null);

            if (result == -1) {
                Log.e(CLASS_NAME, "Something went wrong while updating the user ");
                return false;
            }
        }
        catch(Exception e){
            Log.i(CLASS_NAME, "ERROR !! While adding a new User");
            return false;
        }
        finally {
            db.close();
        }
        return true;
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
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ");
        sb.append(" COUNT(USER_ID) AS COUNT ");

        sb.append(" FROM ");

        sb.append(DB_TABLE_USER);

        sb.append(" WHERE ");
        sb.append(" USER_ID = '" + usernameStr + "' ");

        Log.i(CLASS_NAME, "Query to know if user already exists  :" + sb);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sb), null);

        if (cursor.moveToNext()){
            return ColumnFetcher.getInstance().loadInt(cursor, "COUNT") > 0;
        }
        cursor.close();
        db.close();

        Log.i(CLASS_NAME, "This username is unique in the whole universe..go ahead.");
        return false;
    }

    public UserMO getActiveUser(String userIdStr){
        if(userIdStr == null || userIdStr.trim().isEmpty()){
            return null;
        }

        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" USR.USER_ID, ");
        sqlQuerySB.append(" METRIC, ");
        sqlQuerySB.append(" NAME, ");
        sqlQuerySB.append(" EMAIL, ");
        sqlQuerySB.append(" TELEPHONE, ");
        sqlQuerySB.append(" CNTRY_NAME, ");
        sqlQuerySB.append(" CUR, ");
        sqlQuerySB.append(" CUR_CODE, ");
        sqlQuerySB.append(" CNTRY.CNTRY_ID ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_USER+ " USR ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_COUNTRY+" CNTRY ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CNTRY.CNTRY_ID = USR.CNTRY_ID ");
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
            userModelObject.setTELEPHONE(ColumnFetcher.loadString(cursor, "TELEPHONE"));
            userModelObject.setCNTRY_NAME(ColumnFetcher.loadString(cursor, "CNTRY_NAME"));
            userModelObject.setCUR(ColumnFetcher.loadString(cursor, "CUR"));
            userModelObject.setCUR_CODE(ColumnFetcher.loadString(cursor, "CUR_CODE"));
            userModelObject.setMETRIC(ColumnFetcher.loadString(cursor, "METRIC"));
            userModelObject.setCNTRY_ID(ColumnFetcher.loadString(cursor, "CNTRY_ID"));
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
        sqlQuerySB.append(DB_TABLE_USER);

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
