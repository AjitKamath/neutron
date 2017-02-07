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
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.adapters.CalendarTabsViewPagerAdapter;
import com.finappl.adapters.CalendarViewPagerAdapter;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.Sqlite;
import com.finappl.fragments.AddActivityFragment;
import com.finappl.fragments.BudgetDetailsFragment;
import com.finappl.fragments.OptionsFragment;
import com.finappl.fragments.TransactionDetailsFragment;
import com.finappl.fragments.TransferDetailsFragment;
import com.finappl.models.AccountMO;
import com.finappl.models.BudgetMO;
import com.finappl.models.CalendarMonth;
import com.finappl.models.MonthLegend;
import com.finappl.models.TransactionMO;
import com.finappl.models.TransferMO;
import com.finappl.utils.DateTimeUtil;
import com.finappl.utils.FinappleUtility;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

import static com.finappl.utils.Constants.BUDGET_OBJECT;
import static com.finappl.utils.Constants.FRAGMENT_ADD_ACTIVITY;
import static com.finappl.utils.Constants.FRAGMENT_BUDGET_DETAILS;
import static com.finappl.utils.Constants.FRAGMENT_OPTIONS;
import static com.finappl.utils.Constants.FRAGMENT_TRANSACTION_DETAILS;
import static com.finappl.utils.Constants.FRAGMENT_TRANSFER_DETAILS;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF_1;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.MONTH_IMAGES_ARR;
import static com.finappl.utils.Constants.SELECTED_DATE;
import static com.finappl.utils.Constants.TRANSACTION_OBJECT;
import static com.finappl.utils.Constants.TRANSFER_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;

@SuppressLint("NewApi")
public class HomeActivity extends CommonActivity {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    //------------------------------------------------------------------
    /*components*/
    @InjectView(R.id.calendar_background_iv)
    ImageView calendar_background_iv;

    @InjectView(R.id.calendar_prev_month_month_tv)
    TextView calendar_prev_month_month_tv;

    @InjectView(R.id.calendar_prev_month_year_tv)
    TextView calendar_prev_month_year_tv;

    @InjectView(R.id.calendar_current_month_month_tv)
    TextView calendar_current_month_month_tv;

    @InjectView(R.id.calendar_current_month_year_tv)
    TextView calendar_current_month_year_tv;

    @InjectView(R.id.calendar_next_month_month_tv)
    TextView calendar_next_month_month_tv;

    @InjectView(R.id.calendar_next_month_year_tv)
    TextView calendar_next_month_year_tv;

    @InjectView(R.id.calendar_vp)
    ViewPager calendar_vp;
    /*components*/

    /*calendar*/
    private static final int PAGE_LEFT = 0;
    private static final int PAGE_MIDDLE = 1;
    private static final int PAGE_RIGHT = 2;

    private int selectedCalendarVPIndex = PAGE_MIDDLE;
    private CalendarMonth[] calendarMonthsArr = new CalendarMonth[3];
    /*calendar*/
    //------------------------------------------------------------------

    /*common components*/
    @InjectView(R.id.wrapper_home_cl)
    CoordinatorLayout wrapper_home_cl;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.nav_view)
    NavigationView nav_view;

    @InjectView(R.id.fabtoolbar)
    FABToolbarLayout layout;

    @InjectView(R.id.fabtoolbar_fab)
    FloatingActionButton fabtoolbar_fab;

    @InjectView(R.id.fab_transaction_ll)
    LinearLayout fab_transaction_ll;

    @InjectView(R.id.fab_transfer_ll)
    LinearLayout fab_transfer_ll;
    /*common components*/

    /*Components*/
    @Optional
    @InjectView(R.id.calendarPageRLId)
    RelativeLayout calendarPageRL;

    //@Optional
    //@InjectView(R.id.calendarSummaryTVId)
    TextView calendarSummaryTV;

    //@Optional
    //@InjectView(R.id.calendarAccountsTVId)
    TextView calendarAccountsTV;

//    @Optional
  //  @InjectView(R.id.calendarBudgetsTVId)
    TextView calendarBudgetsTV;

    //@Optional
    //@InjectView(R.id.calendarSchedulesTVId)
    TextView calendarSchedulesTV;
    /*Components*/

    //content_home
    private String selectedDateStr = JAVA_DATE_FORMAT_SDF.format(new Date());
    private String currentFocusedMonthStr = JAVA_DATE_FORMAT_SDF_1.format(new Date());

    //Swipe Multi Views
    private ViewPager viewPager, viewPagerMonths;

    //month legend
    private Map<String, MonthLegend> monthLegendMap = new HashMap<>();


    //db services
    private Sqlite controller = new Sqlite(mContext);
    private CalendarDbService calendarDbService = new CalendarDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //Tabs
    private List<Integer> viewPagerTabsList = new ArrayList<>();

    //view pager
    private CalendarTabsViewPagerAdapter calendarTabsViewPagerAdapter;


    //progress bar
    private ProgressDialog mProgressDialog;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);

        //this is to ensure the tables are in the db...actually calls the Sqlite class constructor..importance of this line is known only when the db is deleted and the app is run
        Log.i(CLASS_NAME, "Initializing the application database starts");
        controller.getWritableDatabase();
        Log.i(CLASS_NAME, "Initializing the application database ends");

        fetchLedger();
        initCalendarMonths();
        setupCalendar();
    }

    public void fetchLedger() {
        //get currently shown month, so as to get ledger for month -1, month, month +1 monhts
        Calendar calendar = Calendar.getInstance();

        if(calendarMonthsArr[PAGE_MIDDLE] != null){
            calendar = calendarMonthsArr[PAGE_MIDDLE].getAdapter().getCurrentCalendar();
        }

        //get ledger
        //the range for fetching the ledger is calendarMonthsArr.length+2 because, the user can see the dates(in grey) of the last+1 & next+1 month when he swipes to left/right
        monthLegendMap = calendarDbService.getMonthLegendOnDate(calendar, calendarMonthsArr.length+2, user.getUSER_ID());
    }

    private void setupCalendar() {
        CalendarViewPagerAdapter adapter = new CalendarViewPagerAdapter(this, calendarMonthsArr);
        calendar_vp.setAdapter(adapter);
        calendar_vp.setCurrentItem(PAGE_MIDDLE, false);
        setupMonths();

        calendar_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            boolean loadCalendar;

            @Override
            public void onPageSelected(int position) {
                selectedCalendarVPIndex = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // the offset is zero when the swiping ends
                loadCalendar = (positionOffset == 0 && positionOffsetPixels == 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if(loadCalendar){
                                // user swiped to right direction --> left page
                                if (selectedCalendarVPIndex == PAGE_LEFT) {
                                    calendarMonthsArr[2] = calendarMonthsArr[1];
                                    calendarMonthsArr[1] = calendarMonthsArr[0];
                                    fetchLedger();
                                    calendarMonthsArr[0] = new CalendarMonth(calendarMonthsArr[0].getOffset()-1, mContext, monthLegendMap, user);

                                }
                                // user swiped to left direction --> right page
                                else if (selectedCalendarVPIndex == PAGE_RIGHT) {
                                    calendarMonthsArr[0] = calendarMonthsArr[1];
                                    calendarMonthsArr[1] = calendarMonthsArr[2];
                                    fetchLedger();
                                    calendarMonthsArr[2] = new CalendarMonth(calendarMonthsArr[2].getOffset()+1, mContext, monthLegendMap, user);
                                }

                                //do something only if the month has been completely changed by the users swipe.
                                //the onPageScrollStateChanged is called even if the user swipes a little bit and then leaves
                                //in that  case nothing must be done. This can be ensured by checking for both selectedViewPagerIndex == LEFT|RIGHT
                                if(selectedCalendarVPIndex == PAGE_LEFT || selectedCalendarVPIndex == PAGE_RIGHT){
                                    calendar_vp.setCurrentItem(PAGE_MIDDLE, false);
                                    refreshCalendar();
                                }
                            }
                        }
                    },100);
                }
            }
        });
    }

    public void fetchUserWrapper(){
        fetchUser();
    }

    public void refreshCalendar(){
        CalendarViewPagerAdapter adapter = (CalendarViewPagerAdapter)calendar_vp.getAdapter();

        //set the new calendarMonthsArr
        adapter.setModel(calendarMonthsArr);
        calendar_vp.getAdapter().notifyDataSetChanged();

        setupMonths();
    }

    public void updateCalendarMonths(){
        fetchLedger();

        calendarMonthsArr[0] = new CalendarMonth(calendarMonthsArr[0].getOffset(), mContext, monthLegendMap, user);
        calendarMonthsArr[1] = new CalendarMonth(calendarMonthsArr[1].getOffset(), mContext, monthLegendMap, user);
        calendarMonthsArr[2] = new CalendarMonth(calendarMonthsArr[2].getOffset(), mContext, monthLegendMap, user);

        setupCalendar();
    }

    @OnClick({R.id.calendar_prev_month_month_tv, R.id.calendar_prev_month_year_tv, R.id.calendar_current_month_month_tv, R.id.calendar_current_month_year_tv,
            R.id.calendar_next_month_month_tv, R.id.calendar_next_month_year_tv})
    public void onMonthClick(View view){
        int id = view.getId();

        switch(id){
            case R.id.calendar_prev_month_month_tv : calendar_vp.setCurrentItem(PAGE_LEFT);
                break;
            case R.id.calendar_prev_month_year_tv : calendar_vp.setCurrentItem(PAGE_LEFT);
                break;
            case R.id.calendar_current_month_month_tv : //TODO:show date picker
                break;
            case R.id.calendar_current_month_year_tv : //TODO:show date picker
                break;
            case R.id.calendar_next_month_month_tv : calendar_vp.setCurrentItem(PAGE_RIGHT);
                break;
            case R.id.calendar_next_month_year_tv : calendar_vp.setCurrentItem(PAGE_RIGHT);
                break;
        }
    }

    private void setupMonths(){
        CalendarMonth page = calendarMonthsArr[PAGE_MIDDLE];

        Calendar calendar = Calendar.getInstance();

        /*set the prev, current & next month*/
        Formatter fmt = null;

        //prev month
        calendar.add(Calendar.MONTH, page.getOffset()-1);
        fmt = new Formatter();
        String prevMonthMonthStr = String.valueOf(fmt.format("%tb", calendar)).toUpperCase();
        String prevMonthYearStr = String.valueOf(calendar.get(Calendar.YEAR));

        //current month
        calendar.add(Calendar.MONTH, 1);
        fmt = new Formatter();
        String currentMonthMonthStr = String.valueOf(fmt.format("%tb", calendar)).toUpperCase();
        String currentMonthYearStr = String.valueOf(calendar.get(Calendar.YEAR));

        //set background image for the month
        //calendar_background_iv.setImageResource(MONTH_IMAGES_ARR[calendar.get(Calendar.MONTH)]);

        //next month
        calendar.add(Calendar.MONTH, 1);
        fmt = new Formatter();
        String nextMonthMonthStr = String.valueOf(fmt.format("%tb", calendar)).toUpperCase();
        String nextMonthYearStr = String.valueOf(calendar.get(Calendar.YEAR));

        fmt.close();

        calendar_prev_month_month_tv.setText(prevMonthMonthStr);
        calendar_prev_month_year_tv.setText(prevMonthYearStr);

        calendar_current_month_month_tv.setText(currentMonthMonthStr);
        calendar_current_month_year_tv.setText(currentMonthYearStr);

        calendar_next_month_month_tv.setText(nextMonthMonthStr);
        calendar_next_month_year_tv.setText(nextMonthYearStr);
        /*set the prev, current & next month*/
    }

    public void changeMonth(boolean previousMonth){
        if(previousMonth){
            calendar_prev_month_month_tv.performClick();
        }
        else{
            calendar_next_month_month_tv.performClick();
        }
    }

    private void initCalendarMonths() {
        calendarMonthsArr[0] = new CalendarMonth(-1, mContext, monthLegendMap, user);
        calendarMonthsArr[1] = new CalendarMonth(0, mContext, monthLegendMap, user);
        calendarMonthsArr[2] = new CalendarMonth(1, mContext, monthLegendMap, user);
    }

    //progress bar
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(HomeActivity.this);
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

    private void showTransactionDetails(TransactionMO transaction){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_TRANSACTION_DETAILS);

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(TRANSACTION_OBJECT, transaction);
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        TransactionDetailsFragment fragment = new TransactionDetailsFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
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
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        TransferDetailsFragment fragment = new TransferDetailsFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
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
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        BudgetDetailsFragment fragment = new BudgetDetailsFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_BUDGET_DETAILS);
    }

    public void setUpTabs() {
        calendarTabsViewPagerAdapter = new CalendarTabsViewPagerAdapter(mContext, viewPagerTabsList, DateTimeUtil.cleanUpDate(selectedDateStr), user, monthLegendMap,
                        new HomeActivity.ListViewItemClickListener() {
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

    private void selectTab(int position){/*
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
        }*/
    }

    public void onTabSelect(View view) {/*
        Log.i(CLASS_NAME, "Son....i created you. The tab implementation...although sucks, a very own of my creation. And you work !! selected: " + view.getId());

        *//*if(checkAndCollapseFab()){
            return;
        }*//*

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
    */}

    private void deselectAllTabs() {/*
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
        calendarSchedulesTV.setTag("");*/
    }

    private void setUpServices() {
        //notify the notification service by calling the receiver
        Intent notifIntent = new Intent();
        notifIntent.setAction("ACTIVITY_ACTION");
        sendBroadcast(notifIntent);
    }

    public void showOptions(){
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        OptionsFragment fragment = new OptionsFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);

        transaction.add(fragment, FRAGMENT_OPTIONS);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void addActivity(){
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_DATE, selectedDateStr);
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        AddActivityFragment fragment = new AddActivityFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);

        transaction.add(fragment, FRAGMENT_ADD_ACTIVITY);
        transaction.addToBackStack(null);
        transaction.commit();
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

    /*@Override
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
    }*/

    /*@OnClick({R.id.calendar_current_month_month_tv, R.id.calendar_current_month_year_tv})
    public void showDatePicker() {
        String tempStr = JAVA_DATE_FORMAT_SDF.format(DateTimeUtil.cleanUpDate(selectedDateStr));
        String tempStrArr[] = tempStr.split("-");

        DatePickerFragment date = new DatePickerFragment();
        *//**
         * Set Up Current Date Into dialog
         *//*
        Bundle args = new Bundle();
        args.putInt("year", Integer.parseInt(tempStrArr[2]));
        args.putInt("month", Integer.parseInt(tempStrArr[1])-1);
        args.putInt("calendar_day__", Integer.parseInt(tempStrArr[0]));
        date.setArguments(args);
        *//**
         * Set Call back to capture selected date
         *//*
        date.setCallBack(datePickerListener);
        date.show(getFragmentManager(), "Date Picker");
    }*/

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

            //no need to set up content_home if there's no change in the selected date
            if(!tempStr.equalsIgnoreCase(selectedDateStr)){
                selectedDateStr = tempStr;

                //change content_home accordingly
                //setupCalendar();
            }
        }
    };

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

    @Override
    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected DrawerLayout getDrawer_layout() {
        return drawer_layout;
    }

    @Override
    protected NavigationView getNav_view() {
        return nav_view;
    }

    @Override
    protected FABToolbarLayout getLayout() {
        return layout;
    }

    @Override
    protected FloatingActionButton getFabtoolbar_fab() {
        return fabtoolbar_fab;
    }

    @Override
    protected LinearLayout getFab_transaction_ll() {
        return fab_transaction_ll;
    }

    @Override
    protected LinearLayout getFab_transfer_ll() {
        return fab_transfer_ll;
    }

    @Override
    protected CoordinatorLayout getWrapper_home_cl() {
        return wrapper_home_cl;
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