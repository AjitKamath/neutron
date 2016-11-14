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
import com.finappl.models.ConsolidatedTransactionModel;
import com.finappl.models.ConsolidatedTransferModel;
import com.finappl.models.ActivitiesMO;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.ReverseSortMapComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;

/**
 * Created by ajit on 17/1/15.
 */
public class CalendarActivitiesSectionListViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private ActivitiesMO dataList;
    private LayoutInflater inflater;
    private List<Object> itemsList = new ArrayList<>();
    private UserMO userObject;

    public CalendarActivitiesSectionListViewAdapter(Context mContext, ActivitiesMO data, UserMO userObject) {
        super();
        this.mContext = mContext;
        this.dataList = data;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.userObject = userObject;

        //build list
        buildList();
    }

    private void buildList() {
        if(dataList == null){
            return;
        }

        TreeMap<String, Object> sortedMap = new TreeMap<>(new ReverseSortMapComparator());

        if(dataList.getTransactionsList() != null && !dataList.getTransactionsList().isEmpty()){
            for(TransactionModel iterList : dataList.getTransactionsList()){
                sortedMap.put(iterList.getCreatDtm(), iterList);
            }
        }

        if(dataList.getTransfersList() != null && !dataList.getTransfersList().isEmpty()){
            for(TransferModel iterList : dataList.getTransfersList()){
                sortedMap.put(iterList.getTransferDate(), iterList);
            }
        }

        Map<String, Object> reverseSortedMap = sortedMap.descendingMap();

        for(Map.Entry<String, Object> iterMap : reverseSortedMap.entrySet()){
            itemsList.add(iterMap.getValue());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.calendar_activity_list_item;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.activityLL = (LinearLayout) convertView.findViewById(R.id.activityLLId);

            //Transaction
            mHolder.activityTransactionLL = (LinearLayout) convertView.findViewById(R.id.activityTransactionLLId);
            mHolder.activityCategoryIV = (ImageView) convertView.findViewById(R.id.activityCategoryIVId);
            mHolder.activityTransactionNameTV = (TextView) convertView.findViewById(R.id.activityTransactionNameTVId);
            mHolder.activityCategoryTV = (TextView) convertView.findViewById(R.id.activityCategoryTVId);
            mHolder.activityAccountTV = (TextView) convertView.findViewById(R.id.activityAccountTVId);
            mHolder.activityCurrencyCodeTV = (TextView) convertView.findViewById(R.id.activityCurrencyCodeTVId);
            mHolder.activityTransactionAmtTV = (TextView) convertView.findViewById(R.id.activityTransactionAmtTVId);

            //Transfer
            mHolder.activityTransferLL = (LinearLayout) convertView.findViewById(R.id.activityTransferLLId);
            mHolder.activityTransferFromAccIV = (ImageView) convertView.findViewById(R.id.activityTransferFromAccIVId);
            mHolder.activityTransferToAccIV = (ImageView) convertView.findViewById(R.id.activityTransferToAccIVId);
            mHolder.activityTransferFromAccTV = (TextView) convertView.findViewById(R.id.activityTransferFromAccTVId);
            mHolder.activityTransferCurrCodeTV = (TextView) convertView.findViewById(R.id.activityTransferCurrCodeTVId);
            mHolder.activityTransferAmtTV = (TextView) convertView.findViewById(R.id.activityTransferAmtTVId);
            mHolder.activityTransferToAccTV = (TextView) convertView.findViewById(R.id.activityTransferToAccTVId);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        makeList(position, mHolder);

        return convertView;
    }

    private void makeList(int  position, ViewHolder mHolder) {
        // object item based on the position
        Object item = itemsList.get(position);

        mHolder.activityLL.setTag(item);

        mHolder.activityTransactionLL.setVisibility(View.GONE);
        mHolder.activityTransferLL.setVisibility(View.GONE);

        if(item instanceof TransactionModel){
            mHolder.activityTransactionLL.setVisibility(View.VISIBLE);
            mHolder.activityCategoryIV.setBackgroundResource(Integer.parseInt(((TransactionModel) item).getCategoryImg()));
            mHolder.activityTransactionNameTV.setText(((TransactionModel) item).getTRAN_NAME());
            mHolder.activityCategoryTV.setText(((TransactionModel) item).getCategory());
            mHolder.activityAccountTV.setText(((TransactionModel) item).getAccount());
            mHolder.activityCurrencyCodeTV.setText(userObject.getCUR_CODE());
            mHolder.activityTransactionAmtTV = FinappleUtility.formatAmountView(mHolder.activityTransactionAmtTV, userObject, ((TransactionModel) item).getTRAN_AMT());

            if("EXPENSE".equalsIgnoreCase(((TransactionModel) item).getTRAN_TYPE())){
                mHolder.activityTransactionAmtTV.setTextColor(mHolder.activityTransactionAmtTV.getResources().getColor(R.color.negativeButtonColor));
            }

            //set font for all the text view
            final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
            setFont(mHolder.activityTransactionLL, robotoCondensedLightFont);

        }
        else if(item instanceof TransferModel){
            mHolder.activityTransferLL.setVisibility(View.VISIBLE);
            mHolder.activityTransferFromAccIV.setBackgroundResource(Integer.parseInt(((TransferModel) item).getFromAccImg()));
            mHolder.activityTransferToAccIV.setBackgroundResource(Integer.parseInt(((TransferModel) item).getToAccImg()));
            mHolder.activityTransferFromAccTV.setText(((TransferModel) item).getFromAccName());
            mHolder.activityTransferCurrCodeTV.setText(userObject.getCUR_CODE());
            mHolder.activityTransferToAccTV.setText(((TransferModel) item).getToAccName());
            mHolder.activityTransferAmtTV = FinappleUtility.formatAmountView(mHolder.activityTransferAmtTV, userObject, ((TransferModel) item).getTRNFR_AMT());

            //set font for all the text view
            final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
            setFont(mHolder.activityTransferLL, robotoCondensedLightFont);
        }
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
        private LinearLayout activityLL;

        //Transaction
        private LinearLayout activityTransactionLL;
        private ImageView activityCategoryIV;
        private TextView activityTransactionNameTV, activityCategoryTV, activityAccountTV, activityCurrencyCodeTV, activityTransactionAmtTV;

        //Transfer
        private LinearLayout activityTransferLL;
        private ImageView activityTransferFromAccIV, activityTransferToAccIV;
        private TextView activityTransferFromAccTV, activityTransferCurrCodeTV, activityTransferAmtTV, activityTransferToAccTV;

    }

}