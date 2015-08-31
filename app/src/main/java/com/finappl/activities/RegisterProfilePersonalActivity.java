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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.adapters.SettingsProfilePersonalCountryAdapter;
import com.finappl.adapters.SettingsProfilePersonalCurrencyAdapter;
import com.finappl.R;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.CountryModel;
import com.finappl.models.CurrencyModel;
import com.finappl.models.SpinnerModel;
import com.finappl.models.UsersModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajit on 31/1/15.
 */
public class RegisterProfilePersonalActivity extends Activity {

    private final String CLASS_NAME = this.getClass().getName();

    private Context mContext = this;

    //UI
    private EditText profilePersonalNameET;
    private TextView profilePersonalDobTV;
    private Spinner profilePersonalCntrySpn, profilePersonalCurSpn;
    private RadioGroup profilePersonalEmplydRadioGrp;
    private RadioButton profilePersonalEmplydYesRadio, profilePersonalEmplydNoRadio;

    //db service
    AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    private UsersModel usersModelObj;

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_profile_personal);

        //get UserModelObj from intent
        getFromIntent();

        //init UI
        initUiComponents();

        //load page data
        onPageLoad();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.profilePersonalRLId), robotoCondensedLightFont);
    }

    private void onPageLoad() {
        //get currency and country lists
        List<CountryModel> countryList = authorizationDbService.getAllCountry();
        List<CurrencyModel> currencyList = authorizationDbService.getAllCurrency();

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
        profilePersonalCntrySpn.setAdapter(new SettingsProfilePersonalCountryAdapter(mContext, R.layout.settings_profile_personal_country_spinner_item,
                countryList));

        for(CurrencyModel iterCurrencyList : currencyList){
            spinnerModelObj = new SpinnerModel();
            spinnerModelObj.setItemId(iterCurrencyList.getCUR_ID());
            spinnerModelObj.setItemName(iterCurrencyList.getCUR_NAME());
            currencySpnList.add(spinnerModelObj);
        }

        profilePersonalCurSpn.setAdapter(new SettingsProfilePersonalCurrencyAdapter(mContext, R.layout.settings_profile_personal_currency_spinner_item,
                currencyList));
    }

    private void getFromIntent() {
        usersModelObj = (UsersModel)getIntent().getSerializableExtra("REGISTER_USER");

        if(usersModelObj == null) {
            Log.e(CLASS_NAME, "Error !! Could not find 'REGISTER_USER' in the intent");
        }
    }

    public void toRegisterUserPage(View view){
        Intent intent = new Intent(this, RegisterProfilePersonalActivity.class);
        startActivity(intent);
        finish();
    }

    private void initUiComponents() {
        profilePersonalNameET = (EditText) this.findViewById(R.id.profilePersonalNameETId);

        profilePersonalDobTV = (TextView) this.findViewById(R.id.profilePersonalDobTVId);

        profilePersonalEmplydRadioGrp = (RadioGroup) this.findViewById(R.id.profilePersonalEmplydRadioGrpId);
        profilePersonalEmplydYesRadio = (RadioButton) this.findViewById(R.id.profilePersonalEmplydYesRadioId);
        profilePersonalEmplydNoRadio = (RadioButton) this.findViewById(R.id.profilePersonalEmplydNoRadioId);

        profilePersonalCntrySpn = (Spinner) this.findViewById(R.id.profilePersonalCntrySpnId);
        profilePersonalCurSpn = (Spinner) this.findViewById(R.id.profilePersonalCurSpnId);
    }

    public void goToRegisterProfileWorkOrContact(View view){
        //validations
        String nameStr = String.valueOf(profilePersonalNameET.getText());

        if(nameStr.trim().isEmpty()){
            showToast("Enter Name");
            return;
        }

        usersModelObj.setNAME(nameStr);

        //set all the inputs in userModelObj and pass it to next screen
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM ''yy");

        try{
            usersModelObj.setDOB(sdf.parse(String.valueOf(profilePersonalDobTV.getText())));
        }
        catch(ParseException pe){
            Log.e(CLASS_NAME, "Date Parse Exception in goToRegisterProfilePersonal():"+pe);
        }
        usersModelObj.setCNTRY_ID(String.valueOf(profilePersonalCntrySpn.getSelectedView().getTag()));
        usersModelObj.setCUR_ID(String.valueOf(profilePersonalCurSpn.getSelectedView().getTag()));

        Intent intent = null;

        //if employed show Profil -Work page or else jump to Contct Screen
        if(profilePersonalEmplydRadioGrp.getCheckedRadioButtonId() == profilePersonalEmplydYesRadio.getId()){
            intent = new Intent(this, RegisterProfileWorkActivity.class);
        }else {
            intent = new Intent(this, RegisterProfileContactActivity.class);
        }

        //pass the user inputs to the next screen...ultimately add the user after the 4th step
        intent.putExtra("REGISTER_USER", usersModelObj);
        startActivity(intent);
        finish();
    }

    public void showDatePicker(View view){
        Log.i(CLASS_NAME, "Working very hard to call date picker to work");
        showDialog(999);
    }

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
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

    //---------------------------------------Date Picker-------------------------------------------------
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            SimpleDateFormat rightSdf = new SimpleDateFormat("d-MM-yyyy");
            SimpleDateFormat wrongSdf = new SimpleDateFormat("d MMM ''yy");

            String convDateStr = "";

            try{
                convDateStr = rightSdf.format(wrongSdf.parseObject(String.valueOf(profilePersonalDobTV.getText())));
            }
            catch(ParseException pe){
                Log.e(CLASS_NAME, "Date Picker Error !!"+pe);
            }

            String selectedDateStrArr[] = convDateStr.split("-");

            return new DatePickerDialog(this, myDateListener, Integer.parseInt(selectedDateStrArr[2]), Integer.parseInt(selectedDateStrArr[1])-1,
                    Integer.parseInt(selectedDateStrArr[0]));
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener;
    {
        myDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                Log.i(CLASS_NAME, "HA HA HA..and you thought date picker wont work for you !!! You pay it. it works for u. Simple.");
                Log.i(CLASS_NAME, "Date picker says that u selected:" + day + "-" + month + "-" + year);

                //change jan-0 to jan-1
                month++;

                Log.i(CLASS_NAME, "Date picker date translated to be:" + day + "-" + month + "-" + year);

                //change dob accordingly
                SimpleDateFormat wrongSdf = new SimpleDateFormat("d-MM-yyyy");
                SimpleDateFormat rightSdf = new SimpleDateFormat("d MMM ''yy");

                try{
                    profilePersonalDobTV.setText(rightSdf.format(wrongSdf.parseObject(day+"-"+month+"-"+year)));
                }
                catch(ParseException pe){
                    Log.e(CLASS_NAME, "Date Picker Error !!"+pe);
                }
            }
        };
    }
    //---------------------------------------Date Picker ends--------------------------------------------
}
