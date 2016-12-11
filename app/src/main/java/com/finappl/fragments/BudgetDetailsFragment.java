package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.dbServices.BudgetsDbService;
import com.finappl.models.BudgetMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.BUDGET_OBJECT;
import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_BUDGET;
import static com.finappl.utils.Constants.FRAGMENT_BUDGET_DETAILS;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class BudgetDetailsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    @InjectView(R.id.budgetDetailsLLId)
    LinearLayout budgetDetailsLL;

    @InjectView(R.id.budgetDetailsNameTVId)
    TextView budgetDetailsNameTV;

    @InjectView(R.id.budgetDetailsGroupIVId)
    ImageView budgetDetailsGroupIV;

    @InjectView(R.id.budgetDetailsGroupTVId)
    TextView budgetDetailsGroupTV;

    @InjectView(R.id.budgetDetailsCurrency1TVId)
    TextView budgetDetailsCurrency1TV;

    @InjectView(R.id.budgetDetailsCurrency2TVId)
    TextView budgetDetailsCurrency2TV;

    @InjectView(R.id.budgetDetailsTotalTVId)
    TextView budgetDetailsTotalTV;

    @InjectView(R.id.budgetDetailsAmountTVId)
    TextView budgetDetailsAmountTV;

    @InjectView(R.id.budgetDetailsNoteLLId)
    LinearLayout budgetDetailsNoteLL;

    @InjectView(R.id.budgetDetailsNoteTVId)
    TextView budgetDetailsNoteTV;

    @InjectView(R.id.budgetDetailsTypeTVId)
    TextView budgetDetailsTypeTV;
    //end of components

    //db services
    private BudgetsDbService budgetsDbService;

    private BudgetMO budget;
    private UserMO loggedInUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budget_details, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    private void getDataFromBundle() {
        budget = (BudgetMO) getArguments().get(BUDGET_OBJECT);
        loggedInUser = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        if(budget == null){
            Log.e(CLASS_NAME, "Disaster !!! add_update_budget object is null");
            showToast("Catastrophic Error !!");
            return;
        }
        else if(budget.getBUDGET_ID() == null || budget.getBUDGET_ID().trim().isEmpty()){
            Log.e(CLASS_NAME, "Disaster !!! BUDGET_ID is null/empty");
            showToast("Catastrophic Error !!");
            return;
        }

        budgetDetailsGroupIV.setBackgroundResource(Integer.parseInt(budget.getBudgetGroupImage()));
        budgetDetailsGroupTV.setText(budget.getBudgetGroupName());
        budgetDetailsCurrency1TV.setText(loggedInUser.getCUR_CODE());
        budgetDetailsCurrency2TV.setText(loggedInUser.getCUR_CODE());
        budgetDetailsNameTV.setText(budget.getBUDGET_NAME());
        budgetDetailsTypeTV.setText(budget.getBUDGET_TYPE());

        budgetDetailsTotalTV = FinappleUtility.formatAmountView(budgetDetailsTotalTV, loggedInUser, budget.getBudgetRangeTotal());
        budgetDetailsAmountTV = FinappleUtility.formatAmountView(budgetDetailsAmountTV, loggedInUser, budget.getBUDGET_AMT());

        budgetDetailsAmountTV.setTextColor(budgetDetailsAmountTV.getResources().getColor(R.color.finappleCurrencyPosColor));
        if(budget.getBudgetRangeTotal() < budget.getBUDGET_AMT()){
            budgetDetailsTotalTV.setTextColor(budgetDetailsTotalTV.getResources().getColor(R.color.finappleCurrencyPosColor));
        }
        else{
            budgetDetailsTotalTV.setTextColor(budgetDetailsTotalTV.getResources().getColor(R.color.finappleCurrencyNegColor));
        }

        if(budget.getBUDGET_NOTE() != null && !budget.getBUDGET_NOTE().trim().isEmpty()){
            budgetDetailsNoteLL.setVisibility(View.VISIBLE);
            budgetDetailsNoteTV.setText(budget.getBUDGET_NOTE());
        }
        else{
            budgetDetailsNoteLL.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.budgetDetailsEditTVId)
    public void onEdit(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_BUDGET);

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(BUDGET_OBJECT, budget);
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUser);

        AddUpdateBudgetFragment fragment = new AddUpdateBudgetFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_ADD_UPDATE_BUDGET);

        //dismiss current fragment
        dismiss();
    }

    private void initComps(){
        setFont(budgetDetailsLL);
    }

    @OnClick(R.id.budgetDetailsDeleteTVId)
    public void confirmDelete(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFIRM_MESSAGE, "Delete Budget ?");

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_BUDGET_DETAILS);

        ConfirmFragment fragment = new ConfirmFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_CONFIRM);
    }

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    // Empty constructor required for DialogFragment
    public BudgetDetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        budgetsDbService = new BudgetsDbService(mContext);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
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

    private void closeFragment(String messageStr){
        dismiss();
    }

    public void onFinishDialog(String messageStr) {
        if(!budgetsDbService.deleteBudget(budget.getBUDGET_ID())){
            messageStr = "Could not delete the Budget";
        }

        closeFragment(messageStr);
        ((CalendarActivity)getActivity()).initActivity();
        ((CalendarActivity)getActivity()).showSnacks(messageStr, OK, Snackbar.LENGTH_LONG);
    }
}