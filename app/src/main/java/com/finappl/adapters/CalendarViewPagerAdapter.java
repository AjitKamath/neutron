package com.finappl.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.activities.HomeActivity;
import com.finappl.models.CalendarMonth;
import com.finappl.utils.FinappleUtility;

import java.util.Date;
import java.util.LinkedList;

import static com.finappl.R.id.calendar_date_cell_date_key;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 1/2/17.
 */

public class CalendarViewPagerAdapter extends PagerAdapter {

    private LinkedList<CalendarMonth> calendarMonth;
    private LayoutInflater inflater;
    private Context mContext;

    public CalendarViewPagerAdapter(Context mContext, LinkedList<CalendarMonth> calendarMonth) {
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
        return calendarMonth.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view =  inflater.inflate(R.layout.calendar_month, null);
        /*final GridView grid = (GridView) view.findViewById(R.id.calendar_month_gv);*/
        final TextView calendar_month_header_month_month_tv = (TextView) view.findViewById(R.id.calendar_month_header_month_month_tv);
        final TextView calendar_month_header_month_year_tv = (TextView) view.findViewById(R.id.calendar_month_header_month_year_tv);
        final ImageView calendar_month_header_prev_iv = (ImageView) view.findViewById(R.id.calendar_month_header_prev_iv);
        final ImageView calendar_month_header_next_iv = (ImageView) view.findViewById(R.id.calendar_month_header_next_iv);
        final RecyclerView test = (RecyclerView) view.findViewById(R.id.recycler_view);

        final CalendarMonth currentPage = calendarMonth.get(position);

        calendar_month_header_month_month_tv.setText(currentPage.getMonth());
        calendar_month_header_month_year_tv.setText(currentPage.getYear());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext,7);
        test.setLayoutManager(mLayoutManager);
        test.setAdapter(currentPage.getAdapter());

        calendar_month_header_prev_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).changeMonth(true);
            }
        });
        calendar_month_header_next_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).changeMonth(false);
            }
        });

        container.addView(view);

        setFont(container);

        return view;
    }

    public void setModel(LinkedList<CalendarMonth> calendarMonth){
        this.calendarMonth = calendarMonth;
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