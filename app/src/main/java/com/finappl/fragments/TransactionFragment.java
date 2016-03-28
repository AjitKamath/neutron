package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.finappl.R;
import com.finappl.adapters.AddUpdateTransactionSpinnerAdapter;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.models.SpinnerModel;
import com.finappl.models.UsersModel;
import com.finappl.utils.FinappleUtility;

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
    private TextView addUpdateYearTV;
    private TextView addUpdateMonthTV;
    private TextView addUpdateSuperScriptTV;
    private TextView addUpdateDayTV;
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
    private ImageButton addUpdatePageFabIB;
    private CheckBox transactionSchedueCB;
    private LinearLayout transactionSchedLL;

    private UsersModel loggedInUserObj;

    private TransactionsDbService transactionsDbService;

    private List<SpinnerModel> categoryList;
    private List<SpinnerModel> accountList;
    private List<SpinnerModel> spentOnList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction_add_update, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        initComps(view);
        setupPage();

        return view;
    }

    private void setupPage() {
        setupSpinners();


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
        addUpdateYearTV = (TextView) view.findViewById(R.id.addUpdateYearTVId);
        addUpdateMonthTV = (TextView) view.findViewById(R.id.addUpdateMonthTVId);
        addUpdateSuperScriptTV = (TextView) view.findViewById(R.id.addUpdateSuperScriptTVId);
        addUpdateDayTV = (TextView) view.findViewById(R.id.addUpdateDayTVId);
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
        addUpdatePageFabIB = (ImageButton) view.findViewById(R.id.addUpdatePageFabIBId);

        addUpdatePageFabIB.setOnClickListener(this);
        addUpdateTranBackImg.setOnClickListener(this);
    }

    private void getLoggedInUser(){
        loggedInUserObj = FinappleUtility.getInstance().getUser(mContext);
    }

    @Override
    public void onClick(View v) {
        DialogResultListener activity = (DialogResultListener) getActivity();
        activity.onFinishUserDialog("RESULT OF THE STUFF");
        this.dismiss();
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
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);

            addUpdateTranAmtET.requestFocus();
        }
    }

    private void initDb() {
        mContext = getActivity().getApplicationContext();

        transactionsDbService = new TransactionsDbService(mContext);
    }

    public interface DialogResultListener {
        void onFinishUserDialog(String resultStr);
    }
}
