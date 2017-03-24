package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.activities.HomeActivity;
import com.finappl.adapters.DaySummaryListViewAdapter;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.dbServices.TransfersDbService;
import com.finappl.models.ActivitiesMO;
import com.finappl.models.DayLedger;
import com.finappl.models.TransactionMO;
import com.finappl.models.TransferMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.DAY_SUMMARY_OBJECT;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_TRANSACTION;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_TRANSFER;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.FRAGMENT_DAY_SUMMARY;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.SELECTED_GENERIC_OBJECT;
import static com.finappl.utils.Constants.SELECTED_TRANASCTION_OBJECT;
import static com.finappl.utils.Constants.TRANSACTION_OBJECT;
import static com.finappl.utils.Constants.TRANSFER_OBJECT;
import static com.finappl.utils.Constants.UI_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class DaySummaryFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.day_summary_rl)
    RelativeLayout day_summary_rl;

    @InjectView(R.id.day_summary_date_tv)
    TextView day_summary_date_tv;

    @InjectView(R.id.day_summary_transactions_count_tv)
    TextView day_summary_transactions_count_tv;

    @InjectView(R.id.day_summary_transfers_count_tv)
    TextView day_summary_transfers_count_tv;

    @InjectView(R.id.day_summary_transactions_amount_tv)
    TextView day_summary_transactions_amount_tv;

    @InjectView(R.id.day_summary_transfers_amount_tv)
    TextView day_summary_transfers_amount_tv;

    @InjectView(R.id.day_summary_lv)
    ListView day_summary_lv;
    /*Components*/

    private DayLedger dayLedger;
    private UserMO user;

    private boolean refreshCalendar = false;

    private TransactionsDbService transactionsDbService;
    private TransfersDbService transfersDbService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.day_summary, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    private void setupPage() {
        try{
            day_summary_date_tv.setText(String.valueOf(UI_DATE_FORMAT_SDF.format(JAVA_DATE_FORMAT_SDF.parse(dayLedger.getDate()))).toUpperCase());
        }
        catch(ParseException e){
            Log.e(CLASS_NAME, "Parse Exception while parsing the date:"+dayLedger.getDate());
            return;
        }

        List<Object> activitiesList = new ArrayList<>();
        ActivitiesMO activities = dayLedger.getActivities();
        if(activities != null){
            if(activities.getTransactionsList() != null){
                day_summary_transactions_count_tv.setText(String.valueOf(activities.getTransactionsList().size()));
                activitiesList.addAll(activities.getTransactionsList());
            }

            if(activities.getTransfersList() != null){
                day_summary_transfers_count_tv.setText(String.valueOf(activities.getTransfersList().size()));
                activitiesList.addAll(activities.getTransfersList());
            }
        }

        day_summary_transactions_amount_tv.setText(String.valueOf(FinappleUtility.formatAmountWithNegative(user, dayLedger.getTransactionsAmountTotal())));
        day_summary_transfers_amount_tv.setText(String.valueOf(FinappleUtility.formatAmountWithNegative(user, dayLedger.getTransfersAmountTotal())));

        //set up list of transactions and transfers
        DaySummaryListViewAdapter listViewAdapter = new DaySummaryListViewAdapter(mContext, user, activitiesList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(R.id.day_summary_transactions_list_item_edit_iv == v.getId()){
                    onClose(v);

                    FragmentManager manager = getFragmentManager();
                    Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSACTION);

                    if (frag != null) {
                        manager.beginTransaction().remove(frag).commit();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(TRANSACTION_OBJECT, (TransactionMO) v.getTag(R.layout.day_summary_transactions_list_item));
                    bundle.putSerializable(LOGGED_IN_OBJECT, user);

                    AddUpdateTransactionFragment fragment = new AddUpdateTransactionFragment();
                    fragment.setArguments(bundle);
                    fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
                    fragment.show(manager, FRAGMENT_ADD_UPDATE_TRANSACTION);
                }
                else if(R.id.day_summary_transactions_list_item_delete_iv == v.getId()){
                    FragmentManager manager = getFragmentManager();
                    Fragment frag = manager.findFragmentByTag(FRAGMENT_CONFIRM);
                    if (frag != null) {
                        manager.beginTransaction().remove(frag).commit();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(CONFIRM_MESSAGE, "Delete Transaction ?");
                    bundle.putSerializable(SELECTED_GENERIC_OBJECT, (TransactionMO) v.getTag(R.layout.day_summary_transactions_list_item));

                    Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_DAY_SUMMARY);

                    ConfirmFragment fragment = new ConfirmFragment();
                    fragment.setArguments(bundle);
                    fragment.setTargetFragment(currentFrag, 0);
                    fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
                    fragment.show(manager, FRAGMENT_CONFIRM);
                }
                else if(R.id.day_summary_transfers_list_item_edit_iv == v.getId()){
                    onClose(v);

                    FragmentManager manager = getFragmentManager();
                    Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSFER);

                    if (frag != null) {
                        manager.beginTransaction().remove(frag).commit();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(TRANSFER_OBJECT, (TransferMO) v.getTag(R.layout.day_summary_transfers_list_item));
                    bundle.putSerializable(LOGGED_IN_OBJECT, user);

                    AddUpdateTransferFragment fragment = new AddUpdateTransferFragment();
                    fragment.setArguments(bundle);
                    fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
                    fragment.show(manager, FRAGMENT_ADD_UPDATE_TRANSFER);
                }
                else if(R.id.day_summary_transfers_list_item_delete_iv == v.getId()){
                    FragmentManager manager = getFragmentManager();
                    Fragment frag = manager.findFragmentByTag(FRAGMENT_CONFIRM);
                    if (frag != null) {
                        manager.beginTransaction().remove(frag).commit();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(CONFIRM_MESSAGE, "Delete Transfer ?");
                    bundle.putSerializable(SELECTED_GENERIC_OBJECT, (TransferMO) v.getTag(R.layout.day_summary_transfers_list_item));

                    Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_DAY_SUMMARY);

                    ConfirmFragment fragment = new ConfirmFragment();
                    fragment.setArguments(bundle);
                    fragment.setTargetFragment(currentFrag, 0);
                    fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
                    fragment.show(manager, FRAGMENT_CONFIRM);
                }
            }
        });
        day_summary_lv.setAdapter(listViewAdapter);
    }

    private void getDataFromBundle() {
        dayLedger = (DayLedger) getArguments().get(DAY_SUMMARY_OBJECT);
        user = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    @OnClick(R.id.day_summary_close_iv)
    public void onClose(View view){
        dismiss();
    }

    private void initComps(){
        setFont(day_summary_rl);
    }

    // Empty constructor required for DialogFragment
    public DaySummaryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        transactionsDbService = new TransactionsDbService(mContext);
        transfersDbService = new TransfersDbService(mContext);
    }

    @Override
    public void onDismiss(final DialogInterface dialog){
        super.onDismiss(dialog);

        if(refreshCalendar){
            ((HomeActivity)getActivity()).updateCalendarMonths();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT ;
            int height = 1200;
            d.getWindow().setLayout(width, height);
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

    public void deleteTransaction(TransactionMO transaction) {
        if(transactionsDbService.deleteTransaction(transaction.getTRAN_ID())){
            DayLedger temp = dayLedger;
            temp.getActivities().getTransactionsList().remove(transaction);

            if("EXPENSE".equalsIgnoreCase(transaction.getTRAN_TYPE())){
                temp.setTransactionsAmountTotal(temp.getTransactionsAmountTotal()+transaction.getTRAN_AMT());
            }
            else{
                temp.setTransactionsAmountTotal(temp.getTransactionsAmountTotal()-transaction.getTRAN_AMT());
            }

            dayLedger = temp;
            setupPage();
            refreshCalendar = true;
            FinappleUtility.showSnacks(getActivity().getCurrentFocus(), "Transaction deleted !", OK, Snackbar.LENGTH_LONG);
        }
    }

    public void deleteTransfer(TransferMO transfer) {
        if(transfersDbService.deleteTransfer(transfer.getTRNFR_ID())){
            DayLedger temp = dayLedger;
            temp.getActivities().getTransfersList().remove(transfer);
            temp.setTransfersAmountTotal(temp.getTransfersAmountTotal()-transfer.getTRNFR_AMT());
            dayLedger = temp;
            setupPage();
            refreshCalendar = true;
            FinappleUtility.showSnacks(getActivity().getCurrentFocus(), "Transfer deleted !", OK, Snackbar.LENGTH_LONG);
        }
    }
}
