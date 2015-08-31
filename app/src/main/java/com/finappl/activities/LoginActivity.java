package com.finappl.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.UsersModel;
import com.finappl.utils.FinappleUtility;

/**
 * Created by ajit on 31/1/15.
 */
public class LoginActivity extends Activity {

    private final String CLASS_NAME = this.getClass().getName();

    private Context mContext = this;

    //UI
    private TextView loginUsernameET, loginPasswordET;

    //db service
    AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //init UI
        initUiComponents();

        //load last username into username field
        setUpPageOnLoad();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.loginRLId), robotoCondensedLightFont);
    }

    public void toRegisterUserPage(View view){
        Intent intent = new Intent(this, RegisterUserActivity.class);
        startActivity(intent);
        finish();
    }

    public void authenticateUser(View view){
        String usernameStr = String.valueOf(loginUsernameET.getText());
        String passStr = String.valueOf(loginPasswordET.getText());

        //validations
        if(usernameStr.trim().isEmpty()){
            showToast("Enter Username");
            return;
        }
        if(usernameStr.trim().length() < 6){
            showToast("Username should be at least 6 Characters Long");
            return;
        }
        if(passStr.trim().isEmpty()){
            showToast("Enter Password");
            return;
        }
        if(passStr.trim().length() < 6){
            showToast("Password should be at least 6 Characters Long");
            return;
        }


        UsersModel usersModelObj = new UsersModel();
        usersModelObj.setUSER_ID(usernameStr);
        usersModelObj.setPASS(passStr);

        if(!authorizationDbService.isAuthenticUser(usersModelObj)){
            //TODO: connect to cloud and check if he's on cloud...if yes import his data to local db and login

            showToast("Login Failed");
            return;
        }

        //TODO: this must be removed during production
        FinappleUtility.getInstance().pullDbFromDeepSystem();

        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
        finish();
    }

    private void setUpPageOnLoad() {
        loginUsernameET.setText(authorizationDbService.getRecentUsername());
    }

    private void initUiComponents() {
        loginUsernameET = (TextView) this.findViewById(R.id.loginUsernameETId);
        loginPasswordET = (TextView) this.findViewById(R.id.loginPasswordETId);
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
}
