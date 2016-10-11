package com.finappl.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.finappl.models.NotificationModel;
import com.finappl.utils.Constants;
import com.finappl.utils.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.finappl.utils.Constants.*;

public class NotificationDbService extends SQLiteOpenHelper {

    private final String CLASS_NAME = this.getClass().getName();

    public long cancelOrAddNotif(NotificationModel notificationModelObj){
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
        SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

        ContentValues values = new ContentValues();

        values.put("CNCL_NOTIF_ID", IdGenerator.getInstance().generateUniqueId("NOTIF"));
        values.put("USER_ID", notificationModelObj.getUSER_ID());
        values.put("CNCL_NOTIF_TYPE", notificationModelObj.getCNCL_NOTIF_TYPE());
        values.put("CNCL_NOTIF_EVNT_ID", notificationModelObj.getCNCL_NOTIF_EVNT_ID());
        values.put("CNCL_NOTIF_RSN", notificationModelObj.getCNCL_NOTIF_RSN());
        values.put("CNCL_NOTIF_DATE", simpleDateFormat.format(notificationModelObj.getCNCL_NOTIF_DATE()));
        values.put("CREAT_DTM", simpleDateTimeFormat.format(new Date()));

        long result = db.insert(DB_TABLE_NOTIFICATION, null, values);
        db.close();
        return result;
    }

    @Override
	public void onCreate(SQLiteDatabase db) {

	}

	public NotificationDbService(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
