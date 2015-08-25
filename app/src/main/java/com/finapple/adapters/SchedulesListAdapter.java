package com.finapple.adapters;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finapple.R;
import com.finapple.model.CategoryModel;
import com.finapple.model.ConsolidatedSchedulesModel;
import com.finapple.model.MonthLegend;
import com.finapple.model.ScheduledTransactionModel;
import com.finapple.model.ScheduledTransferModel;
import com.finapple.model.SpentOnModel;
import com.finapple.model.TransactionModel;
import com.finapple.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by ajit on 29/7/15.
 */
public class SchedulesListAdapter extends BaseAdapter {

    private final String CLASS_NAME = this.getClass().getName();

    private static final int TYPE_TRANSACTION_ITEM = 0;
    private static final int TYPE_TRANSFER_ITEM = 1;

    private Context mContext;
    private ArrayList<Object> mData = new ArrayList<Object>();
    private TreeSet<Integer> mDataTypeSet = new TreeSet<Integer>();
    private LayoutInflater inflater;
    private int listViewId;
    private Map<String, MonthLegend> itemsMap;
    private List<String> dateList = new ArrayList<>();

    public SchedulesListAdapter(Context context, int listViewId, Map<String, MonthLegend> itemsMap) {
        super();
        this.listViewId = listViewId;
        this.itemsMap = itemsMap;
        this.mContext = context;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        buildList();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataTypeSet.contains(position) ? TYPE_TRANSFER_ITEM : TYPE_TRANSACTION_ITEM;
    }

    private void buildList() {
        SortedSet<String> keys = new TreeSet<String>(itemsMap.keySet());
        List<String> keysList = new ArrayList<>();
        keysList.addAll(keys);

        for(int i=0; i<itemsMap.size(); i++) {
            MonthLegend monthLegend = itemsMap.get(keysList.get(i));
            
            if(!monthLegend.isHasScheduledTransaction() || !monthLegend.isHasScheduledTransfer()){
                continue;
            }

            if(monthLegend.isHasScheduledTransaction()){
                List<ScheduledTransactionModel> scheduledTransactionModelList = monthLegend.getScheduledTransactionModelList();

                for(ScheduledTransactionModel iterList : scheduledTransactionModelList) {
                    dateList.add(keysList.get(i));
                    addTransactionItem(iterList);
                }
            }

            if(monthLegend.isHasScheduledTransfer()){
                List<ScheduledTransferModel> scheduledTransferModelList = monthLegend.getScheduledTransferModelList();

                for(ScheduledTransferModel iterList : scheduledTransferModelList) {
                    dateList.add(keysList.get(i));
                    addTransferItem(iterList);
                }
            }
        }
    }

    public void addTransactionItem(Object item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addTransferItem(Object item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;

        if(convertView == null) {
            convertView = inflater.inflate(listViewId, null);

            mHolder = new ViewHolder();

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
            // mHolder.calendarScheduleDateTV = (TextView) convertView.findViewById(R.id.calendarScheduleDateTVId);
            // mHolder.calendarScheduleDateSSTV = (TextView) convertView.findViewById(R.id.calendarScheduleDateSSTVId);
            mHolder.calendarScheduleAmtTV = (TextView) convertView.findViewById(R.id.calendarScheduleAmtTVId);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        makeItem(position, mHolder);
        return convertView;
    }

    private void makeItem(int position, ViewHolder mHolder) {
        if(mData.get(position)instanceof ScheduledTransactionModel){
            ScheduledTransactionModel scheduledTransactionModelObj = (ScheduledTransactionModel)mData.get(position);

            mHolder.calendarScheduleTransactionLL.setVisibility(View.VISIBLE);
            mHolder.calendarScheduleTransferLL.setVisibility(View.GONE);
            mHolder.calendarScheduleTransactionCatTV.setText(scheduledTransactionModelObj.getCategoryNameStr());

            //for tattoo
            mHolder.calendarScheduleFreqTattooTV.setText(scheduledTransactionModelObj.getSCH_TRAN_FREQ());
            if("DAILY".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_FREQ())){
                mHolder.calendarScheduleFreqTattooTV.setBackgroundResource(R.drawable.circle_day_text_view);
            }
            else if("WEEKLY".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_FREQ())){
                mHolder.calendarScheduleFreqTattooTV.setBackgroundResource(R.drawable.circle_week_text_view);
            }
            else if("MONTHLY".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_FREQ())){
                mHolder.calendarScheduleFreqTattooTV.setBackgroundResource(R.drawable.circle_month_text_view);
            }
            else if("YEARLY".equalsIgnoreCase(scheduledTransactionModelObj.getSCH_TRAN_FREQ())){
                mHolder.calendarScheduleFreqTattooTV.setBackgroundResource(R.drawable.circle_year_text_view);
            }

            //for date
            SimpleDateFormat wrongSdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat rightSdf = new SimpleDateFormat("dd");
            try {
                String dayStr = rightSdf.format(wrongSdf.parse(dateList.get(position)));
                String superScriptStr = Constants.DATE_SUPERSCRIPT_ARRAY[Integer.parseInt(dayStr)];

                mHolder.calendarScheduleDateTV.setText(dayStr);
                mHolder.calendarScheduleDateSSTV.setText(superScriptStr);
            }
            catch (ParseException e) {
                Log.e(CLASS_NAME, "ERROR !! "+e);
            }

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
            mHolder.calendarScheduleFreqTattooTV.setText(scheduledTransferModelObj.getSCH_TRNFR_FREQ());
            if("DAILY".equalsIgnoreCase(scheduledTransferModelObj.getSCH_TRNFR_FREQ())){
                mHolder.calendarScheduleFreqTattooTV.setBackgroundResource(R.drawable.circle_day_text_view);
            }
            else if("WEEKLY".equalsIgnoreCase(scheduledTransferModelObj.getSCH_TRNFR_FREQ())){
                mHolder.calendarScheduleFreqTattooTV.setBackgroundResource(R.drawable.circle_week_text_view);
            }
            else if("MONTHLY".equalsIgnoreCase(scheduledTransferModelObj.getSCH_TRNFR_FREQ())){
                mHolder.calendarScheduleFreqTattooTV.setBackgroundResource(R.drawable.circle_month_text_view);
            }
            else if("YEARLY".equalsIgnoreCase(scheduledTransferModelObj.getSCH_TRNFR_FREQ())){
                mHolder.calendarScheduleFreqTattooTV.setBackgroundResource(R.drawable.circle_year_text_view);
            }

            //for date
            SimpleDateFormat wrongSdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat rightSdf = new SimpleDateFormat("dd");
            try {
                String dayStr = rightSdf.format(wrongSdf.parse(dateList.get(position)));
                String superScriptStr = Constants.DATE_SUPERSCRIPT_ARRAY[Integer.parseInt(dayStr)];

                mHolder.calendarScheduleDateTV.setText(dayStr);
                mHolder.calendarScheduleDateSSTV.setText(superScriptStr);
            }
            catch (ParseException e) {
                Log.e(CLASS_NAME, "ERROR !! "+e);
            }

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
            } else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v, font);
            }
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
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
        private TextView calendarScheduleDateTV;
        private TextView calendarScheduleDateSSTV;
        private TextView calendarScheduleAmtTV;
    }
}