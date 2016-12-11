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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.dbServices.BudgetsDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.models.AccountMO;
import com.finappl.models.BudgetMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.RepeatMO;
import com.finappl.models.SpentOnMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.ACCOUNT_LOW_BALANCE_LIMIT;
import static com.finappl.utils.Constants.ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.BUDGET_OBJECT;
import static com.finappl.utils.Constants.CATEGORY_OBJECT;
import static com.finappl.utils.Constants.DB_AFFIRMATIVE;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_BUDGET;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_ACCOUNT;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_AMOUNT;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_CATEGORY;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_REPEAT;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_SPENTON;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.REPEAT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_AMOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_CATEGORY_OBJECT;
import static com.finappl.utils.Constants.SELECTED_REPEAT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_SPENTON_OBJECT;
import static com.finappl.utils.Constants.SOMETHING_WENT_WRONG;
import static com.finappl.utils.Constants.SPENTON_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class AddUpdateBudgetFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.budgetRLId)
    RelativeLayout budgetRL;

    @InjectView(R.id.budgetAmountTVId)
    TextView budgetAmountTV;

    @InjectView(R.id.budgetNameETId)
    EditText budgetNameET;

    @InjectView(R.id.budgetGroupRadioGrpId)
    RadioGroup budgetGroupRadioGrp;

    @InjectView(R.id.budgetCategoryLLId)
    LinearLayout budgetCategoryLL;

    @InjectView(R.id.budgetAccountLLId)
    LinearLayout budgetAccountLL;

    @InjectView(R.id.budgetSpentOnLLId)
    LinearLayout budgetSpentOnLL;

    @InjectView(R.id.budgetTypeLLId)
    LinearLayout budgetTypeLL;

    @InjectView(R.id.budgetNoteETId)
    EditText budgetNoteET;
    /*Components*/

    private CalendarDbService calendarDbService;
    private BudgetsDbService budgetsDbService;

    private List<CategoryMO> categoriesList;
    private List<AccountMO> accountList;
    private List<SpentOnMO> spentOnList;

    private List<RepeatMO> repeatsList;

    private UserMO loggedInUserObj;
    private BudgetMO budget;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_budget, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    @OnClick(R.id.budgetSaveTVId)
    public void saveUpdateBudget(){
        validateAndGetInputs();

        if(budget == null){
            return;
        }

        budget.setUSER_ID(loggedInUserObj.getUSER_ID());

        //new add_update_budget
        String messageStr = SOMETHING_WENT_WRONG;
        if(budget.getBUDGET_ID() == null){
            long result = budgetsDbService.addNewBudget(budget);

            if (result == -1) {
                messageStr = "Failed to create a new Budget !";
            } else {
                messageStr = "New Budget created";
            }
        }
        //old add_update_budget
        else{
            long result = budgetsDbService.updateOldBudget(budget);
            if(result == 0) {
                messageStr = "Failed to update Budget !";
            } else if(result == 1){
                messageStr = "Budget updated";
            }
        }

        closeFragment(messageStr);
    }

    @OnClick(R.id.budgetTypeLLId)
    public void showBudgetTypeFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_REPEAT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(REPEAT_OBJECT, (Serializable) repeatsList);
        bundle.putSerializable(SELECTED_REPEAT_OBJECT, ((RepeatMO)budgetTypeLL.getTag()).getREPEAT_NAME());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_BUDGET);

        SelectRepeatFragment fragment = new SelectRepeatFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_SELECT_REPEAT);
    }

    @OnClick(R.id.budgetSpentOnLLId)
    public void showSpentonFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_SPENTON);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SPENTON_OBJECT, (Serializable) spentOnList);
        bundle.putSerializable(SELECTED_SPENTON_OBJECT, ((SpentOnMO)budgetSpentOnLL.getTag()).getSPNT_ON_ID());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_BUDGET);

        SelectSpentonFragment fragment = new SelectSpentonFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_SELECT_SPENTON);
    }

    @OnClick(R.id.budgetAccountLLId)
    public void showAccountFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_ACCOUNT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(ACCOUNT_OBJECT, (Serializable) accountList);
        bundle.putSerializable(SELECTED_ACCOUNT_OBJECT, ((AccountMO)budgetAccountLL.getTag()).getACC_ID());
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_BUDGET);

        SelectAccountFragment fragment = new SelectAccountFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_SELECT_ACCOUNT);
    }

    @OnClick(R.id.budgetCategoryLLId)
    public void showCategoryFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_CATEGORY);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CATEGORY_OBJECT, (Serializable) categoriesList);
        bundle.putSerializable(SELECTED_CATEGORY_OBJECT, ((CategoryMO)budgetCategoryLL.getTag()).getCAT_ID());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_BUDGET);

        SelectCategoryFragment fragment = new SelectCategoryFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_SELECT_CATEGORY);
    }

    @OnClick(R.id.budgetAmountTVId)
    public void showAmountFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_AMOUNT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_AMOUNT_OBJECT, String.valueOf(budgetAmountTV.getText()));
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_BUDGET);

        SelectAmountFragment fragment = new SelectAmountFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_SELECT_AMOUNT);
    }

    private void initComps() {
        budgetGroupRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(R.id.budgetCategoryRadioId == i){
                    showBudgetFor("CATEGORY");
                }
                else if(R.id.budgetAccountRadioId == i){
                    showBudgetFor("ACCOUNT");
                }
                else if(R.id.budgetSpentOnRadioId == i){
                    showBudgetFor("SPENT ON");
                }
            }
        });

        setFont(budgetRL);
    }

    public BudgetMO validateAndGetInputs(){
        String budgetAmtStr = String.valueOf(budgetAmountTV.getText());
        String budgetNameStr = String.valueOf(budgetNameET.getText());
        String budgetNotesStr = String.valueOf(budgetNoteET.getText());

        budgetAmtStr = budgetAmtStr.replace(",","");

        if(FinappleUtility.isAmountZero(budgetAmtStr)){
            FinappleUtility.showSnacks(budgetRL, "Budget Amount cannot be Zero", OK, Snackbar.LENGTH_LONG);
            return null;
        }
        else if(budgetNameStr == null || budgetNameStr.trim().isEmpty()){
            FinappleUtility.showSnacks(budgetRL, "Budget Name cannot be empty", OK, Snackbar.LENGTH_LONG);
            return null;
        }

        budget.setBUDGET_AMT(Double.parseDouble(budgetAmtStr));
        budget.setBUDGET_NAME(budgetNameStr);
        budget.setBUDGET_GRP_TYPE(String.valueOf(budgetGroupRadioGrp.findViewById(budgetGroupRadioGrp.getCheckedRadioButtonId()).getTag()));
        if("CATEGORY".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())){
            budget.setBUDGET_GRP_ID(((CategoryMO)budgetCategoryLL.getTag()).getCAT_ID());
        }
        else if("ACCOUNT".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())){
            budget.setBUDGET_GRP_ID(((AccountMO)budgetAccountLL.getTag()).getACC_ID());
        }
        else if("SPENT ON".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())){
            budget.setBUDGET_GRP_ID(((SpentOnMO)budgetSpentOnLL.getTag()).getSPNT_ON_ID());
        }
        else{
            FinappleUtility.showSnacks(budgetRL, SOMETHING_WENT_WRONG, OK, Snackbar.LENGTH_INDEFINITE);
            return null;
        }
        budget.setBUDGET_TYPE(((RepeatMO)budgetTypeLL.getTag()).getREPEAT_NAME());
        budget.setBUDGET_NOTE(budgetNotesStr);

        return budget;
    }

    private void getDataFromBundle() {
        budget = (BudgetMO) getArguments().get(BUDGET_OBJECT);
        loggedInUserObj = (UserMO) getArguments().get(LOGGED_IN_OBJECT);

        if(budget == null){
            budget = new BudgetMO();
        }
    }

    private void setupPage() {
        //common setup
        getMasterData();

        /*default page setup*/
        showBudgetFor("CATEGORY");

        //set up select_category_category
        setCategory(getDefaultCategory(categoriesList));

        //set up calendar_tab_account
        setAccount(getDefaultAccount(accountList));

        //set up spent on
        setSpenton(getDefaultSpenton(spentOnList));

        //set up add_update_budget type
        setBudgetType(getDefaultRepeat(repeatsList));
         /*default page setup*/

        //if add_update_budget is being edited
        if(budget != null && budget.getBUDGET_ID() != null){
            //add_update_budget select_amount
            budgetAmountTV.setText(FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), String.valueOf(budget.getBUDGET_AMT())));

            budgetNameET.setText(budget.getBUDGET_NAME());

            //set add_update_budget type radio button
            if("CATEGORY".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())){
                budgetGroupRadioGrp.check(budgetRL.findViewWithTag("CATEGORY").getId());
                setCategory(getCategoryOnId(categoriesList, budget.getBUDGET_GRP_ID()));
                showBudgetFor("CATEGORY");
            }
            else if("ACCOUNT".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())){
                budgetGroupRadioGrp.check(budgetRL.findViewWithTag("ACCOUNT").getId());
                setAccount(getAccountOnId(accountList, budget.getBUDGET_GRP_ID()));
                showBudgetFor("ACCOUNT");
            }
            else if("SPENT ON".equalsIgnoreCase(budget.getBUDGET_GRP_TYPE())){
                budgetGroupRadioGrp.check(budgetRL.findViewWithTag("SPENT ON").getId());
                setSpenton(getSpentonOnId(spentOnList, budget.getBUDGET_GRP_ID()));
                showBudgetFor("SPENT ON");
            }

            //set add_update_budget type
            setBudgetType(getRepeatOnId(repeatsList, budget.getBUDGET_TYPE()));

            //set notes
            budgetNoteET.setText(budget.getBUDGET_NOTE());
        }
    }

    @OnClick(R.id.budgetCloseTVId)
    public void close(){
        dismiss();
    }

    private void closeFragment(String messageStr){
        close();

        ((CalendarActivity)getActivity()).showSnacks(messageStr, OK, Snackbar.LENGTH_SHORT);
        ((CalendarActivity)getActivity()).initActivity();
    }

    private AccountMO getAccountOnId(List<AccountMO> accountList, String accountIdStr){
        for(AccountMO iterList : accountList){
            if(iterList.getACC_ID().equalsIgnoreCase(accountIdStr)){
                return iterList;
            }
        }
        return null;
    }

    private CategoryMO getCategoryOnId(List<CategoryMO> categoriesList, String cateforyIdStr){
        for(CategoryMO iterList : categoriesList){
            if(iterList.getCAT_ID().equalsIgnoreCase(cateforyIdStr)){
                return iterList;
            }
        }
        return null;
    }

    private SpentOnMO getSpentonOnId(List<SpentOnMO> spentOnList, String spentonIdStr){
        for(SpentOnMO iterList : spentOnList){
            if(iterList.getSPNT_ON_ID().equalsIgnoreCase(spentonIdStr)){
                return iterList;
            }
        }
        return null;
    }

    private RepeatMO getRepeatOnId(List<RepeatMO> repeatMOList, String repeatIdStr){
        for(RepeatMO iterList : repeatMOList){
            if(iterList.getREPEAT_ID().equalsIgnoreCase(repeatIdStr)){
                return iterList;
            }
        }
        return null;
    }

    private RepeatMO getDefaultRepeat(List<RepeatMO> repeatMOList){
        for(RepeatMO iterList : repeatMOList){
            if(iterList.getREPEAT_IS_DEF().equalsIgnoreCase(DB_AFFIRMATIVE)){
                return iterList;
            }
        }
        return null;
    }

    private void setBudgetType(RepeatMO repeatMo){
        ((TextView)budgetTypeLL.findViewById(R.id.budgetTypeTVId)).setText(repeatMo.getREPEAT_NAME());
        budgetTypeLL.setTag(repeatMo);
    }

    private void showBudgetFor(String whichBudgetStr){
        budgetCategoryLL.setVisibility(View.GONE);
        budgetAccountLL.setVisibility(View.GONE);
        budgetSpentOnLL.setVisibility(View.GONE);

        if(whichBudgetStr.equalsIgnoreCase("CATEGORY")){
            budgetCategoryLL.setVisibility(View.VISIBLE);
        }
        else if(whichBudgetStr.equalsIgnoreCase("ACCOUNT")){
            budgetAccountLL.setVisibility(View.VISIBLE);
        }
        else if(whichBudgetStr.equalsIgnoreCase("SPENT ON")){
            budgetSpentOnLL.setVisibility(View.VISIBLE);
        }
    }

    private void setCategory(CategoryMO categoryMO){
        ((TextView)budgetCategoryLL.findViewById(R.id.budgetCategoryTVId)).setText(categoryMO.getCAT_NAME());
        budgetCategoryLL.findViewById(R.id.budgetCategoryIVId).setBackgroundResource(Integer.parseInt(categoryMO.getCAT_IMG()));
        budgetCategoryLL.setTag(categoryMO);
    }

    private void setAccount(AccountMO accountsMO){
        ((TextView)budgetAccountLL.findViewById(R.id.budgetAccountTVId)).setText(accountsMO.getACC_NAME());
        budgetAccountLL.findViewById(R.id.budgetAccountIVId).setBackgroundResource(Integer.parseInt(accountsMO.getACC_IMG()));

        TextView budgetAccountTotalTV = (TextView) budgetAccountLL.findViewById(R.id.budgetAccountTotalTVId);
        budgetAccountTotalTV = FinappleUtility.formatAmountView(budgetAccountTotalTV, loggedInUserObj, accountsMO.getACC_TOTAL());

        if(accountsMO.getACC_TOTAL() < ACCOUNT_LOW_BALANCE_LIMIT){
            budgetAccountTotalTV.setVisibility(View.VISIBLE);
        }
        else{
            budgetAccountTotalTV.setVisibility(View.GONE);
        }
        budgetAccountLL.setTag(accountsMO);
    }

    private void setSpenton(SpentOnMO spentOnMO){
        ((TextView)budgetSpentOnLL.findViewById(R.id.budgetSpentonTVId)).setText(spentOnMO.getSPNT_ON_NAME());
        budgetSpentOnLL.findViewById(R.id.budgetSpentonIVId).setBackgroundResource(Integer.parseInt(spentOnMO.getSPNT_ON_IMG()));

        budgetSpentOnLL.setTag(spentOnMO);
    }

    private AccountMO getDefaultAccount(List<AccountMO> accountList){
        for(AccountMO iterList : accountList){
            if(iterList.getACC_IS_DEF().equalsIgnoreCase(DB_AFFIRMATIVE)){
                return iterList;
            }
        }
        return null;
    }

    private CategoryMO getDefaultCategory(List<CategoryMO> categoriesList){
        for(CategoryMO iterList : categoriesList){
            if(iterList.getCAT_IS_DEF().equalsIgnoreCase(DB_AFFIRMATIVE)){
                return iterList;
            }
        }
        return null;
    }

    private SpentOnMO getDefaultSpenton(List<SpentOnMO> spentOnList){
        for(SpentOnMO iterList : spentOnList){
            if(iterList.getSPNT_ON_IS_DEF().equalsIgnoreCase(DB_AFFIRMATIVE)){
                return iterList;
            }
        }
        return null;
    }

    private void getMasterData() {
        categoriesList = calendarDbService.getAllCategories(loggedInUserObj.getUSER_ID());
        accountList = calendarDbService.getAllAccounts(loggedInUserObj.getUSER_ID());
        spentOnList = calendarDbService.getAllSpentOns(loggedInUserObj.getUSER_ID());

        repeatsList = calendarDbService.getAllRepeats();
    }

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    // Empty constructor required for DialogFragment
    public AddUpdateBudgetFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        initDb();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    private void initDb() {
        calendarDbService = new CalendarDbService(mContext);
        budgetsDbService = new BudgetsDbService(mContext);
    }

    private void setAmount(String amountStr){
        budgetAmountTV.setText(amountStr);
    }

    public void onFinishDialog(CategoryMO categoryMO) {
        setCategory(categoryMO);
    }
    public void onFinishDialog(AccountMO accountsMO) {
        setAccount(accountsMO);
    }
    public void onFinishDialog(SpentOnMO spentOnMO) {
        setSpenton(spentOnMO);
    }
    public void onFinishDialog(RepeatMO repeatMO) {
        setBudgetType(repeatMO);
    }
    public void onFinishDialog(String amountStr) {
        setAmount(amountStr);
    }

    public interface DialogResultListener {
        void onFinishUserDialog(String str);
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