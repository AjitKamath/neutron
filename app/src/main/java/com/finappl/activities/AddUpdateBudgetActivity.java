package com.finappl.activities;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.adapters.AddUpdateBudgetSpinnerAdapter;
import com.finappl.R;
import com.finappl.dbServices.AddUpdateBudgetsDbService;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.BudgetModel;
import com.finappl.models.SpinnerModel;
import com.finappl.models.UsersModel;

import java.util.List;
import java.util.Map;

public class AddUpdateBudgetActivity extends Activity {
	
	private final String CLASS_NAME = this.getClass().getName();

    private Context mContext = this;

    //header
    //private LinearLayout addUpdBdgtDiscardLL;
    private ImageView addUpdBdgtHdrBackImg;

    //page content
    private TextView addUpdBdgtNameET, addUpdBdgtAmtET, addUpdBdgtNoteET;
    private RadioGroup addUpdBdgtGrpRadioGrp, addUpdBdgtTypeRadioGrp;
    private RadioButton addUpdBdgtGrpCatRadio, addUpdBdgtGrpAccRadio, addUpdBdgtGrpSpntOnRadio;
    private Spinner addUpdBdgtGrpSpn;
    private RadioButton addUpdBdgtTypeDayRadio, addUpdBdgtTypeWeekRadio, addUpdBdgtTypeMonthRadio, addUpdBdgtTypeYearRadio;

    //buttons
    private TextView addUpdBdgtDoneTV;

    //db services
    private TransactionsDbService addUpdateTransactionsDbService = new TransactionsDbService(mContext);
    private AddUpdateBudgetsDbService addUpdateBudgetsDbService = new AddUpdateBudgetsDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UsersModel loggedInUserObj;

    //spinner adapter
    private AddUpdateBudgetSpinnerAdapter budgetTypeSpinnerAdapter;


    //transactionModel
    private BudgetModel budgetModelObj;

    //spinner lists from db
    private List<SpinnerModel> categorySpnList, accountSpnList, spentOnSpnList;

    //message popper
    private Dialog messageDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.budget_add_update);

        //get the Active user
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return;
        }

        //get budgetModel objet from intent
        getBudgetObjFromIntent();

		//initialize UI components
        initUIComponents();

        //setUp page content
        setUpPage();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup)findViewById(R.id.addUpdateBudgetRLId), robotoCondensedLightFont);
	}

    private void setUpPage() {
        //get all three lists, category, account and spent on
        categorySpnList = addUpdateTransactionsDbService.getAllCategories(loggedInUserObj.getUSER_ID());
        accountSpnList = addUpdateTransactionsDbService.getAllAccounts(loggedInUserObj.getUSER_ID());
        spentOnSpnList = addUpdateTransactionsDbService.getAllSpentOn(loggedInUserObj.getUSER_ID());

        budgetTypeSpinnerAdapter = new AddUpdateBudgetSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, categorySpnList);
        addUpdBdgtGrpSpn.setAdapter(budgetTypeSpinnerAdapter);
        budgetTypeSpinnerAdapter.notifyDataSetChanged();


        //update a old budget
        if(budgetModelObj != null){
            //budget name
            if(budgetModelObj.getBUDGET_NAME() != null){
                addUpdBdgtNameET.setText(budgetModelObj.getBUDGET_NAME());
            }

            //budget amount
            if(budgetModelObj.getBUDGET_AMT() != null){
                addUpdBdgtAmtET.setText(String.valueOf(budgetModelObj.getBUDGET_AMT()));
            }

            //group type
            if(budgetModelObj.getBUDGET_GRP_TYPE() != null){
                if("CATEGORY".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
                    addUpdBdgtGrpCatRadio.setChecked(true);

                    //set spinner
                    budgetTypeSpinnerAdapter = new AddUpdateBudgetSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, categorySpnList);
                    addUpdBdgtGrpSpn.setAdapter(budgetTypeSpinnerAdapter);
                    budgetTypeSpinnerAdapter.notifyDataSetChanged();
                }
                else if("ACCOUNT".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
                    addUpdBdgtGrpAccRadio.setChecked(true);

                    //set spinner
                    budgetTypeSpinnerAdapter = new AddUpdateBudgetSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, accountSpnList);
                    addUpdBdgtGrpSpn.setAdapter(budgetTypeSpinnerAdapter);
                    budgetTypeSpinnerAdapter.notifyDataSetChanged();
                }
                else if("SPENT ON".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
                    addUpdBdgtGrpSpntOnRadio.setChecked(true);

                    //set spinner
                    budgetTypeSpinnerAdapter = new AddUpdateBudgetSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, spentOnSpnList);
                    addUpdBdgtGrpSpn.setAdapter(budgetTypeSpinnerAdapter);
                    budgetTypeSpinnerAdapter.notifyDataSetChanged();
                }

                //pre select the spinner
                addUpdBdgtGrpSpn.setSelection(getSpinnerItemIndex(categorySpnList, budgetModelObj.getBUDGET_GRP_ID()));
            }

            //type
            if(budgetModelObj.getBUDGET_TYPE() != null){
                if("DAY".equalsIgnoreCase(budgetModelObj.getBUDGET_TYPE())){
                    addUpdBdgtTypeDayRadio.setChecked(true);
                }
                else if("WEEK".equalsIgnoreCase(budgetModelObj.getBUDGET_TYPE())){
                    addUpdBdgtTypeWeekRadio.setChecked(true);
                }
                else if("MONTH".equalsIgnoreCase(budgetModelObj.getBUDGET_TYPE())){
                    addUpdBdgtTypeMonthRadio.setChecked(true);
                }
                else if("YEAR".equalsIgnoreCase(budgetModelObj.getBUDGET_TYPE())){
                    addUpdBdgtTypeYearRadio.setChecked(true);
                }
            }

            //notes
            if(budgetModelObj.getBUDGET_NOTE() != null){
                addUpdBdgtNoteET.setText(budgetModelObj.getBUDGET_NOTE());
            }

            addUpdBdgtDoneTV.setText("Update");
        }
    }

    private void getBudgetObjFromIntent() {
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("BUDGET_OBJ")){
            budgetModelObj = (BudgetModel)getIntent().getSerializableExtra("BUDGET_OBJ");
        }
        else{
            Log.e(CLASS_NAME, "Error babay !! Intent is expected to contain BUDGET_OBJ..but could not find it");
        }
    }

    private Integer getSpinnerItemIndex(List<SpinnerModel> spnList, String itemIdStr){
        int spnListSize = spnList.size();
        int index = 0;

        for(int i=0; i<spnListSize; i++){
            if(spnList.get(i).getItemId().equalsIgnoreCase(itemIdStr)){
                index = i;
                break;
            }
        }
        return index;
    }

    private void initUIComponents() {
        //header
        addUpdBdgtHdrBackImg = (ImageView) this.findViewById(R.id.addUpdBdgtHdrBackImgId);

        //page content
        addUpdBdgtNameET = (EditText) this.findViewById(R.id.addUpdBdgtNameETId);
        addUpdBdgtAmtET = (EditText) this.findViewById(R.id.addUpdBdgtAmtETId);
        addUpdBdgtNoteET = (EditText) this.findViewById(R.id.addUpdBdgtNoteETId);
        addUpdBdgtGrpRadioGrp = (RadioGroup) this.findViewById(R.id.addUpdBdgtGrpRadioGrpId);
        addUpdBdgtTypeRadioGrp = (RadioGroup) this.findViewById(R.id.addUpdBdgtTypeRadioGrpId);
        addUpdBdgtGrpCatRadio = (RadioButton) this.findViewById(R.id.addUpdBdgtGrpCatRadioId);
        addUpdBdgtGrpAccRadio = (RadioButton) this.findViewById(R.id.addUpdBdgtGrpAccRadioId);
        addUpdBdgtGrpSpntOnRadio = (RadioButton) this.findViewById(R.id.addUpdBdgtGrpSpntOnRadioId);
        addUpdBdgtGrpSpn = (Spinner) this.findViewById(R.id.addUpdBdgtGrpSpnId);
        addUpdBdgtTypeDayRadio = (RadioButton) this.findViewById(R.id.addUpdBdgtTypeDayRadioId);
        addUpdBdgtTypeWeekRadio = (RadioButton) this.findViewById(R.id.addUpdBdgtTypeWeekRadioId);
        addUpdBdgtTypeMonthRadio = (RadioButton) this.findViewById(R.id.addUpdBdgtTypeMonthRadioId);
        addUpdBdgtTypeYearRadio = (RadioButton) this.findViewById(R.id.addUpdBdgtTypeYearRadioId);
        addUpdBdgtDoneTV = (TextView) this.findViewById(R.id.addUpdBdgtDoneTVId);

        //discard layout
        //addUpdBdgtDiscardLL = (LinearLayout) this.findViewById(R.id.addUpdBdgtDiscardLLId);

        //focus listener for title and amt
        addUpdBdgtNameET.addTextChangedListener(fielTextWatcher);
        addUpdBdgtAmtET.addTextChangedListener(fielTextWatcher);

        //hide discard buton by default
        //addUpdBdgtDiscardLL.setVisibility(View.INVISIBLE);
    }

    public BudgetModel getInputs(){
        String budgetNameStr = addUpdBdgtNameET.getText().toString();
        String budgetAmtStr = addUpdBdgtAmtET.getText().toString();
        String budgetGrpTypeStr = this.findViewById(addUpdBdgtGrpRadioGrp.getCheckedRadioButtonId()).getTag().toString();
        String budgetGrpTypeIdStr = addUpdBdgtGrpSpn.getSelectedView().getTag().toString();
        String budgetTypeStr = this.findViewById(addUpdBdgtTypeRadioGrp.getCheckedRadioButtonId()).getTag().toString();
        String budgetNotesStr = addUpdBdgtNoteET.getText().toString();

        //validations
        if("".equalsIgnoreCase(budgetNameStr.trim())){
            showToast("Oops ! Budget name is required");
            //addUpdateTranNameET.requestFocus();
            return null;
        }

        if("".equalsIgnoreCase(budgetAmtStr.trim())){
            showToast("Please Enter Amount !");
            addUpdBdgtAmtET.requestFocus();
            return null;
        }
        else if(Double.parseDouble(budgetAmtStr) <= 0){
            showToast("amount cannot be ZERO or Less !");
            addUpdBdgtAmtET.requestFocus();
            return null;
        }

        BudgetModel budgetModel = new BudgetModel();
        budgetModel.setBUDGET_NAME(budgetNameStr);
        budgetModel.setBUDGET_AMT(Double.parseDouble(budgetAmtStr));
        budgetModel.setBUDGET_GRP_TYPE(budgetGrpTypeStr);
        budgetModel.setBUDGET_GRP_ID(budgetGrpTypeIdStr);
        budgetModel.setBUDGET_TYPE(budgetTypeStr);
        budgetModel.setBUDGET_NOTE(budgetNotesStr);

        return budgetModel;
    }

    public void onBudgetGroupTypeRadioSelect(View view){
        String whichRadioSelectedStr = view.getTag().toString();

        if("CATEGORY".equalsIgnoreCase(whichRadioSelectedStr)){
            budgetTypeSpinnerAdapter = new AddUpdateBudgetSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, categorySpnList);
            addUpdBdgtGrpSpn.setAdapter(budgetTypeSpinnerAdapter);
            budgetTypeSpinnerAdapter.notifyDataSetChanged();
        }
        else if("ACCOUNT".equalsIgnoreCase(whichRadioSelectedStr)){
            budgetTypeSpinnerAdapter = new AddUpdateBudgetSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, accountSpnList);
            addUpdBdgtGrpSpn.setAdapter(budgetTypeSpinnerAdapter);
            budgetTypeSpinnerAdapter.notifyDataSetChanged();
        }
        else if("SPENT ON".equalsIgnoreCase(whichRadioSelectedStr)){
            budgetTypeSpinnerAdapter = new AddUpdateBudgetSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, spentOnSpnList);
            addUpdBdgtGrpSpn.setAdapter(budgetTypeSpinnerAdapter);
            budgetTypeSpinnerAdapter.notifyDataSetChanged();
        }
    }

	//on click of done/update default_button
	public void onDoneUpdate(View view){
        if(addBudget() != null) {
            Intent intent = new Intent(this, BudgetsViewActivity.class);
            startActivity(intent);
        }
	}
    //on click of done+discard
    public void onDonePlusNew(View view){
        if(addBudget() != null) {
            //calling the same activity
            startActivity(getIntent());
            finish();
        }
    }

    public String addBudget(){
        //get inputs
        BudgetModel budgetModel = getInputs();

        if(budgetModel == null){
            return null

                        ;
        }

        //set user id
        budgetModel.setUSER_ID(loggedInUserObj.getUSER_ID());

        //get done or update from default_button
        String doneDiscardStr = addUpdBdgtDoneTV.getText().toString();

        //its an old expense..so update
        if("Update".equalsIgnoreCase(doneDiscardStr)){
            //get the transaction id from intent and set it in the transaction object
            budgetModel.setBUDGET_ID(budgetModelObj.getBUDGET_ID());

            long result = addUpdateBudgetsDbService.updateOldBudget(budgetModel);

            if(result == 0) {
                showToast("Failed to update Budget !");
                return null;
            } else if(result == 1){
                showToast("Budget updated");
            }
            else if(result == -2){
                showToast("This Budget already exists !");
                return null;
            }
            else{
                showToast("Unknown error !");
                return null;
            }

        }
        else if("Done".equalsIgnoreCase(doneDiscardStr)){
            long result = addUpdateBudgetsDbService.addNewBudget(budgetModel);

            if(result == -1) {
                showToast("Failed to create a new Budget !");
                return null;
            }
            else if(result == -2){
                showToast("This Budget already exists !");
                return null;
            }
            else{
                showToast("New Budget created");
            }
        }

        return "";
    }

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    //on click of discard default_button
	public void onDiscard(View view){
		//discard the screen and go to calendar page
    	Intent intent = new Intent(AddUpdateBudgetActivity.this, SettingsActivity.class);
		startActivity(intent);
        finish();
	}
	
	//method to add new transaction
	/*public void addNewTransaction(TransactionModel transactionModel)
	{
        long result = TransactionDbService.getInstance(this).addNewTransaction(transactionModel);

		//	if -1 its an error..or else its success
		if(result != -1 && result != 0)
		{
			Log.i(CLASS_NAME, "New Transaction created");
			Toast.makeText(this, "Transaction Saved", Toast.LENGTH_SHORT).show();
		}
		//	if error
		else
		{
			Log.e(CLASS_NAME, "Transaction has failed !!");
			Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show();
		}
		
		//transaction has been saved in DB..now move to Calendar page
		Intent intent = new Intent(AddUpdateBudgetActivity.this,CalendarActivity.class);
		startActivity(intent);
	}*/

    public void showMessagePopper(View view){
        if("BACK".equalsIgnoreCase(String.valueOf(addUpdBdgtHdrBackImg.getTag()))){
            Intent intent = new Intent(this, BudgetsViewActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Create custom message popper object
        messageDialog = new Dialog(AddUpdateBudgetActivity.this);
        messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        messageDialog.setContentView(R.layout.message_popper);

        messageDialog.show();

        //buttons
        LinearLayout msgPoprPosLL, msgPoprNegLL;
        msgPoprPosLL = (LinearLayout) messageDialog.findViewById(R.id.msgPoprPosLLId);
        msgPoprNegLL = (LinearLayout) messageDialog.findViewById(R.id.msgPoprNegLLId);

        //set listeners for the buttons
        msgPoprPosLL.setOnClickListener(linearLayoutClickListener);
        msgPoprNegLL.setOnClickListener(linearLayoutClickListener);

        //texts
        TextView msgPoprNegTV, msgPoprPosTV, msgPoprMsgTV;
        msgPoprNegTV = (TextView) messageDialog.findViewById(R.id.msgPoprNegTVId);
        msgPoprPosTV = (TextView) messageDialog.findViewById(R.id.msgPoprPosTVId);
        msgPoprMsgTV = (TextView) messageDialog.findViewById(R.id.msgPoprMsgTVId);

        //set fonts
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

        msgPoprNegTV.setTypeface(robotoCondensedLightFont);
        msgPoprPosTV.setTypeface(robotoCondensedLightFont);
        msgPoprMsgTV.setTypeface(robotoCondensedLightFont);
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

	/*public void updateOldTransaction(TransactionModel transactionModel)
	{
		//TODO: update an old transction
		//	if -1 its an error..or else its success
		
		int noOfRowsUpdated = TransactionDbService.getInstance(this).updateOldTransaction(transactionModel);
		
		if(noOfRowsUpdated == 1)
		{
			Log.i(CLASS_NAME, "Old Transaction updated");
			Toast.makeText(this, "Transaction Updated", Toast.LENGTH_SHORT).show();
		}
		//	if error
		else if(noOfRowsUpdated == 0)
		{
			Log.e(CLASS_NAME, "Old Transaction update failed !!");
			Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Log.e(CLASS_NAME, "Multiple transactions updated..which is an error !!");
			Toast.makeText(this, "Transactions Updated !!(error)", Toast.LENGTH_SHORT).show();
		}
		
		//transaction has been updated in DB..now move to Calendar page
		Intent intent = new Intent(AddUpdateBudgetActivity.this,CalendarActivity.class);
		startActivity(intent);
	}*/

    //---------------------------------Edit Text type Listener-----------------------------------
    TextWatcher fielTextWatcher;
    {
        fielTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(!addUpdBdgtNameET.getText().toString().trim().isEmpty() || !addUpdBdgtAmtET.getText().toString().trim().isEmpty()){
                    if("DISCARD".equalsIgnoreCase(addUpdBdgtHdrBackImg.getTag().toString())){
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
                            addUpdBdgtHdrBackImg.setBackground(addUpdBdgtHdrBackImg.getResources().getDrawable(R.drawable.cancel));
                            addUpdBdgtHdrBackImg.setTag("DISCARD");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addUpdBdgtHdrBackImg.startAnimation(rotateAnim);
                }
                else{
                    if("BACK".equalsIgnoreCase(addUpdBdgtHdrBackImg.getTag().toString())){
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
                            addUpdBdgtHdrBackImg.setBackgroundResource(R.drawable.back);
                            addUpdBdgtHdrBackImg.setTag("BACK");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addUpdBdgtHdrBackImg.startAnimation(rotateAnim);
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
                    case R.id.msgPoprPosLLId :      intent = new Intent(AddUpdateBudgetActivity.this, BudgetsViewActivity.class);
                                                    break;
                    case R.id.msgPoprNegLLId :      break;

                    default:intent = new Intent(AddUpdateBudgetActivity.this, JimBrokeItActivity.class); break;
                }

                if(messageDialog != null){
                    messageDialog.dismiss();
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

