package com.finapple.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.finapple.service.NotificationsService;

/**
 * Created by ajit on 15/8/15.
 */
public class DateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, NotificationsService.class);
        context.startService(service);

    }

}
