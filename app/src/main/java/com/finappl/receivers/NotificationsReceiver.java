package com.finappl.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.finappl.dbServices.CalendarDbService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ajit on 15/8/15.
 */
public class NotificationsReceiver extends BroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        String actionStr = intent.getAction();

        boolean checkForSchedules = false;

        //when the app is installed on phone memory and the phone is restarted
        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(actionStr)) {
            checkForSchedules = true;
        }

        //when the app is installed on sd memory and the phone is restarted
        if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equalsIgnoreCase(actionStr)) {
            checkForSchedules = true;
        }

        //when the time/date is set/changed by the user
        if (Intent.ACTION_TIME_CHANGED.equalsIgnoreCase(actionStr)) {
            checkForSchedules = true;
        }

        //when the date changes at 12:00 AM
        if (Intent.ACTION_DATE_CHANGED.equalsIgnoreCase(actionStr)) {
            checkForSchedules = true;
        }

        List<Object> notificationsList = new ArrayList<>();
        if (checkForSchedules) {
            //notificationsList.addAll(getSchedules());
        }



    }

    /*private List<?> getSchedules() {
        CalendarDbService calendarDbService = new CalendarDbService(mContext);

        //calendarDbService.getS
    }*/


}
