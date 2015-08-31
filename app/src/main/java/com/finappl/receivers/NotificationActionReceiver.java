package com.finappl.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.finappl.activities.JimBrokeItActivity;
import com.finappl.activities.LoginActivity;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.NotificationDbService;
import com.finappl.models.NotificationModel;
import com.finappl.models.ScheduledTransactionModel;
import com.finappl.models.ScheduledTransferModel;
import com.finappl.models.UsersModel;

import java.util.Map;

/**
 * Created by ajit on 9/8/15.
 */

public class NotificationActionReceiver extends BroadcastReceiver {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    public static String NOTIF_ACTION = "CANCEL";

    private NotificationDbService notificationDbService;

    //User
    private UsersModel loggedInUserObj;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        if(intent.getExtras() == null){
            Log.e(CLASS_NAME, "Extras in Intent is not supposed to be null here... ");
            showToast("Notification Error !");
            return;
        }

        notificationDbService = new NotificationDbService(mContext);

        //get the Active user
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return;
        }


        //for CANCEL action in notification STARTS--
        //get the id of the notification from the intent and kill it
        if(intent.getExtras().get("CANCEL_NOTIF_ID") != null){
            Log.i(CLASS_NAME, "User wants to cancel notification having NOTIFICATION ID = " + intent.getExtras().get("CANCEL_NOTIF_ID"));
            NotificationManager manager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
            manager.cancel(Integer.parseInt(String.valueOf(intent.getExtras().get("CANCEL_NOTIF_ID"))));
            Log.i(CLASS_NAME, "Notification having NOTIFICATION ID = " + intent.getExtras().get("CANCEL_NOTIF_ID")+" has been cancelled");

            //now update the NOTIFICATIONS table saying that user has cancelled this notification
            NotificationModel notificationModelObj = new NotificationModel();

            //if the notification was for transactions
            if(intent.getExtras().get("SCH_TRANSACTION") != null){
                ScheduledTransactionModel scheduledTransactionModelObj = (ScheduledTransactionModel)intent.getExtras().get("SCH_TRANSACTION");
                notificationModelObj.setCNCL_NOTIF_DATE(scheduledTransactionModelObj.getSCH_TRAN_DATE());
                notificationModelObj.setCNCL_NOTIF_TYPE("TRANSACTION");
                notificationModelObj.setCNCL_NOTIF_EVNT_ID(scheduledTransactionModelObj.getSCH_TRAN_ID());
            }
            else if(intent.getExtras().get("SCH_TRANSFER") != null){
                ScheduledTransferModel scheduledTransferModelObj = (ScheduledTransferModel)intent.getExtras().get("SCH_TRANSFER");
                notificationModelObj.setCNCL_NOTIF_DATE(scheduledTransferModelObj.getSCH_TRNFR_DATE());
                notificationModelObj.setCNCL_NOTIF_TYPE("TRANSFER");
                notificationModelObj.setCNCL_NOTIF_EVNT_ID(scheduledTransferModelObj.getSCH_TRNFR_ID());
            }
            else{
                Log.e(CLASS_NAME, "Error !! Expected either SCH_TRANSACTION or SCH_TRANSFER in the intent. Found neither");
                return;
            }

            notificationModelObj.setCNCL_NOTIF_RSN("CANCEL");
            notificationModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());
            long result = notificationDbService.cancelOrAddNotif(notificationModelObj);

            if(result == -1){
                showToast("Something went wrong !");
                return;
            }

            if("TRANSACTION".equalsIgnoreCase(notificationModelObj.getCNCL_NOTIF_TYPE())){
                showToast("Scheduled Transaction has been cancelled");
            }
            else if("TRANSFER".equalsIgnoreCase(notificationModelObj.getCNCL_NOTIF_TYPE())){
                showToast("Scheduled Transfer has been cancelled");
            }
        }
        //for CANCEL action in notification ENDS--

        //for ADD action in notification starts--
        //TODO: ADD action yet to be implemented
        //for ADD action in notification ends--
    }

    private UsersModel getUser(){
        AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);
        Map<Integer, UsersModel> userMap = authorizationDbService.getActiveUser();

        if(userMap == null || (userMap != null && userMap.isEmpty())){
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
            showToast("Please Login");
            return null;
        }
        else if(userMap.size() > 1){
            Intent intent = new Intent(mContext, JimBrokeItActivity.class);
            mContext.startActivity(intent);
            showToast("Multiple Users are Active : Possible DB Corruption.");
        }
        else{
            return userMap.get(0);
        }

        Log.e(CLASS_NAME, "I'm not supposed to be read/print/shown..... This should have been a dead code. If you can read me, Authorization of user has failed and you should " +
                "probably die twice by now.");
        return null;
    }

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

}
