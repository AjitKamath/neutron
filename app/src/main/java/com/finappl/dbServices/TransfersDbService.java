package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.TransferMO;

import java.util.Date;

import static com.finappl.utils.Constants.DB_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT_SDF;
import static com.finappl.utils.Constants.DB_NAME;
import static com.finappl.utils.Constants.DB_TABLE_TRANSFER;
import static com.finappl.utils.Constants.DB_VERSION;

public class TransfersDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    public boolean deleteTransfer(String transferIdStr){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = db.delete(DB_TABLE_TRANSFER, "TRNFR_ID = '" + transferIdStr+"'", null) > 0;
        db.close();
        return result;
    }

    public long addNewTransfer(TransferMO transfer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("TRNFR_ID", transfer.getTRNFR_ID());
        values.put("USER_ID", transfer.getUSER_ID());
        values.put("ACC_ID_FRM", transfer.getACC_ID_FRM());
        values.put("ACC_ID_TO", transfer.getACC_ID_TO());
        values.put("TRNFR_AMT", transfer.getTRNFR_AMT());
        values.put("TRNFR_NOTE", transfer.getTRNFR_NOTE());
        values.put("REPEAT_ID", transfer.getREPEAT_ID());
        values.put("NOTIFY", transfer.getNOTIFY());
        values.put("NOTIFY_TIME", transfer.getNOTIFY_TIME());
        values.put("SCHD_UPTO_DATE", transfer.getSCHD_UPTO_DATE());
        values.put("TRNFR_DATE", DB_DATE_FORMAT_SDF.format(transfer.getTRNFR_DATE()));
        values.put("CREAT_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

        long result = 0;
        try {
            // Inserting a new Row
            result =  db.insertOrThrow(DB_TABLE_TRANSFER, null, values);
        }
        catch(Exception e){
            Log.e(CLASS_NAME, "Error while adding transfer:"+e);
        }
        db.close();
        return result;
    }

    public long updateOldTransfer(TransferMO transfer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("ACC_ID_FRM", transfer.getACC_ID_FRM());
        values.put("ACC_ID_TO", transfer.getACC_ID_TO());
        values.put("REPEAT_ID", transfer.getREPEAT_ID());
        values.put("TRNFR_AMT", transfer.getTRNFR_AMT());
        values.put("TRNFR_NOTE", transfer.getTRNFR_NOTE());
        values.put("NOTIFY", transfer.getNOTIFY());
        values.put("NOTIFY_TIME", transfer.getNOTIFY_TIME());
        values.put("SCHD_UPTO_DATE", transfer.getSCHD_UPTO_DATE());
        values.put("TRNFR_DATE", DB_DATE_FORMAT_SDF.format(transfer.getTRNFR_DATE()));
        values.put("MOD_DTM", DB_DATE_TIME_FORMAT_SDF.format(new Date()));

        // Updating an old Row
        int result = db.update(DB_TABLE_TRANSFER, values,	"TRNFR_ID = '" + transfer.getTRNFR_ID() + "'", null);
        db.close();
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

	//constructors
	public TransfersDbService(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
