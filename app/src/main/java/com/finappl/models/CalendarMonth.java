package com.finappl.models;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.finappl.R;
import com.finappl.adapters.CalendarRecyclerAdapter;
import com.finappl.interfaces.OnItemClickListener;
import com.finappl.fragments.DaySummaryFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.finappl.utils.Constants.DAY_SUMMARY_OBJECT;
import static com.finappl.utils.Constants.FRAGMENT_DAY_SUMMARY;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;

/**
 * Created by ajit on 31/1/17.
 */

public class CalendarMonth {
    private final String CLASS_NAME = this.getClass().getName();

    private int offset;
    private CalendarRecyclerAdapter adapter;
    private String month;
    private String year;
    private Calendar calendar;

    public CalendarMonth(int offset, final Activity activity, Map<String, DayLedger> ledger, final UserMO user) {
        this.offset = offset;
        
        this.calendar = Calendar.getInstance();
        this.calendar.add(Calendar.MONTH, offset);

        this.month = this.calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).toUpperCase();
        this.year = String.valueOf(this.calendar.get(Calendar.YEAR));
        
        this.adapter = new CalendarRecyclerAdapter(activity.getApplicationContext(), prepareMonth(), ledger, user, new OnItemClickListener() {
            @Override
            public void onItemClick(View item) {
                DayLedger dayLedger = (DayLedger) item.getTag(R.id.calendar_date_cell_date_key);

                if(dayLedger != null){
                    showTransactionFragment(activity.getFragmentManager(), dayLedger, user);
                }
            }
        });
    }

    private void showTransactionFragment(FragmentManager fragMan, DayLedger dayLedger, UserMO user) {
        Fragment frag = fragMan.findFragmentByTag(FRAGMENT_DAY_SUMMARY);

        if (frag != null) {
            fragMan.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(DAY_SUMMARY_OBJECT, dayLedger);
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        DaySummaryFragment fragment = new DaySummaryFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(fragMan, FRAGMENT_DAY_SUMMARY);
    }

    private List<Date> prepareMonth() {
        /*calculate how many previous month dates must be displayed before the current month dates start
        * week starts from monday. as per Calendar class implementation, week starts from sunday
        * Calendar.DAY_OF_WEEK returns as follows. Sunday-1, Monday-2, Tuesday-3, Wednesday-4, Thursday-5, Friday-6, Saturday-7
        * so in order to start week from monday and also provide a week of previous month date if the 1st day of the month is monday,
        * a hack has to be made as follows:
        * if Calendar.DAY_OF_WEEK is
        * (Sunday)1 - previous month dates to be displayed are 6
        * (Monday)2 - previous month dates to be displayed are 7
        * (Tuesday)3 - previous month dates to be displayed are 1
        * (Wednesday)4 - previous month dates to be displayed are 2
        * (Thursday)5 - previous month dates to be displayed are 3
        * (Friday)6 - previous month dates to be displayed are 4
        * (Saturday)7 - previous month dates to be displayed are 5
        * */

        Calendar temp = (Calendar) this.calendar.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        temp.add(Calendar.MONTH, offset);

        int currentWeekDay = temp.get(Calendar.DAY_OF_WEEK);
        int prevMonthDatesCount = 0;
        switch (currentWeekDay) {
            case 2:
                prevMonthDatesCount = 7;
                break;
            case 3:
                prevMonthDatesCount = 1;
                break;
            case 4:
                prevMonthDatesCount = 2;
                break;
            case 5:
                prevMonthDatesCount = 3;
                break;
            case 6:
                prevMonthDatesCount = 4;
                break;
            case 7:
                prevMonthDatesCount = 5;
                break;
            case 1:
                prevMonthDatesCount = 6;
                break;
            default:
                Log.e(CLASS_NAME, "Calendar is gonna look ugly because the date setting Algo just killed itself !");
        }

        /*set previous month dates, current month dates & next month dates*/
        //prev month
        Calendar anotherTemp = (Calendar)this.calendar.clone();

        anotherTemp.add(Calendar.MONTH, -1);
        int endingDateForPrevMonth = anotherTemp.getActualMaximum(Calendar.DAY_OF_MONTH);
        int startingDateForPrevMonth = endingDateForPrevMonth - prevMonthDatesCount + 1;

        anotherTemp.set(Calendar.DAY_OF_MONTH, startingDateForPrevMonth);

        List<Date> datesList= new ArrayList<>();
        for(int  i=0; i<42; i++){
            datesList.add(anotherTemp.getTime());
            anotherTemp.add(Calendar.DATE, 1);
        }

        return datesList;
    }

    public CalendarRecyclerAdapter getAdapter() {
        return adapter;
    }

    public int getOffset() {
        return offset;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
