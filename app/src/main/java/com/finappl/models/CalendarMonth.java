package com.finappl.models;

import android.content.Context;

import com.finappl.adapters.CalendarGridViewAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ajit on 31/1/17.
 */

public class CalendarMonth {
    private int offset;
    private CalendarGridViewAdapter adapter;
    private String month;
    private String year;


    public CalendarMonth(int offset, Context mContext, Map<String, MonthLegend> ledger, UserMO user) {
        this.offset = offset;
        this.adapter = new CalendarGridViewAdapter(mContext, offset, ledger, user);

        Calendar calendar = adapter.getCurrentCalendar();

        this.month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).toUpperCase();
        this.year = String.valueOf(calendar.get(Calendar.YEAR));
    }

    public CalendarGridViewAdapter getAdapter() {
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
}
