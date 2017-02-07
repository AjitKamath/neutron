package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.UserMO;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.FRAGMENT_ACCOUNTS;
import static com.finappl.utils.Constants.FRAGMENT_CATEGORIES;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.FRAGMENT_OPTIONS;
import static com.finappl.utils.Constants.FRAGMENT_SETTINGS;
import static com.finappl.utils.Constants.FRAGMENT_SPENTONS;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class OptionsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.optionsLLId)
    LinearLayout optionsLL;

    @InjectView(R.id.optionsHeaderNameTVId)
    TextView optionsHeaderNameTV;
    /*Components*/

    private UserMO loggedInUserObj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.options, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    private void setupPage() {
        String nameStr = loggedInUserObj.getNAME();

        if(nameStr == null || nameStr.trim().isEmpty()){
            nameStr = "-Name Not Set-";
        }
        else{
            nameStr = nameStr.trim().toUpperCase();
        }

        optionsHeaderNameTV.setText(nameStr);
    }

    private void getDataFromBundle() {
        loggedInUserObj = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    @OnClick(R.id.optionsLogoutTVId)
    public void logout(){
        dismiss();

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFIRM_MESSAGE, "Logout "+getResources().getString(R.string.app_name)+" ?");

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_OPTIONS);

        ConfirmFragment fragment = new ConfirmFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_CONFIRM);
    }

    @OnClick(R.id.optionsSpentonsLLId)
    public void showSpentons(){
        dismiss();

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SPENTONS);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        SpentonsFragment fragment = new SpentonsFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SPENTONS);
    }

    @OnClick(R.id.optionsAccountsLLId)
    public void showAccounts(){
        dismiss();

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ACCOUNTS);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        AccountsFragment fragment = new AccountsFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_ACCOUNTS);
    }

    @OnClick(R.id.optionsCategoriesLLId)
    public void showCategories(){
        dismiss();

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CATEGORIES);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_CATEGORIES);
    }

    @OnClick(R.id.optionsSettingsLLId)
    public void showSettings(){
        dismiss();

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SETTINGS);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SETTINGS);
    }

    private void initComps(){
        setFont(optionsLL);
    }

    // Empty constructor required for DialogFragment
    public OptionsFragment() {}

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
            int width = ViewGroup.LayoutParams.WRAP_CONTENT ;
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
