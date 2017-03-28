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
import com.finappl.adapters.AccountsFragmentListViewAdapter;
import com.finappl.adapters.BudgetsFragmentListViewAdapter;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.models.AccountMO;
import com.finappl.models.BudgetMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.BUDGET_OBJECT;
import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.FRAGMENT_ACCOUNTS;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_ACCOUNT;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_BUDGET;
import static com.finappl.utils.Constants.FRAGMENT_BUDGETS;
import static com.finappl.utils.Constants.FRAGMENT_BUDGET_DETAILS;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.FRAGMENT_DELETE_CONFIRM;
import static com.finappl.utils.Constants.IMAGE_OBJECT;
import static com.finappl.utils.Constants.IMAGE_SELECTED_ACCOUNT;
import static com.finappl.utils.Constants.IMAGE_SELECTED_BUDGET;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.SELECTED_GENERIC_OBJECT;
import static com.finappl.utils.Constants.SELECTED_IMAGE_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_VIEW;

/**
 * Created by ajit on 21/3/16.
 */
public class BudgetsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.budgets_rl)
    RelativeLayout budgets_rl;

    @InjectView(R.id.budgets_no_budgets_tv)
    TextView budgets_no_budgets_tv;

    @InjectView(R.id.budgets_lv)
    ListView budgets_lv;
    /*Components*/

    private List<BudgetMO> budgetsList;
    private UserMO user;

    /*Database Service*/
    private CalendarDbService calendarDbService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budgets, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    @OnClick(R.id.budgets_add_update_iv)
    public void showAddUpdateBudget(){
        showAddUpdateBudget(null);
    }

    @OnClick(R.id.budgets_close_iv)
    public void close(){
        dismiss();
    }

    private void getDataFromBundle() {
        user = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        getMasterData();

        //if there are no budgets
        if(budgetsList != null &&  !budgetsList.isEmpty()){
            budgets_no_budgets_tv.setVisibility(View.GONE);
        }

        BudgetsFragmentListViewAdapter adapter = new BudgetsFragmentListViewAdapter(mContext, user, budgetsList, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(R.id.budget_delete_iv == view.getId()){
                    showDeleteConfirm((BudgetMO) view.getTag(R.layout.budgets_budget));
                }
                else if(R.id.budget_modify_iv == view.getId()){
                    showAddUpdateBudget((BudgetMO) view.getTag(R.layout.budgets_budget));
                }
                else{
                    FinappleUtility.showSnacks(budgets_rl, UN_IDENTIFIED_VIEW, OK, Snackbar.LENGTH_INDEFINITE);
                }
            }
        });
        budgets_lv.setAdapter(adapter);
    }

    private void showDeleteConfirm(BudgetMO budget){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFIRM_MESSAGE, "Delete Budget ?");
        bundle.putSerializable(SELECTED_GENERIC_OBJECT, budget);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_BUDGETS);

        ConfirmFragment fragment = new ConfirmFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_DELETE_CONFIRM);
    }

    private void showAddUpdateBudget(BudgetMO budget){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_BUDGET);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(BUDGET_OBJECT, budget);
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_BUDGETS);

        AddUpdateBudgetFragment fragment = new AddUpdateBudgetFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_ADD_UPDATE_BUDGET);
    }

    private void getMasterData() {
        budgetsList = calendarDbService.getAllBudgets(new Date(), user.getUSER_ID());
    }

    private void initComps(){
        setFont(budgets_rl);
    }

    // Empty constructor required for DialogFragment
    public BudgetsFragment() {}

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

    public void deleteBudget(BudgetMO budget) {
        calendarDbService.deleteBudget(budget.getBUDGET_ID());
        FinappleUtility.showSnacks(budgets_rl, "Budget deleted !", OK, Snackbar.LENGTH_LONG);
        setupPage();
    }

    public void onFragmentClose(String messageStr) {
        FinappleUtility.showSnacks(budgets_rl, "Budget Added !", OK, Snackbar.LENGTH_LONG);
        setupPage();
    }
}
