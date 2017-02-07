package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.HomeActivity;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.models.AccountMO;
import com.finappl.models.ActivitiesMO;
import com.finappl.models.BudgetMO;
import com.finappl.models.MonthLegend;
import com.finappl.models.TransactionMO;
import com.finappl.models.TransferMO;
import com.finappl.models.UserMO;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 30/9/15.
 */
public class CalendarTabsViewPagerAdapter extends PagerAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    private List<Integer> layoutsList;
    private Map<String, MonthLegend> monthLegendMap;
    private Date selectedDate;
    private HomeActivity.ListViewItemClickListener listViewItemClickListener;

    //db services
    private CalendarDbService calendarDbService;

    //User
    private UserMO loggedInUserObj;

    //month legend availability
    private boolean hasSummary;
    private boolean hasAccounts;
    private boolean hasBudgets;

    private List<TransactionMO> transactionsList;
    private List<TransferMO> transfersList;
    private List<AccountMO> accountsList;
    private List<BudgetMO> budgetsList;

    public int activePageIndex = 0;


    public CalendarTabsViewPagerAdapter(Context context, List<Integer> layoutsList, Date selectedDate
            , UserMO loggedInUserObj, Map<String, MonthLegend> monthLegendMap,
                                        HomeActivity.ListViewItemClickListener listViewItemClickListener) {
        this.mContext = context;
        this.layoutsList = layoutsList;
        this.selectedDate = selectedDate;
        this.loggedInUserObj = loggedInUserObj;
        this.monthLegendMap = monthLegendMap;
        this.listViewItemClickListener = listViewItemClickListener;

        calendarDbService = new CalendarDbService(mContext);
    }

    private void checkMonthLegend() {
        if(loggedInUserObj == null){
            return;
        }

        //Accounts check
        accountsList = calendarDbService.getAllAccounts(loggedInUserObj.getUSER_ID());
        if (accountsList != null && !accountsList.isEmpty()) {
            hasAccounts = true;
        }
        //Accounts check ends

        //Budgets check
        budgetsList = calendarDbService.getAllBudgets(selectedDate, loggedInUserObj.getUSER_ID());
        if (budgetsList != null && !budgetsList.isEmpty()) {
            hasBudgets = true;
        }
        //Budgets check ends

        MonthLegend monthLegendObj = monthLegendMap.get(JAVA_DATE_FORMAT_SDF.format(selectedDate));
        if (monthLegendObj == null) {
            Log.e(CLASS_NAME, "Nothing found in MonthLegend...");
            return;
        }

        //Activities check
        ActivitiesMO activities = monthLegendObj.getActivities();
        if (activities != null) {
            transactionsList = activities.getTransactionsList();
            transfersList = activities.getTransfersList();

            if ((transactionsList != null && !transactionsList.isEmpty())
                    || (transfersList != null && !transfersList.isEmpty())) {
                hasSummary = true;
            }
        }
        //summary check ends
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutsList.get(position), collection, false);

        checkMonthLegend();

        switch (layoutsList.get(position)) {
            case R.layout.calendar_tab_activities:
                setUpActivitiesTab(layout);
                activePageIndex = 0;
                break;

            case R.layout.calendar_tab_accounts:
                setUpAccountsTab(layout);
                activePageIndex = 1;
                break;

            case R.layout.calendar_tab_budgets:
                setUpBudgetsTab(layout);
                activePageIndex = 2;
                break;

            case R.layout.calendar_tab_schedules:
                setUpSchedulesTab(layout);
                activePageIndex = 3;
                break;
        }

        setFont(layout);

        collection.addView(layout);

        return layout;
    }

    private void setUpActivitiesTab(ViewGroup layout) {
        TextView calendarNoActivityTV = (TextView) layout.findViewById(R.id.calendarNoActivityTVId);
        ListView CalendarActivity_NEWLV = (ListView) layout.findViewById(R.id.CalendarActivity_NEWLVId);

        if (!hasSummary) {
            Log.i(CLASS_NAME, "No activities to show on this calendar_day__(" + JAVA_DATE_FORMAT_SDF.format(selectedDate) + ")");

            calendarNoActivityTV.setText("No Activities");
            calendarNoActivityTV.setTextColor(mContext.getResources().getColor(R.color.DarkGray));
            CalendarActivity_NEWLV.setVisibility(View.GONE);
            return;
        }

        CalendarActivitiesSectionListViewAdapter consolAdapter =
                new CalendarActivitiesSectionListViewAdapter(mContext, monthLegendMap.get(JAVA_DATE_FORMAT_SDF.format(selectedDate)).getActivities(), loggedInUserObj);
        CalendarActivity_NEWLV.setAdapter(consolAdapter);
        CalendarActivity_NEWLV.setOnItemClickListener(listViewClickListener);

        //if there are no summary on the selected date then show no add_update_transaction/add_update_transfer text
        if (consolAdapter != null & consolAdapter.getCount() != 0) {
            calendarNoActivityTV.setVisibility(View.GONE);
            CalendarActivity_NEWLV.setVisibility(View.VISIBLE);
        } else {
            calendarNoActivityTV.setVisibility(View.VISIBLE);
            CalendarActivity_NEWLV.setVisibility(View.GONE);
        }
    }

    private void setUpAccountsTab(ViewGroup layout) {
        TextView calendarNoAccountsTV = (TextView) layout.findViewById(R.id.calendarNoAccountsTVId);
        ListView accountsLV = (ListView) layout.findViewById(R.id.accountsLVId);

        if (!hasAccounts) {
            Log.e(CLASS_NAME, "No Accounts !! This is an error !!");

            calendarNoAccountsTV.setText("No Accounts");
            calendarNoAccountsTV.setTextColor(mContext.getResources().getColor(R.color.DarkGray));
            accountsLV.setVisibility(View.GONE);
            return;
        }

        CalendarAccountsListViewAdapter accAdapter = new CalendarAccountsListViewAdapter(mContext, R.layout.calendar_tab_account, accountsList, loggedInUserObj);
        accountsLV.setAdapter(accAdapter);
        accountsLV.setOnItemClickListener(listViewClickListener);

        //if there are no select_account
        if (accAdapter != null & accAdapter.getCount() != 0) {
            calendarNoAccountsTV.setVisibility(View.GONE);
            accountsLV.setVisibility(View.VISIBLE);
        } else {
            calendarNoAccountsTV.setVisibility(View.VISIBLE);
            accountsLV.setVisibility(View.GONE);
        }
    }

    private void setUpBudgetsTab(ViewGroup layout) {
        TextView calendarNoBudgetsTV = (TextView) layout.findViewById(R.id.calendarNoBudgetsTVId);
        ListView budgetsLV = (ListView) layout.findViewById(R.id.budgetsLVId);

        if (!hasBudgets) {
            Log.e(CLASS_NAME, "No Budgets for this calendar_day__/week/month/year !!");

            calendarNoBudgetsTV.setText("No Budgets");
            calendarNoBudgetsTV.setTextColor(mContext.getResources().getColor(R.color.DarkGray));
            budgetsLV.setVisibility(View.GONE);
            return;
        }

        CalendarBudgetsListViewAdapter calendarBudgetsAdapter = new CalendarBudgetsListViewAdapter(mContext, R.layout.calendar_tab_budget, budgetsList, loggedInUserObj);
        budgetsLV.setAdapter(calendarBudgetsAdapter);
        budgetsLV.setOnItemClickListener(listViewClickListener);

        //if there are budgets for this calendar_day__/week/month/year
        if (calendarBudgetsAdapter != null & calendarBudgetsAdapter.getCount() != 0) {
            calendarNoBudgetsTV.setVisibility(View.GONE);
            budgetsLV.setVisibility(View.VISIBLE);
        } else {
            calendarNoBudgetsTV.setVisibility(View.VISIBLE);
            budgetsLV.setVisibility(View.GONE);
        }
    }

    private void setUpSchedulesTab(ViewGroup layout) {
        /*TextView calendarTransHeaderOrMsgTV = (TextView) layout.findViewById(R.id.calendarTransHeaderOrMsgTVId);
        ListView schedulesLV = (ListView) layout.findViewById(R.id.schedulesLVId);

        CalendarSchedulesSectionListViewAdapter schedulesListAdapter =
                new CalendarSchedulesSectionListViewAdapter(mContext, R.layout.calendar_schedules_list_view, scheduledTransactionModelList,
                        scheduledTransferModelList);
        schedulesLV.setAdapter(schedulesListAdapter);
        schedulesListAdapter.notifyDataSetChanged();
        schedulesLV.setOnItemClickListener(listViewClickListener);

        if (schedulesListAdapter != null & schedulesListAdapter.getCount() != 0) {
            schedulesLV.setVisibility(View.VISIBLE);
            calendarTransHeaderOrMsgTV.setVisibility(View.GONE);
        } else {
            calendarTransHeaderOrMsgTV.setVisibility(View.VISIBLE);
            calendarTransHeaderOrMsgTV.setText("No Scheduled Transactions/Transfers");
            calendarTransHeaderOrMsgTV.setTextColor(calendarTransHeaderOrMsgTV.getResources().getColor(R.color.DarkGray));
            schedulesLV.setVisibility(View.GONE);
        }*/
    }

    protected void showToast(String string) {
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return layoutsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(layoutsList.get(position));
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

    //--------------------------------list view click listener------------------------------------------------------
    private ListView.OnItemClickListener listViewClickListener;
    {
        listViewClickListener = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(CLASS_NAME, "MASTER !! you click hath " + view.getId() + ". You shalt go to the view add_update_transaction pageth");

            Object listItemObject = view.getTag();
            if (listViewItemClickListener != null) {
                listViewItemClickListener.onListItemClick(listItemObject);
            }
            }
        };
    }
}