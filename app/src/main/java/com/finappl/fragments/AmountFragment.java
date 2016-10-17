package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.adapters.AccountsFragmentListViewAdapter;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.models.AccountsMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.IdGenerator;

import java.util.List;

import static com.finappl.utils.Constants.ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.AMOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_AMOUNT_OBJECT;

/**
 * Created by ajit on 21/3/16.
 */
public class AmountFragment extends DialogFragment implements View.OnClickListener{
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    private LinearLayout amountOneLL;
    private LinearLayout amountTwoLL;
    private LinearLayout amountThreeLL;
    private LinearLayout amountFourLL;
    private LinearLayout amountFiveLL;
    private LinearLayout amountSixLL;
    private LinearLayout amountSevenLL;
    private LinearLayout amountEightLL;
    private LinearLayout amountNineLL;
    private LinearLayout amountZeroLL;
    private LinearLayout amountDotLL;
    private LinearLayout amountOKLL;
    private ImageView amountDeleteIV;
    private TextView amountAmountTV;
    //end of components

    private String amountStr;

    private UserMO loggedInUserObj;

    private AuthorizationDbService authorizationDbService;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.amount, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getAmountFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    private void getAmountFromBundle() {
        amountStr = (String) getArguments().get(SELECTED_AMOUNT_OBJECT);
    }

    private void setupPage() {
        amountAmountTV.setText(amountStr);
    }

    private void initComps(View view){
        amountOneLL = (LinearLayout) view.findViewById(R.id.amountOneLLId);
        amountTwoLL = (LinearLayout) view.findViewById(R.id.amountTwoLLId);
        amountThreeLL = (LinearLayout) view.findViewById(R.id.amountThreeLLId);
        amountFourLL = (LinearLayout) view.findViewById(R.id.amountFourLLId);
        amountFiveLL = (LinearLayout) view.findViewById(R.id.amountFiveLLId);
        amountSixLL = (LinearLayout) view.findViewById(R.id.amountSixLLId);
        amountSevenLL = (LinearLayout) view.findViewById(R.id.amountSevenLLId);
        amountEightLL = (LinearLayout) view.findViewById(R.id.amountEightLLId);
        amountNineLL = (LinearLayout) view.findViewById(R.id.amountNineLLId);
        amountZeroLL = (LinearLayout) view.findViewById(R.id.amountZeroLLId);
        amountDotLL = (LinearLayout) view.findViewById(R.id.amountDotLLId);
        amountOKLL = (LinearLayout) view.findViewById(R.id.amountOKLLId);
        amountDeleteIV = (ImageView) view.findViewById(R.id.amountDeleteIVId);
        amountAmountTV = (TextView) view.findViewById(R.id.amountAmountTVId);

        amountOneLL.setOnClickListener(this);
        amountTwoLL.setOnClickListener(this);
        amountThreeLL.setOnClickListener(this);
        amountFourLL.setOnClickListener(this);
        amountFiveLL.setOnClickListener(this);
        amountSixLL.setOnClickListener(this);
        amountSevenLL.setOnClickListener(this);
        amountEightLL.setOnClickListener(this);
        amountNineLL.setOnClickListener(this);
        amountZeroLL.setOnClickListener(this);
        amountDotLL.setOnClickListener(this);
        amountOKLL.setOnClickListener(this);
        amountDeleteIV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        amountStr = String.valueOf(amountAmountTV.getText());

        if(amountStr.equals("0")){
            amountStr = "";
        }

        String tempStr;

        switch(v.getId()){
            case R.id.amountOneLLId :
                tempStr = amountStr + String.valueOf(v.getTag());
                amountStr = FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), tempStr);
                break;
            case R.id.amountTwoLLId :
                tempStr = amountStr + String.valueOf(v.getTag());
                amountStr = FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), tempStr);
                break;
            case R.id.amountThreeLLId :
                tempStr = amountStr + String.valueOf(v.getTag());
                amountStr = FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), tempStr);
                break;
            case R.id.amountFourLLId :
                tempStr = amountStr + String.valueOf(v.getTag());
                amountStr = FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), tempStr);
                break;
            case R.id.amountFiveLLId :
                tempStr = amountStr + String.valueOf(v.getTag());
                amountStr = FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), tempStr);
                break;
            case R.id.amountSixLLId :
                tempStr = amountStr + String.valueOf(v.getTag());
                amountStr = FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), tempStr);
                break;
            case R.id.amountSevenLLId :
                tempStr = amountStr + String.valueOf(v.getTag());
                amountStr = FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), tempStr);
                break;
            case R.id.amountEightLLId :
                tempStr = amountStr + String.valueOf(v.getTag());
                amountStr = FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), tempStr);
                break;
            case R.id.amountNineLLId :
                tempStr = amountStr + String.valueOf(v.getTag());
                amountStr = FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), tempStr);
                break;
            case R.id.amountZeroLLId :
                tempStr = amountStr + String.valueOf(v.getTag());
                amountStr = FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), tempStr);
                break;
            case R.id.amountDotLLId :
                tempStr = amountStr + String.valueOf(v.getTag());
                amountStr = FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), tempStr);
                break;
            case R.id.amountDeleteIVId : amountStr = "0";
                break;

            case R.id.amountOKLLId :
                amountStr = String.valueOf(amountAmountTV.getText());

                TransactionFragment activity = (TransactionFragment) getTargetFragment();
                activity.onFinishDialog(FinappleUtility.cleanUpAmount(amountStr));
                dismiss();
                break;
        }

        amountAmountTV.setText(amountStr);
    }

    private void getLoggedInUser(){
        loggedInUserObj = authorizationDbService.getActiveUser(FinappleUtility.getInstance().getActiveUserId(mContext));
    }

    private void initDb() {
        authorizationDbService = new AuthorizationDbService(mContext);
    }

    // Empty constructor required for DialogFragment
    public AmountFragment() {}

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
            int width = 500;
            int height = 800;
            d.getWindow().setLayout(width, height);
        }
    }


}
