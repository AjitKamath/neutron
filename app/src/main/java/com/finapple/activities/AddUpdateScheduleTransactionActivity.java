package com.finapple.activities;

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

import com.finapple.R;
import com.finapple.adapters.AddUpdateTransactionSpinnerAdapter;
import com.finapple.dbServices.AddUpdateTransactionsDbService;
import com.finapple.dbServices.AuthorizationDbService;
import com.finapple.dbServices.ScheduledTransactionsDbService;
import com.finapple.model.ScheduledTransactionModel;
import com.finapple.model.SpinnerModel;
import com.finapple.model.UsersModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class AddUpdateScheduleTransactionActivity extends Activity {
	private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    //User
    private UsersModel loggedInUserObj;

    //db service
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);
    private AddUpdateTransactionsDbService addUpdateTransactionsDbService = new AddUpdateTransactionsDbService(mContext);
    private ScheduledTransactionsDbService scheduledTransactionsDbService = new ScheduledTransactionsDbService(mContext);

    //UI Components
    private ImageView schedTranAddUpdBackImg;
    private TextView schedTranAddUpdDateTV;

    private TextView schedTranAddUpdNameET;
    private TextView schedTranAddUpdAmtET;

    private Spinner schedTranAddUpdCatSpn;
    private Spinner schedTranAddUpdAccSpn;
    private Spinner schedTranAddUpdSpntOnSpn;

    private RadioGroup schedTranAddUpdExpIncRadioGrp;
    private RadioButton schedTranAddUpdExpRadio;
    private RadioButton schedTranAddUpdIncRadio;

    private RadioGroup schedTranSchedRadioGrp;
    private RadioButton schedTranOnceAddRadio;
    private RadioButton schedTranDailyAddRadio;
    private RadioButton schedTranWeeklyAddRadio;
    private RadioButton schedTranMonthlyAddRadio;
    private RadioButton schedTranYearlyAddRadio;

    private RadioGroup schedTranAutoRadioGrp;
    private RadioButton schedTranAutoAddRadio;
    private RadioButton schedTranAutoAddNotifyRadio;

    private EditText schTranAddUpdNoteET;

    //spinner lists from db
    private List<SpinnerModel> categoryList, accountList, spentOnList;

    private ScheduledTransactionModel scheduledTransactionModelObj;

    //message popper
    private Dialog dialog;

    @Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_transaction_add_update);
        Log.e(CLASS_NAME, "Navigated to Schedule Transaction Add Update Screen");

        //get the Active user
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return;
        }

        //init ui components
        initUiComponents();

        //set up spinners
        setUpSpinners();

        getScheduledTransactionObjFromIntent();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.schedTranAddUpdRLId), robotoCondensedLightFont);
    }

    private void setUpSpinners() {
        //get spinner lists from db
        categoryList = addUpdateTransactionsDbService.getAllCategories(loggedInUserObj.getUSER_ID());
        accountList = addUpdateTransactionsDbService.getAllAccounts(loggedInUserObj.getUSER_ID());
        spentOnList = addUpdateTransactionsDbService.getAllSpentOn(loggedInUserObj.getUSER_ID());

        //set Up categories spinner
        schedTranAddUpdCatSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, categoryList));

        //set up accounts spinner
        schedTranAddUpdAccSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, accountList));

        //set up pay type spinner
        schedTranAddUpdSpntOnSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, spentOnList));
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

    private void getScheduledTransactionObjFromIntent() {
        if(getIntent().getSerializableExtra("SCHEDULED_TRANSACTION_OBJ") != null){
            scheduledTransactionModelObj = (ScheduledTransactionModel)getIntent().getSerializableExtra("SCHEDULED_TRANSACTION_OBJ");

            if(scheduledTransactionModelObj != null && scheduledTransactionModelObj.getSCH_TRAN_ID() != null
                    && !scheduledTransactionModelObj.getSCH_TRAN_ID().isEmpty()){
                //fill user id & scheduled transaction id in the ScheduleTransactionModel object and pass it to db to get the Scheduled Transaction Data
                scheduledTransactionModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());

                scheduledTransactionModelObj = scheduledTransactionsDbService.getScheduledTransactionOnScheduledTransactionId(scheduledTransactionModelObj);

                //popullate UI with the returned data
                popullateUiWhenUpdate();
            }
            else if(scheduledTransactionModelObj != null && scheduledTransactionModelObj.getSCH_TRAN_DATE() != null
                    && !scheduledTransactionModelObj.getSCH_TRAN_DATE().isEmpty()){
                Log.e(CLASS_NAME, "Could not find SCH_TRAN_DATE in SCHEDULED_TRANSACTION_OBJ in the intent... This means user is trying to create a new Scheduled Transaction");
                popullateUiWhenNew();
            }
        }
        else{
            Log.e(CLASS_NAME, "Could not find SCHEDULED_TRANSACTION_OBJ in the intent... ");
        }
    }

    private void popullateUiWhenNew() {
        try{
            SimpleDateFormat  rightSdf= new SimpleDateFormat("d MMM ''yy");
            SimpleDateFormat wrongSdf = new SimpleDateFormat("dd-MM-yyyy");

            schedTranAddUpdDateTV.setText(rightSdf.format(wrongSdf.parse(scheduledTransactionModelObj.getSCH_TRAN_DATE())));
        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "Error !!"+e);
        }
    }

    private void popullateUiWhenUpdate(){
        schedTranAddUpdNameET.setText(scheduledTransactionModelObj.getSCH_TRAN_NAME());
        schedTranAddUpdAmtET.setText(String.valueOf(scheduledTransactionModelObj.getSCH_TRAN_AMT()));

        schedTranAddUpdCatSpn.setSelection(getSpinnerItemIndex(categoryList, scheduledTransactionModelObj.getCategoryNameStr()));
        schedTranAddUpdAccSpn.setSelection(getSpinnerItemIndex(accountList, scheduledTransactionModelObj.getAccountNameStr()));;
        schedTranAddUpdSpntOnSpn.setSelection(getSpinnerItemIndex(spentOnList, scheduledTransactionModelObj.getCategoryNameStr()));;

        //expense type radio
        if("EXPENSE".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_TYPE())){
            schedTranAddUpdExpRadio.setSelected(true);
        }
        else{
            schedTranAddUpdIncRadio.setSelected(true);
        }

        //schedule type radio
        if("ONCE".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_FREQ())){
            schedTranOnceAddRadio.setSelected(true);
        }
        else if("DAILY".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_FREQ())){
            schedTranDailyAddRadio.setSelected(true);
        }
        else if("WEEKLY".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_FREQ())){
            schedTranWeeklyAddRadio.setSelected(true);
        }
        if("MONTHLY".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_FREQ())){
            schedTranMonthlyAddRadio.setSelected(true);
        }
        else{
            schedTranYearlyAddRadio.setSelected(true);
        }

        //notes
        schTranAddUpdNoteET.setText(scheduledTransactionModelObj.getSCH_TRAN_NOTE());
    }

    private void initUiComponents() {
        schedTranAddUpdBackImg = (ImageView) this.findViewById(R.id.schedTranAddUpdBackImgId);
        schedTranAddUpdDateTV = (TextView) this.findViewById(R.id.schedTranAddUpdDateTVId);

        schedTranAddUpdNameET = (TextView) this.findViewById(R.id.schedTranAddUpdNameETId);
        schedTranAddUpdAmtET = (TextView) this.findViewById(R.id.schedTranAddUpdAmtETId);

        //text watchers
        schedTranAddUpdNameET.addTextChangedListener(fieldTextWatcher);
        schedTranAddUpdAmtET.addTextChangedListener(fieldTextWatcher);

        schedTranAddUpdCatSpn = (Spinner) this.findViewById(R.id.schedTranAddUpdCatSpnId);
        schedTranAddUpdAccSpn = (Spinner) this.findViewById(R.id.schedTranAddUpdAccSpnId);
        schedTranAddUpdSpntOnSpn = (Spinner) this.findViewById(R.id.schedTranAddUpdSpntOnSpnId);

        schedTranAddUpdExpIncRadioGrp = (RadioGroup) this.findViewById(R.id.schedTranAddUpdExpIncRadioGrpId);
        schedTranAddUpdExpRadio = (RadioButton) this.findViewById(R.id.schedTranAddUpdExpRadioId);
        schedTranAddUpdIncRadio = (RadioButton) this.findViewById(R.id.schedTranAddUpdIncRadioId);

        schedTranSchedRadioGrp = (RadioGroup) this.findViewById(R.id.schedTranSchedRadioGrpId);
        schedTranOnceAddRadio = (RadioButton) this.findViewById(R.id.schedTranOnceAddRadioId);
        schedTranDailyAddRadio = (RadioButton) this.findViewById(R.id.schedTranDailyAddRadioId);
        schedTranWeeklyAddRadio = (RadioButton) this.findViewById(R.id.schedTranWeeklyAddRadioId);
        schedTranMonthlyAddRadio = (RadioButton) this.findViewById(R.id.schedTranMonthlyAddRadioId);
        schedTranYearlyAddRadio = (RadioButton) this.findViewById(R.id.schedTranYearlyAddRadioId);

        schedTranAutoRadioGrp = (RadioGroup) this.findViewById(R.id.schedTranAutoRadioGrpId);
        schedTranAutoAddRadio = (RadioButton) this.findViewById(R.id.schedTranAutoAddRadioId);
        schedTranAutoAddNotifyRadio = (RadioButton) this.findViewById(R.id.schedTranAutoAddNotifyRadioId);

        schTranAddUpdNoteET = (EditText) this.findViewById(R.id.schTranAddUpdNoteETId);
    }

    private ScheduledTransactionModel getUserInputs(){
        SimpleDateFormat wrongSdf = new SimpleDateFormat("d MMM ''yy");
        SimpleDateFormat rightSdf = new SimpleDateFormat("dd-MM-yyyy");

        String dateStr = null;

        try{
            dateStr = rightSdf.format(wrongSdf.parse(String.valueOf(schedTranAddUpdDateTV.getText())));
        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "ERROR !!"+e);
            return null;
        }

        String amountStr = String.valueOf(schedTranAddUpdAmtET.getText());

        if(amountStr.isEmpty()){
            amountStr = "0";
        }

        scheduledTransactionModelObj.setSCH_TRAN_DATE(dateStr);

        scheduledTransactionModelObj.setSCH_TRAN_NAME(String.valueOf(schedTranAddUpdNameET.getText()));
        scheduledTransactionModelObj.setSCH_TRAN_AMT(Double.parseDouble(amountStr));

        scheduledTransactionModelObj.setSCH_TRAN_CAT_ID(String.valueOf(schedTranAddUpdCatSpn.getSelectedView().getTag()));
        scheduledTransactionModelObj.setSCH_TRAN_ACC_ID(String.valueOf(schedTranAddUpdAccSpn.getSelectedView().getTag()));
        scheduledTransactionModelObj.setSCH_TRAN_SPNT_ON_ID(String.valueOf(schedTranAddUpdSpntOnSpn.getSelectedView().getTag()));

        scheduledTransactionModelObj.setSCH_TRAN_TYPE(String.valueOf(this.findViewById(schedTranAddUpdExpIncRadioGrp.getCheckedRadioButtonId()).getTag()));
        scheduledTransactionModelObj.setSCH_TRAN_FREQ(String.valueOf(this.findViewById(schedTranSchedRadioGrp.getCheckedRadioButtonId()).getTag()));
        scheduledTransactionModelObj.setSCH_TRAN_AUTO(String.valueOf(this.findViewById(schedTranAutoRadioGrp.getCheckedRadioButtonId()).getTag()));

        scheduledTransactionModelObj.setSCH_TRAN_NOTE(String.valueOf(schTranAddUpdNoteET.getText()));

        return scheduledTransactionModelObj;
    }

    public void onDoneUpdate(View view){
        scheduledTransactionModelObj = getUserInputs();

        if(scheduledTransactionModelObj == null){
            return;
        }

        //set user id
        scheduledTransactionModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());

        if(scheduledTransactionModelObj.getSCH_TRAN_ID() != null && !scheduledTransactionModelObj.getSCH_TRAN_ID().isEmpty()){
            Log.i(CLASS_NAME, "Schedule Transaction Id is found... This means user is trying to update the Scheduled Transaction");
            //TODO: update yet to be implemented
        }
        else{
            Log.i(CLASS_NAME, "Schedule Transaction Id is not found... This means user is trying to create a new Scheduled Transaction");


            Long result = scheduledTransactionsDbService.createNewScheduledTransaction(scheduledTransactionModelObj);
            if(result != -1){
                showToast("New Scheduled Transaction Created");

                //navigate back to calendar screen
                Intent intent = new Intent(this, CalendarActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                showToast("Error !! Could not create a new Scheduled Transaction");
            }
        }
    }

    public void onDonePlusNew(View view){
        onDoneUpdate(view);

        SimpleDateFormat wrongSdf = new SimpleDateFormat("d MMM ''yy");
        SimpleDateFormat rightSdf = new SimpleDateFormat("dd-MM-yyyy");

        String dateStr = null;

        try{
            dateStr = rightSdf.format(wrongSdf.parse(String.valueOf(schedTranAddUpdDateTV.getText())));
        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "ERROR !!"+e);
            return;
        }

        ScheduledTransactionModel scheduledTransactionModelObj = new ScheduledTransactionModel();
        scheduledTransactionModelObj.setSCH_TRAN_DATE(dateStr);

        startActivity(getIntent());
        finish();
    }

    public void showMessagePopper(View view){
        if("BACK".equalsIgnoreCase(schedTranAddUpdBackImg.getTag().toString())){
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
            String dateStr = String.valueOf(schedTranAddUpdDateTV.getText());

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
                    schedTranAddUpdDateTV.setText(rightSdf.format(wrongSdf.parseObject(day+"-"+month+"-"+year)));
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
                //restrict the user to enter only 2 decimal inputs
                if(!schedTranAddUpdNameET.getText().toString().trim().isEmpty() || !schedTranAddUpdAmtET.getText().toString().trim().isEmpty()){
                    if("DISCARD".equalsIgnoreCase(schedTranAddUpdBackImg.getTag().toString())){
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
                            schedTranAddUpdBackImg.setBackgroundResource(R.drawable.cancel);
                            schedTranAddUpdBackImg.setTag("DISCARD");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    schedTranAddUpdBackImg.startAnimation(rotateAnim);
                }
                else{
                    if("BACK".equalsIgnoreCase(schedTranAddUpdBackImg.getTag().toString())){
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
                            schedTranAddUpdBackImg.setBackground(schedTranAddUpdBackImg.getResources().getDrawable(R.drawable.back));
                            schedTranAddUpdBackImg.setTag("BACK");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    schedTranAddUpdBackImg.startAnimation(rotateAnim);
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
                    case R.id.msgPoprPosLLId :      intent = new Intent(AddUpdateScheduleTransactionActivity.this, CalendarActivity.class);
                        break;
                    case R.id.msgPoprNegLLId :      break;

                    default:intent = new Intent(AddUpdateScheduleTransactionActivity.this, JimBrokeItActivity.class); break;
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

