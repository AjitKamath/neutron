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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.adapters.AddUpdateTransactionSpinnerAdapter;
import com.finappl.R;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.SpinnerModel;
import com.finappl.models.TransactionModel;
import com.finappl.models.UserMO;
import com.finappl.models.UsersModel;
import com.finappl.utils.Constants;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.IdGenerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.finappl.utils.Constants.*;

public class AddUpdateTransactionActivity extends Activity {
	
	private final String CLASS_NAME = this.getClass().getName();

    private Context mContext = this;

    //db services
    private TransactionsDbService addUpdateTransactionsDbService = new TransactionsDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UserMO loggedInUserObj;

	//header
	private TextView addUpdateYearTV, addUpdateMonthTV, addUpdateSuperScriptTV, addUpdateDayTV, doneDiscardTV;
    private ImageView addUpdateTranBackImg;

    //page content
    private EditText addUpdateTranNameET, addUpdateTranAmtET, addUpdateNoteET;
    private Spinner addUpdateCatSpn, addUpdateAccSpn, addUpdateSpntOnSpn;
    private RadioGroup addUpdateTranExpIncRadioGrp;
    private RadioButton addUpdateTranIncRadio, addUpdateTranExpRadio;
    private ImageButton addUpdatePageFabIB;

    //transactionModel
    private TransactionModel transactionModelObj;

    //spinner lists from db
    private List<SpinnerModel> categoryList, accountList, spentOnList;

    //message popper
    private Dialog dialog;


	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction);

        //get the Active user
        loggedInUserObj = authorizationDbService.getActiveUser(FinappleUtility.getInstance().getActiveUserId(mContext));
        if(loggedInUserObj == null){
            return;
        }

        //get transactionModel objet from intent
        getTransactionObjFromIntent();

		//initialize UI components
        initUIComponents();

        //setUp header
        setUpHeader();

        //setUp page content
        setUpPage();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.addUpdateTransactionRLId), robotoCondensedLightFont);
	}

    private void setUpPage() {
        //set spinners content
        setUpSpinners();

        //set transaction name
        if(transactionModelObj.getTRAN_NAME() != null){
            addUpdateTranNameET.setText(transactionModelObj.getTRAN_NAME());

            addUpdatePageFabIB.setImageResource(R.drawable.save_white_small);
            addUpdatePageFabIB.setTag("UPDATE");
        }
        //this means the user is in this page for a fresh new transaction...so give him focus on transaction name on arrival
        else{
            //give transaction Amt focus by default
            addUpdateTranAmtET.requestFocus();
        }

        //set transaction
        if(transactionModelObj.getTRAN_AMT() != null){
            addUpdateTranAmtET.setText(String.valueOf(transactionModelObj.getTRAN_AMT()));
        }

        //select categories spinner by default
        if(transactionModelObj.getCategory() != null){
            addUpdateCatSpn.setSelection(getSpinnerItemIndex(categoryList, transactionModelObj.getCategory()));
        }
        //this means the user is in this page for a fresh new transaction...so select default as selected
        else{
            //addUpdateCatSpn.setSelection(getSpinnerItemIndex(categoryList, Constants.DEFAULTS_CATEGORIES_SELECT));
        }

        //select accounts spinner by default
        if(transactionModelObj.getAccount() != null){
            addUpdateAccSpn.setSelection(getSpinnerItemIndex(accountList, transactionModelObj.getAccount()));
        }
        //this means the user is in this page for a fresh new transaction...so select default as selected
        else{
            //addUpdateAccSpn.setSelection(getSpinnerItemIndex(accountList, Constants.DEFAULTS_ACCOUNTS_SELECT));
        }

        //select spent on spinner by default
        if(transactionModelObj.getSpentOn() != null){
            addUpdateSpntOnSpn.setSelection(getSpinnerItemIndex(spentOnList, transactionModelObj.getSpentOn()));
        }
        //this means the user is in this page for a fresh new transaction...so select default as selected
        else{
            //addUpdateSpntOnSpn.setSelection(getSpinnerItemIndex(spentOnList, Constants.DEFAULTS_SPENTON_SELECT));
        }

        //select transaction type radio default_button
        if(transactionModelObj.getTRAN_TYPE() != null){
            if("INCOME".equalsIgnoreCase(transactionModelObj.getTRAN_TYPE())){
                addUpdateTranIncRadio.setChecked(true);
            }
            else{
                addUpdateTranExpRadio.setChecked(true);
            }
        }
        //pre select expense if its a fresh new transaction
        else{
            addUpdateTranExpRadio.setChecked(true);
        }

        //set notes
        addUpdateNoteET.setText(transactionModelObj.getTRAN_NOTE());
    }

    private void setUpSpinners() {
        //get spinner lists from db
        categoryList = addUpdateTransactionsDbService.getAllCategories(loggedInUserObj.getUSER_ID());
        accountList = addUpdateTransactionsDbService.getAllAccounts(loggedInUserObj.getUSER_ID());
        spentOnList = addUpdateTransactionsDbService.getAllSpentOn(loggedInUserObj.getUSER_ID());

        //set Up categories spinner
        addUpdateCatSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, categoryList));

        //set up accounts spinner
        addUpdateAccSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, accountList));

        //set up pay type spinner
        addUpdateSpntOnSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, spentOnList));
    }

    private void getTransactionObjFromIntent() {
        if(getIntent().getExtras().containsKey("TRANSACTION_OBJ")){
            transactionModelObj = (TransactionModel)getIntent().getSerializableExtra("TRANSACTION_OBJ");
        }
        else{
            Log.e(CLASS_NAME, "Error babay !! Intent is expected to contain TRANSACTION_OBJ..but could not find it");
        }
    }

    public void showDatePicker(View view){
        Log.i(CLASS_NAME, "Working very hard to call date picker to work");
        showDialog(999);
    }

    private void setUpHeader() {
        SimpleDateFormat sdf = new SimpleDateFormat(JAVA_DATE_FORMAT);
        String[] dateStrArr = sdf.format(transactionModelObj.getTRAN_DATE()).split("-");
        int date = Integer.parseInt(dateStrArr[0]);

        addUpdateDayTV.setText(String.valueOf(date));
        addUpdateSuperScriptTV.setText(Constants.DATE_SUPERSCRIPT_ARRAY[date]);
        addUpdateMonthTV.setText(Constants.MONTHS_ARRAY[Integer.parseInt(dateStrArr[1]) - 1]);
        addUpdateYearTV.setText("'" + dateStrArr[2].substring(2));
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

    private void initUIComponents() {
        //header
        /*addUpdateYearTV = (TextView) this.findViewById(R.id.addUpdateYearTVId);
        addUpdateMonthTV = (TextView) this.findViewById(R.id.addUpdateMonthTVId);
        addUpdateSuperScriptTV = (TextView) this.findViewById(R.id.addUpdateSuperScriptTVId);
        addUpdateDayTV = (TextView) this.findViewById(R.id.addUpdateDayTVId);
        addUpdateTranBackImg = (ImageView) this.findViewById(R.id.addUpdateTranBackImgId);*/

        //page content
        addUpdateTranNameET = (EditText) this.findViewById(R.id.addUpdateTranNameETId);
        addUpdateTranAmtET = (EditText) this.findViewById(R.id.addUpdateTranAmtETId);
        addUpdateCatSpn = (Spinner) this.findViewById(R.id.addUpdateCatSpnId);
        addUpdateAccSpn = (Spinner) this.findViewById(R.id.addUpdateAccSpnId);
        addUpdateTranExpRadio = (RadioButton) this.findViewById(R.id.addUpdateTranExpRadioId);
        addUpdateTranIncRadio = (RadioButton) this.findViewById(R.id.addUpdateTranIncRadioId);
        addUpdateTranExpIncRadioGrp = (RadioGroup) this.findViewById(R.id.addUpdateTranExpIncRadioGrpId);
        addUpdateNoteET = (EditText) this.findViewById(R.id.addUpdateNoteETId);
        doneDiscardTV = (TextView) this.findViewById(R.id.addUpdateDoneTVId);

        //addUpdatePageFabIB = (ImageButton) this.findViewById(R.id.addUpdatePageFabIBId);

        //focus listener for title and amt
        addUpdateTranNameET.addTextChangedListener(fieldTextWatcher);
        addUpdateTranAmtET.addTextChangedListener(fieldTextWatcher);
    }

    public TransactionModel getInputs(){
        String yearStr = "20"+addUpdateYearTV.getText().toString().replace("'","");
        String monthStr = addUpdateMonthTV.getText().toString();
        //convert jan to 01, feb to 02 etc
        monthStr = getMonthAsInteger(monthStr);

        String dayStr = addUpdateDayTV.getText().toString();

        if(dayStr != null && dayStr.trim().length() == 1){
            dayStr = "0"+dayStr;
        }

        String tranNameStr = addUpdateTranNameET.getText().toString();
        String tranAmtStr = addUpdateTranAmtET.getText().toString();
        String catIdStr = addUpdateCatSpn.getSelectedView().getTag().toString();
        String categoryNameStr = ((SpinnerModel)addUpdateCatSpn.getSelectedItem()).getItemName();
        String accIdStr = addUpdateAccSpn.getSelectedView().getTag().toString();
        String accNameStr = ((SpinnerModel)addUpdateAccSpn.getSelectedItem()).getItemName();
        String tranTypeStr = this.findViewById(addUpdateTranExpIncRadioGrp.getCheckedRadioButtonId()).getTag().toString();
        String spntOnIdStr = addUpdateSpntOnSpn.getSelectedView().getTag().toString();
        String spntOnNameStr = ((SpinnerModel)addUpdateSpntOnSpn.getSelectedItem()).getItemName();
        String noteStr = addUpdateNoteET.getText().toString();

        //validations
        if("".equalsIgnoreCase(tranNameStr.trim())){
            showToast("Oops ! Transaction name is required");
            //addUpdateTranNameET.requestFocus();
            return null;
        }

        if("".equalsIgnoreCase(tranAmtStr.trim())){
            showToast("enter amount !");
            addUpdateTranAmtET.requestFocus();
            return null;
        }
        else if(Double.parseDouble(tranAmtStr) <= 0){
            showToast("amount cannot be ZERO !");
            addUpdateTranAmtET.requestFocus();
            return null;
        }

        TransactionModel transactionModel = new TransactionModel();

        String dateStr = dayStr + "-" + monthStr + "-" + yearStr;
        SimpleDateFormat sdf = new SimpleDateFormat(JAVA_DATE_FORMAT);

        try{
            transactionModel.setTRAN_DATE(sdf.parse(dateStr));
        }
        catch (ParseException p){
            Log.e(CLASS_NAME, "Error !!"+p);
            return null;
        }

        transactionModel.setTRAN_NAME(tranNameStr);
        transactionModel.setTRAN_AMT(Double.parseDouble(tranAmtStr));
        transactionModel.setCAT_ID(catIdStr);
        transactionModel.setCategory(categoryNameStr);
        transactionModel.setTRAN_TYPE(tranTypeStr);
        transactionModel.setACC_ID(accIdStr);
        transactionModel.setAccount(accNameStr);
        transactionModel.setSPNT_ON_ID(spntOnIdStr);
        transactionModel.setSpentOn(spntOnNameStr);
        transactionModel.setTRAN_NOTE(noteStr);

        return transactionModel;
    }

	//on click of done/update default_button
	public void onDoneUpdate(View view){
        if(addTransaction() != null) {
            Intent intent = new Intent(AddUpdateTransactionActivity.this, CalendarActivity.class);
            startActivity(intent);
        }
	}

    //on click of done+new
    public void onDonePlusNew(View view){
        TransactionModel newTranObj = new TransactionModel();
        newTranObj.setTRAN_DATE(addTransaction());
        getIntent().putExtra("TRANSACTION_OBJ", newTranObj);

        //calling the same activity
        startActivity(getIntent());
        finish();
    }

    public void onDone(View view){
        //get inputs
        TransactionModel transactionModel = getInputs();

        //add user id
        transactionModel.setUSER_ID(loggedInUserObj.getUSER_ID());

        //its an old expense..so update
        if("UPDATE".equalsIgnoreCase(String.valueOf(addUpdatePageFabIB.getTag()))){
            //get the transaction id from intent and set it in the transaction object
            transactionModel.setTRAN_ID(transactionModelObj.getTRAN_ID());

            long result = addUpdateTransactionsDbService.updateOldTransaction(transactionModel);

            if(result == 0) {
                showToast("Failed to update Transaction/Account !");
            } else if(result == 1){
                showToast("Transaction updated");
            }
            else{
                showToast("Unknown error !");
            }
        }
        else if("ADD".equalsIgnoreCase(String.valueOf(addUpdatePageFabIB.getTag()))){
            long result = addUpdateTransactionsDbService.addNewTransaction(transactionModel);

            if(result == -1) {
                showToast("Failed to create a new Transaction !");
            }
            else{
                showToast("New Transaction created");
            }
        }
        else{
            Log.e(CLASS_NAME, "Expected SAVE/UPDATE as tag for the FAB. But found something else. This is an error case.");
        }

        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra("CALENDAR_ACTIVITY_ACTION", transactionModel);
        startActivity(intent);
        finish();
    }

    public Date addTransaction(){
        //get inputs
        TransactionModel transactionModel = getInputs();

        //add user id
        transactionModel.setUSER_ID(loggedInUserObj.getUSER_ID());

        //get done or update from default_button
        String doneDiscardStr = doneDiscardTV.getText().toString();

        //its an old expense..so update
        if("Update".equalsIgnoreCase(doneDiscardStr)){
            //get the transaction id from intent and set it in the transaction object
            transactionModel.setTRAN_ID(transactionModelObj.getTRAN_ID());

            long result = addUpdateTransactionsDbService.updateOldTransaction(transactionModel);

            if(result == 0) {
                showToast("Failed to update Transaction/Account !");
            } else if(result == 1){
                showToast("Transaction updated");
            }
            else{
                showToast("Unknown error !");
            }

        }
        else if("done".equalsIgnoreCase(doneDiscardStr)){
            transactionModel.setTRAN_ID(IdGenerator.getInstance().generateUniqueId("TRAN"));
            long result = addUpdateTransactionsDbService.addNewTransaction(transactionModel);

            if(result == -1) {
                showToast("Failed to create a new Transaction !");
            }
            else{
                showToast("New Transaction created");
            }
        }

        return transactionModel.getTRAN_DATE();
    }

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }


    private String getMonthAsInteger(String monthStr) {
        int monthArrSize = Constants.MONTHS_ARRAY.length;
        int index = 0;

        for(int i=0 ; i<monthArrSize; i++){
            if(Constants.MONTHS_ARRAY[i].equalsIgnoreCase(monthStr)){
                index = i+1;
                break;
            }
        }

        if(index<10){
            return "0"+index;
        }
        return String.valueOf(index);
    }

    public void showMessagePopper(View view){
        if("BACK".equalsIgnoreCase(addUpdateTranBackImg.getTag().toString())){
            Intent intent = new Intent(AddUpdateTransactionActivity.this, CalendarActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Create custom message popper object
        dialog = new Dialog(AddUpdateTransactionActivity.this);
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

    //---------------------------------Edit Text type Listener-----------------------------------
    TextWatcher fieldTextWatcher;
    {
        fieldTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //restrict the user to enter only 2 decimal inputs
                if(!addUpdateTranNameET.getText().toString().trim().isEmpty() || !addUpdateTranAmtET.getText().toString().trim().isEmpty()){
                    if("DISCARD".equalsIgnoreCase(addUpdateTranBackImg.getTag().toString())){
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
                            addUpdateTranBackImg.setBackground(addUpdateTranBackImg.getResources().getDrawable(R.drawable.cancel));
                            addUpdateTranBackImg.setTag("DISCARD");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addUpdateTranBackImg.startAnimation(rotateAnim);
                }
                else{
                    if("BACK".equalsIgnoreCase(addUpdateTranBackImg.getTag().toString())){
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
                            addUpdateTranBackImg.setBackgroundResource(R.drawable.cancel);
                            addUpdateTranBackImg.setTag("BACK");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addUpdateTranBackImg.startAnimation(rotateAnim);
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

    //---------------------------------------Date Picker-------------------------------------------------
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            SimpleDateFormat sdf = new SimpleDateFormat(JAVA_DATE_FORMAT);

            String selectedDateStrArr[] = sdf.format(transactionModelObj.getTRAN_DATE()).split("-");

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
                Log.i(CLASS_NAME, "Date picker says that u selected:"+ day + "-" + month + "-" + year);

                //change jan-0 to jan-1
                month++;

                Log.i(CLASS_NAME, "Date picker date translated to be:"+ day + "-" + month + "-" + year);

                String dateStr = String.valueOf(day);
                String monthStr = String.valueOf(month);

                if(dateStr.length() == 0){
                    dateStr = "0"+dateStr;
                }
                if(monthStr.length() == 0){
                    monthStr = "0"+monthStr;
                }

                //update object
                String tempDateStr = dateStr+"-"+monthStr+"-"+year;
                SimpleDateFormat sdf = new SimpleDateFormat(JAVA_DATE_FORMAT);

                try{
                    transactionModelObj.setTRAN_DATE(sdf.parse(tempDateStr));
                }
                catch (ParseException p){
                    Log.e(CLASS_NAME, "Error !!"+p);
                    return;
                }

                //update the header
                setUpHeader();
            }
        };
    }
    //---------------------------------------Date Picker ends--------------------------------------------

    //--------------------------------Linear Layout click listener--------------------------------------------------
    private LinearLayout.OnClickListener linearLayoutClickListener;
    {
        linearLayoutClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(CLASS_NAME, "Linear Layout Click is working !! There's hope :) by the way you clicked:"+ v.getId());

                Intent intent = null;

                switch(v.getId()){
                    case R.id.msgPoprPosLLId :      intent = new Intent(AddUpdateTransactionActivity.this, CalendarActivity.class);
                                                    break;
                    case R.id.msgPoprNegLLId :      break;

                    default:intent = new Intent(AddUpdateTransactionActivity.this, JimBrokeItActivity.class); break;
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

    @Override
    public void onBackPressed() {
        showMessagePopper(null);
    }
}

