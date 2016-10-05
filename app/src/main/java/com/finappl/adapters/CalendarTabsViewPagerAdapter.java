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
import com.finappl.activities.CalendarActivity;
import com.finappl.dbServices.AddUpdateTransfersDbService;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.Sqlite;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.models.AccountsMO;
import com.finappl.models.BudgetModel;
import com.finappl.models.ConsolidatedTransactionModel;
import com.finappl.models.ConsolidatedTransferModel;
import com.finappl.models.MonthLegend;
import com.finappl.models.ScheduledTransactionModel;
import com.finappl.models.ScheduledTransferModel;
import com.finappl.models.SummaryModel;
import com.finappl.models.UserMO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.finappl.utils.Constants.*;

/**
 * Created by ajit on 30/9/15.
 */
public class CalendarTabsViewPagerAdapter extends PagerAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    private List<Integer> layoutsList;
    private Map<String, MonthLegend> monthLegendMap;
    private Date selectedDate;
    private CalendarActivity.ListViewItemClickListener listViewItemClickListener;

    //db services
    private Sqlite controller;
    private CalendarDbService calendarDbService;
    private AuthorizationDbService authorizationDbService;
    private TransactionsDbService addUpdateTransactionsDbService;
    private AddUpdateTransfersDbService addUpdateTransfersDbService;

    //User
    private UserMO loggedInUserObj;

    //month legend availability
    private boolean hasSummary;
    private boolean hasAccounts;
    private boolean hasBudgets;
    private boolean hasSchedules;

    private Map<String, ConsolidatedTransactionModel> consolidatedTransactionModelMap;
    private Map<String, ConsolidatedTransferModel> consolidatedTransferModelMap;
    private List<AccountsMO> accountsList;
    private List<BudgetModel> budgetsList;
    private List<ScheduledTransactionModel> scheduledTransactionModelList;
    private List<ScheduledTransferModel> scheduledTransferModelList;

    public int activePageIndex = 0;


    public CalendarTabsViewPagerAdapter(Context context, List<Integer> layoutsList, Date selectedDate
            , UserMO loggedInUserObj, Map<String, MonthLegend> monthLegendMap,
                                        CalendarActivity.ListViewItemClickListener listViewItemClickListener) {
        this.mContext = context;
        this.layoutsList = layoutsList;
        this.selectedDate = selectedDate;
        this.loggedInUserObj = loggedInUserObj;
        this.monthLegendMap = monthLegendMap;
        this.listViewItemClickListener = listViewItemClickListener;

        controller = new Sqlite(mContext);
        calendarDbService = new CalendarDbService(mContext);
        authorizationDbService = new AuthorizationDbService(mContext);
        addUpdateTransactionsDbService = new TransactionsDbService(mContext);
        addUpdateTransfersDbService = new AddUpdateTransfersDbService(mContext);
    }

    private void checkMonthLegend() {
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

        //Summary check
        SummaryModel summaryModelObj = monthLegendObj.getSummaryModel();
        if (summaryModelObj != null) {
            consolidatedTransactionModelMap = summaryModelObj.getConsolidatedTransactionModelMap();
            consolidatedTransferModelMap = summaryModelObj.getConsolidatedTransferModelMap();

            if ((consolidatedTransactionModelMap != null && !consolidatedTransactionModelMap.isEmpty())
                    || (consolidatedTransferModelMap != null && !consolidatedTransferModelMap.isEmpty())) {
                hasSummary = true;
            }
        }
        //summary check ends

        //Schedules Check
        scheduledTransactionModelList = monthLegendObj.getScheduledTransactionModelList();
        scheduledTransferModelList = monthLegendObj.getScheduledTransferModelList();

        if ((scheduledTransactionModelList != null && !scheduledTransactionModelList.isEmpty())
                || (scheduledTransferModelList != null && !scheduledTransferModelList.isEmpty())) {
            hasSchedules = true;
        }
        //Schedules Check ends
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutsList.get(position), collection, false);

        checkMonthLegend();

        switch (layoutsList.get(position)) {
            case R.layout.calendar_tab_summary:
                setUpSummaryTab(layout);
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

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(layout, robotoCondensedLightFont);

        collection.addView(layout);

        return layout;
    }

    private void setUpSummaryTab(ViewGroup layout) {
        TextView calendarNoTransTV = (TextView) layout.findViewById(R.id.calendarNoTransTVId);
        ListView consolTranLV = (ListView) layout.findViewById(R.id.consolTranLVId);

        SimpleDateFormat sdf = new SimpleDateFormat(JAVA_DATE_FORMAT);

        if (!hasSummary) {
            Log.i(CLASS_NAME, "No summary to show on this day(" + sdf.format(selectedDate) + ")");

            calendarNoTransTV.setText("No Transactions/Transfers");
            calendarNoTransTV.setTextColor(mContext.getResources().getColor(R.color.DarkGray));
            consolTranLV.setVisibility(View.GONE);
            return;
        }

        CalendarSummarySectionListViewAdapter consolAdapter =
                new CalendarSummarySectionListViewAdapter(mContext, R.layout.calendar_summary_list_view, monthLegendMap.get(sdf.format(selectedDate)).getSummaryModel());
        consolTranLV.setAdapter(consolAdapter);
        consolTranLV.setOnItemClickListener(listViewClickListener);

        //if there are no summary on the selected date then show no transaction/transfer text
        if (consolAdapter != null & consolAdapter.getCount() != 0) {
            calendarNoTransTV.setVisibility(View.GONE);
            consolTranLV.setVisibility(View.VISIBLE);
        } else {
            calendarNoTransTV.setVisibility(View.VISIBLE);
            consolTranLV.setVisibility(View.GONE);
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

        CalendarAccountsListViewAdapter accAdapter = new CalendarAccountsListViewAdapter(mContext, R.layout.calendar_accounts_list_view, accountsList);
        accountsLV.setAdapter(accAdapter);
        accountsLV.setOnItemClickListener(listViewClickListener);

        //if there are no accounts
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
            Log.e(CLASS_NAME, "No Budgets for this day/week/month/year !!");

            calendarNoBudgetsTV.setText("No Budgets");
            calendarNoBudgetsTV.setTextColor(mContext.getResources().getColor(R.color.DarkGray));
            budgetsLV.setVisibility(View.GONE);
            return;
        }

        CalendarBudgetsListViewAdapter calendarBudgetsAdapter = new CalendarBudgetsListViewAdapter(mContext, R.layout.calendar_budgets_list_view, budgetsList);
        budgetsLV.setAdapter(calendarBudgetsAdapter);
        budgetsLV.setOnItemClickListener(listViewClickListener);

        //if there are budgets for this day/week/month/year
        if (calendarBudgetsAdapter != null & calendarBudgetsAdapter.getCount() != 0) {
            calendarNoBudgetsTV.setVisibility(View.GONE);
            budgetsLV.setVisibility(View.VISIBLE);
        } else {
            calendarNoBudgetsTV.setVisibility(View.VISIBLE);
            budgetsLV.setVisibility(View.GONE);
        }
    }

    private void setUpSchedulesTab(ViewGroup layout) {
        TextView calendarTransHeaderOrMsgTV = (TextView) layout.findViewById(R.id.calendarTransHeaderOrMsgTVId);
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
        }
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

    public int getActivePageIndexByLayoutId(int layoutId) {
        int pageId = 0;

        for (int iterLayouts : layoutsList) {
            if (iterLayouts == layoutId) {
                pageId = iterLayouts;
                break;
            }
        }
        return pageId;
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group, Typeface font) {
        int count = group.getChildCount();
        View v;

        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            } else if (v instanceof ViewGroup) {
                setFont((ViewGroup) v, font);
            }
        }
    }

    //--------------------------------list view click listener------------------------------------------------------
    private ListView.OnItemClickListener listViewClickListener;
    {
        listViewClickListener = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(CLASS_NAME, "MASTER !! you click hath " + view.getId() + ". You shalt go to the view transaction pageth");

            Object listItemObject = view.getTag();
            if (listViewItemClickListener != null) {
                listViewItemClickListener.onListItemClick(listItemObject);
            }
            }
        };
    }
}