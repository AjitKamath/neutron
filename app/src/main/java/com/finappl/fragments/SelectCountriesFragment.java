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
import com.finappl.adapters.SelectCountryFragmentListViewAdapter;
import com.finappl.models.CountryMO;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.finappl.utils.Constants.COUNTRY_OBJECT;
import static com.finappl.utils.Constants.SELECTED_COUNTRY_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_PARENT_FRAGMENT;

/**
 * Created by ajit on 21/3/16.
 */
public class SelectCountriesFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    @InjectView(R.id.countryRLId)
    RelativeLayout countryRL;

    @InjectView(R.id.countryContentLVId)
    ListView countriesContentLV;
    //end of components

    private List<CountryMO> countriesList;

    private String selectedCountryStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_country, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    private void getDataFromBundle() {
        countriesList = (List<CountryMO>) getArguments().get(COUNTRY_OBJECT);
        selectedCountryStr = (String) getArguments().get(SELECTED_COUNTRY_OBJECT);
    }

    private void setupPage() {
        SelectCountryFragmentListViewAdapter adapter = new SelectCountryFragmentListViewAdapter(mContext, countriesList, selectedCountryStr);
        countriesContentLV.setAdapter(adapter);
    }

    private void initComps(){
        countriesContentLV.setOnItemClickListener(listViewItemClickListener);

        setFont(countryRL);
    }

    AdapterView.OnItemClickListener listViewItemClickListener;
    {
        listViewItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(getTargetFragment() instanceof SettingsFragment){
                    SettingsFragment fragment = (SettingsFragment) getTargetFragment();
                    fragment.onFinishDialog(countriesList.get(position));
                }
                else{
                    Log.e(CLASS_NAME, UN_IDENTIFIED_PARENT_FRAGMENT);
                }

                dismiss();
            }
        };
    }


    // Empty constructor required for DialogFragment
    public SelectCountriesFragment() {}

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
