package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.activities.HomeActivity;
import com.finappl.models.CalendarMonth;
import com.finappl.utils.FinappleUtility;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

import static com.finappl.R.id.calendar_date_cell_date_key;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 1/2/17.
 */

public class CalendarMonthsHeaderViewPagerAdapter_DELETE extends PagerAdapter {

    private CalendarMonth[] calendarMonth;
    private LayoutInflater inflater;
    private Context mContext;

    public CalendarMonthsHeaderViewPagerAdapter_DELETE(Context mContext, CalendarMonth[] calendarMonth) {
        this.calendarMonth = calendarMonth;
        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        // we only need three pages
        return calendarMonth.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view =  inflater.inflate(R.layout.calendar_month_header, null);
        TextView calendar_month_header_month_month_tv = (TextView) view.findViewById(R.id.calendar_month_header_month_month_tv);
        TextView calendar_month_header_month_year_tv = (TextView) view.findViewById(R.id.calendar_month_header_month_year_tv);

        final CalendarMonth currentPage = calendarMonth[position];

        Calendar calendar = currentPage.getAdapter().getCurrentCalendar();

        Formatter fmt = new Formatter();
        String monthMonthStr = String.valueOf(fmt.format("%tb", calendar)).toUpperCase();
        String monthYearStr = String.valueOf(calendar.get(Calendar.YEAR));
        fmt.close();

        calendar_month_header_month_month_tv.setText(monthMonthStr);
        calendar_month_header_month_year_tv.setText(monthYearStr);

        container.addView(view);

        //FinappleUtility.setFont(mContext, container);

        return view;
    }

    public void setModel(CalendarMonth[] calendarMonth){
        this.calendarMonth = calendarMonth;
        notifyDataSetChanged();
    }



    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }
}