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
import com.finappl.adapters.SelectSpentonFragmentListViewAdapter;
import com.finappl.models.SpentOnMO;

import java.util.List;

import static com.finappl.utils.Constants.SELECTED_SPENTON_OBJECT;
import static com.finappl.utils.Constants.SPENTON_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_PARENT_FRAGMENT;

/**
 * Created by ajit on 21/3/16.
 */
public class SelectSpentonFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    private RelativeLayout spentonsRL;
    private ListView spentonsLV;
    //end of components

    private List<SpentOnMO> spentonsList;

    private String selectedSpentOnStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_spenton, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSpentOnsFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    private void getSpentOnsFromBundle() {
        spentonsList = (List<SpentOnMO>) getArguments().get(SPENTON_OBJECT);
        selectedSpentOnStr = (String) getArguments().get(SELECTED_SPENTON_OBJECT);
    }

    private void setupPage() {
        SelectSpentonFragmentListViewAdapter selectSpentonFragmentListViewAdapter = new SelectSpentonFragmentListViewAdapter(mContext, spentonsList, selectedSpentOnStr);
        spentonsLV.setAdapter(selectSpentonFragmentListViewAdapter);
    }

    private void initComps(View view){
        spentonsRL =(RelativeLayout) view.findViewById(R.id.spentonsRLId);
        spentonsLV = (ListView) view.findViewById(R.id.spentonsContentLVId);

        spentonsLV.setOnItemClickListener(listViewItemClickListener);

        setFont(spentonsRL);
    }

    AdapterView.OnItemClickListener listViewItemClickListener;
    {
        listViewItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(getTargetFragment() instanceof AddUpdateTransactionFragment){
                    AddUpdateTransactionFragment activity = (AddUpdateTransactionFragment) getTargetFragment();
                    activity.onFinishDialog(spentonsList.get(position));
                }
                else if(getTargetFragment() instanceof AddUpdateBudgetFragment){
                    AddUpdateBudgetFragment activity = (AddUpdateBudgetFragment) getTargetFragment();
                    activity.onFinishDialog(spentonsList.get(position));
                }
                else{
                    Log.e(CLASS_NAME, UN_IDENTIFIED_PARENT_FRAGMENT);
                }
                dismiss();
            }
        };
    }


    // Empty constructor required for DialogFragment
    public SelectSpentonFragment() {}

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
