package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.adapters.CalendarSummaryRecyclerViewAdapter;
import com.finappl.adapters.DaySummaryListViewAdapter;
import com.finappl.models.ActivitiesMO;
import com.finappl.models.DayLedger;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.DAY_SUMMARY_OBJECT;
import static com.finappl.utils.Constants.FRAGMENT_ACCOUNTS;
import static com.finappl.utils.Constants.FRAGMENT_CATEGORIES;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.FRAGMENT_OPTIONS;
import static com.finappl.utils.Constants.FRAGMENT_SETTINGS;
import static com.finappl.utils.Constants.FRAGMENT_SPENTONS;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
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
        day_summary_date_tv.setText(dayLedger.getDate());

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
        DaySummaryListViewAdapter listViewAdapter = new DaySummaryListViewAdapter(mContext, user, activitiesList);
        day_summary_lv.setAdapter(listViewAdapter);
    }

    private void getDataFromBundle() {
        dayLedger = (DayLedger) getArguments().get(DAY_SUMMARY_OBJECT);
        user = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
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
}
