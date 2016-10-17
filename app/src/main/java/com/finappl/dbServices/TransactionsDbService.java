package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.AccountsMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.RepeatMO;
import com.finappl.models.SpentOnMO;
import com.finappl.models.SpinnerModel;
import com.finappl.models.TransactionModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.DB_DATE_FORMAT;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_NONAFFIRMATIVE;
import static com.finappl.utils.Constants.DB_TABLE_ACCOUNT;
import static com.finappl.utils.Constants.DB_TABLE_CATEGORY;
import static com.finappl.utils.Constants.DB_TABLE_REPEAT;
import static com.finappl.utils.Constants.DB_TABLE_SPENTON;
import static com.finappl.utils.Constants.DB_TABLE_TRANSACTION;
import static com.finappl.utils.Constants.DB_TABLE_TRANSFER;
import static com.finappl.utils.Constants.DB_VERSION;

public class TransactionsDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

    private Context mContext;

    public RepeatMO getDefaultRepeat(){
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" REPEAT_ID, ");
        sqlQuerySB.append(" REPEAT_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_REPEAT);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" REPEAT_IS_DEF = '"+ Constants.DB_AFFIRMATIVE+"' ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        if (cursor.moveToNext()) {
            RepeatMO repeatMO = new RepeatMO();
            repeatMO.setREPEAT_ID(ColumnFetcher.loadString(cursor, "REPEAT_ID"));
            repeatMO.setREPEAT_NAME(ColumnFetcher.loadString(cursor, "REPEAT_NAME"));

            return  repeatMO;
        }

        return null;
    }

    public SpentOnMO getDefaultSpentOn(String loggedInUserIDStr) {
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SPNT_ON_ID, ");
        sqlQuerySB.append(" SPNT_ON_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_SPENTON);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" SPNT_ON_IS_DEF = '"+ Constants.DB_AFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND (USER_ID = '"+loggedInUserIDStr+"' OR USER_ID = '"+ADMIN_USERID+"') ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        if (cursor.moveToNext()) {
            SpentOnMO spentOnMO = new SpentOnMO();
            spentOnMO.setSPNT_ON_ID(ColumnFetcher.loadString(cursor, "SPNT_ON_ID"));
            spentOnMO.setSPNT_ON_NAME(ColumnFetcher.loadString(cursor, "SPNT_ON_NAME"));

            return  spentOnMO;
        }

        return null;
    }

    public AccountsMO getDefaultAccount(String loggedInUserIDStr){
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" ACC_ID, ");
        sqlQuerySB.append(" ACC_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_IS_DEF = '"+ Constants.DB_AFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND (USER_ID = '"+loggedInUserIDStr+"' OR USER_ID = '"+ADMIN_USERID+"') ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        if (cursor.moveToNext()) {
            AccountsMO accountsMO = new AccountsMO();
            accountsMO.setACC_ID(ColumnFetcher.loadString(cursor, "ACC_ID"));
            accountsMO.setACC_NAME(ColumnFetcher.loadString(cursor, "ACC_NAME"));

            return  accountsMO;
        }

        return null;
    }

    public CategoryMO getDefaultCategory(String loggedInUserIDStr){
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CAT_ID, ");
        sqlQuerySB.append(" CAT_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_CATEGORY);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" CAT_IS_DEF = '"+ Constants.DB_AFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND (USER_ID = '"+loggedInUserIDStr+"' OR USER_ID = '"+ADMIN_USERID+"') ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        if (cursor.moveToNext()) {
            CategoryMO categoryMO = new CategoryMO();
            categoryMO.setCAT_ID(ColumnFetcher.loadString(cursor, "CAT_ID"));
            categoryMO.setCAT_NAME(ColumnFetcher.loadString(cursor, "CAT_NAME"));

            return  categoryMO;
        }

        return null;
    }

    public List<SpinnerModel> getAllCategories(String userId){
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" CAT_ID, ");
        sqlQuerySB.append(" CAT_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_CATEGORY);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" CAT_IS_DEF = '"+ Constants.DB_AFFIRMATIVE+"' ");
        sqlQuerySB.append(" OR USER_ID = '"+userId+"' ");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" CAT_NAME ");
        sqlQuerySB.append(" ASC ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<SpinnerModel> categoryMOList = new ArrayList<>();
        while (cursor.moveToNext()){
            SpinnerModel categoryMO = new SpinnerModel();
            categoryMO.setItemId(ColumnFetcher.getInstance().loadString(cursor, "CAT_ID"));
            categoryMO.setItemName(ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME"));
            categoryMOList.add(categoryMO);
        }
        cursor.close();
        db.close();
        return categoryMOList;
    }

    public List<SpinnerModel> getAllSpentOn(String userId){
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SPNT_ON_ID, ");
        sqlQuerySB.append(" SPNT_ON_NAME ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_SPENTON);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" SPNT_ON_IS_DEF = '"+Constants.DB_AFFIRMATIVE+"' ");
        sqlQuerySB.append(" OR ");
        sqlQuerySB.append(" USER_ID = '"+userId+"' ");

        sqlQuerySB.append(" ORDER BY ");
        sqlQuerySB.append(" SPNT_ON_NAME ");
        sqlQuerySB.append(" ASC ");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        List<SpinnerModel> spentOnList = new ArrayList<>();
        while (cursor.moveToNext()){
            SpinnerModel spentOnMO = new SpinnerModel();
            spentOnMO.setItemId(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_ID"));
            spentOnMO.setItemName(ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME"));
            spentOnList.add(spentOnMO);
        }
        cursor.close();
        db.close();
        return spentOnList;
    }

    //---------------------method to get all accounts--------------------------//
    public List<SpinnerModel> getAllAccounts(String userId){
        List<SpinnerModel> accountsList = new ArrayList<SpinnerModel>();
        SQLiteDatabase db = this.getWritableDatabase();

        if(db == null){
            Log.e(CLASS_NAME, "SQLiteDatabase object is null");
            return null;
        }

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" ACC.ACC_NAME, ");
        sqlQuerySB.append(" ACC.ACC_ID, ");
        sqlQuerySB.append(" ACC.ACC_IS_DEF, ");

        sqlQuerySB.append(" (( SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'INCOME' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_ID = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN_IS_SCHED ");
        sqlQuerySB.append(" = ");
        sqlQuerySB.append(" '"+DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRAN_REPEAT is null ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId+"','"+ADMIN_USERID+"')) ");

        sqlQuerySB.append(" + ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID_TO = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRNFR_IS_SCHED ");
        sqlQuerySB.append(" = ");
        sqlQuerySB.append(" '"+DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRNFR_REPEAT is null ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId+"','"+ADMIN_USERID+"') ))");

        sqlQuerySB.append(" - ");

        sqlQuerySB.append(" ((SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRAN_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_TYPE = 'EXPENSE' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_ID = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId+"','"+ADMIN_USERID+"')) ");

        sqlQuerySB.append(" + ");

        sqlQuerySB.append(" (SELECT ");
        sqlQuerySB.append(" IFNULL(SUM(TRNFR_AMT), '0') ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSFER);
        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID_FRM = ACC.ACC_ID ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId+"','"+ADMIN_USERID+"') ))");

        sqlQuerySB.append(" AS ACC_TOTAL ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT+" ACC ");

        sqlQuerySB.append(" LEFT JOIN ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION+" TRAN");
        sqlQuerySB.append(" ON ");
        sqlQuerySB.append(" TRAN.ACC_ID = ACC.ACC_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRIM(UPPER(ACC.USER_ID)) ");
        sqlQuerySB.append(" IN ");
        sqlQuerySB.append(" ('" + userId+"','"+ADMIN_USERID+"') ");

        sqlQuerySB.append(" GROUP BY ACC.ACC_ID ");
        sqlQuerySB.append(" ORDER BY ACC.CREAT_DTM ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        String accountIdStr, accountNameStr, currecyStr, accountIsDefaultStr;
        Double accountTotal;
        SpinnerModel accountsModel = null;
        while (cursor.moveToNext()){
            accountIdStr = ColumnFetcher.loadString(cursor, "ACC_ID");
            accountNameStr = ColumnFetcher.loadString(cursor, "ACC_NAME");
            accountTotal = ColumnFetcher.loadDouble(cursor, "ACC_TOTAL");
            currecyStr = ColumnFetcher.loadString(cursor, "CUR_NAME");
            accountIsDefaultStr = ColumnFetcher.loadString(cursor, "ACC_IS_DEFAULT");

            accountsModel = new SpinnerModel();
            accountsModel.setItemId(accountIdStr);
            accountsModel.setItemName(accountNameStr);
            accountsList.add(accountsModel);
        }
        cursor.close();
        db.close();
        return accountsList;
    }
    //--------------------- end of method to get all accounts--------------------------//

    public TransactionModel getTransactionOnTransactionId(String transactionIdStr){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" TRAN_DATE, ");
        sqlQuerySB.append(" TRAN_AMT, ");
        sqlQuerySB.append(" TRAN_NAME, ");
        sqlQuerySB.append(" CAT_NAME, ");
        sqlQuerySB.append(" ACC_NAME, ");
        sqlQuerySB.append(" TRAN_TYPE, ");
        sqlQuerySB.append(" SPNT_ON_NAME, ");
        sqlQuerySB.append(" TRAN_NOTE, ");
        sqlQuerySB.append(" SCH_TRAN_ID ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(DB_TABLE_TRANSACTION + " TRAN ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_CATEGORY + " CAT ");
        sqlQuerySB.append(" ON CAT.CAT_ID = TRAN.CAT_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_ACCOUNT + " ACC ");
        sqlQuerySB.append(" ON ACC.ACC_ID = TRAN.ACC_ID ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(DB_TABLE_SPENTON + " SPNT ");
        sqlQuerySB.append(" ON SPNT.SPNT_ON_ID = TRAN.SPNT_ON_ID ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRAN_ID = '" + transactionIdStr + "' ");

        Log.i(CLASS_NAME, "Query to fetch Transaction using the Transaction ID("+transactionIdStr+") : "+sqlQuerySB);
        Cursor cursor = db.rawQuery(String.valueOf(sqlQuerySB), null);

        TransactionModel transactionModelObj = null;
        if(cursor.moveToNext()){
            transactionModelObj = new TransactionModel();
            transactionModelObj.setTRAN_DATE(ColumnFetcher.loadDate(cursor, "TRAN_DATE"));
            transactionModelObj.setTRAN_AMT(ColumnFetcher.loadDouble(cursor, "TRAN_AMT"));
            transactionModelObj.setTRAN_NAME(ColumnFetcher.loadString(cursor, "TRAN_NAME"));
            transactionModelObj.setCategory(ColumnFetcher.loadString(cursor, "CAT_NAME"));
            transactionModelObj.setAccount(ColumnFetcher.loadString(cursor, "ACC_NAME"));
            transactionModelObj.setTRAN_TYPE(ColumnFetcher.loadString(cursor, "TRAN_TYPE"));
            transactionModelObj.setSpentOn(ColumnFetcher.loadString(cursor, "SPNT_ON_NAME"));
            transactionModelObj.setTRAN_NOTE(ColumnFetcher.loadString(cursor, "TRAN_NOTE"));
            transactionModelObj.setSCH_TRAN_ID(ColumnFetcher.loadString(cursor, "SCH_TRAN_ID"));
        }
        cursor.close();
        db.close();
        return transactionModelObj;
    }

    //	method to update an already created transaction.. returns 0 for fail, 1 for success
    public int updateOldTransaction(TransactionModel transactionModel){
		SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("CAT_ID", transactionModel.getCAT_ID());
        values.put("SPNT_ON_ID", transactionModel.getSPNT_ON_ID());
        values.put("ACC_ID", transactionModel.getACC_ID());
        values.put("TRAN_AMT", transactionModel.getTRAN_AMT());
        values.put("TRAN_NAME", transactionModel.getTRAN_NAME());
        values.put("TRAN_TYPE", transactionModel.getTRAN_TYPE());
        values.put("TRAN_NOTE", transactionModel.getTRAN_NOTE());
        values.put("TRAN_DATE", simpleDateFormat.format(transactionModel.getTRAN_DATE()));
        values.put("MOD_DTM", simpleDateTimeFormat.format(new Date()));

		// Updating an old Row
        int result = db.update(DB_TABLE_TRANSACTION, values,	"TRAN_ID = '" + transactionModel.getTRAN_ID() + "'", null);
        db.close();
		return result;
    }

    //	method to add a new transaction..returns -1 on fail to add new transaction. row id on success
    public long addNewTransaction(TransactionModel transactionModel){
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

		values.put("TRAN_ID", transactionModel.getTRAN_ID());
		values.put("USER_ID", transactionModel.getUSER_ID());
		values.put("CAT_ID", transactionModel.getCAT_ID());
		values.put("SPNT_ON_ID", transactionModel.getSPNT_ON_ID());
		values.put("ACC_ID", transactionModel.getACC_ID());
        values.put("REPEAT_ID", transactionModel.getREPEAT_ID());
		values.put("TRAN_AMT", transactionModel.getTRAN_AMT());
		values.put("TRAN_NAME", transactionModel.getTRAN_NAME());
		values.put("TRAN_TYPE", transactionModel.getTRAN_TYPE());
		values.put("TRAN_NOTE", transactionModel.getTRAN_NOTE());
		values.put("TRAN_DATE", simpleDateFormat.format(transactionModel.getTRAN_DATE()));
		values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));

        long result = 0;
        try {
            // Inserting a new Row
            result =  db.insertOrThrow(DB_TABLE_TRANSACTION, null, values);
        }
        catch(Exception e){
            Log.e(CLASS_NAME, "Error while adding transaction:"+e);
        }
        db.close();
        return result;
    }

    //--------------------- end of method to get all spent on type--------------------------//

    //---------------------method to get all accounts--------------------------//
    /*public List<SpinnerModel> getAllAccounts(String userId){
        CalendarDbService cal = new CalendarDbService(mContext);
        return cal.getAllAccounts(userId);
    }*/
    //--------------------- end of method to get all pay type--------------------------//

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	//constructors
	public TransactionsDbService(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
