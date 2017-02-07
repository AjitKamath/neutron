package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.HomeActivity;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.models.TransactionMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_TRANSACTION;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.FRAGMENT_TRANSACTION_DETAILS;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.TRANSACTION_OBJECT;
import static com.finappl.utils.Constants.UI_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class TransactionDetailsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.transactionDetailsLLId)
    LinearLayout transactionDetailsLL;

    @InjectView(R.id.transactionDetailsRepeatLLId)
    LinearLayout transactionDetailsRepeatLL;

    @InjectView(R.id.transactionDetailsNoteLLId)
    LinearLayout transactionDetailsNoteLL;

    @InjectView(R.id.transactionDetailsDateTVId)
    TextView transactionDetailsDateTV;

    @InjectView(R.id.transactionDetailsCategoryIVId)
    ImageView transactionDetailsCategoryIV;

    @InjectView(R.id.transactionDetailsNameTVId)
    TextView transactionDetailsNameTV;

    @InjectView(R.id.transactionDetailsCurrTVId)
    TextView transactionDetailsCurrTV;

    @InjectView(R.id.transactionDetailsAmountTVId)
    TextView transactionDetailsAmountTV;

    @InjectView(R.id.transactionDetailsCategoryTVId)
    TextView transactionDetailsCategoryTV;

    @InjectView(R.id.transactionDetailsAccountIVId)
    ImageView transactionDetailsAccountIV;

    @InjectView(R.id.transactionDetailsAccountTVId)
    TextView transactionDetailsAccountTV;

    @InjectView(R.id.transactionDetailsSpenOnIVId)
    ImageView transactionDetailsSpenOnIV;

    @InjectView(R.id.transactionDetailsSpentOnTVId)
    TextView transactionDetailsSpentOnTV;

    @InjectView(R.id.transactionDetailsRepeatTVId)
    TextView transactionDetailsRepeatTV;

    @InjectView(R.id.transactionDetailsScheduleTVId)
    TextView transactionDetailsScheduleTV;

    @InjectView(R.id.transactionDetailsNotifyAddTVId)
    TextView transactionDetailsNotifyAddTV;

    @InjectView(R.id.transactionDetailsNotifyAddTimeTVId)
    TextView transactionDetailsNotifyAddTimeTV;

    @InjectView(R.id.transactionDetailsNoteTVId)
    TextView transactionDetailsNoteTV;
    /*Components*/

    //db services
    private TransactionsDbService transactionsDbService;

    private TransactionMO transaction;
    private UserMO loggedInUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction_details, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    private void getDataFromBundle() {
        transaction = (TransactionMO) getArguments().get(TRANSACTION_OBJECT);
        loggedInUser = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        if(transaction == null){
            Log.e(CLASS_NAME, "Disaster !!! add_update_transaction object is null");
            showToast("Catastrophic Error !!");
            return;
        }
        else if(transaction.getTRAN_ID() == null || transaction.getTRAN_ID().trim().isEmpty()){
            Log.e(CLASS_NAME, "Disaster !!! TRAN_ID is null/empty");
            showToast("Catastrophic Error !!");
            return;
        }

        transactionDetailsDateTV.setText(UI_DATE_FORMAT_SDF.format(transaction.getTRAN_DATE()));
        transactionDetailsCategoryIV.setBackgroundResource(Integer.parseInt(transaction.getCategoryImg()));
        transactionDetailsCategoryTV.setText(transaction.getCategory());
        transactionDetailsNameTV.setText(transaction.getTRAN_NAME());
        transactionDetailsCurrTV.setText(loggedInUser.getCUR_CODE());
        transactionDetailsAmountTV = FinappleUtility.formatAmountView(transactionDetailsAmountTV, loggedInUser, transaction.getTRAN_AMT());

        if("EXPENSE".equalsIgnoreCase(transaction.getTRAN_TYPE())){
            transactionDetailsAmountTV.setTextColor(transactionDetailsAmountTV.getResources().getColor(R.color.finappleCurrencyNegColor));
        }
        else{
            transactionDetailsAmountTV.setTextColor(transactionDetailsAmountTV.getResources().getColor(R.color.finappleCurrencyPosColor));
        }

        transactionDetailsAccountIV.setBackgroundResource(Integer.parseInt(transaction.getAccountImg()));
        transactionDetailsAccountTV.setText(transaction.getAccount());
        transactionDetailsSpenOnIV.setBackgroundResource(Integer.parseInt(transaction.getSpentOnImg()));
        transactionDetailsSpentOnTV.setText(transaction.getSpentOn());

        //set up repeat
        if(transaction.getREPEAT_ID() == null || transaction.getREPEAT_ID().isEmpty()){
            transactionDetailsRepeatLL.setVisibility(GONE);
        }
        else{
            transactionDetailsRepeatTV.setText(transaction.getRepeat());
            transactionDetailsScheduleTV.setText(transaction.getSCHD_UPTO_DATE());

            if("AUTO".equalsIgnoreCase(transaction.getNOTIFY())){
                transactionDetailsNotifyAddTV.setText("AUTO ADD");
            }
            else{
                transactionDetailsNotifyAddTV.setText("NOTIFY & ADD");
            }

            transactionDetailsNotifyAddTimeTV.setText(transaction.getNOTIFY_TIME());
        }

        //set up notes
        if(transaction.getTRAN_NOTE() == null || transaction.getTRAN_NOTE().isEmpty()){
            transactionDetailsNoteLL.setVisibility(GONE);
        }
        else{
            transactionDetailsNoteTV.setText(transaction.getTRAN_NOTE());
        }
    }

    private void initComps(){
        setFont(transactionDetailsLL);
    }

    @OnClick(R.id.transactionDetailsDeleteTVId)
    public void editTransaction(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSACTION);

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(TRANSACTION_OBJECT, transaction);
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUser);

        AddUpdateTransactionFragment fragment = new AddUpdateTransactionFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_ADD_UPDATE_TRANSACTION);

        //dismiss current fragment
        dismiss();
    }

    @OnClick(R.id.transactionDetailsDeleteTVId)
    public void showConfirmDeleteFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFIRM_MESSAGE, "Delete Transaction ?");

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_TRANSACTION_DETAILS);

        ConfirmFragment fragment = new ConfirmFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_CONFIRM);
    }

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    // Empty constructor required for DialogFragment
    public TransactionDetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        transactionsDbService = new TransactionsDbService(mContext);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
        }
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group) {
        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), UI_FONT);

        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(robotoCondensedLightFont);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v);
            }
        }
    }

    private void closeFragment(String messageStr){
        dismiss();
    }

    public void onFinishDialog(String messageStr) {
        if(!transactionsDbService.deleteTransaction(transaction.getTRAN_ID())){
            messageStr = "Could not delete the Transaction";
        }

        closeFragment(messageStr);

        //((HomeActivity)getActivity()).initActivity();
        ((HomeActivity)getActivity()).showSnacks(messageStr, OK, Snackbar.LENGTH_LONG);
    }
}