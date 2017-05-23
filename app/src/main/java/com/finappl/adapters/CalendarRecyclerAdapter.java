package com.finappl.adapters;

/**
 * Created by ajit on 15/2/17.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.interfaces.OnItemClickListener;
import com.finappl.models.DayLedger;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.finappl.R.id.calendar_date_cell_date_key;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_FONT;


public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.ViewHolder>{
    private final String CLASS_NAME = this.getClass().getName();
    private final Context mContext;
    private final int LAYOUT = R.layout.calendar_day;

    private List<Date> datesList = new ArrayList<>();
    private Map<String, DayLedger> ledger;
    private UserMO user;
    private String today = JAVA_DATE_FORMAT_SDF.format(new Date());

    private final OnItemClickListener listener;


    public CalendarRecyclerAdapter(Context context, List<Date> datesList, Map<String, DayLedger> ledger, UserMO user, OnItemClickListener listener){
        super();
        this.mContext = context;

        this.datesList = datesList;
        this.user = user;
        this.ledger = ledger;

        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView calendar_day_date_tv;
        private LinearLayout calendar_day_ll;
        private RelativeLayout calendar_day_content_rl;
        private LinearLayout calendar_day_transaction_ll;
        private TextView calendar_day_transactions_amt_tv;
        private LinearLayout calendar_day_transfer_ll;
        private TextView calendar_day_transfers_amt_tv;
        private ImageView calendar_day_sched_indicator_iv;

        public ViewHolder(View v){
            super(v);
            calendar_day_date_tv = (TextView) v.findViewById(R.id.calendar_day_date_tv);
            calendar_day_ll = (LinearLayout) v.findViewById(R.id.calendar_day_ll);
            calendar_day_content_rl = (RelativeLayout) v.findViewById(R.id.calendar_day_content_rl);
            calendar_day_transaction_ll = (LinearLayout) v.findViewById(R.id.calendar_day_transaction_ll);
            calendar_day_transactions_amt_tv = (TextView) v.findViewById(R.id.calendar_day_transactions_amt_tv);
            calendar_day_transfer_ll = (LinearLayout) v.findViewById(R.id.calendar_day_transfer_ll);
            calendar_day_transfers_amt_tv = (TextView) v.findViewById(R.id.calendar_day_transfers_amt_tv);
            calendar_day_sched_indicator_iv = (ImageView) v.findViewById(R.id.calendar_day_sched_indicator_iv);
        }

        public void bind(final Date item, final View viewObj, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(viewObj);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(viewObj);
                    return true;
                }
            });
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mContext).inflate(LAYOUT ,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder mHolder, int position){
        Date dateObj = datesList.get(position);
        String cellDate = JAVA_DATE_FORMAT_SDF.format(dateObj);
        int date = Integer.parseInt(cellDate.split("-")[0]);
        DayLedger dayLedger = new DayLedger();
        dayLedger.setDate(cellDate);

        //set cell data
        if(ledger != null && ledger.containsKey(cellDate)){
            dayLedger = ledger.get(cellDate);

            //transaction
            if(dayLedger.isHasTransactions()){
                mHolder.calendar_day_transaction_ll.setVisibility(View.VISIBLE);
                mHolder.calendar_day_transactions_amt_tv = FinappleUtility.shortenAmountView(mHolder.calendar_day_transactions_amt_tv, user, dayLedger.getTransactionsAmountTotal());
            }

            //transfer
            if(dayLedger.isHasTransfers()){
                mHolder.calendar_day_transfer_ll.setVisibility(View.VISIBLE);
                mHolder.calendar_day_transfers_amt_tv = FinappleUtility.shortenAmountView(mHolder.calendar_day_transfers_amt_tv, user, dayLedger.getTransfersAmountTotal());
                mHolder.calendar_day_transfers_amt_tv.setTextColor(ContextCompat.getColor(mContext, R.color.finappleTheme));
            }
        }

        mHolder.calendar_day_content_rl.setTag(calendar_date_cell_date_key, dayLedger);

        //date text
        mHolder.calendar_day_date_tv.setText(String.valueOf(date));

        //grey dates for prev and next month
        if((position < 7 && date > 24) || (position > 27 && date < 14)){
            if(position < 7 && date > 24){
                mHolder.calendar_day_date_tv.setTag("PREV_MONTH");
            }
            else{
                mHolder.calendar_day_date_tv.setTag("NEXT_MONTH");
            }
            mHolder.calendar_day_date_tv.setTextColor(ContextCompat.getColor(mContext, R.color.calendarNextPrevMonthDate));
            mHolder.calendar_day_transactions_amt_tv.setTextColor(ContextCompat.getColor(mContext, R.color.calendarNextPrevMonthDate));
            mHolder.calendar_day_transfers_amt_tv.setTextColor(ContextCompat.getColor(mContext, R.color.calendarNextPrevMonthDate));
            mHolder.calendar_day_transaction_ll.findViewById(R.id.calendar_day_transaction_ind_tv).setBackgroundResource(R.drawable.circle_calendar_transaction_transfer_indicator_disabled);
            mHolder.calendar_day_transfer_ll.findViewById(R.id.calendar_day_transfer_ind_tv).setBackgroundResource(R.drawable.circle_calendar_transaction_transfer_indicator_disabled);
        }
        //todays date
        else if(today.equals(cellDate)){
            mHolder.calendar_day_date_tv.setTextColor(ContextCompat.getColor(mContext, R.color.calendarTodayDate));
        }

        mHolder.bind(datesList.get(position), mHolder.calendar_day_content_rl, listener);

        setFont(mHolder.calendar_day_ll);
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group) {
        final Typeface font = Typeface.createFromAsset(mContext.getAssets(), UI_FONT);

        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v);
            }
        }
    }

    @Override
    public int getItemCount(){
        return datesList.size();
    }
}
