package com.finapple.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finapple.R;
import com.finapple.adapters.BudgetsViewSectionListAdapter;
import com.finapple.adapters.ManageContentCategoriesSectionListAdapter;
import com.finapple.dbServices.AuthorizationDbService;
import com.finapple.dbServices.BudgetsViewDbService;
import com.finapple.model.BudgetsViewModel;
import com.finapple.model.UsersModel;

import java.util.Map;

public class BudgetsViewActivity extends Activity {
	private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    //User
    private UsersModel loggedInUserObj;

    //db service
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);
    private BudgetsViewDbService budgetsViewDbService = new BudgetsViewDbService(mContext);

    //UI Components
    private LinearLayout budgetsDailyTabLL;
    private LinearLayout budgetsWeeklyTabLL;
    private LinearLayout budgetsMonthlyTabLL;
    private LinearLayout budgetsYearlyTabLL;

    private  TextView budgetsDailyTabTV;
    private  TextView budgetsWeeklyTabTV;
    private  TextView budgetsMonthlyTabTV;
    private  TextView budgetsYearlyTabTV;

    private  LinearLayout budgetsDailyLL;
    private  LinearLayout budgetsWeeklyLL;
    private  LinearLayout budgetsMonthlyLL;
    private  LinearLayout budgetsYearlyLL;

    @Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.budgets_view);
        Log.e(CLASS_NAME, "Navigated to Budgets Screen");

        //get the Active user
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return;
        }

        //init ui components
        initUiComponents();

        //set up tab default and lists
        setUpTabs();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.budgetsRLId), robotoCondensedLightFont);

    }

    private void setUpTabs() {
        //reset all tabs
        resetTabs();

        ListView budgetsDailyLV = (ListView) this.findViewById(R.id.budgetsDailyLVId);
        ListView budgetsWeeklyLV = (ListView) this.findViewById(R.id.budgetsWeeklyLVId);
        ListView budgetsMonthlyLV = (ListView) this.findViewById(R.id.budgetsMonthlyLVId);
        ListView budgetsYearlyLV = (ListView) this.findViewById(R.id.budgetsYearlyLVId);

        //Counters
        TextView budgetsDailyTabCounterTV = (TextView) this.findViewById(R.id.budgetsDailyTabCounterTVId);
        TextView budgetsWeeklyTabCounterTV = (TextView) this.findViewById(R.id.budgetsWeeklyTabCounterTVId);
        TextView budgetsMonthlyTabCounterTV = (TextView) this.findViewById(R.id.budgetsMonthlyTabCounterTVId);
        TextView budgetsYearlyTabCounterTV = (TextView) this.findViewById(R.id.budgetsYearlyTabCounterTVId);

        //get all the budgets_view.. daily, weekly, monthly and yearly from the db
        BudgetsViewModel allBudgetsViewObj = budgetsViewDbService.getAllBudgets(loggedInUserObj.getUSER_ID());

        //set daily budgets list
        BudgetsViewSectionListAdapter budgetsViewSectionListAdapter = new BudgetsViewSectionListAdapter(mContext, R.layout
                .budgets_view_list_view_header, R.layout.budgets_view_list_view_content, allBudgetsViewObj.getBudgetDailyMap());
        budgetsViewSectionListAdapter.notifyDataSetChanged();
        budgetsDailyLV.setAdapter(budgetsViewSectionListAdapter);

        int budgetListCounter = 0;
        if(allBudgetsViewObj.getBudgetDailyMap().containsKey("CATEGORY")){
            budgetListCounter += allBudgetsViewObj.getBudgetDailyMap().get("CATEGORY").size();
        }
        if(allBudgetsViewObj.getBudgetDailyMap().containsKey("ACCOUNT")){
            budgetListCounter += allBudgetsViewObj.getBudgetDailyMap().get("ACCOUNT").size();
        }
        if(allBudgetsViewObj.getBudgetDailyMap().containsKey("SPENT ON")){
            budgetListCounter += allBudgetsViewObj.getBudgetDailyMap().get("SPENT ON").size();
        }
        budgetsDailyTabCounterTV.setText(String.valueOf(budgetListCounter));

        //set weekly budgets list
        budgetsViewSectionListAdapter = new BudgetsViewSectionListAdapter(mContext, R.layout
                .budgets_view_list_view_header, R.layout.budgets_view_list_view_content, allBudgetsViewObj.getBudgetWeeklyMap());
        budgetsViewSectionListAdapter.notifyDataSetChanged();
        budgetsWeeklyLV.setAdapter(budgetsViewSectionListAdapter);

        budgetListCounter = 0;
        if(allBudgetsViewObj.getBudgetWeeklyMap().containsKey("CATEGORY")){
            budgetListCounter += allBudgetsViewObj.getBudgetWeeklyMap().get("CATEGORY").size();
        }
        if(allBudgetsViewObj.getBudgetWeeklyMap().containsKey("ACCOUNT")){
            budgetListCounter += allBudgetsViewObj.getBudgetWeeklyMap().get("ACCOUNT").size();
        }
        if(allBudgetsViewObj.getBudgetWeeklyMap().containsKey("SPENT ON")){
            budgetListCounter += allBudgetsViewObj.getBudgetWeeklyMap().get("SPENT ON").size();
        }
        budgetsWeeklyTabCounterTV.setText(String.valueOf(budgetListCounter));

        //set monthly budgets list
        budgetsViewSectionListAdapter = new BudgetsViewSectionListAdapter(mContext, R.layout
                .budgets_view_list_view_header, R.layout.budgets_view_list_view_content, allBudgetsViewObj.getBudgetMonthlyMap());
        budgetsViewSectionListAdapter.notifyDataSetChanged();
        budgetsMonthlyLV.setAdapter(budgetsViewSectionListAdapter);

        budgetListCounter = 0;
        if(allBudgetsViewObj.getBudgetMonthlyMap().containsKey("CATEGORY")){
            budgetListCounter += allBudgetsViewObj.getBudgetMonthlyMap().get("CATEGORY").size();
        }
        if(allBudgetsViewObj.getBudgetMonthlyMap().containsKey("ACCOUNT")){
            budgetListCounter += allBudgetsViewObj.getBudgetMonthlyMap().get("ACCOUNT").size();
        }
        if(allBudgetsViewObj.getBudgetMonthlyMap().containsKey("SPENT ON")){
            budgetListCounter += allBudgetsViewObj.getBudgetMonthlyMap().get("SPENT ON").size();
        }
        budgetsMonthlyTabCounterTV.setText(String.valueOf(budgetListCounter));

        //set yearly budgets list
        budgetsViewSectionListAdapter = new BudgetsViewSectionListAdapter(mContext, R.layout
                .budgets_view_list_view_header, R.layout.budgets_view_list_view_content, allBudgetsViewObj.getBudgetYearlyMap());
        budgetsViewSectionListAdapter.notifyDataSetChanged();
        budgetsYearlyLV.setAdapter(budgetsViewSectionListAdapter);

        budgetListCounter = 0;
        if(allBudgetsViewObj.getBudgetYearlyMap().containsKey("CATEGORY")){
            budgetListCounter += allBudgetsViewObj.getBudgetYearlyMap().get("CATEGORY").size();
        }
        if(allBudgetsViewObj.getBudgetYearlyMap().containsKey("ACCOUNT")){
            budgetListCounter += allBudgetsViewObj.getBudgetYearlyMap().get("ACCOUNT").size();
        }
        if(allBudgetsViewObj.getBudgetYearlyMap().containsKey("SPENT ON")){
            budgetListCounter += allBudgetsViewObj.getBudgetYearlyMap().get("SPENT ON").size();
        }
        budgetsYearlyTabCounterTV.setText(String.valueOf(budgetListCounter));

        //tabs
        budgetsDailyTabLL.setBackgroundColor(budgetsDailyTabLL.getResources().getColor(R.color.activeTab));
        budgetsMonthlyTabLL.setBackgroundColor(budgetsMonthlyTabLL.getResources().getColor(R.color.inactiveTab));
        budgetsWeeklyTabLL.setBackgroundColor(budgetsWeeklyTabLL.getResources().getColor(R.color.inactiveTab));
        budgetsYearlyTabLL.setBackgroundColor(budgetsYearlyTabLL.getResources().getColor(R.color.inactiveTab));

        //labels
        budgetsDailyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.activeTab));
        budgetsMonthlyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.inactiveTab));
        budgetsWeeklyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.inactiveTab));
        budgetsYearlyTabTV.setTextColor(budgetsYearlyTabTV.getResources().getColor(R.color.inactiveTab));

        //lists
        budgetsDailyLL.setVisibility(View.VISIBLE);
        budgetsMonthlyLL.setVisibility(View.GONE);
        budgetsWeeklyLL.setVisibility(View.GONE);
        budgetsYearlyLL.setVisibility(View.GONE);
    }

    public void onTabClick(View view){
        resetTabs();

        switch(view.getId()){
            case R.id.budgetsDailyTabLLId : budgetsDailyTabLL.setBackground(budgetsDailyTabLL.getResources().getDrawable(R.color.activeTab));
                budgetsDailyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.activeTab));
                budgetsDailyLL.setVisibility(View.VISIBLE);
                break;

            case R.id.budgetsWeeklyTabLLId : budgetsWeeklyTabLL.setBackground(budgetsWeeklyTabLL.getResources().getDrawable(R.color.activeTab));
                budgetsWeeklyTabTV.setTextColor(budgetsWeeklyTabTV.getResources().getColor(R.color.activeTab));
                budgetsWeeklyLL.setVisibility(View.VISIBLE);
                break;

            case R.id.budgetsMonthlyTabLLId : budgetsMonthlyTabLL.setBackground(budgetsMonthlyTabLL.getResources().getDrawable(R.color.activeTab));
                budgetsMonthlyTabTV.setTextColor(budgetsMonthlyTabTV.getResources().getColor(R.color.activeTab));
                budgetsMonthlyLL.setVisibility(View.VISIBLE);
                break;

            case R.id.budgetsYearlyTabLLId : budgetsYearlyTabLL.setBackground(budgetsYearlyTabLL.getResources().getDrawable(R.color.activeTab));
                budgetsYearlyTabTV.setTextColor(budgetsYearlyTabTV.getResources().getColor(R.color.activeTab));
                budgetsYearlyLL.setVisibility(View.VISIBLE);
                break;

            default:
                Log.e(CLASS_NAME, "TAB error !!! Something is wrong while switching tabs !! Check where ur clicking..");
                showToast("TAB ERROR !");
        }
    }

    private void resetTabs() {
        //tabs
        budgetsDailyTabLL.setBackground(budgetsDailyTabLL.getResources().getDrawable(R.color.inactiveTab));
        budgetsMonthlyTabLL.setBackground(budgetsMonthlyTabLL.getResources().getDrawable(R.color.inactiveTab));
        budgetsWeeklyTabLL.setBackground(budgetsWeeklyTabLL.getResources().getDrawable(R.color.inactiveTab));
        budgetsYearlyTabLL.setBackground(budgetsWeeklyTabLL.getResources().getDrawable(R.color.inactiveTab));

        //labels
        budgetsDailyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.inactiveTab));
        budgetsMonthlyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.inactiveTab));
        budgetsWeeklyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.inactiveTab));
        budgetsYearlyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.inactiveTab));

        //lists
        budgetsDailyLL.setVisibility(View.GONE);
        budgetsMonthlyLL.setVisibility(View.GONE);
        budgetsWeeklyLL.setVisibility(View.GONE);
        budgetsYearlyLL.setVisibility(View.GONE);

    }

    private void initUiComponents() {
        budgetsDailyTabLL = (LinearLayout) this.findViewById(R.id.budgetsDailyTabLLId);
        budgetsWeeklyTabLL = (LinearLayout) this.findViewById(R.id.budgetsWeeklyTabLLId);
        budgetsMonthlyTabLL = (LinearLayout) this.findViewById(R.id.budgetsMonthlyTabLLId);
        budgetsYearlyTabLL = (LinearLayout) this.findViewById(R.id.budgetsYearlyTabLLId);

        budgetsDailyTabTV = (TextView) this.findViewById(R.id.budgetsDailyTabTVId);
        budgetsWeeklyTabTV = (TextView) this.findViewById(R.id.budgetsWeeklyTabTVId);
        budgetsMonthlyTabTV = (TextView) this.findViewById(R.id.budgetsMonthlyTabTVId);
        budgetsYearlyTabTV = (TextView) this.findViewById(R.id.budgetsYearlyTabTVId);

        budgetsDailyLL = (LinearLayout) this.findViewById(R.id.budgetsDailyLLId);
        budgetsWeeklyLL = (LinearLayout) this.findViewById(R.id.budgetsWeeklyLLId);
        budgetsMonthlyLL = (LinearLayout) this.findViewById(R.id.budgetsMonthlyLLId);
        budgetsYearlyLL = (LinearLayout) this.findViewById(R.id.budgetsYearlyLLId);
    }

    public void onBackClick(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void onAddBudget(View view){
        Intent intent = new Intent(this, AddUpdateBudgetActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private UsersModel getUser(){
        Map<Integer, UsersModel> userMap = authorizationDbService.getActiveUser();

        if(userMap == null || (userMap != null && userMap.isEmpty())){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            showToast("Please Login");
            return null;
        }
        else if(userMap.size() > 1){
            Intent intent = new Intent(this, JimBrokeItActivity.class);
            startActivity(intent);
            finish();
            showToast("Multiple Users are Active : Possible DB Corruption.");
        }
        else{
            return userMap.get(0);
        }

        Log.e(CLASS_NAME, "I'm not supposed to be read/print/shown..... This should have been a dead code. If you can read me, Authorization of user has failed and you should " +
                "probably die twice by now.");
        return null;
    }

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group, Typeface font) {
        int count = group.getChildCount();
        View v;
        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v, font);
            }
        }
    }
}

