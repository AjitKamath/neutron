package com.finappl.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.HomeActivity;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.models.AccountMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.RepeatMO;
import com.finappl.models.SpentOnMO;
import com.finappl.models.TransactionMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.IdGenerator;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.finappl.utils.Constants.ACCOUNT_LOW_BALANCE_LIMIT;
import static com.finappl.utils.Constants.ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.CATEGORY_OBJECT;
import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.DB_AFFIRMATIVE;
import static com.finappl.utils.Constants.DB_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.DB_TIME_FORMAT_SDF;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_TRANSACTION;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_ACCOUNT;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_AMOUNT;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_CATEGORY;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_REPEAT;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_SPENTON;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.REPEAT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_AMOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_CATEGORY_OBJECT;
import static com.finappl.utils.Constants.SELECTED_REPEAT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_SPENTON_OBJECT;
import static com.finappl.utils.Constants.SPENTON_OBJECT;
import static com.finappl.utils.Constants.TRANSACTION_OBJECT;
import static com.finappl.utils.Constants.UI_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UI_TIME_FORMAT_SDF;

/**
 * Created by ajit on 21/3/16.
 */
public class AddUpdateTransactionFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    private RelativeLayout addUpdateTransactionRL;
    private TextView addUpdateDateTV;
    private TextView transactionHeaderCloseTV;
    private EditText addUpdateTranNameET;
    private TextView transactionContentCurrencyTV;
    private TextView transactionContentAmountTV;
    private ScrollView addUpdatePageSV;
    private LinearLayout transactionContentCategoryLL;
    private LinearLayout transactionContentAccountLL;
    private LinearLayout transactionContentSpentonLL;
    private TextView transactionContentAccountTotalTV;
    private TextView transactionContentAccountStatusTV;
    private RadioGroup addUpdateTranExpIncRadioGrp;
    private EditText addUpdateNoteET;
    private TextView transactionHeaderSaveTV;
    private LinearLayout transactionContentRepeatLL;
    private Switch transactionContentRepeatSwitch;
    private View transactionContentNotifyDivider;
    private LinearLayout transactionContentNotifyLL;
    private RadioGroup transactionContentNotifyRG;
    private TextView transactionContentNotifyAddTimeTV;
    private TextView transactionContentAutoAddTimeTV;
    private View transactionContentScheduleDivider;
    private LinearLayout transactionContentScheduleLL;
    private TextView transactionContentScheduleUptoDateTV;

    private UserMO loggedInUserObj;

    private CalendarDbService calendarDbService;
    private TransactionsDbService transactionsDbService;

    private List<CategoryMO> categoriesList;
    private List<AccountMO> accountList;
    private List<SpentOnMO> spentOnList;

    private List<RepeatMO> repeatsList;

    private TransactionMO transactionModelObj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_transaction, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    public void getInputs(){
        try{
            transactionModelObj.setTRAN_DATE(UI_DATE_FORMAT_SDF.parse(String.valueOf(addUpdateDateTV.getText())));
        }
        catch (ParseException pe){
            Log.e(CLASS_NAME, "Parse Exception "+pe);
            return ;
        }

        transactionModelObj.setTRAN_AMT(Double.parseDouble(String.valueOf(transactionContentAmountTV.getText()).replaceAll(",", "")));
        transactionModelObj.setTRAN_NAME(String.valueOf(addUpdateTranNameET.getText()));

        transactionModelObj.setCAT_ID(((CategoryMO)transactionContentCategoryLL.getTag()).getCAT_ID());
        transactionModelObj.setACC_ID(((AccountMO)transactionContentAccountLL.getTag()).getACC_ID());
        transactionModelObj.setSPNT_ON_ID(((SpentOnMO)transactionContentSpentonLL.getTag()).getSPNT_ON_ID());

        transactionModelObj.setTRAN_TYPE(String.valueOf(getView().findViewById(addUpdateTranExpIncRadioGrp.getCheckedRadioButtonId()).getTag()));
        transactionModelObj.setTRAN_NOTE(String.valueOf(addUpdateNoteET.getText()));
        transactionModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());

        if(transactionContentRepeatSwitch.isChecked()){
            transactionModelObj.setREPEAT_ID(((RepeatMO)transactionContentRepeatLL.getTag()).getREPEAT_ID());
            transactionModelObj.setNOTIFY(String.valueOf(getView().findViewById(transactionContentNotifyRG.getCheckedRadioButtonId()).getTag()));

            if(R.id.transactionContentNotifyAddRBId == transactionContentNotifyRG.getCheckedRadioButtonId()){
                transactionModelObj.setNOTIFY_TIME(String.valueOf(transactionContentNotifyAddTimeTV.getText()));
            }
            else if(R.id.transactionContentAutoAddRBId == transactionContentNotifyRG.getCheckedRadioButtonId()){
                transactionModelObj.setNOTIFY_TIME(String.valueOf(transactionContentAutoAddTimeTV.getText()));
            }

            transactionModelObj.setSCHD_UPTO_DATE(String.valueOf(transactionContentScheduleUptoDateTV.getText()));
        }
    }

    private void getDataFromBundle() {
        transactionModelObj = (TransactionMO) getArguments().get(TRANSACTION_OBJECT);
        loggedInUserObj = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        //common setup
        getMasterData();

        //set Currency Code
        transactionContentCurrencyTV.setText(loggedInUserObj.getCUR_CODE());

        String dateStr = (UI_DATE_FORMAT_SDF.format(transactionModelObj.getTRAN_DATE())).toUpperCase();
        addUpdateDateTV.setText(dateStr);

        //hide repeat by default
        transactionContentRepeatLL.setVisibility(View.GONE);
        transactionContentNotifyDivider.setVisibility(View.GONE);
        transactionContentNotifyLL.setVisibility(View.GONE);
        transactionContentScheduleDivider.setVisibility(View.GONE);
        transactionContentScheduleLL.setVisibility(View.GONE);

        if(transactionModelObj.getTRAN_ID() != null){
            //set name
            addUpdateTranNameET.setText(transactionModelObj.getTRAN_NAME());

            //set select_amount
            transactionContentAmountTV.setText(FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), String.valueOf(transactionModelObj.getTRAN_AMT())));

            //set select_category_category
            setCategory(getCategoryOnId(categoriesList, transactionModelObj.getCAT_ID()));

            //set Account
            setAccount(getAccountOnId(accountList, transactionModelObj.getACC_ID()));

            //set Spent On
            setSpenton(getSpentonOnId(spentOnList, transactionModelObj.getSPNT_ON_ID()));

            //set add_update_transaction type
            if("EXPENSE".equalsIgnoreCase(transactionModelObj.getTRAN_TYPE())){
                addUpdateTranExpIncRadioGrp.check(addUpdateTransactionRL.findViewWithTag("EXPENSE").getId());
            }
            else{
                addUpdateTranExpIncRadioGrp.check(addUpdateTransactionRL.findViewWithTag("INCOME").getId());
            }

            //set notes
            addUpdateNoteET.setText(transactionModelObj.getTRAN_NOTE());

            //repeat
            if(transactionModelObj.getREPEAT_ID() != null && !transactionModelObj.getREPEAT_ID().isEmpty()){
                transactionContentRepeatSwitch.setChecked(true);

                //set repeat
                setRepeat(getRepeatOnId(repeatsList, transactionModelObj.getREPEAT_ID()));

                //set notify & add
                if("NOTIFY".equalsIgnoreCase(transactionModelObj.getNOTIFY())){
                    transactionContentNotifyRG.check(addUpdateTransactionRL.findViewWithTag("NOTIFY").getId());
                    transactionContentNotifyAddTimeTV.setText(transactionModelObj.getNOTIFY_TIME());
                }
                else{
                    transactionContentNotifyRG.check(addUpdateTransactionRL.findViewWithTag("AUTO").getId());
                    transactionContentAutoAddTimeTV.setText(transactionModelObj.getNOTIFY_TIME());
                }

                //schedule untill
                transactionContentScheduleUptoDateTV.setText(transactionModelObj.getSCHD_UPTO_DATE());
            }
        }
        else{
            //set default select_category_category to be set
            setCategory(getDefaultCategory(categoriesList));

            //set default calendar_tab_account_delete to be set
            setAccount(getDefaultAccount(accountList));

            //set default select_spenton_spenton to be set
            setSpenton(getDefaultSpenton(spentOnList));

            //set default Repeat to be set
            setRepeat(getDefaultRepeat(repeatsList));

            //REPEAT
            transactionContentRepeatLL.setVisibility(View.GONE);
            transactionContentNotifyDivider.setVisibility(View.GONE);
            transactionContentNotifyLL.setVisibility(View.GONE);

            //NOTIFY
            transactionContentNotifyAddTimeTV.setVisibility(View.VISIBLE);
            transactionContentAutoAddTimeTV.setVisibility(View.INVISIBLE);

            //SCHEDULE
            transactionContentScheduleDivider.setVisibility(View.GONE);
            transactionContentScheduleLL.setVisibility(View.GONE);
        }
    }

    private void saveUpdateTransaction(){
        String messageStr = "Error !!";

        getInputs();

        if(transactionModelObj == null){
            showToast("Error !!");
            return;
        }
        else if(transactionModelObj.getTRAN_AMT() == null || transactionModelObj.getTRAN_AMT().equals(0.0)){
            showToast("Enter Transaction Amount");
            return;
        }
        else if(transactionModelObj.getTRAN_NAME() == null || transactionModelObj.getTRAN_NAME().trim().isEmpty()){
            showToast("Enter Transaction Title");
            return;
        }

         //if transactionModelObj contains transactionId, then its an update. if not its a new add_update_transaction
         if(transactionModelObj.getTRAN_ID() == null) {
             transactionModelObj.setTRAN_ID(IdGenerator.getInstance().generateUniqueId("TRAN"));
             long result = transactionsDbService.addNewTransaction(transactionModelObj);

             if (result == -1) {
                 messageStr = "Failed to create a new Transaction !";
             } else {
                 messageStr = "New Transaction created";
             }
         }
         else{
            long result = transactionsDbService.updateOldTransaction(transactionModelObj);
            if(result == 0) {
                messageStr = "Failed to update Transaction/Account !";
            } else if(result == 1){
                messageStr = "Transaction updated";
            }
            else{
                messageStr = "Unknown error !";
            }
         }

        closeFragment(messageStr);
    }

    private void closeFragment(String messageStr){
        dismiss();

        FinappleUtility.showSnacks(getActivity().getCurrentFocus(), "Transaction saved", OK, Snackbar.LENGTH_LONG);
        ((HomeActivity)getActivity()).updateCalendarMonths();
    }

    private void getMasterData() {
        categoriesList = calendarDbService.getAllCategories(loggedInUserObj.getUSER_ID());
        accountList = calendarDbService.getAllAccounts(loggedInUserObj.getUSER_ID());
        spentOnList = calendarDbService.getAllSpentOns(loggedInUserObj.getUSER_ID());

        repeatsList = calendarDbService.getAllRepeats();
    }

    private void showTransactionDatePicker(int year, int month, int day) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month-1);
        args.putInt("calendar_day__", day);
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(tranDateListener);
        date.show(getFragmentManager(), "Date Picker");
    }

    private void showScheduleUptoDatePicker(int year, int month, int day) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month-1);
        args.putInt("calendar_day__", day);
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(scheduleUptoDateListener);
        date.show(getFragmentManager(), "Date Picker");
    }

    private void showNotifyTimePicker(int hour, int minute) {
        TimePickerFragment time = new TimePickerFragment();

        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        time.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        time.setCallBack(notifyTimeListener);
        time.show(getFragmentManager(), "Time Picker");
    }

    private void showAutoAddTimePicker(int hour, int minute) {
        TimePickerFragment time = new TimePickerFragment();

        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        time.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        time.setCallBack(autoAddTimeListener);
        time.show(getFragmentManager(), "Time Picker");
    }

    private void initComps(View view){
        addUpdateTransactionRL = (RelativeLayout) view.findViewById(R.id.addUpdateTransactionRLId);
        addUpdateDateTV = (TextView) view.findViewById(R.id.addUpdateDateTVId);
        transactionHeaderCloseTV = (TextView) view.findViewById(R.id.transactionHeaderCloseTVId);
        addUpdateTranNameET = (EditText) view.findViewById(R.id.addUpdateTranNameETId);
        transactionContentCurrencyTV = (TextView) view.findViewById(R.id.transactionContentCurrencyTVId);
        transactionContentAmountTV = (TextView) view.findViewById(R.id.transactionContentAmountTVId);

        addUpdatePageSV = (ScrollView) view.findViewById(R.id.addUpdatePageSVId);

        transactionContentCategoryLL = (LinearLayout) view.findViewById(R.id.transactionContentCategoryLLId);
        transactionContentAccountLL = (LinearLayout) view.findViewById(R.id.transactionContentAccountLLId);
        transactionContentSpentonLL = (LinearLayout) view.findViewById(R.id.transactionContentSpentonLLId);

        transactionContentAccountTotalTV = (TextView) view.findViewById(R.id.transactionContentAccountTotalTVId);
        transactionContentAccountStatusTV = (TextView) view.findViewById(R.id.transactionContentAccountStatusTVId);

        addUpdateTranExpIncRadioGrp = (RadioGroup) view.findViewById(R.id.addUpdateTranExpIncRadioGrpId);
        addUpdateNoteET = (EditText) view.findViewById(R.id.addUpdateNoteETId);
        transactionHeaderSaveTV = (TextView) view.findViewById(R.id.transactionHeaderSaveTVId);

        transactionContentRepeatLL = (LinearLayout) view.findViewById(R.id.transactionContentRepeatLLId);
        transactionContentRepeatSwitch = (Switch) view.findViewById(R.id.transactionContentRepeatSwitchId);

        transactionContentNotifyDivider = view.findViewById(R.id.transactionContentNotifyDividerId);
        transactionContentNotifyLL = (LinearLayout) view.findViewById(R.id.transactionContentNotifyLLId);

        transactionContentNotifyRG = (RadioGroup) view.findViewById(R.id.transactionContentNotifyRGId);

        transactionContentNotifyAddTimeTV = (TextView) view.findViewById(R.id.transactionContentNotifyAddTimeTVId);
        transactionContentAutoAddTimeTV = (TextView) view.findViewById(R.id.transactionContentAutoAddTimeTVId);

        transactionContentScheduleDivider = view.findViewById(R.id.transactionContentScheduleDividerId);
        transactionContentScheduleLL = (LinearLayout) view.findViewById(R.id.transactionContentScheduleLLId);

        transactionContentScheduleUptoDateTV = (TextView) view.findViewById(R.id.transactionContentScheduleUptoDateTVId);

        addUpdateDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                        String dateStr = DB_DATE_FORMAT_SDF.format(UI_DATE_FORMAT_SDF.parse(String.valueOf(((TextView)v).getText())));
                        String dateStrArr[] = dateStr.split("-");

                        int year = Integer.parseInt(dateStrArr[0]);
                        int month = Integer.parseInt(dateStrArr[1]);
                        int day = Integer.parseInt(dateStrArr[2]);

                        showTransactionDatePicker(year, month, day);

                    }
                    catch(ParseException e){
                        Log.e(CLASS_NAME, "Error while parsing the date : "+String.valueOf(((TextView)v).getText())+ " : "+e);
                    }

            }
        });

        transactionContentScheduleUptoDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String dateStr = String.valueOf(((TextView)v).getText());

                    if("FOREVER".equalsIgnoreCase(dateStr)){
                        dateStr = DB_DATE_FORMAT_SDF.format(UI_DATE_FORMAT_SDF.parse(String.valueOf(((TextView)v).getText())));
                    }
                    else{
                        dateStr = DB_DATE_FORMAT_SDF.format(UI_DATE_FORMAT_SDF.parse(String.valueOf(((TextView)v).getText())));
                    }

                    String dateStrArr[] = dateStr.split("-");

                    int year = Integer.parseInt(dateStrArr[0]);
                    int month = Integer.parseInt(dateStrArr[1]);
                    int day = Integer.parseInt(dateStrArr[2]);

                    showScheduleUptoDatePicker(year, month, day);
                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "Error while parsing the date : "+String.valueOf(((TextView)v).getText())+ " : "+e);
                }

            }
        });

        transactionContentNotifyAddTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String timeStr = DB_TIME_FORMAT_SDF.format(UI_TIME_FORMAT_SDF.parse(String.valueOf(((TextView)v).getText())));
                    String timeStrArr[] = timeStr.split(":");

                    int hour = Integer.parseInt(timeStrArr[0]);
                    int minute = Integer.parseInt(timeStrArr[1]);

                    showNotifyTimePicker(hour, minute);

                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "Error while parsing the time : "+String.valueOf(((TextView)v).getText())+ " : "+e);
                }

            }
        });

        transactionContentAutoAddTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String timeStr = DB_TIME_FORMAT_SDF.format(UI_TIME_FORMAT_SDF.parse(String.valueOf(((TextView)v).getText())));
                    String timeStrArr[] = timeStr.split(":");

                    int hour = Integer.parseInt(timeStrArr[0]);
                    int minute = Integer.parseInt(timeStrArr[1]);

                    showAutoAddTimePicker(hour, minute);

                }
                catch(ParseException e){
                    Log.e(CLASS_NAME, "Error while parsing the time : "+String.valueOf(((TextView)v).getText())+ " : "+e);
                }

            }
        });

        transactionHeaderSaveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUpdateTransaction();
            }
        });

        transactionHeaderCloseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInputs();

                boolean hasInput = false;
                if(transactionModelObj != null){
                    if(transactionModelObj.getTRAN_NAME() != null && !transactionModelObj.getTRAN_NAME().trim().isEmpty()){
                        hasInput = true;
                    }
                    else if(transactionModelObj.getTRAN_AMT() != null && !transactionModelObj.getTRAN_AMT().equals(0.0)){
                        hasInput = true;
                    }
                }

                if(hasInput){
                    showConfirmCloseFragment();
                }
                else{
                    closeFragment(null);
                }
            }
        });

        transactionContentAmountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAmountFragment();
            }
        });
        transactionContentCategoryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryFragment();
            }
        });
        transactionContentAccountLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccountFragment();
            }
        });
        transactionContentSpentonLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpentonFragment();
            }
        });
        transactionContentRepeatLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRepeatFragment();
            }
        });

        transactionContentRepeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    transactionContentRepeatLL.setVisibility(View.VISIBLE);
                    transactionContentNotifyDivider.setVisibility(View.VISIBLE);
                    transactionContentNotifyLL.setVisibility(View.VISIBLE);
                    transactionContentScheduleDivider.setVisibility(View.VISIBLE);
                    transactionContentScheduleLL.setVisibility(View.VISIBLE);

                    addUpdatePageSV.post(new Runnable() { public void run() { addUpdatePageSV.fullScroll(View.FOCUS_DOWN); } });
                }
                else{
                    addUpdatePageSV.post(new Runnable() { public void run() { addUpdatePageSV.fullScroll(View.FOCUS_UP); } });

                    transactionContentRepeatLL.setVisibility(View.GONE);
                    transactionContentNotifyDivider.setVisibility(View.GONE);
                    transactionContentNotifyLL.setVisibility(View.GONE);
                    transactionContentScheduleDivider.setVisibility(View.GONE);
                    transactionContentScheduleLL.setVisibility(View.GONE);
                }
            }
        });

        transactionContentNotifyRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(R.id.transactionContentNotifyAddRBId == checkedId){
                    transactionContentNotifyAddTimeTV.setVisibility(View.VISIBLE);
                    transactionContentAutoAddTimeTV.setVisibility(View.INVISIBLE);
                }
                else if(R.id.transactionContentAutoAddRBId == checkedId){
                    transactionModelObj.setNOTIFY_TIME(String.valueOf(transactionContentAutoAddTimeTV.getText()));
                    transactionContentNotifyAddTimeTV.setVisibility(View.INVISIBLE);
                    transactionContentAutoAddTimeTV.setVisibility(View.VISIBLE);
                }
            }
        });

        setFont(addUpdateTransactionRL);
    }

    private AccountMO getAccountOnId(List<AccountMO> accountList, String accountIdStr){
        for(AccountMO iterList : accountList){
            if(iterList.getACC_ID().equalsIgnoreCase(accountIdStr)){
                return iterList;
            }
        }
        return null;
    }

    private CategoryMO getCategoryOnId(List<CategoryMO> categoriesList, String cateforyIdStr){
        for(CategoryMO iterList : categoriesList){
            if(iterList.getCAT_ID().equalsIgnoreCase(cateforyIdStr)){
                return iterList;
            }
        }
        return null;
    }

    private SpentOnMO getSpentonOnId(List<SpentOnMO> spentOnList, String spentonIdStr){
        for(SpentOnMO iterList : spentOnList){
            if(iterList.getSPNT_ON_ID().equalsIgnoreCase(spentonIdStr)){
                return iterList;
            }
        }
        return null;
    }

    private RepeatMO getRepeatOnId(List<RepeatMO> repeatMOList, String repeatIdStr){
        for(RepeatMO iterList : repeatMOList){
            if(iterList.getREPEAT_ID().equalsIgnoreCase(repeatIdStr)){
                return iterList;
            }
        }
        return null;
    }

    private AccountMO getDefaultAccount(List<AccountMO> accountList){
        for(AccountMO iterList : accountList){
            if(iterList.getACC_IS_DEF().equalsIgnoreCase(DB_AFFIRMATIVE)){
                return iterList;
            }
        }
        return null;
    }

    private CategoryMO getDefaultCategory(List<CategoryMO> categoriesList){
        for(CategoryMO iterList : categoriesList){
            if(iterList.getCAT_IS_DEF().equalsIgnoreCase(DB_AFFIRMATIVE)){
                return iterList;
            }
        }
        return null;
    }

    private SpentOnMO getDefaultSpenton(List<SpentOnMO> spentOnList){
        for(SpentOnMO iterList : spentOnList){
            if(iterList.getSPNT_ON_IS_DEF().equalsIgnoreCase(DB_AFFIRMATIVE)){
                return iterList;
            }
        }
        return null;
    }

    private RepeatMO getDefaultRepeat(List<RepeatMO> repeatMOList){
        for(RepeatMO iterList : repeatMOList){
            if(iterList.getREPEAT_IS_DEF().equalsIgnoreCase(DB_AFFIRMATIVE)){
                return iterList;
            }
        }
        return null;
    }

    private void showAmountFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_AMOUNT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_AMOUNT_OBJECT, String.valueOf(transactionContentAmountTV.getText()));
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSACTION);

        SelectAmountFragment fragment = new SelectAmountFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_AMOUNT);
    }

    private void showRepeatFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_REPEAT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(REPEAT_OBJECT, (Serializable) repeatsList);
        bundle.putSerializable(SELECTED_REPEAT_OBJECT, ((RepeatMO)transactionContentRepeatLL.getTag()).getREPEAT_NAME());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSACTION);

        SelectRepeatFragment fragment = new SelectRepeatFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_REPEAT);
    }


    private void showCategoryFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_CATEGORY);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CATEGORY_OBJECT, (Serializable) categoriesList);
        bundle.putSerializable(SELECTED_CATEGORY_OBJECT, ((CategoryMO)transactionContentCategoryLL.getTag()).getCAT_ID());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSACTION);

        SelectCategoryFragment fragment = new SelectCategoryFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_CATEGORY);
    }

    private void showAccountFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_ACCOUNT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(ACCOUNT_OBJECT, (Serializable) accountList);
        bundle.putSerializable(SELECTED_ACCOUNT_OBJECT, ((AccountMO)transactionContentAccountLL.getTag()).getACC_ID());
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSACTION);

        SelectAccountFragment fragment = new SelectAccountFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_ACCOUNT);
    }

    private void showSpentonFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_SPENTON);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SPENTON_OBJECT, (Serializable) spentOnList);
        bundle.putSerializable(SELECTED_SPENTON_OBJECT, ((SpentOnMO)transactionContentSpentonLL.getTag()).getSPNT_ON_ID());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSACTION);

        SelectSpentonFragment fragment = new SelectSpentonFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_SPENTON);
    }

    private void showConfirmCloseFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFIRM_MESSAGE, "Discard Transaction ?");

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSACTION);

        ConfirmFragment fragment = new ConfirmFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_CONFIRM);
    }

    private DatePickerDialog.OnDateSetListener tranDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            setDate(year, month+1, day, "TRANSACTION_DATE");
        }
    };

    private DatePickerDialog.OnDateSetListener scheduleUptoDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            setDate(year, month+1, day, "SCHEDULE_UPTO");
        }
    };

    private TimePickerDialog.OnTimeSetListener notifyTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker arg0, int hour, int minute) {
            setTime(hour, minute, "NOTIFY_TIME");
        }
    };

    private TimePickerDialog.OnTimeSetListener autoAddTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker arg0, int hour, int minute) {
            setTime(hour, minute, "AUTO_ADD_TIME");
        }
    };

    private void setDate(int year, int month, int day, String uiComponentStr) {
        try {
            String dateStr = year + "-" + month + "-" + day;
            dateStr = UI_DATE_FORMAT_SDF.format(DB_DATE_FORMAT_SDF.parse(dateStr));

            if("TRANSACTION_DATE".equalsIgnoreCase(uiComponentStr)){
                addUpdateDateTV.setText(dateStr.toUpperCase());
            }
            else if("SCHEDULE_UPTO".equalsIgnoreCase(uiComponentStr)){
                String fromDateStr = String.valueOf(addUpdateDateTV.getText());

                Date fromDate = UI_DATE_FORMAT_SDF.parse(fromDateStr);
                Date uptoDate = UI_DATE_FORMAT_SDF.parse(dateStr);

                if(uptoDate.before(fromDate)){
                    showToast("Schedule Upto cannot be before Transaction date");
                    return;
                }

                transactionContentScheduleUptoDateTV.setText(dateStr.toUpperCase());
            }
        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "Error in parsing the date : "+e);
        }
    }

    private void setTime(int hour, int minute, String uiComponentStr) {
        try {
            String timeStr = hour + ":" + minute;
            timeStr = UI_TIME_FORMAT_SDF.format(DB_TIME_FORMAT_SDF.parseObject(timeStr));

            if("NOTIFY_TIME".equalsIgnoreCase(uiComponentStr)){
                transactionContentNotifyAddTimeTV.setText(timeStr.toUpperCase());
            }
            if("AUTO_ADD_TIME".equalsIgnoreCase(uiComponentStr)){
                transactionContentAutoAddTimeTV.setText(timeStr.toUpperCase());
            }
        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "Error in parsing the time : "+e);
        }
    }

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    // Empty constructor required for DialogFragment
    public AddUpdateTransactionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        initDb();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);

            addUpdateTranNameET.requestFocus();
        }
    }

    private void initDb() {
        calendarDbService = new CalendarDbService(mContext);
        transactionsDbService = new TransactionsDbService(mContext);
    }

    public void onFinishDialog(CategoryMO categoryMO) {
        setCategory(categoryMO);
    }
    public void onFinishDialog(AccountMO accountsMO) {
        setAccount(accountsMO);
    }
    public void onFinishDialog(SpentOnMO spentOnMO) {
        setSpenton(spentOnMO);
    }
    public void onFinishDialog(RepeatMO repeatMO) {
        setRepeat(repeatMO);
    }
    public void onFinishDialog(String amountStr) {
        setAmount(amountStr);
    }
    public void onFinishDialog() {
        closeFragment(null);
    }

    private void setCategory(CategoryMO categoryMO){
        ((TextView)transactionContentCategoryLL.findViewById(R.id.transactionContentCategoryTVId)).setText(categoryMO.getCAT_NAME());
        transactionContentCategoryLL.findViewById(R.id.transactionContentCategoryIVId).setBackgroundResource(Integer.parseInt(categoryMO.getCAT_IMG()));
        transactionContentCategoryLL.setTag(categoryMO);
    }

    private void setAccount(AccountMO accountsMO){
        ((TextView)transactionContentAccountLL.findViewById(R.id.transactionContentAccountTVId)).setText(accountsMO.getACC_NAME());
        transactionContentAccountLL.findViewById(R.id.transactionContentAccountIVId).setBackgroundResource(Integer.parseInt(accountsMO.getACC_IMG()));

        transactionContentAccountTotalTV = FinappleUtility.formatAmountView(transactionContentAccountTotalTV, loggedInUserObj, accountsMO.getACC_TOTAL());

        if(accountsMO.getACC_TOTAL() < ACCOUNT_LOW_BALANCE_LIMIT){
            transactionContentAccountStatusTV.setVisibility(View.VISIBLE);
        }
        else{
            transactionContentAccountStatusTV.setVisibility(View.GONE);
        }

        transactionContentAccountLL.setTag(accountsMO);
    }

    private void setSpenton(SpentOnMO spentOnMO){
        ((TextView)transactionContentSpentonLL.findViewById(R.id.transactionContentSpentonTVId)).setText(spentOnMO.getSPNT_ON_NAME());
        transactionContentSpentonLL.findViewById(R.id.transactionContentSpentonIVId).setBackgroundResource(Integer.parseInt(spentOnMO.getSPNT_ON_IMG()));

        transactionContentSpentonLL.setTag(spentOnMO);
    }
    private void setRepeat(RepeatMO repeatMo){
        ((TextView)transactionContentRepeatLL.findViewById(R.id.transactionContentRepeatTVId)).setText(repeatMo.getREPEAT_NAME());
        
        transactionContentRepeatLL.setTag(repeatMo);
    }

    private void setAmount(String amountStr){
        transactionContentAmountTV.setText(amountStr);
    }

    public interface DialogResultListener {
        void onFinishUserDialog(String str);
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