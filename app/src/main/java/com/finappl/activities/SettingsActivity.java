package com.finappl.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.adapters.SettingsProfilePersonalCountryAdapter;
import com.finappl.adapters.SettingsProfilePersonalCurrencyAdapter;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.SettingsDbService;
import com.finappl.models.CountryModel;
import com.finappl.models.CurrencyModel;
import com.finappl.models.SettingsNotificationModel;
import com.finappl.models.SpinnerModel;
import com.finappl.models.UserMO;
import com.finappl.models.UsersModel;
import com.finappl.utils.Constants;
import com.finappl.utils.EncryptionUtil;
import com.finappl.utils.FinappleUtility;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
public class SettingsActivity extends LockerActivity {
    private final String CLASS_NAME = this.getClass().getName();

    private Context mContext = this;

    //dialogs
    private Dialog dialog;

    //db service
    private SettingsDbService settingsDbService = new SettingsDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UserMO loggedInUserObj;

    private EditText lockPoprPinET, lockPoprNewPinET, lockPoprRPinET;
    private LinearLayout lockPoprNewPinLL;

    private String oldPinStr="", newPinStr="", rPinStr="";

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //get the Active user
        loggedInUserObj = FinappleUtility.getInstance().getUser(mContext);
        if(loggedInUserObj == null){
            return;
        }

        setUpNotifications();

        setUpSounds();

        setUpSecurity();

        setFont((ViewGroup) this.findViewById(R.id.settingsParentRLId));
    }

    public void toManageContent(View view){
        navigateTo(ManageContentActivity.class, null, null);
    }

    public void onHomeClick(View view){
        navigateTo(CalendarActivity.class, null, null);
    }

    public void onBudget(View view){
        navigateTo(BudgetsViewActivity.class, null, null);
    }

    public void showPopper(View view){
        if(view.getTag() == null){
            Log.e(CLASS_NAME, "Bless my Lord !!! Null found in the view you clicked !!! Expecting a tagName");
            return;
        }

        String tagStr = String.valueOf(view.getTag());

        if("PROFILE_PERSONAL".equalsIgnoreCase(tagStr)){
            showProfilePersonalPopper();
        }
    }

    public void showProfilePersonalPopper() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.settings_profile_personal_popper);

        dialog.show();

        EditText settingsProfilePersonalPoprNameET;
        TextView settingsProfilePersonalPoprDobTV, settingsProfilePersonalPoprLastUpdTV;
        settingsProfilePersonalPoprNameET = (EditText) dialog.findViewById(R.id.settingsProfilePersonalPoprNameETId);
        settingsProfilePersonalPoprDobTV = (TextView) dialog.findViewById(R.id.settingsProfilePersonalPoprDobTVId);
        settingsProfilePersonalPoprLastUpdTV = (TextView) dialog.findViewById(R.id.settingsProfilePersonalPoprLastUpdTVId);

        Spinner settingsProfilePersonalPoprCntrySpn, settingsProfilePersonalPoprCurSpn;
        settingsProfilePersonalPoprCntrySpn = (Spinner) dialog.findViewById(R.id.settingsProfilePersonalPoprCntrySpnId);
        settingsProfilePersonalPoprCurSpn = (Spinner) dialog.findViewById(R.id.settingsProfilePersonalPoprCurSpnId);

        //get currency and country lists
        List<CountryModel> countryList = settingsDbService.getAllCountry();
        List<CurrencyModel> currencyList = settingsDbService.getAllCurrency();

        List<SpinnerModel> countrySpnList = new ArrayList<SpinnerModel>();
        List<SpinnerModel> currencySpnList = new ArrayList<SpinnerModel>();

        //popullate countrySpnList and currencySpnList
        SpinnerModel spinnerModelObj = null;

        for(CountryModel iterCountryList : countryList){
            spinnerModelObj = new SpinnerModel();
            spinnerModelObj.setItemId(iterCountryList.getCNTRY_ID());
            spinnerModelObj.setItemName(iterCountryList.getCNTRY_NAME());
            countrySpnList.add(spinnerModelObj);
        }
        settingsProfilePersonalPoprCntrySpn.setAdapter(new SettingsProfilePersonalCountryAdapter(mContext, R.layout.settings_profile_personal_country_spinner_item,
                    countryList));

        for(CurrencyModel iterCurrencyList : currencyList){
            spinnerModelObj = new SpinnerModel();
            spinnerModelObj.setItemId(iterCurrencyList.getCUR_ID());
            spinnerModelObj.setItemName(iterCurrencyList.getCUR_NAME());
            currencySpnList.add(spinnerModelObj);
        }

        settingsProfilePersonalPoprCurSpn.setAdapter(new SettingsProfilePersonalCurrencyAdapter(mContext, R.layout.settings_profile_personal_currency_spinner_item,
                    currencyList));


        //get user data from db and set it in the popper
        UsersModel usersModelObj = new UsersModel();
        usersModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());
        usersModelObj = settingsDbService.getUserProfile(usersModelObj);

        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM ''yy");
        settingsProfilePersonalPoprNameET.setText(usersModelObj.getNAME());
        settingsProfilePersonalPoprDobTV.setText(sdf.format(usersModelObj.getDOB()));

        if(usersModelObj.getUserModDtm() != null){
            settingsProfilePersonalPoprLastUpdTV.setText(sdf.format(usersModelObj.getUserModDtm()));
        }
        else{
            settingsProfilePersonalPoprLastUpdTV.setText(sdf.format(usersModelObj.getUserCreatDtm()));
        }

        //setting spinners pre selection
        settingsProfilePersonalPoprCntrySpn.setSelection(getSpinnerItemIndex(countrySpnList, usersModelObj.getCountryName()));
        settingsProfilePersonalPoprCurSpn.setSelection(getSpinnerItemIndex(currencySpnList, usersModelObj.getCurrencyName()));

        LinearLayout settingsProfilePersonalPoprSaveLL = (LinearLayout) dialog.findViewById(R.id.settingsProfilePersonalPoprSaveLLId);
        settingsProfilePersonalPoprSaveLL.setOnClickListener(linearLayoutClickListener);

        //show date picker on dob click
        settingsProfilePersonalPoprDobTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        //set font for all the text view
        setFont((ViewGroup)dialog.findViewById(R.id.settingsProfilePersonalPoprLLId));
    }

    public void logoutUser(View view){
        // Create custom message popper object
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_popper);

        dialog.show();

        //buttons
        LinearLayout msgPoprPosLL, msgPoprNegLL;
        msgPoprPosLL = (LinearLayout) dialog.findViewById(R.id.msgPoprPosLLId);
        msgPoprNegLL = (LinearLayout) dialog.findViewById(R.id.msgPoprNegLLId);

        //set listeners for the buttons
        msgPoprPosLL.setOnClickListener(logoutPoppeButtonClickListener);
        msgPoprNegLL.setOnClickListener(logoutPoppeButtonClickListener);

        //texts
        TextView msgPoprMsgTV;
        msgPoprMsgTV = (TextView) dialog.findViewById(R.id.msgPoprMsgTVId);

        msgPoprMsgTV.setText("Logout "+mContext.getResources().getString(R.string.app_name_main)+" ?");

        //set font for all the text view
        setFont((ViewGroup) dialog.findViewById(R.id.msgPoprLLId));
    }

    private Integer getSpinnerItemIndex(List<SpinnerModel> spnList, String itemStr){
        int spnListSize = spnList.size();
        int index = 0;

        for(int i=0; i<spnListSize; i++){
            if(spnList.get(i).getItemName().equalsIgnoreCase(itemStr)){
                index = i;
                break;
            }
        }
        return index;
    }

    public void showDatePicker(View view){
        Log.i(CLASS_NAME, "Working very hard to call date picker to work");
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {

            TextView settingsProfilePersonalPoprDobTV = (TextView) dialog.findViewById(R.id.settingsProfilePersonalPoprDobTVId);
            String selectedDobStr = String.valueOf(settingsProfilePersonalPoprDobTV.getText());

            SimpleDateFormat wrongSdf = new SimpleDateFormat("d MMMM ''yy");
            SimpleDateFormat rightSdf = new SimpleDateFormat("dd-MM-yyyy");

            String selectedDobStrArr[] = null;
            try{
                selectedDobStrArr = rightSdf.format(wrongSdf.parse(selectedDobStr)).split("-");
            }
            catch(ParseException pe){
                Log.e(CLASS_NAME, "Date Parse Exception in onCreateDialog method:"+pe);
            }

            return new DatePickerDialog(this, myDateListener, Integer.parseInt(selectedDobStrArr[2]), Integer.parseInt(selectedDobStrArr[1])-1,
                        Integer.parseInt(selectedDobStrArr[0]));
        }
        return null;
    }

    public void showNotificationsPopper(View view){
        // Create custom message popper object
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.settings_notifications_popper);

        dialog.show();

        //get user preferences
        SettingsNotificationModel settingsNotificationModelObj = settingsDbService.getNotifSettingsOnUserId(loggedInUserObj.getUSER_ID());

        //set time picker
        TimePicker settingsNotifsPopperTimeTP = (TimePicker) dialog.findViewById(R.id.settingsNotifsPopperTimeTPId);
        settingsNotifsPopperTimeTP.setIs24HourView(false);

        String timeStrArr[] = settingsNotificationModelObj.getSET_NOTIF_TIME().split(":");
        settingsNotifsPopperTimeTP.setCurrentHour(Integer.parseInt(timeStrArr[0]));
        settingsNotifsPopperTimeTP.setCurrentMinute(Integer.parseInt(timeStrArr[1]));

        //set vibrate
        LinearLayout settingsNotifsPopperVibeLL = (LinearLayout) dialog.findViewById(R.id.settingsNotifsPopperVibeLLId);
        LinearLayout settingsNotifsPopperBuzzTickLL = (LinearLayout) dialog.findViewById(R.id.settingsNotifsPopperBuzzTickLLId);
        ImageView settingsNotifsPopperBuzzTickIV = (ImageView) dialog.findViewById(R.id.settingsNotifsPopperBuzzTickIVId);

        if(Constants.DB_AFFIRMATIVE.equalsIgnoreCase(settingsNotificationModelObj.getSET_NOTIF_BUZZ())){
            settingsNotifsPopperBuzzTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_checked);
            settingsNotifsPopperBuzzTickIV.setBackgroundResource(R.drawable.tick_white);
            settingsNotifsPopperBuzzTickLL.setTag("ENABLED");
        }
        else {
            settingsNotifsPopperBuzzTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_unchecked);
            settingsNotifsPopperBuzzTickIV.setBackgroundResource(R.drawable.tick_grey);
            settingsNotifsPopperBuzzTickLL.setTag("DISABLED");
        }

        TextView settingsNotifsPopperSaveTV = (TextView) dialog.findViewById(R.id.settingsNotifsPopperSaveTVId);

        settingsNotifsPopperVibeLL.setOnClickListener(notifPoppeButtonClickListener);
        settingsNotifsPopperSaveTV.setOnClickListener(notifPoppeButtonClickListener);

        //set font for all the text view
        setFont((ViewGroup) dialog.findViewById(R.id.settingsNotifsPopperLLId));
    }

    public void setUpNotifications(){
        LinearLayout settingsNotifsTickLL = (LinearLayout) this.findViewById(R.id.settingsNotifsTickLLId);
        ImageView settingsNotifsTickIV = (ImageView) this.findViewById(R.id.settingsNotifsTickIVId);

        if(settingsDbService.isNotifEnabledOnUserId(loggedInUserObj.getUSER_ID())){
            settingsNotifsTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_checked);
            settingsNotifsTickLL.setTag("ENABLED");
            settingsNotifsTickIV.setBackgroundResource(R.drawable.tick_white);
        }
        else{
            settingsNotifsTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_unchecked);
            settingsNotifsTickLL.setTag("DISABLED");
            settingsNotifsTickIV.setBackgroundResource(R.drawable.tick_grey);
        }
    }

    public void setUpSounds(){
        LinearLayout settingsSoundsTickLL = (LinearLayout) this.findViewById(R.id.settingsSoundsTickLLId);
        ImageView settingsSoundsTickIV = (ImageView) this.findViewById(R.id.settingsSoundsTickIVId);

        if(settingsDbService.isSoundsEnabledOnUserId(loggedInUserObj.getUSER_ID())){
            settingsSoundsTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_checked);
            settingsSoundsTickLL.setTag("ENABLED");
            settingsSoundsTickIV.setBackgroundResource(R.drawable.tick_white);
        }
        else{
            settingsSoundsTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_unchecked);
            settingsSoundsTickLL.setTag("DISABLED");
            settingsSoundsTickIV.setBackgroundResource(R.drawable.tick_grey);
        }
    }

    public void setUpSecurity(){
        LinearLayout settingsSecurityTickLL = (LinearLayout) this.findViewById(R.id.settingsSecurityTickLLId);
        ImageView settingsSecurityTickIV = (ImageView) this.findViewById(R.id.settingsSecurityTickIVId);

        if(settingsDbService.isSecurityEnabledOnUserId(loggedInUserObj.getUSER_ID())){
            settingsSecurityTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_checked);
            settingsSecurityTickLL.setTag("ENABLED");
            settingsSecurityTickIV.setBackgroundResource(R.drawable.tick_white);
        }
        else{
            settingsSecurityTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_unchecked);
            settingsSecurityTickLL.setTag("DISABLED");
            settingsSecurityTickIV.setBackgroundResource(R.drawable.tick_grey);
        }
    }

    public void enableDisableSounds(View view){
        LinearLayout settingsSoundsTickLL = (LinearLayout) this.findViewById(R.id.settingsSoundsTickLLId);
        ImageView settingsSoundsTickIV = (ImageView) this.findViewById(R.id.settingsSoundsTickIVId);

        if("ENABLED".equalsIgnoreCase(String.valueOf(settingsSoundsTickLL.getTag()))){
            showToast("Sounds disabled");
            settingsSoundsTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_unchecked);
            settingsSoundsTickLL.setTag("DISABLED");
            settingsSoundsTickIV.setBackgroundResource(R.drawable.tick_grey);
            settingsDbService.enableDisableSoundsOnUserId(loggedInUserObj.getUSER_ID(), false);
        }
        else{
            showToast("Sounds enabled");
            settingsSoundsTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_checked);
            settingsSoundsTickLL.setTag("ENABLED");
            settingsSoundsTickIV.setBackgroundResource(R.drawable.tick_white);
            settingsDbService.enableDisableSoundsOnUserId(loggedInUserObj.getUSER_ID(), true);
        }
    }

    private void killPoppers(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

    public void enableDisableSecPopper(View view){
        // Create custom message popper object
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lock_popper);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        oldPinStr = "";
        newPinStr = "";
        rPinStr = "";

        TextView lockPoprOneTV, lockPoprTwoTV, lockPoprThreeTV, lockPoprFourTV, lockPoprFiveTV, lockPoprSixTV, lockPoprSevenTV, lockPoprEightTV, lockPoprNineTV, lockPoprZeroTV;
        LinearLayout lockPoprDeleteLL, lockPoprSubmitLL;

        lockPoprPinET = (EditText) dialog.findViewById(R.id.lockPoprPinETId);
        lockPoprNewPinET = (EditText) dialog.findViewById(R.id.lockPoprNewPinETId);
        lockPoprRPinET = (EditText) dialog.findViewById(R.id.lockPoprRPinETId);

        lockPoprOneTV = (TextView) dialog.findViewById(R.id.lockPoprOneTVId);
        lockPoprTwoTV = (TextView) dialog.findViewById(R.id.lockPoprTwoTVId);
        lockPoprThreeTV = (TextView) dialog.findViewById(R.id.lockPoprThreeTVId);
        lockPoprFourTV = (TextView) dialog.findViewById(R.id.lockPoprfourTVId);
        lockPoprFiveTV = (TextView) dialog.findViewById(R.id.lockPoprFiveTVId);
        lockPoprSixTV = (TextView) dialog.findViewById(R.id.lockPoprSixTVId);
        lockPoprSevenTV = (TextView) dialog.findViewById(R.id.lockPoprSevenTVId);
        lockPoprEightTV = (TextView) dialog.findViewById(R.id.lockPoprEightTVId);
        lockPoprNineTV = (TextView) dialog.findViewById(R.id.lockPoprNineTVId);
        lockPoprZeroTV = (TextView) dialog.findViewById(R.id.lockPoprZeroTVId);

        lockPoprNewPinLL = (LinearLayout) dialog.findViewById(R.id.lockPoprNewPinLLId);
        lockPoprDeleteLL = (LinearLayout) dialog.findViewById(R.id.lockPoprDeleteLLId);
        lockPoprSubmitLL = (LinearLayout) dialog.findViewById(R.id.lockPoprSubmitLLId);

        lockPoprOneTV.setOnClickListener(textViewClickListener);
        lockPoprTwoTV.setOnClickListener(textViewClickListener);
        lockPoprThreeTV.setOnClickListener(textViewClickListener);
        lockPoprFourTV.setOnClickListener(textViewClickListener);
        lockPoprFiveTV.setOnClickListener(textViewClickListener);
        lockPoprSixTV.setOnClickListener(textViewClickListener);
        lockPoprSevenTV.setOnClickListener(textViewClickListener);
        lockPoprEightTV.setOnClickListener(textViewClickListener);
        lockPoprNineTV.setOnClickListener(textViewClickListener);
        lockPoprZeroTV.setOnClickListener(textViewClickListener);

        lockPoprDeleteLL.setOnClickListener(linearLayoutClickListenerForTick);
        lockPoprSubmitLL.setOnClickListener(linearLayoutClickListenerForTick);

        if(loggedInUserObj.getSET_SEC_PIN() == null || (loggedInUserObj.getSET_SEC_PIN() != null && loggedInUserObj.getSET_SEC_PIN().isEmpty())){
            lockPoprPinET.setVisibility(View.GONE);
            lockPoprNewPinLL.setVisibility(View.VISIBLE);
        }
        else{
            lockPoprPinET.setVisibility(View.VISIBLE);
            lockPoprNewPinLL.setVisibility(View.GONE);
        }

        dialog.show();

        setFont((ViewGroup) dialog.findViewById(R.id.lockPoprLLId));
    }

    public void changePinPopper(View view){
        // Create custom message popper object
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lock_popper);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        oldPinStr = "";
        newPinStr = "";
        rPinStr = "";

        TextView lockPoprOneTV, lockPoprTwoTV, lockPoprThreeTV, lockPoprFourTV, lockPoprFiveTV, lockPoprSixTV, lockPoprSevenTV, lockPoprEightTV, lockPoprNineTV, lockPoprZeroTV;
        LinearLayout lockPoprDeleteLL, lockPoprSubmitLL;

        lockPoprPinET = (EditText) dialog.findViewById(R.id.lockPoprPinETId);
        lockPoprNewPinET = (EditText) dialog.findViewById(R.id.lockPoprNewPinETId);
        lockPoprRPinET = (EditText) dialog.findViewById(R.id.lockPoprRPinETId);

        lockPoprOneTV = (TextView) dialog.findViewById(R.id.lockPoprOneTVId);
        lockPoprTwoTV = (TextView) dialog.findViewById(R.id.lockPoprTwoTVId);
        lockPoprThreeTV = (TextView) dialog.findViewById(R.id.lockPoprThreeTVId);
        lockPoprFourTV = (TextView) dialog.findViewById(R.id.lockPoprfourTVId);
        lockPoprFiveTV = (TextView) dialog.findViewById(R.id.lockPoprFiveTVId);
        lockPoprSixTV = (TextView) dialog.findViewById(R.id.lockPoprSixTVId);
        lockPoprSevenTV = (TextView) dialog.findViewById(R.id.lockPoprSevenTVId);
        lockPoprEightTV = (TextView) dialog.findViewById(R.id.lockPoprEightTVId);
        lockPoprNineTV = (TextView) dialog.findViewById(R.id.lockPoprNineTVId);
        lockPoprZeroTV = (TextView) dialog.findViewById(R.id.lockPoprZeroTVId);

        lockPoprNewPinLL = (LinearLayout) dialog.findViewById(R.id.lockPoprNewPinLLId);
        lockPoprDeleteLL = (LinearLayout) dialog.findViewById(R.id.lockPoprDeleteLLId);
        lockPoprSubmitLL = (LinearLayout) dialog.findViewById(R.id.lockPoprSubmitLLId);

        lockPoprOneTV.setOnClickListener(textViewClickListener);
        lockPoprTwoTV.setOnClickListener(textViewClickListener);
        lockPoprThreeTV.setOnClickListener(textViewClickListener);
        lockPoprFourTV.setOnClickListener(textViewClickListener);
        lockPoprFiveTV.setOnClickListener(textViewClickListener);
        lockPoprSixTV.setOnClickListener(textViewClickListener);
        lockPoprSevenTV.setOnClickListener(textViewClickListener);
        lockPoprEightTV.setOnClickListener(textViewClickListener);
        lockPoprNineTV.setOnClickListener(textViewClickListener);
        lockPoprZeroTV.setOnClickListener(textViewClickListener);

        lockPoprDeleteLL.setOnClickListener(linearLayoutClickListenerForChangePin);
        lockPoprSubmitLL.setOnClickListener(linearLayoutClickListenerForChangePin);

        lockPoprNewPinLL.setVisibility(View.VISIBLE);

        dialog.show();

        setFont((ViewGroup) dialog.findViewById(R.id.lockPoprLLId));
    }

    public void enableDisableWidget(View view){
        showToast("Widget is not yet implemented");
    }

    public void enableDisableNotifications(View view){
        LinearLayout settingsNotifsTickLL = (LinearLayout) this.findViewById(R.id.settingsNotifsTickLLId);
        ImageView settingsNotifsTickIV = (ImageView) this.findViewById(R.id.settingsNotifsTickIVId);

        if("ENABLED".equalsIgnoreCase(String.valueOf(settingsNotifsTickLL.getTag()))){
            showToast("Notifications disabled");
            settingsNotifsTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_unchecked);
            settingsNotifsTickLL.setTag("DISABLED");
            settingsNotifsTickIV.setBackgroundResource(R.drawable.tick_grey);
            settingsDbService.enableDisableNotificationOnUserId(loggedInUserObj.getUSER_ID(), false);
        }
        else{
            showToast("Notifications enabled");
            settingsNotifsTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_checked);
            settingsNotifsTickLL.setTag("ENABLED");
            settingsNotifsTickIV.setBackgroundResource(R.drawable.tick_white);
            settingsDbService.enableDisableNotificationOnUserId(loggedInUserObj.getUSER_ID(), true);
        }
    }

    private DatePickerDialog.OnDateSetListener myDateListener;
    {
        myDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                Log.i(CLASS_NAME, "HA HA HA..and you thought date picker wont work for you !!! You pay it. it works for u. Simple.");
                Log.i(CLASS_NAME, "Date picker says that u selected:"+ day + "-" + month + "-" + year);

                //change jan-0 to jan-1
                month++;

                Log.i(CLASS_NAME, "Date picker date translated to be:" + day + "-" + month + "-" + year);

                TextView settingsProfilePersonalPoprDobTV = (TextView) dialog.findViewById(R.id.settingsProfilePersonalPoprDobTVId);
                String newDobStr = day+"-"+month+"-"+year;

                SimpleDateFormat wrongSdf = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat rightSdf = new SimpleDateFormat("d MMMM ''yy");

                try{
                    settingsProfilePersonalPoprDobTV.setText(rightSdf.format(wrongSdf.parse(newDobStr)));
                }
                catch(ParseException pe){
                    Log.e(CLASS_NAME, "Date Parse Exception in myDateListener:"+pe);
                }
            }
        };
    }
    //---------------------------------------Date Picker ends--------------------------------------------

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    //--------------------------------------logout popper button listener----------------------------------------
    private LinearLayout.OnClickListener logoutPoppeButtonClickListener;
    {
        logoutPoppeButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                switch(v.getId()){
                    case R.id.msgPoprPosLLId: settingsDbService.logoutAllUsers();
                        showToast("Logged Out");
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        };
    }
    //---------------------------------------logout popper button listener ends------------------------------------

    //--------------------------------------notif popper button listener----------------------------------------
    private LinearLayout.OnClickListener notifPoppeButtonClickListener;
    {
        notifPoppeButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout settingsNotifsPopperBuzzTickLL = (LinearLayout) dialog.findViewById(R.id.settingsNotifsPopperBuzzTickLLId);
                ImageView settingsNotifsPopperBuzzTickIV = (ImageView) dialog.findViewById(R.id.settingsNotifsPopperBuzzTickIVId);

                switch (v.getId()) {
                    case R.id.settingsNotifsPopperVibeLLId:
                        if ("ENABLED".equalsIgnoreCase(String.valueOf(settingsNotifsPopperBuzzTickLL.getTag()))) {
                            settingsNotifsPopperBuzzTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_unchecked);
                            settingsNotifsPopperBuzzTickIV.setBackgroundResource(R.drawable.tick_grey);
                            settingsNotifsPopperBuzzTickLL.setTag("DISABLED");
                            //settingsDbService.enableDisableNotificationVibrationsOnUserId(loggedInUserObj.getUSER_ID(), false);
                        } else {
                            settingsNotifsPopperBuzzTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_checked);
                            settingsNotifsPopperBuzzTickIV.setBackgroundResource(R.drawable.tick_white);
                            settingsNotifsPopperBuzzTickLL.setTag("ENABLED");
                            //settingsDbService.enableDisableNotificationVibrationsOnUserId(loggedInUserObj.getUSER_ID(), true);
                        }
                        break;
                    case R.id.settingsNotifsPopperSaveTVId:
                        showToast("Saved");
                        dialog.dismiss();
                        SettingsNotificationModel settingsNotificationModelObj = new SettingsNotificationModel();
                        TimePicker settingsNotifsPopperTimeTP = (TimePicker) dialog.findViewById(R.id.settingsNotifsPopperTimeTPId);
                        settingsNotificationModelObj.setSET_NOTIF_TIME(settingsNotifsPopperTimeTP.getCurrentHour() + ":" + settingsNotifsPopperTimeTP.getCurrentMinute());
                        if ("ENABLED".equalsIgnoreCase(String.valueOf(settingsNotifsPopperBuzzTickLL.getTag()))) {
                            settingsNotificationModelObj.setSET_NOTIF_BUZZ(Constants.DB_AFFIRMATIVE);
                        } else {
                            settingsNotificationModelObj.setSET_NOTIF_BUZZ(Constants.DB_NONAFFIRMATIVE);
                        }

                        //add user id
                        settingsNotificationModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());

                        settingsDbService.saveNotificationSetting(settingsNotificationModelObj);
                        break;
                }
            }
        };
    }
    //---------------------------------------notif popper button listener ends------------------------------------

    //--------------------------------Linear Layout click listener--------------------------------------------------
    private LinearLayout.OnClickListener linearLayoutClickListener;
    {
        linearLayoutClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dialogue UI items
                TextView settingsProfilePersonalPoprDobTV, settingsProfilePersonalPoprLastUpdTV;
                EditText settingsProfilePersonalPoprNameET;
                settingsProfilePersonalPoprNameET = (EditText) dialog.findViewById(R.id.settingsProfilePersonalPoprNameETId);
                settingsProfilePersonalPoprDobTV = (TextView) dialog.findViewById(R.id.settingsProfilePersonalPoprDobTVId);
                settingsProfilePersonalPoprLastUpdTV = (TextView) dialog.findViewById(R.id.settingsProfilePersonalPoprLastUpdTVId);

                Spinner settingsProfilePersonalPoprCntrySpn, settingsProfilePersonalPoprCurSpn;
                settingsProfilePersonalPoprCntrySpn = (Spinner) dialog.findViewById(R.id.settingsProfilePersonalPoprCntrySpnId);
                settingsProfilePersonalPoprCurSpn = (Spinner) dialog.findViewById(R.id.settingsProfilePersonalPoprCurSpnId);

                String nameStr = String.valueOf(settingsProfilePersonalPoprNameET.getText());
                String dobStr = String.valueOf(settingsProfilePersonalPoprDobTV.getText());
                String countryIdStr = String.valueOf(settingsProfilePersonalPoprCntrySpn.getSelectedView().getTag());
                String currencyIdStr = String.valueOf(settingsProfilePersonalPoprCurSpn.getSelectedView().getTag());

                if(nameStr.trim().isEmpty()){
                    showToast("Enter your Name");
                    return;
                }

                SimpleDateFormat badSdf = new SimpleDateFormat("d MMMM ''yy");
                SimpleDateFormat goodSdf = new SimpleDateFormat("dd-MM-yyyy");
                Date dob = null;

                try{
                    dob = goodSdf.parse(goodSdf.format(badSdf.parse(dobStr)));
                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "Error in date parsing !!"+e);
                    return;
                }

                UsersModel userModelObj = new UsersModel();
                userModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());
                userModelObj.setNAME(nameStr);
                userModelObj.setDOB(dob);
                userModelObj.setCNTRY_ID(countryIdStr);
                userModelObj.setCUR_ID(currencyIdStr);

                int result = settingsDbService.savePersonalProfile(userModelObj);

                if(result == 1){
                    showToast("Saved");
                    settingsProfilePersonalPoprLastUpdTV.setText("Just Now");
                    Log.i(CLASS_NAME, "Updated the personal profile");
                }
                else{
                    showToast("Oops ! Jim broke something !");
                    Log.e(CLASS_NAME, "Personal Profile save failed due to db update failure");
                    return;
                }
            }
        };
    }
    //--------------------------------Linear Layout ends--------------------------------------------------

    private TextView.OnClickListener textViewClickListener;
    {
        textViewClickListener = new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(lockPoprPinET.isFocused()){
                    oldPinStr += String.valueOf(v.getTag());
                    lockPoprPinET.setText(lockPoprPinET.getText()+"*");
                }
                else if(lockPoprNewPinET.isFocused()){
                    newPinStr += String.valueOf(v.getTag());
                    lockPoprNewPinET.setText(lockPoprNewPinET.getText()+"*");
                }
                else if(lockPoprRPinET.isFocused()){
                    rPinStr += String.valueOf(v.getTag());
                    lockPoprRPinET.setText(lockPoprRPinET.getText()+"*");
                }
            }
        };
    }

    private LinearLayout.OnClickListener linearLayoutClickListenerForChangePin;
    {
        linearLayoutClickListenerForChangePin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(v.getId()) {
                    case R.id.lockPoprDeleteLLId:
                        if (lockPoprPinET.isFocused()) {
                            String inputStr = String.valueOf(lockPoprPinET.getText());

                            if(!inputStr.isEmpty()) {
                                inputStr = inputStr.substring(0, inputStr.length()-1);
                                lockPoprPinET.setText(inputStr);

                                oldPinStr = oldPinStr.substring(0, oldPinStr.length()-1);
                            }
                        }
                        else if (lockPoprNewPinET.isFocused()) {
                            String inputStr = String.valueOf(lockPoprNewPinET.getText());

                            if(!inputStr.isEmpty()) {
                                inputStr = inputStr.substring(0, inputStr.length()-1);
                                lockPoprNewPinET.setText(inputStr);

                                newPinStr = newPinStr.substring(0, newPinStr.length()-1);
                            }
                        }
                        else if (lockPoprRPinET.isFocused()) {
                            String inputStr = String.valueOf(lockPoprRPinET.getText());

                            if(!inputStr.isEmpty()) {
                                inputStr = inputStr.substring(0, inputStr.length()-1);
                                lockPoprRPinET.setText(inputStr);

                                rPinStr = rPinStr.substring(0, rPinStr.length()-1);
                            }
                        }
                        break;

                    case R.id.lockPoprSubmitLLId:
                        //When User wants to change the existing PIN
                        if(View.VISIBLE == lockPoprNewPinLL.getVisibility() && View.VISIBLE == lockPoprPinET.getVisibility()) {
                            try {
                                if (oldPinStr.isEmpty()) {
                                    showToast("Enter Current PIN");
                                } else if (newPinStr.isEmpty()) {
                                    showToast("New PIN cannot be empty");
                                } else if (rPinStr.isEmpty()) {
                                    showToast("Repeat PIN cannot be empty");
                                } else if (!newPinStr.equals(rPinStr)) {
                                    showToast("New PIN & Repeat PIN should match");
                                } else if (!EncryptionUtil.encrypt(oldPinStr).equals(loggedInUserObj.getSET_SEC_PIN())) {
                                    showToast("Incorrect PIN");

                                    lockPoprPinET.setText("");
                                    oldPinStr = "";

                                } else {
                                    settingsDbService.saveSecurityKeyUserId(loggedInUserObj.getUSER_ID(), EncryptionUtil.encrypt(newPinStr));
                                    killPoppers();
                                    setUpSecurity();

                                    //update Sec PIN in loggedInUserObj
                                    loggedInUserObj = FinappleUtility.getInstance().getUser(mContext);

                                    showToast("New PIN Saved");
                                }
                            } catch (Exception e) {
                                Log.e(CLASS_NAME, "Error !! While Encryption ");
                            }
                        }
                        //When User wants to save the PIN for the first time
                        else if(View.VISIBLE == lockPoprNewPinLL.getVisibility() && View.GONE == lockPoprPinET.getVisibility()) {
                            try {
                                if (newPinStr.isEmpty()) {
                                    showToast("New PIN cannot be empty");
                                }
                                else if (rPinStr.isEmpty()) {
                                    showToast("Repeat PIN cannot be empty");
                                }
                                else if (!newPinStr.equals(rPinStr)) {
                                    showToast("New PIN & Repeat PIN should match");
                                }
                                else {
                                    settingsDbService.saveSecurityKeyUserId(loggedInUserObj.getUSER_ID(), EncryptionUtil.encrypt(newPinStr));
                                    killPoppers();
                                    setUpSecurity();

                                    //update Sec PIN in loggedInUserObj
                                    loggedInUserObj = FinappleUtility.getInstance().getUser(mContext);

                                    showToast("PIN Saved");
                                }
                            } catch (Exception e) {
                                Log.e(CLASS_NAME, "Error !! While Encryption ");
                            }
                        }
                        break;

                    default: showToast("Error !!");
                }
            }
        };
    }

    private LinearLayout.OnClickListener linearLayoutClickListenerForTick;
    {
        linearLayoutClickListenerForTick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(v.getId()) {
                    case R.id.lockPoprDeleteLLId:
                        if (lockPoprPinET.isFocused()) {
                            String inputStr = String.valueOf(lockPoprPinET.getText());

                            if(!inputStr.isEmpty()) {
                                inputStr = inputStr.substring(0, inputStr.length()-1);
                                lockPoprPinET.setText(inputStr);

                                oldPinStr = oldPinStr.substring(0, oldPinStr.length()-1);
                            }
                        }
                        else if (lockPoprNewPinET.isFocused()) {
                            String inputStr = String.valueOf(lockPoprNewPinET.getText());

                            if(!inputStr.isEmpty()) {
                                inputStr = inputStr.substring(0, inputStr.length()-1);
                                lockPoprNewPinET.setText(inputStr);

                                newPinStr = newPinStr.substring(0, newPinStr.length()-1);
                            }
                        }
                        else if (lockPoprRPinET.isFocused()) {
                            String inputStr = String.valueOf(lockPoprRPinET.getText());

                            if(!inputStr.isEmpty()) {
                                inputStr = inputStr.substring(0, inputStr.length()-1);
                                lockPoprRPinET.setText(inputStr);

                                rPinStr = rPinStr.substring(0, rPinStr.length()-1);
                            }
                        }
                        break;

                    case R.id.lockPoprSubmitLLId:
                        //When User wants to save the PIN for the first time & Enable/Disable security
                         if(View.VISIBLE == lockPoprNewPinLL.getVisibility() && View.GONE == lockPoprPinET.getVisibility()) {
                            try {
                                if (newPinStr.isEmpty()) {
                                    showToast("New PIN cannot be empty");
                                }
                                else if (rPinStr.isEmpty()) {
                                    showToast("Repeat PIN cannot be empty");
                                }
                                else if (!newPinStr.equals(rPinStr)) {
                                    showToast("New PIN & Repeat PIN should match");
                                }
                                else {
                                    settingsDbService.saveSecurityKeyUserId(loggedInUserObj.getUSER_ID(), EncryptionUtil.encrypt(newPinStr));
                                    settingsDbService.enableDisableSecurityOnUserId(loggedInUserObj.getUSER_ID(), true);
                                    killPoppers();
                                    setUpSecurity();

                                    //update Sec PIN in loggedInUserObj
                                    loggedInUserObj = FinappleUtility.getInstance().getUser(mContext);

                                    showToast("PIN Saved & Security is Enabled");
                                }
                            } catch (Exception e) {
                                Log.e(CLASS_NAME, "Error !! While Encryption ");
                            }
                        }
                        else if(View.GONE == lockPoprNewPinLL.getVisibility() && View.VISIBLE == lockPoprPinET.getVisibility()) {
                            try {
                                if (oldPinStr.isEmpty()) {
                                    showToast("Enter PIN");
                                }
                                else if (!EncryptionUtil.encrypt(oldPinStr).equals(loggedInUserObj.getSET_SEC_PIN())) {
                                    showToast("Incorrect PIN");

                                    lockPoprPinET.setText("");
                                    oldPinStr = "";
                                }
                                else {
                                    boolean secIsEnabled = false;
                                    if(!loggedInUserObj.getSET_SEC_PIN().isEmpty()){
                                        secIsEnabled = true;
                                    }

                                    settingsDbService.enableDisableSecurityOnUserId(loggedInUserObj.getUSER_ID(), !secIsEnabled);
                                    killPoppers();
                                    setUpSecurity();

                                    //update Sec PIN in loggedInUserObj
                                    loggedInUserObj = FinappleUtility.getInstance().getUser(mContext);

                                    if(!loggedInUserObj.getSET_SEC_PIN().isEmpty()){
                                        showToast("Security is Enabled");
                                    }
                                    else{
                                        showToast("Security is Disabled");
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(CLASS_NAME, "Error !! While Encryption ");
                            }
                        }

                        break;

                    default: showToast("Error !!");
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        navigateTo(CalendarActivity.class, null, null);
    }
}