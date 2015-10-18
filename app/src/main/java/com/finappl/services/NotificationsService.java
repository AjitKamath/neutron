package com.finappl.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.dbServices.AddUpdateTransactionsDbService;
import com.finappl.dbServices.AddUpdateTransfersDbService;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.NotificationDbService;
import com.finappl.models.MonthLegend;
import com.finappl.models.NotificationActionModel;
import com.finappl.models.NotificationModel;
import com.finappl.models.ScheduledTransactionModel;
import com.finappl.models.ScheduledTransferModel;
import com.finappl.models.TodaysNotifications;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.models.UsersModel;
import com.finappl.utils.IdGenerator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajit on 15/8/15.
 */
public class NotificationsService extends Service {
    private final String CLASS_NAME = this.getClass().getName();

    private Context mContext = this;

    private UsersModel loggedInUserObj;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        //get the Active user
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return Service.START_STICKY;
        }
        else if("N".equalsIgnoreCase(loggedInUserObj.getSET_NOTIF_ACTIVE())){
            return Service.START_STICKY;
        }

        //handling notification actions
        if(intent != null && intent.getExtras() != null && intent.getExtras().get("NOTIFICATION_ACTION") != null){
            Log.i(CLASS_NAME, "User Performed action on a notification");
            NotificationActionModel notificationActionModelObj = (NotificationActionModel) intent.getExtras().get("NOTIFICATION_ACTION");
            handleNotificationAction(notificationActionModelObj);
            return Service.START_STICKY;
        }

        final long timeDelay = getTimeDelayForNotificationOnTime(loggedInUserObj.getSET_NOTIF_TIME());

        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //build notifications
                buildAllNotifications(getTodaysNotifications());
            }

        }, timeDelay);

        return Service.START_STICKY;
    }

    private void handleNotificationAction(NotificationActionModel notificationActionModelObj){
        NotificationDbService notificationDbService = new NotificationDbService(mContext);

        //to cancel the notifications
        if("CANCEL".equalsIgnoreCase(notificationActionModelObj.getNotificationActionStr())){
            Log.i(CLASS_NAME, "User Performed 'CANCEL' action on a notification");
            Log.i(CLASS_NAME, "Attempting to cancel the Notification..");
            NotificationManager manager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
            manager.cancel(String.valueOf(notificationActionModelObj.getNotificationIdStr()), Integer.parseInt(String.valueOf(notificationActionModelObj.getNotificationIdStr())));
            Log.i(CLASS_NAME, "Attempting is completed. Notification must have disappeared by now");

            NotificationModel notificationModelObj = new NotificationModel();

            if("SCHEDULED_TRANSFER".equalsIgnoreCase(notificationActionModelObj.getNotificationTypeStr())) {
                Log.i(CLASS_NAME, "The Notifications was for 'SCHEDULED_TRANSFER'");
                ScheduledTransferModel scheduledTransferModelObj = (ScheduledTransferModel) notificationActionModelObj.getNotificationObject();
                notificationModelObj.setCNCL_NOTIF_DATE(scheduledTransferModelObj.getSCH_TRNFR_DATE());
                notificationModelObj.setCNCL_NOTIF_TYPE("SCHEDULED_TRANSFER");
                notificationModelObj.setCNCL_NOTIF_EVNT_ID(scheduledTransferModelObj.getSCH_TRNFR_ID());
            }
            else if("SCHEDULED_TRANSACTION".equalsIgnoreCase(notificationActionModelObj.getNotificationTypeStr())){
                Log.i(CLASS_NAME, "The Notifications was for 'SCHEDULED_TRANSACTION'");
                ScheduledTransactionModel scheduledTransactionModelObj = (ScheduledTransactionModel) notificationActionModelObj.getNotificationObject();
                notificationModelObj.setCNCL_NOTIF_DATE(scheduledTransactionModelObj.getSCH_TRAN_DATE());
                notificationModelObj.setCNCL_NOTIF_TYPE("SCHEDULED_TRANSACTION");
                notificationModelObj.setCNCL_NOTIF_EVNT_ID(scheduledTransactionModelObj.getSCH_TRAN_ID());
            }
            else{
                Log.e(CLASS_NAME, "Error !! Expected either SCHEDULED_TRANSACTION or SCHEDULED_TRANSFER in the intent. Found neither");
                return;
            }

            notificationModelObj.setCNCL_NOTIF_RSN("CANCEL");
            notificationModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());

            Log.i(CLASS_NAME, "Attempting to save the cancelled notification into db..");
            long result = notificationDbService.cancelOrAddNotif(notificationModelObj);

            if(result == -1){
                Log.e(CLASS_NAME, "Error !! Could not save the notification into db !!");
                showToast("Something went wrong !");
                return;
            }

            if("SCHEDULED_TRANSACTION".equalsIgnoreCase(notificationActionModelObj.getNotificationTypeStr())){
                Log.i(CLASS_NAME, "Cancelled Notification for 'SCHEDULED_TRANSACTION' is saved into db to avoid repetitions");
                showToast("Scheduled Transaction has been cancelled");
            }
            else if("SCHEDULED_TRANSFER".equalsIgnoreCase(notificationActionModelObj.getNotificationTypeStr())){
                Log.i(CLASS_NAME, "Cancelled Notification for 'SCHEDULED_TRANSFER' is saved into db to avoid repetitions");
                showToast("Scheduled Transfer has been cancelled");
            }
        }
        //to cancel the notifications ends--

        //TODO: yet to implement add and show
    }

    private void buildSchTransferNotification(ScheduledTransferModel scheduledTransferModelObj, UsersModel loggedInUserObj){
        String notificationTitle = this.getResources().getString(R.string.notifTitleSchedTransfer);
        String notificationMessage = this.getResources().getString(R.string.notifMsgSchedules);
        notificationTitle = notificationTitle.replace("XXXXX", loggedInUserObj.getNAME());
        notificationMessage = notificationMessage.replace("YYYYY", "Transfer")+" "+loggedInUserObj.getCurrencyText()+scheduledTransferModelObj.getSCH_TRNFR_AMT();

        //generate unique hash for this particular transfer using its id
        int notifHashCode = IdGenerator.getInstance().getIntegerOnString(scheduledTransferModelObj.getSCH_TRNFR_ID());

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //to handle action on notification when the user clicks on the notification
        Intent intent = new Intent(this, CalendarActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //ACTIONS on notifications starts--
        //to handle add action
        Intent addIntent = new Intent(this, NotificationsService.class);
        addIntent.setAction("ADD");
        NotificationActionModel notificationActionModel = new NotificationActionModel();
        notificationActionModel.setNotificationActionStr("ADD");
        notificationActionModel.setNotificationIdStr(String.valueOf(notifHashCode));
        notificationActionModel.setNotificationTypeStr("SCHEDULED_TRANSFER");
        notificationActionModel.setNotificationObject(scheduledTransferModelObj);
        addIntent.putExtra("NOTIFICATION_ACTION", notificationActionModel);
        PendingIntent pendingIntentAdd = PendingIntent.getService(this, notifHashCode, addIntent, 0);
        //to handle add action

        //to handle cancel action
        Intent cancelIntent = new Intent(this, NotificationsService.class);
        cancelIntent.setAction("CANCEL");
        notificationActionModel = new NotificationActionModel();
        notificationActionModel.setNotificationActionStr("CANCEL");
        notificationActionModel.setNotificationIdStr(String.valueOf(notifHashCode));
        notificationActionModel.setNotificationTypeStr("SCHEDULED_TRANSFER");
        notificationActionModel.setNotificationObject(scheduledTransferModelObj);
        cancelIntent.putExtra("NOTIFICATION_ACTION", notificationActionModel);
        PendingIntent pendingIntentCancel = PendingIntent.getService(this, notifHashCode, cancelIntent, 0);
        //to handle cancel action

        //to handle show action in the notification_scheduled_transfer
        Intent showIntent = new Intent(this, NotificationsService.class);
        cancelIntent.setAction("SHOW");
        notificationActionModel = new NotificationActionModel();
        notificationActionModel.setNotificationActionStr("SHOW");
        notificationActionModel.setNotificationIdStr(String.valueOf(notifHashCode));
        notificationActionModel.setNotificationTypeStr("SCHEDULED_TRANSFER");
        notificationActionModel.setNotificationObject(scheduledTransferModelObj);
        cancelIntent.putExtra("NOTIFICATION_ACTION", notificationActionModel);
        PendingIntent pendingIntentShow = PendingIntent.getService(this, notifHashCode, showIntent, 0);
        //to handle show action
        //ACTIONS on notifications ends--

        Notification mNotification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.india)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .addAction(R.drawable.plus_small_grey, "ADD", pendingIntentAdd)
                .addAction(R.drawable.show_small_grey, "SHOW", pendingIntentShow)
                .addAction(R.drawable.delete_small_grey, "CANCEL", pendingIntentCancel)
                .setStyle(new Notification.InboxStyle()
                        .setBigContentTitle(scheduledTransferModelObj.getFromAccountStr() + " to " + scheduledTransferModelObj.getToAccountStr())
                        .addLine(loggedInUserObj.getCurrencyText() + scheduledTransferModelObj.getSCH_TRNFR_AMT())
                        .setSummaryText("This Scheduled Transfer will happen " + scheduledTransferModelObj.getSCH_TRNFR_FREQ()))
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();

        mNotification.setLatestEventInfo(this, notificationTitle, notificationMessage, pIntent);
        notificationManager.notify(String.valueOf(notifHashCode), notifHashCode, mNotification);
    }

    private void buildSchTransactionNotification(ScheduledTransactionModel scheduledTransactionModelObj, UsersModel loggedInUserObj){
        String notificationTitle = this.getResources().getString(R.string.notifTitleSchedTransaction);
        String notificationMessage = this.getResources().getString(R.string.notifMsgSchedules);
        notificationTitle = notificationTitle.replace("XXXXX", loggedInUserObj.getNAME());
        notificationMessage = notificationMessage.replace("YYYYY", "Transaction")+" "+loggedInUserObj.getCurrencyText()+scheduledTransactionModelObj.getSCH_TRAN_AMT();

        //generate unique hash for this particular transfer using its id
        int notifHashCode = IdGenerator.getInstance().getIntegerOnString(scheduledTransactionModelObj.getSCH_TRAN_ID());

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //to handle action on notification when the user clicks on the notification
        Intent intent = new Intent(this, CalendarActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //ACTIONS on notifications starts--
        //to handle add action
        Intent addIntent = new Intent(this, NotificationsService.class);
        addIntent.setAction("ADD");
        NotificationActionModel notificationActionModel = new NotificationActionModel();
        notificationActionModel.setNotificationActionStr("ADD");
        notificationActionModel.setNotificationIdStr(String.valueOf(notifHashCode));
        notificationActionModel.setNotificationTypeStr("SCHEDULED_TRANSACTION");
        notificationActionModel.setNotificationObject(scheduledTransactionModelObj);
        addIntent.putExtra("NOTIFICATION_ACTION", notificationActionModel);
        PendingIntent pendingIntentAdd = PendingIntent.getService(this, notifHashCode, addIntent, 0);
        //to handle add action

        //to handle cancel action
        Intent cancelIntent = new Intent(this, NotificationsService.class);
        cancelIntent.setAction("CANCEL");
        notificationActionModel = new NotificationActionModel();
        notificationActionModel.setNotificationActionStr("CANCEL");
        notificationActionModel.setNotificationIdStr(String.valueOf(notifHashCode));
        notificationActionModel.setNotificationTypeStr("SCHEDULED_TRANSACTION");
        notificationActionModel.setNotificationObject(scheduledTransactionModelObj);
        cancelIntent.putExtra("NOTIFICATION_ACTION", notificationActionModel);
        PendingIntent pendingIntentCancel = PendingIntent.getService(this, notifHashCode, cancelIntent, 0);
        //to handle cancel action

        //to handle show action
        Intent showIntent = new Intent(this, NotificationsService.class);
        cancelIntent.setAction("SHOW");
        notificationActionModel = new NotificationActionModel();
        notificationActionModel.setNotificationActionStr("SHOW");
        notificationActionModel.setNotificationIdStr(String.valueOf(notifHashCode));
        notificationActionModel.setNotificationTypeStr("SCHEDULED_TRANSACTION");
        notificationActionModel.setNotificationObject(scheduledTransactionModelObj);
        cancelIntent.putExtra("NOTIFICATION_ACTION", notificationActionModel);
        PendingIntent pendingIntentShow = PendingIntent.getService(this, notifHashCode, showIntent, 0);
        //to handle show action
        //ACTIONS on notifications ends--

        Notification mNotification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.india)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .addAction(R.drawable.plus_small_grey, "ADD", pendingIntentAdd)
                .addAction(R.drawable.show_small_grey, "SHOW", pendingIntentShow)
                .addAction(R.drawable.delete_small_grey, "CANCEL", pendingIntentCancel)
                .setStyle(new Notification.InboxStyle()
                        .setBigContentTitle(scheduledTransactionModelObj.getSCH_TRAN_NAME())
                        .addLine(loggedInUserObj.getCurrencyText() + scheduledTransactionModelObj.getSCH_TRAN_AMT() + " (" + scheduledTransactionModelObj.getSCH_TRAN_TYPE() + ")")
                        .setSummaryText("This Scheduled Transaction will happen " + scheduledTransactionModelObj.getSCH_TRAN_FREQ()))
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();

        mNotification.setLatestEventInfo(this, notificationTitle, notificationMessage, pIntent);
        notificationManager.notify(String.valueOf(notifHashCode), notifHashCode, mNotification);
    }

    private TodaysNotifications getTodaysNotifications(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String todayStr = sdf.format(new Date());


        Map<String, MonthLegend> monthLegendMap = new HashMap<>();

        //get all schedules from the db
        CalendarDbService calendarDbService = new CalendarDbService(getApplicationContext());
        monthLegendMap = calendarDbService.getScheduledTransactions(monthLegendMap, todayStr, loggedInUserObj.getUSER_ID());
        monthLegendMap = calendarDbService.getScheduledTransfers(monthLegendMap, todayStr, loggedInUserObj.getUSER_ID());

        //get all the scheduled Transactions/Transfers from the month legend
        List<ScheduledTransactionModel> schedTransactionModelObjList = null;
        List<ScheduledTransferModel> scheduledTransferModelObjList = null;

        if(monthLegendMap != null && !monthLegendMap.isEmpty() && monthLegendMap.get(todayStr) != null
                && ((monthLegendMap.get(todayStr).getScheduledTransactionModelList() != null && !monthLegendMap.get(todayStr).getScheduledTransactionModelList().isEmpty())
                || ((monthLegendMap.get(todayStr).getScheduledTransferModelList() != null && !monthLegendMap.get(todayStr).getScheduledTransferModelList().isEmpty())))){
            Log.i(CLASS_NAME, "Trying to get All Scheduled Transactions/Transfers from MonthLegend to display them as notification_scheduled_transaction");
            MonthLegend monthLegendObj = monthLegendMap.get(todayStr);

            schedTransactionModelObjList = monthLegendObj.getScheduledTransactionModelList();
            scheduledTransferModelObjList = monthLegendObj.getScheduledTransferModelList();
        }

        TodaysNotifications todaysNotificationsObj = new TodaysNotifications();
        todaysNotificationsObj.setLoggedInUser(loggedInUserObj);

        //for scheduled transactions starts--
        if(schedTransactionModelObjList != null && !schedTransactionModelObjList.isEmpty()){
            Log.i(CLASS_NAME, "There are no Scheduled Transactions for the date("+todayStr+")");
            //remove those schedules who have been either addded already or rejected by the user.
            Log.i(CLASS_NAME, "Found " + schedTransactionModelObjList.size() + " Scheduled Transactions...but need to filter out those which are already added/cancelled");
            todaysNotificationsObj.setTodaysSchedTransactionsList(calendarDbService.getSchedTransactionsListAfterCancelledNotifsOnDate(schedTransactionModelObjList
                    , loggedInUserObj.getUSER_ID(), todayStr));
            Log.i(CLASS_NAME, "After filtering already added or cancelled , found " + schedTransactionModelObjList.size() + " scheduled transactions");
            Log.i(CLASS_NAME, "Finished building notifications for Scheduled Transactions");
        }
        //for scheduled transactions ends--

        //for scheduled transfers starts--
        if(scheduledTransferModelObjList != null && !scheduledTransferModelObjList.isEmpty()){
            Log.i(CLASS_NAME, "There are no Scheduled Transfers for the date("+todayStr+")");
            //remove those schedules who have been either addded already or rejected by the user.
            Log.i(CLASS_NAME, "Found " + scheduledTransferModelObjList.size() + " Scheduled Transfers...but need to filter out those which are already added/cancelled");
            todaysNotificationsObj.setTodaysSchedTransfersList(calendarDbService.getSchedTransfersListAfterCancelledNotifsOnDate(scheduledTransferModelObjList
                    , loggedInUserObj.getUSER_ID(), todayStr));
            Log.i(CLASS_NAME, "After filtering already added or cancelled , found " + scheduledTransferModelObjList.size() + " scheduled transfers");
            Log.i(CLASS_NAME, "Finished building notifications for Scheduled Transfers");
        }
        //for scheduled transfers ends--

        return todaysNotificationsObj;
    }


    private void buildAllNotifications(TodaysNotifications todaysNotificationsObj) {
        //build Scheduled Transactions starts--
        if(todaysNotificationsObj.getTodaysSchedTransactionsList() != null && !todaysNotificationsObj.getTodaysSchedTransactionsList().isEmpty()){
            Log.i(CLASS_NAME, "There are " + todaysNotificationsObj.getTodaysSchedTransactionsList().size() + " Scheduled Transactions which are scheduled for today");
            for(ScheduledTransactionModel iterSchedTransactionsList : todaysNotificationsObj.getTodaysSchedTransactionsList()){
                Log.i(CLASS_NAME, "Scheduled Transaction ID : "+iterSchedTransactionsList.getSCH_TRAN_ID());

                //if the transaction is auto, auto add it and notify the user
                if("AUTO_ADD".equalsIgnoreCase(iterSchedTransactionsList.getSCH_TRAN_AUTO())){
                    Log.i(CLASS_NAME, "Scheduled Transaction is - "+iterSchedTransactionsList.getSCH_TRAN_AUTO());
                    Log.i(CLASS_NAME, "Automatically adding a transaction starts");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    TransactionModel transactionModelObj = new TransactionModel();
                    transactionModelObj.setUSER_ID(todaysNotificationsObj.getLoggedInUser().getUSER_ID());
                    transactionModelObj.setCAT_ID(iterSchedTransactionsList.getSCH_TRAN_CAT_ID());
                    transactionModelObj.setSPNT_ON_ID(iterSchedTransactionsList.getSCH_TRAN_SPNT_ON_ID());
                    transactionModelObj.setACC_ID(iterSchedTransactionsList.getSCH_TRAN_ACC_ID());
                    transactionModelObj.setSCH_TRAN_ID(iterSchedTransactionsList.getSCH_TRAN_ID());
                    transactionModelObj.setTRAN_AMT(iterSchedTransactionsList.getSCH_TRAN_AMT());
                    transactionModelObj.setTRAN_NAME(iterSchedTransactionsList.getSCH_TRAN_NAME());
                    transactionModelObj.setTRAN_TYPE(iterSchedTransactionsList.getSCH_TRAN_TYPE());
                    transactionModelObj.setTRAN_NOTE(iterSchedTransactionsList.getSCH_TRAN_NOTE());
                    transactionModelObj.setTRAN_DATE(sdf.format(new Date()));

                    if((new AddUpdateTransactionsDbService(getApplicationContext())).addNewTransaction(transactionModelObj) != -1){
                        Log.i(CLASS_NAME, "Building a notification just to notify the user of the automatically added scheduled transaction");
                        buildSchTransactionJustNotification(iterSchedTransactionsList, todaysNotificationsObj.getLoggedInUser());

                        //update in NOTIFICATIONS table saying this transaction has already been added. Avoids duplicate transaction and notification.
                        Log.i(CLASS_NAME, "Saving the transaction in NOTIFICATIONS table to avoid duplicate transaction and notification");
                        NotificationModel notificationModelObj = new NotificationModel();
                        notificationModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());
                        notificationModelObj.setCNCL_NOTIF_TYPE("TRANSACTION");
                        notificationModelObj.setCNCL_NOTIF_EVNT_ID(iterSchedTransactionsList.getSCH_TRAN_ID());
                        notificationModelObj.setCNCL_NOTIF_RSN("AUTO_ADD");

                        if((new NotificationDbService(getApplicationContext()).cancelOrAddNotif(notificationModelObj)) != -1){
                            Log.i(CLASS_NAME, "Notification has been added to NOTIFICATIONS table to avoid duplications");
                        }
                        else{
                            Log.e(CLASS_NAME, "Adding Notification into NOTIFICATIONS table failed");
                            showToast("Automatic Transaction Failed");
                        }
                    }
                    else{
                        Log.e(CLASS_NAME, "Automatically adding a transaction failed");
                        showToast("Automatic Transaction Failed");
                    }
                    Log.i(CLASS_NAME, "Automatically adding a transaction ends");
                }
                else{
                    Log.i(CLASS_NAME, "Scheduled Transaction is - "+iterSchedTransactionsList.getSCH_TRAN_AUTO());
                    buildSchTransactionNotification(iterSchedTransactionsList, todaysNotificationsObj.getLoggedInUser());
                }
            }
        }
        else{
            Log.i(CLASS_NAME, "There are No Scheduled Transactions which are scheduled for today");
        }
        //build Scheduled Transactions ends--

        //build Scheduled Transfers starts--
        if(todaysNotificationsObj.getTodaysSchedTransfersList() != null && !todaysNotificationsObj.getTodaysSchedTransfersList().isEmpty()){
            Log.i(CLASS_NAME, "There are "+todaysNotificationsObj.getTodaysSchedTransfersList().size()+" Scheduled Transfers which are scheduled for today");
            for(ScheduledTransferModel iterSchedTranfersList : todaysNotificationsObj.getTodaysSchedTransfersList()){
                Log.i(CLASS_NAME, "Scheduled Transfer ID : "+iterSchedTranfersList.getSCH_TRNFR_ID());

                //if the transfer is auto, auto add it and notify the user
                if("AUTO_ADD".equalsIgnoreCase(iterSchedTranfersList.getSCH_TRNFR_AUTO())){
                    Log.i(CLASS_NAME, "Scheduled Transfer is - "+iterSchedTranfersList.getSCH_TRNFR_AUTO());
                    Log.i(CLASS_NAME, "Automatically adding a transfer starts");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    TransferModel transferModelObj = new TransferModel();
                    transferModelObj.setUSER_ID(todaysNotificationsObj.getLoggedInUser().getUSER_ID());
                    transferModelObj.setACC_ID_FRM(iterSchedTranfersList.getSCH_TRNFR_ACC_ID_FRM());
                    transferModelObj.setACC_ID_TO(iterSchedTranfersList.getSCH_TRNFR_ACC_ID_TO());
                    transferModelObj.setTRNFR_AMT(iterSchedTranfersList.getSCH_TRNFR_AMT());
                    transferModelObj.setTRNFR_NOTE(iterSchedTranfersList.getSCH_TRNFR_NOTE());
                    transferModelObj.setTRNFR_DATE(sdf.format(new Date()));


                    if((new AddUpdateTransfersDbService(getApplicationContext())).addNewTransfer(transferModelObj) != -1){
                        Log.e(CLASS_NAME, "Building a notification just to notify the user of the automatically added scheduled transfer");
                        buildSchTransferJustNotification(iterSchedTranfersList, todaysNotificationsObj.getLoggedInUser());

                        //update in NOTIFICATIONS table saying this transfer has already been added. Avoids duplicate transfer and notification.
                        Log.i(CLASS_NAME, "Saving the transfer in NOTIFICATIONS table to avoid duplicate transfer and notification");
                        NotificationModel notificationModelObj = new NotificationModel();
                        notificationModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());
                        notificationModelObj.setCNCL_NOTIF_TYPE("TRANSFER");
                        notificationModelObj.setCNCL_NOTIF_EVNT_ID(iterSchedTranfersList.getSCH_TRNFR_ID());
                        notificationModelObj.setCNCL_NOTIF_RSN("AUTO_ADD");

                        if((new NotificationDbService(getApplicationContext()).cancelOrAddNotif(notificationModelObj)) != -1){
                            Log.i(CLASS_NAME, "Notification has been added to NOTIFICATIONS table to avoid duplications");
                        }
                        else{
                            Log.e(CLASS_NAME, "Adding Notification into NOTIFICATIONS table failed");
                            showToast("Automatic Transfer Failed");
                        }
                    }
                    else{
                        Log.e(CLASS_NAME, "Automatically adding a transfer failed");
                        showToast("Automatic Transfer Failed");
                    }
                    Log.i(CLASS_NAME, "Automatically adding a transfer ends");
                }
                else{
                    Log.i(CLASS_NAME, "Scheduled Transfer is - "+iterSchedTranfersList.getSCH_TRNFR_AUTO());
                    buildSchTransferNotification(iterSchedTranfersList, todaysNotificationsObj.getLoggedInUser());
                }
            }
        } else {
            Log.i(CLASS_NAME, "There are No Scheduled Transfers which are scheduled for today");
        }
        //build Scheduled Transfers ends--
    }

    private void buildSchTransferJustNotification(ScheduledTransferModel scheduledTransferModelObj, UsersModel loggedInUserObj) {
        String notificationTitle = this.getResources().getString(R.string.notifTitleSchedules);
        String notificationMessage = this.getResources().getString(R.string.notifMsgSchedulesAdd);
        notificationTitle = notificationTitle.replace("XXXXX", loggedInUserObj.getNAME());
        notificationMessage = notificationMessage.replace("YYYYY", "Transfer");
        notificationMessage = notificationMessage.replace("WWWWW", loggedInUserObj.getCurrencyText()+scheduledTransferModelObj.getSCH_TRNFR_AMT());

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //to handle action on notification when the user clicks on the notification_scheduled_transaction
        Intent intent = new Intent(this, CalendarActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification mNotification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.india)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .setStyle(new Notification.InboxStyle()
                        .setBigContentTitle(scheduledTransferModelObj.getFromAccountStr() + " to " + scheduledTransferModelObj.getToAccountStr())
                        .addLine(loggedInUserObj.getCurrencyText() + scheduledTransferModelObj.getSCH_TRNFR_AMT())
                        .setSummaryText("This Transfer will happen " + scheduledTransferModelObj.getSCH_TRNFR_FREQ()))
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .build();

        //generate unique hash for this particular transfer using its id
        int notifHashCode = IdGenerator.getInstance().getIntegerOnString(scheduledTransferModelObj.getSCH_TRNFR_ID());

        mNotification.setLatestEventInfo(this, notificationTitle, notificationMessage, pIntent);
        notificationManager.notify(notifHashCode, mNotification);
    }

    private void buildSchTransactionJustNotification(ScheduledTransactionModel scheduledTransactionModelObj, UsersModel loggedInUserObj){
        String notificationTitle = this.getResources().getString(R.string.notifTitleSchedules);
        String notificationMessage = this.getResources().getString(R.string.notifMsgSchedulesAdd);
        notificationTitle = notificationTitle.replace("XXXXX", loggedInUserObj.getNAME());
        notificationMessage = notificationMessage.replace("YYYYY", "Transaction");
        notificationMessage = notificationMessage.replace("WWWWW", loggedInUserObj.getCurrencyText()+scheduledTransactionModelObj.getSCH_TRAN_AMT());

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //to handle action on notification when the user clicks on the notification_scheduled_transaction
        Intent intent = new Intent(this, CalendarActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification mNotification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.india)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .setStyle(new Notification.InboxStyle()
                        .setBigContentTitle(scheduledTransactionModelObj.getSCH_TRAN_NAME())
                        .addLine(loggedInUserObj.getCurrencyText() + scheduledTransactionModelObj.getSCH_TRAN_AMT() + " (" + scheduledTransactionModelObj.getSCH_TRAN_TYPE() + ")")
                        .setSummaryText("This Transaction will happen " + scheduledTransactionModelObj.getSCH_TRAN_FREQ()))
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .build();

        //generate unique hash for this particular transaction using its id
        int notifHashCode = IdGenerator.getInstance().getIntegerOnString(scheduledTransactionModelObj.getSCH_TRAN_ID());

        mNotification.setLatestEventInfo(this, notificationTitle, notificationMessage, pIntent);
        notificationManager.notify(notifHashCode, mNotification);
    }

    //this method returns the delay between current time and the time user has opted to get notifications. If the current time is more than the users, then the delay is
    //proposed to be 0
    private long getTimeDelayForNotificationOnTime(String timeStr){
        String timeStrArr[] = timeStr.split(":");
        Calendar calendar = Calendar.getInstance();
        long currentTimestamp = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeStrArr[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeStrArr[1]));
        calendar.set(Calendar.SECOND, 0);
        long diffTimestamp = calendar.getTimeInMillis() - currentTimestamp;
        return (diffTimestamp < 0 ? 0 : diffTimestamp);
    }



    private UsersModel getUser(){
        AuthorizationDbService authorizationDbService = new AuthorizationDbService(getApplicationContext());
        Map<Integer, UsersModel> userMap = authorizationDbService.getActiveUser();

        if(userMap != null && userMap.size() == 1){
            return userMap.get(0);
        }

        Log.e(CLASS_NAME, "I'm not supposed to be read/print/shown..... This should have been a dead code. If you can read me, Authorization of user has failed and you should " +
                "probably die twice by now.");
        return null;
    }

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(CLASS_NAME, "Notifications Service has been stopped..");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


}
