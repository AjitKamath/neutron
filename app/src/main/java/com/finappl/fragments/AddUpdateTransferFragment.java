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
import com.finappl.dbServices.TransfersDbService;
import com.finappl.models.AccountMO;
import com.finappl.models.RepeatMO;
import com.finappl.models.TransferMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.IdGenerator;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.ACCOUNT_LOW_BALANCE_LIMIT;
import static com.finappl.utils.Constants.ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.ACCOUNT_TYPE_FLAG;
import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.DB_AFFIRMATIVE;
import static com.finappl.utils.Constants.DB_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.DB_TIME_FORMAT_SDF;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_TRANSFER;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_ACCOUNT;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_AMOUNT;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_REPEAT;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.REPEAT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_AMOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_REPEAT_OBJECT;
import static com.finappl.utils.Constants.TRANSFER_OBJECT;
import static com.finappl.utils.Constants.UI_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UI_TIME_FORMAT_SDF;

/**
 * Created by ajit on 21/3/16.
 */
public class AddUpdateTransferFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    @InjectView(R.id.transferRLId)
    RelativeLayout transferRL;

    @InjectView(R.id.transferDateTVId)
    TextView transferDateTV;

    @InjectView(R.id.transferSVId)
    ScrollView transferSV;

    @InjectView(R.id.transferCurrencyTVId)
    TextView transferCurrencyTV;

    @InjectView(R.id.transferAmountTVId)
    TextView transferAmountTV;

    @InjectView(R.id.transferFromAccountLLId)
    LinearLayout transferFromAccountLL;

    @InjectView(R.id.transferFromAccountTotalTVId)
    TextView transferFromAccountTotalTV;

    @InjectView(R.id.transferToAccountLLId)
    LinearLayout transferToAccountLL;

    @InjectView(R.id.transferToAccountTotalTVId)
    TextView transferToAccountTotalTV;

    @InjectView(R.id.transferNoteETId)
    EditText transferNoteET;

    @InjectView(R.id.transferRepeatSwitchId)
    Switch transferRepeatSwitch;

    @InjectView(R.id.transferRepeatLLId)
    LinearLayout transferRepeatLL;

    @InjectView(R.id.transferNotifyDividerId)
    View transferNotifyDivider;

    @InjectView(R.id.transferNotifyLLId)
    LinearLayout transferNotifyLL;

    @InjectView(R.id.transferScheduleDividerId)
    View transferScheduleDivider;

    @InjectView(R.id.transferNotifyRGId)
    RadioGroup transferNotifyRG;

    @InjectView(R.id.transferNotifyAddTimeTVId)
    TextView transferNotifyAddTimeTV;

    @InjectView(R.id.transferAutoAddTimeTVId)
    TextView transferAutoAddTimeTV;

    @InjectView(R.id.transferScheduleLLId)
    LinearLayout transferScheduleLL;

    @InjectView(R.id.transferScheduleUptoDateTVId)
    TextView transferScheduleUptoDateTV;
    //end of components

    private UserMO loggedInUserObj;

    private CalendarDbService calendarDbService;
    private TransfersDbService transfersDbService;

    private List<AccountMO> accountList;

    private List<RepeatMO> repeatsList;

    private TransferMO transfer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_transfer, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    public void getInputs(){
        try{
            transfer.setTRNFR_DATE(UI_DATE_FORMAT_SDF.parse(String.valueOf(transferDateTV.getText())));
        }
        catch (ParseException pe){
            Log.e(CLASS_NAME, "Parse Exception "+pe);
            return ;
        }

        transfer.setTRNFR_AMT(Double.parseDouble(String.valueOf(transferAmountTV.getText()).replaceAll(",", "")));
        transfer.setACC_ID_FRM(((AccountMO)transferFromAccountLL.getTag()).getACC_ID());
        transfer.setACC_ID_TO(((AccountMO)transferToAccountLL.getTag()).getACC_ID());
        transfer.setTRNFR_NOTE(String.valueOf(transferNoteET.getText()));
        transfer.setUSER_ID(loggedInUserObj.getUSER_ID());

        if(transferRepeatSwitch.isChecked()){
            transfer.setREPEAT_ID(((RepeatMO)transferRepeatLL.getTag()).getREPEAT_ID());
            transfer.setNOTIFY(String.valueOf(getView().findViewById(transferNotifyRG.getCheckedRadioButtonId()).getTag()));

            if(R.id.transferNotifyAddRBId == transferNotifyRG.getCheckedRadioButtonId()){
                transfer.setNOTIFY_TIME(String.valueOf(transferNotifyAddTimeTV.getText()));
            }
            else if(R.id.transferAutoAddRBId == transferNotifyRG.getCheckedRadioButtonId()){
                transfer.setNOTIFY_TIME(String.valueOf(transferAutoAddTimeTV.getText()));
            }

            transfer.setSCHD_UPTO_DATE(String.valueOf(transferScheduleUptoDateTV.getText()));
        }
    }

    private void getDataFromBundle() {
        transfer = (TransferMO) getArguments().get(TRANSFER_OBJECT);
        loggedInUserObj = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        //common setup
        getMasterData();

        //set Currency Code
        transferCurrencyTV.setText(loggedInUserObj.getCUR_CODE());

        String dateStr = (UI_DATE_FORMAT_SDF.format(transfer.getTRNFR_DATE())).toUpperCase();
        transferDateTV.setText(dateStr);

        //hide repeat by default
        transferRepeatLL.setVisibility(View.GONE);
        transferNotifyDivider.setVisibility(View.GONE);
        transferNotifyLL.setVisibility(View.GONE);
        transferScheduleDivider.setVisibility(View.GONE);
        transferScheduleLL.setVisibility(View.GONE);

        if(transfer.getTRNFR_ID() != null){
            //set select_amount
            transferAmountTV.setText(FinappleUtility.formatAmount(loggedInUserObj.getMETRIC(), String.valueOf(transfer.getTRNFR_AMT())));

            //set From Account
            setFromAccount(getAccountOnId(accountList, transfer.getACC_ID_FRM()));

            //set To Account
            setToAccount(getAccountOnId(accountList, transfer.getACC_ID_TO()));

            //set notes
            transferNoteET.setText(transfer.getTRNFR_NOTE());

            //repeat
            if(transfer.getREPEAT_ID() != null && !transfer.getREPEAT_ID().isEmpty()){
                transferRepeatSwitch.setChecked(true);

                //set repeat
                setRepeat(getRepeatOnId(repeatsList, transfer.getREPEAT_ID()));

                //set notify & add
                if("NOTIFY".equalsIgnoreCase(transfer.getNOTIFY())){
                    transferNotifyRG.check(transferRL.findViewWithTag("NOTIFY").getId());
                    transferNotifyAddTimeTV.setText(transfer.getNOTIFY_TIME());
                }
                else{
                    transferNotifyRG.check(transferRL.findViewWithTag("AUTO").getId());
                    transferAutoAddTimeTV.setText(transfer.getNOTIFY_TIME());
                }

                //schedule until
                transferScheduleUptoDateTV.setText(transfer.getSCHD_UPTO_DATE());
            }
        }
        else{
            //set default from account to be set
            setFromAccount(getDefaultAccount(accountList));

            //set default to account to be set
            setToAccount(getDefaultAccount(accountList));

            //set default Repeat to be set
            setRepeat(getDefaultRepeat(repeatsList));

            //REPEAT
            transferRepeatLL.setVisibility(View.GONE);
            transferNotifyDivider.setVisibility(View.GONE);
            transferNotifyLL.setVisibility(View.GONE);

            //NOTIFY
            transferNotifyAddTimeTV.setVisibility(View.VISIBLE);
            transferAutoAddTimeTV.setVisibility(View.INVISIBLE);

            //SCHEDULE
            transferScheduleDivider.setVisibility(View.GONE);
            transferScheduleLL.setVisibility(View.GONE);
        }
    }


    private void closeFragment(String messageStr){
        dismiss();

        FinappleUtility.showSnacks(getActivity().getCurrentFocus(), "Transfer saved", OK, Snackbar.LENGTH_LONG);
        ((HomeActivity)getActivity()).updateCalendarMonths();
    }

    private void getMasterData() {
        accountList = calendarDbService.getAllAccounts(loggedInUserObj.getUSER_ID());
        repeatsList = calendarDbService.getAllRepeats();
    }

    private void showTransferDatePicker(int year, int month, int day) {
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

    @OnClick(R.id.transferDateTVId)
    public void showTransferDatePicker(){
        try{
            String dateStr = DB_DATE_FORMAT_SDF.format(UI_DATE_FORMAT_SDF.parse(String.valueOf(transferDateTV.getText())));
            String dateStrArr[] = dateStr.split("-");

            int year = Integer.parseInt(dateStrArr[0]);
            int month = Integer.parseInt(dateStrArr[1]);
            int day = Integer.parseInt(dateStrArr[2]);

            showTransferDatePicker(year, month, day);

        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "Error while parsing the date : "+String.valueOf(transferDateTV.getText())+ " : "+e);
        }
    }

    @OnClick(R.id.transferScheduleUptoDateTVId)
    public void showScheduleUptoDatePicker(){
        try{
            String dateStr = String.valueOf(transferScheduleUptoDateTV.getText());

            if("FOREVER".equalsIgnoreCase(dateStr)){
                dateStr = DB_DATE_FORMAT_SDF.format(UI_DATE_FORMAT_SDF.parse(String.valueOf(transferDateTV.getText())));
            }
            else{
                dateStr = DB_DATE_FORMAT_SDF.format(UI_DATE_FORMAT_SDF.parse(String.valueOf(transferScheduleUptoDateTV.getText())));
            }

            String dateStrArr[] = dateStr.split("-");

            int year = Integer.parseInt(dateStrArr[0]);
            int month = Integer.parseInt(dateStrArr[1]);
            int day = Integer.parseInt(dateStrArr[2]);

            showScheduleUptoDatePicker(year, month, day);
        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "Error while parsing the date : "+String.valueOf(transferScheduleUptoDateTV.getText())+ " : "+e);
        }
    }

    @OnClick(R.id.transferNotifyAddTimeTVId)
    public void showNotifyAddTimePicker(){
        try{
            String timeStr = DB_TIME_FORMAT_SDF.format(UI_TIME_FORMAT_SDF.parse(String.valueOf(transferNotifyAddTimeTV.getText())));
            String timeStrArr[] = timeStr.split(":");

            int hour = Integer.parseInt(timeStrArr[0]);
            int minute = Integer.parseInt(timeStrArr[1]);

            showNotifyTimePicker(hour, minute);

        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "Error while parsing the time : "+String.valueOf(transferNotifyAddTimeTV.getText())+ " : "+e);
        }
    }

    @OnClick(R.id.transferAutoAddTimeTVId)
    public void showAutoAddTimePicker(){
        try{
            String timeStr = DB_TIME_FORMAT_SDF.format(UI_TIME_FORMAT_SDF.parse(String.valueOf(transferAutoAddTimeTV.getText())));
            String timeStrArr[] = timeStr.split(":");

            int hour = Integer.parseInt(timeStrArr[0]);
            int minute = Integer.parseInt(timeStrArr[1]);

            showAutoAddTimePicker(hour, minute);

        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "Error while parsing the time : "+String.valueOf(transferAutoAddTimeTV.getText())+ " : "+e);
        }
    }

    @OnClick(R.id.transferHeaderSaveTVId)
    public void saveUpdateTransfer(){
        String messageStr = "Error !!";

        getInputs();

        if(transfer == null){
            showToast("Error !!");
            return;
        }
        else if(transfer.getTRNFR_AMT() == null || transfer.getTRNFR_AMT().equals(0.0)){
            showToast("Enter Transfer Amount");
            return;
        }
        else if(transfer.getACC_ID_FRM().equalsIgnoreCase(transfer.getACC_ID_TO())){
            showToast("Cannot Transfer between the same Accounts");
            return;
        }

        //if add_update_transfer contains add_update_transfer id, then its an update. if not its a new add_update_transfer
        if(transfer.getTRNFR_ID() == null) {
            transfer.setTRNFR_ID(IdGenerator.getInstance().generateUniqueId("TRNFR"));
            long result = transfersDbService.addNewTransfer(transfer);

            if (result == -1) {
                messageStr = "Failed to create a new Transfer !";
            } else {
                messageStr = "New Transfer created";
            }
        }
        else{
            long result = transfersDbService.updateOldTransfer(transfer);
            if(result == 0) {
                messageStr = "Failed to update Transfer/Account !";
            } else if(result == 1){
                messageStr = "Transfer updated";
            }
            else{
                messageStr = "Unknown error !";
            }
        }

        closeFragment(messageStr);
    }

    @OnClick(R.id.transferCloseTVId)
    public void close(){
        getInputs();

        boolean hasInput = false;
        if(transfer != null){
            if(transfer.getTRNFR_AMT() != null && !transfer.getTRNFR_AMT().equals(0.0)){
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

    /*@OnCheckedChanged(R.id.transferRepeatSwitchId)
    public void onRepeatSwitch(){
        if(transferRepeatSwitch.isChecked()){
            transferRepeatLL.setVisibility(View.VISIBLE);
            transferNotifyDivider.setVisibility(View.VISIBLE);
            transferNotifyLL.setVisibility(View.VISIBLE);
            transferScheduleDivider.setVisibility(View.VISIBLE);
            transferScheduleLL.setVisibility(View.VISIBLE);

            transferSV.post(new Runnable() { public void run() { transferSV.fullScroll(View.FOCUS_DOWN); } });
        }
        else{
            transferSV.post(new Runnable() { public void run() { transferSV.fullScroll(View.FOCUS_UP); } });

            transferRepeatLL.setVisibility(View.GONE);
            transferNotifyDivider.setVisibility(View.GONE);
            transferNotifyLL.setVisibility(View.GONE);
            transferScheduleDivider.setVisibility(View.GONE);
            transferScheduleLL.setVisibility(View.GONE);
        }
    }

    @OnItemSelected(R.id.transferNotifyRGId)
    public void onNotifyOrAuto(){
        int checkedId = transferNotifyRG.getCheckedRadioButtonId();

        if(R.id.transactionContentNotifyAddRBId == checkedId){
            transferNotifyAddTimeTV.setVisibility(View.VISIBLE);
            transferAutoAddTimeTV.setVisibility(View.INVISIBLE);
        }
        else if(R.id.transactionContentAutoAddRBId == checkedId){
            add_update_transfer.setNOTIFY_TIME(String.valueOf(transferAutoAddTimeTV.getText()));
            transferNotifyAddTimeTV.setVisibility(View.INVISIBLE);
            transferAutoAddTimeTV.setVisibility(View.VISIBLE);
        }
    }*/

    private void initComps(){
        //Change listeners
        transferRepeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(transferRepeatSwitch.isChecked()){
                    transferRepeatLL.setVisibility(View.VISIBLE);
                    transferNotifyDivider.setVisibility(View.VISIBLE);
                    transferNotifyLL.setVisibility(View.VISIBLE);
                    transferScheduleDivider.setVisibility(View.VISIBLE);
                    transferScheduleLL.setVisibility(View.VISIBLE);

                    transferSV.post(new Runnable() { public void run() { transferSV.fullScroll(View.FOCUS_DOWN); } });
                }
                else{
                    transferSV.post(new Runnable() { public void run() { transferSV.fullScroll(View.FOCUS_UP); } });

                    transferRepeatLL.setVisibility(View.GONE);
                    transferNotifyDivider.setVisibility(View.GONE);
                    transferNotifyLL.setVisibility(View.GONE);
                    transferScheduleDivider.setVisibility(View.GONE);
                    transferScheduleLL.setVisibility(View.GONE);
                }
            }
        });

        transferNotifyRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checkedId = transferNotifyRG.getCheckedRadioButtonId();

                if(R.id.transferNotifyAddRBId == checkedId){
                    transferNotifyAddTimeTV.setVisibility(View.VISIBLE);
                    transferAutoAddTimeTV.setVisibility(View.INVISIBLE);
                }
                else if(R.id.transferAutoAddRBId == checkedId){
                    transfer.setNOTIFY_TIME(String.valueOf(transferAutoAddTimeTV.getText()));
                    transferNotifyAddTimeTV.setVisibility(View.INVISIBLE);
                    transferAutoAddTimeTV.setVisibility(View.VISIBLE);
                }
            }
        });

        setFont(transferRL);
    }

    private AccountMO getAccountOnId(List<AccountMO> accountList, String accountIdStr){
        for(AccountMO iterList : accountList){
            if(iterList.getACC_ID().equalsIgnoreCase(accountIdStr)){
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

    private RepeatMO getDefaultRepeat(List<RepeatMO> repeatMOList){
        for(RepeatMO iterList : repeatMOList){
            if(iterList.getREPEAT_IS_DEF().equalsIgnoreCase(DB_AFFIRMATIVE)){
                return iterList;
            }
        }
        return null;
    }

    @OnClick(R.id.transferAmountTVId)
    public void showAmountFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_AMOUNT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_AMOUNT_OBJECT, String.valueOf(transferAmountTV.getText()));
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSFER);

        SelectAmountFragment fragment = new SelectAmountFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_AMOUNT);
    }

    @OnClick(R.id.transferRepeatLLId)
    public void showRepeatFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_REPEAT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(REPEAT_OBJECT, (Serializable) repeatsList);
        bundle.putSerializable(SELECTED_REPEAT_OBJECT, ((RepeatMO)transferRepeatLL.getTag()).getREPEAT_NAME());

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSFER);

        SelectRepeatFragment fragment = new SelectRepeatFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_REPEAT);
    }

    @OnClick(R.id.transferFromAccountLLId)
    public void showFromAccountFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_ACCOUNT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(ACCOUNT_OBJECT, (Serializable) accountList);
        bundle.putSerializable(ACCOUNT_TYPE_FLAG, "FROM");
        bundle.putSerializable(SELECTED_ACCOUNT_OBJECT, ((AccountMO)transferFromAccountLL.getTag()).getACC_ID());
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSFER);

        SelectAccountFragment fragment = new SelectAccountFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_ACCOUNT);
    }

    @OnClick(R.id.transferToAccountLLId)
    public void showToAccountFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_ACCOUNT);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(ACCOUNT_OBJECT, (Serializable) accountList);
        bundle.putSerializable(ACCOUNT_TYPE_FLAG, "TO");
        bundle.putSerializable(SELECTED_ACCOUNT_OBJECT, ((AccountMO)transferToAccountLL.getTag()).getACC_ID());
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUserObj);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSFER);

        SelectAccountFragment fragment = new SelectAccountFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_ACCOUNT);
    }

    private void showConfirmCloseFragment(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFIRM_MESSAGE, "Discard Transfer ?");

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSFER);

        ConfirmFragment fragment = new ConfirmFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_CONFIRM);
    }

    private DatePickerDialog.OnDateSetListener tranDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            setDate(year, month+1, day, "TRANSFER_DATE");
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

            if("TRANSFER_DATE".equalsIgnoreCase(uiComponentStr)){
                transferDateTV.setText(dateStr.toUpperCase());
            }
            else if("SCHEDULE_UPTO".equalsIgnoreCase(uiComponentStr)){
                String fromDateStr = String.valueOf(transferDateTV.getText());

                Date fromDate = UI_DATE_FORMAT_SDF.parse(fromDateStr);
                Date uptoDate = UI_DATE_FORMAT_SDF.parse(dateStr);

                if(uptoDate.before(fromDate)){
                    showToast("Schedule Upto cannot be before Transfer date");
                    return;
                }

                transferScheduleUptoDateTV.setText(dateStr.toUpperCase());
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
                transferNotifyAddTimeTV.setText(timeStr.toUpperCase());
            }
            if("AUTO_ADD_TIME".equalsIgnoreCase(uiComponentStr)){
                transferAutoAddTimeTV.setText(timeStr.toUpperCase());
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
    public AddUpdateTransferFragment() {}

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
        }
    }

    private void initDb() {
        calendarDbService = new CalendarDbService(mContext);
        transfersDbService = new TransfersDbService(mContext);
    }

    public void onFinishDialog(AccountMO accountsMO, String whichAccountStr) {
        if("FROM".equalsIgnoreCase(whichAccountStr)){
            setFromAccount(accountsMO);
        }
        else {
            setToAccount(accountsMO);
        }
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

    private void setFromAccount(AccountMO accountsMO){
        ((TextView)transferFromAccountLL.findViewById(R.id.transferFromAccountTVId)).setText(accountsMO.getACC_NAME());
        transferFromAccountLL.findViewById(R.id.transferFromAccountIVId).setBackgroundResource(Integer.parseInt(accountsMO.getACC_IMG()));

        transferFromAccountTotalTV = FinappleUtility.formatAmountView(transferFromAccountTotalTV, loggedInUserObj, accountsMO.getACC_TOTAL());

        if(accountsMO.getACC_TOTAL() < ACCOUNT_LOW_BALANCE_LIMIT){
            transferFromAccountLL.findViewById(R.id.transferFromAccountStatusTVId).setVisibility(View.VISIBLE);
        }
        else{
            transferFromAccountLL.findViewById(R.id.transferFromAccountStatusTVId).setVisibility(View.GONE);
        }

        transferFromAccountLL.setTag(accountsMO);
    }

    private void setToAccount(AccountMO accountsMO){
        ((TextView)transferToAccountLL.findViewById(R.id.transferToAccountTVId)).setText(accountsMO.getACC_NAME());
        transferToAccountLL.findViewById(R.id.transferToAccountIVId).setBackgroundResource(Integer.parseInt(accountsMO.getACC_IMG()));

        transferToAccountTotalTV = FinappleUtility.formatAmountView(transferToAccountTotalTV, loggedInUserObj, accountsMO.getACC_TOTAL());

        if(accountsMO.getACC_TOTAL() < ACCOUNT_LOW_BALANCE_LIMIT){
            transferToAccountLL.findViewById(R.id.transferToAccountStatusTVId).setVisibility(View.VISIBLE);
        }
        else{
            transferToAccountLL.findViewById(R.id.transferToAccountStatusTVId).setVisibility(View.GONE);
        }

        transferToAccountLL.setTag(accountsMO);
    }

    private void setRepeat(RepeatMO repeatMo){
        ((TextView)transferRepeatLL.findViewById(R.id.transferRepeatTVId)).setText(repeatMo.getREPEAT_NAME());

        transferRepeatLL.setTag(repeatMo);
    }

    private void setAmount(String amountStr){
        transferAmountTV.setText(amountStr);
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