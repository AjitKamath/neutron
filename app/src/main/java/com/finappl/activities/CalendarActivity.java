package com.finappl.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.adapters.AddUpdateTransactionSpinnerAdapter;
import com.finappl.adapters.CalendarMonthsViewPagerAdapter;
import com.finappl.adapters.CalendarTabsViewPagerAdapter;
import com.finappl.adapters.CalendarTransactionsOptionsPopperViewPagerAdapter;
import com.finappl.adapters.SummaryPopperListAdapter;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.Sqlite_NEW;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.fragments.LoginFragment;
import com.finappl.fragments.TransactionFragment;
import com.finappl.fragments.TransferFragment;
import com.finappl.models.AccountsMO;
import com.finappl.models.BudgetModel;
import com.finappl.models.CategoryMO;
import com.finappl.models.ConsolidatedTransactionModel;
import com.finappl.models.ConsolidatedTransferModel;
import com.finappl.models.MonthLegend;
import com.finappl.models.ScheduledTransactionModel;
import com.finappl.models.ScheduledTransferModel;
import com.finappl.models.SpinnerModel;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.models.UserMO;
import com.finappl.utils.Constants;
import com.finappl.utils.DateTimeUtil;
import com.finappl.utils.FinappleUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.finappl.utils.Constants.FRAGMENT_LOGIN;
import static com.finappl.utils.Constants.FRAGMENT_TRANSACTION;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT;
import static com.finappl.utils.Constants.TRANSACTION_OBJECT;
import static com.finappl.utils.Constants.UI_DATE_FORMAT;
import static com.finappl.utils.Constants.UI_DATE_TIME_FORMAT;

@SuppressLint("NewApi")
public class CalendarActivity extends LockerActivity implements TransactionFragment.DialogResultListener, TransferFragment.DialogResultListener {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    private SimpleDateFormat sdf = new SimpleDateFormat(JAVA_DATE_FORMAT);
    private SimpleDateFormat sdf1 = new SimpleDateFormat("MM-yyyy");

    //header
    private TextView yearTV,  calendarMonthTV, calendarTodayTV;
    private LinearLayout calendarHeaderMonthYearLL;

    //calendar
    private GridView calendarView;
    //private Calendar _calendar;
    private String selectedDateStr = sdf.format(new Date());
    private String currentFocusedMonthStr = sdf1.format(new Date());

    //Swipe Multi Views
    private ViewPager viewPager, viewPagerMonths;

    //month legend
    private Map<String, MonthLegend> monthLegendMap = new HashMap<String, MonthLegend>();

    //back default_button counter
    private Integer backButtonCounter = 0;

    //db services
    private Sqlite_NEW controller = new Sqlite_NEW(mContext);
    private CalendarDbService calendarDbService = new CalendarDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);
    private TransactionsDbService addUpdateTransactionsDbService = new TransactionsDbService(mContext);
    //private AddUpdateTransfersDbService addUpdateTransfersDbService = new AddUpdateTransfersDbService(mContext);

    //User
    private UserMO loggedInUserObj;

    //FAB starts
    private ImageButton calendarFabIB;

    private LinearLayout calendarFabTransactionLL;
    private LinearLayout calendarFabTransferLL;
    private LinearLayout calendarFabReportLL;

    private boolean expanded = false;

    private float calendarFabTransactionLLOffset;
    private float calendarFabTransferLLOffset;
    private float calendarFabReportLLOffset;
    //FAB ends

    //popup
    private Dialog dialog, anotherDialog, messageDialog, anotherMessageDialog;

    //view pager
    private List<Integer> viewPagerTabsList, viewPagerMonthsList;
    private CalendarTabsViewPagerAdapter calendarTabsViewPagerAdapter;
    private CalendarMonthsViewPagerAdapter calendarMonthsViewPagerAdapter;

    //----------------------------------
    private static final int PAGE_LEFT = 0;
    private static final int PAGE_MIDDLE = 1;
    private static final int PAGE_RIGHT = 2;

    private LayoutInflater mInflater;
    private int mSelectedPageIndex = 999;
    private int oldScreenIndex;

    private boolean ignore = false;

    private Bundle savedInstanceState;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        //this is to ensure the tables are in the db...actually calls the Sqlite class constructor..importance of this line is known only when the db is deleted and the app is run
        Log.i(CLASS_NAME, "Initializing the application database starts");
        controller.getWritableDatabase();
        Log.i(CLASS_NAME, "Initializing the application database ends");

        //get the Active user
        loggedInUserObj = authorizationDbService.getActiveUser(FinappleUtility.getInstance().getActiveUserId(mContext));
        if(loggedInUserObj == null){
            forceLogin();
            return;
        }

        setContentView(R.layout.calendar);

        initActivity();

        //set font for all the text view
        setFont((ViewGroup) this.findViewById(R.id.calendarPageRLId));
    }

    private void initActivity(){
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

        //set up FAB
        setUpFab();

        //call notification service
        //TODO: this might not be required in production
        setUpServices();

        //Set up poppers if we navigated to this page from sme other activities.
        setUpActionPoppers();

        //close fab if its open
        checkAndCollapseFab();
    }

    private void forceLogin() {
        // close existing dialog fragments
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_LOGIN);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        LoginFragment fragment = new LoginFragment();
        fragment.show(manager, FRAGMENT_LOGIN);
    }

    private void setUpActionPoppers() {
        if(getIntent().getExtras() == null){
            Log.i(CLASS_NAME, "No Extras in the intent.. So no intended Action Poppers");
            return;
        }
        else if(getIntent().getExtras().get("CALENDAR_ACTIVITY_ACTION") == null){
            Log.i(CLASS_NAME, "Could not Find CALENDAR_ACTIVITY_ACTIONS in the intent... There seems to be no intended Action Poppers.");
            return;
        }

        Object actionObject = getIntent().getExtras().get("CALENDAR_ACTIVITY_ACTION");

        //if action was a new Transaction added/old transaction being updated
        if(actionObject instanceof TransactionModel){
            showTransactionAddedUpdatedPopper((TransactionModel) actionObject);
        }
        //if action was a new Transfer added/old Transfer being updated
        else if(actionObject instanceof TransferModel){
            showTransferAddedUpdatedPopper((TransferModel) actionObject);
        }


    }

    private void showTransferAddedUpdatedPopper(final TransferModel transferModelObj){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_transfer_added_popper);
        dialog.setOnDismissListener(dialogsDismissListener);

        dialog.show();

        TextView calendarTransferAddedPopperTitleTV = (TextView) dialog.findViewById(R.id.calendarTransferAddedPopperTitleTVId);
        TextView calendarTransferAddedPopperAmountTV = (TextView) dialog.findViewById(R.id.calendarTransferAddedPopperAmountTVId);
        TextView calendarTransferAddedPopperFromAccountTV = (TextView) dialog.findViewById(R.id.calendarTransferAddedPopperFromAccountTVId);
        TextView calendarTransferAddedPopperToAccountTV = (TextView) dialog.findViewById(R.id.calendarTransferAddedPopperToAccountTVId);
        ImageView calendarTransferAddedPopperDeleteIV = (ImageView) dialog.findViewById(R.id.calendarTransferAddedPopperDeleteIVId);
        ImageView calendarTransferAddedPopperEditIV = (ImageView) dialog.findViewById(R.id.calendarTransferAddedPopperEditIVId);
        LinearLayout calendarTransferAddedPopperOkLL = (LinearLayout) dialog.findViewById(R.id.calendarTransferAddedPopperOkLLId);
        LinearLayout calendarTransferAddedPopperAddLL = (LinearLayout) dialog.findViewById(R.id.calendarTransferAddedPopperAddLLId);

        //if the transfer was updated..the action object would contain TRNFR_ID
        if(transferModelObj.getTRNFR_ID() != null){
            calendarTransferAddedPopperTitleTV.setText("Transfer has been Updated !");
        }
        //if the transfer was newly created
        else{
            calendarTransferAddedPopperTitleTV.setText("Transfer is Complete !");
        }

        //amount
        //TODO: Approximatization is needed
        calendarTransferAddedPopperAmountTV.setText(String.valueOf(transferModelObj.getTRNFR_AMT()));

        //from & to account
        calendarTransferAddedPopperFromAccountTV.setText(transferModelObj.getFromAccName());
        calendarTransferAddedPopperToAccountTV.setText(transferModelObj.getToAccName());

        calendarTransferAddedPopperDeleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showMessagePopper(transferModelObj);
            }
        });

        calendarTransferAddedPopperEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //navigateTo(AddUpdateTransferActivity.class, "TRANSFER_OBJ", transferModelObj);
            }
        });

        calendarTransferAddedPopperOkLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        calendarTransferAddedPopperAddLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(toAddUpdateTransfer());
                finish();
            }
        });

        //set font for all the text view
        setFont((ViewGroup)dialog.findViewById(R.id.calendarTransferAddedPopperLLId));
    }

    private void showTransactionAddedUpdatedPopper(final TransactionModel transactionModelObj){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_transaction_added_popper);
        dialog.setOnDismissListener(dialogsDismissListener);

        dialog.show();

        TextView calendarTransactionAddedPopperTitleTV = (TextView) dialog.findViewById(R.id.calendarTransactionAddedPopperTitleTVId);
        TextView calendarTransactionAddedPopperAmountTV = (TextView) dialog.findViewById(R.id.calendarTransactionAddedPopperAmountTVId);
        TextView calendarTransactionAddedPopperCategoryTV = (TextView) dialog.findViewById(R.id.calendarTransactionAddedPopperCategoryTVId);
        ImageView calendarTransactionAddedPopperDeleteIV = (ImageView) dialog.findViewById(R.id.calendarTransactionAddedPopperDeleteIVId);
        ImageView calendarTransactionAddedPopperEditIV = (ImageView) dialog.findViewById(R.id.calendarTransactionAddedPopperEditIVId);
        LinearLayout calendarTransactionAddedPopperOkLL = (LinearLayout) dialog.findViewById(R.id.calendarTransactionAddedPopperOkLLId);
        LinearLayout calendarTransactionAddedPopperAddLL = (LinearLayout) dialog.findViewById(R.id.calendarTransactionAddedPopperAddLLId);

        //if the transaction was updated..the action object would contain TRAN_ID
        if(transactionModelObj.getTRAN_ID() != null){
            calendarTransactionAddedPopperTitleTV.setText("Transaction Updated !");
        }
        //if the transaction was newly created
        else{
            calendarTransactionAddedPopperTitleTV.setText("New Transaction Added !");
        }

        //amount
        //TODO: Approximatization is needed
        if("EXPENSE".equalsIgnoreCase(transactionModelObj.getTRAN_TYPE())){
            calendarTransactionAddedPopperAmountTV.setText("-"+transactionModelObj.getTRAN_AMT());
            calendarTransactionAddedPopperAmountTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyNegColor));
        }
        else{
            calendarTransactionAddedPopperAmountTV.setText(String.valueOf(transactionModelObj.getTRAN_AMT()));
            calendarTransactionAddedPopperAmountTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyPosColor));
        }

        //category
        calendarTransactionAddedPopperCategoryTV.setText(transactionModelObj.getCategory());

        calendarTransactionAddedPopperDeleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showMessagePopper(transactionModelObj);
            }
        });

        calendarTransactionAddedPopperEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //navigateTo(AddUpdateTransactionActivity.class, "TRANSACTION_OBJ", transactionModelObj);
            }
        });

        calendarTransactionAddedPopperOkLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        calendarTransactionAddedPopperAddLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(toAddUpdateTransaction());
                finish();
            }
        });

        //set font for all the text view
        setFont((ViewGroup)dialog.findViewById(R.id.calendarTransactionAddedPopperLLId));
    }

    private void setUpFab() {
        final ViewGroup calendarFabRL = (ViewGroup) findViewById(R.id.calendarFabRLId);
        calendarFabIB = (ImageButton) findViewById(R.id.calendarFabIBId);
        calendarFabTransactionLL = (LinearLayout) findViewById(R.id.calendarFabTransactionLLId);
        calendarFabTransferLL = (LinearLayout) findViewById(R.id.calendarFabTransferLLId);
        calendarFabReportLL = (LinearLayout) findViewById(R.id.calendarFabReportLLId);

        //hide action buttons by default
        calendarFabTransactionLL.setVisibility(View.INVISIBLE);
        calendarFabTransferLL.setVisibility(View.INVISIBLE);
        calendarFabReportLL.setVisibility(View.INVISIBLE);

        calendarFabIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded = !expanded;
                if (expanded) {
                    expandFab();
                } else {
                    collapseFab();
                }
            }
        });
        calendarFabRL.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                calendarFabRL.getViewTreeObserver().removeOnPreDrawListener(this);
                calendarFabTransactionLLOffset = calendarFabIB.getY() - calendarFabTransactionLL.getY();
                calendarFabTransactionLL.setTranslationY(calendarFabTransactionLLOffset);
                calendarFabTransferLLOffset = calendarFabIB.getY() - calendarFabTransferLL.getY();
                calendarFabTransferLL.setTranslationY(calendarFabTransferLLOffset);
                calendarFabReportLLOffset = calendarFabIB.getY() - calendarFabReportLL.getY();
                calendarFabReportLL.setTranslationY(calendarFabReportLLOffset);
                return true;
            }
        });
    }

    //---------------------------------------------------------------
    private void collapseFab() {
        int currentRotation = 0;
        final RotateAnimation rotateAnim = new RotateAnimation(currentRotation, currentRotation + 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.setDuration(250);
        rotateAnim.setFillEnabled(true);
        rotateAnim.setFillAfter(true);

        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                RelativeLayout test = (RelativeLayout) findViewById(R.id.calendarPageLLId);
                test.animate().alpha(1.0f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((LinearLayout)findViewById(R.id.calendarFabTransferLLId)).setVisibility(View.INVISIBLE);
                ((LinearLayout)findViewById(R.id.calendarFabTransactionLLId)).setVisibility(View.INVISIBLE);
                ((LinearLayout)findViewById(R.id.calendarFabReportLLId)).setVisibility(View.INVISIBLE);
                calendarFabIB.setImageResource(R.drawable.plus_white_small);

                //enableDisableChildViews(test, true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        calendarFabIB.startAnimation(rotateAnim);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createCollapseAnimator(calendarFabTransactionLL, calendarFabTransactionLLOffset),
                createSlideRightAnimator(calendarFabTransactionLL, calendarFabTransactionLLOffset),
                createCollapseAnimator(calendarFabTransferLL, calendarFabTransferLLOffset),
                createSlideRightAnimator(calendarFabTransferLL, calendarFabTransferLLOffset),
                createCollapseAnimator(calendarFabReportLL, calendarFabReportLLOffset),
                createSlideRightAnimator(calendarFabReportLL, calendarFabReportLLOffset));
        animatorSet.start();
        animateFab();
    }

    //this returns true in case the FAB was opened
    public boolean checkAndCollapseFab(){
        if (expanded) {
            expanded = !expanded;
            collapseFab();
            return true;
        }
        return false;
    }

    private void expandFab() {
        int currentRotation = 0;
        final RotateAnimation rotateAnim = new RotateAnimation(currentRotation, currentRotation + 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.setDuration(250);
        rotateAnim.setFillEnabled(true);
        rotateAnim.setFillAfter(true);

        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                RelativeLayout test = (RelativeLayout) findViewById(R.id.calendarPageLLId);
                test.animate().alpha(0.1f);

                /*RelativeLayout test2 = (RelativeLayout) findViewById(R.id.fab_container);
                test2.animate().alpha(1.0f);*/
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((LinearLayout)findViewById(R.id.calendarFabTransferLLId)).setVisibility(View.VISIBLE);
                ((LinearLayout)findViewById(R.id.calendarFabTransactionLLId)).setVisibility(View.VISIBLE);
                ((LinearLayout)findViewById(R.id.calendarFabReportLLId)).setVisibility(View.VISIBLE);
                calendarFabIB.setImageResource(R.drawable.cross_white_small);
                calendarFabIB.setClickable(true);

                RelativeLayout test = (RelativeLayout) findViewById(R.id.calendarPageLLId);
                //enableDisableChildViews(test, false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        calendarFabIB.startAnimation(rotateAnim);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createExpandAnimator(calendarFabTransactionLL, calendarFabTransactionLLOffset),
                createSlideLeftAnimator(calendarFabTransactionLL, calendarFabTransactionLLOffset),
                createExpandAnimator(calendarFabTransferLL, calendarFabTransferLLOffset),
                createSlideLeftAnimator(calendarFabTransferLL, calendarFabTransferLLOffset),
                createExpandAnimator(calendarFabReportLL, calendarFabReportLLOffset),
                createSlideLeftAnimator(calendarFabReportLL, calendarFabReportLLOffset));
        animatorSet.start();
        animateFab();
    }

    private static final String TRANSLATION_Y = "translationY";
    private static final String TRANSLATION_X = "translationX";

    private Animator createCollapseAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private Animator createExpandAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private Animator createSlideRightAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_X, 0, offset-100)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private Animator createSlideLeftAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_X, offset-100, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private void animateFab() {
        Drawable drawable = calendarFabIB.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }
    //-----------------------------------------------------------------------------

    private void showTransactionsPopper(ConsolidatedTransactionModel consolidatedTransactionModelObj){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_summary_popper);
        dialog.setOnDismissListener(dialogsDismissListener);

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

        //convert date into dd MMM 'yy
        SimpleDateFormat rightSdf = new SimpleDateFormat(UI_DATE_FORMAT);
        String rightDateStr = rightSdf.format(consolidatedTransactionModelObj.getDate());

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
        SummaryPopperListAdapter summaryPopperListAdapter = new SummaryPopperListAdapter(mContext, R.layout.calendar_summary_popper_list_view
                , transactionsOnDayList);
        summaryPopperLV.setAdapter(summaryPopperListAdapter);
        summaryPopperListAdapter.notifyDataSetChanged();
        summaryPopperLV.setOnItemClickListener(listViewClickListener);

        //set font for all the text view
        setFont(summaryPopperLL);
    }

    public void showTransferPopper(View view){
        checkAndCollapseFab();

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
        setFont((ViewGroup) dialog.findViewById(R.id.calendarTransfersPopperLLId));
    }

    private void prepareDialog(int layout){
        dialog = new Dialog(mContext, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setOnDismissListener(dialogsDismissListener);

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
        checkAndCollapseFab();

        showAddUpdateTransactionPopper(null);








        /*
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
        setFont((ViewGroup) dialog.findViewById(R.id.calendarTransactionsPopperLLId));*/
    }

    public void showQuickTransactionPopper(){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_quick_transaction_popper);
        dialog.setOnDismissListener(dialogsDismissListener);

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
        quickTransactionCatSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(mContext, R.layout.cat_acc_spnt_spnr, categoryList));

        //set up accounts spinner
        quickTransactionAccSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(mContext, R.layout.cat_acc_spnt_spnr, accountList));

        //set up pay type spinner
        quickTransactionSpntOnSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(mContext, R.layout.cat_acc_spnt_spnr, spentOnList));

        //TODO: ideally, spent on, category & account should be the one which the user has selected in the Quick Transaction Template in the settings

        dialog.show();

        quickTransactionAmtET.requestFocus();

        quickTransactionDoneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double amount;

                if (String.valueOf(quickTransactionAmtET.getText()).isEmpty()
                        || (amount = Double.parseDouble(String.valueOf(quickTransactionAmtET.getText()))) == 0) {
                    showToast("Amount cannot be Zero !");
                    return;
                }

                TransactionModel transactionModelObj = new TransactionModel();

                transactionModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());
                transactionModelObj.setTRAN_DATE(DateTimeUtil.cleanUpDate(selectedDateStr));
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

                    refreshActivity();
                } else {
                    showToast("Error !! Could not create Transaction");
                    Log.e(CLASS_NAME, "ERROR !! Could not create Quick Transaction");
                }
            }
        });

        //set font for all the text view
        setFont((ViewGroup) dialog.findViewById(R.id.quickTransactionPopperLLId));
    }

    private void showScheduledTransactionDetailsPopper(final ScheduledTransactionModel scheduledTransactionModelObj) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_scheduled_transaction_popper);
        dialog.setOnDismissListener(dialogsDismissListener);

        dialog.show();

        //Buttons
        ImageView schedTransactionPopperEditIV, schedTransactionPopperDeleteIV;
        schedTransactionPopperEditIV = (ImageView) dialog.findViewById(R.id.schedTransactionPopperEditIVId);
        schedTransactionPopperDeleteIV = (ImageView) dialog.findViewById(R.id.schedTransactionPopperDeleteIVId);

        schedTransactionPopperDeleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScheduleMessagePopper(scheduledTransactionModelObj);
            }
        });
        schedTransactionPopperEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog != null) {
                    dialog.dismiss();
                }
                toEditScheduledTransaction(scheduledTransactionModelObj);
            }
        });

        //texts
        TextView schedTransactionPopperDateTV, schedTransactionPopperAmountTV, schedTransactionPopperFreqTV, schedTransactionPopperAutoTV, schedTransactionPopperNameTV
                ,schedTransactionPopperCategoryTV, schedTransactionPopperAccountTV, schedTransactionPopperSpentOnTV, schedTransactionPopperNotesTV, schedTransactionPopperCreateDateTV
                ,schedTransactionPopperUpdateDateTV, schedTransactionPopperStatusTV;
        LinearLayout schedTransactionPopperUpdateDateLV, schedTransactionPopperStatusLV;
        ImageView schedTransactionPopperStatusIV;

        schedTransactionPopperDateTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperDateTVId);
        schedTransactionPopperAmountTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperAmountTVId);
        schedTransactionPopperFreqTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperFreqTVId);
        schedTransactionPopperAutoTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperAutoTVId);
        schedTransactionPopperNameTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperNameTVId);
        schedTransactionPopperCategoryTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperCategoryTVId);
        schedTransactionPopperAccountTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperAccountTVId);
        schedTransactionPopperSpentOnTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperSpentOnTVId);
        schedTransactionPopperNotesTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperNotesTVId);
        schedTransactionPopperCreateDateTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperCreateDateTVId);
        schedTransactionPopperUpdateDateTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperUpdateDateTVId);
        schedTransactionPopperUpdateDateLV = (LinearLayout) dialog.findViewById(R.id.schedTransactionPopperUpdateDateLVId);
        schedTransactionPopperStatusLV = (LinearLayout) dialog.findViewById(R.id.schedTransactionPopperStatusLVId);
        schedTransactionPopperStatusTV = (TextView) dialog.findViewById(R.id.schedTransactionPopperStatusTVId);
        schedTransactionPopperStatusIV = (ImageView) dialog.findViewById(R.id.schedTransactionPopperStatusIVId);

        SimpleDateFormat sdfWrong = new SimpleDateFormat(JAVA_DATE_FORMAT);
        SimpleDateFormat sdfRight = new SimpleDateFormat(UI_DATE_FORMAT);

        try{
            schedTransactionPopperDateTV.setText(sdfRight.format(sdfWrong.parse(selectedDateStr)));
            schedTransactionPopperCreateDateTV.setText(sdfRight.format(scheduledTransactionModelObj.getCREAT_DTM()));

            if(scheduledTransactionModelObj.getMOD_DTM() == null){
                schedTransactionPopperUpdateDateLV.setVisibility(View.GONE);
            }
            else{
                schedTransactionPopperUpdateDateTV.setText(sdfRight.format(scheduledTransactionModelObj.getMOD_DTM()));
            }
        }
        catch(ParseException pe){
            Log.e(CLASS_NAME, "Error ! "+pe);
            return;
        }

        //TODO: Approximatization required

        if("EXPENSE".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_TYPE())){
            schedTransactionPopperAmountTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyNegColor));
            schedTransactionPopperAmountTV.setText("-"+scheduledTransactionModelObj.getSCH_TRAN_AMT());
        }
        else if("INCOME".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_TYPE())){
            schedTransactionPopperAmountTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyPosColor));
            schedTransactionPopperAmountTV.setText(String.valueOf(scheduledTransactionModelObj.getSCH_TRAN_AMT()));
        }
        else{
            Log.e(CLASS_NAME, "Stupidity Error !!");
            return;
        }

        if(scheduledTransactionModelObj.getStatus() != null && !scheduledTransactionModelObj.getStatus().isEmpty()){
            schedTransactionPopperStatusLV.setVisibility(View.VISIBLE);
            schedTransactionPopperEditIV.setVisibility(View.GONE);

            if("CANCEL".equalsIgnoreCase(scheduledTransactionModelObj.getStatus())){
                schedTransactionPopperStatusIV.setBackgroundResource(R.drawable.cross_white_small);
                schedTransactionPopperStatusTV.setTextColor(getResources().getColor(R.color.red));
                schedTransactionPopperStatusTV.setText("Cancelled");
            }
            else if("AUTO_ADD".equalsIgnoreCase(scheduledTransactionModelObj.getStatus()) || "ADD".equalsIgnoreCase(scheduledTransactionModelObj.getStatus())){
                schedTransactionPopperStatusIV.setBackgroundResource(R.drawable.tick_white_small);
                schedTransactionPopperStatusTV.setTextColor(getResources().getColor(R.color.finappleTheme));
                schedTransactionPopperStatusTV.setText("Added");
            }
            else if("SCHEDULED".equalsIgnoreCase(scheduledTransactionModelObj.getStatus())){
                schedTransactionPopperStatusIV.setBackgroundResource(R.drawable.scheduled_transaction_white);
                schedTransactionPopperStatusTV.setTextColor(getResources().getColor(R.color.orrange));
                schedTransactionPopperStatusTV.setText("Scheduled");

                //enable editing when its scheduled
                schedTransactionPopperEditIV.setVisibility(View.VISIBLE);
            }
        }
        else{
            Log.e(CLASS_NAME, "Expecting Scheduled Transaction status to be AUTO_ADD/ADD/CANCEL/SCHEDULED..but found none.");
            schedTransactionPopperStatusIV.setBackgroundResource(R.drawable.info_white);
            schedTransactionPopperStatusTV.setTextColor(getResources().getColor(R.color.red));
            schedTransactionPopperStatusTV.setText("Unavailable");
        }

        schedTransactionPopperFreqTV.setText("REPEATS " + scheduledTransactionModelObj.getSCH_TRAN_FREQ());

        if("AUTO_ADD".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_AUTO())){
            schedTransactionPopperAutoTV.setText("AUTO ADD");
        }
        else if("NOTIFY_ADD".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_AUTO())){
            schedTransactionPopperAutoTV.setText("NOTIFY & ADD");
        }
        else{
            Log.e(CLASS_NAME, "Stupidity Error !!");
            return;
        }

        schedTransactionPopperNameTV.setText(scheduledTransactionModelObj.getSCH_TRAN_NAME());
        schedTransactionPopperCategoryTV.setText(scheduledTransactionModelObj.getCategoryNameStr());
        schedTransactionPopperAccountTV.setText(scheduledTransactionModelObj.getAccountNameStr());
        schedTransactionPopperSpentOnTV.setText(scheduledTransactionModelObj.getSpentOnNameStr());
        schedTransactionPopperNotesTV.setText(scheduledTransactionModelObj.getSCH_TRAN_NOTE());

        //set font for all the text view
        setFont((ViewGroup)dialog.findViewById(R.id.schedTransactionPopperLLId));
    }

    private void toEditScheduledTransaction(ScheduledTransactionModel scheduledTransactionModelObj) {
        //navigateTo(AddUpdateScheduleTransactionActivity.class, "SCHEDULED_TRANSACTION_OBJ", scheduledTransactionModelObj);
    }

    private void toEditScheduledTransfer(ScheduledTransferModel scheduledTransferModelObj) {
        //navigateTo(AddUpdateScheduleTransferActivity.class, "SCHEDULED_TRANSFER_OBJ", scheduledTransferModelObj);
    }

    private void showScheduledTransferDetailsPopper(final ScheduledTransferModel scheduledTransferModelObj) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_scheduled_transfer_popper);
        dialog.setOnDismissListener(dialogsDismissListener);

        dialog.show();

        //Buttons
        ImageView schedTransferPopperEditIV, schedTransferPopperDeleteIV;
        schedTransferPopperEditIV = (ImageView) dialog.findViewById(R.id.schedTransferPopperEditIVId);
        schedTransferPopperDeleteIV = (ImageView) dialog.findViewById(R.id.schedTransferPopperDeleteIVId);

        schedTransferPopperDeleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScheduleMessagePopper(scheduledTransferModelObj);
            }
        });
        schedTransferPopperEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog != null) {
                    dialog.dismiss();
                }
                toEditScheduledTransfer(scheduledTransferModelObj);
            }
        });

        //texts
        TextView schedTransferPopperDateTV, schedTransferPopperAmountTV, schedTransferPopperFreqTV, schedTransferPopperAutoTV, schedTransferPopperFromAccTV
                , schedTransferPopperToAccTV, schedTransferPopperNotesTV, schedTransferPopperCreateDateTV, schedTransferPopperUpdateDateTV
                , schedTransferPopperStatusTV;
        LinearLayout schedTransferPopperUpdateDateLV, schedTransferPopperStatusLV;
        ImageView schedTransferPopperStatusIV;

        schedTransferPopperDateTV = (TextView) dialog.findViewById(R.id.schedTransferPopperDateTVId);
        schedTransferPopperAmountTV = (TextView) dialog.findViewById(R.id.schedTransferPopperAmountTVId);
        schedTransferPopperFreqTV = (TextView) dialog.findViewById(R.id.schedTransferPopperFreqTVId);
        schedTransferPopperAutoTV = (TextView) dialog.findViewById(R.id.schedTransferPopperAutoTVId);
        schedTransferPopperFromAccTV = (TextView) dialog.findViewById(R.id.schedTransferPopperFromAccTVId);
        schedTransferPopperToAccTV = (TextView) dialog.findViewById(R.id.schedTransferPopperToAccTVId);
        schedTransferPopperNotesTV = (TextView) dialog.findViewById(R.id.schedTransferPopperNotesTVId);
        schedTransferPopperCreateDateTV = (TextView) dialog.findViewById(R.id.schedTransferPopperCreateDateTVId);
        schedTransferPopperUpdateDateTV = (TextView) dialog.findViewById(R.id.schedTransferPopperUpdateDateTVId);
        schedTransferPopperUpdateDateLV = (LinearLayout) dialog.findViewById(R.id.schedTransferPopperUpdateDateLVId);
        schedTransferPopperStatusLV = (LinearLayout) dialog.findViewById(R.id.schedTransferPopperStatusLVId);
        schedTransferPopperStatusIV = (ImageView) dialog.findViewById(R.id.schedTransferPopperStatusIVId);
        schedTransferPopperStatusTV = (TextView) dialog.findViewById(R.id.schedTransferPopperStatusTVId);

        SimpleDateFormat sdfWrong = new SimpleDateFormat(JAVA_DATE_FORMAT);
        SimpleDateFormat sdfRight = new SimpleDateFormat(UI_DATE_FORMAT);

        try{
            schedTransferPopperDateTV.setText(sdfRight.format(sdfWrong.parse(selectedDateStr)));
            schedTransferPopperCreateDateTV.setText(sdfRight.format(scheduledTransferModelObj.getCREAT_DTM()));

            if(scheduledTransferModelObj.getMOD_DTM() == null){
                schedTransferPopperUpdateDateLV.setVisibility(View.GONE);
            }
            else{
                schedTransferPopperUpdateDateTV.setText(sdfRight.format(scheduledTransferModelObj.getMOD_DTM()));
            }
        }
        catch(ParseException pe){
            Log.e(CLASS_NAME, "Error ! "+pe);
            return;
        }

        if(scheduledTransferModelObj.getStatus() != null && !scheduledTransferModelObj.getStatus().isEmpty()){
            schedTransferPopperStatusLV.setVisibility(View.VISIBLE);
            schedTransferPopperEditIV.setVisibility(View.GONE);

            if("CANCEL".equalsIgnoreCase(scheduledTransferModelObj.getStatus())){
                schedTransferPopperStatusIV.setBackgroundResource(R.drawable.cross_white_small);
                schedTransferPopperStatusTV.setTextColor(getResources().getColor(R.color.red));
                schedTransferPopperStatusTV.setText("Cancelled");
            }
            else if("AUTO_ADD".equalsIgnoreCase(scheduledTransferModelObj.getStatus()) || "ADD".equalsIgnoreCase(scheduledTransferModelObj.getStatus())){
                schedTransferPopperStatusIV.setBackgroundResource(R.drawable.tick_white_small);
                schedTransferPopperStatusTV.setTextColor(getResources().getColor(R.color.finappleTheme));
                schedTransferPopperStatusTV.setText("Added");
            }
            else if("SCHEDULED".equalsIgnoreCase(scheduledTransferModelObj.getStatus())){
                schedTransferPopperStatusIV.setBackgroundResource(R.drawable.scheduled_transaction_white);
                schedTransferPopperStatusTV.setTextColor(getResources().getColor(R.color.orrange));
                schedTransferPopperStatusTV.setText("Scheduled");

                //enable editing when its scheduled
                schedTransferPopperEditIV.setVisibility(View.VISIBLE);
            }
        }
        else{
            Log.e(CLASS_NAME, "Expecting Scheduled Transaction status to be AUTO_ADD/ADD/CANCEL/SCHEDULED..but found none.");
            schedTransferPopperStatusIV.setBackgroundResource(R.drawable.info_white);
            schedTransferPopperStatusTV.setTextColor(getResources().getColor(R.color.red));
            schedTransferPopperStatusTV.setText("Unavailable");
        }

        //TODO: Approximatization required

        schedTransferPopperAmountTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyPosColor));
        schedTransferPopperAmountTV.setText(String.valueOf(scheduledTransferModelObj.getSCH_TRNFR_AMT()));

        schedTransferPopperFreqTV.setText("REPEATS " + scheduledTransferModelObj.getSCH_TRNFR_FREQ());

        if("AUTO_ADD".equalsIgnoreCase(scheduledTransferModelObj.getSCH_TRNFR_AUTO())){
            schedTransferPopperAutoTV.setText("AUTO ADD");
        }
        else if("NOTIFY_ADD".equalsIgnoreCase(scheduledTransferModelObj.getSCH_TRNFR_AUTO())) {
            schedTransferPopperAutoTV.setText("NOTIFY & ADD");
        }
        else{
            Log.e(CLASS_NAME, "Stupidity Error !!");
            return;
        }

        schedTransferPopperFromAccTV.setText(scheduledTransferModelObj.getFromAccountStr());
        schedTransferPopperToAccTV.setText(scheduledTransferModelObj.getToAccountStr());
        schedTransferPopperNotesTV.setText(scheduledTransferModelObj.getSCH_TRNFR_NOTE());

        //set font for all the text view
        setFont((ViewGroup)dialog.findViewById(R.id.schedTransferPopperLLId));
    }

    private void showTransferDetailsPopper(final TransferModel transferObj) {
        anotherDialog = new Dialog(mContext);
        anotherDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        anotherDialog.setContentView(R.layout.transfer_view_details_popper);

        anotherDialog.show();

        //Buttons
        ImageView tranferDtlPopperEditIV, transferDtlPopperDelIV;
        tranferDtlPopperEditIV = (ImageView) anotherDialog.findViewById(R.id.tranferDtlPopperEditIVId);
        transferDtlPopperDelIV = (ImageView) anotherDialog.findViewById(R.id.transferDtlPopperDelIVId);

        transferDtlPopperDelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessagePopper(transferObj);
            }
        });
        tranferDtlPopperEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(anotherDialog != null) {
                    anotherDialog.dismiss();
                }
                toEditTransfer(transferObj);
            }
        });

        //texts
        TextView transferDtlPopperFromAccTV, transferDtlPopperToAccTV, transferDtlPopperNoteTV, transferDtlPopperDateTV, transferDtlPopperAmtTV,
                transferDtlPopperCreateTV, transferDtlPopperUpdtdTV;
        LinearLayout transferDtlPopperNoteLL;
        transferDtlPopperFromAccTV = (TextView) anotherDialog.findViewById(R.id.transferDtlPopperFromAccTVId);
        transferDtlPopperToAccTV = (TextView) anotherDialog.findViewById(R.id.transferDtlPopperToAccTVId);
        transferDtlPopperNoteTV = (TextView) anotherDialog.findViewById(R.id.transferDtlPopperNoteTVId);
        transferDtlPopperDateTV = (TextView) anotherDialog.findViewById(R.id.transferDtlPopperDateTVId);
        transferDtlPopperAmtTV = (TextView) anotherDialog.findViewById(R.id.transferDtlPopperAmtTVId);
        transferDtlPopperCreateTV = (TextView) anotherDialog.findViewById(R.id.transferDtlPopperCreateTVId);
        transferDtlPopperUpdtdTV = (TextView) anotherDialog.findViewById(R.id.transferDtlPopperUpdtdTVId);
        transferDtlPopperNoteLL = (LinearLayout) anotherDialog.findViewById(R.id.transferDtlPopperNoteLLId);

        //set values getFormattedDate
        transferDtlPopperFromAccTV.setText(transferObj.getFromAccName());
        transferDtlPopperToAccTV.setText(transferObj.getToAccName());
        transferDtlPopperNoteTV.setText(transferObj.getTRNFR_NOTE());
        transferDtlPopperAmtTV.setText(String.valueOf(transferObj.getTRNFR_AMT()));

        tranferDtlPopperEditIV.setTag(transferObj.getTRNFR_ID());
        transferDtlPopperDelIV.setTag(transferObj.getTRNFR_ID());

        //hide the notes panel if there isnt any note
        if(transferObj.getTRNFR_NOTE() == null){
            transferDtlPopperNoteLL.setVisibility(View.GONE);
        }
        else if(transferObj.getTRNFR_NOTE().isEmpty()){
            transferDtlPopperNoteLL.setVisibility(View.GONE);
        }

        SimpleDateFormat goodFormat1 = new SimpleDateFormat("d MMM ''yy, h:mm a");
        SimpleDateFormat goodFormat2 = new SimpleDateFormat("d MMM ''yy");

        transferDtlPopperDateTV.setText(goodFormat2.format(transferObj.getTRNFR_DATE()));
        transferDtlPopperCreateTV.setText(goodFormat1.format(transferObj.getCREAT_DTM()));

        if(transferObj.getMOD_DTM() != null){
            transferDtlPopperUpdtdTV.setText(goodFormat2.format(transferObj.getMOD_DTM()));
        }

        //set font for all the text view
        setFont((ViewGroup)anotherDialog.findViewById(R.id.transferDtlPopperLLId));
    }

    private void showTransactionDetailsPopper(final TransactionModel transactionModelObj){
        anotherDialog = new Dialog(mContext);
        anotherDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        anotherDialog.setContentView(R.layout.transaction_view_details_popper);

        anotherDialog.show();

        //Dialogue UI items
        LinearLayout tranDtlPopperSchTranLL, tranDtlPopperCatTattooLL, tranDtlPopperNoteLL, tranDtlPopperContentLL;
        tranDtlPopperSchTranLL = (LinearLayout) anotherDialog.findViewById(R.id.tranDtlPopperSchTranLLId);
        tranDtlPopperNoteLL = (LinearLayout) anotherDialog.findViewById(R.id.tranDtlPopperNoteLLId);

        //Buttons
        ImageView tranDtlPopperDelIV, tranDtlPopperEditIV;
        tranDtlPopperDelIV = (ImageView) anotherDialog.findViewById(R.id.tranDtlPopperDelIVId);
        tranDtlPopperEditIV = (ImageView) anotherDialog.findViewById(R.id.tranDtlPopperEditIVId);

        tranDtlPopperDelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessagePopper(transactionModelObj);
            }
        });
        tranDtlPopperEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killPopper();
                toEditTransaction(transactionModelObj);
            }
        });

        //texts
        TextView tranDtlPopperTranNameTV, tranDtlPopperCatTV, tranDtlPopperAccTV, tranDtlPopperSpntOnTV, tranDtlPopperNoteTV, tranDtlPopperTranTypTV,
                tranDtlPopperAmtTV, tranDtlPopperCreateTV, tranDtlPopperUpdtdTV, tranDtlPopperTranDateTV ;

        //tranDtlPopperTranTypTV = (TextView) anotherDialog.findViewById(R.id.tranDtlPopperTranTypTVId);
        tranDtlPopperTranDateTV = (TextView) anotherDialog.findViewById(R.id.tranDtlPopperTranDateTVId);
        tranDtlPopperAmtTV = (TextView) anotherDialog.findViewById(R.id.tranDtlPopperAmtTVId);
        tranDtlPopperTranNameTV = (TextView) anotherDialog.findViewById(R.id.tranDtlPopperTranNameTVId);
        tranDtlPopperCatTV = (TextView) anotherDialog.findViewById(R.id.tranDtlPopperCatTVId);
        tranDtlPopperAccTV = (TextView) anotherDialog.findViewById(R.id.tranDtlPopperAccTVId);
        tranDtlPopperSpntOnTV = (TextView) anotherDialog.findViewById(R.id.tranDtlPopperSpntOnTVId);
        tranDtlPopperNoteTV = (TextView) anotherDialog.findViewById(R.id.tranDtlPopperNoteTVId);
        tranDtlPopperCreateTV = (TextView) anotherDialog.findViewById(R.id.tranDtlPopperCreateTVId);
        tranDtlPopperUpdtdTV = (TextView) anotherDialog.findViewById(R.id.tranDtlPopperUpdtdTVId);

        if("INCOME".equalsIgnoreCase(transactionModelObj.getTRAN_TYPE())){
            tranDtlPopperAmtTV.setTextColor(tranDtlPopperAmtTV.getResources().getColor(R.color.finappleCurrencyPosColor));
            //tranDtlPopperTranTypTV.setBackground(tranDtlPopperTranTypTV.getResources().getDrawable(R.drawable.circle_income_text_view, null));
            //tranDtlPopperTranTypTV.setText("I");
            tranDtlPopperAmtTV.setText(String.valueOf(transactionModelObj.getTRAN_AMT()));
        }
        else{
            tranDtlPopperAmtTV.setTextColor(tranDtlPopperAmtTV.getResources().getColor(R.color.finappleCurrencyNegColor));
            //tranDtlPopperTranTypTV.setBackground(tranDtlPopperTranTypTV.getResources().getDrawable(R.drawable.circle_expense_text_view, null));
            //tranDtlPopperTranTypTV.setText("E");
            tranDtlPopperAmtTV.setText(String.valueOf("-"+transactionModelObj.getTRAN_AMT()));
        }

        SimpleDateFormat goodFormat1 = new SimpleDateFormat(UI_DATE_TIME_FORMAT);
        SimpleDateFormat goodFormat2 = new SimpleDateFormat(UI_DATE_FORMAT);

        //set values
        tranDtlPopperTranNameTV.setText(transactionModelObj.getTRAN_NAME());
        tranDtlPopperCatTV.setText(transactionModelObj.getCategory());
        tranDtlPopperAccTV.setText(transactionModelObj.getAccount());
        tranDtlPopperSpntOnTV.setText(transactionModelObj.getSpentOn());
        tranDtlPopperNoteTV.setText(transactionModelObj.getTRAN_NOTE());

        tranDtlPopperTranDateTV.setText(goodFormat2.format(transactionModelObj.getTRAN_DATE()));
        tranDtlPopperCreateTV.setText(goodFormat1.format(transactionModelObj.getCREAT_DTM()));

        if(transactionModelObj.getMOD_DTM() != null){
            tranDtlPopperUpdtdTV.setText(goodFormat2.format(transactionModelObj.getMOD_DTM()));
        }

        tranDtlPopperDelIV.setTag(transactionModelObj.getTRAN_ID());
        tranDtlPopperEditIV.setTag(transactionModelObj.getTRAN_ID());

        //hide the notes panel if there isnt any note
        if(transactionModelObj.getTRAN_NOTE() == null){
            tranDtlPopperNoteLL.setVisibility(View.GONE);
        }
        else if(transactionModelObj.getTRAN_NOTE().isEmpty()){
            tranDtlPopperNoteLL.setVisibility(View.GONE);
        }

        //set font for all the text view
        setFont((ViewGroup)anotherDialog.findViewById(R.id.tranDtlPopperLLId));
    }

    private void toEditTransaction(TransactionModel transactionModelObj) {
        //navigateTo(AddUpdateTransactionActivity.class, "TRANSACTION_OBJ", transactionModelObj);
    }

    private void toEditTransfer(TransferModel transferModelObj) {
        //navigateTo(AddUpdateTransferActivity.class, "TRANSFER_OBJ", transferModelObj);
    }

    private void refreshActivity(){
        Log.i(CLASS_NAME, "Selected Date : "+selectedDateStr);
        Intent intent = new Intent(mContext, CalendarActivity.class);
        intent.putExtra("SELECTED_DATE", selectedDateStr);
        mContext.startActivity(intent);
    }

    public void showAccountDeletePopper(AccountsMO accountsModelObj){
        // Create custom message popper object
        anotherMessageDialog = new Dialog(mContext);
        anotherMessageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        anotherMessageDialog.setContentView(R.layout.message_popper_account);

        anotherMessageDialog.show();

        //buttons
        LinearLayout msgPoprAccPosLL, msgPoprAccNegLL;
        msgPoprAccPosLL = (LinearLayout) anotherMessageDialog.findViewById(R.id.msgPoprAccPosLLId);
        msgPoprAccNegLL = (LinearLayout) anotherMessageDialog.findViewById(R.id.msgPoprAccNegLLId);

        //validation
        if(accountsModelObj == null){
            Log.e(CLASS_NAME, "ERROR !! Tag is null");
            return;
        }

        //set positive buttons tag as tran id for deleting
        msgPoprAccPosLL.setTag(accountsModelObj);

        //set listeners for the buttons
        msgPoprAccPosLL.setOnClickListener(linearLayoutClickListener);
        msgPoprAccNegLL.setOnClickListener(linearLayoutClickListener);

        //set font for all the text view
        setFont((ViewGroup) anotherMessageDialog.findViewById(R.id.msgPoprAccLLId));
    }

    public void showMessagePopper(Object object){
        // Create custom message popper object
        messageDialog = new Dialog(mContext);
        messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        messageDialog.setContentView(R.layout.message_popper);

        messageDialog.show();

        //buttons
        LinearLayout msgPoprPosLL, msgPoprNegLL;
        msgPoprPosLL = (LinearLayout) messageDialog.findViewById(R.id.msgPoprPosLLId);
        msgPoprNegLL = (LinearLayout) messageDialog.findViewById(R.id.msgPoprNegLLId);

        //validation
        if(object == null){
            Log.e(CLASS_NAME, "ERROR !! Tag is null");
            return;
        }

        //set positive buttons tag as tran id for deleting
        msgPoprPosLL.setTag(object);

        //set listeners for the buttons
        msgPoprPosLL.setOnClickListener(linearLayoutClickListener);
        msgPoprNegLL.setOnClickListener(linearLayoutClickListener);

        //texts
        TextView msgPoprNegTV, msgPoprPosTV, msgPoprMsgTV;
        msgPoprNegTV = (TextView) messageDialog.findViewById(R.id.msgPoprNegTVId);
        msgPoprPosTV = (TextView) messageDialog.findViewById(R.id.msgPoprPosTVId);
        msgPoprMsgTV = (TextView) messageDialog.findViewById(R.id.msgPoprMsgTVId);

        if(object instanceof  TransactionModel){
            msgPoprMsgTV.setText("Delete this Transaction ?");
        }
        else if(object instanceof  TransferModel){
            msgPoprMsgTV.setText("Delete this Transfer ?");
        }
        else if(object instanceof BudgetModel){
            msgPoprMsgTV.setText("Delete this Budget ?");
        }
        else if(object instanceof AccountsMO){
            msgPoprMsgTV.setText("Delete this Account ?");
        }
        else{
            msgPoprMsgTV.setText("Error !!");
            Log.e(CLASS_NAME, "Boy !! let me tell you something. ManToMan. I'm expecting either a Transfer/Transaction/Budget/Account Model Object. Nothing else. NOT JUNK !!!!");
            return;
        }

        //set font for all the text view
        setFont((ViewGroup) messageDialog.findViewById(R.id.msgPoprLLId));
    }

    public void showScheduleMessagePopper(Object object){
        // Create custom message popper object
        messageDialog = new Dialog(mContext);
        messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        messageDialog.setContentView(R.layout.message_popper_schedule);

        messageDialog.show();

        //buttons
        LinearLayout msgPoprSchedNegLL, msgPoprSchedSuperNegLL;
        msgPoprSchedNegLL = (LinearLayout) messageDialog.findViewById(R.id.msgPoprSchedNegLLId);
        msgPoprSchedSuperNegLL = (LinearLayout) messageDialog.findViewById(R.id.msgPoprSchedSuperNegLLId);

        //validation
        if(object == null){
            Log.e(CLASS_NAME, "ERROR !! Tag is null");
            return;
        }

        //set positive buttons tag as tran id for deleting
        msgPoprSchedNegLL.setTag(object);
        msgPoprSchedSuperNegLL.setTag(object);

        //set listeners for the buttons
        msgPoprSchedNegLL.setOnClickListener(linearLayoutClickListener);
        msgPoprSchedSuperNegLL.setOnClickListener(linearLayoutClickListener);

        //texts
        TextView msgPoprSchedMsgTV;
        msgPoprSchedMsgTV = (TextView) messageDialog.findViewById(R.id.msgPoprSchedMsgTVId);

        if(object instanceof  ScheduledTransactionModel){
            msgPoprSchedMsgTV.setText("You can either Delete this Transaction or remove all the Schedules ahead");
        }
        else if(object instanceof  ScheduledTransferModel){
            msgPoprSchedMsgTV.setText("You can either Delete this Transfer or remove all the Schedules ahead");
        }
        else{
            msgPoprSchedMsgTV.setText("Error !!");
            Log.e(CLASS_NAME, "Boy !! let me tell you something. Man-Man. I'm expecting either a Scheduled Transaction id, Scheduled transfer id. Nothing else. NOT JUNK !!!!");
            return;
        }

        //set font for all the text view
        setFont((ViewGroup) messageDialog.findViewById(R.id.msgPoprSchedLLId));
    }

    private void showBudgetPopper(final BudgetModel budgetModelObj){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_budget_popper);
        dialog.setOnDismissListener(dialogsDismissListener);

        dialog.show();

        //commons
        TextView budgetPopperTotalAmountTV = (TextView) dialog.findViewById(R.id.budgetPopperTotalAmountTVId);
        TextView budgetPopperBudgetAmountTV = (TextView) dialog.findViewById(R.id.budgetPopperBudgetAmountTVId);
        TextView budgetPopperRangeTV = (TextView) dialog.findViewById(R.id.budgetPopperRangeTVId);
        TextView budgetPopperNameTV = (TextView) dialog.findViewById(R.id.budgetPopperNameTVId);
        TextView budgetPopperTypeHeadingTV = (TextView) dialog.findViewById(R.id.budgetPopperTypeHeadingTVId);
        TextView budgetPopperTypeValueTV = (TextView) dialog.findViewById(R.id.budgetPopperTypeValueTVId);
        TextView budgetPopperNotesTV = (TextView) dialog.findViewById(R.id.budgetPopperNotesTVId);
        TextView budgetPopperCreateDateTV = (TextView) dialog.findViewById(R.id.budgetPopperCreateDateTVId);
        TextView budgetPopperUpdateDateTV = (TextView) dialog.findViewById(R.id.budgetPopperUpdateDateTVId);

        LinearLayout budgetUpdateDateLV = (LinearLayout) dialog.findViewById(R.id.budgetUpdateDateLVId);

        ImageView budgetPopperEditIV = (ImageView) dialog.findViewById(R.id.budgetPopperEditIVId);
        ImageView budgetPopperDeleteIV = (ImageView) dialog.findViewById(R.id.budgetPopperDeleteIVId);

        budgetPopperEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog != null){
                    dialog.dismiss();
                }
                toEditBudget(budgetModelObj);
            }
        });

        budgetPopperDeleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessagePopper(budgetModelObj);
            }
        });

        //TODO: Approximatization is required
        budgetPopperTotalAmountTV.setText(String.valueOf(budgetModelObj.getBudgetRangeTotal()));
        budgetPopperBudgetAmountTV.setText(String.valueOf(budgetModelObj.getBUDGET_AMT()));

        if(budgetModelObj.getBudgetRangeTotal() > budgetModelObj.getBUDGET_AMT()){
            budgetPopperTotalAmountTV.setTextColor(getResources().getColor(R.color.finappleCurrencyNegColor));
        }
        else{budgetPopperTotalAmountTV.setTextColor(getResources().getColor(R.color.finappleCurrencyPosColor));
            budgetPopperTotalAmountTV.setTextColor(getResources().getColor(R.color.finappleCurrencyPosColor));
        }

        budgetPopperRangeTV.setText(budgetModelObj.getBUDGET_TYPE().toUpperCase());
        budgetPopperNameTV.setText(budgetModelObj.getBUDGET_NAME());
        budgetPopperTypeHeadingTV.setText(budgetModelObj.getBUDGET_GRP_TYPE().toUpperCase());

        if("CATEGORY".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
            budgetPopperTypeValueTV.setText(budgetModelObj.getCategoryNameStr());
        }
        else if("ACCOUNT".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
            budgetPopperTypeValueTV.setText(budgetModelObj.getAccountNameStr());
        }
        else if("SPENT ON".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
            budgetPopperTypeValueTV.setText(budgetModelObj.getSpentOnNameStr());
        }
        else{
            Log.e(CLASS_NAME, "Stupidity Error !! Expecting CATEGORY/ACCOUNT/SPENT ON but found none of them");
            return;
        }

        budgetPopperNotesTV.setText(budgetModelObj.getBUDGET_NOTE());

        //convert dd-MM-yyyy into dd MMM 'yy
        SimpleDateFormat rightSdf = new SimpleDateFormat(UI_DATE_FORMAT);

        budgetPopperCreateDateTV.setText(rightSdf.format(budgetModelObj.getCREAT_DTM()));

        if(budgetModelObj.getMOD_DTM() != null){
            budgetPopperUpdateDateTV.setText(rightSdf.format(budgetModelObj.getMOD_DTM()));
            budgetUpdateDateLV.setVisibility(View.VISIBLE);
        }
        else{
            budgetUpdateDateLV.setVisibility(View.GONE);
        }

        //set font for all the text view
        setFont((ViewGroup) dialog.findViewById(R.id.budgetPopperLLId));
    }

    private void toEditBudget(BudgetModel budgetModelObj) {
        //navigateTo(AddUpdateBudgetActivity.class, "BUDGET_OBJ", budgetModelObj);
    }

    private void showAccountPopper(final AccountsMO accountsModelObj){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_account_popper);
        dialog.setOnDismissListener(dialogsDismissListener);

        dialog.show();

        //commons
        TextView accountPopperTitleTV = (TextView) dialog.findViewById(R.id.accountPopperTitleTVId);
        ImageView accountPopperEditIV = (ImageView) dialog.findViewById(R.id.accountPopperEditIVId);
        ImageView accountPopperDelIV = (ImageView) dialog.findViewById(R.id.accountPopperDelIVId);
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
            accountPopperBalanceTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyNegColor));
        }
        else{
            accountPopperBalanceTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyPosColor));
        }

        //set Last Transaction
        TransactionModel transactionModelObj = calendarDbService.getLastTransactionOnAccountId(accountsModelObj.getACC_ID());

        //convert dd-MM-yyyy into dd MMM 'yy
        SimpleDateFormat rightSdf = new SimpleDateFormat(UI_DATE_FORMAT);

        if(transactionModelObj == null){
            accountPopperLastTransactionLL.setVisibility(View.GONE);
        }
        else{
            accountPopperTransactionCategoryTV.setText(transactionModelObj.getCategory());

            if("EXPENSE".equalsIgnoreCase(transactionModelObj.getTRAN_TYPE())){
                accountPopperTransactionAmtTV.setText("-"+transactionModelObj.getTRAN_AMT());
                accountPopperTransactionAmtTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyNegColor));
            }
            else{
                accountPopperTransactionAmtTV.setText(String.valueOf(transactionModelObj.getTRAN_AMT()));
                accountPopperTransactionAmtTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyPosColor));
            }

            accountPopperTransactionDateTV.setText(rightSdf.format(transactionModelObj.getTRAN_DATE()));
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

            accountPopperTransferDateTV.setText(rightSdf.format(transferModelObj.getTRNFR_DATE()));
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
                navigateTo(AddUpdateAccountActivity.class, "ACCOUNT_OBJ", accountsModelObj);
            }
        });

        accountPopperDelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessagePopper(accountsModelObj);
            }
        });

        if("Y".equalsIgnoreCase(accountsModelObj.getACC_IS_DEFAULT())){
            accountPopperEditIV.setVisibility(View.GONE);
            accountPopperDelIV.setVisibility(View.GONE);
        }

        //set font for all the text view
        setFont((ViewGroup) dialog.findViewById(R.id.accountPopperLLId));
    }

    private void showTransfersPopper(ConsolidatedTransferModel consolidatedTransferModelObj){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_summary_popper);
        dialog.setOnDismissListener(dialogsDismissListener);

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
        SimpleDateFormat wrongSdf = new SimpleDateFormat(JAVA_DATE_FORMAT);
        SimpleDateFormat rightSdf = new SimpleDateFormat(UI_DATE_FORMAT);
        String rightDateStr = rightSdf.format(consolidatedTransferModelObj.getDate());

        summaryPopperDateTV.setText(rightDateStr);
        summaryPopperTotalLL.setBackgroundResource(R.color.transferIndicator);
        summaryPopperTotalTV.setText(String.valueOf(consolidatedTransferModelObj.getAmount()));

        //set up list
        TransferModel trfrsObj = new TransferModel();
        trfrsObj.setTRNFR_DATE(consolidatedTransferModelObj.getDate());
        trfrsObj.setFromAccName(consolidatedTransferModelObj.getFromAccountStr());
        trfrsObj.setToAccName(consolidatedTransferModelObj.getToAccountStr());
        //set user id
        trfrsObj.setUSER_ID(loggedInUserObj.getUSER_ID());

        List<Object> transferList = calendarDbService.getTransfersOnDateAndAccounts(trfrsObj);
        SummaryPopperListAdapter summaryPopperListAdapter = new SummaryPopperListAdapter(mContext, R.layout.calendar_summary_popper_list_view
                , transferList);
        summaryPopperLV.setAdapter(summaryPopperListAdapter);
        summaryPopperListAdapter.notifyDataSetChanged();
        summaryPopperLV.setOnItemClickListener(listViewClickListener);

        //set font for all the text view
        setFont((ViewGroup) summaryPopperLL);
    }

    private void fetchMonthLegend(){
        monthLegendMap = calendarDbService.getMonthLegendOnDate(currentFocusedMonthStr, loggedInUserObj.getUSER_ID());
    }

    private void setUpTabs() {
        viewPagerTabsList = new ArrayList<>();
        viewPagerTabsList.add(R.layout.calendar_tab_summary);
        viewPagerTabsList.add(R.layout.calendar_tab_accounts);
        viewPagerTabsList.add(R.layout.calendar_tab_budgets);
        viewPagerTabsList.add(R.layout.calendar_tab_schedules);

        calendarTabsViewPagerAdapter = new CalendarTabsViewPagerAdapter(mContext, viewPagerTabsList, DateTimeUtil.cleanUpDate(selectedDateStr), loggedInUserObj, monthLegendMap,
                        new ListViewItemClickListener() {
                            @Override
                            public void onListItemClick(Object listItemObject) {
                                if(checkAndCollapseFab()){
                                    return;
                                }

                                if (listItemObject instanceof ConsolidatedTransactionModel) {
                                    showTransactionsPopper((ConsolidatedTransactionModel) listItemObject);
                                }
                                else if (listItemObject instanceof ConsolidatedTransferModel) {
                                    showTransfersPopper((ConsolidatedTransferModel) listItemObject);
                                }
                                else if (listItemObject instanceof AccountsMO) {
                                    showAccountPopper((AccountsMO) listItemObject);
                                }
                                else if (listItemObject instanceof BudgetModel) {
                                    showBudgetPopper((BudgetModel) listItemObject);
                                }
                                else if (listItemObject instanceof ScheduledTransactionModel) {
                                    showScheduledTransactionDetailsPopper((ScheduledTransactionModel) listItemObject);
                                }
                                else if (listItemObject instanceof ScheduledTransferModel) {
                                    showScheduledTransferDetailsPopper((ScheduledTransferModel) listItemObject);
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
                if (checkAndCollapseFab()) {
                    return;
                }
            }
        });
    }

    private void setUpCalendar() {
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
                    String monthAndYear = sdf.format(new Date());
                    String monthAndYearArr[] = monthAndYear.split("-");

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
                    //oldScreenIndex = position;
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
                    String monthAndYearArr[] = monthAndYear.split("-");

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
                    calendarSummaryTV.setTextColor(mContext.getResources().getColor(R.color.white));
                    calendarSummaryTV.setTag("SELECTED");
                }
                break;

            case 1:
                if (!"SELECTED".equalsIgnoreCase(calendarAccountsTV.getTag().toString())) {
                    deselectAllTabs();
                    calendarAccountsTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarAccountsTV.setTextColor(mContext.getResources().getColor(R.color.white));
                    calendarAccountsTV.setTag("SELECTED");
                }
                break;

            case 2:
                if (!"SELECTED".equalsIgnoreCase(calendarBudgetsTV.getTag().toString())) {
                    deselectAllTabs();
                    calendarBudgetsTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarBudgetsTV.setTextColor(mContext.getResources().getColor(R.color.white));
                    calendarBudgetsTV.setTag("SELECTED");
                }
                break;

            case 3:
                if (!"SELECTED".equalsIgnoreCase(calendarSchedulesTV.getTag().toString())) {
                    deselectAllTabs();
                    calendarSchedulesTV.setBackgroundResource(R.drawable.calendar_small_tab_active_inner);
                    calendarSchedulesTV.setTextColor(mContext.getResources().getColor(R.color.white));
                    calendarSchedulesTV.setTag("SELECTED");
                }
                break;

            default:
                showToast("Tab Error !!");
        }
    }

    public void onTabSelect(View view) {
        Log.i(CLASS_NAME, "Son....i created you. The tab implementation...although sucks, a very own of my creation. And you work !! selected: " + view.getId());

        if(checkAndCollapseFab()){
            return;
        }

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

    private void initUIComponents() {
        //get UI components

        //header
        yearTV = (TextView) this.findViewById(R.id.calendarFullYearId);
        calendarMonthTV = (TextView) this.findViewById(R.id.calendarMonthId);
        calendarTodayTV = (TextView) this.findViewById(R.id.calendarTodayTVId);
        calendarHeaderMonthYearLL = (LinearLayout) this.findViewById(R.id.calendarHeaderMonthYearLLId);

        //view pager
        viewPager = (ViewPager) this.findViewById(R.id.calendarTabsVPId);
        viewPagerMonths = (ViewPager) this.findViewById(R.id.calendarDatesVPId);
    }

    private void setUpHeader() {
        String tempSelectedDateStrArr[] = currentFocusedMonthStr.split("-");
        calendarMonthTV.setText(Constants.MONTHS_ARRAY[Integer.parseInt(tempSelectedDateStrArr[0])-1]);
        yearTV.setText(tempSelectedDateStrArr[1]);

        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        if(!currentFocusedMonthStr.equalsIgnoreCase(sdf.format(new Date()))){
            calendarHeaderMonthYearLL.animate().setDuration(500).translationX(-150);

        } else{
            calendarHeaderMonthYearLL.animate().setDuration(500).translationX(0);
        }
    }

    public void onTodayClick(View view){
        //this is to stop unnecessary code to execute in setUpCalendar method page selected event
        ignore = true;

        Date today = new Date();
        selectedDateStr = sdf.format(today);
        //initActivity();

        SimpleDateFormat sdf1 = new SimpleDateFormat("MM-yyyy");
        currentFocusedMonthStr = sdf1.format(today);
        setUpCalendar();
        setUpTabs();
    }

    private Intent toAddUpdateTransaction() {
        /*TransactionModel tranObj = new TransactionModel();
        tranObj.setTRAN_DATE(DateTimeUtil.cleanUpDate(selectedDateStr));
        Intent intent = new Intent(mContext, AddUpdateTransactionActivity.class);
        intent.putExtra("TRANSACTION_OBJ", tranObj);
        return intent;*/

        return getIntent();
    }

    private Intent toAddUpdateTransfer() {
        /*TransferModel tranfrObj = new TransferModel();
        tranfrObj.setTRNFR_DATE(DateTimeUtil.cleanUpDate(selectedDateStr));
        Intent intent = new Intent(mContext, AddUpdateTransferActivity.class);
        intent.putExtra("TRANSFER_OBJ", tranfrObj);
        return intent;*/

        return getIntent();
    }

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
                        showAddUpdateTransactionPopper(null);
                        break;
                    case R.id.transactionPopperQuickLVId :
                        killPopper();
                        showQuickTransactionPopper();
                        break;
                    case R.id.transactionPopperSchedLVId :
                        killPopper();
                        /*intent = new Intent(mContext, AddUpdateScheduleTransactionActivity.class);
                        ScheduledTransactionModel scheduledTransactionModelObj = new ScheduledTransactionModel();
                        scheduledTransactionModelObj.setSCH_TRAN_DATE(DateTimeUtil.cleanUpDate(selectedDateStr));
                        intent.putExtra("SCHEDULED_TRANSACTION_OBJ", scheduledTransactionModelObj);*/
                        break;
                    case R.id.transferPopperNewLVId :  intent = toAddUpdateTransfer();
                        break;
                    case R.id.transferPopperQuickLVId :
                        killPopper();
                        showQuickTransferPopper();
                        break;
                    case R.id.transferPopperSchedLVId :
                        killPopper();
                        /*intent = new Intent(mContext, AddUpdateScheduleTransferActivity.class);
                        ScheduledTransferModel scheduledTransferModelObj = new ScheduledTransferModel();
                        scheduledTransferModelObj.setSCH_TRNFR_DATE(DateTimeUtil.cleanUpDate(selectedDateStr));
                        intent.putExtra("SCHEDULED_TRANSFER_OBJ", scheduledTransferModelObj);*/
                        break;

                    case R.id.msgPoprPosLLId:
                        killPopper();
                        Object object = v.getTag();

                        if(object instanceof TransactionModel){
                            if(calendarDbService.deleteTransaction(((TransactionModel) object).getTRAN_ID())){
                                refreshActivity();
                                showToast("Transaction deleted !");
                            }
                            else{
                                showToast("Could not Delete the Transaction");
                            }
                        }
                        else if(object instanceof TransferModel) {
                            if(calendarDbService.deleteTransfer(((TransferModel) object).getTRNFR_ID())){
                                refreshActivity();
                                showToast("Transfer deleted !");
                            }
                            else{
                                showToast("Could not Delete the Transfer");
                            }
                        }
                        else if(object instanceof BudgetModel) {
                            if(calendarDbService.deleteBudget(((BudgetModel) object).getBUDGET_ID())){
                                refreshActivity();
                                showToast("Budget deleted !");
                            }
                            else{
                                showToast("Could not Delete the Budget");
                            }
                        }
                        else if(object instanceof AccountsMO) {
                            showAccountDeletePopper((AccountsMO) object);
                        }
                        break;

                    case R.id.msgPoprNegLLId:
                        killMessagePopper();
                        break;

                    case R.id.msgPoprSchedNegLLId:
                        killMessagePopper();
                        Object obj = v.getTag();

                        if(obj instanceof ScheduledTransactionModel) {
                            ScheduledTransactionModel scheduledTransactionModelObject = (ScheduledTransactionModel) obj;

                            if(calendarDbService.deleteOneSched(scheduledTransactionModelObject)){
                                refreshActivity();
                                showToast("Scheduled Transaction deleted !");
                            }
                            else{
                                showToast("Could not Delete the Scheduled Transaction");
                            }
                        }
                        else if(obj instanceof ScheduledTransferModel) {
                            ScheduledTransferModel scheduledTransferModelObject = (ScheduledTransferModel) obj;

                            if(calendarDbService.deleteOneSched(scheduledTransferModelObject)){
                                refreshActivity();
                                showToast("Scheduled Transfer deleted !");
                            }
                            else{
                                showToast("Could not Delete the Scheduled Transfer");
                            }
                        }
                        break;

                    case R.id.msgPoprSchedSuperNegLLId:
                        killMessagePopper();
                        Object obj2 = v.getTag();

                        if(obj2 instanceof ScheduledTransactionModel) {
                            ScheduledTransactionModel scheduledTransactionModelObject = (ScheduledTransactionModel) obj2;

                            if(calendarDbService.deleteAllSched(scheduledTransactionModelObject)){
                                refreshActivity();
                                showToast("Scheduled Transaction deleted !");
                            }
                            else{
                                showToast("Could not Delete the Scheduled Transaction");
                            }
                        }
                        else if(obj2 instanceof ScheduledTransferModel) {
                            ScheduledTransferModel scheduledTransferModelObject = (ScheduledTransferModel) obj2;

                            if(calendarDbService.deleteAllSched(scheduledTransferModelObject)){
                                refreshActivity();
                                showToast("Scheduled Transfer deleted !");
                            }
                            else{
                                showToast("Could not Delete the Scheduled Transfer");
                            }
                        }
                        break;

                    case R.id.msgPoprAccPosLLId:
                        killPopper();
                        AccountsMO accountsModelObj = (AccountsMO)v.getTag();

                        if(calendarDbService.deleteAccount(accountsModelObj)) {
                            refreshActivity();
                            showToast("Account deleted !");
                        }
                        else{
                            showToast("Could not Delete the Account");
                        }
                        break;

                    case R.id.msgPoprAccNegLLId:
                        killAccountMessagePopper();
                        break;

                    default:intent = new Intent(mContext, JimBrokeItActivity.class); break;
                }

                if(intent != null){
                    mContext.startActivity(intent);
                }
            }
        };
    }

    private void showAddUpdateTransactionPopper(String transactionIdStr) {
        killPopper();

        TransactionModel transactionModelObj = new TransactionModel();
        if(transactionIdStr != null && !transactionIdStr.isEmpty()){
            transactionModelObj.setTRAN_ID(transactionIdStr);
        }
        else{
            try{
                transactionModelObj.setTRAN_DATE(sdf.parse(selectedDateStr));
            }
            catch (ParseException pe){
                Log.e(CLASS_NAME, "Date Parse Error !! "+pe);
            }
        }

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_TRANSACTION);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(TRANSACTION_OBJECT, transactionModelObj);

        TransactionFragment editNameDialog = new TransactionFragment();
        editNameDialog.setArguments(bundle);
        editNameDialog.show(manager, FRAGMENT_TRANSACTION);
    }

    @Override
    public void onFinishUserDialog(String resultStr) {
        if("LOGIN".equalsIgnoreCase(resultStr)){
            killPopper();
            forceLogin();
            return;
        }

        initActivity();

        showToast(resultStr);
    }


    public void goToSettings(View view){
        if(checkAndCollapseFab()){
            return;
        }

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
                navigateTo(SettingsActivity.class, null, null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageIV.startAnimation(rotateAnim);
    }

    public void showDatePicker(View view) {
        if(checkAndCollapseFab()){
            return;
        }

        Log.i(CLASS_NAME, "Working very hard to call date picker to work");
        // Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
        showDialog(999);
    }

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
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

    private void killPopper() {
        if (dialog != null) {
            dialog.dismiss();
        }

        if (anotherDialog != null) {
            anotherDialog.dismiss();
        }

        if (messageDialog != null) {
            messageDialog.dismiss();
        }

        if (anotherMessageDialog != null) {
            anotherMessageDialog.dismiss();
        }

        isNavigation = true;
    }

    private void killMessagePopper() {
        if (messageDialog != null) {
            messageDialog.dismiss();
        }
    }

    private void killAccountMessagePopper() {
        if (anotherMessageDialog != null) {
            anotherMessageDialog.dismiss();
        }

        if (messageDialog != null) {
            messageDialog.dismiss();
        }


    }

    private void showQuickTransferPopper() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_quick_transfer_popper);
        dialog.setOnDismissListener(dialogsDismissListener);

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
        quickTransferAccFrmSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(mContext, R.layout.cat_acc_spnt_spnr, accountList));
        quickTransferAccToSpn.setAdapter(new AddUpdateTransactionSpinnerAdapter(mContext, R.layout.cat_acc_spnt_spnr, accountList));

        //TODO: ideally, from and to account should be the one which the user has selected in the Quick Transfer Template in the settings

        dialog.show();

        quickTransferAmtET.requestFocus();

        quickTransferDoneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double amount;

                // if the amount is not entered or is Zero
                if (String.valueOf(quickTransferAmtET.getText()).isEmpty()
                        || (amount = Double.parseDouble(String.valueOf(quickTransferAmtET.getText()))) == 0) {
                    showToast("Amount cannot be Zero !");
                    return;
                }

                TransferModel transferModelObj = new TransferModel();

                transferModelObj.setACC_ID_FRM(String.valueOf(quickTransferAccFrmSpn.getSelectedView().getTag()));
                transferModelObj.setACC_ID_TO(String.valueOf(quickTransferAccToSpn.getSelectedView().getTag()));
                transferModelObj.setTRNFR_AMT(amount);
                transferModelObj.setTRNFR_DATE(DateTimeUtil.cleanUpDate(selectedDateStr));
                transferModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());

                //stop the user if the from and to accounts are same
                if (transferModelObj.getACC_ID_FRM().equalsIgnoreCase(transferModelObj.getACC_ID_TO())) {
                    showToast("Cannot Transfer between same Accounts");
                    return;
                }

                /*Long result = addUpdateTransfersDbService.addNewTransfer(transferModelObj);

                if (result == -1) {
                    showToast("Error !! Could not create Transaction");
                    Log.e(CLASS_NAME, "ERROR !! Could not create Quick Transaction");
                } else if (result == 0) {
                    showToast("Error !! Could not update Accounts after Transfer");
                    Log.e(CLASS_NAME, "ERROR !! Possibly transfer success but failed to update accounts");
                } else {
                    Log.i(CLASS_NAME, "Quick Transfer is successfully inserted into the db");
                    showToast("New Quick Transfer Created");

                    dialog.dismiss();

                    refreshActivity();
                }*/
            }
        });

        //set font for all the text view
        setFont((ViewGroup) dialog.findViewById(R.id.quickTransferPopperLLId));
    }

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
                setUpCalendar();
            }
        };
    }
    //---------------------------------------Date Picker ends--------------------------------------------

    //generic method to disable all child view click for a particular view
    private void enableDisableChildViews(ViewGroup layout, boolean enable) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                enableDisableChildViews((ViewGroup) child, enable);
            } else {
                child.setEnabled(enable);
            }
        }
    }

    private Dialog.OnDismissListener dialogsDismissListener;
    {
        dialogsDismissListener = new Dialog.OnDismissListener(){

            @Override
            public void onDismiss(DialogInterface dialog) {
                isNavigation = true;
            }
        };
    }

    //this is for the list which is in the popup...the scope is in the activity itself
    private ListView.OnItemClickListener listViewClickListener;
    {
        listViewClickListener = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(CLASS_NAME, "MASTER !! you click hath " + view.getId() + ". You shalt go to the view transaction pageth");

                Object listItemObject = view.getTag();

                if(listItemObject instanceof TransactionModel){
                    showTransactionDetailsPopper((TransactionModel) listItemObject);
                }
                else if(listItemObject instanceof TransferModel){
                    showTransferDetailsPopper((TransferModel) listItemObject);
                }
            }
        };
    }

    //abstracts
    public interface ListViewItemClickListener {
        public abstract void onListItemClick(Object position);
    }

    public interface GridViewItemClickListener {
        public abstract void onGridViewItemClick(Object position);
    }
}