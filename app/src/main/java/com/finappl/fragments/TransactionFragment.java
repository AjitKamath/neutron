package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowId;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.adapters.AddUpdateTransactionSpinnerAdapter;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.models.AccountsMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.SpentOnMO;
import com.finappl.models.SpentOnModel;
import com.finappl.models.SpinnerModel;
import com.finappl.models.TransactionModel;
import com.finappl.models.UserMO;
import com.finappl.models.UsersModel;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.IdGenerator;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

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
    private LinearLayout transactionContentCategoryLL;
    private LinearLayout transactionContentAccountLL;
    private LinearLayout transactionContentSpentonLL;
    private RadioButton addUpdateTranExpRadio;
    private RadioButton addUpdateTranIncRadio;
    private RadioGroup addUpdateTranExpIncRadioGrp;
    private EditText addUpdateNoteET;
    private ImageView transactionSaveIV;
    private CheckBox transactionSchedueCB;
    private LinearLayout transactionSchedLL;

    private UserMO loggedInUserObj;

    private CalendarDbService calendarDbService;
    private TransactionsDbService transactionsDbService;
    private AuthorizationDbService authorizationDbService;

    private List<CategoryMO> categoriesList;
    private List<AccountsMO> accountList;
    private List<SpentOnMO> spentOnList;


    private TransactionModel transactionModelObj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction, container);

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

        transactionModelObj.setCAT_ID(((CategoryMO)transactionContentCategoryLL.getTag()).getCAT_ID());
        transactionModelObj.setACC_ID(((AccountsMO)transactionContentAccountLL.getTag()).getACC_ID());
        transactionModelObj.setSPNT_ON_ID(((SpentOnMO)transactionContentSpentonLL.getTag()).getSPNT_ON_ID());

        transactionModelObj.setTRAN_TYPE(String.valueOf(getView().findViewById(addUpdateTranExpIncRadioGrp.getCheckedRadioButtonId()).getTag()));
        transactionModelObj.setTRAN_NOTE(String.valueOf(addUpdateNoteET.getText()));
        transactionModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());
    }

    private void getTransactionFromBundle() {
        transactionModelObj = (TransactionModel) getArguments().get(TRANSACTION_OBJECT);
    }

    private void setupPage() {
        if(transactionModelObj.getTRAN_ID() != null){
            //this transaction is being edited
        }
        else{
            addUpdateDateTV.setText(UI_DATE_FORMAT_SDF.format(transactionModelObj.getTRAN_DATE()));

            //set default category to be set
            CategoryMO categoryMO = transactionsDbService.getDefaultCategory(loggedInUserObj.getUSER_ID());
            setCategory(categoryMO);

            //set default account to be set
            AccountsMO accountsMO = transactionsDbService.getDefaultAccount(loggedInUserObj.getUSER_ID());
            setAccount(accountsMO);

            //set default spenton to be set
            SpentOnMO spentOnMO = transactionsDbService.getDefaultSpentOn(loggedInUserObj.getUSER_ID());
            setSpenton(spentOnMO);
        }

        getMasterData();
    }

    private void getMasterData() {
        categoriesList = calendarDbService.getAllCategories(loggedInUserObj.getUSER_ID());
        accountList = calendarDbService.getAllAccounts(loggedInUserObj.getUSER_ID());
        spentOnList = calendarDbService.getAllSpentOn(loggedInUserObj.getUSER_ID());
    }

    private void initComps(View view){
        addUpdateDateTV = (TextView) view.findViewById(R.id.addUpdateDateTVId);
        addUpdateTranBackImg = (ImageView) view.findViewById(R.id.addUpdateTranBackImgId);
        addUpdateTranNameET = (EditText) view.findViewById(R.id.addUpdateTranNameETId);
        addUpdateTranAmtET = (EditText) view.findViewById(R.id.addUpdateTranAmtETId);

        transactionContentCategoryLL = (LinearLayout) view.findViewById(R.id.transactionContentCategoryLLId);
        transactionContentAccountLL = (LinearLayout) view.findViewById(R.id.transactionContentAccountLLId);
        transactionContentSpentonLL = (LinearLayout) view.findViewById(R.id.transactionContentSpentonLLId);

        addUpdateTranExpRadio = (RadioButton) view.findViewById(R.id.addUpdateTranExpRadioId);
        addUpdateTranIncRadio = (RadioButton) view.findViewById(R.id.addUpdateTranIncRadioId);
        addUpdateTranExpIncRadioGrp = (RadioGroup) view.findViewById(R.id.addUpdateTranExpIncRadioGrpId);
        addUpdateNoteET = (EditText) view.findViewById(R.id.addUpdateNoteETId);
        transactionSaveIV = (ImageView) view.findViewById(R.id.transactionSaveIVId);

        transactionSaveIV.setOnClickListener(this);
        addUpdateTranBackImg.setOnClickListener(this);

        transactionContentCategoryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryFragment();
            }
        });
        transactionContentAccountLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccountFragment();
            }
        });
        transactionContentSpentonLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpentonFragment();
            }
        });

        addUpdateTranNameET.addTextChangedListener(fieldTextWatcher);
        addUpdateTranAmtET.addTextChangedListener(fieldTextWatcher);
    }

    private void showCategoryFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CATEGORY);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CATEGORY_OBJECT, (Serializable) categoriesList);
        bundle.putSerializable(SELECTED_CATEGORY_OBJECT, ((CategoryMO)transactionContentCategoryLL.getTag()).getCAT_ID());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_TRANSACTION);

        CategoriesFragment categoriesFragment = new CategoriesFragment();
        categoriesFragment.setArguments(bundle);
        categoriesFragment.setTargetFragment(currentFrag, 0);
        categoriesFragment.show(manager, FRAGMENT_CATEGORY);
    }

    private void showAccountFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ACCOUNT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(ACCOUNT_OBJECT, (Serializable) accountList);
        bundle.putSerializable(SELECTED_ACCOUNT_OBJECT, ((AccountsMO)transactionContentAccountLL.getTag()).getACC_ID());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_TRANSACTION);

        AccountsFragment accountsFragment = new AccountsFragment();
        accountsFragment.setArguments(bundle);
        accountsFragment.setTargetFragment(currentFrag, 0);
        accountsFragment.show(manager, FRAGMENT_ACCOUNT);
    }

    private void showSpentonFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SPENTON);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SPENTON_OBJECT, (Serializable) spentOnList);
        bundle.putSerializable(SELECTED_SPENTON_OBJECT, ((SpentOnMO)transactionContentSpentonLL.getTag()).getSPNT_ON_ID());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_TRANSACTION);

        SpentonsFragment spentonsFragment = new SpentonsFragment();
        spentonsFragment.setArguments(bundle);
        spentonsFragment.setTargetFragment(currentFrag, 0);
        spentonsFragment.show(manager, FRAGMENT_SPENTON);
    }

    private void getLoggedInUser(){
        loggedInUserObj = authorizationDbService.getActiveUser(FinappleUtility.getInstance().getActiveUserId(mContext));
    }

    @Override
    public void onClick(View v) {
        String messageStr = "Error !!";
        if(v.getId() == transactionSaveIV.getId()){
            getInputs();

            //if transactionModelObj contains transactionId, then its an update. if not its a new transaction
            if(transactionModelObj.getTRAN_ID() == null){
                transactionModelObj.setTRAN_ID(IdGenerator.getInstance().generateUniqueId("TRAN"));
                long result = transactionsDbService.addNewTransaction(transactionModelObj);

                if(result == -1) {
                    messageStr = "Failed to create a new Transaction !";
                }
                else{
                    messageStr = "New Transaction created";
                }
            }
            else{
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
        }

        CalendarActivity activity = (CalendarActivity) this.getActivity();
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
        mContext = getActivity().getApplicationContext();

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
        calendarDbService = new CalendarDbService(mContext);
        transactionsDbService = new TransactionsDbService(mContext);
        authorizationDbService = new AuthorizationDbService(mContext);
    }

    public void onFinishUserDialog(CategoryMO categoryMO) {
        setCategory(categoryMO);
    }
    public void onFinishUserDialog(AccountsMO accountsMO) {
        setAccount(accountsMO);
    }
    public void onFinishUserDialog(SpentOnMO spentOnMO) {
        setSpenton(spentOnMO);
    }

    private void setCategory(CategoryMO categoryMO){
        ((TextView)transactionContentCategoryLL.findViewById(R.id.transactionContentCategoryTVId)).setText(categoryMO.getCAT_NAME());
        transactionContentCategoryLL.setTag(categoryMO);
    }

    private void setAccount(AccountsMO accountsMO){
        ((TextView)transactionContentAccountLL.findViewById(R.id.transactionContentAccountTVId)).setText(accountsMO.getACC_NAME());
        transactionContentAccountLL.setTag(accountsMO);
    }

    private void setSpenton(SpentOnMO spentOnMO){
        ((TextView)transactionContentSpentonLL.findViewById(R.id.transactionContentSpentonTVId)).setText(spentOnMO.getSPNT_ON_NAME());
        transactionContentSpentonLL.setTag(spentOnMO);
    }

    public interface DialogResultListener {
        void onFinishUserDialog(String str);
    }
}
