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
import static com.finappl.utils.Constants.DB_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT_SDF;
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

    public boolean deleteTransaction(String transactionIdStr){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = db.delete(DB_TABLE_TRANSACTION, "TRAN_ID = '" + transactionIdStr+"'", null) > 0;
        db.close();
        return result;
    }

    //	method to update an already created transaction.. returns 0 for fail, 1 for success
    public int updateOldTransaction(TransactionModel transactionModel){
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("CAT_ID", transactionModel.getCAT_ID());
        values.put("SPNT_ON_ID", transactionModel.getSPNT_ON_ID());
        values.put("ACC_ID", transactionModel.getACC_ID());
        values.put("REPEAT_ID", transactionModel.getREPEAT_ID());
        values.put("TRAN_AMT", transactionModel.getTRAN_AMT());
        values.put("TRAN_NAME", transactionModel.getTRAN_NAME());
        values.put("TRAN_TYPE", transactionModel.getTRAN_TYPE());
        values.put("TRAN_NOTE", transactionModel.getTRAN_NOTE());
        values.put("NOTIFY", transactionModel.getNOTIFY());
        values.put("NOTIFY_TIME", transactionModel.getNOTIFY_TIME());
        values.put("SCHD_UPTO_DATE", transactionModel.getSCHD_UPTO_DATE());
        values.put("TRAN_DATE", DB_DATE_FORMAT_SDF.format(transactionModel.getTRAN_DATE()));
        values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

		// Updating an old Row
        int result = db.update(DB_TABLE_TRANSACTION, values,	"TRAN_ID = '" + transactionModel.getTRAN_ID() + "'", null);
        db.close();
		return result;
    }

    //	method to add a new transaction..returns -1 on fail to add new transaction. row id on success
    public long addNewTransaction(TransactionModel transactionModel){
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

		values.put("TRAN_ID", transactionModel.getTRAN_ID());
		values.put("USER_ID", transactionModel.getUSER_ID());
		values.put("CAT_ID", transactionModel.getCAT_ID());
		values.put("SPNT_ON_ID", transactionModel.getSPNT_ON_ID());
		values.put("ACC_ID", transactionModel.getACC_ID());
		values.put("TRAN_AMT", transactionModel.getTRAN_AMT());
		values.put("TRAN_NAME", transactionModel.getTRAN_NAME());
		values.put("TRAN_TYPE", transactionModel.getTRAN_TYPE());
		values.put("TRAN_NOTE", transactionModel.getTRAN_NOTE());
        values.put("REPEAT_ID", transactionModel.getREPEAT_ID());
        values.put("NOTIFY", transactionModel.getNOTIFY());
        values.put("NOTIFY_TIME", transactionModel.getNOTIFY_TIME());
        values.put("SCHD_UPTO_DATE", transactionModel.getSCHD_UPTO_DATE());
        values.put("TRAN_DATE", DB_DATE_FORMAT_SDF.format(transactionModel.getTRAN_DATE()));
		values.put("CREAT_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

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

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	//constructors
	public TransactionsDbService(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}
}
