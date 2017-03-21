package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.UI_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UI_TIME_FORMAT;
import static com.finappl.utils.Constants.UI_TIME_FORMAT_SDF;

/**
 * Created by ajit on 17/1/15.
 */
public class CalendarSummaryTransactionsListViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private LayoutInflater inflater;

    private UserMO user;
    private List<TransactionMO> transactionList;
    private View.OnClickListener clickListener;

    public CalendarSummaryTransactionsListViewAdapter(Context mContext, UserMO user, List<TransactionMO> transactionList, View.OnClickListener clickListener) {
        super();

        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.transactionList = transactionList;
        this.clickListener = clickListener;
        this.user = user;

        buildList();
    }

    private void buildList() {
        TreeMap<String, Object> sortedMap = new TreeMap<>(new ReverseSortMapComparator());
        if(transactionList != null && !transactionList.isEmpty()){
            for(TransactionMO iterList : transactionList){
                sortedMap.put(iterList.getCreatDtm(), iterList);
            }
        }

        Map<String, Object> reverseSortedMap = sortedMap.descendingMap();
        transactionList.clear();
        for(Map.Entry<String, Object> iterMap : reverseSortedMap.entrySet()){
            transactionList.add((TransactionMO) iterMap.getValue());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.calendar_summary_transactions_list_item;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.calendar_summary_transactions_list_item_ll = (LinearLayout) convertView.findViewById(R.id.calendar_summary_transactions_list_item_ll);
            mHolder.calendar_summary_transactions_list_item_cat_iv = (ImageView) convertView.findViewById(R.id.calendar_summary_transactions_list_item_cat_iv);
            mHolder.calendar_summary_transactions_list_item_name_tv = (TextView) convertView.findViewById(R.id.calendar_summary_transactions_list_item_name_tv);
            mHolder.calendar_summary_transactions_list_item_amount_tv = (TextView) convertView.findViewById(R.id.calendar_summary_transactions_list_item_amount_tv);
            mHolder.calendar_summary_transactions_list_item_cat_tv = (TextView) convertView.findViewById(R.id.calendar_summary_transactions_list_item_cat_tv);
            mHolder.calendar_summary_transactions_list_item_account_tv = (TextView) convertView.findViewById(R.id.calendar_summary_transactions_list_item_account_tv);
            mHolder.calendar_summary_transactions_list_item_time_tv = (TextView) convertView.findViewById(R.id.calendar_summary_transactions_list_item_time_tv);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        TransactionMO transaction = transactionList.get(position);

        mHolder.calendar_summary_transactions_list_item_cat_iv.setBackgroundResource(Integer.parseInt(transaction.getCategoryImg()));
        mHolder.calendar_summary_transactions_list_item_name_tv.setText(transaction.getTRAN_NAME());
        mHolder.calendar_summary_transactions_list_item_cat_tv.setText(transaction.getCategory());
        mHolder.calendar_summary_transactions_list_item_account_tv.setText(transaction.getAccount());
        mHolder.calendar_summary_transactions_list_item_amount_tv = FinappleUtility.formatAmountView(mHolder.calendar_summary_transactions_list_item_amount_tv, user, transaction.getTRAN_AMT());

        if(transaction.getMOD_DTM() != null){
            mHolder.calendar_summary_transactions_list_item_time_tv.setText(FinappleUtility.formatTime(transaction.getMOD_DTM()));
        }
        else{
            mHolder.calendar_summary_transactions_list_item_time_tv.setText(FinappleUtility.formatTime(transaction.getCREAT_DTM()));
        }

        if("EXPENSE".equalsIgnoreCase(transaction.getTRAN_TYPE())){
            mHolder.calendar_summary_transactions_list_item_amount_tv.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyNegColor));
        }
        else{
            mHolder.calendar_summary_transactions_list_item_amount_tv.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyPosColor));
        }

        setFont(mHolder.calendar_summary_transactions_list_item_ll);

        return convertView;
    }


    private class ViewHolder {
        private LinearLayout calendar_summary_transactions_list_item_ll;
        private ImageView calendar_summary_transactions_list_item_cat_iv;
        private TextView calendar_summary_transactions_list_item_name_tv;
        private TextView calendar_summary_transactions_list_item_amount_tv;
        private TextView calendar_summary_transactions_list_item_cat_tv;
        private TextView calendar_summary_transactions_list_item_account_tv;
        private TextView calendar_summary_transactions_list_item_time_tv;
    }

    @Override
    public int getCount() {
        return transactionList.size();
    }

    @Override
    public TransactionMO getItem(int position) {
        return transactionList.get(position);
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