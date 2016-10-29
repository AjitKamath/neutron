package com.finappl.adapters.calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.MonthLegend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.finappl.utils.Constants.JAVA_DATE_FORMAT;

/**
 * Created by ajit on 8/1/15.
 */
// Inner Class
public class CalendarMonth2GridViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();

    private final Context _context;
    public static List<String> list;
    private static final int DAY_OFFSET = 1;
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int daysInMonth;
    private int currentDayOfMonth;
    private int currentWeekDay;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    private final String today = dateFormatter.format(new Date());

    public static int currMonth, preMonth, nexMonth;

    public String dateToPreSelect;
    public Date toSelectDate;

    private Map<String, MonthLegend> monthLegendDataMap;
    private LayoutInflater inflater;

    public int month, year;

    public View todaysView;

    // Days in Current Month
    public CalendarMonth2GridViewAdapter(Context context, Map<String, MonthLegend> monthLegendMap, int month, int year, Date dateToPreselect) {
        super();
        this._context = context;
        this.list = new ArrayList<>();

        this.month = month;
        this.year = year;

        SimpleDateFormat sdf = new SimpleDateFormat(JAVA_DATE_FORMAT);
        this.toSelectDate = dateToPreselect;
        this.dateToPreSelect = sdf.format(dateToPreselect);
        this.monthLegendDataMap = monthLegendMap;
        this.inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d(CLASS_NAME, "CalendarAdapter Class is called with date to pre select:" + dateToPreSelect);

        // Print Month
        Log.i(CLASS_NAME, "PERFORMANCE TEST BEGIN-CURR MONTH");
        printMonth(month, year);
        Log.i(CLASS_NAME, "PERFORMANCE TEST ENDS-CURR MONTH");
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
        int daysInPrevMonth = 0;
        int prevMonth = 0;
        int prevYear = 0;
        int nextMonth = 0;
        int nextYear = 0;
        int currentMonth = mm;
        currMonth = currentMonth;

        //String currentMonthName = getMonthAsString(currentMonth);
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

        if(cal.isLeapYear(cal.get(Calendar.YEAR)))
            if(mm == 2)
                ++daysInMonth;
            else if(mm == 3)
                ++daysInPrevMonth;


        //converting single digit month to double digit..1 as 01..2 as 02
        String preMonthStr = null, currMonthStr = null, nextMonthStr = null;
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

            if(i == getCurrentDayOfMonth() && Integer.parseInt(today.split("-")[1]) == currMonth && Integer.parseInt(today.split("-")[2]) == yy) {
                list.add(dayAsStr+ "-"+ currMonthStr + "-" + yy);
            } else {
                list.add(dayAsStr  + "-"+ currMonthStr + "-" + yy);
            }
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

    /**
     * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
     * ALL entries from a SQLite database for that month. Iterate over the
     * List of All entries, and get the dateCreated, which is converted into
     * day.
     *
     * @param year
     * @param month
     * @return
     */
    private Map<String, Integer> findNumberOfEventsPerMonth(int year, int month) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        return map;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        TextView gridcell_date_TV;
        GridLayout grid_cell_GL;
        GridLayout calendarGridDayContentGL;
        TextView calendarCellSchTransactionIndicatorTV;
        TextView calendarCellTransactionIndicatorTV;
        TextView calendarCellTransferIndicatorTV;
        TextView calendarCellSchTransferIndicatorTV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layoutResourceId = R.layout.calendar_grid_cell;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layoutResourceId, null);

            // Get a reference to the Day gridcell
            mHolder.gridcell_date_TV = (TextView) convertView.findViewById(R.id.calendarDayTVId);
            mHolder.grid_cell_GL = (GridLayout) convertView.findViewById(R.id.calendarGridDayGL);
            mHolder.calendarGridDayContentGL = (GridLayout) convertView.findViewById(R.id.calendarGridDayContentGL);
            mHolder.calendarCellSchTransactionIndicatorTV = (TextView) convertView.findViewById(R.id.calendarCellSchTransactionIndicatorTVId);
            mHolder.calendarCellTransactionIndicatorTV = (TextView) convertView.findViewById(R.id.calendarCellTransactionIndicatorTVId);
            mHolder.calendarCellTransferIndicatorTV = (TextView) convertView.findViewById(R.id.calendarCellTransferIndicatorTVId);
            mHolder.calendarCellSchTransferIndicatorTV = (TextView) convertView.findViewById(R.id.calendarCellSchTransferIndicatorTVId);

            convertView.setTag(layoutResourceId, mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag(layoutResourceId);
        }

        // ACCOUNT FOR SPACING-
        String theDatsStr = list.get(position);

        mHolder.calendarGridDayContentGL.setBackgroundResource(R.drawable.circle_calendar_no_tap);

        if(monthLegendDataMap != null && monthLegendDataMap.containsKey(theDatsStr)) {

            //for transaction indicator
            if(monthLegendDataMap.get(theDatsStr).getSummaryModel() != null
                        && !monthLegendDataMap.get(theDatsStr).getSummaryModel().getConsolidatedTransactionModelMap().isEmpty()){
                mHolder.calendarCellTransactionIndicatorTV.setVisibility(View.VISIBLE);
            }
            else{
                mHolder.calendarCellTransactionIndicatorTV.setVisibility(View.GONE);
            }

            //for transfer indicator
            if(monthLegendDataMap.get(theDatsStr).getSummaryModel() != null
                    && !monthLegendDataMap.get(theDatsStr).getSummaryModel().getConsolidatedTransferModelMap().isEmpty()){
                mHolder.calendarCellTransferIndicatorTV.setVisibility(View.VISIBLE);
            }
            else{
                mHolder.calendarCellTransferIndicatorTV.setVisibility(View.GONE);
            }

            //for scheduled transaction indicator
            if(monthLegendDataMap.get(theDatsStr).isHasScheduledTransaction()){
                mHolder.calendarCellSchTransactionIndicatorTV.setVisibility(View.VISIBLE);
            }
            else{
                mHolder.calendarCellSchTransactionIndicatorTV.setVisibility(View.GONE);
            }

            //for scheduled transfers indicator
            if(monthLegendDataMap.get(theDatsStr).isHasScheduledTransfer()){
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
        }

        Integer theDayDigit = Integer.parseInt(theDatsStr.split("-")[0]);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Set the Day GridCell
        mHolder.gridcell_date_TV.setText(String.valueOf(theDayDigit));

        if((theDayDigit > 22 && position < 7) || (theDayDigit < 14 && position > 27)) {
            mHolder.gridcell_date_TV.setTextColor(_context.getResources().getColor(R.color.calendarNextPrevMonthDate));
        }
        else if(sdf.format(new Date()).equalsIgnoreCase(theDatsStr)) {
            mHolder.gridcell_date_TV.setTextColor(_context.getResources().getColor(R.color.calendarTodayDate));
        }
        else{
            mHolder.gridcell_date_TV.setTextColor(_context.getResources().getColor(R.color.calendarThisMonthDate));
        }

        //by default all cell except the date passed in this adapter must have this as background
        int gridDrawable = R.drawable.circle_calendar_no_tap;

        if(dateToPreSelect.equalsIgnoreCase(theDatsStr)) {
            mHolder.calendarGridDayContentGL.setBackgroundResource(R.drawable.circle_calendar_one_tap);
            gridDrawable = R.drawable.circle_calendar_one_tap;
            todaysView = mHolder.calendarGridDayContentGL;
        }

        GridLayout.LayoutParams params = (GridLayout.LayoutParams) mHolder.calendarGridDayContentGL.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = 90;
        params.width = 90;

        //this is for changing grid cell color on click purpose...do not delete
        mHolder.calendarGridDayContentGL.setTag(gridDrawable);
        mHolder.calendarGridDayContentGL.setTag(R.id.calendarGridDayContentGL, theDatsStr);

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(_context.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) convertView.findViewById(R.id.calendarGridDayGL), robotoCondensedLightFont);

        return convertView;
    }

    public int getCurrentDayOfMonth() {
        return currentDayOfMonth;
    }

    private void setCurrentDayOfMonth(int currentDayOfMonth) {
        this.currentDayOfMonth = currentDayOfMonth;
    }

    public void setCurrentWeekDay(int currentWeekDay) {
        this.currentWeekDay = currentWeekDay;
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group, Typeface font) {
        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v, font);
            }
        }
    }
}
