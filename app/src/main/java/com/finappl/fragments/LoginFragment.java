package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.activities.LoginActivity;
import com.finappl.adapters.CountriesSpinnerAdapter;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.CountryMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

import static com.finappl.utils.Constants.DEFAULT_COUNTRIES_CURRENCIES;
import static com.finappl.utils.Constants.SHARED_PREF;
import static com.finappl.utils.Constants.SHARED_PREF_ACTIVE_USER_ID;
import static com.finappl.utils.Constants.UI_DATE_FORMAT_SDF;

/**
 * Created by ajit on 21/3/16.
 */
public class LoginFragment extends DialogFragment implements LinearLayout.OnClickListener {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    private TextView loginUsernameET, loginPasswordET;
    private LinearLayout loginLL, registerLL, register1NextLL;
    private TextView finapplTV;
    private ImageView discardIV;
    private LinearLayout finapplLL;
    private LinearLayout simpleLL;
    private LinearLayout register1ContentLL;
    private TextView screenJobTV;
    private EditText registerUserIdET;
    private EditText registerNameET;
    private EditText registerPassET;
    private EditText registerRepeatPassET;
    private Spinner registerCountrySpn;
    private TextView registerDobTV;
    private TextView registerCountryCodeTV;
    private EditText registerPhoneET;
    private EditText registerEmailET;

    private UserMO userObj;

    private AuthorizationDbService authorizationDbService;
    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container);

        dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        initComps(view);
        setupPage();

        return view;
    }

    public void getInputs(){
        userObj = new UserMO();

        //for login
        userObj.setUSER_ID(String.valueOf(loginUsernameET.getText()));
        userObj.setPASS(String.valueOf(loginPasswordET.getText()));

        //for register
        userObj.setNAME(String.valueOf(registerNameET.getText()));
        userObj.setPASS(String.valueOf(registerPassET.getText()));

        //TODO: country spinner implementation is pending
        userObj.setCNTRY_ID(String.valueOf(((CountryMO)registerCountrySpn.getSelectedView().getTag()).getCNTRY_ID()));
        try {
            userObj.setDOB(UI_DATE_FORMAT_SDF.parse(String.valueOf(registerDobTV.getText())));
        }
        catch (java.text.ParseException e){
            Log.e(CLASS_NAME, "Parse Exception: "+e);
        }
        userObj.setTELEPHONE(String.valueOf(registerPhoneET.getText()));
        userObj.setEMAIL(String.valueOf(registerEmailET.getText()));
    }

    private void setupPage() {
        //TODO: load most recent user

        //setup countries spinner
        List<CountryMO> countryMOList = authorizationDbService.getAllCountries();
        registerCountrySpn.setAdapter(new CountriesSpinnerAdapter(mContext, countryMOList));
        registerCountryCodeTV.setText("+"+countryMOList.get(0).getCNTRY_CODE());
    }

    private void initComps(View view){
        loginUsernameET = (TextView) view.findViewById(R.id.loginUsernameETId);
        loginPasswordET = (TextView) view.findViewById(R.id.loginPasswordETId);
        loginLL = (LinearLayout) view.findViewById(R.id.loginButtonLLId);
        registerLL = (LinearLayout) view.findViewById(R.id.registerLLId);
        discardIV = (ImageView) view.findViewById(R.id.discardIVId);
        finapplLL = (LinearLayout) view.findViewById(R.id.loginLLId);
        simpleLL = (LinearLayout) view.findViewById(R.id.simpleLLId);
        register1NextLL = (LinearLayout) view.findViewById(R.id.register1NextLLId);
        screenJobTV = (TextView) view.findViewById(R.id.screenJobTVId);
        registerUserIdET = (EditText) view.findViewById(R.id.registerUserIdETId);
        registerNameET = (EditText) view.findViewById(R.id.registerNameETId);
        registerPassET = (EditText) view.findViewById(R.id.registerPassETId);
        registerRepeatPassET = (EditText) view.findViewById(R.id.registerRepeatPassETId);
        registerCountrySpn = (Spinner) view.findViewById(R.id.registerCountrySpnId);
        registerDobTV = (TextView) view.findViewById(R.id.registerDobTVId);
        registerCountryCodeTV = (TextView) view.findViewById(R.id.registerCountryCodeTVId);
        registerPhoneET = (EditText) view.findViewById(R.id.registerPhoneETId);
        registerEmailET = (EditText) view.findViewById(R.id.registerEmailETId);

        loginLL.setOnClickListener(this);
        registerLL.setOnClickListener(this);
        discardIV.setOnClickListener(this);
        register1NextLL.setOnClickListener(this);

        discardIV.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        String messageStr = "Error !!";
        boolean doLogin = false;
        if(v.getId() == loginLL.getId()){
            getInputs();

            if(userObj == null){
                messageStr = "Enter Username/Password";
            }
            else if(userObj.getUSER_ID() == null || (userObj.getUSER_ID() != null && userObj.getUSER_ID().trim().isEmpty())){
                messageStr = "Enter Username";
            }
            else if(userObj.getUSER_ID().length() < 8){
                messageStr = "Username should be at least 8 characters long";
            }
            else if(userObj.getPASS() == null || (userObj.getPASS() != null && userObj.getPASS().trim().isEmpty())){
                messageStr = "Enter Password";
            }
            else if(userObj.getPASS().length() < 6){
                messageStr = "Username should be at least 6 characters long";
            }
            else{
                if(authorizationDbService.isAuthenticUser(userObj)){
                    SharedPreferences sharedpreferences = mContext.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(SHARED_PREF_ACTIVE_USER_ID, userObj.getUSER_ID());
                    editor.commit();
                    doLogin = true;
                }
                else{
                    showToast("Wrong Username/Password !");
                }
            }
        }
        else if(v.getId() == registerLL.getId()){
            animateView(finapplLL);
            discardIV.setVisibility(View.VISIBLE);
            screenJobTV.setText("REGISTER");
        }
        else if(v.getId() == register1NextLL.getId()){
            getInputs();

            //get the countries
            String userIdStr = String.valueOf(registerUserIdET.getText());
            String repeatPasswordStr = String.valueOf(registerRepeatPassET.getText());

            if(userIdStr == null || (userIdStr != null && userIdStr.trim().isEmpty())){
                showToast("Username cannot be empty");
            }
            if(userIdStr.contains(" ")){
                //TODO: check for special chars in user id
                showToast("Username should be Alphanumeric only");
            }
            if(userObj.getNAME() == null || (userObj.getNAME() != null && userObj.getNAME().trim().isEmpty())){
                showToast("Name cannot be empty");
            }
            else if(userObj.getPASS() == null || (userObj.getPASS() != null && userObj.getPASS().trim().isEmpty())){
                showToast("Password cannot be empty");
            }
            else if(repeatPasswordStr == null || (repeatPasswordStr != null && repeatPasswordStr.trim().isEmpty())){
                showToast("Repeat Password cannot be empty");
            }
            else if(!repeatPasswordStr.equals(userObj.getPASS())){
                showToast("Passwords do not match");
            }
            else if(userObj.getTELEPHONE() == null || (userObj.getTELEPHONE() != null && userObj.getTELEPHONE().trim().isEmpty())){
                showToast("Phone number cannot be empty");
            }
            else if(userObj.getEMAIL() == null || (userObj.getEMAIL() != null && userObj.getEMAIL().trim().isEmpty())){
                showToast("Email cannot be empty");
            }
            else if(!userObj.getEMAIL().contains("@") || !userObj.getEMAIL().contains(".com")){
                showToast("Enter valid Email");
            }
            else if(authorizationDbService.isUserExists(userObj.getUSER_ID())){
                showToast("Username already exists !");
            }
            else{
                userObj.setUSER_ID(userIdStr);
                userObj.setDEV_ID(FinappleUtility.getDeviceId(mContext));
                if(authorizationDbService.addNewUser(userObj)){
                    SharedPreferences sharedpreferences = mContext.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(SHARED_PREF_ACTIVE_USER_ID, userObj.getUSER_ID());
                    editor.commit();
                    doLogin = true;
                }
                else{
                    showToast("User could not be registered !");
                }
            }
        }
        else if(v.getId() == discardIV.getId()) {
            doLogin = true;
            messageStr = "LOGIN";
        }

        if(doLogin){
            CalendarActivity activity = (CalendarActivity) this.getActivity();
            activity.onFinishUserDialog(messageStr);
            this.dismiss();
        }
    }

    private void animateView(final LinearLayout layout){
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout.getLayoutParams();
                params.leftMargin = (int)(-660 * interpolatedTime);
                layout.setLayoutParams(params);
            }
        };
        a.setDuration(250); // in ms
        layout.startAnimation(a);

        //TODO: -660 is based on the moto-g test on other screen sizes required
    }

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    // Empty constructor required for DialogFragment
    public LoginFragment() {}



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDb();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
            d.setCancelable(false);
        }
    }

    private void initDb() {
        mContext = getActivity().getApplicationContext();

        authorizationDbService = new AuthorizationDbService(mContext);
    }

    public interface DialogResultListener {
        void onFinishUserDialog(String resultStr);
    }
}
