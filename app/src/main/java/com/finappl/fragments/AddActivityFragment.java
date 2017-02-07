package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.TransactionMO;
import com.finappl.models.TransferMO;
import com.finappl.models.UserMO;

import java.text.ParseException;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_BUDGET;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_TRANSACTION;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_TRANSFER;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.SELECTED_DATE;
import static com.finappl.utils.Constants.TRANSACTION_OBJECT;
import static com.finappl.utils.Constants.TRANSFER_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class AddActivityFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    private LinearLayout addActivityLL, addActivityTransactionLL, addActivityTransferLL;
    //end of components

    private String selectedDateStr;
    private UserMO loggedInUserObj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps(view);

        return view;
    }

    private void getDataFromBundle() {
        selectedDateStr = (String) getArguments().get(SELECTED_DATE);
        loggedInUserObj = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void initComps(View view){
        addActivityLL = (LinearLayout) view.findViewById(R.id.addActivityLLId);
        addActivityTransactionLL = (LinearLayout) view.findViewById(R.id.addActivityTransactionLLId);
        addActivityTransferLL = (LinearLayout) view.findViewById(R.id.addActivityTransferLLId);

        addActivityTransactionLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionMO transactionModelObj = new TransactionMO();
                try{
                    transactionModelObj.setTRAN_DATE(JAVA_DATE_FORMAT_SDF.parse(selectedDateStr));
                }
                catch (ParseException pe){
                    Log.e(CLASS_NAME, "Date Parse Error !! "+pe);
                }

                FragmentManager manager = getFragmentManager();
                Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSACTION);

                if (frag != null) {
                    manager.beginTransaction().remove(frag).commit();
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable(TRANSACTION_OBJECT, transactionModelObj);
                bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

                AddUpdateTransactionFragment fragment = new AddUpdateTransactionFragment();
                fragment.setArguments(bundle);
                fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
                fragment.show(manager, FRAGMENT_ADD_UPDATE_TRANSACTION);

                //close the current fragment
                dismiss();
            }
        });

        setFont(addActivityLL);
    }

    @OnClick(R.id.addActivityTransferLLId)
    public void showAddTransfer(){
        TransferMO transfer = new TransferMO();
        try{
            transfer.setTRNFR_DATE(JAVA_DATE_FORMAT_SDF.parse(selectedDateStr));
        }
        catch (ParseException pe){
            Log.e(CLASS_NAME, "Date Parse Error !! "+pe);
            return;
        }

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSFER);

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(TRANSFER_OBJECT, transfer);
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        AddUpdateTransferFragment fragment = new AddUpdateTransferFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_ADD_UPDATE_TRANSFER);

        //close the current fragment
        dismiss();
    }

    @OnClick(R.id.addActivityBudgetLLId)
    public void showAddBudget(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_BUDGET);

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        AddUpdateBudgetFragment fragment = new AddUpdateBudgetFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_ADD_UPDATE_BUDGET);

        //close the current fragment
        dismiss();
    }

    // Empty constructor required for DialogFragment
    public AddActivityFragment() {}

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
