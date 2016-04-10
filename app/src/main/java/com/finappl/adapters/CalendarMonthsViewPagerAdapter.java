package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.adapters.calendar.CalendarMonth1GridViewAdapter;
import com.finappl.adapters.calendar.CalendarMonth2GridViewAdapter;
import com.finappl.adapters.calendar.CalendarMonth3GridViewAdapter;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.dbServices.AddUpdateTransfersDbService;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.Sqlite;
import com.finappl.models.AccountsModel;
import com.finappl.models.ActivityModel;
import com.finappl.models.BudgetModel;
import com.finappl.models.ConsolidatedTransactionModel;
import com.finappl.models.ConsolidatedTransferModel;
import com.finappl.models.MonthLegend;
import com.finappl.models.ScheduledTransactionModel;
import com.finappl.models.ScheduledTransferModel;
import com.finappl.models.UserMO;
import com.finappl.models.UsersModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.finappl.utils.Constants.*;

/**
 * Created by ajit on 30/9/15.
 */
public class CalendarMonthsViewPagerAdapter extends PagerAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    public Date selectedDate;
    private Map<String, MonthLegend> monthLegendMap;

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
    private List<AccountsModel> accountsList;
    private List<BudgetModel> budgetsList;
    private List<ScheduledTransactionModel> scheduledTransactionModelList;
    private List<ScheduledTransferModel> scheduledTransferModelList;

    private LinkedList<Integer> monthIndexSet;

    private CalendarActivity.GridViewItemClickListener gridViewItemClickListener;

    private CalendarMonth3GridViewAdapter calendarMonth3GridViewAdapter;
    private CalendarMonth2GridViewAdapter calendarMonth2GridViewAdapter;
    private CalendarMonth1GridViewAdapter calendarMonth1GridViewAdapter;

    private String centralMateMonthStr;
    public String currentFocusedMonthStr;
    public boolean doMonthChange;
    public int currentMonthIndex;

    public int maxMonths = MONTHS_RANGE;

    public View oldMonthView;

    public CalendarMonthsViewPagerAdapter(Context context, Date selectedDate, String centralMateMonthStr,
                                          UserMO loggedInUserObj, Map<String, MonthLegend> monthLegendMap,
                                          CalendarActivity.GridViewItemClickListener gridViewItemClickListener) {
        this.mContext = context;
        this.selectedDate = selectedDate;
        this.centralMateMonthStr = centralMateMonthStr;
        this.loggedInUserObj = loggedInUserObj;
        this.monthLegendMap = monthLegendMap;
        this.gridViewItemClickListener = gridViewItemClickListener;

        this.currentFocusedMonthStr = centralMateMonthStr;

        controller = new Sqlite(mContext);
        calendarDbService = new CalendarDbService(mContext);
        authorizationDbService = new AuthorizationDbService(mContext);
        addUpdateTransactionsDbService = new TransactionsDbService(mContext);
        addUpdateTransfersDbService = new AddUpdateTransfersDbService(mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup)inflater.inflate(R.layout.calendar_month, null);

        setUpMonth(layout, position);

        collection.addView(layout);
        return layout;
    }

    private void setUpMonth(ViewGroup layout, int position) {
        String dateMonthStrArr[] = centralMateMonthStr.split("-");

        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        String monthAndYear = sdf.format(new Date());
        String monthAndYearArr[] = monthAndYear.split("-");

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.set(Integer.parseInt(dateMonthStrArr[1]), Integer.parseInt(dateMonthStrArr[0]) - 1, 1);

        cal.add(Calendar.MONTH, position-(maxMonths/2));

        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        //int day = cal.get(Calendar.DAY_OF_MONTH);

        GridView currGrid = (GridView) layout.findViewById(R.id.calendarPageCalendarCurrGVId);

        SimpleDateFormat sdf1 = new SimpleDateFormat(JAVA_DATE_FORMAT);
        Date junkDate = null;
        try{
            junkDate = sdf1.parse("01-01-1970");
        }
        catch (ParseException p){
            Log.e(CLASS_NAME, "Error in Parse Date: "+p);
            return;
        }
        if(position == (maxMonths/2)-1){
            calendarMonth1GridViewAdapter = new CalendarMonth1GridViewAdapter(mContext, monthLegendMap, month, year, junkDate);
            currGrid.setAdapter(calendarMonth1GridViewAdapter);
        }
        else if(position == (maxMonths/2)){

            calendarMonth2GridViewAdapter = new CalendarMonth2GridViewAdapter(mContext, monthLegendMap, month, year, selectedDate);
            currGrid.setAdapter(calendarMonth2GridViewAdapter);
        }
        else{
            calendarMonth3GridViewAdapter = new CalendarMonth3GridViewAdapter(mContext, monthLegendMap, month, year, junkDate);
            currGrid.setAdapter(calendarMonth3GridViewAdapter);
        }
        currGrid.setOnItemClickListener(gvItemClickListener);
    }

    protected void showToast(String string) {
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
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
        return maxMonths;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    private AdapterView.OnItemClickListener gvItemClickListener;
    {
        gvItemClickListener = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItemObject = view.getTag();
                if (gridViewItemClickListener != null) {
                    GridLayout calendarGridDayContentGL = (GridLayout) view.findViewById(R.id.calendarGridDayContentGL);
                    //get backGround color of the currently clicked date cell
                    int dateCellColor = (int) calendarGridDayContentGL.getTag();

                    //get the date text color
                    TextView gridcell_date_TV = (TextView) view.findViewById(R.id.calendarDayTVId);

                    SimpleDateFormat sdf = new SimpleDateFormat(JAVA_DATE_FORMAT);
                    Date selectedDateFromCell = null;
                    try{
                        selectedDateFromCell = sdf.parse(String.valueOf(calendarGridDayContentGL.getTag(R.id.calendarGridDayContentGL)));
                    }
                    catch (ParseException e){
                        Log.e(CLASS_NAME, "Parse Exception : "+e);
                    }

                    if(gridcell_date_TV.getCurrentTextColor() == mContext.getResources().getColor(R.color.calendarNextPrevMonthDate)) {
                        Log.i(CLASS_NAME, "I Clicked on a grey date... nothing to do");
                        return;
                    }

                    //remove clicked date in old month view
                    if(oldMonthView == null){
                        //oldMonthView is null when the todays date is single tapped by default
                        oldMonthView = calendarMonth2GridViewAdapter.todaysView;
                    }
                    if(oldMonthView != null) {
                        oldMonthView.setBackgroundResource(R.drawable.circle_calendar_no_tap);
                        oldMonthView.setTag(R.drawable.circle_calendar_no_tap);
                    }

                    if (dateCellColor == R.drawable.circle_calendar_one_tap) {
                        Log.i(CLASS_NAME, "Dude u just clicked on the same cell twice !! either u r high or u want to view the transactions in detail..");
                        calendarGridDayContentGL.setBackgroundResource(R.drawable.circle_calendar_two_tap);
                        calendarGridDayContentGL.setTag(R.drawable.circle_calendar_two_tap);

                        oldMonthView = calendarGridDayContentGL;

                        TextView transactIndicatorView = (TextView) calendarGridDayContentGL.findViewById(R.id.calendarCellTransactionIndicatorTVId);
                        TextView transferIndicatorView = (TextView) calendarGridDayContentGL.findViewById(R.id.calendarCellTransferIndicatorTVId);

                        //if the activity transaction indicator or transfer indicator both are invisible...then do not proceed to ViewTransaction page..because there's no point
                        if (transactIndicatorView.getVisibility() == View.GONE && transferIndicatorView.getVisibility() == View.GONE) {
                            showToast("No Activities to Show");
                            return;
                        }

                        //go to view transaction activity
                        ActivityModel activityModel = new ActivityModel();
                        activityModel.setFromDate(selectedDate);
                        activityModel.setToDate(selectedDate);
                        activityModel.setWhichActivityStr("TRANSACTIONS");

                        //if there's only transfer in this date..then show transfers as default opened tab in view activities page
                        if (transactIndicatorView.getVisibility() != View.VISIBLE && transferIndicatorView.getVisibility() == View.VISIBLE) {
                            activityModel.setWhichActivityStr("TRANSFER");
                        }
                        //navigateTo(ViewActivitiesActivity.class, "ACTIVITY_OBJ", activityModel);
                    } else if (dateCellColor == R.drawable.circle_calendar_no_tap) {
                        Log.i(CLASS_NAME, "New Date Cell is clicked(" + selectedDateFromCell + "), Checking whether its an prev month or next month date");
                        selectedDate = selectedDateFromCell;

                        /*if(gridcell_date_TV.getCurrentTextColor() == mContext.getResources().getColor(R.color.calendarNextPrevMonthDate)){
                            doMonthChange = true;
                            Log.i(CLASS_NAME, "This cell is not in the current month, Changing the month");
                            calendarGridDayContentGL.setBackgroundResource(R.drawable.circle_calendar_no_tap);
                            calendarGridDayContentGL.setTag(R.drawable.circle_calendar_no_tap);
                        }
                        else{
                            doMonthChange = false;
                            calendarGridDayContentGL.setBackgroundResource(R.drawable.circle_calendar_one_tap);
                            calendarGridDayContentGL.setTag(R.drawable.circle_calendar_one_tap);
                            oldMonthView = calendarGridDayContentGL;
                        }*/
                            doMonthChange = false;
                            calendarGridDayContentGL.setBackgroundResource(R.drawable.circle_calendar_one_tap);
                            calendarGridDayContentGL.setTag(R.drawable.circle_calendar_one_tap);
                            oldMonthView = calendarGridDayContentGL;
                    } else {
                        //if the activity transaction indicator or transfer indicator both are invisible...then do not proceed to ViewTransaction page..because there's no point
                        if (calendarGridDayContentGL.findViewById(R.id.calendarCellTransactionIndicatorTVId).getVisibility() == View.GONE
                                && calendarGridDayContentGL.findViewById(R.id.calendarCellTransferIndicatorTVId).getVisibility() == View.GONE) {
                            showToast("No Activities to Show");
                            return;
                        }
                    }

                    gridViewItemClickListener.onGridViewItemClick(position);
                }
            }
        };
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
}