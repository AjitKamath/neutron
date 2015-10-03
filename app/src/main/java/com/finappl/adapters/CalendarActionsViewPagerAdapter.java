package com.finappl.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.AddUpdateAccountActivity;
import com.finappl.activities.JimBrokeItActivity;
import com.finappl.activities.LoginActivity;
import com.finappl.dbServices.AddUpdateTransactionsDbService;
import com.finappl.dbServices.AddUpdateTransfersDbService;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.Sqlite;
import com.finappl.models.AccountsModel;
import com.finappl.models.BudgetModel;
import com.finappl.models.ConsolidatedTransactionModel;
import com.finappl.models.ConsolidatedTransferModel;
import com.finappl.models.MonthLegend;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.models.UsersModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ajit on 30/9/15.
 */
public class CalendarActionsViewPagerAdapter extends PagerAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    private List<Integer> layoutsList;
    private Map<String, MonthLegend> monthLegendMap;
    private String selectedDateStr;

    //db services
    private Sqlite controller = new Sqlite(mContext);
    private CalendarDbService calendarDbService = new CalendarDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);
    private AddUpdateTransactionsDbService addUpdateTransactionsDbService = new AddUpdateTransactionsDbService(mContext);
    private AddUpdateTransfersDbService addUpdateTransfersDbService = new AddUpdateTransfersDbService(mContext);

    //User
    private UsersModel loggedInUserObj;

    //popup
    private Dialog dialog;

    public CalendarActionsViewPagerAdapter(Context context, List<Integer> layoutsList, Map<String, MonthLegend> monthLegendMap,
                                           String selectedDateStr, UsersModel loggedInUserObj) {
        this.mContext = context;
        this.layoutsList = layoutsList;
        this.monthLegendMap = monthLegendMap;
        this.selectedDateStr = selectedDateStr;
        this.loggedInUserObj = loggedInUserObj;

        controller = new Sqlite(mContext);
        calendarDbService = new CalendarDbService(mContext);
        authorizationDbService = new AuthorizationDbService(mContext);
        addUpdateTransactionsDbService = new AddUpdateTransactionsDbService(mContext);
        addUpdateTransfersDbService = new AddUpdateTransfersDbService(mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutsList.get(position), collection, false);

        switch(layoutsList.get(position)){
            case R.layout.calendar_tabs_flipper:
                setUpTabs(layout);
                break;


        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(layout, robotoCondensedLightFont);

        collection.addView(layout);

        return layout;
    }

    private void setUpTabs(final ViewGroup layout) {
        ListView consolTranLV, accountsLV, budgetsLV, schedulesLV;
        TextView calendarNoTransTV, calendarNoAccountsTV, calendarNoBudgetsTV, calendarTransHeaderOrMsgTV;
        TextView calendarSummaryTV, calendarAccountsTV, calendarBudgetsTV, calendarSchedulesTV;

        calendarNoTransTV = (TextView) layout.findViewById(R.id.calendarNoTransTVId);
        calendarNoAccountsTV = (TextView) layout.findViewById(R.id.calendarNoAccountsTVId);
        calendarNoBudgetsTV = (TextView) layout.findViewById(R.id.calendarNoBudgetsTVId);
        calendarTransHeaderOrMsgTV = (TextView) layout.findViewById(R.id.calendarTransHeaderOrMsgTVId);

        calendarSummaryTV = (TextView) layout.findViewById(R.id.calendarSummaryTVId);
        calendarAccountsTV = (TextView) layout.findViewById(R.id.calendarAccountsTVId);
        calendarBudgetsTV = (TextView) layout.findViewById(R.id.calendarBudgetsTVId);
        calendarSchedulesTV = (TextView) layout.findViewById(R.id.calendarSchedulesTVId);

        consolTranLV = (ListView) layout.findViewById(R.id.consolTranLVId);
        accountsLV = (ListView) layout.findViewById(R.id.accountsLVId);
        budgetsLV = (ListView) layout.findViewById(R.id.budgetsLVId);
        schedulesLV = (ListView) layout.findViewById(R.id.schedulesLVId);

        //summary tab
        String selectedDateStrArr[] = selectedDateStr.split("-");
        Integer tempMonth =  Integer.parseInt(selectedDateStrArr[1]);
        if(tempMonth < 10){
            selectedDateStrArr[1] = "0"+ tempMonth;
        }

        String tempDate = selectedDateStrArr[0]+"-"+selectedDateStrArr[1]+"-"+selectedDateStrArr[2];

        boolean hasScheduledActivity = true;

        //schedules tab starts
        if(monthLegendMap == null || (monthLegendMap != null && monthLegendMap.isEmpty())){
            Log.i(CLASS_NAME, "Nothing found in MonthLegend... No point in setting up Schedules. Continuing");
            return;
        }

        if(!monthLegendMap.containsKey(tempDate)){
            hasScheduledActivity = false;
        }
        else if(monthLegendMap.get(tempDate).getSummaryModel() == null){
            hasScheduledActivity = false;
        }
        else if((monthLegendMap.get(tempDate).getSummaryModel().getConsolidatedTransactionModelMap() == null
                || (monthLegendMap.get(tempDate).getSummaryModel().getConsolidatedTransactionModelMap() != null
                && monthLegendMap.get(tempDate).getSummaryModel().getConsolidatedTransactionModelMap().isEmpty()))
                && (monthLegendMap.get(tempDate).getSummaryModel().getConsolidatedTransferModelMap() == null
                || (monthLegendMap.get(tempDate).getSummaryModel().getConsolidatedTransferModelMap() != null
                && monthLegendMap.get(tempDate).getSummaryModel().getConsolidatedTransferModelMap().isEmpty()))){
            hasScheduledActivity = false;
        }

        if(!hasScheduledActivity){
            Log.i(CLASS_NAME, "Boring Day !! No summary to show today");

            calendarNoTransTV.setText("No Transactions/Transfers");
            calendarNoTransTV.setTextColor(calendarNoTransTV.getResources().getColor(R.color.DarkGray));
            consolTranLV.setVisibility(View.GONE);
        }
        else{
            ConsolidatedSummarySectionAdapter consolAdapter = new ConsolidatedSummarySectionAdapter(mContext, R.layout.calendar_summary_list_view, monthLegendMap.get(tempDate).getSummaryModel());
            consolTranLV.setAdapter(consolAdapter);
            consolTranLV.setOnItemClickListener(listViewClickListener);

            //if there are no summary on the selected date then show no transaction/transfer text
            if(consolAdapter != null & consolAdapter.getCount() != 0){
                calendarNoTransTV.setVisibility(View.GONE);
                consolTranLV.setVisibility(View.VISIBLE);
            }
            else{
                calendarNoTransTV.setVisibility(View.VISIBLE);
                consolTranLV.setVisibility(View.GONE);
            }
        }
        //summary tab ends--

        //accounts tab
        List<AccountsModel> accountsList = calendarDbService.getAllAccounts(loggedInUserObj.getUSER_ID());

        CalendarAccountsAdapter accAdapter = new CalendarAccountsAdapter(mContext, R.layout.calendar_accounts_list_view, accountsList);
        accountsLV.setAdapter(accAdapter);
        accountsLV.setOnItemClickListener(listViewClickListener);

        //if there are no accounts o the selected date then show no accounts text
        if(accAdapter != null & accAdapter.getCount() != 0){
            calendarNoAccountsTV.setVisibility(View.GONE);
            accountsLV.setVisibility(View.VISIBLE);
        }
        else{
            calendarNoAccountsTV.setVisibility(View.VISIBLE);
            accountsLV.setVisibility(View.GONE);
        }
        //accounts tab ends--

        //budgets tab starts
        List<BudgetModel> budgetsList = calendarDbService.getAllBudgets(selectedDateStr, loggedInUserObj.getUSER_ID());

        if(budgetsList != null){
            CalendarBudgetsAdapter calendarBudgetsAdapter = new CalendarBudgetsAdapter(mContext, R.layout.calendar_budgets_list_view, budgetsList);
            budgetsLV.setAdapter(calendarBudgetsAdapter);

            //if there are no transactions o the selected date then show no transaction text
            if(calendarBudgetsAdapter != null & calendarBudgetsAdapter.getCount() != 0){
                calendarNoBudgetsTV.setVisibility(View.GONE);
                budgetsLV.setVisibility(View.VISIBLE);
            }
            else{
                calendarNoBudgetsTV.setVisibility(View.VISIBLE);
                budgetsLV.setVisibility(View.GONE);
            }
        }
        //budgets tab ends--

        //schedules start
        CalendarSchedulesSectionListAdapter schedulesListAdapter = new CalendarSchedulesSectionListAdapter(mContext,  R.layout.calendar_schedules_list_view, monthLegendMap.get(tempDate));
        schedulesLV.setAdapter(schedulesListAdapter);
        schedulesListAdapter.notifyDataSetChanged();

        if(schedulesListAdapter != null & schedulesListAdapter.getCount() != 0){
            schedulesLV.setVisibility(View.VISIBLE);
            calendarTransHeaderOrMsgTV.setVisibility(View.GONE);
        }
        else{
            calendarTransHeaderOrMsgTV.setVisibility(View.VISIBLE);
            calendarTransHeaderOrMsgTV.setText("No Scheduled Transactions/Transfers");
            calendarTransHeaderOrMsgTV.setTextColor(calendarTransHeaderOrMsgTV.getResources().getColor(R.color.DarkGray));
            schedulesLV.setVisibility(View.GONE);
        }
        //schedulee tab ends--

        calendarSummaryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabSelect(v, layout);
            }
        });

        calendarAccountsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabSelect(v, layout);
            }
        });

        calendarBudgetsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabSelect(v, layout);
            }
        });

        calendarSchedulesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabSelect(v, layout);
            }
        });
    }

    public void onTabSelect(View view, ViewGroup layout){
        Log.i(CLASS_NAME, "Son....i created you. The tab implementation...although sucks, a very own of my creation. And you work !! selected: " + view.getId());

        LinearLayout calendarSummaryTabLL, calendarAccTabLL, calendarBudgetsTabLL, calendarSchedulesTabLL;
        calendarSummaryTabLL = (LinearLayout) layout.findViewById(R.id.calendarSummaryTabLLId);
        calendarAccTabLL = (LinearLayout) layout.findViewById(R.id.calendarAccTabLLId);
        calendarBudgetsTabLL = (LinearLayout) layout.findViewById(R.id.calendarBudgetsTabLLId);
        calendarSchedulesTabLL = (LinearLayout) layout.findViewById(R.id.calendarSchedulesTabLLId);
        TextView calendarSummaryTV = (TextView) layout.findViewById(R.id.calendarSummaryTVId);
        TextView calendarAccountsTV = (TextView) layout.findViewById(R.id.calendarAccountsTVId);
        TextView calendarBudgetsTV = (TextView) layout.findViewById(R.id.calendarBudgetsTVId);
        TextView calendarSchedulesTV = (TextView) layout.findViewById(R.id.calendarSchedulesTVId);

        switch(view.getId()){
            case R.id.calendarSummaryTVId: if(!"SELECTED".equalsIgnoreCase(view.getTag().toString())){
                deselectAllTabs(layout);
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
                deselectAllTabs(layout);
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
                deselectAllTabs(layout);
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
                deselectAllTabs(layout);
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

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    private void deselectAllTabs(ViewGroup layout){
        TextView calendarSummaryTV = (TextView) layout.findViewById(R.id.calendarSummaryTVId);
        TextView calendarAccountsTV = (TextView) layout.findViewById(R.id.calendarAccountsTVId);
        TextView calendarBudgetsTV = (TextView) layout.findViewById(R.id.calendarBudgetsTVId);
        TextView calendarSchedulesTV = (TextView) layout.findViewById(R.id.calendarSchedulesTVId);

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

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return layoutsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
       return mContext.getString(layoutsList.get(position));
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

    private void showTransactionsPopper(ConsolidatedTransactionModel consolidatedTransactionModelObj){
        dialog = new Dialog(mContext);
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
        setFont(summaryPopperLL, robotoCondensedLightFont);
    }

    private void showAccountPopper(final AccountsModel accountsModelObj){
        dialog = new Dialog(mContext);
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
            accountPopperBalanceTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyNegColor));
        }
        else{
            accountPopperBalanceTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyPosColor));
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
                accountPopperTransactionAmtTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyNegColor));
            }
            else{
                accountPopperTransactionAmtTV.setText(String.valueOf(transactionModelObj.getTRAN_AMT()));
                accountPopperTransactionAmtTV.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyPosColor));
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
                Intent intent = new Intent(mContext, AddUpdateAccountActivity.class);
                intent.putExtra("ACCOUNT_OBJ", accountsModelObj);
                mContext.startActivity(intent);
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
        dialog = new Dialog(mContext);
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
        setFont(summaryPopperLL, robotoCondensedLightFont);
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
                    showTransactionsPopper((ConsolidatedTransactionModel) listItemObject);
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

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
