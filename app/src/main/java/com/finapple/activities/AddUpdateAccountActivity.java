package com.finapple.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finapple.R;
import com.finapple.dbServices.AddUpdateAccDbService;
import com.finapple.dbServices.AuthorizationDbService;
import com.finapple.model.AccountsModel;
import com.finapple.model.UsersModel;

import java.util.Map;

/**
 * Created by ajit on 31/1/15.
 */
public class AddUpdateAccountActivity extends Activity {

    private final String CLASS_NAME = this.getClass().getName();

    //header
    private ImageView addAccHeaderBackImg;

    //page
    private EditText addAccAccNameET, addAccInitAmtET, addAccNoteET;

    //buttons
    private TextView addAccDoneTV;

    private Context mContext = this;

    //message popper
    private Dialog dialog;

    //db service
    private AddUpdateAccDbService addUpdateAccDbService = new AddUpdateAccDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UsersModel loggedInUserObj;

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_content_add_update_account);

        //get the Active user
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return;
        }

        //init ui components
        initUIComponents();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.addAccRLId), robotoCondensedLightFont);
    }

    public AccountsModel getInputs(){
        String accAmt = addAccInitAmtET.getText().toString();

        if(accAmt.isEmpty()){
            accAmt = "0";
        }

        AccountsModel accObj =  new AccountsModel();
        accObj.setACC_NAME(addAccAccNameET.getText().toString());
        accObj.setInitialAmount(Double.parseDouble(accAmt));
        accObj.setACC_NOTE(addAccNoteET.getText().toString());
        return accObj;
    }

    public void onDoneUpdate(View view){
        AccountsModel accObj = getInputs();

        if(accObj.getACC_NAME().trim().isEmpty()){
            showToast("account name cannot be empty !");
            return;
        }

        //pass the user id as well
        accObj.setUSER_ID(loggedInUserObj.getUSER_ID());


        if("SAVE".equalsIgnoreCase(addAccDoneTV.getText().toString())){
            long result = addUpdateAccDbService.addNewAccount(accObj);

            if(result == -2){
                showToast("account already exists !");
                return;
            }
            else if(result == -1){
                showToast("could not create the account !");
                return;
            }
            else{
                showToast("new account created !");

                Intent intent = new Intent(this, ManageContentActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else{
            //TODO: update for add account yet to be implemented
        }
    }

    public void onBackClick(View view){
        Intent intent = null;

        if("BACK".equalsIgnoreCase(view.getTag().toString())){
            intent = new Intent(this, ManageContentActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            showMessagePopper(view);
        }
    }

    private void initUIComponents() {
        //initialize UI components
        //header
        addAccHeaderBackImg = (ImageView) this.findViewById(R.id.addAccHeaderBackImgId);

        //page
        addAccAccNameET = (EditText) this.findViewById(R.id.addAccAccNameETId);
        addAccInitAmtET = (EditText) this.findViewById(R.id.addAccInitAmtETId);
        addAccNoteET = (EditText) this.findViewById(R.id.addAccNoteETId);

        //buttons
        addAccDoneTV = (TextView) this.findViewById(R.id.addAccDoneTVId);

        addAccAccNameET.addTextChangedListener(fileTextWatcher);
    }

    public void showMessagePopper(View view){
        if("BACK".equalsIgnoreCase(addAccHeaderBackImg.getTag().toString())){
            Intent intent = new Intent(this, ManageContentActivity.class);
            startActivity(intent);
            finish();
            return;
        }

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
        msgPoprPosLL.setOnClickListener(linearLayoutClickListener);
        msgPoprNegLL.setOnClickListener(linearLayoutClickListener);

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) dialog.findViewById(R.id.msgPoprLLId), robotoCondensedLightFont);
    }

    @Override
    public void onBackPressed() {
        showMessagePopper(null);
    }

    //--------------------------------Linear Layout click listener--------------------------------------------------
    private LinearLayout.OnClickListener linearLayoutClickListener;
    {
        linearLayoutClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(CLASS_NAME, "Linear Layout Click is working !! There's hope :) by the way you clicked:" + v.getId());

                Intent intent = null;

                switch(v.getId()){
                    case R.id.msgPoprPosLLId :      intent = new Intent(mContext, ManageContentActivity.class);
                        break;
                    case R.id.msgPoprNegLLId :      break;

                    default:intent = new Intent(mContext, JimBrokeItActivity.class); break;
                }

                if(dialog != null){
                    dialog.dismiss();
                }

                if(intent != null){
                    startActivity(intent);
                    finish();
                }
            }
        };
    }
    //--------------------------------Linear Layout ends--------------------------------------------------

    //---------------------------------Edit Text type Listener-----------------------------------
    TextWatcher fileTextWatcher;
    {
        fileTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //restrict the user to enter only 2 decimal inputs
                if(addAccAccNameET.getText().length() != 0){
                    if("DISCARD".equalsIgnoreCase(addAccHeaderBackImg.getTag().toString())){
                        return;
                    }

                    int currentRotation = 0;
                    final RotateAnimation rotateAnim = new RotateAnimation(currentRotation, currentRotation + 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
                    rotateAnim.setInterpolator(new LinearInterpolator());
                    rotateAnim.setDuration(100);
                    rotateAnim.setFillEnabled(true);
                    rotateAnim.setFillAfter(true);

                    rotateAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            addAccHeaderBackImg.setBackground(addAccHeaderBackImg.getResources().getDrawable(R.drawable.cancel));
                            addAccHeaderBackImg.setTag("DISCARD");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addAccHeaderBackImg.startAnimation(rotateAnim);
                }
                else{
                    if("BACK".equalsIgnoreCase(addAccHeaderBackImg.getTag().toString())){
                        return;
                    }

                    int currentRotation = 0;
                    final RotateAnimation rotateAnim = new RotateAnimation(currentRotation, currentRotation+360, Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF,0.5f);
                    rotateAnim.setInterpolator(new LinearInterpolator());
                    rotateAnim.setDuration(100);
                    rotateAnim.setFillEnabled(true);
                    rotateAnim.setFillAfter(true);

                    rotateAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            addAccHeaderBackImg.setBackground(addAccHeaderBackImg.getResources().getDrawable(R.drawable.back));
                            addAccHeaderBackImg.setTag("BACK");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addAccHeaderBackImg.startAnimation(rotateAnim);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        };
    }
    //---------------------------------Edit Text type Listener ends-----------------------------------

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

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
