package com.finappl.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.ConsolidatedTransactionModel;
import com.finappl.models.ConsolidatedTransferModel;
import com.finappl.models.SummaryModel;
import com.finappl.utils.FinappleUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by ajit on 17/1/15.
 */
public class CalendarSummarySectionListViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private int layoutResourceId;
    private SummaryModel dataList;
    private LayoutInflater inflater;
    private List<Object> itemsList = new ArrayList<>();

    public CalendarSummarySectionListViewAdapter(Context mContext, int layoutResourceId, SummaryModel data) {
        super();
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = data;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //build list
        buildList();
    }

    private void buildList() {
        if(!dataList.getConsolidatedTransactionModelMap().isEmpty()){
            for(Map.Entry<String, ConsolidatedTransactionModel> iterConsolMap : dataList.getConsolidatedTransactionModelMap().entrySet()){
                itemsList.add(iterConsolMap.getValue());
                notifyDataSetChanged();
            }
        }

        if(!dataList.getConsolidatedTransferModelMap().isEmpty()){
            for(Map.Entry<String, ConsolidatedTransferModel> iterConsolMap : dataList.getConsolidatedTransferModelMap().entrySet()){
                itemsList.add(iterConsolMap.getValue());
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layoutResourceId, null);

            //for consolidated transactions
            mHolder.consolSummaryLL = (LinearLayout) convertView.findViewById(R.id.consolSummaryLLId);
            mHolder.consolTranTypTV = (TextView) convertView.findViewById(R.id.consolTranTypTVId);
            mHolder.catTV = (TextView) convertView.findViewById(R.id.consolTranCatTVId);
            mHolder.countTV = (TextView) convertView.findViewById(R.id.consolTranCatCountTVId);
            mHolder.totalTV = (TextView) convertView.findViewById(R.id.consolTranCatTotalTVId);

            //for consolidated transfer
            mHolder.consolTrfrLL = (LinearLayout) convertView.findViewById(R.id.consolTrfrLLId);
            mHolder.consolTrfrFrmAccTV = (TextView) convertView.findViewById(R.id.consolTrfrFrmAccTVId);
            mHolder.consolTrfrToAccTV = (TextView) convertView.findViewById(R.id.consolTrfrToAccTVId);

            convertView.setTag(layoutResourceId, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layoutResourceId);
        }

        makeList(position, mHolder);

        return convertView;
    }

    private void makeList(int  position, ViewHolder mHolder) {
        // object item based on the position
        Object item = itemsList.get(position);

        mHolder.consolSummaryLL.setTag(item);

        if(item instanceof ConsolidatedTransactionModel){
            mHolder.consolTranTypTV.setBackgroundResource(R.drawable.circle_calendar_transaction_indicator);
            mHolder.catTV.setVisibility(View.VISIBLE);
            mHolder.consolTrfrLL.setVisibility(View.GONE);
            mHolder.catTV.setText(((ConsolidatedTransactionModel) item).getCategory());
            mHolder.countTV.setText("x " + ((ConsolidatedTransactionModel) item).getCount());

            mHolder.totalTV.setText(String.valueOf(((ConsolidatedTransactionModel) item).getTotal()));
            if(((ConsolidatedTransactionModel) item).getTotal() < 0) {
                mHolder.totalTV.setTextColor(mHolder.totalTV.getResources().getColor(R.color.finappleCurrencyNegColor));
            }
            else{
                mHolder.totalTV.setTextColor(mHolder.totalTV.getResources().getColor(R.color.finappleCurrencyPosColor));
            }
        }
        else if(item instanceof ConsolidatedTransferModel){
            mHolder.consolTranTypTV.setBackgroundResource(R.drawable.circle_calendar_transfer_indicator);
            mHolder.catTV.setVisibility(View.GONE);
            mHolder.consolTrfrLL.setVisibility(View.VISIBLE);
            mHolder.consolTrfrFrmAccTV.setText(((ConsolidatedTransferModel) item).getFromAccountStr());
            mHolder.consolTrfrToAccTV.setText(((ConsolidatedTransferModel) item).getToAccountStr());
            mHolder.countTV.setText("x " + ((ConsolidatedTransferModel) item).getCount());

            mHolder.totalTV.setText(String.valueOf(((ConsolidatedTransferModel) item).getAmount()));
            mHolder.totalTV.setTextColor(mHolder.totalTV.getResources().getColor(R.color.finappleCurrencyPosColor));
        }

        //TODO: Approximatization required

        //this is to offset the last item to allow its content to be viewed by scrolling
        if(position == itemsList.size()-1){
            mHolder.consolSummaryLL.setPadding(0, 0, 0, FinappleUtility.getInstance().getDpAsPixels(mContext.getResources(), 65));
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.consolSummaryLL, robotoCondensedLightFont);
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

    private class ViewHolder{
        //for consolidated transaction
        private LinearLayout consolSummaryLL;
        private TextView consolTranTypTV;
        private TextView catTV;
        private TextView countTV;
        private TextView totalTV;
        
        //for consolidated transfer
        private LinearLayout consolTrfrLL;
        private TextView consolTrfrFrmAccTV;
        private TextView consolTrfrToAccTV;
    }

}