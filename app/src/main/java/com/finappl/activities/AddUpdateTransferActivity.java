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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.adapters.AddUpdateTransactionSpinnerAdapter;
import com.finappl.R;
import com.finappl.dbServices.AddUpdateTransactionsDbService;
import com.finappl.dbServices.AddUpdateTransfersDbService;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.SpinnerModel;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.models.UsersModel;
import com.finappl.utils.Constants;

import java.util.List;
import java.util.Map;

public class AddUpdateTransferActivity extends Activity {
	
	private final String CLASS_NAME = this.getClass().getName();

    private Context mContext = this;

    //db services
    private AddUpdateTransfersDbService addUpdateTransferDbService = new AddUpdateTransfersDbService(mContext);
    private AddUpdateTransactionsDbService addUpdateTransactionsDbService = new AddUpdateTransactionsDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UsersModel loggedInUserObj;

	//header
	private TextView addUpdtrnfrDayTV, addUpdTrnfrMonthTV, addUpdTrnfrYearTV, addUpdTrnfrSuperScriptTV;
    private ImageView addUpdTrnfrHdrBackIV;

    //page content
    private EditText addUpdTrnfrAmtET, addUpdTrnfrNoteET;
    private Spinner addUpdTrnfrFromAccSpn, addUpdTrnfrToAccSpn;

    //buttons
    private ImageView addUpdateDoneUpdateImg;
    private TextView addUpdTrnfrDoneTV;

    //transactionModel
    private TransferModel transferModelObj;

    //spinner lists from db
    private List<SpinnerModel> accList;

    //message popper
    private Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transfer_add_update);

        //get the Active user
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return;
        }

        //get transactionModel objet from intent
        getTransferObjFromIntent();

		//initialize UI components
        initUIComponents();

        //setUp header
        setUpHeader();

        //setUp page content
        setUpPage();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.addUpdTrnfrRLId), robotoCondensedLightFont);
	}

    private void setUpPage() {
        //set spinners content
        setUpSpinners();

        //set transfer
        if(transferModelObj.getTRNFR_AMT() != null){
            addUpdTrnfrAmtET.setText(String.valueOf(transferModelObj.getTRNFR_AMT()));
        }

        //select spinner by default
        if(transferModelObj.getFromAccName() != null){
            addUpdTrnfrFromAccSpn.setSelection(getSpinnerItemIndex(accList, transferModelObj.getFromAccName()));
            addUpdTrnfrDoneTV.setText("update");
        }
        //this means the user is in this page for a fresh new transfer...so select default as selected
        else{
            addUpdTrnfrFromAccSpn.setSelection(getSpinnerItemIndex(accList, Constants.DEFAULTS_ACCOUNTS_SELECT));
        }

        //select spinner by default
        if(transferModelObj.getToAccName() != null){
            addUpdTrnfrToAccSpn.setSelection(getSpinnerItemIndex(accList, transferModelObj.getToAccName()));
        }
        //this means the user is in this page for a fresh new transfer...so select default as selected
        else{
            addUpdTrnfrToAccSpn.setSelection(getSpinnerItemIndex(accList, Constants.DEFAULTS_ACCOUNTS_SELECT));
        }

        //set notes
        if(transferModelObj.getTRNFR_NOTE() != null){
            addUpdTrnfrNoteET.setText(transferModelObj.getTRNFR_NOTE());
        }
    }

    private void setUpSpinners() {
        //get spinner lists from db
        accList = addUpdateTransactionsDbService.getAllAccounts(loggedInUserObj.getUSER_ID());

        addUpdTrnfrFromAccSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, accList));
        addUpdTrnfrToAccSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, accList));
    }

    private void getTransferObjFromIntent() {
        if(getIntent().getExtras().containsKey("TRANSFER_OBJ")){
            transferModelObj = (TransferModel)getIntent().getSerializableExtra("TRANSFER_OBJ");
        }
        else{
            Log.e(CLASS_NAME, "Error babay !! Intent is expected to contain TRANSFER_OBJ..but could not find it");
        }
    }

    private void setUpHeader() {
        String[] dateStrArr = transferModelObj.getTRNFR_DATE().split("-");
        int date = Integer.parseInt(dateStrArr[0]);

        addUpdtrnfrDayTV.setText(String.valueOf(date));
        addUpdTrnfrSuperScriptTV.setText(Constants.DATE_SUPERSCRIPT_ARRAY[date]);
        addUpdTrnfrMonthTV.setText(Constants.MONTHS_ARRAY[Integer.parseInt(dateStrArr[1])-1]);
        addUpdTrnfrYearTV.setText("'"+dateStrArr[2].substring(2));
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
        addUpdtrnfrDayTV = (TextView) this.findViewById(R.id.addUpdtrnfrDayTVId);
        addUpdTrnfrMonthTV = (TextView) this.findViewById(R.id.addUpdTrnfrMonthTVId);
        addUpdTrnfrYearTV = (TextView) this.findViewById(R.id.addUpdTrnfrYearTVId);
        addUpdTrnfrSuperScriptTV = (TextView) this.findViewById(R.id.addUpdTrnfrSuperScriptTVId);
        addUpdTrnfrHdrBackIV = (ImageView) this.findViewById(R.id.addUpdTrnfrHdrBackIVId);

        //the page
        addUpdTrnfrAmtET = (EditText) this.findViewById(R.id.addUpdTrnfrAmtETId);
        addUpdTrnfrNoteET = (EditText) this.findViewById(R.id.addUpdTrnfrNoteETId);
        addUpdTrnfrFromAccSpn = (Spinner) this.findViewById(R.id.addUpdTrnfrFromAccSpnId);
        addUpdTrnfrToAccSpn = (Spinner) this.findViewById(R.id.addUpdTrnfrToAccSpnId);

        //text watcher
        addUpdTrnfrAmtET.addTextChangedListener(fieldTextWatcher);

        //buttons
        addUpdTrnfrDoneTV = (TextView) this.findViewById(R.id.addUpdTrnfrDoneTVId);
    }

    public TransferModel getInputs(){
        String yearStr = "20"+addUpdTrnfrYearTV.getText().toString().replace("'","");
        String monthStr = addUpdTrnfrMonthTV.getText().toString();
        //convert jan to 01, feb to 02 etc
        monthStr = getMonthAsInteger(monthStr);

        String dayStr = addUpdtrnfrDayTV.getText().toString();

        if(dayStr != null && dayStr.trim().length() == 1){
            dayStr = "0"+dayStr;
        }

        String tranAmtStr = addUpdTrnfrAmtET.getText().toString();
        String frmAccIdStr = addUpdTrnfrFromAccSpn.getSelectedView().getTag().toString();
        String toAccIdStr = addUpdTrnfrToAccSpn.getSelectedView().getTag().toString();
        String noteStr = addUpdTrnfrNoteET.getText().toString();

        //validations
        if("".equalsIgnoreCase(tranAmtStr.trim())){
            showToast("Enter Amount to Transfer !");
            addUpdTrnfrAmtET.requestFocus();
            return null;
        }
        else if(Double.parseDouble(tranAmtStr) <= 0){
            showToast("Amount cannot be Zero !");
            addUpdTrnfrAmtET.requestFocus();
            return null;
        }

        TransferModel transferModel = new TransferModel();

        transferModel.setTRNFR_DATE(dayStr+"-"+monthStr+"-"+yearStr);
        transferModel.setTRNFR_AMT(Double.parseDouble(tranAmtStr));
        transferModel.setACC_ID_FRM(frmAccIdStr);
        transferModel.setACC_ID_TO(toAccIdStr);
        transferModel.setTRNFR_NOTE(noteStr);
        transferModel.setTRNFR_ID(transferModelObj.getTRNFR_ID());

        return transferModel;
    }

	//on click of done/update default_button
	public void onDoneUpdate(View view){
        if(!addNewTransfer().isEmpty()) {
            Intent intent = new Intent(AddUpdateTransferActivity.this, CalendarActivity.class);
            startActivity(intent);
        }
	}

    //on click of done+discard
    public void onDonePlusNew(View view){
        TransactionModel newTranObj = new TransactionModel();
        newTranObj.setTRAN_DATE(addNewTransfer());
        getIntent().putExtra("TRANSFER_OBJ", newTranObj);

        //calling the same activity
        startActivity(getIntent());
        finish();
    }

    public String addNewTransfer(){
        //get inputs
        TransferModel transferModel = getInputs();

        //add user id
        transferModel.setUSER_ID(loggedInUserObj.getUSER_ID());

        if(transferModel == null){
            showToast("Enter Amount !");
            return "";
        }
        else if(transferModel.getACC_ID_FRM().equalsIgnoreCase(transferModel.getACC_ID_TO())){
            showToast("Transfer cannot be done between same Accounts !");
            return "";
        }

        //get done or update from default_button
        String doneDiscardStr = addUpdTrnfrDoneTV.getText().toString();

        //its an old expense..so update
        if("update".equalsIgnoreCase(doneDiscardStr)){
            long result = addUpdateTransferDbService.updateOldTransfer(transferModel);

            if(result == 0) {
                showToast("Failed to update Transfer/Account !");
            } else if(result == 1){
                showToast("Transferred Successfully");
            }
            else{
                showToast("Unknown error !");
            }
        }
        else if("done".equalsIgnoreCase(doneDiscardStr)){
            long result = addUpdateTransferDbService.addNewTransfer(transferModel);

            if(result == -1) {
                showToast("Failed to Transfer !");
            } else if(result == 0) {
                showToast("Account Update Failed !");
            }
            else{
                showToast("Transfer Complete ! !");
            }
        }

        return transferModel.getTRNFR_DATE();
    }

    public void showMessagePopper(View view){
        if("BACK".equalsIgnoreCase(addUpdTrnfrHdrBackIV.getTag().toString())){
            Intent intent = new Intent(AddUpdateTransferActivity.this, CalendarActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Create custom message popper object
        dialog = new Dialog(AddUpdateTransferActivity.this);
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
        Log.i(CLASS_NAME, "Working very hard to call date picker to work");
        showDialog(999);
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

    //on click of discard default_button
	public void onDiscard(View view)
	{
		//discard the screen and go to calendar page
    	Intent intent = new Intent(AddUpdateTransferActivity.this,CalendarActivity.class);
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

    //---------------------------------Edit Text type Listener-----------------------------------
    TextWatcher fieldTextWatcher;
    {
        fieldTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //restrict the user to enter only 2 decimal inputs
                if(!addUpdTrnfrAmtET.getText().toString().trim().isEmpty()){
                    if("DISCARD".equalsIgnoreCase(addUpdTrnfrHdrBackIV.getTag().toString())){
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
                            addUpdTrnfrHdrBackIV.setBackground(addUpdTrnfrHdrBackIV.getResources().getDrawable(R.drawable.cancel));
                            addUpdTrnfrHdrBackIV.setTag("DISCARD");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addUpdTrnfrHdrBackIV.startAnimation(rotateAnim);
                }
                else{
                    if("BACK".equalsIgnoreCase(addUpdTrnfrHdrBackIV.getTag().toString())){
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
                            addUpdTrnfrHdrBackIV.setBackground(addUpdTrnfrHdrBackIV.getResources().getDrawable(R.drawable.back));
                            addUpdTrnfrHdrBackIV.setTag("BACK");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addUpdTrnfrHdrBackIV.startAnimation(rotateAnim);
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
            String selectedDateStrArr[] = transferModelObj.getTRNFR_DATE().split("-");

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
                transferModelObj.setTRNFR_DATE(dateStr+"-"+monthStr+"-"+year);

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
                    case R.id.msgPoprPosLLId :      intent = new Intent(AddUpdateTransferActivity.this, CalendarActivity.class);
                                                    break;
                    case R.id.msgPoprNegLLId :      break;

                    default:intent = new Intent(AddUpdateTransferActivity.this, JimBrokeItActivity.class); break;
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

