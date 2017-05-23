package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.AccountMO;
import com.finappl.models.ActivitiesMO;
import com.finappl.models.BudgetMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.CountryMO;
import com.finappl.models.DayLedger;
import com.finappl.models.RepeatMO;
import com.finappl.models.SpentOnMO;
import com.finappl.models.TagsMO;
import com.finappl.models.TransactionMO;
import com.finappl.models.TransferMO;
import com.finappl.models.UserMO;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.DateTimeUtil;
import com.finappl.utils.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.DB_AFFIRMATIVE;
import static com.finappl.utils.Constants.DB_DATE_FORMAT;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT_SDF;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_NONAFFIRMATIVE;
import static com.finappl.utils.Constants.DB_TABLE_ACCOUNT;
import static com.finappl.utils.Constants.DB_TABLE_BUDGET;
import static com.finappl.utils.Constants.DB_TABLE_CATEGORY;
import static com.finappl.utils.Constants.DB_TABLE_COUNTRY;
import static com.finappl.utils.Constants.DB_TABLE_REPEAT;
import static com.finappl.utils.Constants.DB_TABLE_SPENTON;
import static com.finappl.utils.Constants.DB_TABLE_TAGS;
import static com.finappl.utils.Constants.DB_TABLE_TRANSACTION;
import static com.finappl.utils.Constants.DB_TABLE_TRANSFER;
import static com.finappl.utils.Constants.DB_VERSION;
import static com.finappl.utils.Constants.REPEATS_DAY;
import static com.finappl.utils.Constants.REPEATS_MONTH;
import static com.finappl.utils.Constants.REPEATS_WEEK;
import static com.finappl.utils.Constants.REPEATS_YEAR;
import static com.finappl.utils.Constants.UN_IDENTIFIED_OBJECT_TYPE;


public class CalendarDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    private static CalendarDbService sInstance = null;

    public void updateAll(UserMO user, Object object) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values;
        Date now = new Date();
        //category
        if (object instanceof CategoryMO) {
            CategoryMO category = (CategoryMO) object;

            //transaction
            values = new ContentValues();
            values.put("CAT_ID", category.getDefaultCategoryId());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(now));
            db.update(DB_TABLE_TRANSACTION, values, "CAT_ID = '" + category.getCAT_ID() + "'", null);

            //no transfers delete for category

            //Budgets
            values = new ContentValues();
            values.put("BUDGET_GRP_ID", category.getDefaultCategoryId());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(now));
            db.update(DB_TABLE_BUDGET, values, "BUDGET_GRP_ID = '" + category.getCAT_ID() + "' AND USER_ID = '" + user.getUSER_ID() + "'", null);
        }
        //account
        else if (object instanceof AccountMO) {
            AccountMO account = (AccountMO) object;

            //transactions
            values = new ContentValues();
            values.put("ACC_ID", account.getDefaultAccountId());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(now));
            db.update(DB_TABLE_TRANSACTION, values, "ACC_ID = '" + account.getACC_ID() + "' AND USER_ID = '" + user.getUSER_ID() + "'", null);

            //transfers
            values = new ContentValues();
            values.put("ACC_ID_FRM", account.getDefaultAccountId());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(now));
            db.update(DB_TABLE_TRANSFER, values, "ACC_ID_FRM = '" + account.getACC_ID() + "' AND USER_ID = '" + user.getUSER_ID() + "'", null);

            values = new ContentValues();
            values.put("ACC_ID_TO", account.getDefaultAccountId());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(now));
            db.update(DB_TABLE_TRANSFER, values, "ACC_ID_TO = '" + account.getACC_ID() + "' AND USER_ID = '" + user.getUSER_ID() + "'", null);

            //Budgets
            values = new ContentValues();
            values.put("BUDGET_GRP_ID", account.getDefaultAccountId());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(now));
            db.update(DB_TABLE_BUDGET, values, "BUDGET_GRP_ID = '" + account.getACC_ID() + "' AND USER_ID = '" + user.getUSER_ID() + "'", null);
        }
        //spent on
        else if (object instanceof SpentOnMO) {
            SpentOnMO spenton = (SpentOnMO) object;

            //transactions
            values = new ContentValues();
            values.put("SPNT_ON_ID", spenton.getDefaultSpentonId());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(now));
            db.update(DB_TABLE_TRANSACTION, values, "SPNT_ON_ID = '" + spenton.getSPNT_ON_ID() + "' AND USER_ID = '" + user.getUSER_ID() + "'", null);

            //no transfers delete for spenton

            //Budgets
            values = new ContentValues();
            values.put("BUDGET_GRP_ID", spenton.getDefaultSpentonId());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(now));
            db.update(DB_TABLE_BUDGET, values, "BUDGET_GRP_ID = '" + spenton.getSPNT_ON_ID() + "' AND USER_ID = '" + user.getUSER_ID() + "'", null);
        }
    }

    public SpentOnMO getDefaultSpentOn(String userId) {
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SPNT_ON_ID, ");
        sqlQuerySB.append(" SPNT_ON_IMG, ");
        sqlQuerySB.append(" SPNT_ON_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_SPENTON);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID IN ('" + ADMIN_USERID + "',  '" + userId + "') ");
        sqlQuerySB.append(" AND SPNT_ON_IS_DEF = '" + DB_AFFIRMATIVE + "' ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        if (cursor.moveToNext()) {
            SpentOnMO spentOn = new SpentOnMO();
            spentOn.setSPNT_ON_ID(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_ID"));
            spentOn.setSPNT_ON_IMG(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_IMG"));
            spentOn.setSPNT_ON_NAME(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME"));
            return spentOn;
        }
        cursor.close();
        db.close();
        return new SpentOnMO();
    }

    public AccountMO getDefaultAccount(String userId) {
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" ACC_ID, ");
        sqlQuerySB.append(" ACC_IMG, ");
        sqlQuerySB.append(" ACC_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID IN ('" + ADMIN_USERID + "',  '" + userId + "') ");
        sqlQuerySB.append(" AND ACC_IS_DEF = '" + DB_AFFIRMATIVE + "' ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        if (cursor.moveToNext()) {
            AccountMO account = new AccountMO();
            account.setACC_ID(ColumnFetcher.getInstance().loadString(cursor, "ACC_ID"));
            account.setACC_IMG(ColumnFetcher.getInstance().loadString(cursor, "ACC_IMG"));
            account.setACC_NAME(ColumnFetcher.getInstance().loadString(cursor, "ACC_NAME"));
            return account;
        }
        cursor.close();
        db.close();
        return new AccountMO();
    }

    public CategoryMO getDefaultCategory(String userId) {
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CAT_ID, ");
        sqlQuerySB.append(" CAT_IMG, ");
        sqlQuerySB.append(" CAT_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_CATEGORY);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID IN ('" + ADMIN_USERID + "',  '" + userId + "') ");
        sqlQuerySB.append(" AND CAT_IS_DEF = '" + DB_AFFIRMATIVE + "' ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        if (cursor.moveToNext()) {
            CategoryMO category = new CategoryMO();
            category.setCAT_ID(ColumnFetcher.getInstance().loadString(cursor, "CAT_ID"));
            category.setCAT_IMG(ColumnFetcher.getInstance().loadString(cursor, "CAT_IMG"));
            category.setCAT_NAME(ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME"));
            return category;
        }
        cursor.close();
        db.close();
        return new CategoryMO();
    }

    public int getBudgetsCount(UserMO user, Object object) {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ");
        sb.append(" COUNT(BUDGET_ID) AS COUNT ");

        sb.append(" FROM ");

        sb.append(DB_TABLE_BUDGET);

        sb.append(" WHERE ");
        sb.append(" USER_ID = '" + user.getUSER_ID() + "' ");

        if (object instanceof CategoryMO) {
            sb.append(" AND BUDGET_GRP_ID = '" + ((CategoryMO) object).getCAT_ID() + "' ");
        } else if (object instanceof AccountMO) {
            sb.append(" AND BUDGET_GRP_ID = '" + ((AccountMO) object).getACC_ID() + "' ");
        } else if (object instanceof SpentOnMO) {
            sb.append(" AND BUDGET_GRP_ID = '" + ((SpentOnMO) object).getSPNT_ON_ID() + "' ");
        } else {
            Log.i(CLASS_NAME, UN_IDENTIFIED_OBJECT_TYPE + " : " + sb);
            return 0;
        }

        Log.i(CLASS_NAME, "Query to know the count of budgets created :" + sb);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sb), null);

        if (cursor.moveToNext()) {
            return ColumnFetcher.loadInt(cursor, "COUNT");
        }
        cursor.close();
        db.close();

        return 0;
    }

    public int getTransfersCount(UserMO user, Object object) {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ");
        sb.append(" COUNT(TRNFR_ID) AS COUNT ");

        sb.append(" FROM ");

        sb.append(DB_TABLE_TRANSFER);

        sb.append(" WHERE ");
        sb.append(" USER_ID = '" + user.getUSER_ID() + "' ");

        if (object instanceof AccountMO) {
            sb.append(" AND (ACC_ID_FRM = '" + ((AccountMO) object).getACC_ID() + "' ");
            sb.append(" OR ACC_ID_TO = '" + ((AccountMO) object).getACC_ID() + "') ");
        } else {
            Log.i(CLASS_NAME, UN_IDENTIFIED_OBJECT_TYPE + " : " + sb);
            return 0;
        }

        Log.i(CLASS_NAME, "Query to know the count of transfers created :" + sb);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sb), null);

        if (cursor.moveToNext()) {
            return ColumnFetcher.loadInt(cursor, "COUNT");
        }
        cursor.close();
        db.close();

        return 0;
    }

    public int getTransactionsCount(UserMO user, Object object) {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ");
        sb.append(" COUNT(TRAN_ID) AS COUNT ");

        sb.append(" FROM ");

        sb.append(DB_TABLE_TRANSACTION);

        sb.append(" WHERE ");
        sb.append(" USER_ID = '" + user.getUSER_ID() + "' ");

        if (object instanceof CategoryMO) {
            sb.append(" AND CAT_ID = '" + ((CategoryMO) object).getCAT_ID() + "' ");
        } else if (object instanceof AccountMO) {
            sb.append(" AND ACC_ID = '" + ((AccountMO) object).getACC_ID() + "' ");
        } else if (object instanceof SpentOnMO) {
            sb.append(" AND SPNT_ON_ID = '" + ((SpentOnMO) object).getSPNT_ON_ID() + "' ");
        } else {
            Log.i(CLASS_NAME, UN_IDENTIFIED_OBJECT_TYPE + " : " + sb);
            return 0;
        }

        Log.i(CLASS_NAME, "Query to know the count of transactions created :" + sb);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sb), null);

        if (cursor.moveToNext()) {
            return ColumnFetcher.loadInt(cursor, "COUNT");
        }
        cursor.close();
        db.close();

        return 0;
    }

    public boolean deleteSpenton(String spentonIdStr) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = db.delete(DB_TABLE_SPENTON, "SPNT_ON_ID = '" + spentonIdStr + "'", null) > 0;

        if (result) {
            db.delete(DB_TABLE_TAGS, "TAG_TYPE_ID = '" + spentonIdStr + "'", null);
        }

        return result;
    }

    public boolean deleteBudget(String budget_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DB_TABLE_BUDGET, "BUDGET_ID = '" + budget_id + "'", null) > 0;
    }

    public boolean deleteAccount(String accountIdStr) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = db.delete(DB_TABLE_ACCOUNT, "ACC_ID = '" + accountIdStr + "'", null) > 0;

        if (result) {
            db.delete(DB_TABLE_TAGS, "TAG_TYPE_ID = '" + accountIdStr + "'", null);
        }

        return result;
    }

    public boolean deleteCategory(String categoryIdStr) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = db.delete(DB_TABLE_CATEGORY, "CAT_ID = '" + categoryIdStr + "'", null) > 0;

        if (result) {
            db.delete(DB_TABLE_TAGS, "TAG_TYPE_ID = '" + categoryIdStr + "'", null);
        }

        return result;
    }

    public boolean updateSpenton(SpentOnMO spenton) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Date date = new Date();

            ContentValues values = new ContentValues();
            values.put("SPNT_ON_NAME", spenton.getSPNT_ON_NAME());
            values.put("SPNT_ON_IMG", spenton.getSPNT_ON_IMG());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(date));

            long result = db.update(DB_TABLE_SPENTON, values, "SPNT_ON_ID = '" + spenton.getSPNT_ON_ID() + "'", null);

            if (result == -1) {
                Log.e(CLASS_NAME, "Something went wrong while updating the spent on ");
                return false;
            }

            TagsMO tag = new TagsMO();
            tag.setUSER_ID(spenton.getUSER_ID());
            tag.setTAG_TYPE("SPENT ON");
            tag.setTAG_TYPE_ID(spenton.getSPNT_ON_ID());
            tag.setTAGS(spenton.getTagsStr());

            if (isTagAlreadyExist(tag)) {
                return updateTags(tag);
            } else {
                addNewTags(tag);
            }
        } catch (Exception e) {
            Log.i(CLASS_NAME, "ERROR !! While updating a spent on");
            return false;
        } finally {
            db.close();
        }
        return true;
    }

    public boolean updateAccount(AccountMO account){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Date date = new Date();

            ContentValues values = new ContentValues();
            values.put("ACC_NAME", account.getACC_NAME());
            values.put("ACC_IMG", account.getACC_IMG());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(date));

            long result = db.update(DB_TABLE_ACCOUNT, values, "ACC_ID = '" + account.getACC_ID() + "'", null);

            if (result == -1) {
                Log.e(CLASS_NAME, "Something went wrong while updating the account ");
                return false;
            }

            TagsMO tag = new TagsMO();
            tag.setUSER_ID(account.getUSER_ID());
            tag.setTAG_TYPE("ACCOUNT");
            tag.setTAG_TYPE_ID(account.getACC_ID());
            tag.setTAGS(account.getTagsStr());

            if(isTagAlreadyExist(tag)){
                return updateTags(tag);
            }
            else{
                addNewTags(tag);
            }
        }
        catch(Exception e){
            Log.i(CLASS_NAME, "ERROR !! While updating a Account");
            return false;
        }
        finally {
            db.close();
        }
        return true;
    }

    public boolean updateCategory(CategoryMO category) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Date date = new Date();

            ContentValues values = new ContentValues();
            values.put("CAT_NAME", category.getCAT_NAME());
            values.put("CAT_IMG", category.getCAT_IMG());
            values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(date));

            long result = db.update(DB_TABLE_CATEGORY, values, "CAT_ID = '" + category.getCAT_ID() + "'", null);

            if (result == -1) {
                Log.e(CLASS_NAME, "Something went wrong while updating the category ");
                return false;
            }

            TagsMO tag = new TagsMO();
            tag.setUSER_ID(category.getUSER_ID());
            tag.setTAG_TYPE("CATEGORY");
            tag.setTAG_TYPE_ID(category.getCAT_ID());
            tag.setTAGS(category.getTagsStr());

            if (isTagAlreadyExist(tag)) {
                return updateTags(tag);
            } else {
                addNewTags(tag);
            }
        } catch (Exception e) {
            Log.i(CLASS_NAME, "ERROR !! While updating a Category");
            return false;
        } finally {
            db.close();
        }
        return true;
    }

    public boolean updateTags(TagsMO tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("TAGS", tag.getTAGS());
        values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

        return db.update(DB_TABLE_TAGS, values, "TAG_TYPE_ID = '" + tag.getTAG_TYPE_ID() + "'", null) > 0;
    }

    public long addNewTags(TagsMO tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("USER_ID", tag.getUSER_ID());
        values.put("TAG_ID", IdGenerator.generateUniqueId("TAG"));
        values.put("TAG_TYPE", tag.getTAG_TYPE());
        values.put("TAG_TYPE_ID", tag.getTAG_TYPE_ID());
        values.put("TAGS", tag.getTAGS());
        values.put("CREAT_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

        return db.insert(DB_TABLE_TAGS, null, values);
    }

    public long addNewSpenton(SpentOnMO spentOn) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Date date = new Date();

            ContentValues values = new ContentValues();
            values.put("USER_ID", spentOn.getUSER_ID());
            values.put("SPNT_ON_ID", spentOn.getSPNT_ON_ID());
            values.put("SPNT_ON_NAME", spentOn.getSPNT_ON_NAME().trim().toUpperCase());
            values.put("SPNT_ON_IS_DEF", DB_NONAFFIRMATIVE);
            values.put("SPNT_ON_IMG", spentOn.getSPNT_ON_IMG());
            values.put("CREAT_DTM", DB_DATE_TIME_FORMAT_SDF.format(date));

            long result = db.insert(DB_TABLE_SPENTON, null, values);

            if (result == -1) {
                return -1;
            }

            if (spentOn.getTagsStr() == null || spentOn.getTagsStr().trim().isEmpty()) {
                return result;
            }

            TagsMO tag = new TagsMO();
            tag.setUSER_ID(spentOn.getUSER_ID());
            tag.setTAG_TYPE("SPENT ON");
            tag.setTAG_TYPE_ID(spentOn.getSPNT_ON_ID());
            tag.setTAGS(spentOn.getTagsStr());

            return addNewTags(tag);
        } catch (Exception e) {
            Log.i(CLASS_NAME, "ERROR !! While adding a new spent on");
            db.close();
            return -1;
        } finally {
            db.close();
        }
    }

    public long addNewAccount(AccountMO account) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Date date = new Date();

            ContentValues values = new ContentValues();
            values.put("USER_ID", account.getUSER_ID());
            values.put("ACC_ID", account.getACC_ID());
            values.put("ACC_NAME", account.getACC_NAME().trim().toUpperCase());
            values.put("ACC_IS_DEF", DB_NONAFFIRMATIVE);
            values.put("ACC_IMG", account.getACC_IMG());
            values.put("CREAT_DTM", DB_DATE_TIME_FORMAT_SDF.format(date));

            long result = db.insert(DB_TABLE_ACCOUNT, null, values);

            if (result == -1) {
                return -1;
            }

            if (account.getTagsStr() == null || account.getTagsStr().trim().isEmpty()) {
                return result;
            }

            TagsMO tag = new TagsMO();
            tag.setUSER_ID(account.getUSER_ID());
            tag.setTAG_TYPE("ACCOUNT");
            tag.setTAG_TYPE_ID(account.getACC_ID());
            tag.setTAGS(account.getTagsStr());

            return addNewTags(tag);
        } catch (Exception e) {
            Log.i(CLASS_NAME, "ERROR !! While adding a new account");
            db.close();
            return -1;
        } finally {
            db.close();
        }
    }

    public long addNewCategory(CategoryMO category) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Date date = new Date();

            ContentValues values = new ContentValues();
            values.put("USER_ID", category.getUSER_ID());
            values.put("CAT_ID", category.getCAT_ID());
            values.put("CAT_NAME", category.getCAT_NAME().trim().toUpperCase());
            values.put("CAT_IS_DEF", DB_NONAFFIRMATIVE);
            values.put("CAT_IMG", category.getCAT_IMG());
            values.put("CREAT_DTM", DB_DATE_TIME_FORMAT_SDF.format(date));

            long result = db.insert(DB_TABLE_CATEGORY, null, values);

            if (result == -1) {
                return -1;
            }

            if (category.getTagsStr() == null || category.getTagsStr().trim().isEmpty()) {
                return result;
            }

            TagsMO tag = new TagsMO();
            tag.setUSER_ID(category.getUSER_ID());
            tag.setTAG_TYPE("CATEGORY");
            tag.setTAG_TYPE_ID(category.getCAT_ID());
            tag.setTAGS(category.getTagsStr());

            return addNewTags(tag);
        } catch (Exception e) {
            Log.i(CLASS_NAME, "ERROR !! While adding a new category");
            db.close();
            return -1;
        } finally {
            db.close();
        }
    }

    public boolean isTagAlreadyExist(TagsMO tag) {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ");
        sb.append(" COUNT(USER_ID) AS COUNT ");

        sb.append(" FROM ");

        sb.append(DB_TABLE_TAGS);

        sb.append(" WHERE ");
        sb.append(" USER_ID IN ('" + tag.getUSER_ID() + "', '" + ADMIN_USERID + "') ");
        sb.append(" AND UPPER(TAG_TYPE_ID) = '" + tag.getTAG_TYPE_ID() + "' ");

        Log.i(CLASS_NAME, "Query to know if tag already exists  :" + sb);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sb), null);

        if (cursor.moveToNext()) {
            return ColumnFetcher.loadInt(cursor, "COUNT") > 0;
        }
        cursor.close();
        db.close();

        return false;
    }

    public boolean isSpentonAlreadyExist(String user_id, String spnt_on_name) {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ");
        sb.append(" COUNT(USER_ID) AS COUNT ");

        sb.append(" FROM ");

        sb.append(DB_TABLE_SPENTON);

        sb.append(" WHERE ");
        sb.append(" USER_ID IN ('" + user_id + "', '" + ADMIN_USERID + "') ");
        sb.append(" AND UPPER(SPNT_ON_NAME) = '" + spnt_on_name.toUpperCase() + "' ");

        Log.i(CLASS_NAME, "Query to know if spent on already exists  :" + sb);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sb), null);

        if (cursor.moveToNext()) {
            return ColumnFetcher.loadInt(cursor, "COUNT") > 0;
        }
        cursor.close();
        db.close();

        return false;
    }

    public boolean isAccountAlreadyExist(String user_id, String acc_name) {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ");
        sb.append(" COUNT(USER_ID) AS COUNT ");

        sb.append(" FROM ");

        sb.append(DB_TABLE_ACCOUNT);

        sb.append(" WHERE ");
        sb.append(" USER_ID IN ('" + user_id + "', '" + ADMIN_USERID + "') ");
        sb.append(" AND UPPER(ACC_NAME) = '" + acc_name.toUpperCase() + "' ");

        Log.i(CLASS_NAME, "Query to know if account already exists  :" + sb);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sb), null);

        if (cursor.moveToNext()) {
            return ColumnFetcher.loadInt(cursor, "COUNT") > 0;
        }
        cursor.close();
        db.close();

        return false;
    }

    public boolean isCategoryAlreadyExist(String user_id, String cat_name) {
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ");
        sb.append(" COUNT(USER_ID) AS COUNT ");

        sb.append(" FROM ");

        sb.append(DB_TABLE_CATEGORY);

        sb.append(" WHERE ");
        sb.append(" USER_ID IN ('" + user_id + "', '" + ADMIN_USERID + "') ");
        sb.append(" AND UPPER(CAT_NAME) = '" + cat_name.toUpperCase() + "' ");

        Log.i(CLASS_NAME, "Query to know if category already exists  :" + sb);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sb), null);

        if (cursor.moveToNext()) {
            return ColumnFetcher.loadInt(cursor, "COUNT") > 0;
        }
        cursor.close();
        db.close();

        return false;
    }

    //method to get all the budgets_view for the particlar user
    public List<BudgetMO> getAllBudgets(Date date, String userId) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" BUDGET_ID, ");
        sqlQuerySB.append(" BUDGET_NAME, ");
        sqlQuerySB.append(" BUDGET_GRP_ID, ");
        sqlQuerySB.append(" BUDGET_GRP_TYPE, ");
        sqlQuerySB.append(" BUDGET_TYPE, ");
        sqlQuerySB.append(" BUDGET_AMT, ");
        sqlQuerySB.append(" BUDGET_NOTE, ");
        sqlQuerySB.append(" CREAT_DTM, ");
        sqlQuerySB.append(" MOD_DTM ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_BUDGET);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '" + userId + "' ");

        Log.i(CLASS_NAME, "Query to get the budgets: " + String.valueOf(sqlQuerySB));
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        List<BudgetMO> budgetModelList = new ArrayList<>();
        BudgetMO budgetModelObj;
        while (cursor.moveToNext()) {
            String budIdStr = ColumnFetcher.loadString(cursor, "BUDGET_ID");
            String budNmeStr = ColumnFetcher.loadString(cursor, "BUDGET_NAME");
            String budGrpId = ColumnFetcher.loadString(cursor, "BUDGET_GRP_ID");
            String budGrpTyp = ColumnFetcher.loadString(cursor, "BUDGET_GRP_TYPE");
            String budTypeStr = ColumnFetcher.loadString(cursor, "BUDGET_TYPE");
            Double budAmt = ColumnFetcher.loadDouble(cursor, "BUDGET_AMT");
            String budNoteStr = ColumnFetcher.loadString(cursor, "BUDGET_NOTE");
            Date creatDtm = ColumnFetcher.loadDateTime(cursor, "CREAT_DTM");

            budgetModelObj = new BudgetMO();
            budgetModelObj.setBUDGET_ID(budIdStr);
            budgetModelObj.setBUDGET_NAME(budNmeStr);
            budgetModelObj.setBUDGET_GRP_ID(budGrpId);
            budgetModelObj.setBUDGET_GRP_TYPE(budGrpTyp);
            budgetModelObj.setBUDGET_TYPE(budTypeStr);
            budgetModelObj.setBUDGET_NOTE(budNoteStr);
            budgetModelObj.setBUDGET_AMT(budAmt);
            budgetModelObj.setCREAT_DTM(creatDtm);
            budgetModelObj.setBudgetGroupImage(getBudgetGroupImage(budgetModelObj));//TODO : Avoid unecessary db calls here and in the next line
            budgetModelObj.setBudgetGroupName(getBudgetGroupTypeNameOnBudgetGroupType(budGrpTyp, budGrpId));
            budgetModelObj.setBudgetRangeTotal(getTotalExpenseOnBudget(budgetModelObj, date));

            budgetModelList.add(budgetModelObj);
        }

        cursor.close();
        db.close();
        return budgetModelList;
    }

    private String getBudgetGroupTypeNameOnBudgetGroupType(String budgetGroupTypeStr, String budgetGroupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);
        String selectorStr = "";
        String tableStr = "";
        String whereStr = "";

        if ("CATEGORY".equalsIgnoreCase(budgetGroupTypeStr)) {
            selectorStr = " CAT_NAME ";
            tableStr = DB_TABLE_CATEGORY;
            whereStr = " CAT_ID ";
        } else if ("ACCOUNT".equalsIgnoreCase(budgetGroupTypeStr)) {
            selectorStr = " ACC_NAME ";
            tableStr = DB_TABLE_ACCOUNT;
            whereStr = " ACC_ID ";
        } else if ("SPENT ON".equalsIgnoreCase(budgetGroupTypeStr)) {
            selectorStr = " SPNT_ON_NAME ";
            tableStr = DB_TABLE_SPENTON;
            whereStr = " SPNT_ON_ID ";
        }

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(selectorStr);
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(tableStr);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(whereStr + " = '" + budgetGroupId + "'");

        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        if (cursor.moveToNext()) {
            return ColumnFetcher.loadString(cursor, selectorStr.trim());
        }
        db.close();
        return null;
    }

    private String getBudgetGroupImage(BudgetMO budget) {
        StringBuilder sb = new StringBuilder();

        if ("CATEGORY".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())) {
            sb.append(" SELECT ");
            sb.append(" CAT_IMG AS BUDGET_GROUP_IMG ");
            sb.append(" FROM ");
            sb.append(DB_TABLE_CATEGORY);
            sb.append(" WHERE ");
            sb.append(" CAT_ID = '" + budget.getBUDGET_GRP_ID() + "' ");
        } else if ("ACCOUNT".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())) {
            sb.append(" SELECT ");
            sb.append(" ACC_IMG AS BUDGET_GROUP_IMG ");
            sb.append(" FROM ");
            sb.append(DB_TABLE_ACCOUNT);
            sb.append(" WHERE ");
            sb.append(" ACC_ID = '" + budget.getBUDGET_GRP_ID() + "' ");
        } else if ("SPENT ON".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())) {
            sb.append(" SELECT ");
            sb.append(" SPNT_ON_IMG AS BUDGET_GROUP_IMG ");
            sb.append(" FROM ");
            sb.append(DB_TABLE_SPENTON);
            sb.append(" WHERE ");
            sb.append(" SPNT_ON_ID = '" + budget.getBUDGET_GRP_ID() + "' ");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sb), null);

        if (cursor.moveToNext()) {
            return ColumnFetcher.loadString(cursor, "BUDGET_GROUP_IMG");
        }
        db.close();
        return "";
    }

    private Double getTotalExpenseOnBudget(BudgetMO budget, Date date) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder();

        StringBuilder groupConnectorSB = new StringBuilder();
        if ("CATEGORY".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())) {
            groupConnectorSB.append(" AND CAT_ID = '" + budget.getBUDGET_GRP_ID() + "' ");
        } else if ("ACCOUNT".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())) {
            groupConnectorSB.append(" AND ACC_ID = '" + budget.getBUDGET_GRP_ID() + "' ");
        }
        if ("SPENT ON".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())) {
            groupConnectorSB.append(" AND SPNT_ON_ID = '" + budget.getBUDGET_GRP_ID() + "' ");
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DB_DATE_FORMAT);
        String dateStr = sdf.format(date);
        String dateStrArr[] = dateStr.split("-");
        String dateConnectorStr = "";
        if (REPEATS_DAY.equalsIgnoreCase(budget.getBUDGET_TYPE())) {
            dateConnectorStr = " AND TRAN_DATE = '" + dateStrArr[0] + "-" + dateStrArr[1] + "-" + dateStrArr[2] + "' ";
        } else if (REPEATS_WEEK.equalsIgnoreCase(budget.getBUDGET_TYPE())) {
            List<String> weekDaysList = DateTimeUtil.getAllDatesInWeekOnDate(dateStr);

            dateConnectorStr += " AND TRAN_DATE IN (";
            for (String iterDatesList : weekDaysList) {
                dateConnectorStr += "'" + iterDatesList + "',";
            }

            if (dateConnectorStr.contains(",")) {
                dateConnectorStr = dateConnectorStr.substring(0, dateConnectorStr.lastIndexOf(",")) + ") ";
            }

        } else if (REPEATS_MONTH.equalsIgnoreCase(budget.getBUDGET_TYPE())) {
            dateConnectorStr = " AND TRAN_DATE LIKE '%-" + dateStrArr[1] + "-" + dateStrArr[2] + "' ";
        } else if (REPEATS_YEAR.equalsIgnoreCase(budget.getBUDGET_TYPE())) {
            dateConnectorStr = " AND TRAN_DATE LIKE '%-" + dateStrArr[2] + "' ";
        }

        sqlQuerySB.append(" SELECT ");

        sqlQuerySB.append(" ((SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), 0) ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'EXPENSE' ");
        sqlQuerySB.append(groupConnectorSB);
        sqlQuerySB.append(dateConnectorStr + ") ");

        sqlQuerySB.append(" - ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), 0) ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'INCOME' ");
        sqlQuerySB.append(groupConnectorSB);
        sqlQuerySB.append(dateConnectorStr + ")) ");

        sqlQuerySB.append(" AS TOTAL ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION + " TRAN ");

        //sqlQuerySB.append(" WHERE 1=1 ");

        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        if (cursor.moveToNext()) {
            return ColumnFetcher.loadDouble(cursor, "TOTAL");
        }
        db.close();
        return 0.0;
    }

    private Map<String, DayLedger> getTransfers(Map<String, DayLedger> monthLegendMap, String dateStrArr[], String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" FRM_ACC.ACC_NAME AS ACC_FROM, ");
        sqlQuerySB.append(" FRM_ACC.ACC_ID AS ACC_ID_FROM, ");
        sqlQuerySB.append(" FRM_ACC.ACC_IMG AS ACC_FROM_IMG, ");
        sqlQuerySB.append(" TO_ACC.ACC_NAME AS ACC_TO, ");
        sqlQuerySB.append(" TO_ACC.ACC_ID AS ACC_ID_TO, ");
        sqlQuerySB.append(" TO_ACC.ACC_IMG AS ACC_TO_IMG, ");
        sqlQuerySB.append(" TRFR.TRNFR_AMT, ");
        sqlQuerySB.append(" TRFR.TRNFR_ID, ");
        sqlQuerySB.append(" TRFR.CREAT_DTM, ");
        sqlQuerySB.append(" TRFR.TRNFR_DATE, ");
        sqlQuerySB.append(" RPT.REPEAT_ID, ");
        sqlQuerySB.append(" RPT.REPEAT_NAME, ");
        sqlQuerySB.append(" RPT.REPEAT_IMG, ");
        sqlQuerySB.append(" TRFR.PARENT_TRNFR_ID, ");
        sqlQuerySB.append(" TRFR.SCHD_UPTO_DATE, ");
        sqlQuerySB.append(" TRFR.NOTIFY, ");
        sqlQuerySB.append(" TRFR.NOTIFY_TIME, ");
        sqlQuerySB.append(" TRFR.TRNFR_NOTE ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER + " TRFR ");

        sqlQuerySB.append(" JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " FRM_ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" FRM_ACC.ACC_ID = TRFR.ACC_ID_FRM ");

        sqlQuerySB.append(" JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " TO_ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TO_ACC.ACC_ID = TRFR.ACC_ID_TO ");

        sqlQuerySB.append(" LEFT OUTER JOIN ");
        sqlQuerySB.append(DB_TABLE_REPEAT + " RPT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" RPT.REPEAT_ID = TRFR.REPEAT_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRFR.TRNFR_DATE ");
        sqlQuerySB.append(" BETWEEN ");
        sqlQuerySB.append(" '" + dateStrArr[0] + "' AND '" + dateStrArr[1] + "' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRFR.USER_ID = '" + userId + "' ");

        Log.i(CLASS_NAME, "Query to fetch Transfers : " + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()) {
            String fromAccStr = ColumnFetcher.loadString(cursor, "ACC_FROM");
            String fromAccIdStr = ColumnFetcher.loadString(cursor, "ACC_ID_FROM");
            String fromAccImgStr = ColumnFetcher.loadString(cursor, "ACC_FROM_IMG");
            String toAccStr = ColumnFetcher.loadString(cursor, "ACC_TO");
            String toAccIdStr = ColumnFetcher.loadString(cursor, "ACC_ID_TO");
            String toAccImgStr = ColumnFetcher.loadString(cursor, "ACC_TO_IMG");
            Double amt = ColumnFetcher.loadDouble(cursor, "TRNFR_AMT");
            Date tranDate = ColumnFetcher.loadDate(cursor, "TRNFR_DATE");
            String tranDateStr = ColumnFetcher.loadString(cursor, "TRNFR_DATE");
            String tranIdStr = ColumnFetcher.loadString(cursor, "TRNFR_ID");
            String creatDtmStr = ColumnFetcher.loadString(cursor, "CREAT_DTM");
            Date creatDtm = ColumnFetcher.loadDateTime(cursor, "CREAT_DTM");
            String schedDateStr = ColumnFetcher.loadString(cursor, "SCHD_UPTO_DATE");
            String parentTransferIdStr = ColumnFetcher.loadString(cursor, "PARENT_TRNFR_ID");
            String notifyStr = ColumnFetcher.loadString(cursor, "NOTIFY");
            String notifyTimeStr = ColumnFetcher.loadString(cursor, "NOTIFY_TIME");
            String repeatIdStr = ColumnFetcher.loadString(cursor, "REPEAT_ID");
            String repeatStr = ColumnFetcher.loadString(cursor, "REPEAT_NAME");
            String repeatImgStr = ColumnFetcher.loadString(cursor, "REPEAT_IMG");
            String noteStr = ColumnFetcher.loadString(cursor, "TRNFR_NOTE");

            String tempDateStrArr[] = tranDateStr.split("-");

            String transferDateStr = tempDateStrArr[2] + "-" + tempDateStrArr[1] + "-" + tempDateStrArr[0];

            DayLedger dayLedger;
            Double totalAmount = 0.0;
            ActivitiesMO activities;
            List<TransferMO> transfersList;
            TransferMO transfer;

            transfer = new TransferMO();
            transfer.setFromAccName(fromAccStr);
            transfer.setACC_ID_FRM(fromAccIdStr);
            transfer.setFromAccImg(fromAccImgStr);
            transfer.setToAccName(toAccStr);
            transfer.setACC_ID_TO(toAccIdStr);
            transfer.setToAccImg(toAccImgStr);
            transfer.setTRNFR_AMT(amt);
            transfer.setTRNFR_DATE(tranDate);
            transfer.setTransferDate(tranDateStr);
            transfer.setTRNFR_ID(tranIdStr);
            transfer.setCreatDtm(creatDtmStr);
            transfer.setREPEAT_ID(repeatIdStr);
            transfer.setSCHD_UPTO_DATE(schedDateStr);
            transfer.setNOTIFY(notifyStr);
            transfer.setNOTIFY_TIME(notifyTimeStr);
            transfer.setRepeat(repeatStr);
            transfer.setRepeatImg(repeatImgStr);
            transfer.setTRNFR_NOTE(noteStr);
            transfer.setCREAT_DTM(creatDtm);
            transfer.setPARENT_TRNFR_ID(parentTransferIdStr);

            //if the legend map already contains an entry for this date
            if (monthLegendMap.containsKey(transferDateStr)) {
                dayLedger = monthLegendMap.get(transferDateStr);

                if (dayLedger == null) {
                    dayLedger = new DayLedger();
                }

                activities = dayLedger.getActivities();
                totalAmount = amt+ dayLedger.getTransfersAmountTotal();

                if (activities == null) {
                    activities = new ActivitiesMO();
                }

                transfersList = activities.getTransfersList();

                if (transfersList == null) {
                    transfersList = new ArrayList<>();
                }
            }
            //if the legend map doesnt contains an entry for this date
            else {
                dayLedger = new DayLedger();
                activities = new ActivitiesMO();
                totalAmount = amt;
                transfersList = new ArrayList<>();
            }

            transfersList.add(transfer);
            activities.setTransfersList(transfersList);
            dayLedger.setDate(transferDateStr);
            dayLedger.setActivities(activities);
            dayLedger.setTransfersAmountTotal(totalAmount);

            if(transfer.getRepeat() != null && !transfer.getRepeat().trim().isEmpty()){
                dayLedger.setHasScheduledTransfer(true);
            }

            monthLegendMap.put(transferDateStr, dayLedger);
        }
        cursor.close();
        db.close();
        return monthLegendMap;
    }

    private Map<String, DayLedger> getTransactions(Map<String, DayLedger> monthLegendMap, String[] dateStrArr, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CAT.CAT_ID, ");
        sqlQuerySB.append(" CAT.CAT_NAME, ");
        sqlQuerySB.append(" CAT.CAT_IMG, ");
        sqlQuerySB.append(" TRAN.TRAN_AMT, ");
        sqlQuerySB.append(" TRAN.TRAN_ID, ");
        sqlQuerySB.append(" TRAN.TRAN_TYPE, ");
        sqlQuerySB.append(" TRAN.TRAN_DATE, ");
        sqlQuerySB.append(" TRAN.TRAN_NAME, ");
        sqlQuerySB.append(" TRAN.CREAT_DTM, ");
        sqlQuerySB.append(" TRAN.PARENT_TRAN_ID, ");
        sqlQuerySB.append(" TRAN.SCHD_UPTO_DATE, ");
        sqlQuerySB.append(" TRAN.NOTIFY, ");
        sqlQuerySB.append(" TRAN.NOTIFY_TIME, ");
        sqlQuerySB.append(" TRAN.TRAN_NOTE, ");
        sqlQuerySB.append(" ACC.ACC_ID, ");
        sqlQuerySB.append(" ACC.ACC_NAME, ");
        sqlQuerySB.append(" ACC.ACC_IMG, ");
        sqlQuerySB.append(" SPN.SPNT_ON_ID, ");
        sqlQuerySB.append(" SPN.SPNT_ON_NAME, ");
        sqlQuerySB.append(" SPN.SPNT_ON_IMG, ");
        sqlQuerySB.append(" RPT.REPEAT_ID, ");
        sqlQuerySB.append(" RPT.REPEAT_NAME, ");
        sqlQuerySB.append(" RPT.REPEAT_IMG ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION + " TRAN ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_CATEGORY + " CAT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" CAT.CAT_ID = TRAN.CAT_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " ACC ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" ACC.ACC_ID = TRAN.ACC_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_SPENTON + " SPN ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" SPN.SPNT_ON_ID = TRAN.SPNT_ON_ID ");

        sqlQuerySB.append(" LEFT OUTER JOIN ");
        sqlQuerySB.append(DB_TABLE_REPEAT + " RPT ");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" RPT.REPEAT_ID = TRAN.REPEAT_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN.TRAN_DATE ");
        sqlQuerySB.append(" BETWEEN ");
        sqlQuerySB.append(" '" + dateStrArr[0] + "' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" '" + dateStrArr[1] + "' ");

        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN.USER_ID = '" + userId + "' ");

        Log.i(CLASS_NAME, "Query to fetch Transactions : " + sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()) {
            String tranNoteStr = ColumnFetcher.loadString(cursor, "TRAN_NOTE");
            String parentTransactionIdStr = ColumnFetcher.loadString(cursor, "PARENT_TRAN_ID");
            String schedDateStr = ColumnFetcher.loadString(cursor, "SCHD_UPTO_DATE");
            String notifyStr = ColumnFetcher.loadString(cursor, "NOTIFY");
            String notifyTimeStr = ColumnFetcher.loadString(cursor, "NOTIFY_TIME");
            String repeatIdStr = ColumnFetcher.loadString(cursor, "REPEAT_ID");
            String repeatStr = ColumnFetcher.loadString(cursor, "REPEAT_NAME");
            String repeatImgStr = ColumnFetcher.loadString(cursor, "REPEAT_IMG");
            String spntOnIdStr = ColumnFetcher.loadString(cursor, "SPNT_ON_ID");
            String spntOnStr = ColumnFetcher.loadString(cursor, "SPNT_ON_NAME");
            String spntImgStr = ColumnFetcher.loadString(cursor, "SPNT_ON_IMG");
            String accountIdStr = ColumnFetcher.loadString(cursor, "ACC_ID");
            String accountStr = ColumnFetcher.loadString(cursor, "ACC_NAME");
            String accountImgStr = ColumnFetcher.loadString(cursor, "ACC_IMG");
            String nameStr = ColumnFetcher.loadString(cursor, "TRAN_NAME");
            String catIdStr = ColumnFetcher.loadString(cursor, "CAT_ID");
            String catNameStr = ColumnFetcher.loadString(cursor, "CAT_NAME");
            String catImgStr = ColumnFetcher.loadString(cursor, "CAT_IMG");
            Double amt = ColumnFetcher.loadDouble(cursor, "TRAN_AMT");
            String tranTypeStr = ColumnFetcher.loadString(cursor, "TRAN_TYPE");
            String tranIdStr = ColumnFetcher.loadString(cursor, "TRAN_ID");
            String transactionDateStr = ColumnFetcher.loadString(cursor, "TRAN_DATE");
            Date transactionDate = ColumnFetcher.loadDate(cursor, "TRAN_DATE");
            String creatDtmStr = ColumnFetcher.loadString(cursor, "CREAT_DTM");
            Date creatDtm = ColumnFetcher.loadDateTime(cursor, "CREAT_DTM");
            String tempDateStrArr[] = transactionDateStr.split("-");

            String tranDateStr = tempDateStrArr[2] + "-" + tempDateStrArr[1] + "-" + tempDateStrArr[0];

            TransactionMO transaction = new TransactionMO();
            transaction.setCategory(catNameStr);
            transaction.setCAT_ID(catIdStr);
            transaction.setACC_ID(accountIdStr);
            transaction.setSPNT_ON_ID(spntOnIdStr);
            transaction.setREPEAT_ID(repeatIdStr);
            transaction.setTRAN_AMT(amt);
            transaction.setTRAN_NAME(nameStr);
            transaction.setCategoryImg(catImgStr);
            transaction.setAccount(accountStr);
            transaction.setTRAN_TYPE(tranTypeStr);
            transaction.setTRAN_ID(tranIdStr);
            transaction.setTransactionDate(transactionDateStr);
            transaction.setTRAN_DATE(transactionDate);
            transaction.setCreatDtm(creatDtmStr);
            transaction.setCREAT_DTM(creatDtm);
            transaction.setPARENT_TRAN_ID(parentTransactionIdStr);
            transaction.setSCHD_UPTO_DATE(schedDateStr);
            transaction.setNOTIFY(notifyStr);
            transaction.setNOTIFY_TIME(notifyTimeStr);
            transaction.setRepeat(repeatStr);
            transaction.setRepeatImg(repeatImgStr);
            transaction.setSpentOn(spntOnStr);
            transaction.setSpentOnImg(spntImgStr);
            transaction.setAccountImg(accountImgStr);
            transaction.setTRAN_NOTE(tranNoteStr);

            DayLedger dayLedger;
            Double totalAmount;
            ActivitiesMO activities;
            List<TransactionMO> transactionsList;

            //if the legend map already contains an entry for this date
            if (monthLegendMap.containsKey(tranDateStr)) {
                dayLedger = monthLegendMap.get(tranDateStr);

                if (dayLedger == null) {
                    dayLedger = new DayLedger();
                }

                totalAmount = dayLedger.getTransactionsAmountTotal();
                if ("EXPENSE".equalsIgnoreCase(tranTypeStr)) {
                    totalAmount -= amt;
                } else {
                    totalAmount += amt;
                }

                activities = dayLedger.getActivities();

                if (activities == null) {
                    activities = new ActivitiesMO();
                }

                transactionsList = activities.getTransactionsList();

                if (transactionsList == null) {
                    transactionsList = new ArrayList<>();
                }
            }
            //if the legend map doesnt contains an entry for this date
            else {
                totalAmount = 0.0;
                if ("EXPENSE".equalsIgnoreCase(tranTypeStr)) {
                    totalAmount -= amt;
                } else {
                    totalAmount += amt;
                }

                dayLedger = new DayLedger();
                activities = new ActivitiesMO();
                transactionsList = new ArrayList<>();
            }

            transactionsList.add(transaction);
            activities.setTransactionsList(transactionsList);
            dayLedger.setDate(tranDateStr);
            dayLedger.setActivities(activities);
            dayLedger.setTransactionsAmountTotal(totalAmount);

            if(transaction.getRepeat() != null && !transaction.getRepeat().trim().isEmpty()){
                dayLedger.setHasScheduledTransaction(true);
            }

            monthLegendMap.put(tranDateStr, dayLedger);
        }
        cursor.close();
        db.close();
        return monthLegendMap;
    }

    public Map<String, DayLedger> getMonthLegendOnDate(Calendar calendar, int monthsRange, String userId) {
        Map<String, DayLedger> monthLegendMap = new HashMap<>();

        //get start and end dates based on the passed date
        String dateStrArr[] = DateTimeUtil.getStartAndEndMonthDates(calendar, monthsRange/2);

        //get all the transactions on the passed date
        monthLegendMap = getTransactions(monthLegendMap, dateStrArr, userId);

        //get all the transfers on the passed date
        monthLegendMap = getTransfers(monthLegendMap, dateStrArr, userId);

        return monthLegendMap;
    }

    public TagsMO getTagOnTagTypeId(String userId, String tagIdStr) {
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" TAG_ID, ");
        sqlQuerySB.append(" TAG_TYPE, ");
        sqlQuerySB.append(" TAG_TYPE_ID, ");
        sqlQuerySB.append(" TAGS ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TAGS);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID IN ('" + ADMIN_USERID + "',  '" + userId + "') ");
        sqlQuerySB.append(" AND TAG_TYPE_ID = '" + tagIdStr + "' ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        if (cursor.moveToNext()) {
            TagsMO tag = new TagsMO();
            tag.setTAG_ID(ColumnFetcher.getInstance().loadString(cursor, "TAG_ID"));
            tag.setTAG_TYPE(ColumnFetcher.getInstance().loadString(cursor, "TAG_TYPE"));
            tag.setTAG_TYPE_ID(ColumnFetcher.getInstance().loadString(cursor, "TAG_TYPE_ID"));
            tag.setTAGS(ColumnFetcher.getInstance().loadString(cursor, "TAGS"));

            return tag;
        }
        cursor.close();
        db.close();
        return new TagsMO();
    }

    //--------------------- end of method to get all select_account--------------------------//

    public List<CountryMO> getAllCountriesAndCurrencies() {
        StringBuilder sqlQuerySB = new StringBuilder();
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CNTRY_ID, ");
        sqlQuerySB.append(" CNTRY_NAME, ");
        sqlQuerySB.append(" CNTRY_CODE, ");
        sqlQuerySB.append(" CUR, ");
        sqlQuerySB.append(" CUR_CODE, ");
        sqlQuerySB.append(" CNTRY_IMG, ");
        sqlQuerySB.append(" METRIC ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_COUNTRY);

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" CNTRY_NAME ");
        sqlQuerySB.append(" ASC ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<CountryMO> countriesList = new ArrayList<>();
        while (cursor.moveToNext()) {
            CountryMO country = new CountryMO();
            country.setCNTRY_ID(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_ID"));
            country.setCNTRY_NAME(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_NAME"));
            country.setCNTRY_CODE(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_CODE"));
            country.setCUR(ColumnFetcher.getInstance().loadString(cursor, "CUR"));
            country.setCUR_CODE(ColumnFetcher.getInstance().loadString(cursor, "CUR_CODE"));
            country.setCNTRY_IMG(ColumnFetcher.getInstance().loadString(cursor, "CNTRY_IMG"));
            country.setMETRIC(ColumnFetcher.getInstance().loadString(cursor, "METRIC"));
            countriesList.add(country);
        }
        cursor.close();
        db.close();
        return countriesList;
    }

    public List<RepeatMO> getAllRepeats() {
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" REPEAT_ID, ");
        sqlQuerySB.append(" REPEAT_NAME, ");
        sqlQuerySB.append(" REPEAT_IS_DEF, ");
        sqlQuerySB.append(" REPEAT_IMG ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_REPEAT);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<RepeatMO> repeatMOList = new ArrayList<>();
        while (cursor.moveToNext()) {
            RepeatMO repeatMO = new RepeatMO();
            repeatMO.setREPEAT_ID(ColumnFetcher.getInstance().loadString(cursor, "REPEAT_ID"));
            repeatMO.setREPEAT_NAME(ColumnFetcher.getInstance().loadString(cursor, "REPEAT_NAME"));
            repeatMO.setREPEAT_IS_DEF(ColumnFetcher.getInstance().loadString(cursor, "REPEAT_IS_DEF"));
            repeatMO.setREPEAT_IMG(ColumnFetcher.getInstance().loadString(cursor, "REPEAT_IMG"));

            repeatMOList.add(repeatMO);
        }
        cursor.close();
        db.close();
        return repeatMOList;
    }

    public List<CategoryMO> getAllCategories(String userId) {
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CAT_ID, ");
        sqlQuerySB.append(" CAT_NAME, ");
        sqlQuerySB.append(" CAT_IS_DEF, ");
        sqlQuerySB.append(" CAT_IMG, ");
        sqlQuerySB.append(" USER_ID ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_CATEGORY);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID IN ('" + ADMIN_USERID + "',  '" + userId + "') ");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" CAT_NAME ");
        sqlQuerySB.append(" ASC ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<CategoryMO> categoryList = new ArrayList<>();
        while (cursor.moveToNext()) {
            CategoryMO categoryMO = new CategoryMO();
            categoryMO.setCAT_ID(ColumnFetcher.getInstance().loadString(cursor, "CAT_ID"));
            categoryMO.setCAT_NAME(ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME"));
            categoryMO.setCAT_IMG(ColumnFetcher.getInstance().loadString(cursor, "CAT_IMG"));
            categoryMO.setCAT_IS_DEF(ColumnFetcher.getInstance().loadString(cursor, "CAT_IS_DEF"));
            categoryMO.setUSER_ID(ColumnFetcher.getInstance().loadString(cursor, "USER_ID"));

            categoryList.add(categoryMO);
        }
        cursor.close();
        db.close();
        return categoryList;
    }

    public List<SpentOnMO> getAllSpentOns(String userId) {
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SPNT_ON_ID, ");
        sqlQuerySB.append(" SPNT_ON_NAME, ");
        sqlQuerySB.append(" SPNT_ON_IMG, ");
        sqlQuerySB.append(" SPNT_ON_IS_DEF, ");
        sqlQuerySB.append(" USER_ID ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_SPENTON);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" USER_ID = '" + ADMIN_USERID + "' ");
        sqlQuerySB.append(" OR USER_ID = '" + userId + "' ");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" SPNT_ON_NAME ");
        sqlQuerySB.append(" ASC ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<SpentOnMO> spentOnList = new ArrayList<>();
        while (cursor.moveToNext()) {
            SpentOnMO spentOnMO = new SpentOnMO();
            spentOnMO.setSPNT_ON_ID(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_ID"));
            spentOnMO.setSPNT_ON_NAME(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME"));
            spentOnMO.setSPNT_ON_IMG(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_IMG"));
            spentOnMO.setSPNT_ON_IS_DEF(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_IS_DEF"));
            spentOnMO.setUSER_ID(ColumnFetcher.getInstance().loadString(cursor, "USER_ID"));
            spentOnList.add(spentOnMO);
        }
        cursor.close();
        db.close();
        return spentOnList;
    }

    //---------------------method to get all select_account--------------------------//
    public List<AccountMO> getAllAccounts(String userId) {
        List<AccountMO> accountsList = new ArrayList<AccountMO>();
        SQLiteDatabase db = this.getWritableDatabase();

        if (db == null) {
            Log.e(CLASS_NAME, "SQLiteDatabase object is null");
            return null;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT ");
        sb.append(" ACC.ACC_NAME, ");
        sb.append(" ACC.ACC_ID, ");
        sb.append(" ACC.ACC_IS_DEF, ");
        sb.append(" ACC.ACC_IMG, ");
        sb.append(" ACC.USER_ID, ");

        sb.append(" ((SELECT ");
        sb.append(" IFNULL(SUM(TRAN_AMT), '0') ");
        sb.append(" FROM ");
        sb.append(DB_TABLE_TRANSACTION + " TRAN ");
        sb.append(" WHERE ");
        sb.append(" TRAN_TYPE = 'INCOME' ");
        sb.append(" AND TRAN.ACC_ID = ACC.ACC_ID ");
        sb.append(" AND TRIM(TRAN.USER_ID) = '" + userId + "') ");

        sb.append(" + ");

        sb.append(" (SELECT ");
        sb.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sb.append(" FROM ");
        sb.append(DB_TABLE_TRANSFER + " TRNFR ");
        sb.append(" WHERE ");
        sb.append(" TRNFR.ACC_ID_TO = ACC.ACC_ID ");
        sb.append(" AND TRIM(TRNFR.USER_ID) = '" + userId + "')) ");

        sb.append(" - ");

        sb.append(" ((SELECT ");
        sb.append(" IFNULL(SUM(TRAN_AMT), '0') ");
        sb.append(" FROM ");
        sb.append(DB_TABLE_TRANSACTION + " TRAN ");
        sb.append(" WHERE ");
        sb.append(" TRAN_TYPE = 'EXPENSE' ");
        sb.append(" AND TRAN.ACC_ID = ACC.ACC_ID ");
        sb.append(" AND TRIM(TRAN.USER_ID) = '" + userId + "') ");

        sb.append(" + ");

        sb.append(" (SELECT ");
        sb.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sb.append(" FROM ");
        sb.append(DB_TABLE_TRANSFER + " TRNFR ");
        sb.append(" WHERE ");
        sb.append(" TRNFR.ACC_ID_FRM = ACC.ACC_ID ");
        sb.append(" AND TRIM(TRNFR.USER_ID) = '" + userId + "')) ");

        sb.append(" AS ACC_TOTAL ");

        sb.append(" FROM ");
        sb.append(DB_TABLE_ACCOUNT + " ACC ");

        sb.append(" WHERE ");
        sb.append(" TRIM(ACC.USER_ID) ");
        sb.append(" IN ");
        sb.append(" ('" + userId + "','" + ADMIN_USERID + "') ");

        sb.append(" GROUP BY ACC.ACC_ID ");
        sb.append(" ORDER BY ACC.CREAT_DTM ");

        Log.i(CLASS_NAME, "Query to get all the accounts : "+sb);
        Cursor cursor = db.rawQuery(String.valueOf(sb), null);

        while (cursor.moveToNext()) {
            String accountIdStr = ColumnFetcher.loadString(cursor, "ACC_ID");
            String accountNameStr = ColumnFetcher.loadString(cursor, "ACC_NAME");
            Double accountTotal = ColumnFetcher.loadDouble(cursor, "ACC_TOTAL");
            String accountIsDefaultStr = ColumnFetcher.loadString(cursor, "ACC_IS_DEF");
            String accountImgStr = ColumnFetcher.loadString(cursor, "ACC_IMG");
            String userIdStr = ColumnFetcher.loadString(cursor, "USER_ID");

            AccountMO account = new AccountMO();
            account.setACC_ID(accountIdStr);
            account.setACC_NAME(accountNameStr);
            account.setACC_TOTAL(accountTotal);
            account.setACC_IS_DEF(accountIsDefaultStr);
            account.setACC_IMG(accountImgStr);
            account.setUSER_ID(userIdStr);
            accountsList.add(account);
        }
        cursor.close();
        db.close();
        return accountsList;
    }
    //--------------------- end of method to get all select_account--------------------------//

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    // get class instance
    public static CalendarDbService getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new CalendarDbService(context.getApplicationContext());
        }
        return sInstance;
    }

    // constructors
    public CalendarDbService(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }
}
