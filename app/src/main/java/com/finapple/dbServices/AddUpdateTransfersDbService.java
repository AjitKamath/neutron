package com.finapple.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finapple.model.AccountsModel;
import com.finapple.model.TransferModel;
import com.finapple.util.ColumnFetcher;
import com.finapple.util.Constants;
import com.finapple.util.IdGenerator;

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
	private static final int DATABASE_VERSION = 1;

    //	method to update an already created transfer.. returns 0 for fail, 1 for success
    public int updateOldTransfer(TransferModel transferModel){
		SQLiteDatabase db = this.getWritableDatabase();

        //undo the previous transaction for this ID

        //get all the transfer details for the transfer ID
        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" TRNFR_AMT, ");
        sqlQuerySB.append(" ACC_ID_FRM, ");
        sqlQuerySB.append(" ACC_ID_TO ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(TRANSFERS_TABLE);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" TRNFR_ID = '"+transferModel.getTRNFR_ID()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" TRNFR_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" USER_ID = '"+transferModel.getUSER_ID()+"' ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        Double currAmt = 0.0;
        String fromAccIdStr = "", toAccIdStr = "";
        while (cursor.moveToNext())
        {
            currAmt = ColumnFetcher.getInstance().loadDouble(cursor, "TRNFR_AMT");
            fromAccIdStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_ID_FRM");
            toAccIdStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_ID_TO");
        }

        //get current accounts acc id and total
        sqlQuerySB = null;
        sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" ACC_ID, ");
        sqlQuerySB.append(" ACC_TOTAL ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(accountTable);

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID IN ( '"+fromAccIdStr+"', '"+toAccIdStr+"' )");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" USER_ID = '"+transferModel.getTRNFR_ID()+"' ");

        cursor = db.rawQuery(sqlQuerySB.toString(), null);

        Map<String, AccountsModel> accMap = new HashMap<>();
        while (cursor.moveToNext())
        {
            AccountsModel accountsModel = new AccountsModel();
            accountsModel.setACC_ID(ColumnFetcher.getInstance().loadString(cursor, "ACC_ID"));
            accountsModel.setACC_TOTAL(ColumnFetcher.getInstance().loadDouble(cursor, "ACC_TOTAL"));
            accMap.put(accountsModel.getACC_ID(), accountsModel);
        }

        //now update from acc = current total + previous, to acc = curr total - previous
        ContentValues values = new ContentValues();

        values.put("ACC_ID", fromAccIdStr);
        values.put("ACC_TOTAL", String.valueOf(accMap.get(fromAccIdStr).getACC_TOTAL() + currAmt));

        // Updating an old Row
        int result = db.update(accountTable, values,	"ACC_ID = '" + fromAccIdStr + "'", null);

        values.put("ACC_ID", toAccIdStr);
        values.put("ACC_TOTAL", String.valueOf(accMap.get(toAccIdStr).getACC_TOTAL() - currAmt));

        // Updating an old Row
        result = db.update(accountTable, values,	"ACC_ID = '" + toAccIdStr + "'", null);

        //now go for the updating with the new values
        //for from account
        sqlQuerySB = null;
        sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" UPDATE ");
        sqlQuerySB.append(accountTable);

        sqlQuerySB.append(" SET ");
        sqlQuerySB.append(" ACC_TOTAL = ACC_TOTAL - "+transferModel.getTRNFR_AMT());

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID = '"+transferModel.getACC_ID_FRM()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" USER_ID = '"+transferModel.getTRNFR_ID()+"' ");

        //update
        db.rawQuery(sqlQuerySB.toString(), null);

        //for to account
        sqlQuerySB = null;
        sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" UPDATE ");
        sqlQuerySB.append(accountTable);

        sqlQuerySB.append(" SET ");
        sqlQuerySB.append(" ACC_TOTAL = ACC_TOTAL + "+transferModel.getTRNFR_AMT());

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" ACC_ID = '"+transferModel.getACC_ID_TO()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" USER_ID = '"+transferModel.getTRNFR_ID()+"' ");

        //update
        db.rawQuery(sqlQuerySB.toString(), null);

        //update row in transfers table
        values = null;
        values = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        values.put("ACC_ID_FRM", transferModel.getACC_ID_FRM());
        values.put("ACC_ID_TO", transferModel.getACC_ID_TO());
        values.put("TRNFR_AMT", transferModel.getTRNFR_AMT());
        values.put("TRNFR_NOTE", transferModel.getTRNFR_NOTE());
        values.put("MOD_DTM", sdf.format(new Date()));

		// Updating an old Row
		result = db.update(TRANSFERS_TABLE, values,	"TRNFR_ID = '" + transferModel.getTRNFR_ID() + "'", null);

        return result;
    }

    //	method to add a new transaction..returns -1 on fail to add new transaction. 0 on failed to update accounts
    public long addNewTransfer(TransferModel transferModel){
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

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

        //do not continue if insert failed
        if(result == -1){
            Log.e(CLASS_NAME, "New transfer couldn't be saved in db...not continuing to avoid data discrepency");
            return result;
        }

        //now go for the updating with the new values
        //for from account

        //get acc total of from and to accounts
        StringBuilder sqlQuerySB = new StringBuilder(50);
        sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" ACC_TOTAL, ");
        sqlQuerySB.append(" ACC_ID ");
        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(accountTable);
        sqlQuerySB.append(" WHERE  ");
        sqlQuerySB.append(" (ACC_ID = '"+transferModel.getACC_ID_FRM()+"' ");
        sqlQuerySB.append(" OR ");
        sqlQuerySB.append(" ACC_ID = '"+transferModel.getACC_ID_TO()+"') ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" USER_ID = '"+transferModel.getTRNFR_ID()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" ACC_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");

        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        Double currenFromAccTotal = 0.0, currentToAccTotal = 0.0;
        while (cursor.moveToNext()){
            Double temp = ColumnFetcher.getInstance().loadDouble(cursor, "ACC_TOTAL");

            if(transferModel.getACC_ID_FRM().equalsIgnoreCase(ColumnFetcher.getInstance().loadString(cursor, "ACC_ID"))){
                currenFromAccTotal = temp;
            }
            else{
                currentToAccTotal = temp;
            }
        }

        //calc total
        currenFromAccTotal = currenFromAccTotal - transferModel.getTRNFR_AMT();
        currentToAccTotal = currentToAccTotal + transferModel.getTRNFR_AMT();

        values.clear();
        values = new ContentValues();

        values.put("ACC_TOTAL", currenFromAccTotal);
        values.put("MOD_DTM", sdf.format(new Date()));

        result = db.update(accountTable, values, "ACC_ID = '"+transferModel.getACC_ID_FRM()+"'", null);

        if(result == -1){
            return result;
        }

        //update
        values.clear();
        values = new ContentValues();

        values.put("ACC_TOTAL", currentToAccTotal);
        values.put("MOD_DTM", sdf.format(new Date()));

        result = db.update(accountTable, values, "ACC_ID = '"+transferModel.getACC_ID_TO()+"'", null);

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
