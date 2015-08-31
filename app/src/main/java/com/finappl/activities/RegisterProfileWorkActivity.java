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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.UsersModel;

/**
 * Created by ajit on 31/1/15.
 */
public class RegisterProfileWorkActivity extends Activity {

    private final String CLASS_NAME = this.getClass().getName();

    private Context mContext = this;

    //UI
    private EditText profileWorkCompanyET, profileWorkSalaryET;
    private Spinner profileWorkWorkTypeSpn, profileWorkSalaryFreqSpn;

    //db service
    AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    private UsersModel usersModelObj;

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_profile_work);

        //get UserModelObj from intent
        getFromIntent();

        //init UI
        initUiComponents();

        //load page data
        onPageLoad();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.profileWorkRLId), robotoCondensedLightFont);
    }

    private void onPageLoad() {

    }

    private void getFromIntent() {
        usersModelObj = (UsersModel)getIntent().getSerializableExtra("REGISTER_USER");
        Log.e(CLASS_NAME, "Error !! Could not find 'REGISTER_USER' in the intent");
    }

    public void toRegisterUserPage(View view){
        Intent intent = new Intent(this, RegisterProfileWorkActivity.class);
        startActivity(intent);
        finish();
    }

    private void initUiComponents() {
        profileWorkCompanyET = (EditText) this.findViewById(R.id.profileWorkCompanyETId);
        profileWorkSalaryET = (EditText) this.findViewById(R.id.profileWorkSalaryETId);

        profileWorkWorkTypeSpn = (Spinner) this.findViewById(R.id.profileWorkWorkTypeSpnId);
        profileWorkSalaryFreqSpn = (Spinner) this.findViewById(R.id.profileWorkSalaryFreqSpnId);
    }

    public void goToRegisterProfileContact(View view){
        //set all the inputs in userModelObj and pass it to next screen
        usersModelObj.setWORK_TYPE(String.valueOf(profileWorkWorkTypeSpn.getSelectedView().getTag()));
        usersModelObj.setCOMPANY(String.valueOf(profileWorkCompanyET.getText()));
        usersModelObj.setSALARY(Double.parseDouble(String.valueOf(profileWorkSalaryET.getText())));
        usersModelObj.setSAL_FREQ(String.valueOf(profileWorkSalaryFreqSpn.getSelectedView().getTag()));

        Intent intent = new Intent(this, RegisterProfileContactActivity.class);

        //pass the user inputs to the next screen...ultimately add the user after the 4th step
        intent.putExtra("REGISTER_USER", usersModelObj);
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
