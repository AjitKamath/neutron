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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.models.AccountMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.SpentOnMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_CATEGORY_OBJECT;
import static com.finappl.utils.Constants.SELECTED_GENERIC_OBJECT;
import static com.finappl.utils.Constants.SELECTED_SPENTON_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_OBJECT_TYPE;

/**
 * Created by ajit on 21/3/16.
 */
public class DeleteConfirmFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.deleteConfirmLLId)
    LinearLayout deleteConfirmLL;

    @InjectView(R.id.deleteConfirmTransactionsLLId)
    LinearLayout deleteConfirmTransactionsLL;

    @InjectView(R.id.deleteConfirmTransactionsCountTVId)
    TextView deleteConfirmTransactionsCountTV;

    @InjectView(R.id.deleteConfirmTransfersLLId)
    LinearLayout deleteConfirmTransfersLL;

    @InjectView(R.id.deleteConfirmTransfersCountTVId)
    TextView deleteConfirmTransfersCountTV;

    @InjectView(R.id.deleteConfirmBudgetsLLId)
    LinearLayout deleteConfirmBudgetsLL;

    @InjectView(R.id.deleteConfirmBudgetsCountTVId)
    TextView deleteConfirmBudgetsCountTV;

    @InjectView(R.id.deleteConfirmDefaultIVId)
    ImageView deleteConfirmDefaultIV;

    @InjectView(R.id.deleteConfirmDefaultTVId)
    TextView deleteConfirmDefaultTV;
    /*Components*/

    /*Database connections*/
    private CalendarDbService calendarDbService;

    private Object object;
    private UserMO user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_confirm, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    @OnClick(R.id.deleteConfirmOKTVId)
    public void normalizeAndDelete(){
        if(getTargetFragment() instanceof CategoriesFragment){
            CategoriesFragment fragment = (CategoriesFragment) getTargetFragment();
            CategoryMO category = (CategoryMO) getArguments().get(SELECTED_GENERIC_OBJECT);
            category.setDefaultCategoryId(((CategoryMO)deleteConfirmDefaultIV.getTag()).getCAT_ID());
            fragment.normalizeImpactsAndDeleteCategory(category);
        }
        if(getTargetFragment() instanceof AccountsFragment){
            AccountsFragment fragment = (AccountsFragment) getTargetFragment();
            AccountMO account = (AccountMO) getArguments().get(SELECTED_GENERIC_OBJECT);
            account.setDefaultAccountId(((AccountMO)deleteConfirmDefaultIV.getTag()).getACC_ID());
            fragment.normalizeImpactsAndDeleteAccount(account);
        }
        if(getTargetFragment() instanceof SpentonsFragment){
            SpentonsFragment fragment = (SpentonsFragment) getTargetFragment();
            SpentOnMO spentOn = (SpentOnMO) getArguments().get(SELECTED_GENERIC_OBJECT);
            spentOn.setDefaultSpentonId(((SpentOnMO)deleteConfirmDefaultIV.getTag()).getSPNT_ON_ID());
            fragment.normalizeImpactsAndDeleteSpenton(spentOn);
        }
        else{
            FinappleUtility.showSnacks(deleteConfirmLL, UN_IDENTIFIED_OBJECT_TYPE, OK, LENGTH_INDEFINITE);
            return;
        }

        close();
    }

    @OnClick(R.id.deleteConfirmCancelTVId)
    public void close(){
        dismiss();
    }

    private void getDataFromBundle() {
        object =  getArguments().get(SELECTED_GENERIC_OBJECT);
        user = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        //check for impacts and set it
        int transactionsCount = 0;
        int transfersCount = 0;
        int budgetsCount = 0;

        String defaultImageStr = "";
        String defaultTextStr = "";

        //category
        if(object instanceof CategoryMO){
            CategoryMO category = (CategoryMO) object;
            //transactions
            transactionsCount = calendarDbService.getTransactionsCount(user, category);
            //budgets
            budgetsCount = calendarDbService.getBudgetsCount(user, category);
            //No transfers for category

            category = calendarDbService.getDefaultCategory(user.getUSER_ID());
            defaultImageStr = category.getCAT_IMG();
            defaultTextStr = category.getCAT_NAME();

            deleteConfirmDefaultIV.setTag(category);
        }
        //account
        else if(object instanceof AccountMO){
            AccountMO account = (AccountMO) object;
            //transactions
            transactionsCount = calendarDbService.getTransactionsCount(user, account);
            //transfers
            transfersCount = calendarDbService.getTransfersCount(user, account);
            //budgets
            budgetsCount = calendarDbService.getBudgetsCount(user, account);

            account = calendarDbService.getDefaultAccount(user.getUSER_ID());
            defaultImageStr = account.getACC_IMG();
            defaultTextStr = account.getACC_NAME();

            deleteConfirmDefaultIV.setTag(account);
        }
        //Spent On
        else if(object instanceof SpentOnMO){
            SpentOnMO spentOn = (SpentOnMO) object;
            transactionsCount = calendarDbService.getTransactionsCount(user, spentOn);
            //budgets
            budgetsCount = calendarDbService.getBudgetsCount(user, spentOn);
            //No transfers for spent on

            spentOn = calendarDbService.getDefaultSpentOn(user.getUSER_ID());
            defaultImageStr = spentOn.getSPNT_ON_IMG();
            defaultTextStr = spentOn.getSPNT_ON_NAME();

            deleteConfirmDefaultIV.setTag(spentOn);
        }

        //if there are no transactions, transfers and budgets then show delete confirmation
        if(transactionsCount == 0 && transfersCount == 0 && budgetsCount == 0){
            showDelete();
            dismiss();
            return;
        }

        //transactions
        if(transactionsCount > 0){
            deleteConfirmTransactionsCountTV.setText(String.valueOf(transactionsCount));
        }
        else{
            deleteConfirmTransactionsLL.setVisibility(View.GONE);
        }
        //transfers
        if(transfersCount > 0){
            deleteConfirmTransfersCountTV.setText(String.valueOf(transfersCount));
        }
        else{
            deleteConfirmTransfersLL.setVisibility(View.GONE);
        }
        //budgets
        if(budgetsCount > 0){
            deleteConfirmBudgetsCountTV.setText(String.valueOf(budgetsCount));
        }
        else{
            deleteConfirmBudgetsLL.setVisibility(View.GONE);
        }

        //set default
        deleteConfirmDefaultIV.setBackgroundResource(Integer.parseInt(defaultImageStr));
        deleteConfirmDefaultTV.setText(defaultTextStr);
    }

    private void showDelete() {
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        if(object instanceof CategoryMO){
            bundle.putSerializable(CONFIRM_MESSAGE, "Delete Category ?");
            bundle.putSerializable(SELECTED_CATEGORY_OBJECT, (CategoryMO)object);
        }
        else if(object instanceof AccountMO){
            bundle.putSerializable(CONFIRM_MESSAGE, "Delete Account ?");
            bundle.putSerializable(SELECTED_ACCOUNT_OBJECT, (AccountMO)object);
        }
        else if(object instanceof SpentOnMO){
            bundle.putSerializable(CONFIRM_MESSAGE, "Delete Spent On ?");
            bundle.putSerializable(SELECTED_SPENTON_OBJECT, (SpentOnMO)object);
        }

        Fragment currentFrag = getTargetFragment();

        ConfirmFragment fragment = new ConfirmFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_CONFIRM);
    }

    private void initComps(){


        setFont(deleteConfirmLL);
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
    public DeleteConfirmFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

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
}
