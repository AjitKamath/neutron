package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.AccountMO;
import com.finappl.models.CalendarSummary;
import com.finappl.models.DayLedger;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.util.ArrayList;
import java.util.List;

import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 1/2/17.
 */

public class CalendarSummaryViewPagerAdapter extends PagerAdapter {
    private final String CLASS_NAME = this.getClass().getName();

    private LayoutInflater inflater;
    private Context mContext;

    private List<Object> summaryList;
    private UserMO user;

    public CalendarSummaryViewPagerAdapter(Context mContext, UserMO user, List<Object> summaryList) {
        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.summaryList = summaryList;
        this.user = user;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return summaryList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        switch(position){
            case 0: return setupSummary(container, position);
            case 1: return setupAccounts(container, position);
            default:
                Log.e(CLASS_NAME, "Error !! possible cause : SummaryList size is not equal to the no. of viewpager views");
                return null;
        }
    }

    private Object setupAccounts(ViewGroup container, int position) {
        final View view = inflater.inflate(R.layout.calendar_accounts, null);

        List<AccountMO> accountsList = (List<AccountMO>) summaryList.get(position);

        Double allAccountsTotalAmount = FinappleUtility.getConolidatedAccountsAmount(accountsList);

        TextView calendar_accounts_amount_tv = (TextView) view.findViewById(R.id.calendar_accounts_amount_tv);
        ListView calendar_accounts_lv = (ListView) view.findViewById(R.id.calendar_accounts_lv);

        calendar_accounts_amount_tv = FinappleUtility.formatAmountView(calendar_accounts_amount_tv, user, allAccountsTotalAmount);

        CalendarAccountsListViewAdapter adapter = new CalendarAccountsListViewAdapter(mContext, user, accountsList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinappleUtility.showSnacks(view, "Account clicked", OK, Snackbar.LENGTH_SHORT);
            }
        });
        calendar_accounts_lv.setAdapter(adapter);

        //dynamically change the height of the list view
        int baseHeightForListView = 75;
        int calculateListViewHeight = baseHeightForListView * 10;

        if(adapter.getCount() <= 10){
            calculateListViewHeight = baseHeightForListView * adapter.getCount();
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, calculateListViewHeight);
        layoutParams.setMargins(0, 0, 0, 30);
        calendar_accounts_lv.setLayoutParams(layoutParams);

        setFont(container);
        container.addView(view);

        return view;
    }

    private View setupSummary(ViewGroup container, int position) {
        final View view = inflater.inflate(R.layout.calendar_summary, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.calendar_summary_rv);
        recyclerView.setHasFixedSize(true);

        DayLedger dayLedger = (DayLedger) summaryList.get(position);

        if(dayLedger == null){
            Log.i(CLASS_NAME, "Nothing to show today");
            dayLedger = new DayLedger();
            dayLedger.setHasTransactions(false);
            dayLedger.setHasTransfers(false);
        }

        List<CalendarSummary> calendarSummaryList = new ArrayList<>();
        CalendarSummary calendarSummary = null;

        //transactions
        calendarSummary = new CalendarSummary();
        calendarSummary.setHeading("TRANSACTIONS");
        if(dayLedger.isHasTransactions()){
            calendarSummary.setAmount(dayLedger.getTransactionsAmountTotal());

            CalendarSummaryTransactionsListViewAdapter tranAdapter = new CalendarSummaryTransactionsListViewAdapter(mContext, user, dayLedger.getActivities().getTransactionsList(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FinappleUtility.showSnacks(view, "Yet to be implemented !", OK, Snackbar.LENGTH_SHORT);
                }
            });

            calendarSummary.setListViewAdapter(tranAdapter);
        }
        else{
            calendarSummary.setAmount(0.0);
        }
        calendarSummaryList.add(calendarSummary);
        //transactions

        //transfers
        calendarSummary = new CalendarSummary();
        calendarSummary.setHeading("TRANSFERS");
        if(dayLedger.isHasTransfers()){
            calendarSummary.setAmount(dayLedger.getTransfersAmountTotal());

            CalendarSummaryTransfersListViewAdapter transferAdapter = new CalendarSummaryTransfersListViewAdapter(mContext, user, dayLedger.getActivities().getTransfersList(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FinappleUtility.showSnacks(view, "Yet to be implemented !", OK, Snackbar.LENGTH_SHORT);
                }
            });

            calendarSummary.setListViewAdapter(transferAdapter);
        }
        else{
            calendarSummary.setAmount(0.0);
        }
        calendarSummaryList.add(calendarSummary);
        //transfers

        LinearLayoutManager horizontalManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalManager);
        CalendarSummaryRecyclerViewAdapter listViewAdapter = new CalendarSummaryRecyclerViewAdapter(mContext, user, calendarSummaryList);
        recyclerView.setAdapter(listViewAdapter);
        recyclerView.setNestedScrollingEnabled(true);

        setFont(container);
        container.addView(view);

        return view;
    }

    public void setSummaryList(List<Object> summaryList){
        this.summaryList = summaryList;
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group) {
        final Typeface font = Typeface.createFromAsset(mContext.getAssets(), UI_FONT);

        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v);
            }
        }
    }
}