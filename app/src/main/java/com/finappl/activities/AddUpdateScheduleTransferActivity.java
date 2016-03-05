package com.finappl.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.adapters.AddUpdateTransactionSpinnerAdapter;
import com.finappl.dbServices.AddUpdateTransactionsDbService;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.ScheduledTransfersDbService;
import com.finappl.models.ScheduledTransactionModel;
import com.finappl.models.ScheduledTransferModel;
import com.finappl.models.SpinnerModel;
import com.finappl.models.UsersModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AddUpdateScheduleTransferActivity extends Activity {
	private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    //User
    private UsersModel loggedInUserObj;

    //db service
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);
    private AddUpdateTransactionsDbService addUpdateTransactionsDbService = new AddUpdateTransactionsDbService(mContext);
    private ScheduledTransfersDbService scheduledTransfersDbService = new ScheduledTransfersDbService(mContext);

    //UI Components
    private ImageView schedTransferAddUpdBackImg;
    private TextView schedTransferAddUpdDateTV;

    private TextView schedTransferAddUpdAmtET;

    private Spinner schedTransferAddUpdAccFromSpn;
    private Spinner schedTransferAddUpdAccToSpn;

    private RadioGroup schedTransferSchedRadioGrp;
    private RadioButton schedTransferOnceAddRadio;
    private RadioButton schedTransferDailyAddRadio;
    private RadioButton schedTransferWeeklyAddRadio;
    private RadioButton schedTransferMonthlyAddRadio;
    private RadioButton schedTransferYearlyAddRadio;

    private RadioGroup schedTransferAutoRadioGrp;
    private RadioButton schedTransferAutoAddRadio;
    private RadioButton schedTransferAutoAddNotifyRadio;

    private EditText schTranAddUpdNoteET;

    //spinner lists from db
    private List<SpinnerModel> accountList;

    private ScheduledTransferModel scheduledTransferModelObj;

    //message popper
    private Dialog dialog;

    @Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_transfer_add_update);
        Log.i(CLASS_NAME, "Navigated to Schedule Transfer Add Update Screen");

        //get the Active user
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return;
        }

        //init ui components
        initUiComponents();

        //set up spinners
        setUpSpinners();

        getScheduledTransferObjFromIntent();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.schedTransferAddUpdRLId), robotoCondensedLightFont);
    }

    private void setUpSpinners() {
        //get spinner lists from db
        accountList = addUpdateTransactionsDbService.getAllAccounts(loggedInUserObj.getUSER_ID());

        //set up accounts spinner
        schedTransferAddUpdAccFromSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, accountList));
        schedTransferAddUpdAccToSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, accountList));
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

    private void getScheduledTransferObjFromIntent() {
        if(getIntent().getSerializableExtra("SCHEDULED_TRANSFER_OBJ") != null){
            scheduledTransferModelObj = (ScheduledTransferModel)getIntent().getSerializableExtra("SCHEDULED_TRANSFER_OBJ");

            if(scheduledTransferModelObj != null && scheduledTransferModelObj.getSCH_TRNFR_ID() != null
                    && !scheduledTransferModelObj.getSCH_TRNFR_ID().isEmpty()){
                //fill user id & scheduled transfer id in the scheduledTransferModelObj and pass it to db to get the Scheduled Transfer Data
                scheduledTransferModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());

                scheduledTransferModelObj = scheduledTransfersDbService.getScheduledTransferOnScheduledTransferId(scheduledTransferModelObj);

                //popullate UI with the returned data
                popullateUiWhenUpdate();
            }
            else if(scheduledTransferModelObj != null && scheduledTransferModelObj.getSCH_TRNFR_DATE() != null){
                Log.i(CLASS_NAME, "Could not find SCH_TRNFR_DATE in SCHEDULED_TRANSFER_OBJ in the intent... This means user is trying to create a new Scheduled Transfer");
                popullateUiWhenNew();
            }
        }
        else{
            Log.e(CLASS_NAME, "Could not find SCHEDULED_TRANSACTION_OBJ in the intent... ");
        }
    }

    private void popullateUiWhenNew() {
        SimpleDateFormat rightSdf= new SimpleDateFormat("d MMM ''yy");
        schedTransferAddUpdDateTV.setText(rightSdf.format(scheduledTransferModelObj.getSCH_TRNFR_DATE()));
    }

    private void popullateUiWhenUpdate(){
        SimpleDateFormat rightSdf= new SimpleDateFormat("d MMM ''yy");
        schedTransferAddUpdDateTV.setText(rightSdf.format(scheduledTransferModelObj.getSCH_TRNFR_DATE()));

        schedTransferAddUpdAmtET.setText(String.valueOf(scheduledTransferModelObj.getSCH_TRNFR_AMT()));

        schedTransferAddUpdAccFromSpn.setSelection(getSpinnerItemIndex(accountList, scheduledTransferModelObj.getFromAccountStr()));
        schedTransferAddUpdAccToSpn.setSelection(getSpinnerItemIndex(accountList, scheduledTransferModelObj.getToAccountStr()));

        //schedule type radio
        if("ONCE".equalsIgnoreCase(scheduledTransferModelObj.getSCH_TRNFR_FREQ())){
            schedTransferOnceAddRadio.setChecked(true);
        }
        else if("DAILY".equalsIgnoreCase(scheduledTransferModelObj.getSCH_TRNFR_FREQ())){
            schedTransferDailyAddRadio.setChecked(true);
        }
        else if("WEEKLY".equalsIgnoreCase(scheduledTransferModelObj.getSCH_TRNFR_FREQ())){
            schedTransferWeeklyAddRadio.setChecked(true);
        }
        if("MONTHLY".equalsIgnoreCase(scheduledTransferModelObj.getSCH_TRNFR_FREQ())){
            schedTransferMonthlyAddRadio.setChecked(true);
        }
        else{
            schedTransferYearlyAddRadio.setChecked(true);
        }

        //notes
        schTranAddUpdNoteET.setText(scheduledTransferModelObj.getSCH_TRNFR_NOTE());
    }

    private void initUiComponents() {
        schedTransferAddUpdBackImg = (ImageView) this.findViewById(R.id.schedTransferAddUpdBackImgId);
        schedTransferAddUpdDateTV = (TextView) this.findViewById(R.id.schedTransferAddUpdDateTVId);

        schedTransferAddUpdAmtET = (TextView) this.findViewById(R.id.schedTransferAddUpdAmtETId);

        //text watchers
        schedTransferAddUpdAmtET.addTextChangedListener(fieldTextWatcher);

        schedTransferAddUpdAccFromSpn = (Spinner) this.findViewById(R.id.schedTransferAddUpdAccFromSpnId);
        schedTransferAddUpdAccToSpn = (Spinner) this.findViewById(R.id.schedTransferAddUpdAccToSpnId);

        schedTransferSchedRadioGrp = (RadioGroup) this.findViewById(R.id.schedTransferSchedRadioGrpId);
        schedTransferOnceAddRadio = (RadioButton) this.findViewById(R.id.schedTransferOnceAddRadioId);
        schedTransferDailyAddRadio = (RadioButton) this.findViewById(R.id.schedTransferDailyAddRadioId);
        schedTransferWeeklyAddRadio = (RadioButton) this.findViewById(R.id.schedTransferWeeklyAddRadioId);
        schedTransferMonthlyAddRadio = (RadioButton) this.findViewById(R.id.schedTransferMonthlyAddRadioId);
        schedTransferYearlyAddRadio = (RadioButton) this.findViewById(R.id.schedTransferYearlyAddRadioId);

        schedTransferAutoRadioGrp = (RadioGroup) this.findViewById(R.id.schedTransferAutoRadioGrpId);
        schedTransferAutoAddRadio = (RadioButton) this.findViewById(R.id.schedTransferAutoAddRadioId);
        schedTransferAutoAddNotifyRadio = (RadioButton) this.findViewById(R.id.schedTransferAutoAddNotifyRadioId);

        schTranAddUpdNoteET = (EditText) this.findViewById(R.id.schTranAddUpdNoteETId);
    }

    private ScheduledTransferModel getUserInputs(){
        SimpleDateFormat wrongSdf = new SimpleDateFormat("d MMM ''yy");

        Date date;
        try{
            date = wrongSdf.parse(String.valueOf(schedTransferAddUpdDateTV.getText()));
        }
        catch (ParseException e){
            Log.e(CLASS_NAME, "Error in date parsing: "+e);
            return null;
        }

        String amountStr = String.valueOf(schedTransferAddUpdAmtET.getText());

        if(amountStr.isEmpty()){
            amountStr = "0";
        }

        scheduledTransferModelObj.setSCH_TRNFR_DATE(date);

        scheduledTransferModelObj.setSCH_TRNFR_AMT(Double.parseDouble(amountStr));

        scheduledTransferModelObj.setSCH_TRNFR_ACC_ID_FRM(String.valueOf(schedTransferAddUpdAccFromSpn.getSelectedView().getTag()));
        scheduledTransferModelObj.setSCH_TRNFR_ACC_ID_TO(String.valueOf(schedTransferAddUpdAccToSpn.getSelectedView().getTag()));

        scheduledTransferModelObj.setSCH_TRNFR_FREQ(String.valueOf(this.findViewById(schedTransferSchedRadioGrp.getCheckedRadioButtonId()).getTag()));
        scheduledTransferModelObj.setSCH_TRNFR_AUTO(String.valueOf(this.findViewById(schedTransferAutoRadioGrp.getCheckedRadioButtonId()).getTag()));

        scheduledTransferModelObj.setSCH_TRNFR_NOTE(String.valueOf(schTranAddUpdNoteET.getText()));

        return scheduledTransferModelObj;
    }

    public void onDoneUpdate(View view){
        scheduledTransferModelObj = getUserInputs();

        if(scheduledTransferModelObj == null){
            return;
        }

        //if the amt is 0
        if(scheduledTransferModelObj.getSCH_TRNFR_AMT() <= 0){
            showToast("Amount cannot be Zero");
            return;
        }

        if(scheduledTransferModelObj.getSCH_TRNFR_ACC_ID_FRM().equalsIgnoreCase(scheduledTransferModelObj.getSCH_TRNFR_ACC_ID_TO())){
            showToast("Cannot Transfer between same Accounts");
            return;
        }

        //set user id
        scheduledTransferModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());

        if(scheduledTransferModelObj.getSCH_TRNFR_ID() != null && !scheduledTransferModelObj.getSCH_TRNFR_ID().isEmpty()){
            Log.i(CLASS_NAME, "Schedule Transfer Id is found... This means user is trying to update the Scheduled Transfer");

            int result = scheduledTransfersDbService.updateOldScheduledTransfer(scheduledTransferModelObj);

            if(result == 0){
                showToast("Error !! Couldn't update the Scheduled Transfer");
                return;
            }

            //navigate back to calendar screen
            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
            finish();
            showToast("Scheduled Transfer has been updated");
        } else {
            Log.i(CLASS_NAME, "Schedule Transfer Id is not found... This means user is trying to create a new Scheduled Transfer");

            Long result = scheduledTransfersDbService.createNewScheduledTransfer(scheduledTransferModelObj);
            if(result != -1){
                showToast("New Scheduled Transfer Created");

                //notify the notification service by calling the receiver
                Intent notifIntent = new Intent();
                notifIntent.setAction("ACTIVITY_ACTION");
                sendBroadcast(notifIntent);

                //navigate back to calendar screen
                Intent intent = new Intent(this, CalendarActivity.class);
                startActivity(intent);
                finish();
            } else {
                showToast("Error !! Could not create a new Scheduled Transaction");
            }
        }
    }

    public void onDonePlusNew(View view){
        onDoneUpdate(view);

        SimpleDateFormat wrongSdf = new SimpleDateFormat("d MMM ''yy");

        Date date;

        try{
            date = wrongSdf.parse(String.valueOf(schedTransferAddUpdDateTV.getText()));
        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "ERROR !!"+e);
            return;
        }

        ScheduledTransactionModel scheduledTransferModelObj = new ScheduledTransactionModel();
        scheduledTransferModelObj.setSCH_TRAN_DATE(date);

        startActivity(getIntent());
        finish();
    }

    public void showMessagePopper(View view){
        if("BACK".equalsIgnoreCase(schedTransferAddUpdBackImg.getTag().toString())){
            Intent intent = new Intent(this, CalendarActivity.class);
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

        //texts
        TextView msgPoprNegTV, msgPoprPosTV, msgPoprMsgTV;
        msgPoprNegTV = (TextView) dialog.findViewById(R.id.msgPoprNegTVId);
        msgPoprPosTV = (TextView) dialog.findViewById(R.id.msgPoprPosTVId);
        msgPoprMsgTV = (TextView) dialog.findViewById(R.id.msgPoprMsgTVId);

        //set fonts
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

        msgPoprNegTV.setTypeface(robotoCondensedLightFont);
        msgPoprPosTV.setTypeface(robotoCondensedLightFont);
        msgPoprMsgTV.setTypeface(robotoCondensedLightFont);
    }

    public void showDatePicker(View view){
        Log.i(CLASS_NAME, "Calling Date Picker");
        showDialog(999);
    }

    public void onBackClick(View view){
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
        finish();
    }

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
            String dateStr = String.valueOf(schedTransferAddUpdDateTV.getText());

            SimpleDateFormat wrongSdf = new SimpleDateFormat("d MMM ''yy");
            SimpleDateFormat rightSdf = new SimpleDateFormat("dd-MM-yyyy");

            try{
                dateStr = rightSdf.format(wrongSdf.parse(dateStr));
            }
            catch(ParseException e){
                Log.e(CLASS_NAME, "Error !!"+e);
            }

            String selectedDateStrArr[] = dateStr.split("-");

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

                SimpleDateFormat  rightSdf= new SimpleDateFormat("d MMM ''yy");
                SimpleDateFormat wrongSdf = new SimpleDateFormat("dd-MM-yyyy");

                //update the date in the header
                try{
                    schedTransferAddUpdDateTV.setText(rightSdf.format(wrongSdf.parseObject(day+"-"+month+"-"+year)));
                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "Error !! "+e);
                }

            }
        };
    }
    //---------------------------------------Date Picker ends--------------------------------------------

    //---------------------------------Edit Text type Listener-----------------------------------
    TextWatcher fieldTextWatcher;
    {
        fieldTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(!schedTransferAddUpdAmtET.getText().toString().trim().isEmpty()){
                    if("DISCARD".equalsIgnoreCase(schedTransferAddUpdBackImg.getTag().toString())){
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
                            schedTransferAddUpdBackImg.setBackgroundResource(R.drawable.cancel);
                            schedTransferAddUpdBackImg.setTag("DISCARD");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    schedTransferAddUpdBackImg.startAnimation(rotateAnim);
                }
                else{
                    if("BACK".equalsIgnoreCase(schedTransferAddUpdBackImg.getTag().toString())){
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
                            schedTransferAddUpdBackImg.setBackground(schedTransferAddUpdBackImg.getResources().getDrawable(R.drawable.back));
                            schedTransferAddUpdBackImg.setTag("BACK");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    schedTransferAddUpdBackImg.startAnimation(rotateAnim);
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

    //--------------------------------Linear Layout click listener--------------------------------------------------
    private LinearLayout.OnClickListener linearLayoutClickListener;
    {
        linearLayoutClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(CLASS_NAME, "Linear Layout Click is working !! There's hope :) by the way you clicked:"+ v.getId());

                Intent intent = null;

                switch(v.getId()){
                    case R.id.msgPoprPosLLId :      intent = new Intent(AddUpdateScheduleTransferActivity.this, CalendarActivity.class);
                        break;
                    case R.id.msgPoprNegLLId :      break;

                    default:intent = new Intent(AddUpdateScheduleTransferActivity.this, JimBrokeItActivity.class); break;
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


}

