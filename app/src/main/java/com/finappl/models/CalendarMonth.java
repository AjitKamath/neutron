package com.finappl.models;

import android.content.Context;

import com.finappl.adapters.CalendarGridViewAdapter;

import java.util.Date;
import java.util.Map;

/**
 * Created by ajit on 31/1/17.
 */

public class CalendarMonth {
    private int offset;
    private CalendarGridViewAdapter adapter;


    public CalendarMonth(int offset, Context mContext, Map<String, MonthLegend> ledger, UserMO user) {
        this.offset = offset;
        this.adapter = new CalendarGridViewAdapter(mContext, offset, ledger, user);
    }

    public CalendarGridViewAdapter getAdapter() {
        return adapter;
    }

    public int getOffset() {
        return offset;
    }
}
