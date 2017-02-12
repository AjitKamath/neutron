package com.finappl.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.HomeActivity;
import com.finappl.adapters.calendar_DELETE.CalendarMonth1GridViewAdapter;
import com.finappl.adapters.calendar_DELETE.CalendarMonth2GridViewAdapter;
import com.finappl.adapters.calendar_DELETE.CalendarMonth3GridViewAdapter;
import com.finappl.models.DayLedger;
import com.finappl.models.UserMO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static com.finappl.utils.Constants.JAVA_DATE_FORMAT;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.MONTHS_RANGE;

/**
 * Created by ajit on 30/9/15.
 */
public class CalendarMonthsViewPagerAdapter_DELETE extends PagerAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    public Date selectedDate;
    private Map<String, DayLedger> monthLegendMap;

    //User
    private UserMO loggedInUserObj;

    private HomeActivity.GridViewItemClickListener gridViewItemClickListener;

    private CalendarMonth3GridViewAdapter calendarMonth3GridViewAdapter;
    private CalendarMonth2GridViewAdapter calendarMonth2GridViewAdapter;
    private CalendarMonth1GridViewAdapter calendarMonth1GridViewAdapter;

    private String centralMateMonthStr;
    public String currentFocusedMonthStr;
    public boolean doMonthChange;

    public int maxMonths = MONTHS_RANGE;

    public View oldMonthView;

    //progress bar
    private ProgressDialog mProgressDialog;

    public CalendarMonthsViewPagerAdapter_DELETE(Context context, Date selectedDate, String centralMateMonthStr,
                                                 UserMO loggedInUserObj, Map<String, DayLedger> monthLegendMap,
                                                 HomeActivity.GridViewItemClickListener gridViewItemClickListener) {
        this.mContext = context;
        this.selectedDate = selectedDate;
        this.centralMateMonthStr = centralMateMonthStr;
        this.loggedInUserObj = loggedInUserObj;
        this.monthLegendMap = monthLegendMap;
        this.gridViewItemClickListener = gridViewItemClickListener;

        this.currentFocusedMonthStr = centralMateMonthStr;
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

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.set(Integer.parseInt(dateMonthStrArr[1]), Integer.parseInt(dateMonthStrArr[0]) - 1, 1);

        cal.add(Calendar.MONTH, position-(maxMonths/2));

        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        GridView currGrid = null;//(GridView) layout.findViewById(R.id.calendarPageCalendarCurrGVId);

        SimpleDateFormat sdf1 = new SimpleDateFormat(JAVA_DATE_FORMAT);
        Date junkDate;
        try{
            junkDate = sdf1.parse("01-01-1970");
        }
        catch (ParseException p){
            Log.e(CLASS_NAME, "Error in Parse Date: "+p);
            return;
        }
        if(position == (maxMonths/2)-1){
            calendarMonth1GridViewAdapter = new CalendarMonth1GridViewAdapter(mContext, monthLegendMap, month, year, junkDate, loggedInUserObj);
            currGrid.setAdapter(calendarMonth1GridViewAdapter);
        }
        else if(position == (maxMonths/2)){
            calendarMonth2GridViewAdapter = new CalendarMonth2GridViewAdapter(mContext, monthLegendMap, month, year, selectedDate, loggedInUserObj);
            currGrid.setAdapter(calendarMonth2GridViewAdapter);
        }
        else{
            calendarMonth3GridViewAdapter = new CalendarMonth3GridViewAdapter(mContext, monthLegendMap, month, year, junkDate, loggedInUserObj);
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
                if (gridViewItemClickListener != null) {
                    LinearLayout calendarGridDayContentLL = null;//(LinearLayout) view.findViewById(R.id.calendarGridDayContentLLId);
                    //get backGround color of the currently clicked date cell
                    int dateCellColor = (int) calendarGridDayContentLL.getTag();

                    //get the date text color
                    TextView gridcell_date_TV = null;//(TextView) view.findViewById(R.id.calendarDayTVId);

                    Date selectedDateFromCell = null;
                    try{
                        selectedDateFromCell = JAVA_DATE_FORMAT_SDF.parse(String.valueOf(calendarGridDayContentLL.getTag(calendarGridDayContentLL.getId())));
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
                        oldMonthView.setBackgroundResource(R.drawable.circle_calendar_one_tap);
                        oldMonthView.setTag(R.drawable.circle_calendar_one_tap);

                        oldMonthView = calendarGridDayContentLL;

                        TextView transactIndicatorView = null;//(TextView) calendarGridDayContentLL.findViewById(R.id.calendarCellTransactionIndicatorTVId);
                        TextView transferIndicatorView = null;//(TextView) calendarGridDayContentLL.findViewById(R.id.calendarCellTransferIndicatorTVId);

                        //if the activity add_update_transaction indicator or add_update_transfer indicator both are invisible...then do not proceed to ViewTransaction page..because there's no point
                        if (transactIndicatorView.getVisibility() == View.GONE && transferIndicatorView.getVisibility() == View.GONE) {
                            showToast("No Activities to Show");
                            return;
                        }

                        //TODO: show fragment with all the transactions and transfers on this calendar_day__

                    } else if (dateCellColor == R.drawable.circle_calendar_no_tap) {
                        Log.i(CLASS_NAME, "New Date Cell is clicked(" + selectedDateFromCell + "), Checking whether its an prev month or next month date");
                        selectedDate = selectedDateFromCell;

                        doMonthChange = false;
                        calendarGridDayContentLL.setBackgroundResource(R.drawable.circle_calendar_one_tap);
                        calendarGridDayContentLL.setTag(R.drawable.circle_calendar_one_tap);
                        oldMonthView = calendarGridDayContentLL;
                    }

                    gridViewItemClickListener.onGridViewItemClick(position);
                }
            }
        };
    }
}