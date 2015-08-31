package com.finappl.activities;

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

import com.finappl.R;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.UsersModel;

/**
 * Created by ajit on 31/1/15.
 */
public class RegisterProfileContactActivity extends Activity {

    private final String CLASS_NAME = this.getClass().getName();

    private Context mContext = this;

    //UI
    private EditText profileContactTelephoneET, profileContactEmailET;

    //db service
    AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    private UsersModel usersModelObj;

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_profile_contact);

        //get UserModelObj from intent
        getFromIntent();

        //init UI
        initUiComponents();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.profileContactRLId), robotoCondensedLightFont);
    }

    private void getFromIntent() {
        usersModelObj = (UsersModel)getIntent().getSerializableExtra("REGISTER_USER");

        if(usersModelObj == null){
            Log.e(CLASS_NAME, "Error !! Could not find 'REGISTER_USER' in the intent");
        }
    }

    private void initUiComponents() {
        profileContactTelephoneET = (EditText) this.findViewById(R.id.profileContactTelephoneETId);
        profileContactEmailET = (EditText) this.findViewById(R.id.profileContactEmailETId);
    }

    public void goToCalendarPage(View view){
        //set all the inputs in userModelObj and pass it to next screen
        usersModelObj.setTELEPHONE(String.valueOf(profileContactTelephoneET.getText()));
        usersModelObj.setEMAIL(String.valueOf(profileContactEmailET.getText()));

        //TODO: imlement fetching real device ID
        usersModelObj.setDEV_ID("HARDCODED");

        //TODO: need to register the user to cloud db before registering into local DB here...

        //Register the user here
        long result = authorizationDbService.addNewUser(usersModelObj);

        if(result != -1) {
            Log.i(CLASS_NAME, "It seems the user is registered into the system... Lets confirm whther he is really in the db");
        }
        else{
            Log.e(CLASS_NAME, "User could not be registered into the local Db and ur dreaming about registering him into the cloud !!! Get help !!");
            return;
        }

        //check if user exists..only then let him proceed to the Calendar Screen
        if(!authorizationDbService.isUserExists(usersModelObj.getUSER_ID())){
            Log.e(CLASS_NAME, "Good Lord Have Mercy !!!! User is not found in the local DB...cannot proceed to Calendar Screen..continue rotting in here");
            showToast("Something went wrong");
            return;
        }

        Intent intent = new Intent(this, CalendarActivity.class);
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
