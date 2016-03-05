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

import static com.finappl.utils.Constants.DB_DATE_FORMAT;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_TABLE_TRANSFERSTABLE;
import static com.finappl.utils.Constants.DB_VERSION;

public class AddUpdateTransfersDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
    private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

    //	method to update an already created transfer.. returns 0 for fail, 1 for success
    public int updateOldTransfer(TransferModel transferModel){
		SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        values.put("ACC_ID_FRM", transferModel.getACC_ID_FRM());
        values.put("ACC_ID_TO", transferModel.getACC_ID_TO());
        values.put("TRNFR_AMT", transferModel.getTRNFR_AMT());
        values.put("TRNFR_NOTE", transferModel.getTRNFR_NOTE());
        values.put("MOD_DTM", simpleDateTimeFormat.format(new Date()));

		// Updating an old Row
		int result = db.update(DB_TABLE_TRANSFERSTABLE, values,	"TRNFR_ID = '" + transferModel.getTRNFR_ID() + "'", null);

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
		values.put("TRNFR_DATE", simpleDateFormat.format(transferModel.getTRNFR_DATE()));
		values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));

		// Inserting a new Row
		long result =  db.insert(DB_TABLE_TRANSFERSTABLE, null, values);
        return result;
    }

    //--------------------- end of method to get all pay type--------------------------//

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	//constructors
	public AddUpdateTransfersDbService(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
