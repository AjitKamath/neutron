package com.finappl.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
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
import android.widget.ViewFlipper;

import com.finappl.R;
import com.finappl.adapters.AddUpdateTransactionSpinnerAdapter;
import com.finappl.adapters.CalendarAccountsAdapter;
import com.finappl.adapters.CalendarActionsViewPagerAdapter;
import com.finappl.adapters.CalendarBudgetsAdapter;
import com.finappl.adapters.CalendarGridAdpter;
import com.finappl.adapters.CalendarSchedulesSectionListAdapter;
import com.finappl.adapters.ConsolidatedSummarySectionAdapter;
import com.finappl.adapters.CalendarTransactionsOptionsPopperViewPagerAdapter;
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
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.models.UsersModel;
import com.finappl.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint("NewApi")
public class CalendarActivity extends AppCompatActivity {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    //header
    private TextView yearTV, /*calendarFinappleNameTV,*/ calendarMonthTV;

    //calendar
    private CalendarGridAdpter adapter;// adapter instance
    private GridView calendarView;
    private Calendar _calendar;
    private String selectedDateStr = sdf.format(new Date());

    //Swipe Multi Views
    private ViewPager viewPager;

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
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return;
        }

        //initialize calendar
        initializeCalendar();

        String selectedDateStrArr[] = selectedDateStr.split("-");
        if(selectedDateStrArr[0].length() == 1){
            selectedDateStrArr[0] = "0"+ selectedDateStrArr[0];
        }
        if(selectedDateStrArr[1].length() == 1){
            selectedDateStrArr[1] = "0"+ selectedDateStrArr[1];
        }

        //set current date
        selectedDateStr = selectedDateStrArr[0]+"-"+selectedDateStrArr[1]+"-"+selectedDateStrArr[2];

        initUIComponents();

        //set up calendar
        setGridCellAdapterToDate(Integer.parseInt(selectedDateStrArr[0]), Integer.parseInt(selectedDateStrArr[1]), Integer.parseInt(selectedDateStrArr[2]));

        //call notification service
        //TODO: this might not be required in production
        setUpServices();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.calendarPageRLId), robotoCondensedLightFont);
    }

    private void setUpActions() {
        List<Integer> list = new ArrayList<>();
        list.add(R.layout.calendar_tabs_flipper);
        list.add(R.layout.calendar_actions_flipper);

        CalendarActionsViewPagerAdapter calendarActionsViewPagerAdapter =
                new CalendarActionsViewPagerAdapter(mContext, list, monthLegendMap, selectedDateStr, loggedInUserObj);
        viewPager.setAdapter(calendarActionsViewPagerAdapter);

        final ImageView calendarMultiViewTabsActiveIndIV = (ImageView)this.findViewById(R.id.calendarMultiViewTabsActiveIndIVId);
        final ImageView calendarMultiViewActionsActiveIndIV = (ImageView)this.findViewById(R.id.calendarMultiViewActionsActiveIndIVId);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    calendarMultiViewTabsActiveIndIV.setBackgroundResource(R.drawable.ring_darkblue_in_darkblue);
                    calendarMultiViewActionsActiveIndIV.setBackgroundResource(R.drawable.ring_white_in_blue);

                }
                else if(position == 1){
                    calendarMultiViewActionsActiveIndIV.setBackgroundResource(R.drawable.ring_darkblue_in_darkblue);
                    calendarMultiViewTabsActiveIndIV.setBackgroundResource(R.drawable.ring_white_in_blue);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setUpServices() {
        //notify the notification service by calling the receiver
        Intent notifIntent = new Intent();
        notifIntent.setAction("ACTIVITY_ACTION");
        sendBroadcast(notifIntent);
    }

    private void getMonthLegend() {
        monthLegendMap = calendarDbService.getMonthLegendOnDate(selectedDateStr, loggedInUserObj.getUSER_ID());
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

    private void initializeCalendar(){
        //this method runs on app start up, so setting the calendar to current actual state
        _calendar = Calendar.getInstance(Locale.getDefault());
    }

    private void initUIComponents() {
        //get UI components

        //header
        yearTV = (TextView) this.findViewById(R.id.calendarFullYearId);
        calendarMonthTV = (TextView) this.findViewById(R.id.calendarMonthId);

        //calendar
        calendarView = (GridView) this.findViewById(R.id.calendarPageCalendarGVId);

        //view pager
        viewPager = (ViewPager) this.findViewById(R.id.calendarActionsTabsVPId);
    }

    private void setUpHeader() {
        //convert the selectedDateStr which's in raw format dd-(MM-1)-yyyy-TYPE to dd MMM yy and WEEK
        String tempSelectedDateStrArr[] = selectedDateStr.split("-");
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(tempSelectedDateStrArr[2]), Integer.parseInt(tempSelectedDateStrArr[1]) - 1, Integer.parseInt(tempSelectedDateStrArr[0]));

        String yearStr = String.valueOf(cal.get(cal.YEAR));
        int month = cal.get(cal.MONTH);

        //set year
        yearTV.setText(yearStr);
        calendarMonthTV.setText(Constants.MONTHS_ARRAY[month]);
    }

    //pass month as jan-1 feb-2
    //TODO: Convert this into ViewPager
    private void setGridCellAdapterToDate(int day, int month, int year) {
        _calendar.set(year, month-1, day);
        month = _calendar.get(Calendar.MONTH);
        year = _calendar.get(Calendar.YEAR);
        day = _calendar.get(Calendar.DAY_OF_MONTH);

        //get this months legend
        getMonthLegend();

        setUpHeader();

        setUpActions();

        adapter = new CalendarGridAdpter(this, monthLegendMap, day, month+1, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);

        String tempMonthStr = String.valueOf(month+1);
        String tempDayStr= String.valueOf(day);
        if(month+1<10){
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

                        setUpActions();
                    } else {
                        //if the activity transaction indicator or transfer indicator both are invisible...then do not proceed to ViewTransaction page..because there's no point
                        if (calendarGridDayContentGL.findViewById(R.id.calendarCellTransactionIndicatorTVId).getVisibility() == View.GONE
                                && calendarGridDayContentGL.findViewById(R.id.calendarCellTransferIndicatorTVId).getVisibility() == View.GONE) {
                            showToast("No Activities to Show");
                            return;
                        }
                    }
                }
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
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setDimAmount(0.8f);
        wlp.gravity = Gravity.CENTER;
        //wlp.y = 80;
        window.setAttributes(wlp);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    public void showTransactionPopper(View view){
        // Create custom calendar_transaction_options_popper object
        prepareDialog(R.layout.calendar_transaction_options_popper);

        final ViewPager viewPager = (ViewPager) dialog.findViewById(R.id.viewpager);

        List<Integer> list = new ArrayList<>();
        list.add(R.layout.calendar_transaction_options_popper_info);
        list.add(R.layout.blue);
        list.add(R.layout.orange);

        final CalendarTransactionsOptionsPopperViewPagerAdapter calendarTransactionsOptionsPopperViewPagerAdapter = new CalendarTransactionsOptionsPopperViewPagerAdapter(mContext, list);
        viewPager.setAdapter(calendarTransactionsOptionsPopperViewPagerAdapter);

        //texts
        LinearLayout transactionPopperNewLV, transactionPopperQuickLV, transactionPopperSchedLV;
        transactionPopperNewLV = (LinearLayout) dialog.findViewById(R.id.transactionPopperNewLVId);
        transactionPopperQuickLV = (LinearLayout) dialog.findViewById(R.id.transactionPopperQuickLVId);
        transactionPopperSchedLV = (LinearLayout) dialog.findViewById(R.id.transactionPopperSchedLVId);

        transactionPopperNewLV.setOnClickListener(linearLayoutClickListener);
        transactionPopperQuickLV.setOnClickListener(linearLayoutClickListener);
        transactionPopperSchedLV.setOnClickListener(linearLayoutClickListener);

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

        //texts
        LinearLayout transferPopperNewLV, transferPopperQuickLV, transferPopperSchedLV;
        transferPopperNewLV = (LinearLayout) dialog.findViewById(R.id.transferPopperNewLVId);
        transferPopperQuickLV = (LinearLayout) dialog.findViewById(R.id.transferPopperQuickLVId);
        transferPopperSchedLV = (LinearLayout) dialog.findViewById(R.id.transferPopperSchedLVId);

        transferPopperNewLV.setOnClickListener(linearLayoutClickListener);
        transferPopperQuickLV.setOnClickListener(linearLayoutClickListener);
        transferPopperSchedLV.setOnClickListener(linearLayoutClickListener);

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) dialog.findViewById(R.id.calendarTransfersPopperLLId), robotoCondensedLightFont);
    }

    public void showQuickTransactionPopper(){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_quick_transaction_popper);

        //ui
        final EditText quickTransactionAmtET;
        final Spinner quickTransactionCatSpn, quickTransactionAccSpn, quickTransactionSpntOnSpn;
        final RadioGroup quickTransactionExpIncRadioGrp;
        //RadioButton quickTransactionExpRadio, quickTransactionIncRadio;
        TextView quickTransactionDoneTV;

        quickTransactionAmtET = (EditText) dialog.findViewById(R.id.quickTransactionAmtETId);
        quickTransactionAmtET.requestFocus();

        quickTransactionCatSpn = (Spinner) dialog.findViewById(R.id.quickTransactionCatSpnId);
        quickTransactionAccSpn = (Spinner) dialog.findViewById(R.id.quickTransactionAccSpnId);
        quickTransactionSpntOnSpn = (Spinner) dialog.findViewById(R.id.quickTransactionSpntOnSpnId);

        quickTransactionExpIncRadioGrp = (RadioGroup) dialog.findViewById(R.id.quickTransactionExpIncRadioGrpId);

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

    private void showAccountPopper(final AccountsModel accountsModelObj){
        dialog = new Dialog(CalendarActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_account_popper);

        dialog.show();

        //commons
        TextView accountPopperTitleTV = (TextView) dialog.findViewById(R.id.accountPopperTitleTVId);
        ImageView accountPopperEditIV = (ImageView) dialog.findViewById(R.id.accountPopperEditIVId);
        TextView accountPopperBalanceTV = (TextView) dialog.findViewById(R.id.accountPopperBalanceTVId);
        View accountPopperDividerView = (View) dialog.findViewById(R.id.accountPopperDividerViewId);
        View accountPopperDividerTwoView = (View) dialog.findViewById(R.id.accountPopperDividerTwoViewId);

        //Last Transactions
        LinearLayout accountPopperLastTransactionLL = (LinearLayout) dialog.findViewById(R.id.accountPopperLastTransactionLLId);
        TextView accountPopperTransactionCategoryTV = (TextView) dialog.findViewById(R.id.accountPopperTransactionCategoryTVId);
        TextView accountPopperTransactionAmtTV = (TextView) dialog.findViewById(R.id.accountPopperTransactionAmtTVId);
        TextView accountPopperTransactionDateTV = (TextView) dialog.findViewById(R.id.accountPopperTransactionDateTVId);

        //Last Transfers
        LinearLayout accountPopperLastTransferLL = (LinearLayout) dialog.findViewById(R.id.accountPopperLastTransferLLId);
        TextView accountPopperTransferFromTV = (TextView) dialog.findViewById(R.id.accountPopperTransferFromTVId);
        TextView accountPopperTransferToTV = (TextView) dialog.findViewById(R.id.accountPopperTransferToTVId);
        TextView accountPopperTransferAmtTV = (TextView) dialog.findViewById(R.id.accountPopperTransferAmtTVId);
        TextView accountPopperTransferDateTV = (TextView) dialog.findViewById(R.id.accountPopperTransferDateTVId);

        accountPopperTitleTV.setText(accountsModelObj.getACC_NAME());
        accountPopperBalanceTV.setText(String.valueOf(accountsModelObj.getACC_TOTAL()));

        if(accountsModelObj.getACC_TOTAL() <= 0){
            accountPopperBalanceTV.setTextColor(getResources().getColor(R.color.finappleCurrencyNegColor));
        }
        else{
            accountPopperBalanceTV.setTextColor(getResources().getColor(R.color.finappleCurrencyPosColor));
        }

        //set Last Transaction
        TransactionModel transactionModelObj = calendarDbService.getLastTransactionOnAccountId(accountsModelObj.getACC_ID());

        //convert dd-MM-yyyy into dd MMM 'yy
        SimpleDateFormat wrongSdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat rightSdf = new SimpleDateFormat("d MMM ''yy");

        if(transactionModelObj == null){
            accountPopperLastTransactionLL.setVisibility(View.GONE);
        }
        else{
            accountPopperTransactionCategoryTV.setText(transactionModelObj.getCategory());

            if("EXPENSE".equalsIgnoreCase(transactionModelObj.getTRAN_TYPE())){
                accountPopperTransactionAmtTV.setText("-"+transactionModelObj.getTRAN_AMT());
                accountPopperTransactionAmtTV.setTextColor(getResources().getColor(R.color.finappleCurrencyNegColor));
            }
            else{
                accountPopperTransactionAmtTV.setText(String.valueOf(transactionModelObj.getTRAN_AMT()));
                accountPopperTransactionAmtTV.setTextColor(getResources().getColor(R.color.finappleCurrencyPosColor));
            }

            try{
                accountPopperTransactionDateTV.setText(rightSdf.format(wrongSdf.parse(transactionModelObj.getTRAN_DATE())));
            }
            catch(ParseException ex){
                Log.e(CLASS_NAME, "Date parsing is sick of your wrong date formats...correction required !!"+ex);
            }
        }

        //Set Last Transfer
        TransferModel transferModelObj = calendarDbService.getLastTransferOnAccountId(accountsModelObj.getACC_ID());

        if(transferModelObj == null){
            accountPopperLastTransferLL.setVisibility(View.GONE);
        }
        else{
            accountPopperTransferFromTV.setText(transferModelObj.getFromAccName());
            accountPopperTransferToTV.setText(transferModelObj.getToAccName());
            accountPopperTransferAmtTV.setText(String.valueOf(transferModelObj.getTRNFR_AMT()));

            try{
                accountPopperTransferDateTV.setText(rightSdf.format(wrongSdf.parse(transferModelObj.getTRNFR_DATE())));
            }
            catch(ParseException ex){
                Log.e(CLASS_NAME, "Date parsing is sick of your wrong date formats...correction required !!"+ex);
            }
        }

        if(transactionModelObj == null && transferModelObj == null){
            accountPopperDividerView.setVisibility(View.GONE);
            accountPopperDividerTwoView.setVisibility(View.GONE);
        }
        else if(transactionModelObj == null || transferModelObj == null){
            accountPopperDividerTwoView.setVisibility(View.GONE);
        }

        accountPopperEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), AddUpdateAccountActivity.class);
                intent.putExtra("ACCOUNT_OBJ", accountsModelObj);
                startActivity(intent);
                finish();
            }
        });

        if("Y".equalsIgnoreCase(accountsModelObj.getACC_IS_DEFAULT())){
            accountPopperEditIV.setVisibility(View.GONE);
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) dialog.findViewById(R.id.accountPopperLLId), robotoCondensedLightFont);
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
                else if(listItemObject instanceof AccountsModel){
                    showAccountPopper((AccountsModel) listItemObject);
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
                    case R.id.transactionPopperNewLVId :
                        killPopper();
                        intent = toAddUpdateTransaction();
                        break;
                    case R.id.transactionPopperQuickLVId :
                        killPopper();
                        showQuickTransactionPopper();
                        break;
                    case R.id.transactionPopperSchedLVId :
                        killPopper();
                        intent = new Intent(CalendarActivity.this, AddUpdateScheduleTransactionActivity.class);
                        ScheduledTransactionModel scheduledTransactionModelObj = new ScheduledTransactionModel();
                        scheduledTransactionModelObj.setSCH_TRAN_DATE(selectedDateStr.substring(0, selectedDateStr.lastIndexOf("-")));
                        intent.putExtra("SCHEDULED_TRANSACTION_OBJ", scheduledTransactionModelObj);
                        break;
                    case R.id.transferPopperNewLVId :  intent = toAddUpdateTransfer();
                        break;
                    case R.id.transferPopperQuickLVId :
                        killPopper();
                        showQuickTransferPopper();
                        break;
                    case R.id.transferPopperSchedLVId :
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
        quickTransferAmtET.requestFocus();

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

                    //refresh the calendar to fetch updates after quick transaction
                    String selectedDateStrArr[] = selectedDateStr.split("-");
                    setGridCellAdapterToDate(Integer.parseInt(selectedDateStrArr[0]), Integer.parseInt(selectedDateStrArr[1]), Integer.parseInt(selectedDateStrArr[2]));
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