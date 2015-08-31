package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.MonthLegend;
import com.finappl.models.ScheduledTransactionModel;
import com.finappl.models.ScheduledTransferModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class CalendarSchedulesSectionListAdapter extends BaseAdapter {

    private final String CLASS_NAME = this.getClass().getName();

    private static final int TYPE_CONTENT_ITEM = 0;
    private static final int TYPE_MAX_COUNT = TYPE_CONTENT_ITEM + 1;

    private ArrayList<Object> mData = new ArrayList<Object>();
    private List<Integer> itemTypeList = new ArrayList<>();
    private int contentLayoutId;
    private MonthLegend monthLegendObj;
    private Context mContext;
    private LayoutInflater inflater;

    public CalendarSchedulesSectionListAdapter(Context context, int headerLayoutId, int contentLayoutId, MonthLegend monthLegendObj) {
        super();
        this.contentLayoutId = contentLayoutId;
        this.monthLegendObj = monthLegendObj;
        this.mContext = context;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        buildSectionList();
    }


    @Override
    public int getItemViewType(int position) {
        return itemTypeList.get(position);
    }

    private void buildSectionList() {
        if(monthLegendObj == null){
            return;
        }

        List<ScheduledTransactionModel> scheduledTransactionModelList = monthLegendObj.getScheduledTransactionModelList();
        List<ScheduledTransferModel> scheduledTransferModelList = monthLegendObj.getScheduledTransferModelList();

        //for transactions
        if(scheduledTransactionModelList != null && !scheduledTransactionModelList.isEmpty()) {
            for (ScheduledTransactionModel iterList : scheduledTransactionModelList) {
                itemTypeList.add(TYPE_CONTENT_ITEM);
                addItem(iterList);
            }
        }

        //for transfers
        if(scheduledTransferModelList != null && !scheduledTransferModelList.isEmpty()) {
            for (ScheduledTransferModel iterList : scheduledTransferModelList) {
                itemTypeList.add(TYPE_CONTENT_ITEM);
                addItem(iterList);
            }
        }

    }

    public void addItem(Object item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        Integer type = getItemViewType(position);

        if(convertView == null) {
            mHolder = new ViewHolder();

            switch(type) {
                case TYPE_CONTENT_ITEM:
                    convertView = inflater.inflate(contentLayoutId, null);

                    //for transaction
                    mHolder.calendarScheduleTransactionLL = (LinearLayout) convertView.findViewById(R.id.calendarScheduleTransactionLLId);
                    mHolder.calendarScheduleTransactionCatTV = (TextView) convertView.findViewById(R.id.calendarScheduleTransactionCatTVId);

                    //for transfers
                    mHolder.calendarScheduleTransferLL = (LinearLayout) convertView.findViewById(R.id.calendarScheduleTransferLLId);
                    mHolder.calendarScheduleTransferFromTV = (TextView) convertView.findViewById(R.id.calendarScheduleTransferFromTVId);
                    mHolder.calendarScheduleTransferToTV = (TextView) convertView.findViewById(R.id.calendarScheduleTransferToTVId);

                    //common
                    mHolder.calendarSchedulesLL = (LinearLayout) convertView.findViewById(R.id.calendarSchedulesLLId);
                    mHolder.calendarScheduleFreqTattooTV = (TextView) convertView.findViewById(R.id.calendarScheduleFreqTattooTVId);
                    mHolder.calendarScheduleAmtTV = (TextView) convertView.findViewById(R.id.calendarScheduleAmtTVId);

                    break;
            }

            convertView.setTag(mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        switch(type) {
            case TYPE_CONTENT_ITEM:
                makeContentItem(position, mHolder);
                break;
        }

        return convertView;
    }

    private void makeContentItem(int position, ViewHolder mHolder) {
        if(mData.get(position)instanceof ScheduledTransactionModel){
            ScheduledTransactionModel scheduledTransactionModelObj = (ScheduledTransactionModel)mData.get(position);

            mHolder.calendarScheduleTransactionLL.setVisibility(View.VISIBLE);
            mHolder.calendarScheduleTransferLL.setVisibility(View.GONE);
            mHolder.calendarScheduleTransactionCatTV.setText(scheduledTransactionModelObj.getCategoryNameStr());

            //for tattoo
            mHolder.calendarScheduleFreqTattooTV.setBackgroundResource(R.drawable.circle_calendar_schedule_transaction_indicator);
            mHolder.calendarScheduleFreqTattooTV.setText(scheduledTransactionModelObj.getSCH_TRAN_FREQ());

            //for amount
            if(scheduledTransactionModelObj.getSCH_TRAN_AMT() >= 0){
                mHolder.calendarScheduleAmtTV.setText(String.valueOf(scheduledTransactionModelObj.getSCH_TRAN_AMT()));
                mHolder.calendarScheduleAmtTV.setTextColor(mHolder.calendarScheduleAmtTV.getResources().getColor(R.color.finappleCurrencyPosColor));
            }
            else{
                mHolder.calendarScheduleAmtTV.setText(String.valueOf(scheduledTransactionModelObj.getSCH_TRAN_AMT() * -1));   //to remove the '-' sign
                mHolder.calendarScheduleAmtTV.setTextColor(mHolder.calendarScheduleAmtTV.getResources().getColor(R.color.finappleCurrencyNegColor));
            }
        }
        else if(mData.get(position)instanceof ScheduledTransferModel){
            ScheduledTransferModel scheduledTransferModelObj = (ScheduledTransferModel)mData.get(position);

            mHolder.calendarScheduleTransactionLL.setVisibility(View.GONE);
            mHolder.calendarScheduleTransferLL.setVisibility(View.VISIBLE);

            //for tattoo
            mHolder.calendarScheduleFreqTattooTV.setBackgroundResource(R.drawable.circle_calendar_schedule_transfer_indicator);
            mHolder.calendarScheduleFreqTattooTV.setText(scheduledTransferModelObj.getSCH_TRNFR_FREQ());

            //for amount
            if(scheduledTransferModelObj.getSCH_TRNFR_AMT() >= 0){
                mHolder.calendarScheduleAmtTV.setText(String.valueOf(scheduledTransferModelObj.getSCH_TRNFR_AMT()));
                mHolder.calendarScheduleAmtTV.setTextColor(mHolder.calendarScheduleAmtTV.getResources().getColor(R.color.finappleCurrencyPosColor));
            }
            else{
                mHolder.calendarScheduleAmtTV.setText(String.valueOf(scheduledTransferModelObj.getSCH_TRNFR_AMT() * -1));   //to remove the '-' sign
                mHolder.calendarScheduleAmtTV.setTextColor(mHolder.calendarScheduleAmtTV.getResources().getColor(R.color.finappleCurrencyNegColor));
            }

            mHolder.calendarScheduleTransactionLL.setVisibility(View.GONE);
            mHolder.calendarScheduleTransferLL.setVisibility(View.VISIBLE);

            mHolder.calendarScheduleTransferFromTV.setText(scheduledTransferModelObj.getFromAccountStr());
            mHolder.calendarScheduleTransferToTV.setText(scheduledTransferModelObj.getToAccountStr());
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.calendarSchedulesLL, robotoCondensedLightFont);
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

    private class ViewHolder {
        //for transaction
        private LinearLayout calendarScheduleTransactionLL;
        private TextView calendarScheduleTransactionCatTV;

        //for transfers
        private LinearLayout calendarScheduleTransferLL;
        private TextView calendarScheduleTransferFromTV;
        private TextView calendarScheduleTransferToTV;

        //common
        private LinearLayout calendarSchedulesLL;
        private TextView calendarScheduleFreqTattooTV;
        private TextView calendarScheduleAmtTV;
    }
}