package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.SELECTED_AMOUNT_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_PARENT_FRAGMENT;

/**
 * Created by ajit on 21/3/16.
 */
public class SelectAmountFragment extends DialogFragment implements View.OnClickListener{
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*components*/
    @InjectView(R.id.amount_curr_tv)
    TextView amount_curr_tv;
    /*components*/

    //components
    private LinearLayout amountLL;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_amount, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    private void getFromBundle() {
        amountStr = (String) getArguments().get(SELECTED_AMOUNT_OBJECT);
        loggedInUserObj = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        amount_curr_tv.setText(loggedInUserObj.getCUR_CODE());
        amountAmountTV.setText(amountStr);
    }

    private void initComps(View view){
        amountLL = (LinearLayout) view.findViewById(R.id.amountLLId);

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

        setFont(amountLL);
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

                if(getTargetFragment() instanceof AddUpdateTransactionFragment){
                    AddUpdateTransactionFragment fragment = (AddUpdateTransactionFragment) getTargetFragment();
                    fragment.onFinishDialog(FinappleUtility.cleanUpAmount(amountStr));
                }
                else if (getTargetFragment() instanceof AddUpdateTransferFragment){
                    AddUpdateTransferFragment fragment = (AddUpdateTransferFragment) getTargetFragment();
                    fragment.onFinishDialog(FinappleUtility.cleanUpAmount(amountStr));
                }

                dismiss();
                break;
        }

        amountAmountTV.setText(amountStr);
    }

    // Empty constructor required for DialogFragment
    public SelectAmountFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public void onStop(){
        super.onStop();

        amountStr = String.valueOf(amountAmountTV.getText());

        if(getTargetFragment() instanceof AddUpdateTransactionFragment){
            AddUpdateTransactionFragment fragment = (AddUpdateTransactionFragment) getTargetFragment();
            fragment.onFinishDialog(FinappleUtility.cleanUpAmount(amountStr));
        }
        else if(getTargetFragment() instanceof AddUpdateTransferFragment){
            AddUpdateTransferFragment fragment = (AddUpdateTransferFragment) getTargetFragment();
            fragment.onFinishDialog(FinappleUtility.cleanUpAmount(amountStr));
        }
        else if(getTargetFragment() instanceof AddUpdateBudgetFragment){
            AddUpdateBudgetFragment fragment = (AddUpdateBudgetFragment) getTargetFragment();
            fragment.onFinishDialog(FinappleUtility.cleanUpAmount(amountStr));
        }
        else{
            Log.e(CLASS_NAME, UN_IDENTIFIED_PARENT_FRAGMENT);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
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
}
