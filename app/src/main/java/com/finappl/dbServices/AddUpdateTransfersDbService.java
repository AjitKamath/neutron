package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.AccountsModel;
import com.finappl.models.TransferModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;
import com.finappl.utils.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddUpdateTransfersDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    //db tables
    private static final String USERS_TABLE = Constants.DB_TABLE_USERSTABLE;
    private static final String accountTable = Constants.DB_TABLE_ACCOUNTTABLE;
    private static final String categoryTable = Constants.DB_TABLE_CATEGORYTABLE;
    private static final String spentOnTable = Constants.DB_TABLE_SPENTONTABLE;
    private static final String transactionTable = Constants.DB_TABLE_TRANSACTIONTABLE;
    private static final String scheduledTransactionsTable = Constants.DB_TABLE_SCHEDULEDTRANSACTIONSTABLE;
    private static final String budgetTable = Constants.DB_TABLE_BUDGETTABLE;
    private static final String TRANSFERS_TABLE = Constants.DB_TABLE_TRANSFERSTABLE;

	private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = Constants.DB_VERSION;

    //	method to update an already created transfer.. returns 0 for fail, 1 for success
    public int updateOldTransfer(TransferModel transferModel){
		SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        values.put("ACC_ID_FRM", transferModel.getACC_ID_FRM());
        values.put("ACC_ID_TO", transferModel.getACC_ID_TO());
        values.put("TRNFR_AMT", transferModel.getTRNFR_AMT());
        values.put("TRNFR_NOTE", transferModel.getTRNFR_NOTE());
        values.put("MOD_DTM", sdf.format(new Date()));

		// Updating an old Row
		int result = db.update(TRANSFERS_TABLE, values,	"TRNFR_ID = '" + transferModel.getTRNFR_ID() + "'", null);

        return result;
    }

    //	method to add a new transaction..returns -1 on fail to add new transaction. 0 on failed to update accounts
    public long addNewTransfer(TransferModel transferModel){
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        values.put("TRNFR_ID", IdGenerator.getInstance().generateUniqueId("TRNSFR"));
		values.put("USER_ID", transferModel.getUSER_ID());
		values.put("ACC_ID_FRM", transferModel.getACC_ID_FRM());
		values.put("ACC_ID_TO", transferModel.getACC_ID_TO());
		values.put("TRNFR_AMT", transferModel.getTRNFR_AMT());
		values.put("TRNFR_IS_DEL", Constants.DB_NONAFFIRMATIVE);
		values.put("TRNFR_NOTE", transferModel.getTRNFR_NOTE());
		values.put("TRNFR_DATE", transferModel.getTRNFR_DATE());
		values.put("CREAT_DTM", sdf.format(new Date()));

		// Inserting a new Row
		long result =  db.insert(TRANSFERS_TABLE, null, values);
        return result;
    }

    //--------------------- end of method to get all pay type--------------------------//

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	//constructors
	public AddUpdateTransfersDbService(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	//getters setters
}
