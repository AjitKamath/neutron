package com.finappl.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.adapters.AccountsAdapter;
import com.finappl.adapters.AddUpdateTransactionSpinnerAdapter;
import com.finappl.adapters.BudgetsAdapter;
import com.finappl.adapters.CalendarGridAdpter;
import com.finappl.adapters.CalendarSchedulesSectionListAdapter;
import com.finappl.adapters.ConsolidatedSummaryAdapter;
import com.finappl.adapters.SummaryPopperListAdapter;
import com.finappl.dbServices.AddUpdateTransactionsDbService;
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
import com.finappl.models.SpinnerModel;
import com.finappl.models.TodaysNotifications;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.models.UsersModel;
import com.finappl.services.NotificationsService;
import com.finappl.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint("NewApi")
public class CalendarActivity extends Activity {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    //calendar
    private CalendarGridAdpter adapter;// adapter instance
    private GridView calendarView;
    private Calendar _calendar;
    private String selectedDateStr = sdf.format(new Date());

    //header
    private TextView yearTV, /*calendarFinappleNameTV,*/ calendarMonthTV;

    //summary
    private ListView consolTranLV, accountsLV, budgetsLV, schedulesLV;
    private TextView calendarNoTransTV, calendarNoAccountsTV, calendarNoBudgetsTV, calendarTransHeaderOrMsgTV,
            calendarAddTV, calendarTransferTV;
    private TextView calendarSummaryTV, calendarAccountsTV, calendarBudgetsTV, calendarSchedulesTV;

    //month legend
    private Map<String, MonthLegend> monthLegendMap = new HashMap<String, MonthLegend>();

    //popup
    private Dialog dialog;

    //back default_button counter
    private Integer backButtonCounter = 0;

    //db services
    private Sqlite controller = new Sqlite(mContext);
    private CalendarDbService calendarDbService = new CalendarDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);
    private AddUpdateTransactionsDbService addUpdateTransactionsDbService = new AddUpdateTransactionsDbService(mContext);
    private AddUpdateTransfersDbService addUpdateTransfersDbService = new AddUpdateTransfersDbService(mContext);

    //User
    private UsersModel loggedInUserObj;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        //this is to ensure the tables are in the db...actually calls the Sqlite class constructor..importance of this line is known only when the db is deleted and the app is run
        Log.i(CLASS_NAME, "Initializing the application database starts");
        controller.getWritableDatabase();
        Log.i(CLASS_NAME, "Initializing the application database ends");

        //get the Active user
        Log.i(CLASS_NAME, "Getting the active/logged in user starts");
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return;
        }
        Log.i(CLASS_NAME, "Getting the active/logged in user ends");

        //initialize calendar
        Log.i(CLASS_NAME, "Initializing the calendar starts");
        initializeCalendar();
        Log.i(CLASS_NAME, "Initializing the calendar ends");

        String selectedDateStrArr[] = selectedDateStr.split("-");

        if(selectedDateStrArr[0].length() == 1){
            selectedDateStrArr[0] = "0"+ selectedDateStrArr[0];
        }
        if(selectedDateStrArr[1].length() == 1){
            selectedDateStrArr[1] = "0"+ selectedDateStrArr[1];
        }

        //set current date
        selectedDateStr = selectedDateStrArr[0]+"-"+selectedDateStrArr[1]+"-"+selectedDateStrArr[2];

        Log.i(CLASS_NAME, "Initializing the UI components starts");
        initUIComponents();
        Log.i(CLASS_NAME, "Initializing the UI components ends");

        //set up header
        Log.i(CLASS_NAME, "setting up the header starts");
        setUpHeader();
        Log.i(CLASS_NAME, "setting up the header ends");

        //set up calendar
        Log.i(CLASS_NAME, "setting up calendar in the UI starts");
        setGridCellAdapterToDate(Integer.parseInt(selectedDateStrArr[0]), Integer.parseInt(selectedDateStrArr[1]), Integer.parseInt(selectedDateStrArr[2]));
        Log.i(CLASS_NAME, "setting up calendar in the UI ends");

        //set up summary
        Log.i(CLASS_NAME, "setting up summary tab in the UI starts");
        setUpSummary();
        Log.i(CLASS_NAME, "setting up summary tab in the UI ends");

        //set up accounts
        Log.i(CLASS_NAME, "setting up accounts tab in the UI starts");
        setUpAccounts();
        Log.i(CLASS_NAME, "setting up accounts tab in the UI ends");

        //set up budgets_view
        Log.i(CLASS_NAME, "setting up budgets tab in the UI starts");
        setUpBudgets();
        Log.i(CLASS_NAME, "setting up budgets tab in the UI ends");

        //set up schedules
        Log.i(CLASS_NAME, "setting up schedules tab in the UI starts");
        setUpSchedules();
        Log.i(CLASS_NAME, "setting up schedules tab in the UI ends");

        //prepare services
        Log.i(CLASS_NAME, "setting up all the services starts ");
        setUpServices();
        Log.i(CLASS_NAME, "setting up all the services ends");

        //set font for all the text view
        Log.i(CLASS_NAME, "setting fonts for all the UI components starts");
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.calendarPageRLId), robotoCondensedLightFont);
        Log.i(CLASS_NAME, "setting fonts for all the UI components ends");
    }

    private void setUpServices() {
        //prepare notifications service
        //pass todaysNotificationsObj to the NotificationsService through the intent
        Intent intent = new Intent(this, NotificationsService.class);
        intent.putExtra("TODAYS_NOTIFS", getTodaysNotifications());

        //reboot service
        stopService(intent);
        startService(intent);
    }

    private TodaysNotifications getTodaysNotifications(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String todayStr = sdf.format(new Date());

        //get all the scheduled Transactions/Transfers from the month legend
        List<ScheduledTransactionModel> schedTransactionModelObjList = null;
        List<ScheduledTransferModel> scheduledTransferModelObjList = null;
        if(monthLegendMap != null && !monthLegendMap.isEmpty() && monthLegendMap.get(todayStr) != null
                && monthLegendMap.get(todayStr).getScheduledTransactionModelList() != null){
            Log.i(CLASS_NAME, "Trying to get All Scheduled Transactions/Transfers from MonthLegend to display them as notification_scheduled_transaction");
            MonthLegend monthLegendObj = monthLegendMap.get(todayStr);

            schedTransactionModelObjList = monthLegendObj.getScheduledTransactionModelList();
            scheduledTransferModelObjList = monthLegendObj.getScheduledTransferModelList();
        }

        TodaysNotifications todaysNotificationsObj = new TodaysNotifications();
        todaysNotificationsObj.setLoggedInUser(loggedInUserObj);

        //for scheduled transactions starts--
        if(schedTransactionModelObjList != null && !schedTransactionModelObjList.isEmpty()){
            Log.i(CLASS_NAME, "There are no Scheduled Transactions for the date("+todayStr+")");
            //remove those schedules who have been either addded already or rejected by the user.
            Log.i(CLASS_NAME, "Found " + schedTransactionModelObjList.size() + " Scheduled Transactions...but need to filter out those which are already added/cancelled");
            todaysNotificationsObj.setTodaysSchedTransactionsList(calendarDbService.getSchedTransactionsListAfterCancelledNotifsOnDate(schedTransactionModelObjList
                    , loggedInUserObj.getUSER_ID(), todayStr));
            Log.i(CLASS_NAME, "After filtering already added or cancelled , found " + schedTransactionModelObjList.size() + " scheduled transactions");
            Log.i(CLASS_NAME, "Finished building notifications for Scheduled Transactions");
        }
        //for scheduled transactions ends--

        //for scheduled transfers starts--
        if(scheduledTransferModelObjList != null && !scheduledTransferModelObjList.isEmpty()){
            Log.i(CLASS_NAME, "There are no Scheduled Transfers for the date("+todayStr+")");
            //remove those schedules who have been either addded already or rejected by the user.
            Log.i(CLASS_NAME, "Found " + scheduledTransferModelObjList.size() + " Scheduled Transfers...but need to filter out those which are already added/cancelled");
            todaysNotificationsObj.setTodaysSchedTransfersList(calendarDbService.getSchedTransfersListAfterCancelledNotifsOnDate(scheduledTransferModelObjList
                    , loggedInUserObj.getUSER_ID(), todayStr));
            Log.i(CLASS_NAME, "After filtering already added or cancelled , found " + scheduledTransferModelObjList.size() + " scheduled transfers");
            Log.i(CLASS_NAME, "Finished building notifications for Scheduled Transfers");
        }
        //for scheduled transfers ends--

        return todaysNotificationsObj;
    }

    private void getMonthLegend() {
        monthLegendMap = calendarDbService.getMonthLegendOnDate(selectedDateStr, loggedInUserObj.getUSER_ID());
    }

    public void onSettingsClick(View view){
        Intent intent
                = new Intent(CalendarActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private Intent toAddUpdateTransaction(){
        TransactionModel tranObj = new TransactionModel();
        tranObj.setTRAN_DATE(selectedDateStr.substring(0, selectedDateStr.lastIndexOf("-")));
        Intent intent = new Intent(CalendarActivity.this, AddUpdateTransactionActivity.class);
        intent.putExtra("TRANSACTION_OBJ", tranObj);
        return  intent;
    }

    private Intent toAddUpdateTransfer(){
        TransferModel tranfrObj = new TransferModel();
        tranfrObj.setTRNFR_DATE(selectedDateStr.substring(0, selectedDateStr.lastIndexOf("-")));
        Intent intent = new Intent(CalendarActivity.this, AddUpdateTransferActivity.class);
        intent.putExtra("TRANSFER_OBJ", tranfrObj);
        return  intent;
    }

    private void setUpAccounts() {
        List<AccountsModel> accountsList = calendarDbService.getAllAccounts(loggedInUserObj.getUSER_ID());

        AccountsAdapter accAdapter = new AccountsAdapter(this, R.layout.calendar_accounts_list_view, accountsList);
        accountsLV.setAdapter(accAdapter);

        //if there are no accounts o the selected date then show no accounts text
        if(accAdapter != null & accAdapter.getCount() != 0){
            calendarNoAccountsTV.setVisibility(View.GONE);
            accountsLV.setVisibility(View.VISIBLE);
        }
        else{
            calendarNoAccountsTV.setVisibility(View.VISIBLE);
            accountsLV.setVisibility(View.GONE);
        }
    }

    private void setUpBudgets() {
        List<BudgetModel> budgetsList = calendarDbService.getAllBudgets(selectedDateStr, loggedInUserObj.getUSER_ID());

        if(budgetsList == null){
            return;
        }

        BudgetsAdapter budgetsAdapter = new BudgetsAdapter(this, R.layout.calendar_budgets_list_view, budgetsList);
        budgetsLV.setAdapter(budgetsAdapter);

        //if there are no transactions o the selected date then show no transaction text
        if(budgetsAdapter != null & budgetsAdapter.getCount() != 0){
            calendarNoBudgetsTV.setVisibility(View.GONE);
            budgetsLV.setVisibility(View.VISIBLE);
        }
        else{
            calendarNoBudgetsTV.setVisibility(View.VISIBLE);
            budgetsLV.setVisibility(View.GONE);
        }
    }

    private void setUpSchedules() {
        if(monthLegendMap == null || (monthLegendMap != null && monthLegendMap.isEmpty())){
            Log.i(CLASS_NAME, "Nothing found in MonthLegend... No point in setting up Schedules. Continuing");
            return;
        }

        String selectedDateStrArr[] = selectedDateStr.split("-");
        Integer tempMonth =  Integer.parseInt(selectedDateStrArr[1]);
        if(tempMonth < 10){
            selectedDateStrArr[1] = "0"+ tempMonth;
        }
        String tempDateStr = selectedDateStrArr[0]+"-"+selectedDateStrArr[1]+"-"+selectedDateStrArr[2];

        CalendarSchedulesSectionListAdapter schedulesListAdapter = new CalendarSchedulesSectionListAdapter(this,
                R.layout.calendar_schedules_list_view_header,  R.layout.calendar_schedules_list_view, monthLegendMap.get(tempDateStr));
        schedulesLV.setAdapter(schedulesListAdapter);
        schedulesListAdapter.notifyDataSetChanged();

        if(schedulesListAdapter != null & schedulesListAdapter.getCount() != 0){
            SimpleDateFormat wrongSdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat rightSdf = new SimpleDateFormat("d MMM ''yy");
            try {
                String dayStr = rightSdf.format(wrongSdf.parse(tempDateStr));
                calendarTransHeaderOrMsgTV.setText(dayStr);
            }
            catch (ParseException e) {
                Log.e(CLASS_NAME, "ERROR !! "+e);
            }

            calendarTransHeaderOrMsgTV.setTextColor(calendarTransHeaderOrMsgTV.getResources().getColor(R.color.orrange));
            schedulesLV.setVisibility(View.VISIBLE);
        }
        else{
            calendarTransHeaderOrMsgTV.setText("No Scheduled Transactions/Transfers");
            calendarTransHeaderOrMsgTV.setTextColor(calendarTransHeaderOrMsgTV.getResources().getColor(R.color.DarkGray));
            schedulesLV.setVisibility(View.GONE);
        }
    }

    private void setUpSummary() {
        String selectedDateStrArr[] = selectedDateStr.split("-");
        Integer tempMonth =  Integer.parseInt(selectedDateStrArr[1]);
        if(tempMonth < 10){
            selectedDateStrArr[1] = "0"+ tempMonth;
        }

        String tempDate = selectedDateStrArr[0]+"-"+selectedDateStrArr[1]+"-"+selectedDateStrArr[2];

        if(!monthLegendMap.containsKey(tempDate) || (monthLegendMap.containsKey(tempDate)
                && (monthLegendMap.get(tempDate).getSummaryModel().getConsolidatedTransactionModelMap().isEmpty()
                && monthLegendMap.get(tempDate).getSummaryModel().getConsolidatedTransferModelMap().isEmpty()))){
            Log.i(CLASS_NAME, "Boring Day !! No summary to show today");

            calendarNoTransTV.setText("No Transactions/Transfers");
            calendarNoTransTV.setTextColor(calendarNoTransTV.getResources().getColor(R.color.DarkGray));
            consolTranLV.setVisibility(View.GONE);

            return;
        }

        SimpleDateFormat wrongSdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat rightSdf = new SimpleDateFormat("d MMM ''yy");
        try {
            String dayStr = rightSdf.format(wrongSdf.parse(tempDate));
            calendarNoTransTV.setText(dayStr);
        }
        catch (ParseException e) {
            Log.e(CLASS_NAME, "ERROR !! "+e);
        }

        calendarNoTransTV.setTextColor(calendarNoTransTV.getResources().getColor(R.color.orrange));
        consolTranLV.setVisibility(View.VISIBLE);


        ConsolidatedSummaryAdapter consolAdapter = new ConsolidatedSummaryAdapter(this, R.layout.calendar_summary_list_view, monthLegendMap.get(tempDate).getSummaryModel());
        consolTranLV.setAdapter(consolAdapter);
        consolTranLV.setOnItemClickListener(listViewClickListener);
    }

    public void onTabSelect(View view){
        Log.i(CLASS_NAME, "Son....i created you. The tab implementation...although sucks, a very own of my creation. And you work !! selected: " + view.getId());

        LinearLayout calendarSummaryTabLL, calendarAccTabLL, calendarBudgetsTabLL, calendarSchedulesTabLL;
        calendarSummaryTabLL = (LinearLayout) this.findViewById(R.id.calendarSummaryTabLLId);
        calendarAccTabLL = (LinearLayout) this.findViewById(R.id.calendarAccTabLLId);
        calendarBudgetsTabLL = (LinearLayout) this.findViewById(R.id.calendarBudgetsTabLLId);
        calendarSchedulesTabLL = (LinearLayout) this.findViewById(R.id.calendarSchedulesTabLLId);

        switch(view.getId()){
            case R.id.calendarSummaryTVId: if(!"SELECTED".equalsIgnoreCase(view.getTag().toString())){
                    deselectAllTabs();
                    calendarSummaryTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarSummaryTV.setTextColor(calendarSummaryTV.getResources().getColor(R.color.white));
                    view.setTag("SELECTED");
                    calendarSummaryTabLL.setVisibility(View.VISIBLE);
                    calendarAccTabLL.setVisibility(View.GONE);
                    calendarBudgetsTabLL.setVisibility(View.GONE);
                    calendarSchedulesTabLL.setVisibility(View.GONE);
                }
                break;

            case R.id.calendarAccountsTVId : if(!"SELECTED".equalsIgnoreCase(view.getTag().toString())){
                    deselectAllTabs();
                    calendarAccountsTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarAccountsTV.setTextColor(calendarSummaryTV.getResources().getColor(R.color.white));
                    view.setTag("SELECTED");
                    calendarSummaryTabLL.setVisibility(View.GONE);
                    calendarAccTabLL.setVisibility(View.VISIBLE);
                    calendarBudgetsTabLL.setVisibility(View.GONE);
                    calendarSchedulesTabLL.setVisibility(View.GONE);
                }
                break;

            case R.id.calendarBudgetsTVId : if(!"SELECTED".equalsIgnoreCase(view.getTag().toString())){
                    deselectAllTabs();
                    calendarBudgetsTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarBudgetsTV.setTextColor(calendarSummaryTV.getResources().getColor(R.color.white));
                    view.setTag("SELECTED");
                    calendarSummaryTabLL.setVisibility(View.GONE);
                    calendarAccTabLL.setVisibility(View.GONE);
                    calendarBudgetsTabLL.setVisibility(View.VISIBLE);
                    calendarSchedulesTabLL.setVisibility(View.GONE);
                }
                break;

            case R.id.calendarSchedulesTVId : if(!"SELECTED".equalsIgnoreCase(view.getTag().toString())){
                    deselectAllTabs();
                    calendarSchedulesTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarSchedulesTV.setTextColor(calendarSchedulesTV.getResources().getColor(R.color.white));
                    view.setTag("SELECTED");
                    calendarSummaryTabLL.setVisibility(View.GONE);
                    calendarAccTabLL.setVisibility(View.GONE);
                    calendarBudgetsTabLL.setVisibility(View.GONE);
                    calendarSchedulesTabLL.setVisibility(View.VISIBLE);
                }
                break;

            default: showToast("Tab Error !!");
        }
    }

    private void deselectAllTabs(){
        //summary tab
        calendarSummaryTV.setBackgroundResource(R.drawable.calendar_small_tab_inactive_inner);
        calendarSummaryTV.setTextColor(calendarSummaryTV.getResources().getColor(R.color.finappleTheme));
        calendarSummaryTV.setTag("");

        calendarAccountsTV.setBackgroundResource(R.drawable.calendar_small_tab_inactive_inner);
        calendarAccountsTV.setTextColor(calendarSummaryTV.getResources().getColor(R.color.finappleTheme));
        calendarAccountsTV.setTag("");

        calendarBudgetsTV.setBackgroundResource(R.drawable.calendar_small_tab_inactive_inner);
        calendarBudgetsTV.setTextColor(calendarSummaryTV.getResources().getColor(R.color.finappleTheme));
        calendarBudgetsTV.setTag("");

        calendarSchedulesTV.setBackgroundResource(R.drawable.calendar_small_tab_inactive_inner);
        calendarSchedulesTV.setTextColor(calendarSummaryTV.getResources().getColor(R.color.finappleTheme));
        calendarSchedulesTV.setTag("");
    }

    private void initializeCalendar(){
        //this method runs on app start up, so setting the calendar to current actual state
        _calendar = Calendar.getInstance(Locale.getDefault());
    }

    private void initUIComponents() {
        //get UI components

        //header
        //calendarFinappleNameTV = (TextView) this.findViewById(R.id.calendarFinappleNameTVId);
        yearTV = (TextView) this.findViewById(R.id.calendarFullYearId);
        calendarMonthTV = (TextView) this.findViewById(R.id.calendarMonthId);

        //set up options popper
        setUpOptionsPopper();

        //calendar
        calendarView = (GridView) this.findViewById(R.id.calendarPageCalendarGVId);

        //summary
        calendarNoTransTV = (TextView) this.findViewById(R.id.calendarNoTransTVId);
        calendarNoAccountsTV = (TextView) this.findViewById(R.id.calendarNoAccountsTVId);
        calendarNoBudgetsTV = (TextView) this.findViewById(R.id.calendarNoBudgetsTVId);
        calendarTransHeaderOrMsgTV = (TextView) this.findViewById(R.id.calendarTransHeaderOrMsgTVId);

        //tabs
        calendarSummaryTV = (TextView) this.findViewById(R.id.calendarSummaryTVId);
        calendarAccountsTV = (TextView) this.findViewById(R.id.calendarAccountsTVId);
        calendarBudgetsTV = (TextView) this.findViewById(R.id.calendarBudgetsTVId);
        calendarSchedulesTV = (TextView) this.findViewById(R.id.calendarSchedulesTVId);

        //buttons
        calendarAddTV = (TextView) this.findViewById(R.id.calendarAddTVId);
        calendarTransferTV = (TextView) this.findViewById(R.id.calendarTransferTVId);

        consolTranLV = (ListView) this.findViewById(R.id.consolTranLVId);
        accountsLV = (ListView) this.findViewById(R.id.accountsLVId);
        budgetsLV = (ListView) this.findViewById(R.id.budgetsLVId);
        schedulesLV = (ListView) this.findViewById(R.id.schedulesLVId);
    }

    private void setUpOptionsPopper() {
        //setup options
        ImageView optionsIV = (ImageView) this.findViewById(R.id.calendarOptionsIVId);
        final PopupMenu popupMenu = new PopupMenu(this, optionsIV);
        popupMenu.inflate(R.menu.calendar_options);

        optionsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.calendarOptionBudgetId:
                        break;
                    case R.id.calendarOptionReportId:
                        break;
                    case R.id.calendarOptionPlanABuy:
                        break;
                }
                return true;
            }
        });
    }

    private void setUpHeader() {
        //convert the selectedDateStr which's in raw format dd-(MM-1)-yyyy-TYPE to dd MMM yy and WEEK
        String tempSelectedDateStrArr[] = selectedDateStr.split("-");
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(tempSelectedDateStrArr[2]), Integer.parseInt(tempSelectedDateStrArr[1]) - 1, Integer.parseInt(tempSelectedDateStrArr[0]));

        int date = Integer.parseInt(tempSelectedDateStrArr[0]);
        String yearStr = String.valueOf(cal.get(cal.YEAR));
        int month = cal.get(cal.MONTH);

        //set year
        yearTV.setText(yearStr);
        calendarMonthTV.setText(Constants.MONTHS_ARRAY[month]);
    }

    //pass month as jan-1 feb-2
    private void setGridCellAdapterToDate(int day, int month, int year) {
        _calendar.set(year, month-1, day);
        month = _calendar.get(Calendar.MONTH);
        year = _calendar.get(Calendar.YEAR);
        day = _calendar.get(Calendar.DAY_OF_MONTH);

       // GridView calendarView2 = (GridView) this.findViewById(R.id.calendarPageCalendarGVId2);
       // GridView calendarView3 = (GridView) this.findViewById(R.id.calendarPageCalendarGVId3);

        //get this months legend
        getMonthLegend();

        //update summary
        //setUpSummary();

        //set up budgets_view
        //setUpBudgets();

        //set up schedules
        //setUpSchedules();

        adapter = new CalendarGridAdpter(this, monthLegendMap, day, month+1, year);

        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);

        //calendarView2.setAdapter(adapter);
        //calendarView3.setAdapter(adapter);

        String tempMonthStr = String.valueOf(month+1);
        String tempDayStr= String.valueOf(day);
        if(month<10){
            tempMonthStr = "0" + tempMonthStr;
        }

        if(day < 10){
            tempDayStr = "0" + tempDayStr;
        }

        selectedDateStr = tempDayStr + "-" + tempMonthStr + "-" + year + "-PRESENT";

        calendarView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedDateStr = CalendarGridAdpter.list.get(position);
                String selectedDateStrArr[] = selectedDateStr.split("-");
                int selectedDate = Integer.parseInt(selectedDateStrArr[0]);
                int selectedYear = Integer.parseInt(selectedDateStrArr[2]);

                //update header
                setUpHeader();

                GridLayout calendarGridDayContentGL = (GridLayout) view.findViewById(R.id.calendarGridDayContentGL);

                //get backGround color of the currently clicked date cell
                int dateCellColor = (int) calendarGridDayContentGL.getTag();

                //user selected date is not in current month..either past month or next month
                if (selectedDateStr.contains("PAST") || selectedDateStr.contains("FUTURE")) {
                    Log.i(CLASS_NAME, "I clicked on either PAST or FUTURE month");

                    //get month legend before the month is refreshed
                    //getMonthLegend();

                    //user has selected the past month
                    if (position < 7) {
                        Log.i(CLASS_NAME, "oh..its a PAST");
                        setGridCellAdapterToDate(selectedDate, CalendarGridAdpter.preMonth, selectedYear);
                    }
                    //user has selected the future month
                    else if (position > 27) {
                        Log.i(CLASS_NAME, "wow..the FUTURE !!");
                        setGridCellAdapterToDate(selectedDate, CalendarGridAdpter.nexMonth, selectedYear);
                    }
                } else {
                    Log.i(CLASS_NAME, "Wandering in the same month and wondering why i'm even looking at this log !!");

                    if (dateCellColor == R.drawable.circle_calendar_one_tap) {
                        Log.i(CLASS_NAME, "Dude u just clicked on the same cell twice !! either u r high or u want to view the transactions in detail..");
                        calendarGridDayContentGL.setBackgroundResource(R.drawable.circle_calendar_two_tap);
                        calendarGridDayContentGL.setTag(R.drawable.circle_calendar_two_tap);

                        TextView transactIndicatorView = (TextView) calendarGridDayContentGL.findViewById(R.id.calendarCellTransactionIndicatorTVId);
                        TextView transferIndicatorView = (TextView) calendarGridDayContentGL.findViewById(R.id.calendarCellTransferIndicatorTVId);

                        //if the activity transaction indicator or transfer indicator both are invisible...then do not proceed to ViewTransaction page..because there's no point
                        if (transactIndicatorView.getVisibility() == View.GONE && transferIndicatorView.getVisibility() == View.GONE) {
                            showToast("No Activities to Show");
                            return;
                        }

                        //go to view transaction activity
                        Intent intent = new Intent(CalendarActivity.this, ViewActivitiesActivity.class);

                        ActivityModel activityModel = new ActivityModel();
                        activityModel.setFromDateStr(selectedDateStr.substring(0, selectedDateStr.lastIndexOf("-")));
                        activityModel.setToDateStr(selectedDateStr.substring(0, selectedDateStr.lastIndexOf("-")));
                        activityModel.setWhichActivityStr("TRANSACTIONS");

                        //if there's only transfer in this date..then show transfers as default opened tab in view activities page
                        if (transactIndicatorView.getVisibility() != View.VISIBLE && transferIndicatorView.getVisibility() == View.VISIBLE) {
                            activityModel.setWhichActivityStr("TRANSFER");
                        }
                        intent.putExtra("ACTIVITY_OBJ", activityModel);

                        startActivity(intent);
                        finish();
                    } else if (dateCellColor == R.drawable.circle_calendar_no_tap) {
                        Log.i(CLASS_NAME, "Oooo.. New Date Cell..O Magic wand..turn this cell into blue !!");

                        View cell = null;
                        for (int i = 0; i < 42; i++) {
                            cell = calendarView.getChildAt(i).findViewById(calendarGridDayContentGL.getId());
                            cell.setBackgroundResource(R.drawable.circle_calendar_no_tap);
                            cell.setTag(R.drawable.circle_calendar_no_tap);
                        }
                        calendarGridDayContentGL.setBackgroundResource(R.drawable.circle_calendar_one_tap);
                        calendarGridDayContentGL.setTag(R.drawable.circle_calendar_one_tap);
                    } else {
                        //if the activity transaction indicator or transfer indicator both are invisible...then do not proceed to ViewTransaction page..because there's no point
                        if (calendarGridDayContentGL.findViewById(R.id.calendarCellTransactionIndicatorTVId).getVisibility() == View.GONE
                                && calendarGridDayContentGL.findViewById(R.id.calendarCellTransferIndicatorTVId).getVisibility() == View.GONE) {
                            showToast("No Activities to Show");
                            return;
                        }
                    }
                }

                //update summary
                setUpSummary();

                //update budgets_view
                setUpBudgets();

                //update schedules
                setUpSchedules();
            }
        });
    }

    public void goToSettings(View view){
        final ImageView imageIV = (ImageView)this.findViewById(R.id.calendarSidePaneImgId);

        int currentRotation = 0;
        final RotateAnimation rotateAnim = new RotateAnimation(currentRotation, currentRotation + 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.setDuration(250);
        rotateAnim.setFillEnabled(true);
        rotateAnim.setFillAfter(true);

        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(mContext, SettingsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageIV.startAnimation(rotateAnim);
    }

    private void prepareDialog(int layout){
        dialog = new Dialog(this, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.y = 80;
        window.setAttributes(wlp);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                calendarAddTV.setBackgroundResource(R.drawable.oval_buttons_text_view);
                calendarTransferTV.setBackgroundResource(R.drawable.oval_buttons_text_view);
            }
        });
    }

    public void showTransactionPopper(View view){
        // Create custom calendar_transaction_options_popper object
        prepareDialog(R.layout.calendar_transaction_options_popper);

        calendarAddTV.setBackgroundResource(R.drawable.oval_buttons_selected_text_view);

        //texts
        TextView transactionPopperNewTV, transactionPopperQuickTV, transactionPopperSchedTV;
        transactionPopperNewTV = (TextView) dialog.findViewById(R.id.transactionPopperNewTVId);
        transactionPopperQuickTV = (TextView) dialog.findViewById(R.id.transactionPopperQuickTVId);
        transactionPopperSchedTV = (TextView) dialog.findViewById(R.id.transactionPopperSchedTVId);

        transactionPopperNewTV.setOnClickListener(linearLayoutClickListener);
        transactionPopperQuickTV.setOnClickListener(linearLayoutClickListener);
        transactionPopperSchedTV.setOnClickListener(linearLayoutClickListener);


        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) dialog.findViewById(R.id.calendarTransactionsPopperLLId), robotoCondensedLightFont);
    }

    public void showDatePicker(View view) {
        Log.i(CLASS_NAME, "Working very hard to call date picker to work");
        // Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
        showDialog(999);
    }

    public void showTransferPopper(View view){
        // Create custom calendar_transfer_options_popper object
        prepareDialog(R.layout.calendar_transfer_options_popper);

        calendarTransferTV.setBackgroundResource(R.drawable.oval_buttons_selected_text_view);

        //texts
        TextView transferPopperNewTV, transferPopperQuickTV, transferPopperSchedTV;
        transferPopperNewTV = (TextView) dialog.findViewById(R.id.transferPopperNewTVId);
        transferPopperQuickTV = (TextView) dialog.findViewById(R.id.transferPopperQuickTVId);
        transferPopperSchedTV = (TextView) dialog.findViewById(R.id.transferPopperSchedTVId);

        transferPopperNewTV.setOnClickListener(linearLayoutClickListener);
        transferPopperQuickTV.setOnClickListener(linearLayoutClickListener);
        transferPopperSchedTV.setOnClickListener(linearLayoutClickListener);

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) dialog.findViewById(R.id.calendarTransfersPopperLLId), robotoCondensedLightFont);
    }

    public void showQuickTransactionPopper(){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_quick_transaction_popper);

        /*((ViewGroup)dialog.getWindow().getDecorView())
                .getChildAt(0).startAnimation(AnimationUtils.loadAnimation(
                mContext, android.R.anim.fadein));*/

        //ui
        final EditText quickTransactionAmtET;
        final Spinner quickTransactionCatSpn, quickTransactionAccSpn, quickTransactionSpntOnSpn;
        final RadioGroup quickTransactionExpIncRadioGrp;
        //RadioButton quickTransactionExpRadio, quickTransactionIncRadio;
        TextView quickTransactionDoneTV;

        quickTransactionAmtET = (EditText) dialog.findViewById(R.id.quickTransactionAmtETId);

        quickTransactionCatSpn = (Spinner) dialog.findViewById(R.id.quickTransactionCatSpnId);
        quickTransactionAccSpn = (Spinner) dialog.findViewById(R.id.quickTransactionAccSpnId);
        quickTransactionSpntOnSpn = (Spinner) dialog.findViewById(R.id.quickTransactionSpntOnSpnId);

        quickTransactionExpIncRadioGrp = (RadioGroup) dialog.findViewById(R.id.quickTransactionExpIncRadioGrpId);
        //quickTransactionExpRadio = (RadioButton) dialog.findViewById(R.id.quickTransactionExpRadioId);
        //quickTransactionIncRadio = (RadioButton) dialog.findViewById(R.id.quickTransactionIncRadioId);

        quickTransactionDoneTV = (TextView) dialog.findViewById(R.id.quickTransactionDoneTVId);

        //get Categories and Accounts from the db for this user
        List<SpinnerModel> categoryList = addUpdateTransactionsDbService.getAllCategories(loggedInUserObj.getUSER_ID());
        List<SpinnerModel> accountList = addUpdateTransactionsDbService.getAllAccounts(loggedInUserObj.getUSER_ID());
        List<SpinnerModel> spentOnList = addUpdateTransactionsDbService.getAllSpentOn(loggedInUserObj.getUSER_ID());

        //set Up categories spinner
        quickTransactionCatSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, categoryList));

        //set up accounts spinner
        quickTransactionAccSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, accountList));

        //set up pay type spinner
        quickTransactionSpntOnSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, spentOnList));

        //TODO: ideally, spent on, category & account should be the one which the user has selected in the Quick Transaction Template in the settings

        dialog.show();

        quickTransactionAmtET.requestFocus();

        quickTransactionDoneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double amount;

                if(String.valueOf(quickTransactionAmtET.getText()).isEmpty()
                        || (amount = Double.parseDouble(String.valueOf(quickTransactionAmtET.getText()))) == 0){
                    showToast("Amount cannot be Zero !");
                    return;
                }

                TransactionModel transactionModelObj = new TransactionModel();

                transactionModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());
                transactionModelObj.setTRAN_DATE(selectedDateStr.substring(0, selectedDateStr.lastIndexOf("-")));
                transactionModelObj.setTRAN_AMT(amount);
                transactionModelObj.setTRAN_NAME(Constants.DEFAULT_QUICK_TRANSACTION_NAME);

                transactionModelObj.setCAT_ID(String.valueOf(quickTransactionCatSpn.getSelectedView().getTag()));
                transactionModelObj.setACC_ID(String.valueOf(quickTransactionAccSpn.getSelectedView().getTag()));
                transactionModelObj.setSPNT_ON_ID(String.valueOf(quickTransactionSpntOnSpn.getSelectedView().getTag()));

                transactionModelObj.setTRAN_TYPE(String.valueOf(dialog.findViewById(quickTransactionExpIncRadioGrp.getCheckedRadioButtonId()).getTag()));

                Long result = addUpdateTransactionsDbService.addNewTransaction(transactionModelObj);

                if (result != -1) {
                    Log.i(CLASS_NAME, "Quick Transaction is successfully inserted into the db");
                    showToast("New Quick Transaction Created");

                    dialog.dismiss();

                    //refresh the calendar to fetch updates after quick transaction
                    String selectedDateStrArr[] = selectedDateStr.split("-");
                    setGridCellAdapterToDate(Integer.parseInt(selectedDateStrArr[0]), Integer.parseInt(selectedDateStrArr[1]), Integer.parseInt(selectedDateStrArr[2]));

                    //update summary
                    setUpSummary();

                    //update budgets
                    setUpBudgets();

                    //refresh the accounts list view to fetch updates after quick transaction
                    setUpAccounts();
                }
                else{
                    showToast("Error !! Could not create Transaction");
                    Log.e(CLASS_NAME, "ERROR !! Could not create Quick Transaction");
                }
            }
        });


        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) dialog.findViewById(R.id.quickTransactionPopperLLId), robotoCondensedLightFont);
    }

    private UsersModel getUser(){
        Map<Integer, UsersModel> userMap = authorizationDbService.getActiveUser();

        if(userMap == null || (userMap != null && userMap.isEmpty())){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            showToast("Please Login");
            return null;
        }
        else if(userMap.size() > 1){
            Intent intent = new Intent(this, JimBrokeItActivity.class);
            startActivity(intent);
            finish();
            showToast("Multiple Users are Active : Possible DB Corruption.");
        }
        else{
            return userMap.get(0);
        }

        Log.e(CLASS_NAME, "I'm not supposed to be read/print/shown..... This should have been a dead code. If you can read me, Authorization of user has failed and you should " +
                    "probably die twice by now.");
        return null;
    }

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private void killPopper(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(backButtonCounter == 0){
            backButtonCounter++;
            showToast("press again to exit");
        }
        else{
            backButtonCounter = 0;
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    private void showTransactionsPopper(ConsolidatedTransactionModel consolidatedTransactionModelObj){
        dialog = new Dialog(CalendarActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_summary_popper);

        dialog.show();

        LinearLayout summaryPopperLL = (LinearLayout) dialog.findViewById(R.id.summaryPopperLLId);
        LinearLayout summaryPoppeTitleLL = (LinearLayout) dialog.findViewById(R.id.summaryPoppeTitleLLId);
        TextView summaryPopperTransactionTitleTV = (TextView) dialog.findViewById(R.id.summaryPopperTransactionTitleTVId);
        LinearLayout summaryPopperTransferTitleLL = (LinearLayout) dialog.findViewById(R.id.summaryPopperTransferTitleLLId);
        TextView summaryPopperCountTV = (TextView) dialog.findViewById(R.id.summaryPopperCountTVId);
        TextView summaryPopperDateTV = (TextView) dialog.findViewById(R.id.summaryPopperDateTVId);
        ListView summaryPopperLV = (ListView) dialog.findViewById(R.id.summaryPopperLVId);
        LinearLayout summaryPopperTotalLL = (LinearLayout) dialog.findViewById(R.id.summaryPopperTotalLLId);
        TextView summaryPopperTotalTV = (TextView) dialog.findViewById(R.id.summaryPopperTotalTVId);

        //hide transfers and show transactions
        summaryPopperTransactionTitleTV.setVisibility(View.VISIBLE);
        summaryPopperTransferTitleLL.setVisibility(View.GONE);

        summaryPoppeTitleLL.setBackgroundResource(R.color.transactionIndicator);
        summaryPopperTransactionTitleTV.setText(consolidatedTransactionModelObj.getCategory());
        summaryPopperCountTV.setText("x " + consolidatedTransactionModelObj.getCount());

        //convert dd-MM-yyyy into dd MMM 'yy
        SimpleDateFormat wrongSdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat rightSdf = new SimpleDateFormat("d MMM ''yy");
        String rightDateStr = "ERROR";

        try{
            rightDateStr = rightSdf.format(wrongSdf.parse(consolidatedTransactionModelObj.getDate()));
        }
        catch(ParseException ex){
            Log.e(CLASS_NAME, "Date parsing is sick of your wrong date formats...correction required !!"+ex);
        }

        summaryPopperDateTV.setText(rightDateStr);
        summaryPopperTotalLL.setBackgroundResource(R.color.transactionIndicator);
        summaryPopperTotalTV.setText(String.valueOf(consolidatedTransactionModelObj.getTotal()));

        //set up list
        TransactionModel transObj = new TransactionModel();
        transObj.setTRAN_DATE(consolidatedTransactionModelObj.getDate());
        transObj.setCategory(consolidatedTransactionModelObj.getCategory());
        //set user id
        transObj.setUSER_ID(loggedInUserObj.getUSER_ID());

        List<Object> transactionsOnDayList = calendarDbService.getTransactionsOnDateAndCategory(transObj);
        SummaryPopperListAdapter summaryPopperListAdapter = new SummaryPopperListAdapter(mContext, R.layout.calendar_transactions_popper_list_view
                , transactionsOnDayList);
        summaryPopperLV.setAdapter(summaryPopperListAdapter);
        summaryPopperListAdapter.notifyDataSetChanged();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) summaryPopperLL, robotoCondensedLightFont);
    }

    private void showTransfersPopper(ConsolidatedTransferModel consolidatedTransferModelObj){
        dialog = new Dialog(CalendarActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_summary_popper);

        dialog.show();

        LinearLayout summaryPopperLL = (LinearLayout) dialog.findViewById(R.id.summaryPopperLLId);
        LinearLayout summaryPoppeTitleLL = (LinearLayout) dialog.findViewById(R.id.summaryPoppeTitleLLId);
        TextView summaryPopperTransactionTitleTV = (TextView) dialog.findViewById(R.id.summaryPopperTransactionTitleTVId);
        LinearLayout summaryPopperTransferTitleLL = (LinearLayout) dialog.findViewById(R.id.summaryPopperTransferTitleLLId);
        TextView summaryPopperFromAccTV =(TextView) dialog.findViewById(R.id.summaryPopperFromAccTVId);
        TextView summaryPopperToAccTV =(TextView) dialog.findViewById(R.id.summaryPopperToAccTVId);
        TextView summaryPopperCountTV = (TextView) dialog.findViewById(R.id.summaryPopperCountTVId);
        TextView summaryPopperDateTV = (TextView) dialog.findViewById(R.id.summaryPopperDateTVId);
        ListView summaryPopperLV = (ListView) dialog.findViewById(R.id.summaryPopperLVId);
        LinearLayout summaryPopperTotalLL = (LinearLayout) dialog.findViewById(R.id.summaryPopperTotalLLId);
        TextView summaryPopperTotalTV = (TextView) dialog.findViewById(R.id.summaryPopperTotalTVId);

        //hide transactions and show transfers
        summaryPopperTransactionTitleTV.setVisibility(View.GONE);
        summaryPopperTransferTitleLL.setVisibility(View.VISIBLE);

        summaryPoppeTitleLL.setBackgroundResource(R.color.transferIndicator);
        summaryPopperFromAccTV.setText(consolidatedTransferModelObj.getFromAccountStr());
        summaryPopperToAccTV.setText(consolidatedTransferModelObj.getToAccountStr());
        summaryPopperCountTV.setText("x " + consolidatedTransferModelObj.getCount());

        //convert dd-MM-yyyy into dd MMM 'yy
        SimpleDateFormat wrongSdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat rightSdf = new SimpleDateFormat("d MMM ''yy");
        String rightDateStr = "ERROR";

        try{
            rightDateStr = rightSdf.format(wrongSdf.parse(consolidatedTransferModelObj.getDateStr()));
        }
        catch(ParseException ex){
            Log.e(CLASS_NAME, "Date parsing is sick of your wrong date formats...correction required !!"+ex);
        }

        summaryPopperDateTV.setText(rightDateStr);
        summaryPopperTotalLL.setBackgroundResource(R.color.transferIndicator);
        summaryPopperTotalTV.setText(String.valueOf(consolidatedTransferModelObj.getAmount()));

        //set up list
        TransferModel trfrsObj = new TransferModel();
        trfrsObj.setTRNFR_DATE(consolidatedTransferModelObj.getDateStr());
        trfrsObj.setFromAccName(consolidatedTransferModelObj.getFromAccountStr());
        trfrsObj.setToAccName(consolidatedTransferModelObj.getToAccountStr());
        //set user id
        trfrsObj.setUSER_ID(loggedInUserObj.getUSER_ID());

        List<Object> transferList = calendarDbService.getTransfersOnDateAndAccounts(trfrsObj);
        SummaryPopperListAdapter summaryPopperListAdapter = new SummaryPopperListAdapter(mContext, R.layout.calendar_transactions_popper_list_view
                , transferList);
        summaryPopperLV.setAdapter(summaryPopperListAdapter);
        summaryPopperListAdapter.notifyDataSetChanged();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) summaryPopperLL, robotoCondensedLightFont);
    }

    //--------------------------------list view click listener------------------------------------------------------
    private ListView.OnItemClickListener listViewClickListener;
    {
        listViewClickListener = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(CLASS_NAME, "MASTER !! you click hath " + view.getId() + ". You shalt go to the view transaction pageth");

                Object listItemObject = view.getTag();

                if(listItemObject instanceof ConsolidatedTransactionModel){
                    showTransactionsPopper((ConsolidatedTransactionModel)listItemObject);
                }
                else if(listItemObject instanceof ConsolidatedTransferModel){
                    showTransfersPopper((ConsolidatedTransferModel) listItemObject);
                }

            }
        };
    }
    //--------------------------------list view ends----------------------------------------------------

    //--------------------------------Linear Layout click listener--------------------------------------------------
    private LinearLayout.OnClickListener linearLayoutClickListener;
    {
        linearLayoutClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(CLASS_NAME, "Linear Layout Click is working !! There's hope :) by the way you clicked:"+ v.getId());

                Intent intent = null;

                switch(v.getId()){
                    case R.id.transactionPopperNewTVId :
                        killPopper();
                        intent = toAddUpdateTransaction();
                        break;
                    case R.id.transactionPopperQuickTVId :
                        killPopper();
                        showQuickTransactionPopper();
                        break;
                    case R.id.transactionPopperSchedTVId :
                        killPopper();
                        intent = new Intent(CalendarActivity.this, AddUpdateScheduleTransactionActivity.class);
                        ScheduledTransactionModel scheduledTransactionModelObj = new ScheduledTransactionModel();
                        scheduledTransactionModelObj.setSCH_TRAN_DATE(selectedDateStr.substring(0, selectedDateStr.lastIndexOf("-")));
                        intent.putExtra("SCHEDULED_TRANSACTION_OBJ", scheduledTransactionModelObj);
                        break;
                    case R.id.transferPopperNewTVId :  intent = toAddUpdateTransfer();
                        break;
                    case R.id.transferPopperQuickTVId :
                        killPopper();
                        showQuickTransferPopper();
                        break;
                    case R.id.transferPopperSchedTVId :
                        killPopper();
                        intent = new Intent(CalendarActivity.this, AddUpdateScheduleTransferActivity.class);
                        ScheduledTransferModel scheduledTransferModelObj = new ScheduledTransferModel();
                        scheduledTransferModelObj.setSCH_TRNFR_DATE(selectedDateStr.substring(0, selectedDateStr.lastIndexOf("-")));
                        intent.putExtra("SCHEDULED_TRANSFER_OBJ", scheduledTransferModelObj);
                        break;

                    default:intent = new Intent(CalendarActivity.this, JimBrokeItActivity.class); break;
                }

                if(intent != null){
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private void showQuickTransferPopper() {
        dialog = new Dialog(CalendarActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_quick_transfer_popper);

        //ui
        final EditText quickTransferAmtET;
        final Spinner quickTransferAccFrmSpn, quickTransferAccToSpn;
        TextView quickTransferDoneTV;

        quickTransferAmtET = (EditText) dialog.findViewById(R.id.quickTransferAmtETId);

        quickTransferAccFrmSpn = (Spinner) dialog.findViewById(R.id.quickTransferAccFrmSpnId);
        quickTransferAccToSpn = (Spinner) dialog.findViewById(R.id.quickTransferAccToSpnId);

        quickTransferDoneTV = (TextView) dialog.findViewById(R.id.quickTransferDoneTVId);

        //get Accounts from the db for this user
        List<SpinnerModel> accountList = addUpdateTransactionsDbService.getAllAccounts(loggedInUserObj.getUSER_ID());

        //set up accounts spinner
        quickTransferAccFrmSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, accountList));
        quickTransferAccToSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(this, R.layout.cat_acc_spnt_spnr, accountList));

        //TODO: ideally, from and to account should be the one which the user has selected in the Quick Transfer Template in the settings

        dialog.show();

        quickTransferAmtET.requestFocus();

        quickTransferDoneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double amount;

                // if the amount is not entered or is Zero
                if(String.valueOf(quickTransferAmtET.getText()).isEmpty()
                        || (amount = Double.parseDouble(String.valueOf(quickTransferAmtET.getText()))) == 0){
                    showToast("Amount cannot be Zero !");
                    return;
                }

                TransferModel transferModelObj = new TransferModel();

                transferModelObj.setACC_ID_FRM(String.valueOf(quickTransferAccFrmSpn.getSelectedView().getTag()));
                transferModelObj.setACC_ID_TO(String.valueOf(quickTransferAccToSpn.getSelectedView().getTag()));
                transferModelObj.setTRNFR_AMT(amount);
                transferModelObj.setTRNFR_DATE(selectedDateStr.substring(0, selectedDateStr.lastIndexOf("-")));
                transferModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());

                //stop the user if the from and to accounts are same
                if(transferModelObj.getACC_ID_FRM().equalsIgnoreCase(transferModelObj.getACC_ID_TO())){
                    showToast("Cannot Transfer between same Accounts");
                    return;
                }

                Long result = addUpdateTransfersDbService.addNewTransfer(transferModelObj);

                if (result == -1) {
                    showToast("Error !! Could not create Transaction");
                    Log.e(CLASS_NAME, "ERROR !! Could not create Quick Transaction");
                }
                else if(result == 0){
                    showToast("Error !! Could not update Accounts after Transfer");
                    Log.e(CLASS_NAME, "ERROR !! Possibly transfer success but failed to update accounts");
                }
                else{
                    Log.i(CLASS_NAME, "Quick Transfer is successfully inserted into the db");
                    showToast("New Quick Transfer Created");

                    dialog.dismiss();

                    //refresh the calendar to fetch updates after quick transfer
                    String selectedDateStrArr[] = selectedDateStr.split("-");
                    setGridCellAdapterToDate(Integer.parseInt(selectedDateStrArr[0]), Integer.parseInt(selectedDateStrArr[1]), Integer.parseInt(selectedDateStrArr[2]));

                    //refresh the accounts list view to fetch updates after quick transfer
                    setUpAccounts();

                    //update summary
                    setUpSummary();
                }
            }
        });


        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) dialog.findViewById(R.id.quickTransferPopperLLId), robotoCondensedLightFont);
    }
    //--------------------------------Linear Layout ends--------------------------------------------------

    //---------------------------------------Date Picker-------------------------------------------------
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            String selectedDateStrArr[] = selectedDateStr.split("-");

            return new DatePickerDialog(this, myDateListener, Integer.parseInt(selectedDateStrArr[2]), Integer.parseInt(selectedDateStrArr[1])-1,
                        Integer.parseInt(selectedDateStrArr[0]));
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener;
    {
        myDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                Log.i(CLASS_NAME, "HA HA HA..and you thought date picker wont work for you !!! You pay it. it works for u. Simple.");
                Log.i(CLASS_NAME, "Date picker says that u selected:"+ day + "-" + month + "-" + year);

                //change jan-0 to jan-1
                month++;

                Log.i(CLASS_NAME, "Date picker date translated to be:"+ day + "-" + month + "-" + year);

                String dayStr = String.valueOf(day);
                if(day < 10){
                    dayStr = "0"+dayStr;
                }

                String monthStr = String.valueOf(month);
                if(month < 10){
                    monthStr = "0"+monthStr;
                }

                selectedDateStr = dayStr+"-"+monthStr+"-"+year+"-SELECTED_FROM_DATE_PICKER";

                //change calendar accordingly
                setGridCellAdapterToDate(day, month, year);

                //update the header
                setUpHeader();
            }
        };
    }
    //---------------------------------------Date Picker ends--------------------------------------------

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