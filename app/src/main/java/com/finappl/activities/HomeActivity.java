package com.finappl.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.adapters.CalendarSummaryViewPagerAdapter;
import com.finappl.adapters.CalendarViewPagerAdapter;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.Sqlite;
import com.finappl.fragments.TransactionDetailsFragment;
import com.finappl.fragments.TransferDetailsFragment;
import com.finappl.models.AccountMO;
import com.finappl.models.CalendarMonth;
import com.finappl.models.DayLedger;
import com.finappl.models.TransactionMO;
import com.finappl.models.TransferMO;
import com.finappl.utils.FinappleUtility;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.google.android.gms.vision.text.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

import static com.finappl.utils.Constants.FRAGMENT_TRANSACTION_DETAILS;
import static com.finappl.utils.Constants.FRAGMENT_TRANSFER_DETAILS;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF_1;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.TRANSACTION_OBJECT;
import static com.finappl.utils.Constants.TRANSFER_OBJECT;
import static com.finappl.utils.Constants.UI_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_FONT;

@SuppressLint("NewApi")
public class HomeActivity extends CommonActivity {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    //------------------------------------------------------------------
    /*components*/
    @InjectView(R.id.calendar_background_iv)
    ImageView calendar_background_iv;

    @InjectView(R.id.calendar_vp)
    ViewPager calendar_vp;

    @InjectView(R.id.calendar_summary_briefings_date_tv)
    TextView calendar_summary_briefings_date_tv;

    @InjectView(R.id.calendar_summary_briefings_accounts_summary_iv)
    ImageView calendar_summary_briefings_accounts_summary_iv;

    @InjectView(R.id.calendar_summary_briefings_accounts_summary_tv)
    TextView calendar_summary_briefings_accounts_summary_tv;

    @InjectView(R.id.calendar_summary_briefings_accounts_count_tv)
    TextView calendar_summary_briefings_accounts_count_tv;

    @InjectView(R.id.calendar_summary_vp)
    ViewPager calendar_summary_vp;
    /*components*/

    private static final int PAGE_COUNT = 11;
    private static final int PAGE_MIDDLE = PAGE_COUNT/2;

    private int selectedCalendarVPIndex = PAGE_MIDDLE;
    private LinkedList<CalendarMonth> calendarMonthsArr = new LinkedList<>();
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

    //content_home
    private String selectedDateStr = JAVA_DATE_FORMAT_SDF.format(new Date());
    private String currentFocusedMonthStr = JAVA_DATE_FORMAT_SDF_1.format(new Date());

    //Swipe Multi Views
    private ViewPager viewPager, viewPagerMonths;

    //month legend
    private Map<String, DayLedger> dayLederMap = new HashMap<>();

    //accounts
    private List<AccountMO> accountsList = new ArrayList<>();


    //db services
    private Sqlite controller = new Sqlite(mContext);
    private CalendarDbService calendarDbService = new CalendarDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //Tabs
    private List<Integer> viewPagerTabsList = new ArrayList<>();

    //progress bar
    private ProgressBar mProgressDialog;
    private ProgressDialog progressDialog;

    private static LoadUI loadUIThread;

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

        loadUIThread = new LoadUI();
        loadUIThread.execute("FIRST_LOAD");
    }

    private void setupSummary(Date date) {
        setupSummaryBriefings("SUMMARY");

        DayLedger dayLedger = null;
        String dateUiStr = null;
        Date today = null;

        try{
            today = JAVA_DATE_FORMAT_SDF.parse(JAVA_DATE_FORMAT_SDF.format(new Date()));
        }
        catch (ParseException e){
            Log.e(CLASS_NAME, "Date Parse Exception : "+today);
            return;
        }

        if(date == null){
            date = today;
        }

        String dateStr = JAVA_DATE_FORMAT_SDF.format(date);
        dayLedger = dayLederMap.get(dateStr);

        if(date.equals(today)){
            dateUiStr = "TODAY";
        }
        else{
            dateUiStr = UI_DATE_FORMAT_SDF.format(date);
        }

        calendar_summary_briefings_date_tv.setText(dateUiStr.toUpperCase());

        List<Object> summaryList = new ArrayList<>();

        //adding summary into summaryList
        summaryList.add(dayLedger);

        //adding accounts into summaryList
        summaryList.add(accountsList);

        CalendarSummaryViewPagerAdapter adapter = new CalendarSummaryViewPagerAdapter(mContext, user, summaryList);
        calendar_summary_vp.setAdapter(adapter);

        calendar_summary_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setupSummaryBriefings("SUMMARY");
                        break;
                    case 1:
                        setupSummaryBriefings("ACCOUNTS");
                        break;
                    default:
                        FinappleUtility.showSnacks(wrapper_home_cl, "Un Identified Screen", OK, Snackbar.LENGTH_INDEFINITE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /*pass month as jan-0, feb-1*/
    @OnClick(R.id.calendar_summary_briefings_date_tv)
    public void showDatepickerForSummary(View view) {
        String dateStr = String.valueOf(((TextView) view).getText());

        if("TODAY".equalsIgnoreCase(dateStr)){
            dateStr = JAVA_DATE_FORMAT_SDF.format(new Date());
        }
        else{
            try{
                dateStr = JAVA_DATE_FORMAT_SDF.format(UI_DATE_FORMAT_SDF.parse(dateStr));
            }
            catch (ParseException e){
                Log.e(CLASS_NAME, "Date Parse Exception : "+dateStr);
                return;
            }
        }

        final int month = Integer.parseInt(dateStr.split("-")[1])-1;
        final int year = Integer.parseInt(dateStr.split("-")[2]);
        final int day = Integer.parseInt(dateStr.split("-")[0]);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dateStr = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
                        try{
                            Date date = JAVA_DATE_FORMAT_SDF.parse(dateStr);
                            setupSummary(date);
                        }
                        catch (ParseException e){
                            Log.e(CLASS_NAME, "Date Parse Exception : "+dateStr);
                            return;
                        }
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    @OnClick(R.id.calendar_summary_briefings_accounts_summary_ll)
    public void onBriefingClick(View view){
        if(calendar_summary_vp.getCurrentItem() == 0){
            calendar_summary_vp.setCurrentItem(1);
            setupSummaryBriefings("ACCOUNTS");
        }
        else if(calendar_summary_vp.getCurrentItem() == 1){
            calendar_summary_vp.setCurrentItem(0);
            setupSummaryBriefings("SUMMARY");
        }
    }

    private void setupSummaryBriefings(String whichBrifings){
        switch(whichBrifings){
            case "SUMMARY" : calendar_summary_briefings_accounts_summary_iv.setBackgroundResource(R.drawable.money_bag);
                calendar_summary_briefings_accounts_count_tv.setVisibility(View.VISIBLE);
                calendar_summary_briefings_accounts_summary_tv.setText("ACCOUNTS");
                calendar_summary_briefings_accounts_count_tv.setText(String.valueOf(accountsList.size()));
                break;
            case "ACCOUNTS": calendar_summary_briefings_accounts_summary_iv.setBackgroundResource(R.drawable.summary);
                calendar_summary_briefings_accounts_count_tv.setVisibility(View.GONE);
                calendar_summary_briefings_accounts_summary_tv.setText("SUMMARY");
                break;
            default: FinappleUtility.showSnacks(getWrapper_home_cl(), "Could not identify the purpose("+whichBrifings+")", OK, Snackbar.LENGTH_INDEFINITE);
        }
    }

    public void fetchLegend() {
        //get currently shown month, so as to get ledger for month - MIDDLE_PAGE, month, month + MIDDLE_PAGE monhts
        Calendar calendar = Calendar.getInstance();

        if (calendarMonthsArr != null && !calendarMonthsArr.isEmpty() && calendarMonthsArr.get(PAGE_MIDDLE) != null) {
            calendar = calendarMonthsArr.get(PAGE_MIDDLE).getCalendar();
        }

        //get ledger
        //the range for fetching the ledger is calendarMonthsArr.length+2 because, the user can see the dates(in grey) of the last+1 & next+1 month when he swipes to left/right
        dayLederMap = calendarDbService.getMonthLegendOnDate(calendar, calendarMonthsArr.size() + 2, user.getUSER_ID());

        //fetch accounts
        accountsList = calendarDbService.getAllAccounts(user.getUSER_ID());
    }

    private void setupCalendar() {
        final CalendarViewPagerAdapter adapter = new CalendarViewPagerAdapter(mContext, calendarMonthsArr);
        calendar_vp.setAdapter(adapter);
        calendar_vp.setCurrentItem(PAGE_MIDDLE, false);
        calendar_vp.setClipToPadding(false);
        calendar_vp.setPadding(50, 0, 50, 0);
        calendar_vp.setPageMargin(40);
        calendar_vp.setOffscreenPageLimit(PAGE_COUNT);

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
                    //FinappleUtility.showSnacks(calendarPageRL, String.valueOf(selectedCalendarVPIndex), OK, Snackbar.LENGTH_SHORT);
                    int max = calendarMonthsArr.size() - 1;
                    int min = 0;

                    if (selectedCalendarVPIndex == min || selectedCalendarVPIndex == max) {
                        Calendar temp = calendarMonthsArr.get(selectedCalendarVPIndex).getCalendar();
                        showDatepicker(temp.get(Calendar.MONTH), temp.get(Calendar.YEAR));
                    }
                }
            }
        });
    }

    public void fetchUserWrapper(){
        fetchUser();
    }

    public void refreshCalendar() {
        PagerAdapter adapter = calendar_vp.getAdapter();

        calendar_vp.setCurrentItem(PAGE_MIDDLE, false);
        ((CalendarViewPagerAdapter) adapter).setModel(calendarMonthsArr);
    }

    public void updateCalendarMonths(){
        fetchLegend();

        for(int i=0; i<PAGE_COUNT; i++){
            calendarMonthsArr.set(i, new CalendarMonth(calendarMonthsArr.get(i).getOffset(), this, dayLederMap, user));
        }

        setupCalendar();
    }

    public void changeMonth(boolean previousMonth){
        if(previousMonth){
            calendar_vp.setCurrentItem(calendar_vp.getCurrentItem()-1);
        }
        else{
            calendar_vp.setCurrentItem(calendar_vp.getCurrentItem()+1);
        }
    }

    private void initCalendarMonths() {
        int offset = PAGE_MIDDLE * -1;
        for(int i=0; i<PAGE_COUNT; i++){
            calendarMonthsArr.addLast(new CalendarMonth(offset, this, dayLederMap, user));
            offset = offset+1;
        }
    }

    /*pass month as jan-0, feb-1*/
    private void showDatepicker(final int month, final int year) {
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        reInitCalendarMonths(monthOfYear, year);
                        loadUIThread.execute("REFRESH_CALENDAR");
                    }
                }, year, month, 1);
        datePickerDialog.show();

        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                reInitCalendarMonths(month, year);
                loadUIThread.execute("REFRESH_CALENDAR");
            }
        });
    }

    /*pass month as jan-0, feb-1*/
    private void reInitCalendarMonths(int month, int year) {
        Calendar userChoiceCalendar = Calendar.getInstance();
        userChoiceCalendar.set(Calendar.MONTH, month);
        userChoiceCalendar.set(Calendar.YEAR, year);

        int diffYear = userChoiceCalendar.get(Calendar.YEAR) - Calendar.getInstance().get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + userChoiceCalendar.get(Calendar.MONTH) - Calendar.getInstance().get(Calendar.MONTH);

        int offset = diffMonth - PAGE_MIDDLE;

        calendarMonthsArr.clear();
        for(int i=0; i<PAGE_COUNT; i++){
            calendarMonthsArr.addLast(new CalendarMonth(offset, this, dayLederMap, user));
            offset = offset+1;
        }
    }

    //progress bar
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressBar(HomeActivity.this);
            mProgressDialog.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShown()) {
            mProgressDialog.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void showSnacks(String message, String option, int lengthShort) {
        FinappleUtility.showSnacks(calendarPageRL, message, option, lengthShort);
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

    public class LoadUI extends AsyncTask<String, Void, Void> {
        ProgressDialog pd;
        Context context;

        @Override
        protected void onPreExecute() {
            if(pd == null){
                pd = new ProgressDialog(mContext);
            }

            pd.setIndeterminate(true);
            pd.setMessage("..loading data");
            pd.setTitle("please wait..");
            pd.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            if (params == null || params.length == 0){
                return null;
            }

            final String whatToDo = params[0];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if("FIRST_LOAD".equalsIgnoreCase(whatToDo)){
                        fetchLegend();
                        initCalendarMonths();
                        setupCalendar();
                        setupSummary(null);
                    }
                    else if ("REFRESH_CALENDAR".equalsIgnoreCase(whatToDo)){
                        refreshCalendar();
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            super.onPostExecute(result);
        }
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