package com.finappl.adapters.calendar_DELETE;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.DayLedger;
import com.finappl.models.UserMO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 8/1/15.
 */
// Inner Class
public class CalendarMonth2GridViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();

    private final Context mContext;
    public static List<String> list;
    private static final int DAY_OFFSET = 1;
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int daysInMonth;

    public static int currMonth, preMonth, nexMonth;

    public String dateToPreSelect;
    public Date toSelectDate;

    private Map<String, DayLedger> monthLegendDataMap;
    private LayoutInflater inflater;

    private final int layoutResourceId = 0;//R.layout.calendar_day__;
    private final int noTapCircle = R.drawable.circle_calendar_no_tap;
    private final int oneTapCircle = R.drawable.circle_calendar_one_tap;
    private static int pastFutureDateColor;
    private static int todayDateColor;
    private static int monthInFocusColor;
    private static int cellSize;

    public int month, year;

    public View todaysView;

    private UserMO loggedInUser;

    //progress bar
    private ProgressDialog mProgressDialog;

    // Days in Current Month
    public CalendarMonth2GridViewAdapter(Context context, Map<String, DayLedger> monthLegendMap, int month, int year, Date dateToPreselect, UserMO loggedInUser) {
        super();
        this.mContext = context;
        list = new ArrayList<>();

        this.month = month;
        this.year = year;

        this.toSelectDate = dateToPreselect;
        this.dateToPreSelect = JAVA_DATE_FORMAT_SDF.format(dateToPreselect);
        this.monthLegendDataMap = monthLegendMap;
        this.loggedInUser = loggedInUser;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //prepare constants. This is to improve performance as object properties have to be read for each cell of the calendar_month in the content_home
        prepareConstants();

        // Print Month
        printMonth(month, year);
    }

    private void prepareConstants() {
        pastFutureDateColor = mContext.getResources().getColor(R.color.calendarNextPrevMonthDate);
        todayDateColor = mContext.getResources().getColor(R.color.calendarTodayDate);
        monthInFocusColor = mContext.getResources().getColor(R.color.calendarThisMonthDate);
        cellSize = mContext.getResources().getInteger(R.integer.calendar_grid_cell_size);
    }

    private int getNumberOfDaysOfMonth(int i) {
        return daysOfMonth[i-1];
    }

    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Prints Month
     *
     * @param mm
     * @param yy
     */
    public void printMonth(int mm, int yy) {
        int trailingSpaces = 0;
        int daysInPrevMonth;
        int prevMonth;
        int prevYear;
        int nextMonth;
        int nextYear;
        int currentMonth = mm;
        currMonth = currentMonth;

        daysInMonth = getNumberOfDaysOfMonth(currentMonth);
        GregorianCalendar cal = new GregorianCalendar(yy, currentMonth-1, 1);

        if(currentMonth == 12) {
            prevMonth = currentMonth - 1;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 1;
            prevYear = yy;
            nextYear = yy + 1;
        } else if(currentMonth == 1) {
            prevMonth = 12;
            prevYear = yy - 1;
            nextYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 2;
        } else {
            prevMonth = currentMonth - 1;
            nextMonth = currentMonth + 1;
            nextYear = yy;
            prevYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
        }
        preMonth = prevMonth;
        nexMonth = nextMonth;

        int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK);

        switch(currentWeekDay){
            case 2 : trailingSpaces = 7;
                break;
            case 3 : trailingSpaces = 1;
                break;
            case 4 : trailingSpaces = 2;
                break;
            case 5 : trailingSpaces = 3;
                break;
            case 6 : trailingSpaces = 4;
                break;
            case 7 : trailingSpaces = 5;
                break;
            case 1 : trailingSpaces = 6;
                break;
            default: Log.i(CLASS_NAME, "Calendar is gonna look ugly because the date cell border setting Algo just died !");
        }

        if(cal.isLeapYear(cal.get(Calendar.YEAR))) {
            if (mm == 2) {
                ++daysInMonth;
            } else if (mm == 3) {
                ++daysInPrevMonth;
            }
        }

        //converting single digit month to double digit..1 as 01..library as 02
        String preMonthStr;
        String currMonthStr;
        String nextMonthStr;

        if(prevMonth<10){
            preMonthStr = "0"+prevMonth;
        }
        else{
            preMonthStr = String.valueOf(prevMonth);
        }

        if(currMonth<10){
            currMonthStr = "0"+currMonth;
        }
        else{
            currMonthStr = String.valueOf(currMonth);
        }

        if(nextMonth<10){
            nextMonthStr = "0"+nextMonth;
        }
        else{
            nextMonthStr = String.valueOf(nextMonth);
        }

        // Trailing Month days
        if(trailingSpaces == 0){
            for(int i = 0; i < 7; i++) {
                list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)+ i) + "-" + preMonthStr + "-" + prevYear);
            }
        }
        else{
            for(int i = 0; i < trailingSpaces; i++) {
                list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)+ i)+ "-" + preMonthStr + "-" + prevYear);
            }
        }

        // Current Month Days
        for(int i = 1; i <= daysInMonth; i++) {
            String dayAsStr = String.valueOf(i);
            if(i<10){
                dayAsStr = "0"+i;
            }

            list.add(dayAsStr  + "-"+ currMonthStr + "-" + yy);
        }
        // Leading Month days
        for(int i = 0; (i < (list.size() % 7) + 7) && (list.size() < 42); i++) {
            String dayAsStr = String.valueOf(i + 1);
            if(Integer.parseInt(dayAsStr)<10){
                dayAsStr = "0"+(i+1);
            }

            list.add(dayAsStr + "-"+ nextMonthStr + "-" + nextYear);
        }

        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        private LinearLayout calendarGridDayContentLL;
        private TextView gridcell_date_TV;
        private TextView calendarDayAmountTV;
        private TextView calendarCellSchTransactionIndicatorTV;
        private TextView calendarCellTransactionIndicatorTV;
        private TextView calendarCellTransferIndicatorTV;
        private TextView calendarCellSchTransferIndicatorTV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layoutResourceId, null);

            // Get a reference to the Day gridcell
            /*mHolder.calendarGridDayContentLL = (LinearLayout) convertView.findViewById(R.id.calendarGridDayContentLLId);
            mHolder.gridcell_date_TV = (TextView) convertView.findViewById(R.id.calendarDayTVId);
            mHolder.calendarDayAmountTV = (TextView) convertView.findViewById(R.id.calendarDayAmountTVId);
            mHolder.calendarCellSchTransactionIndicatorTV = (TextView) convertView.findViewById(R.id.calendarCellSchTransactionIndicatorTVId);
            mHolder.calendarCellTransactionIndicatorTV = (TextView) convertView.findViewById(R.id.calendarCellTransactionIndicatorTVId);
            mHolder.calendarCellTransferIndicatorTV = (TextView) convertView.findViewById(R.id.calendarCellTransferIndicatorTVId);
            mHolder.calendarCellSchTransferIndicatorTV = (TextView) convertView.findViewById(R.id.calendarCellSchTransferIndicatorTVId);
*/
            convertView.setTag(layoutResourceId, mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag(layoutResourceId);
        }

        String theDatsStr = list.get(position);

        mHolder.calendarGridDayContentLL.setBackgroundResource(noTapCircle);

        if(monthLegendDataMap != null && monthLegendDataMap.containsKey(theDatsStr)) {
            DayLedger dayLedger = monthLegendDataMap.get(theDatsStr);

            //for calendar_day__ total amount
            /*if(dayLedger.getTotalAmount() == null || dayLedger.getTotalAmount().equals(0.0)){
                mHolder.calendarDayAmountTV.setVisibility(View.GONE);
            }
            else{
                mHolder.calendarDayAmountTV.setVisibility(View.VISIBLE);
                mHolder.calendarDayAmountTV = FinappleUtility.shortenAmountView(mHolder.calendarDayAmountTV, loggedInUser, dayLedger.getTotalAmount());
            }*/

            //for add_update_transaction indicator
            if(dayLedger.getActivities() != null && dayLedger.getActivities() != null
                    && dayLedger.getActivities().getTransactionsList() != null && !dayLedger.getActivities().getTransactionsList().isEmpty()){
                mHolder.calendarCellTransactionIndicatorTV.setVisibility(View.VISIBLE);
            }
            else{
                mHolder.calendarCellTransactionIndicatorTV.setVisibility(View.GONE);
            }

            //for add_update_transfer indicator
            if(dayLedger.getActivities() != null && dayLedger.getActivities() != null
                    && dayLedger.getActivities().getTransfersList() != null && !dayLedger.getActivities().getTransfersList().isEmpty()){
                mHolder.calendarCellTransferIndicatorTV.setVisibility(View.VISIBLE);
            }
            else{
                mHolder.calendarCellTransferIndicatorTV.setVisibility(View.GONE);
            }

            //for scheduled add_update_transaction indicator
            if(dayLedger.isHasScheduledTransaction()){
                mHolder.calendarCellSchTransactionIndicatorTV.setVisibility(View.VISIBLE);
            }
            else{
                mHolder.calendarCellSchTransactionIndicatorTV.setVisibility(View.GONE);
            }

            //for scheduled transfers indicator
            if(dayLedger.isHasScheduledTransfer()){
                mHolder.calendarCellSchTransferIndicatorTV.setVisibility(View.VISIBLE);
            }
            else{
                mHolder.calendarCellSchTransferIndicatorTV.setVisibility(View.GONE);
            }
        }
        else{
            mHolder.calendarCellTransactionIndicatorTV.setVisibility(View.GONE);
            mHolder.calendarCellTransferIndicatorTV.setVisibility(View.GONE);
            mHolder.calendarCellSchTransactionIndicatorTV.setVisibility(View.GONE);
            mHolder.calendarCellSchTransferIndicatorTV.setVisibility(View.GONE);
            mHolder.calendarDayAmountTV.setVisibility(View.GONE);
        }

        Integer theDayDigit = Integer.parseInt(theDatsStr.split("-")[0]);

        // Set the Day GridCell
        mHolder.gridcell_date_TV.setText(String.valueOf(theDayDigit));

        if((theDayDigit > 22 && position < 7) || (theDayDigit < 14 && position > 27)) {
            mHolder.gridcell_date_TV.setTextColor(pastFutureDateColor);
        }
        else if(JAVA_DATE_FORMAT_SDF.format(new Date()).equalsIgnoreCase(theDatsStr)) {
            mHolder.gridcell_date_TV.setTextColor(todayDateColor);
        }
        else{
            mHolder.gridcell_date_TV.setTextColor(monthInFocusColor);
        }

        int gridDrawable = noTapCircle;

        if(dateToPreSelect.equalsIgnoreCase(theDatsStr)) {
            mHolder.calendarGridDayContentLL.setBackgroundResource(oneTapCircle);
            gridDrawable = oneTapCircle;
            todaysView = mHolder.calendarGridDayContentLL;
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHolder.calendarGridDayContentLL.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = cellSize;
        params.width = cellSize;

        //this is for changing calendar_month cell color on click purpose...do not delete
        mHolder.calendarGridDayContentLL.setTag(gridDrawable);
        mHolder.calendarGridDayContentLL.setTag(mHolder.calendarGridDayContentLL.getId(), theDatsStr);

        //set font for all the text view
        setFont(mHolder.calendarGridDayContentLL);

        return convertView;
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
