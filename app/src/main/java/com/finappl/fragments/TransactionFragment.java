package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.adapters.AddUpdateTransactionSpinnerAdapter;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.models.SpinnerModel;
import com.finappl.models.TransactionModel;
import com.finappl.models.UsersModel;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.IdGenerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.finappl.utils.Constants.*;

/**
 * Created by ajit on 21/3/16.
 */
public class TransactionFragment extends DialogFragment implements ImageButton.OnClickListener {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    private RelativeLayout addUpdateTransactionRL;

    //components
    private TextView addUpdateDateTV;
    private ImageView addUpdateTranBackImg;
    private EditText addUpdateTranNameET;
    private EditText addUpdateTranAmtET;
    private Spinner addUpdateCatSpn;
    private Spinner addUpdateAccSpn;
    private RadioButton addUpdateTranExpRadio;
    private RadioButton addUpdateTranIncRadio;
    private RadioGroup addUpdateTranExpIncRadioGrp;
    private Spinner addUpdateSpntOnSpn;
    private EditText addUpdateNoteET;
    private ImageView transactionSaveIV;
    private CheckBox transactionSchedueCB;
    private LinearLayout transactionSchedLL;

    private UsersModel loggedInUserObj;

    private TransactionsDbService transactionsDbService;
    private AuthorizationDbService authorizationDbService;

    private List<SpinnerModel> categoryList;
    private List<SpinnerModel> accountList;
    private List<SpinnerModel> spentOnList;

    private TransactionModel transactionModelObj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction_add_update, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getTransactionFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    public void getInputs(){
        try{
            transactionModelObj.setTRAN_DATE(UI_DATE_FORMAT_SDF.parse(String.valueOf(addUpdateDateTV.getText())));
        }
        catch (ParseException pe){
            Log.e(CLASS_NAME, "Parse Exception "+pe);
            return ;
        }

        transactionModelObj.setTRAN_AMT(Double.parseDouble(String.valueOf(addUpdateTranAmtET.getText())));
        transactionModelObj.setTRAN_NAME(String.valueOf(addUpdateTranNameET.getText()));
        transactionModelObj.setCAT_ID(String.valueOf(addUpdateCatSpn.getSelectedView().getTag()));
        transactionModelObj.setCategory(((SpinnerModel) addUpdateCatSpn.getSelectedItem()).getItemName());
        transactionModelObj.setACC_ID(String.valueOf(addUpdateAccSpn.getSelectedView().getTag()));
        transactionModelObj.setAccount(((SpinnerModel) addUpdateAccSpn.getSelectedItem()).getItemName());
        transactionModelObj.setTRAN_TYPE(String.valueOf(getView().findViewById(addUpdateTranExpIncRadioGrp.getCheckedRadioButtonId()).getTag()));
        transactionModelObj.setSPNT_ON_ID(String.valueOf(addUpdateSpntOnSpn.getSelectedView().getTag()));
        transactionModelObj.setSpentOn(((SpinnerModel) addUpdateSpntOnSpn.getSelectedItem()).getItemName());
        transactionModelObj.setTRAN_NOTE(String.valueOf(addUpdateNoteET.getText()));
        transactionModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());
    }

    private void getTransactionFromBundle() {
        transactionModelObj = (TransactionModel) getArguments().get(TRANSACTION_OBJECT);
    }

    private void setupPage() {
        setupSpinners();

        //set rest of the page values based on the transactionModelObj from the bundle

        //if the transactionModelObj contains transactionId in it, then its an already existing transaction. if not, it a new transaction
        if(transactionModelObj.getTRAN_ID() != null && !transactionModelObj.getTRAN_ID().trim().isEmpty()){
            TransactionModel tempTransactionModelObj = transactionsDbService.getTransactionOnTransactionId(transactionModelObj.getTRAN_ID());

            addUpdateDateTV.setText(UI_DATE_FORMAT_SDF.format(tempTransactionModelObj.getTRAN_DATE()));
            addUpdateTranAmtET.setText(String.valueOf(tempTransactionModelObj.getTRAN_AMT()));
            addUpdateTranNameET.setText(tempTransactionModelObj.getTRAN_NAME());
            addUpdateCatSpn.setSelection(getSpinnerItemIndex(categoryList, tempTransactionModelObj.getCategory()));
            addUpdateAccSpn.setSelection(getSpinnerItemIndex(accountList, tempTransactionModelObj.getAccount()));
            if("INCOME".equalsIgnoreCase(tempTransactionModelObj.getTRAN_TYPE())){
                addUpdateTranIncRadio.setChecked(true);
            }
            addUpdateSpntOnSpn.setSelection(getSpinnerItemIndex(spentOnList, tempTransactionModelObj.getSpentOn()));
            addUpdateNoteET.setText(tempTransactionModelObj.getTRAN_NOTE());
            transactionSaveIV.setBackgroundResource(R.drawable.save_white_small);
        }
        else if(transactionModelObj.getTRAN_DATE() != null){
            addUpdateDateTV.setText(UI_DATE_FORMAT_SDF.format(transactionModelObj.getTRAN_DATE()).toUpperCase());
        }
        else{
            Log.e(CLASS_NAME, "Expected data not found in "+TRANSACTION_OBJECT);
        }
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

    private void setupSpinners() {
        categoryList = transactionsDbService.getAllCategories(loggedInUserObj.getUSER_ID());
        accountList = transactionsDbService.getAllAccounts(loggedInUserObj.getUSER_ID());
        spentOnList = transactionsDbService.getAllSpentOn(loggedInUserObj.getUSER_ID());

        addUpdateCatSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(mContext, R.layout.cat_acc_spnt_spnr, categoryList));
        addUpdateAccSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(mContext, R.layout.cat_acc_spnt_spnr, accountList));
        addUpdateSpntOnSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(mContext, R.layout.cat_acc_spnt_spnr, spentOnList));
    }

    private void initComps(View view){
        addUpdateDateTV = (TextView) view.findViewById(R.id.addUpdateDateTVId);
        addUpdateTranBackImg = (ImageView) view.findViewById(R.id.addUpdateTranBackImgId);
        addUpdateTranNameET = (EditText) view.findViewById(R.id.addUpdateTranNameETId);
        addUpdateTranAmtET = (EditText) view.findViewById(R.id.addUpdateTranAmtETId);
        addUpdateCatSpn = (Spinner) view.findViewById(R.id.addUpdateCatSpnId);
        addUpdateAccSpn = (Spinner) view.findViewById(R.id.addUpdateAccSpnId);
        addUpdateTranExpRadio = (RadioButton) view.findViewById(R.id.addUpdateTranExpRadioId);
        addUpdateTranIncRadio = (RadioButton) view.findViewById(R.id.addUpdateTranIncRadioId);
        addUpdateTranExpIncRadioGrp = (RadioGroup) view.findViewById(R.id.addUpdateTranExpIncRadioGrpId);
        addUpdateSpntOnSpn = (Spinner) view.findViewById(R.id.addUpdateSpntOnSpnId);
        addUpdateNoteET = (EditText) view.findViewById(R.id.addUpdateNoteETId);
        transactionSaveIV = (ImageView) view.findViewById(R.id.transactionSaveIVId);

        transactionSaveIV.setOnClickListener(this);
        addUpdateTranBackImg.setOnClickListener(this);

        addUpdateTranNameET.addTextChangedListener(fieldTextWatcher);
        addUpdateTranAmtET.addTextChangedListener(fieldTextWatcher);
    }

    private void getLoggedInUser(){
        loggedInUserObj = authorizationDbService.getActiveUser().get(0);
    }

    @Override
    public void onClick(View v) {
        String messageStr = "Error !!";
        if(v.getId() == transactionSaveIV.getId()){
            getInputs();

            //if transactionModelObj contains transactionId, then its an update. if not its a new transaction
            if(transactionModelObj.getTRAN_ID() != null && !transactionModelObj.getTRAN_ID().trim().isEmpty()){
                long result = transactionsDbService.updateOldTransaction(transactionModelObj);

                if(result == 0) {
                    messageStr = "Failed to update Transaction/Account !";
                } else if(result == 1){
                    messageStr = "Transaction updated";
                }
                else{
                    messageStr = "Unknown error !";
                }
            }
            else{
                transactionModelObj.setTRAN_ID(IdGenerator.getInstance().generateUniqueId("TRAN"));
                long result = transactionsDbService.addNewTransaction(transactionModelObj);

                if(result == -1) {
                    messageStr = "Failed to create a new Transaction !";
                }
                else{
                    messageStr = "New Transaction created";
                }
            }
        }





        DialogResultListener activity = (DialogResultListener) getActivity();
        activity.onFinishUserDialog(messageStr);
        this.dismiss();
    }

    private void setUpSaveButton(){
        LinearLayout transactionsButtonLL = (LinearLayout) getView().findViewById(R.id.transactionsButtonLLId);
        if("HIDDEN".equalsIgnoreCase(String.valueOf(transactionSaveIV.getTag()))){
            transactionsButtonLL.animate().setDuration(250).translationX(-80);
            transactionSaveIV.setTag("SHOWN");

        } else{
            transactionsButtonLL.animate().setDuration(250).translationX(0);
            transactionSaveIV.setTag("HIDDEN");
        }
    }

    TextWatcher fieldTextWatcher;
    {
        fieldTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String amountStr = String.valueOf(addUpdateTranAmtET.getText());
                Double amount;
                if(amountStr.trim().isEmpty()){
                    amount = 0.0;
                }
                else{
                    amount = Double.parseDouble(amountStr);
                }


                if(!String.valueOf(addUpdateTranNameET.getText()).trim().isEmpty() && amount  != 0){
                    if("HIDDEN".equalsIgnoreCase(String.valueOf(transactionSaveIV.getTag()))){
                        setUpSaveButton();
                    }
                }
                else{
                    if("SHOWN".equalsIgnoreCase(String.valueOf(transactionSaveIV.getTag()))){
                        setUpSaveButton();
                    }
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

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    // Empty constructor required for DialogFragment
    public TransactionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDb();

        getLoggedInUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);

            addUpdateTranAmtET.requestFocus();
        }
    }

    private void initDb() {
        mContext = getActivity().getApplicationContext();

        transactionsDbService = new TransactionsDbService(mContext);
        authorizationDbService = new AuthorizationDbService(mContext);
    }

    public interface DialogResultListener {
        void onFinishUserDialog(String resultStr);
    }
}
