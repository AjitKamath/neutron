package com.finappl.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.adapters.SettingsProfilePersonalCountryAdapter;
import com.finappl.adapters.SettingsProfilePersonalCurrencyAdapter;
import com.finappl.R;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.SettingsDbService;
import com.finappl.models.CountryModel;
import com.finappl.models.CurrencyModel;
import com.finappl.models.SpinnerModel;
import com.finappl.models.UsersModel;

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
        settingsDbService.logoutAllUsers();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        showToast("Logged Out");
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

                String newDobStrArr[] = null;
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