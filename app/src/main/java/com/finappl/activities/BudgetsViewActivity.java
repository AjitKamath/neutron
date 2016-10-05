package com.finappl.activities;

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

import com.finappl.R;
import com.finappl.adapters.BudgetsViewSectionListAdapter;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.BudgetsViewDbService;
import com.finappl.models.BudgetsViewModel;
import com.finappl.models.UserMO;
import com.finappl.models.UsersModel;
import com.finappl.utils.FinappleUtility;

import java.util.Map;

public class BudgetsViewActivity extends Activity {
	private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    //User
    private UserMO loggedInUserObj;

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
        loggedInUserObj = authorizationDbService.getActiveUser(FinappleUtility.getInstance().getActiveUserId(mContext));
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

        resetTabs();

        budgetsDailyTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
        budgetsDailyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.finappleTheme));
        budgetsDailyLL.setVisibility(View.VISIBLE);
    }

    public void onTabClick(View view){
        resetTabs();

        switch(view.getId()){
            case R.id.budgetsDailyTabTVId : budgetsDailyTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
                budgetsDailyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.finappleTheme));
                budgetsDailyLL.setVisibility(View.VISIBLE);
                break;

            case R.id.budgetsWeeklyTabTVId : budgetsWeeklyTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
                budgetsWeeklyTabTV.setTextColor(budgetsWeeklyTabTV.getResources().getColor(R.color.finappleTheme));
                budgetsWeeklyLL.setVisibility(View.VISIBLE);
                break;

            case R.id.budgetsMonthlyTabTVId : budgetsMonthlyTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
                budgetsMonthlyTabTV.setTextColor(budgetsMonthlyTabTV.getResources().getColor(R.color.finappleTheme));
                budgetsMonthlyLL.setVisibility(View.VISIBLE);
                break;

            case R.id.budgetsYearlyTabTVId : budgetsYearlyTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
                budgetsYearlyTabTV.setTextColor(budgetsYearlyTabTV.getResources().getColor(R.color.finappleTheme));
                budgetsYearlyLL.setVisibility(View.VISIBLE);
                break;

            default:
                Log.e(CLASS_NAME, "TAB error !!! Something is wrong while switching tabs !! Check where ur clicking..");
                showToast("TAB ERROR !");
        }
    }

    private void resetTabs() {
        //tabs
        budgetsDailyTabTV.setBackgroundResource(R.drawable.view_activities_inactive_tab_inner);
        budgetsMonthlyTabTV.setBackgroundResource(R.drawable.view_activities_inactive_tab_inner);
        budgetsWeeklyTabTV.setBackgroundResource(R.drawable.view_activities_inactive_tab_inner);
        budgetsYearlyTabTV.setBackgroundResource(R.drawable.view_activities_inactive_tab_inner);

        //labels
        budgetsDailyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.white));
        budgetsMonthlyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.white));
        budgetsWeeklyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.white));
        budgetsYearlyTabTV.setTextColor(budgetsDailyTabTV.getResources().getColor(R.color.white));

        //lists
        budgetsDailyLL.setVisibility(View.GONE);
        budgetsMonthlyLL.setVisibility(View.GONE);
        budgetsWeeklyLL.setVisibility(View.GONE);
        budgetsYearlyLL.setVisibility(View.GONE);

    }

    private void initUiComponents() {
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

