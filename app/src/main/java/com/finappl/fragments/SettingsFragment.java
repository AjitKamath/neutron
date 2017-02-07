package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.activities.HomeActivity;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.models.CountryMO;
import com.finappl.models.UserMO;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.COUNTRY_OBJECT;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_COUNTRIES;
import static com.finappl.utils.Constants.FRAGMENT_SETTINGS;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.SAVED;
import static com.finappl.utils.Constants.SELECTED_COUNTRY_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class SettingsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.settingsRLId)
    RelativeLayout settingsRL;

    @InjectView(R.id.settingsNameETId)
    EditText settingsNameET;

    @InjectView(R.id.settingsEmailETId)
    EditText settingsEmailET;

    @InjectView(R.id.settingsCountryCodeTVId)
    TextView settingsCountryCodeTV;

    @InjectView(R.id.settingsNumberETId)
    EditText settingsNumberET;

    @InjectView(R.id.settingsCountryCurrencyMetricGLId)
    GridLayout settingsCountryCurrencyMetricGL;
    /*Components*/

    private UserMO loggedInUserObj;
    private List<CountryMO> countriesList;

    //database
    private CalendarDbService calendarDbService;
    private AuthorizationDbService authorizationDbService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    @OnClick(R.id.settingsCloseTVId)
    public void onClose(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFIRM_MESSAGE, "Discard Changes ?");

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_SETTINGS);

        ConfirmFragment fragment = new ConfirmFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_CONFIRM);
    }

    @OnClick(R.id.settingsSaveTVId)
    public void saveSettings(){
        if(validateAndGetInputs()){
            if(authorizationDbService.updateUser(loggedInUserObj)){
                dismiss();
                ((HomeActivity)getActivity()).fetchUserWrapper();
                ((HomeActivity)getActivity()).setUpTabs();
                ((HomeActivity)getActivity()).showSnacks(SAVED, OK, Snackbar.LENGTH_SHORT);
            }
            else{
                showSnacks("Something went wrong. Could not save.");
            }
        }
    }

    private boolean validateAndGetInputs() {
        String emailStr = String.valueOf(settingsEmailET.getText());
        String nameStr = String.valueOf(settingsNameET.getText());
        String phoneNumberStr = String.valueOf(settingsNumberET.getText());
        String countryIdStr = ((CountryMO)settingsCountryCurrencyMetricGL.getTag()).getCNTRY_ID();

        if(emailStr == null || emailStr.trim().isEmpty()){
            showSnacks("Email cannot be empty");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()){
            showSnacks("Invalid Email");
            return false;
        }

        if(!Patterns.PHONE.matcher(phoneNumberStr).matches()){
            showSnacks("Invalid Phone Number");
            return false;
        }

        loggedInUserObj.setEMAIL(emailStr.trim());
        loggedInUserObj.setNAME(nameStr.trim());
        loggedInUserObj.setTELEPHONE(phoneNumberStr.trim());
        loggedInUserObj.setCNTRY_ID(countryIdStr);

        return true;
    }

    @OnClick(R.id.settingsCountryCurrencyMetricGLId)
    public void showCountries(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_COUNTRIES);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(COUNTRY_OBJECT, (Serializable) countriesList);
        bundle.putSerializable(SELECTED_COUNTRY_OBJECT, ((CountryMO)settingsCountryCurrencyMetricGL.getTag()).getCNTRY_ID());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_SETTINGS);

        SelectCountriesFragment fragment = new SelectCountriesFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_COUNTRIES);
    }

    private void setupPage() {
        getMasterData();

        if(loggedInUserObj == null){
            Log.e(CLASS_NAME, "Logged in user object is null. Cannot proceed.");
            return;
        }

        if(loggedInUserObj.getNAME() != null){
            settingsNameET.setText(loggedInUserObj.getNAME().toUpperCase());
        }

        if(loggedInUserObj.getEMAIL() != null){
            settingsEmailET.setText(loggedInUserObj.getEMAIL().toUpperCase());
        }

        if(loggedInUserObj.getTELEPHONE() != null){
            settingsNumberET.setText(loggedInUserObj.getTELEPHONE());
        }

        if(loggedInUserObj.getCNTRY_ID() != null){
            setCountry(getCountryOnCountryId(loggedInUserObj.getCNTRY_ID()));
        }
    }

    private void setCountry(CountryMO country){
        if(country == null){
            Log.e(CLASS_NAME, "select_country_country object is null. Cannot set the select_country_country for the spinner");
            return;
        }

        //set select_country_country and its currency code and currency
        settingsCountryCurrencyMetricGL.findViewById(R.id.settingsCountryIVId).setBackgroundResource(Integer.parseInt(country.getCNTRY_IMG()));
        ((TextView)settingsCountryCurrencyMetricGL.findViewById(R.id.settingsCountryTVId)).setText(country.getCNTRY_NAME());
        ((TextView)settingsCountryCurrencyMetricGL.findViewById(R.id.settingsCurrencyCodeTVId)).setText(country.getCUR_CODE());
        ((TextView)settingsCountryCurrencyMetricGL.findViewById(R.id.settingsCurrencyTVId)).setText(country.getCUR());
        ((TextView)settingsCountryCurrencyMetricGL.findViewById(R.id.settingsMetricTVId)).setText(country.getMETRIC());

        //set select_country_country code
        settingsCountryCodeTV.setText("+"+country.getCNTRY_CODE());

        settingsCountryCurrencyMetricGL.setTag(country);
    }

    private CountryMO getCountryOnCountryId(String countryIdStr){
        for(CountryMO iterList : countriesList){
            if(iterList.getCNTRY_ID().equalsIgnoreCase(countryIdStr)){
                return iterList;
            }
        }

        return null;
    }

    private void getMasterData() {
        countriesList = calendarDbService.getAllCountriesAndCurrencies();
    }

    private void getDataFromBundle() {
        loggedInUserObj = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void initComps(){
        setFont(settingsRL);
    }

    // Empty constructor required for DialogFragment
    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        calendarDbService = new CalendarDbService(mContext);
        authorizationDbService = new AuthorizationDbService(mContext);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT ;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
        }
    }

    private void showSnacks(String messageStr){
        Snackbar snackbar = Snackbar.make(settingsRL, messageStr, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group) {
        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), UI_FONT);

        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(robotoCondensedLightFont);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v);
            }
        }
    }

    public void onFinishDialog(CountryMO countryMO) {
        setCountry(countryMO);
    }
}
