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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.activities.HomeActivity;
import com.finappl.models.CategoryMO;
import com.google.firebase.auth.FirebaseAuth;

import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.SELECTED_CATEGORY_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_PARENT_FRAGMENT;

/**
 * Created by ajit on 21/3/16.
 */
public class ConfirmFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    private LinearLayout closeConfirmLL;
    private TextView closeConfirmMessageTV, closeConfirmOKTV, closeConfirmCancelTV;
    //end of components

    private String messageStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.close_confirm, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getMessageFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    private void getMessageFromBundle() {
        messageStr = (String) getArguments().get(CONFIRM_MESSAGE);
    }

    private void setupPage() {
        closeConfirmMessageTV.setText(messageStr);
    }

    private void initComps(View view){
        closeConfirmLL = (LinearLayout) view.findViewById(R.id.closeConfirmLLId);
        closeConfirmMessageTV = (TextView) view.findViewById(R.id.closeConfirmMessageTVId);
        closeConfirmOKTV = (TextView) view.findViewById(R.id.closeConfirmOKTVId);
        closeConfirmCancelTV = (TextView) view.findViewById(R.id.closeConfirmCancelTVId);

        closeConfirmOKTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getTargetFragment() instanceof AddUpdateTransactionFragment){
                    AddUpdateTransactionFragment fragment = (AddUpdateTransactionFragment) getTargetFragment();
                    fragment.onFinishDialog();
                }
                else if(getTargetFragment() instanceof TransactionDetailsFragment){
                    TransactionDetailsFragment fragment = (TransactionDetailsFragment) getTargetFragment();
                    fragment.onFinishDialog("Transaction Deleted !");
                }
                if(getTargetFragment() instanceof AddUpdateTransferFragment){
                    AddUpdateTransferFragment fragment = (AddUpdateTransferFragment) getTargetFragment();
                    fragment.onFinishDialog();
                }
                else if(getTargetFragment() instanceof TransferDetailsFragment){
                    TransferDetailsFragment fragment = (TransferDetailsFragment) getTargetFragment();
                    fragment.onFinishDialog("Transfer Deleted !");
                }
                else if(getTargetFragment() instanceof OptionsFragment){
                    FirebaseAuth user = FirebaseAuth.getInstance();
                    user.signOut();
                    //((HomeActivity)getActivity()).forceLogin();
                }
                else if(getTargetFragment() instanceof SettingsFragment){
                    SettingsFragment fragment = (SettingsFragment) getTargetFragment();
                    fragment.dismiss();
                }
                else if(getTargetFragment() instanceof BudgetDetailsFragment){
                    BudgetDetailsFragment fragment = (BudgetDetailsFragment) getTargetFragment();
                    fragment.onFinishDialog("Budget Deleted !");
                }
                else if(getTargetFragment() instanceof CategoriesFragment){
                    CategoriesFragment fragment = (CategoriesFragment) getTargetFragment();
                    CategoryMO category = (CategoryMO) getArguments().get(SELECTED_CATEGORY_OBJECT);
                    fragment.deleteCategory(category);
                }
                else{
                    Log.e(CLASS_NAME, UN_IDENTIFIED_PARENT_FRAGMENT);
                    return;
                }

                dismiss();
            }
        });

        closeConfirmCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        setFont(closeConfirmLL);
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

    // Empty constructor required for DialogFragment
    public ConfirmFragment() {}

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
}
