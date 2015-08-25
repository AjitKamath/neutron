package com.finapple.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.finapple.R;
import com.finapple.model.MonthLegend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajit on 8/1/15.
 */
// Inner Class
public class CalendarGridAdpter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();

    private final Context _context;
    public static List<String> list;
    private static final int DAY_OFFSET = 1;
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int daysInMonth;
    private int currentDayOfMonth;
    private int currentWeekDay;
    private TextView gridcell_date_TV;
    private TextView calendarCellSchTransactionIndicatorTV, calendarCellTransactionIndicatorTV,
            calendarCellTransferIndicatorTV, calendarCellSchTransferIndicatorTV;
    private GridLayout grid_cell_GL, calendarGridDayContentGL;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    private final String today = dateFormatter.format(new Date());

    public static int currMonth, preMonth, nexMonth;

    private static String dateToPreSelect;

    private Map<String, MonthLegend> monthLegendDataMap;

    // Days in Current Month
    public CalendarGridAdpter(Context context, Map<String, MonthLegend> monthLegendMap, int day, int month, int year) {
        super();
        this._context = context;
        this.list = new ArrayList<>();
        this.dateToPreSelect = day+"-"+month+"-"+year;
        this.monthLegendDataMap = monthLegendMap;

        Log.d(CLASS_NAME, "CalendarAdapter Class is called with date to pre select:"+dateToPreSelect);

        //setting date to pre select into calendar instance so that we can use it to set grid cell values
        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        // Print Month
        printMonth(month, year);
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
    private void printMonth(int mm, int yy) {
        //Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);

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
                list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)+ i) + "-" + preMonthStr + "-" + prevYear + "-PAST");
            }
        }
        else{
            for(int i = 0; i < trailingSpaces; i++) {
                list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)+ i)+ "-" + preMonthStr + "-" + prevYear+ "-PAST");
            }
        }


        // Current Month Days
        for(int i = 1; i <= daysInMonth; i++) {
            String dayAsStr = String.valueOf(i);
            if(i<10){
                dayAsStr = "0"+i;
            }

            if(i == getCurrentDayOfMonth() && Integer.parseInt(today.split("-")[1]) == currMonth && Integer.parseInt(today.split("-")[2]) == yy) {
                list.add(dayAsStr+ "-"+ currMonthStr + "-" + yy + "-TODAY");
            } else {
                list.add(dayAsStr  + "-"+ currMonthStr + "-" + yy+ "-PRESENT");
            }
        }
        // Leading Month days
        for(int i = 0; (i < (list.size() % 7) + 7) && (list.size() < 42); i++) {
            String dayAsStr = String.valueOf(i + 1);
            if(Integer.parseInt(dayAsStr)<10){
                dayAsStr = "0"+(i+1);
            }

            list.add(dayAsStr + "-"+ nextMonthStr + "-" + nextYear+ "-FUTURE" );
        }
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) _context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.calendar_grid_cell, parent, false);
        }

        // Get a reference to the Day gridcell
        gridcell_date_TV = (TextView) row.findViewById(R.id.calendarDayTVId);
        grid_cell_GL = (GridLayout) row.findViewById(R.id.calendarGridDayGL);
        calendarGridDayContentGL = (GridLayout)row.findViewById(R.id.calendarGridDayContentGL);
        calendarCellSchTransactionIndicatorTV = (TextView) row.findViewById(R.id.calendarCellSchTransactionIndicatorTVId);
        calendarCellTransactionIndicatorTV = (TextView) row.findViewById(R.id.calendarCellTransactionIndicatorTVId);
        calendarCellTransferIndicatorTV = (TextView) row.findViewById(R.id.calendarCellTransferIndicatorTVId);
        calendarCellSchTransferIndicatorTV = (TextView) row.findViewById(R.id.calendarCellSchTransferIndicatorTVId);

        //Algo impl for setting up padding to show thin borders on date cells |_|_|_|_|
        int right=1, bottom=1, left=0, top=0;

        if(position >= 0 && position < 7){
            top = 1;
        }

        if(position == 0 || position == 7 || position == 14 || position == 21 || position == 28 || position == 35){
            left = 1;
        }

        grid_cell_GL.setPadding(left,top,right,bottom);
        //--end of Algo impl

        // ACCOUNT FOR SPACING-
        String[] daysArr = list.get(position).split("-");
        String theday = daysArr[0];
        String themonth = daysArr[1];
        String theyear = daysArr[2];
        String dayType = daysArr[3];

        String dateToCheckStr = theday+"-"+themonth+"-"+theyear;

        if(monthLegendDataMap != null && monthLegendDataMap.containsKey(dateToCheckStr)) {

            //for transaction indicator
            if(monthLegendDataMap.get(dateToCheckStr).getConsolidatedTransactionModelList() != null
                        && !monthLegendDataMap.get(dateToCheckStr).getConsolidatedTransactionModelList().isEmpty()
                        && !"".equalsIgnoreCase(monthLegendDataMap.get(dateToCheckStr).getConsolidatedTransactionModelList().get(0).getCategory())){
                calendarCellTransactionIndicatorTV.setVisibility(View.VISIBLE);
            }
            else{
                calendarCellTransactionIndicatorTV.setVisibility(View.GONE);
            }

            //for transfer indicator
            if(monthLegendDataMap.get(dateToCheckStr).isHasTransfer()){
                calendarCellTransferIndicatorTV.setVisibility(View.VISIBLE);
            }
            else{
                calendarCellTransferIndicatorTV.setVisibility(View.GONE);
            }

            //for scheduled transaction indicator
            if(monthLegendDataMap.get(dateToCheckStr).isHasScheduledTransaction()){
                calendarCellSchTransactionIndicatorTV.setVisibility(View.VISIBLE);
            }
            else{
                calendarCellSchTransactionIndicatorTV.setVisibility(View.GONE);
            }

            //for scheduled transfers indicator
            if(monthLegendDataMap.get(dateToCheckStr).isHasScheduledTransfer()){
                calendarCellSchTransferIndicatorTV.setVisibility(View.VISIBLE);
            }
            else{
                calendarCellSchTransferIndicatorTV.setVisibility(View.GONE);
            }

        }
        else{
            calendarCellTransactionIndicatorTV.setVisibility(View.GONE);
            calendarCellTransferIndicatorTV.setVisibility(View.GONE);
            calendarCellSchTransactionIndicatorTV.setVisibility(View.GONE);
            calendarCellSchTransferIndicatorTV.setVisibility(View.GONE);
        }

        // Set the Day GridCell
        gridcell_date_TV.setText(String.valueOf(Integer.parseInt(theday)));

        if("PAST".equalsIgnoreCase(dayType) || "FUTURE".equalsIgnoreCase(dayType)) {
            gridcell_date_TV.setTextColor(gridcell_date_TV.getResources().getColor(R.color.calendarNextPrevMonthDate));
        }
        else if("PRESENT".equalsIgnoreCase(dayType)) {
            gridcell_date_TV.setTextColor(gridcell_date_TV.getResources().getColor(R.color.calendarThisMonthDate));
        }
        else if("TODAY".equalsIgnoreCase(dayType)) {
            gridcell_date_TV.setTextColor(gridcell_date_TV.getResources().getColor(R.color.calendarTodayDate));
        }

        String dateToPreSelectArr[] = dateToPreSelect.split("-");

        //by default all cell except the date passed in this adapter must have this as background
        int gridDrawable = R.drawable.circle_calendar_no_tap;

        if(Integer.parseInt(theday) == Integer.parseInt(dateToPreSelectArr[0]) && (Integer.parseInt(themonth)) == Integer.parseInt(dateToPreSelectArr[1])
                    && Integer.parseInt(theyear) == Integer.parseInt(dateToPreSelectArr[2]) && !("PAST".equalsIgnoreCase(dayType) || "FUTURE".equalsIgnoreCase(dayType))) {
            calendarGridDayContentGL.setBackgroundResource(R.drawable.circle_calendar_one_tap);
            gridDrawable = R.drawable.circle_calendar_one_tap;
        }

        //this is for changing grid cell color on click purpose...do not delete
        calendarGridDayContentGL.setTag(gridDrawable);

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(_context.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) row.findViewById(R.id.calendarGridDayGL), robotoCondensedLightFont);

        return row;
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
