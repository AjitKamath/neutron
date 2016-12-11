package com.finappl.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.adapters.CalendarMonthsViewPagerAdapter;
import com.finappl.adapters.CalendarTabsViewPagerAdapter;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.Sqlite;
import com.finappl.fragments.AddActivityFragment;
import com.finappl.fragments.BudgetDetailsFragment;
import com.finappl.fragments.DatePickerFragment;
import com.finappl.fragments.LoginFragment;
import com.finappl.fragments.OptionsFragment;
import com.finappl.fragments.TransactionDetailsFragment;
import com.finappl.fragments.TransferDetailsFragment;
import com.finappl.models.AccountMO;
import com.finappl.models.BudgetMO;
import com.finappl.models.MonthLegend;
import com.finappl.models.TransactionMO;
import com.finappl.models.TransferMO;
import com.finappl.models.UserMO;
import com.finappl.utils.Constants;
import com.finappl.utils.DateTimeUtil;
import com.finappl.utils.FinappleUtility;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.BUDGET_OBJECT;
import static com.finappl.utils.Constants.FRAGMENT_ADD_ACTIVITY;
import static com.finappl.utils.Constants.FRAGMENT_BUDGET_DETAILS;
import static com.finappl.utils.Constants.FRAGMENT_LOGIN;
import static com.finappl.utils.Constants.FRAGMENT_OPTIONS;
import static com.finappl.utils.Constants.FRAGMENT_TRANSACTION_DETAILS;
import static com.finappl.utils.Constants.FRAGMENT_TRANSFER_DETAILS;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF_1;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.SELECTED_DATE;
import static com.finappl.utils.Constants.TRANSACTION_OBJECT;
import static com.finappl.utils.Constants.TRANSFER_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;

@SuppressLint("NewApi")
public class CalendarActivity extends AppCompatActivity {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    /*Components*/
    @InjectView(R.id.calendarPageRLId)
    RelativeLayout calendarPageRL;

    @InjectView(R.id.calendarMonthTVId)
    TextView calendarMonthTV;

    @InjectView(R.id.calendarYearTVId)
    TextView calendarYearTV;

    @InjectView(R.id.calendarSummaryTVId)
    TextView calendarSummaryTV;

    @InjectView(R.id.calendarAccountsTVId)
    TextView calendarAccountsTV;

    @InjectView(R.id.calendarBudgetsTVId)
    TextView calendarBudgetsTV;

    @InjectView(R.id.calendarSchedulesTVId)
    TextView calendarSchedulesTV;
    /*Components*/

    //calendar
    private String selectedDateStr = JAVA_DATE_FORMAT_SDF.format(new Date());
    private String currentFocusedMonthStr = JAVA_DATE_FORMAT_SDF_1.format(new Date());

    //Swipe Multi Views
    private ViewPager viewPager, viewPagerMonths;

    //month legend
    private Map<String, MonthLegend> monthLegendMap = new HashMap<String, MonthLegend>();

    //back default_button counter
    private Integer backButtonCounter = 0;

    //db services
    private Sqlite controller = new Sqlite(mContext);
    private CalendarDbService calendarDbService = new CalendarDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UserMO loggedInUserObj;

    //Tabs
    private List<Integer> viewPagerTabsList = new ArrayList<>();
    private List<TextView> tabHeadersList = new ArrayList<>();

    //view pager
    private CalendarTabsViewPagerAdapter calendarTabsViewPagerAdapter;
    private CalendarMonthsViewPagerAdapter calendarMonthsViewPagerAdapter;

    private int mSelectedPageIndex = 999;
    private int oldScreenIndex;

    private boolean ignore = false;

    //progress bar
    private ProgressDialog mProgressDialog;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        ButterKnife.inject(this);

        //this is to ensure the tables are in the db...actually calls the Sqlite class constructor..importance of this line is known only when the db is deleted and the app is run
        Log.i(CLASS_NAME, "Initializing the application database starts");
        controller.getWritableDatabase();
        Log.i(CLASS_NAME, "Initializing the application database ends");

        initActivity();

        //set font for all the text view
        setFont(calendarPageRL);

        if(loggedInUserObj == null){
            forceLogin();
            return;
        }
    }

    public void getLoggedInUser() {
        FirebaseAuth user = FirebaseAuth.getInstance();
        if(user != null && user.getCurrentUser() != null && user.getCurrentUser().getUid() != null){
            loggedInUserObj = authorizationDbService.getActiveUser(user.getCurrentUser().getUid());

            //set if the user has verified his email
            //loggedInUserObj.setEmailVerified(user.getCurrentUser().isEmailVerified());
        }
    }

    //progress bar
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(CalendarActivity.this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void initActivity(){
        getLoggedInUser();

        //Check if Obsolete since the app is going to be single screen app
        if(getIntent().getExtras() != null && getIntent().getExtras().get("SELECTED_DATE") != null){
            selectedDateStr = String.valueOf(getIntent().getExtras().get("SELECTED_DATE"));
        }

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
        currentFocusedMonthStr = selectedDateStrArr[1]+"-"+selectedDateStrArr[2];
        setUpCalendar();

        //call notification service
        //TODO: this might not be required in production
        setUpServices();
    }

    public void forceLogin() {
        // close existing dialog fragments
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_LOGIN);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        LoginFragment fragment = new LoginFragment();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_LOGIN);

        loggedInUserObj = null;
        monthLegendMap = null;

        setUpCalendar();
        setUpTabs();
    }

    private void fetchMonthLegend(){
        if(loggedInUserObj  == null){
            return;
        }

        monthLegendMap = calendarDbService.getMonthLegendOnDate(currentFocusedMonthStr, loggedInUserObj.getUSER_ID());
    }

    private void showTransactionDetails(TransactionMO transaction){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_TRANSACTION_DETAILS);

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(TRANSACTION_OBJECT, transaction);
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        TransactionDetailsFragment fragment = new TransactionDetailsFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_TRANSACTION_DETAILS);
    }

    private void showTransferDetails(TransferMO transfer){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_TRANSFER_DETAILS);

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(TRANSFER_OBJECT, transfer);
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        TransferDetailsFragment fragment = new TransferDetailsFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_TRANSFER_DETAILS);
    }

    private void showBudgetDetails(BudgetMO budget){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_BUDGET_DETAILS);

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(BUDGET_OBJECT, budget);
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        BudgetDetailsFragment fragment = new BudgetDetailsFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_BUDGET_DETAILS);
    }

    public void setUpTabs() {
        calendarTabsViewPagerAdapter = new CalendarTabsViewPagerAdapter(mContext, viewPagerTabsList, DateTimeUtil.cleanUpDate(selectedDateStr), loggedInUserObj, monthLegendMap,
                        new ListViewItemClickListener() {
                            @Override
                            public void onListItemClick(Object listItemObject) {
                                if(listItemObject instanceof TransactionMO){
                                    showTransactionDetails((TransactionMO) listItemObject);
                                }
                                else if(listItemObject instanceof TransferMO){
                                    showTransferDetails((TransferMO) listItemObject);
                                }
                                else if(listItemObject instanceof BudgetMO){
                                    showBudgetDetails((BudgetMO) listItemObject);
                                }
                                else if(listItemObject instanceof AccountMO){
                                    Log.i(CLASS_NAME, "Clicked on an Account. All good. No worries");
                                }
                                else{
                                    Log.e(CLASS_NAME, "Disaster !! The list item is of type("+listItemObject.getClass()+") for which there's no code to handle");
                                    showToast("Sorry ! Something is wrong");
                                    return;
                                }
                            }
                        });

        int activePageIndex = 0;
        if(viewPager != null && viewPager.getAdapter() != null){
            activePageIndex = viewPager.getCurrentItem();
        }

        viewPager.setAdapter(calendarTabsViewPagerAdapter);
        viewPager.setCurrentItem(activePageIndex);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (viewPagerTabsList == null || (viewPagerTabsList != null && viewPagerTabsList.isEmpty())) {
                    Log.e(CLASS_NAME, "ERRROR !! expecting viewPagerList to be non empty");
                    return;
                }

                if (calendarTabsViewPagerAdapter == null) {
                    Log.e(CLASS_NAME, "ERRROR !! expecting calendarTabsViewPagerAdapter to be non null");
                    return;
                }

                selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                /*if (checkAndCollapseFab()) {
                    return;
                }*/
            }
        });
    }

    private void setUpCalendar() {
        if(viewPagerMonths == null){
            return;
        }

        setUpHeader();
        fetchMonthLegend();
        setUpTabs();
        calendarMonthsViewPagerAdapter = new CalendarMonthsViewPagerAdapter(mContext, DateTimeUtil.cleanUpDate(selectedDateStr), currentFocusedMonthStr, loggedInUserObj, monthLegendMap,
                new GridViewItemClickListener() {
                    @Override
                    public void onGridViewItemClick(Object position) {
                        SimpleDateFormat sdf = new SimpleDateFormat(JAVA_DATE_FORMAT);
                        selectedDateStr = sdf.format(calendarMonthsViewPagerAdapter.selectedDate);
                        setUpTabs();

                        if(calendarMonthsViewPagerAdapter.doMonthChange) {
                            String selectedDateStrArr[] = selectedDateStr.split("-");
                            currentFocusedMonthStr = selectedDateStrArr[1]+"-"+selectedDateStrArr[2];
                            setUpCalendar();
                        }
                    }
                });
        viewPagerMonths.setAdapter(calendarMonthsViewPagerAdapter);
        viewPagerMonths.setCurrentItem(calendarMonthsViewPagerAdapter.getCount() / 2, false);
        calendarMonthsViewPagerAdapter.notifyDataSetChanged();
        oldScreenIndex = viewPagerMonths.getCurrentItem();

        /*Dynamically set height of the month view pager since view pager WRAP_CONTENT is useless*/
        ViewGroup.LayoutParams params = viewPagerMonths.getLayoutParams();
        params.height = getResources().getInteger(R.integer.calendar_grid_cell_size)*6;
        viewPagerMonths.setLayoutParams(params);

        viewPagerMonths.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //for all the left/right swipes in the limited loaded months
                if(!ignore) {
                    currentFocusedMonthStr = calendarMonthsViewPagerAdapter.currentFocusedMonthStr;
                    String dateMonthStrArr[] = currentFocusedMonthStr.split("-");

                    SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");

                    Calendar cal = Calendar.getInstance(Locale.getDefault());
                    cal.set(Integer.parseInt(dateMonthStrArr[1]), Integer.parseInt(dateMonthStrArr[0]) - 1, 1);
                    cal.add(Calendar.MONTH, position-oldScreenIndex);

                    int month = cal.get(Calendar.MONTH) + 1;
                    int year = cal.get(Calendar.YEAR);

                    currentFocusedMonthStr = "";
                    if (month < 10) {
                        currentFocusedMonthStr = "0";
                    }
                    currentFocusedMonthStr += month + "-" + year;
                    setUpHeader();
                    calendarMonthsViewPagerAdapter.currentFocusedMonthStr = currentFocusedMonthStr;
                }
                else{
                    ignore = false;
                }

                mSelectedPageIndex = position;
                oldScreenIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    currentFocusedMonthStr = calendarMonthsViewPagerAdapter.currentFocusedMonthStr;
                    String dateMonthStrArr[] = currentFocusedMonthStr.split("-");

                    SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
                    String monthAndYear = sdf.format(new Date());

                    Calendar cal = Calendar.getInstance(Locale.getDefault());
                    cal.set(Integer.parseInt(dateMonthStrArr[1]), Integer.parseInt(dateMonthStrArr[0]) - 1, 1);

                    boolean refreshCalendar = false;
                    //if the user has reached to the beginning of the limited loaded months
                    if (mSelectedPageIndex == 0) {
                        cal.add(Calendar.MONTH, -1);
                        refreshCalendar = true;
                    }
                    //if the user has reached to the end of the limited loaded months
                    else if (mSelectedPageIndex == calendarMonthsViewPagerAdapter.getCount() - 1) {
                        cal.add(Calendar.MONTH, +1);
                        refreshCalendar = true;
                    }

                    if(refreshCalendar){
                        int month = cal.get(Calendar.MONTH) + 1;
                        int year = cal.get(Calendar.YEAR);

                        currentFocusedMonthStr = "";
                        if (month < 10) {
                            currentFocusedMonthStr = "0";
                        }
                        currentFocusedMonthStr += month + "-" + year;
                        ignore = true;
                        setUpCalendar();
                        return;
                    }
                }
            }
        });
    }

    private void selectTab(int position){
        TextView calendarSummaryTV = (TextView) this.findViewById(R.id.calendarSummaryTVId);
        TextView calendarAccountsTV = (TextView) this.findViewById(R.id.calendarAccountsTVId);
        TextView calendarBudgetsTV = (TextView) this.findViewById(R.id.calendarBudgetsTVId);
        TextView calendarSchedulesTV = (TextView) this.findViewById(R.id.calendarSchedulesTVId);

        switch (position) {
            case 0:
                if (!"SELECTED".equalsIgnoreCase(calendarSummaryTV.getTag().toString())) {
                    deselectAllTabs();
                    calendarSummaryTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarSummaryTV.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    calendarSummaryTV.setTag("SELECTED");
                }
                break;

            case 1:
                if (!"SELECTED".equalsIgnoreCase(calendarAccountsTV.getTag().toString())) {
                    deselectAllTabs();
                    calendarAccountsTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarAccountsTV.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    calendarAccountsTV.setTag("SELECTED");
                }
                break;

            case 2:
                if (!"SELECTED".equalsIgnoreCase(calendarBudgetsTV.getTag().toString())) {
                    deselectAllTabs();
                    calendarBudgetsTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarBudgetsTV.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    calendarBudgetsTV.setTag("SELECTED");
                }
                break;

            case 3:
                if (!"SELECTED".equalsIgnoreCase(calendarSchedulesTV.getTag().toString())) {
                    deselectAllTabs();
                    calendarSchedulesTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarSchedulesTV.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    calendarSchedulesTV.setTag("SELECTED");
                }
                break;

            default:
                showToast("Tab Error !!");
        }
    }

    public void onTabSelect(View view) {
        Log.i(CLASS_NAME, "Son....i created you. The tab implementation...although sucks, a very own of my creation. And you work !! selected: " + view.getId());

        /*if(checkAndCollapseFab()){
            return;
        }*/

        TextView calendarSummaryTV = (TextView) this.findViewById(R.id.calendarSummaryTVId);
        TextView calendarAccountsTV = (TextView) this.findViewById(R.id.calendarAccountsTVId);
        TextView calendarBudgetsTV = (TextView) this.findViewById(R.id.calendarBudgetsTVId);
        TextView calendarSchedulesTV = (TextView) this.findViewById(R.id.calendarSchedulesTVId);

        switch (view.getId()) {
            case R.id.calendarSummaryTVId:
                if (!"SELECTED".equalsIgnoreCase(calendarSummaryTV.getTag().toString())) {
                    deselectAllTabs();
                    calendarSummaryTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarSummaryTV.setTextColor(calendarSummaryTV.getResources().getColor(R.color.white));
                    calendarSummaryTV.setTag("SELECTED");
                    viewPager.setCurrentItem(0);
                }
                break;

            case R.id.calendarAccountsTVId:
                if (!"SELECTED".equalsIgnoreCase(calendarAccountsTV.getTag().toString())) {
                    deselectAllTabs();
                    calendarAccountsTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarAccountsTV.setTextColor(calendarSummaryTV.getResources().getColor(R.color.white));
                    calendarAccountsTV.setTag("SELECTED");
                    viewPager.setCurrentItem(1);
                }
                break;

            case R.id.calendarBudgetsTVId:
                if (!"SELECTED".equalsIgnoreCase(calendarBudgetsTV.getTag().toString())) {
                    deselectAllTabs();
                    calendarBudgetsTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarBudgetsTV.setTextColor(calendarSummaryTV.getResources().getColor(R.color.white));
                    calendarBudgetsTV.setTag("SELECTED");
                    viewPager.setCurrentItem(2);
                }
                break;

            case R.id.calendarSchedulesTVId:
                if (!"SELECTED".equalsIgnoreCase(calendarSchedulesTV.getTag().toString())) {
                    deselectAllTabs();
                    calendarSchedulesTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarSchedulesTV.setTextColor(calendarSchedulesTV.getResources().getColor(R.color.white));
                    calendarSchedulesTV.setTag("SELECTED");
                    viewPager.setCurrentItem(3);
                }
                break;

            default:
                showToast("Tab Error !!");
        }
    }

    private void deselectAllTabs() {
        TextView calendarSummaryTV = (TextView) this.findViewById(R.id.calendarSummaryTVId);
        TextView calendarAccountsTV = (TextView) this.findViewById(R.id.calendarAccountsTVId);
        TextView calendarBudgetsTV = (TextView) this.findViewById(R.id.calendarBudgetsTVId);
        TextView calendarSchedulesTV = (TextView) this.findViewById(R.id.calendarSchedulesTVId);

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

    private void setUpServices() {
        //notify the notification service by calling the receiver
        Intent notifIntent = new Intent();
        notifIntent.setAction("ACTIVITY_ACTION");
        sendBroadcast(notifIntent);
    }

    @OnClick(R.id.calendarOptionsIVId)
    public void showOptions(){
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        OptionsFragment fragment = new OptionsFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);

        transaction.add(fragment, FRAGMENT_OPTIONS);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initUIComponents() {
        //view pager
        viewPager = (ViewPager) this.findViewById(R.id.calendarTabsVPId);
        viewPagerMonths = (ViewPager) this.findViewById(R.id.calendarDatesVPId);

        viewPagerTabsList.clear();
        viewPagerTabsList.add(R.layout.calendar_tab_activities);
        viewPagerTabsList.add(R.layout.calendar_tab_accounts);
        viewPagerTabsList.add(R.layout.calendar_tab_budgets);
        viewPagerTabsList.add(R.layout.calendar_tab_schedules);

        tabHeadersList.clear();
        tabHeadersList.add(calendarSummaryTV);
        tabHeadersList.add(calendarAccountsTV);
        tabHeadersList.add(calendarBudgetsTV);
        tabHeadersList.add(calendarSchedulesTV);
    }

    @OnClick(R.id.calendarHeaderAddIVId)
    public void addActivity(){
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_DATE, selectedDateStr);
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        AddActivityFragment fragment = new AddActivityFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);

        transaction.add(fragment, FRAGMENT_ADD_ACTIVITY);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setUpHeader() {
        String tempSelectedDateStrArr[] = currentFocusedMonthStr.split("-");

        calendarMonthTV.setText(Constants.MONTHS_ARRAY[Integer.parseInt(tempSelectedDateStrArr[0])-1]);
        calendarYearTV.setText(tempSelectedDateStrArr[1]);
    }

    protected void showToast(String string){
        if(string == null || (string != null && string.trim().isEmpty())){
            return;
        }


        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    public void showSnacks(String messageStr, final String doWhatStr, int duration){
        FinappleUtility.showSnacks(calendarPageRL, messageStr, doWhatStr, duration);
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

    @OnClick({R.id.calendarMonthTVId, R.id.calendarYearTVId})
    public void showDatePicker() {
        String tempStr = JAVA_DATE_FORMAT_SDF.format(DateTimeUtil.cleanUpDate(selectedDateStr));
        String tempStrArr[] = tempStr.split("-");

        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();
        args.putInt("year", Integer.parseInt(tempStrArr[2]));
        args.putInt("month", Integer.parseInt(tempStrArr[1])-1);
        args.putInt("calendar_day", Integer.parseInt(tempStrArr[0]));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(datePickerListener);
        date.show(getFragmentManager(), "Date Picker");
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
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

            String tempStr = dayStr+"-"+monthStr+"-"+year;

            //no need to set up calendar if there's no change in the selected date
            if(!tempStr.equalsIgnoreCase(selectedDateStr)){
                selectedDateStr = tempStr;

                //change calendar accordingly
                setUpCalendar();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //activityResumed();

        getLoggedInUser();

        //TODO: enable this oce the Firebase supports email verification check
        /*if(!loggedInUserObj.isEmailVerified()){
            showSnacks(EMAIL_NOT_VERIFIED, VERIFY_EMAIL);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //abstracts
    public interface ListViewItemClickListener {
        void onListItemClick(Object position);
    }

    public interface GridViewItemClickListener {
        void onGridViewItemClick(Object position);
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group) {
        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), UI_FONT);

        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(robotoCondensedLightFont);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v);
            }
        }
    }
}