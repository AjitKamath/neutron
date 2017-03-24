package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.AccountMO;
import com.finappl.models.TransactionMO;
import com.finappl.models.TransferMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.ReverseSortMapComparator;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT;
import static com.finappl.utils.Constants.DB_DATE_TIME_FORMAT_SDF;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_OBJECT_TYPE;

/**
 * Created by ajit on 17/1/15.
 */
public class DaySummaryListViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private LayoutInflater inflater;

    private UserMO user;
    private List<Object> itemsList;
    private View.OnClickListener clickListener;

    public DaySummaryListViewAdapter(Context mContext, UserMO user, List<Object> itemsList, View.OnClickListener clickListener) {
        super();

        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.itemsList = itemsList;
        this.user = user;
        this.clickListener = clickListener;

        prepareData();
    }

    private void prepareData() {
        TreeMap<String, Object> sortedMap = new TreeMap<>(new ReverseSortMapComparator());
        if(itemsList != null && !itemsList.isEmpty()){
            for(Object iterList : itemsList){
                if(iterList instanceof TransactionMO){
                    TransactionMO transaction = (TransactionMO) iterList;
                    Date date = transaction.getCREAT_DTM();

                    if(transaction.getMOD_DTM() != null){
                        date = transaction.getMOD_DTM();
                    }

                    sortedMap.put(DB_DATE_TIME_FORMAT_SDF.format(date), iterList);
                }
                else if(iterList instanceof TransferMO){
                    TransferMO transfer = (TransferMO) iterList;
                    Date date = transfer.getCREAT_DTM();

                    if(transfer.getMOD_DTM() != null){
                        date = transfer.getMOD_DTM();
                    }

                    sortedMap.put(DB_DATE_TIME_FORMAT_SDF.format(date), iterList);
                }
                else{
                    Log.e(CLASS_NAME, UN_IDENTIFIED_OBJECT_TYPE);
                    return;
                }
            }
        }

        Map<String, Object> reverseSortedMap = sortedMap.descendingMap();
        itemsList.clear();
        for(Map.Entry<String, Object> iterMap : reverseSortedMap.entrySet()){
            itemsList.add(iterMap.getValue());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout;

        Object item = itemsList.get(position);

        if(item instanceof TransactionMO){
            layout = R.layout.day_summary_transactions_list_item;

            if(convertView == null) {
                mHolder = new ViewHolder();
                convertView = inflater.inflate(layout, null);

                mHolder.day_summary_transactions_list_item_edit_iv = (ImageView) convertView.findViewById(R.id.day_summary_transactions_list_item_edit_iv);
                mHolder.day_summary_transactions_list_item_delete_iv = (ImageView) convertView.findViewById(R.id.day_summary_transactions_list_item_delete_iv);
                mHolder.day_summary_transactions_list_item_cv = (CardView) convertView.findViewById(R.id.day_summary_transactions_list_item_cv);
                mHolder.day_summary_transactions_list_item_cat_iv = (ImageView) convertView.findViewById(R.id.day_summary_transactions_list_item_cat_iv);
                mHolder.day_summary_transactions_list_item_name_tv = (TextView) convertView.findViewById(R.id.day_summary_transactions_list_item_name_tv);
                mHolder.day_summary_transactions_list_item_amount_tv = (TextView) convertView.findViewById(R.id.day_summary_transactions_list_item_amount_tv);
                mHolder.day_summary_transactions_list_item_cat_tv = (TextView) convertView.findViewById(R.id.day_summary_transactions_list_item_cat_tv);
                mHolder.day_summary_transactions_list_item_account_tv = (TextView) convertView.findViewById(R.id.day_summary_transactions_list_item_account_tv);
                mHolder.day_summary_transactions_list_item_spent_on_tv = (TextView) convertView.findViewById(R.id.day_summary_transactions_list_item_spent_on_tv);
                mHolder.day_summary_transactions_list_item_time_tv = (TextView) convertView.findViewById(R.id.day_summary_transactions_list_item_time_tv);
                mHolder.day_summary_transactions_list_item_notes_tv = (TextView) convertView.findViewById(R.id.day_summary_transactions_list_item_notes_tv);

                convertView.setTag(layout, mHolder);

            } else {
                mHolder = (ViewHolder) convertView.getTag(layout);
            }

            TransactionMO transaction = (TransactionMO) item;

            mHolder.day_summary_transactions_list_item_cat_iv.setBackgroundResource(Integer.parseInt(transaction.getCategoryImg()));
            mHolder.day_summary_transactions_list_item_name_tv.setText(transaction.getTRAN_NAME());
            mHolder.day_summary_transactions_list_item_cat_tv.setText(transaction.getCategory());
            mHolder.day_summary_transactions_list_item_account_tv.setText(transaction.getAccount());
            mHolder.day_summary_transactions_list_item_spent_on_tv.setText(transaction.getSpentOn());
            mHolder.day_summary_transactions_list_item_amount_tv = FinappleUtility.formatAmountView(mHolder.day_summary_transactions_list_item_amount_tv, user, transaction.getTRAN_AMT());

            if("EXPENSE".equalsIgnoreCase(transaction.getTRAN_TYPE())){
                mHolder.day_summary_transactions_list_item_amount_tv.setTextColor(ContextCompat.getColor(mContext, R.color.finappleCurrencyNegColor));
            }
            else{
                mHolder.day_summary_transactions_list_item_amount_tv.setTextColor(ContextCompat.getColor(mContext, R.color.finappleCurrencyPosColor));
            }

            if(transaction.getTRAN_NOTE() != null && !transaction.getTRAN_NOTE().trim().isEmpty()){
                mHolder.day_summary_transactions_list_item_notes_tv.setText(transaction.getTRAN_NOTE());
            }
            else{
                mHolder.day_summary_transactions_list_item_notes_tv.setVisibility(View.GONE);
            }

            if(transaction.getMOD_DTM() == null){
                mHolder.day_summary_transactions_list_item_time_tv.setText(FinappleUtility.formatTime(transaction.getCREAT_DTM()));
            }
            else{
                mHolder.day_summary_transactions_list_item_time_tv.setText(FinappleUtility.formatTime(transaction.getMOD_DTM()));
            }

            mHolder.day_summary_transactions_list_item_edit_iv.setTag(layout, transaction);
            mHolder.day_summary_transactions_list_item_delete_iv.setTag(layout, transaction);

            mHolder.day_summary_transactions_list_item_edit_iv.setOnClickListener(clickListener);
            mHolder.day_summary_transactions_list_item_delete_iv.setOnClickListener(clickListener);

            setFont(mHolder.day_summary_transactions_list_item_cv);
        }
        else if(item instanceof TransferMO){
            layout = R.layout.day_summary_transfers_list_item;

            if(convertView == null) {
                mHolder = new ViewHolder();
                convertView = inflater.inflate(layout, null);

                mHolder.day_summary_transfers_list_item_edit_iv = (ImageView) convertView.findViewById(R.id.day_summary_transfers_list_item_edit_iv);
                mHolder.day_summary_transfers_list_item_delete_iv = (ImageView) convertView.findViewById(R.id.day_summary_transfers_list_item_delete_iv);
                mHolder.day_summary_transfers_list_item_cv = (CardView) convertView.findViewById(R.id.day_summary_transfers_list_item_cv);
                mHolder.day_summary_transfers_list_item_account_from_iv = (ImageView) convertView.findViewById(R.id.day_summary_transfers_list_item_account_from_iv);
                mHolder.day_summary_transfers_list_item_account_to_iv = (ImageView) convertView.findViewById(R.id.day_summary_transfers_list_item_account_to_iv);
                mHolder.day_summary_transfers_list_item_account_from_tv = (TextView) convertView.findViewById(R.id.day_summary_transfers_list_item_account_from_tv);
                mHolder.day_summary_transfers_list_item_account_to_tv = (TextView) convertView.findViewById(R.id.day_summary_transfers_list_item_account_to_tv);
                mHolder.day_summary_transfers_list_item_amt_tv = (TextView) convertView.findViewById(R.id.day_summary_transfers_list_item_amt_tv);
                mHolder.day_summary_transfers_list_item_time_tv = (TextView) convertView.findViewById(R.id.day_summary_transfers_list_item_time_tv);
                mHolder.day_summary_transfers_list_item_notes_tv = (TextView) convertView.findViewById(R.id.day_summary_transfers_list_item_notes_tv);

                convertView.setTag(layout, mHolder);

            } else {
                mHolder = (ViewHolder) convertView.getTag(layout);
            }

            TransferMO transfer = (TransferMO) item;

            mHolder.day_summary_transfers_list_item_account_from_iv.setBackgroundResource(Integer.parseInt(transfer.getFromAccImg()));
            mHolder.day_summary_transfers_list_item_account_to_iv.setBackgroundResource(Integer.parseInt(transfer.getToAccImg()));
            mHolder.day_summary_transfers_list_item_account_from_tv.setText(transfer.getFromAccName());
            mHolder.day_summary_transfers_list_item_account_to_tv.setText(transfer.getToAccName());
            mHolder.day_summary_transfers_list_item_notes_tv.setText(transfer.getTRNFR_NOTE());

            mHolder.day_summary_transfers_list_item_amt_tv = FinappleUtility.formatAmountView(mHolder.day_summary_transfers_list_item_amt_tv, user, transfer.getTRNFR_AMT());
            mHolder.day_summary_transfers_list_item_amt_tv.setTextColor(ContextCompat.getColor(mContext, R.color.finappleCurrencyNeutralColor));

            if(transfer.getMOD_DTM() == null){
                mHolder.day_summary_transfers_list_item_time_tv.setText(FinappleUtility.formatTime(transfer.getCREAT_DTM()));
            }
            else{
                mHolder.day_summary_transfers_list_item_time_tv.setText(FinappleUtility.formatTime(transfer.getMOD_DTM()));
            }

            mHolder.day_summary_transfers_list_item_edit_iv.setTag(layout, transfer);
            mHolder.day_summary_transfers_list_item_delete_iv.setTag(layout, transfer);

            mHolder.day_summary_transfers_list_item_edit_iv.setOnClickListener(clickListener);
            mHolder.day_summary_transfers_list_item_delete_iv.setOnClickListener(clickListener);

            setFont(mHolder.day_summary_transfers_list_item_cv);
        }
        else{
            FinappleUtility.showSnacks(convertView, UN_IDENTIFIED_OBJECT_TYPE, OK, Snackbar.LENGTH_INDEFINITE);
            return convertView;
        }

        return convertView;
    }


    private class ViewHolder {
        //transaction
        private ImageView day_summary_transactions_list_item_edit_iv;
        private ImageView day_summary_transactions_list_item_delete_iv;
        private CardView day_summary_transactions_list_item_cv;
        private ImageView day_summary_transactions_list_item_cat_iv;
        private TextView day_summary_transactions_list_item_name_tv;
        private TextView day_summary_transactions_list_item_amount_tv;
        private TextView day_summary_transactions_list_item_cat_tv;
        private TextView day_summary_transactions_list_item_account_tv;
        private TextView day_summary_transactions_list_item_spent_on_tv;
        private TextView day_summary_transactions_list_item_time_tv;
        private TextView day_summary_transactions_list_item_notes_tv;

        //transfer
        private ImageView day_summary_transfers_list_item_edit_iv;
        private ImageView day_summary_transfers_list_item_delete_iv;
        private CardView day_summary_transfers_list_item_cv;
        private ImageView day_summary_transfers_list_item_account_from_iv;
        private ImageView day_summary_transfers_list_item_account_to_iv;
        private TextView day_summary_transfers_list_item_account_from_tv;
        private TextView day_summary_transfers_list_item_account_to_tv;
        private TextView day_summary_transfers_list_item_amt_tv;
        private TextView day_summary_transfers_list_item_time_tv;
        private TextView day_summary_transfers_list_item_notes_tv;
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
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