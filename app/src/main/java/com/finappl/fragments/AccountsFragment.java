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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.adapters.AccountsFragmentListViewAdapter;
import com.finappl.models.AccountMO;
import com.finappl.models.UserMO;

import java.util.List;

import static com.finappl.utils.Constants.ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.ACCOUNT_TYPE_FLAG;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_PARENT_FRAGMENT;

/**
 * Created by ajit on 21/3/16.
 */
public class AccountsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    private RelativeLayout accountsRL;
    private ListView accountsLV;
    //end of components

    private List<AccountMO> accountsList;

    private String selectedAccountStr;
    private String whichAccountStr;

    private UserMO loggedInUserMo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accounts, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getAccountsFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    private void getAccountsFromBundle() {
        accountsList = (List<AccountMO>) getArguments().get(ACCOUNT_OBJECT);
        selectedAccountStr = (String) getArguments().get(SELECTED_ACCOUNT_OBJECT);
        loggedInUserMo = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
        whichAccountStr = (String) getArguments().get(ACCOUNT_TYPE_FLAG);
    }

    private void setupPage() {
        AccountsFragmentListViewAdapter accountsFragmentListViewAdapter = new AccountsFragmentListViewAdapter(mContext, accountsList, selectedAccountStr, loggedInUserMo);
        accountsLV.setAdapter(accountsFragmentListViewAdapter);
    }

    private void initComps(View view){
        accountsRL = (RelativeLayout) view.findViewById(R.id.accountsRLId);
        accountsLV = (ListView) view.findViewById(R.id.accountsContentLVId);

        accountsLV.setOnItemClickListener(listViewItemClickListener);

        setFont(accountsRL);
    }

    AdapterView.OnItemClickListener listViewItemClickListener;
    {
        listViewItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(getTargetFragment() instanceof TransactionFragment){
                    TransactionFragment activity = (TransactionFragment) getTargetFragment();
                    activity.onFinishDialog(accountsList.get(position));
                }
                else if(getTargetFragment() instanceof TransferFragment){
                    TransferFragment activity = (TransferFragment) getTargetFragment();
                    activity.onFinishDialog(accountsList.get(position), whichAccountStr);
                }
                else{
                    Log.e(CLASS_NAME, UN_IDENTIFIED_PARENT_FRAGMENT);
                }

                dismiss();
            }
        };
    }


    // Empty constructor required for DialogFragment
    public AccountsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog d = getDialog();
        if (d!=null) {
            int width = 600;
            int height = 750;
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
