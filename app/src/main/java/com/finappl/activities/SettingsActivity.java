package com.finappl.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.finappl.models.UsersModel;
import com.finappl.utils.Constants;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
public class SettingsActivity extends Activity {
    private final String CLASS_NAME = this.getClass().getName();

    private Context mContext = this;

    //dialogs
    private Dialog dialog;

    //db service
    private SettingsDbService settingsDbService = new SettingsDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UsersModel loggedInUserObj;

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //get the Active user
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return;
        }

        setUpNotifications();

        setUpSounds();

        setUpSecurity();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(this.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.settingsParentRLId), robotoCondensedLightFont);
    }

    public void toManageContent(View view){
        Intent intent = new Intent(this, ManageContentActivity.class);
        startActivity(intent);
        finish();
    }

    public void onHomeClick(View view){
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
        finish();
    }

    public void onBudget(View view){
        Intent intent = new Intent(this, BudgetsViewActivity.class);
        startActivity(intent);
        finish();
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
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup)dialog.findViewById(R.id.settingsProfilePersonalPoprLLId), robotoCondensedLightFont);
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
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) dialog.findViewById(R.id.msgPoprLLId), robotoCondensedLightFont);
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

    public void showChangeSecurityPinPopper(View view){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.settings_security_popper);

        dialog.show();

        final TextView settingsSecurityMsgTV = (TextView) dialog.findViewById(R.id.settingsSecurityMsgTVId);
        final EditText settingsSecurityKeyET = (EditText) dialog.findViewById(R.id.settingsSecurityKeyETId);
        final TextView settingsSecurityPopperActionTV = (TextView) dialog.findViewById(R.id.settingsSecurityPopperActionTVId);

        if(settingsDbService.getUserSecurityKeyOnUserId(loggedInUserObj.getUSER_ID()).isEmpty()){
            settingsSecurityMsgTV.setText("Set Your PIN");
            settingsSecurityPopperActionTV.setText("SAVE");
        }
        else{
            settingsSecurityMsgTV.setText("Enter Your PIN");
            settingsSecurityPopperActionTV.setText("OK");
        }

        settingsSecurityPopperActionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("SAVE".equalsIgnoreCase(String.valueOf(settingsSecurityPopperActionTV.getText()))){
                    if(String.valueOf(settingsSecurityKeyET.getText()).trim().isEmpty()){
                        showToast("Enter PIN");
                    }
                    else{
                        settingsDbService.saveSecurityKeyUserId(loggedInUserObj.getUSER_ID(), String.valueOf(settingsSecurityKeyET.getText()));
                        dialog.dismiss();
                        showToast("PIN saved");
                    }
                }
                else{
                    if(String.valueOf(settingsSecurityKeyET.getText()).trim().isEmpty()){
                        showToast("Enter your PIN");
                    }
                    else if(settingsDbService.authenticateOnUserIdAndKey(loggedInUserObj.getUSER_ID(), String.valueOf(settingsSecurityKeyET.getText()))){
                        if(settingsDbService.getUserSecurityKeyOnUserId(loggedInUserObj.getUSER_ID()).equals(String.valueOf(settingsSecurityKeyET.getText()))){
                            settingsSecurityMsgTV.setText("Enter Your New PIN");
                            settingsSecurityPopperActionTV.setText("SAVE");
                            settingsSecurityKeyET.setText("");
                        }
                        else{
                            showToast("Incorrect PIN");
                            dialog.dismiss();
                        }
                    }
                }
            }
        });



        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) dialog.findViewById(R.id.settingsSecurityPopperLLId), robotoCondensedLightFont);
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
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) dialog.findViewById(R.id.settingsNotifsPopperLLId), robotoCondensedLightFont);
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

    public void enableDisableSecurity(View view){
        LinearLayout settingsSecurityTickLL = (LinearLayout) this.findViewById(R.id.settingsSecurityTickLLId);
        ImageView settingsSecurityTickIV = (ImageView) this.findViewById(R.id.settingsSecurityTickIVId);

        if("ENABLED".equalsIgnoreCase(String.valueOf(settingsSecurityTickLL.getTag()))){
            showToast("Security disabled");
            settingsSecurityTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_unchecked);
            settingsSecurityTickLL.setTag("DISABLED");
            settingsSecurityTickIV.setBackgroundResource(R.drawable.tick_grey);
            settingsDbService.enableDisableSecurityOnUserId(loggedInUserObj.getUSER_ID(), false);
        }
        else{
            showToast("Security enabled");
            settingsSecurityTickLL.setBackgroundResource(R.drawable.circle_tick_super_inner_checked);
            settingsSecurityTickLL.setTag("ENABLED");
            settingsSecurityTickIV.setBackgroundResource(R.drawable.tick_white);
            settingsDbService.enableDisableSecurityOnUserId(loggedInUserObj.getUSER_ID(), true);
        }
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

    private UsersModel getUser(){
        Map<Integer, UsersModel> userMap = authorizationDbService.getActiveUser();

        if(userMap == null || (userMap != null && userMap.isEmpty())){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            showToast("Please Login");
            return null;
        }
        else if(userMap.size() > 1){
            Intent intent = new Intent(this, JimBrokeItActivity.class);
            startActivity(intent);
            finish();
            showToast("Multiple Users are Active : Possible DB Corruption.");
        }
        else{
            return userMap.get(0);
        }

        Log.e(CLASS_NAME, "I'm not supposed to be read/print/shown..... This should have been a dead code. If you can read me, Authorization of user has failed and you should " +
                    "probably die twice by now.");
        return null;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
        finish();
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group, Typeface font) {
        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v, font);
            }
        }
    }
}