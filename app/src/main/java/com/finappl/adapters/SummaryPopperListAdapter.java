package com.finappl.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.utils.FinappleUtility;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by ajit on 17/1/15.
 */
public class SummaryPopperListAdapter extends BaseAdapter {
    private String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private int layoutResourceId;
    private List<Object> itemsList = new ArrayList<>();
    private LayoutInflater inflater;

    public SummaryPopperListAdapter(Context mContext, int layoutResourceId, List<Object> itemsList) {
        super();
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.itemsList = itemsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layoutResourceId, null);

            mHolder.summaryPopperTileLL = (LinearLayout) convertView.findViewById(R.id.summaryPopperTileLLId);
            mHolder.summaryPopperTransferLL = (LinearLayout) convertView.findViewById(R.id.summaryPopperTransferLLId);
            mHolder.summaryPopperFromAccTV = (TextView) convertView.findViewById(R.id.summaryPopperFromAccountTVId);
            mHolder.summaryPopperToAccTV = (TextView) convertView.findViewById(R.id.summaryPopperToAccountTVId);
            mHolder.summaryPopperTypTV = (TextView) convertView.findViewById(R.id.summaryPopperTypTVId);
            mHolder.summaryPopperNameTV = (TextView) convertView.findViewById(R.id.summaryPopperNameTVId);
            mHolder.summaryPopperAmtTV = (TextView) convertView.findViewById(R.id.summaryPopperAmtTVId);
            mHolder.summaryPopperAccTV = (TextView) convertView.findViewById(R.id.summaryPopperAccTVId);
            mHolder.summaryPopperTimeTV = (TextView) convertView.findViewById(R.id.summaryPopperTimeTVId);

            convertView.setTag(layoutResourceId, mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag(layoutResourceId);
        }

        makeList(position, mHolder);
        return convertView;
    }

    private void makeList(int position, ViewHolder mHolder) {
        // object item based on the position
        Object item = itemsList.get(position);

        if(item instanceof TransactionModel){
            TransactionModel tranItem = (TransactionModel) item;

            mHolder.summaryPopperTileLL.setTag(tranItem);
            mHolder.summaryPopperTypTV.setBackgroundResource(R.drawable.circle_calendar_transaction_indicator);
            mHolder.summaryPopperNameTV.setVisibility(View.VISIBLE);
            mHolder.summaryPopperTransferLL.setVisibility(View.GONE);
            mHolder.summaryPopperNameTV.setText(tranItem.getTRAN_NAME());
            mHolder.summaryPopperAccTV.setVisibility(View.VISIBLE);
            mHolder.summaryPopperAccTV.setText(tranItem.getAccount());

            if("EXPENSE".equalsIgnoreCase(tranItem.getTRAN_TYPE())){
                mHolder.summaryPopperAmtTV.setText("-"+String.valueOf(tranItem.getTRAN_AMT()));
                mHolder.summaryPopperAmtTV.setTextColor(mHolder.summaryPopperAmtTV.getResources().getColor(R.color.finappleCurrencyNegColor));
            }
            else{
                mHolder.summaryPopperAmtTV.setText(String.valueOf(tranItem.getTRAN_AMT()));
                mHolder.summaryPopperAmtTV.setTextColor(mHolder.summaryPopperAmtTV.getResources().getColor(R.color.finappleCurrencyPosColor));
            }

            //TODO:APPROXIMATIZATION

            SimpleDateFormat sdfRight = new SimpleDateFormat("h:mm a");

            if(tranItem.getMOD_DTM() != null) {
                mHolder.summaryPopperTimeTV.setText(sdfRight.format(tranItem.getMOD_DTM()));
            }
            else{
                mHolder.summaryPopperTimeTV.setText(sdfRight.format(tranItem.getCREAT_DTM()));
            }
        }
        else if(item instanceof TransferModel){
            TransferModel trfrItem = (TransferModel) item;

            mHolder.summaryPopperTileLL.setTag(trfrItem);
            mHolder.summaryPopperTypTV.setBackgroundResource(R.drawable.circle_calendar_transfer_indicator);
            mHolder.summaryPopperNameTV.setVisibility(View.GONE);
            mHolder.summaryPopperTransferLL.setVisibility(View.VISIBLE);
            mHolder.summaryPopperFromAccTV.setText(trfrItem.getFromAccName());
            mHolder.summaryPopperToAccTV.setText(trfrItem.getToAccName());
            mHolder.summaryPopperAccTV.setVisibility(View.GONE);

            mHolder.summaryPopperAmtTV.setText(String.valueOf(trfrItem.getTRNFR_AMT()));
            mHolder.summaryPopperAmtTV.setTextColor(mHolder.summaryPopperAmtTV.getResources().getColor(R.color.finappleCurrencyPosColor));

            //TODO:APPROXIMATIZATION

            SimpleDateFormat sdfRight = new SimpleDateFormat("h:mm a");

            if(trfrItem.getMOD_DTM() != null) {
                mHolder.summaryPopperTimeTV.setText(sdfRight.format(trfrItem.getMOD_DTM()));
            }
            else{
                mHolder.summaryPopperTimeTV.setText(sdfRight.format(trfrItem.getCREAT_DTM()));
            }
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.summaryPopperTileLL, robotoCondensedLightFont);
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

    private class ViewHolder {
        private LinearLayout summaryPopperTileLL;
        private LinearLayout summaryPopperTransferLL;
        private TextView summaryPopperFromAccTV;
        private TextView summaryPopperToAccTV;
        private TextView summaryPopperTypTV;
        private TextView summaryPopperNameTV;
        private TextView summaryPopperAmtTV;
        private TextView summaryPopperAccTV;
        private TextView summaryPopperTimeTV;
    }
}