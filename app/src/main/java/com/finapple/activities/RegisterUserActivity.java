package com.finapple.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finapple.R;
import com.finapple.dbServices.AuthorizationDbService;
import com.finapple.model.UsersModel;

/**
 * Created by ajit on 31/1/15.
 */
public class RegisterUserActivity extends Activity {

    private final String CLASS_NAME = this.getClass().getName();

    private Context mContext = this;

    //UI
    private EditText registerUsernameET, registerPasswordET, registerRepeatPasswordET;

    //db service
    AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);


    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);

        //init UI
        initUiComponents();
        
        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.registerRLId), robotoCondensedLightFont);
    }

    public void toRegisterUserPage(View view){
        Intent intent = new Intent(this, RegisterUserActivity.class);
        startActivity(intent);
        finish();
    }

    private void initUiComponents() {
        registerUsernameET = (EditText) this.findViewById(R.id.registerUsernameETId);
        registerPasswordET = (EditText) this.findViewById(R.id.registerPasswordETId);
        registerRepeatPasswordET = (EditText) this.findViewById(R.id.registerRepeatPasswordETId);
    }

    public void goToRegisterProfilePersonal(View view){
        //UI validations
        String usernameStr = String.valueOf(registerUsernameET.getText());
        String passwordStr = String.valueOf(registerPasswordET.getText());
        String repeatPasswordStr = String.valueOf(registerRepeatPasswordET.getText());


        if(usernameStr.trim().isEmpty()){
            Log.i(CLASS_NAME, "abe saale..username is empty");
            showToast("Enter Username");
            return;
        }
        if(usernameStr.trim().length() < 6){
            Log.i(CLASS_NAME, "abe saale..Username Not Long Enough");
            showToast("Username Should be atleast 6 Characters Long");
            return;
        }
        if(passwordStr.isEmpty()){
            Log.i(CLASS_NAME, "abe saale..password is not found");
            showToast("Enter Password");
            return;
        }
        if(passwordStr.trim().length() == 0){
            Log.i(CLASS_NAME, "abe saale..what nonsense password is this ? All spaces ?");
            showToast("Bad Password");
            return;
        }
        if(passwordStr.trim().length() < 6){
            Log.i(CLASS_NAME, "abe saale..Password not long enough");
            showToast("Password Should be atleast 6 Characters Long");
            return;
        }
        if(!repeatPasswordStr.equals(passwordStr)){
            Log.i(CLASS_NAME, "abe saale..passwords not matching");
            showToast("Both Passwords Should Match");
            return;
        }

        //check for username if it already exists in the local DB
        //TODO: must implement the same to check for username already being taken by checking in the Cloud DB
        if(authorizationDbService.isUserExists(usernameStr)){
            showToast("Sorry, This Username is Already Taken. Choose Another.");
            return;
        }

        //pass the user inputs to the next screen...ultimately add the user after the 4th step
        //TODO: Need to register him into Cloud. Make this first and let us register him in local db later.
        UsersModel userModelObj = new UsersModel();
        userModelObj.setUSER_ID(usernameStr);
        userModelObj.setPASS(passwordStr);

        Intent intent = new Intent(this, RegisterProfilePersonalActivity.class);
        intent.putExtra("REGISTER_USER", userModelObj);
        startActivity(intent);
        finish();
    }

    public void onBackClick(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
