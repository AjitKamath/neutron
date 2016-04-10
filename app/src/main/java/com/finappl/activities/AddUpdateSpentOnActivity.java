package com.finappl.activities;

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

import com.finappl.R;
import com.finappl.dbServices.AddUpdateSpentOnDbService;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.SpentOnModel;
import com.finappl.models.UserMO;
import com.finappl.models.UsersModel;
import com.finappl.utils.FinappleUtility;

import java.util.Map;

/**
 * Created by ajit on 31/1/15.
 */
public class AddUpdateSpentOnActivity extends Activity {

    private final String CLASS_NAME = this.getClass().getName();

    //header
    private TextView addSpentOnLabelTV;
    private ImageView addSpentOnHeaderBackImg;

    //page
    private  TextView addSpentOnSpentOnNameTV;
    private EditText addSpentOnSpentOnNameET, addSpentOnNoteET;

    //buttons
    private TextView addSpentOnDoneTV, addSpentOnDiscardTV;

    private Context mContext = this;

    //message popper
    private Dialog dialog;

    //db service
    private AddUpdateSpentOnDbService addUpdateSpentOnDbService = new AddUpdateSpentOnDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UserMO loggedInUserObj;

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_content_add_update_spenton);

        //get the Active user
        loggedInUserObj = FinappleUtility.getInstance().getUser(mContext);
        if(loggedInUserObj == null){
            return;
        }

        //init ui components
        initUIComponents();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.addSpentOnRLId), robotoCondensedLightFont);
    }

    public SpentOnModel getInputs(){
        SpentOnModel spentOnObj =  new SpentOnModel();
        spentOnObj.setSPNT_ON_NAME(addSpentOnSpentOnNameET.getText().toString());
        spentOnObj.setSPNT_ON_NOTE(addSpentOnNoteET.getText().toString());
        return spentOnObj;
    }

    public void onDoneUpdate(View view){
        SpentOnModel spentOnObj = getInputs();

        //add user id
        spentOnObj.setUSER_ID(loggedInUserObj.getUSER_ID());

        if("".equalsIgnoreCase(spentOnObj.getSPNT_ON_NAME().trim())){
            showToast("spent on name cannot be empty !");
            return;
        }

        if("SAVE".equalsIgnoreCase(addSpentOnDoneTV.getText().toString())){
            long result = addUpdateSpentOnDbService.addNewSpentOn(spentOnObj);

            if(result == -2){
                showToast("spent on already exists !");
                return;
            }
            else if(result == -1){
                showToast("could not add spent on !");
                return;
            }
            else{
                showToast("New Spent On Saved !");

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

    public void showMessagePopper(View view){
        if("BACK".equalsIgnoreCase(addSpentOnHeaderBackImg.getTag().toString())){
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

    private void initUIComponents() {
        //initialize UI components
        //header
        addSpentOnLabelTV = (TextView) this.findViewById(R.id.addSpentOnLabelTVId);
        addSpentOnHeaderBackImg = (ImageView) this.findViewById(R.id.addSpentOnHeaderBackImgId);

        //page
        addSpentOnSpentOnNameET = (EditText) this.findViewById(R.id.addSpentOnSpentOnNameETId);
        addSpentOnNoteET = (EditText) this.findViewById(R.id.addSpentOnNoteETId);
        /*addSpentOnSpentOnNameTV = (TextView) this.findViewById(R.id.addSpentOnSpentOnNameTVId);*/

        //buttons
        addSpentOnDoneTV = (TextView) this.findViewById(R.id.addSpentOnDoneTVId);

        addSpentOnSpentOnNameET.addTextChangedListener(fileTextWatcher);
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
                if(addSpentOnSpentOnNameET.getText().length() != 0){
                    if("DISCARD".equalsIgnoreCase(addSpentOnHeaderBackImg.getTag().toString())){
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
                            addSpentOnHeaderBackImg.setBackground(addSpentOnHeaderBackImg.getResources().getDrawable(R.drawable.cancel));
                            addSpentOnHeaderBackImg.setTag("DISCARD");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addSpentOnHeaderBackImg.startAnimation(rotateAnim);
                }
                else{
                    if("BACK".equalsIgnoreCase(addSpentOnHeaderBackImg.getTag().toString())){
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
                            addSpentOnHeaderBackImg.setBackground(addSpentOnHeaderBackImg.getResources().getDrawable(R.drawable.back));
                            addSpentOnHeaderBackImg.setTag("BACK");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addSpentOnHeaderBackImg.startAnimation(rotateAnim);
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
