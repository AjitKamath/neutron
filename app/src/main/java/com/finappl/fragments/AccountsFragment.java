package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.adapters.AccountsFragmentListViewAdapter;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.models.AccountMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.FRAGMENT_ACCOUNTS;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_ACCOUNT;
import static com.finappl.utils.Constants.FRAGMENT_DELETE_CONFIRM;
import static com.finappl.utils.Constants.IMAGE_OBJECT;
import static com.finappl.utils.Constants.IMAGE_SELECTED_ACCOUNT;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.SELECTED_GENERIC_OBJECT;
import static com.finappl.utils.Constants.SELECTED_IMAGE_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_VIEW;

/**
 * Created by ajit on 21/3/16.
 */
public class AccountsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.accountsRLId)
    RelativeLayout accountsRL;

    @InjectView(R.id.accountsLVId)
    ListView accountsLV;
    /*Components*/

    private List<AccountMO> accountsList;
    private UserMO user;

    /*Database Service*/
    private CalendarDbService calendarDbService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accounts, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    @OnClick(R.id.accountsAddUpdateTVId)
    public void showAddUpdateAccount(){
        AccountMO account = new AccountMO();
        account.setACC_IMG(String.valueOf(IMAGE_SELECTED_ACCOUNT));
        showAddUpdateAccount(account);
    }

    @OnClick(R.id.accountsCloseTVId)
    public void close(){
        dismiss();
    }

    private void getDataFromBundle() {
        user = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        getMasterData();

        AccountsFragmentListViewAdapter adapter = new AccountsFragmentListViewAdapter(mContext, user, accountsList, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(R.id.accountDeleteIVId == view.getId()){
                    //check for transactions & budgets which were created using the account & warn user that all the transactions/budgets will be marked to default account
                    showDeleteConfirm((AccountMO)view.getTag());
                }
                else if(R.id.accountModifyIVId == view.getId()){
                    showAddUpdateAccount((AccountMO)view.getTag());
                }
                else{
                    FinappleUtility.showSnacks(accountsRL, UN_IDENTIFIED_VIEW, OK, Snackbar.LENGTH_INDEFINITE);
                }
            }
        });
        accountsLV.setAdapter(adapter);
    }

    private void showDeleteConfirm(AccountMO account){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_DELETE_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_GENERIC_OBJECT, account);
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ACCOUNTS);

        DeleteConfirmFragment fragment = new DeleteConfirmFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_DELETE_CONFIRM);
    }

    private void showAddUpdateAccount(AccountMO account){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_ACCOUNT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_IMAGE_OBJECT, account);
        bundle.putSerializable(IMAGE_OBJECT, (Serializable) accountsList);
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ACCOUNTS);

        AddUpdateAccountFragment fragment = new AddUpdateAccountFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_ADD_UPDATE_ACCOUNT);
    }

    private void getMasterData() {
        accountsList = calendarDbService.getAllAccounts(user.getUSER_ID());
    }

    private void initComps(){
        setFont(accountsRL);
    }

    // Empty constructor required for DialogFragment
    public AccountsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        initDb();
    }

    private void initDb() {
        calendarDbService = new CalendarDbService(mContext);
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

    public void onFragmentClose(String messageStr) {
        FinappleUtility.showSnacks(accountsRL, messageStr, OK, Snackbar.LENGTH_LONG);
        setupPage();
    }

    //this method is called when u have to change all over the db where this account was used and then delete the account
    public void normalizeImpactsAndDeleteAccount(AccountMO account) {
        calendarDbService.updateAll(user, account);
        deleteAccount(account);
    }

    //this method is called when we just have to delete the account which has not been used anywhere
    public void deleteAccount(AccountMO account) {
        calendarDbService.deleteAccount(account.getACC_ID());
        FinappleUtility.showSnacks(accountsRL, "Account deleted !", OK, Snackbar.LENGTH_LONG);
        setupPage();
        ((CalendarActivity)getActivity()).initActivity();
    }
}
