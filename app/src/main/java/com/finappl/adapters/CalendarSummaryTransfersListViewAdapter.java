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
import com.finappl.models.TransactionMO;
import com.finappl.models.TransferMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.ReverseSortMapComparator;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UI_TIME_FORMAT_SDF;

/**
 * Created by ajit on 17/1/15.
 */
public class CalendarSummaryTransfersListViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private LayoutInflater inflater;

    private UserMO user;
    private List<TransferMO> transferList;
    private View.OnClickListener clickListener;

    public CalendarSummaryTransfersListViewAdapter(Context mContext, UserMO user, List<TransferMO> transferList, View.OnClickListener clickListener) {
        super();

        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.transferList = transferList;
        this.clickListener = clickListener;
        this.user = user;

        buildList();
    }

    private void buildList() {
        TreeMap<String, Object> sortedMap = new TreeMap<>(new ReverseSortMapComparator());
        if(transferList != null && !transferList.isEmpty()){
            for(TransferMO iterList : transferList){
                sortedMap.put(iterList.getCreatDtm(), iterList);
            }
        }

        Map<String, Object> reverseSortedMap = sortedMap.descendingMap();
        transferList.clear();
        for(Map.Entry<String, Object> iterMap : reverseSortedMap.entrySet()){
            transferList.add((TransferMO) iterMap.getValue());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.calendar_summary_transfers_list_item;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.calendar_summary_transfers_list_item_ll = (LinearLayout) convertView.findViewById(R.id.calendar_summary_transfers_list_item_ll);
            mHolder.calendar_summary_transfers_list_item_account_from_iv = (ImageView) convertView.findViewById(R.id.calendar_summary_transfers_list_item_account_from_iv);
            mHolder.calendar_summary_transfers_list_item_account_from_tv = (TextView) convertView.findViewById(R.id.calendar_summary_transfers_list_item_account_from_tv);
            mHolder.calendar_summary_transfers_list_item_account_to_iv = (ImageView) convertView.findViewById(R.id.calendar_summary_transfers_list_item_account_to_iv);
            mHolder.calendar_summary_transfers_list_item_account_to_tv = (TextView) convertView.findViewById(R.id.calendar_summary_transfers_list_item_account_to_tv);
            mHolder.calendar_summary_transfers_list_item_amt_tv = (TextView) convertView.findViewById(R.id.calendar_summary_transfers_list_item_amt_tv);
            mHolder.calendar_summary_transfers_list_item_time_tv = (TextView) convertView.findViewById(R.id.calendar_summary_transfers_list_item_time_tv);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        TransferMO transfer = transferList.get(position);

        mHolder.calendar_summary_transfers_list_item_account_from_iv.setBackgroundResource(Integer.parseInt(transfer.getFromAccImg()));
        mHolder.calendar_summary_transfers_list_item_account_from_tv.setText(transfer.getFromAccName());
        mHolder.calendar_summary_transfers_list_item_account_to_iv.setBackgroundResource(Integer.parseInt(transfer.getToAccImg()));
        mHolder.calendar_summary_transfers_list_item_account_to_tv.setText(transfer.getToAccName());
        mHolder.calendar_summary_transfers_list_item_amt_tv = FinappleUtility.formatAmountView(mHolder.calendar_summary_transfers_list_item_amt_tv, user, transfer.getTRNFR_AMT());
        mHolder.calendar_summary_transfers_list_item_amt_tv.setTextColor(mContext.getResources().getColor(R.color.finappleCurrencyNeutralColor));

        if(transfer.getMOD_DTM() != null){
            mHolder.calendar_summary_transfers_list_item_time_tv.setText(FinappleUtility.formatTime(transfer.getMOD_DTM()));
        }
        else{
            mHolder.calendar_summary_transfers_list_item_time_tv.setText(FinappleUtility.formatTime(transfer.getCREAT_DTM()));
        }

        setFont(mHolder.calendar_summary_transfers_list_item_ll);

        return convertView;
    }


    private class ViewHolder {
        private LinearLayout calendar_summary_transfers_list_item_ll;
        private ImageView calendar_summary_transfers_list_item_account_from_iv;
        private TextView calendar_summary_transfers_list_item_account_from_tv;
        private ImageView calendar_summary_transfers_list_item_account_to_iv;
        private TextView calendar_summary_transfers_list_item_account_to_tv;
        private TextView calendar_summary_transfers_list_item_amt_tv;
        private TextView calendar_summary_transfers_list_item_time_tv;
    }

    @Override
    public int getCount() {
        return transferList.size();
    }

    @Override
    public TransferMO getItem(int position) {
        return transferList.get(position);
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