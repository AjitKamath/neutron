package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.ScheduledTransferModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;
import com.finappl.utils.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduledTransfersDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    private static final String DATABASE_NAME = Constants.DB_NAME;
	private static final int DATABASE_VERSION = Constants.DB_VERSION;

    //this method creates a new transfer based on the users input
    public Long createNewScheduledTransfer(ScheduledTransferModel scheduledTransferModelObj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        values.put("SCH_TRNFR_ID", IdGenerator.getInstance().generateUniqueId("SCH_TRNFR"));
        values.put("USER_ID", scheduledTransferModelObj.getUSER_ID());
        values.put("SCH_TRNFR_ACC_ID_FRM", scheduledTransferModelObj.getSCH_TRNFR_ACC_ID_FRM());
        values.put("SCH_TRNFR_ACC_ID_TO", scheduledTransferModelObj.getSCH_TRNFR_ACC_ID_TO());
        values.put("SCH_TRNFR_DATE", scheduledTransferModelObj.getSCH_TRNFR_DATE());
        values.put("SCH_TRNFR_FREQ", scheduledTransferModelObj.getSCH_TRNFR_FREQ());
        values.put("SCH_TRNFR_AMT", scheduledTransferModelObj.getSCH_TRNFR_AMT());
        values.put("SCH_TRNFR_NOTE", scheduledTransferModelObj.getSCH_TRNFR_NOTE());
        values.put("SCH_TRNFR_AUTO", scheduledTransferModelObj.getSCH_TRNFR_AUTO());
        values.put("SCH_TRNFR_IS_DEL", Constants.DB_NONAFFIRMATIVE);
        values.put("CREAT_DTM", sdf.format(new Date()));

        // inserting a new row
        return db.insert(Constants.DB_TABLE_SHEDULEDTRANSFERSTABLE, null, values);
    }

    //this method creates a update scheduled transfer based on the users input
    public int updateOldScheduledTransfer(ScheduledTransferModel scheduledTransferModelObj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        values.put("SCH_TRNFR_ACC_ID_FRM", scheduledTransferModelObj.getSCH_TRNFR_ACC_ID_FRM());
        values.put("SCH_TRNFR_ACC_ID_TO", scheduledTransferModelObj.getSCH_TRNFR_ACC_ID_TO());
        values.put("SCH_TRNFR_DATE", scheduledTransferModelObj.getSCH_TRNFR_DATE());
        values.put("SCH_TRNFR_FREQ", scheduledTransferModelObj.getSCH_TRNFR_FREQ());
        values.put("SCH_TRNFR_AMT", scheduledTransferModelObj.getSCH_TRNFR_AMT());
        values.put("SCH_TRNFR_NOTE", scheduledTransferModelObj.getSCH_TRNFR_NOTE());
        values.put("SCH_TRNFR_AUTO", scheduledTransferModelObj.getSCH_TRNFR_AUTO());
        values.put("CREAT_DTM", sdf.format(new Date()));

        // update a old row
        return db.update(Constants.DB_TABLE_SHEDULEDTRANSFERSTABLE, values, "SCH_TRNFR_ID = '" + scheduledTransferModelObj.getSCH_TRNFR_ID() + "'", null);
    }

    //Gets the Scheduled Transfer using the Scheduled Transfer Id & User Id and returns back the Scheduled Transfer object
    public ScheduledTransferModel getScheduledTransferOnScheduledTransferId(ScheduledTransferModel scheduledTransferModelObj){
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sqlQuerySB = new StringBuilder(50);

        sqlQuerySB.append(" SELECT ");
        sqlQuerySB.append(" SCH_TRNFR_DATE, ");
        sqlQuerySB.append(" SCH_TRNFR_ACC_ID_FRM, ");
        sqlQuerySB.append(" SCH_TRNFR_ACC_ID_TO, ");
        sqlQuerySB.append(" SCH_TRNFR_FREQ, ");
        sqlQuerySB.append(" SCH_TRNFR_AMT, ");
        sqlQuerySB.append(" SCH_TRNFR_NOTE, ");
        sqlQuerySB.append(" SCH_TRNFR_AUTO, ");
        sqlQuerySB.append(" SCH.CREAT_DTM, ");
        sqlQuerySB.append(" SCH.MOD_DTM, ");
        sqlQuerySB.append(" FRM_ACC.ACC_NAME AS ACC_FRM, ");
        sqlQuerySB.append(" TO_ACC.ACC_NAME AS TO_FRM ");

        sqlQuerySB.append(" FROM ");
        sqlQuerySB.append(Constants.DB_TABLE_SHEDULEDTRANSFERSTABLE + " SCH ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(Constants.DB_TABLE_ACCOUNTTABLE+" FRM_ACC ");
        sqlQuerySB.append(" ON FRM_ACC.ACC_ID = SCH.SCH_TRNFR_ACC_ID_FRM ");

        sqlQuerySB.append(" INNER JOIN ");
        sqlQuerySB.append(Constants.DB_TABLE_ACCOUNTTABLE+" TO_ACC ");
        sqlQuerySB.append(" ON TO_ACC.ACC_ID = SCH.SCH_TRNFR_ACC_ID_TO ");

        sqlQuerySB.append(" WHERE ");
        sqlQuerySB.append(" SCH.USER_ID = '"+scheduledTransferModelObj.getUSER_ID()+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" SCH_TRNFR_IS_DEL = '"+Constants.DB_NONAFFIRMATIVE+"' ");
        sqlQuerySB.append(" AND ");
        sqlQuerySB.append(" SCH_TRNFR_ID = '"+scheduledTransferModelObj.getSCH_TRNFR_ID()+"' ");

        Log.i(CLASS_NAME, "Query to fetch Scheduled Transfers using Scheduled Transfer ID  :"+sqlQuerySB);
        Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

        while (cursor.moveToNext()){
            String schTransferDateStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_DATE");
            String schTransferFromAccNameStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_FRM");
            String schTransferToAccNameStr = ColumnFetcher.getInstance().loadString(cursor, "TO_FRM");
            String schTransferFromAccIdStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_ACC_ID_FRM");
            String schTransferToAccIdStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_ACC_ID_TO");
            String schTransferFreqStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_FREQ");
            Double schTransferAmt = ColumnFetcher.getInstance().loadDouble(cursor, "SCH_TRNFR_AMT");
            String schTransferNoteStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_NOTE");
            String schTransferAutoStr = ColumnFetcher.getInstance().loadString(cursor, "SCH_TRNFR_AUTO");
            String schTransferCreateDtmStr = ColumnFetcher.getInstance().loadString(cursor, "CREAT_DTM");
            String schTransferModDtmStr = ColumnFetcher.getInstance().loadString(cursor, "MOD_DTM");

            scheduledTransferModelObj.setSCH_TRNFR_DATE(schTransferDateStr);
            scheduledTransferModelObj.setFromAccountStr(schTransferFromAccNameStr);
            scheduledTransferModelObj.setToAccountStr(schTransferToAccNameStr);
            scheduledTransferModelObj.setSCH_TRNFR_ACC_ID_FRM(schTransferFromAccIdStr);
            scheduledTransferModelObj.setSCH_TRNFR_ACC_ID_TO(schTransferToAccIdStr);
            scheduledTransferModelObj.setSCH_TRNFR_FREQ(schTransferFreqStr);
            scheduledTransferModelObj.setSCH_TRNFR_AMT(schTransferAmt);
            scheduledTransferModelObj.setSCH_TRNFR_NOTE(schTransferNoteStr);
            scheduledTransferModelObj.setSCH_TRNFR_AUTO(schTransferAutoStr);
            scheduledTransferModelObj.setCREAT_DTM(schTransferCreateDtmStr);
            scheduledTransferModelObj.setMOD_DTM(schTransferModDtmStr);

            return scheduledTransferModelObj;
        }
        cursor.close();

        Log.e(CLASS_NAME, "If i'm printing, you must have screwed up. scheduledTransferModelObj shouldnt be null at this point");
        return null;
    }

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	public ScheduledTransfersDbService(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
