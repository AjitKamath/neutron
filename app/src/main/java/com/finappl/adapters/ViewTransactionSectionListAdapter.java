package com.finappl.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.DayTransactionsModel;
import com.finappl.models.TransactionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by ajit on 17/1/15.
 */
public class ViewTransactionSectionListAdapter extends BaseAdapter {

    private final String CLASS_NAME = this.getClass().getName();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    private ArrayList<Object> mData = new ArrayList<Object>();
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    private int sectionId;
    private int listId;
    private Map<String, DayTransactionsModel> itemsMap;
    private Context mContext;

    public ViewTransactionSectionListAdapter(Context context, int sectionId, int listId, Map<String, DayTransactionsModel> itemsMap) {
        super();
        this.sectionId = sectionId;
        this.listId = listId;
        this.itemsMap = itemsMap;
        this.mContext = context;

        buildSectionList();
    }

    private void buildSectionList() {
        for(Map.Entry<String, DayTransactionsModel> iterMap : itemsMap.entrySet()) {
            addSeparatorItem(iterMap.getValue());

            List<TransactionModel> transactionsList = iterMap.getValue().getDayTransactionsList();

            for(TransactionModel iterList : transactionsList) {
                addItem(iterList);
            }
        }
    }

    public void addItem(Object item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSeparatorItem(Object item) {
        mData.add(item);
        // save separator position
        mSeparatorsSet.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
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
        int type = getItemViewType(position);

        System.out.println("getView " + position + " " + convertView + " type = " + type);

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

            switch(type) {
                case TYPE_ITEM:
                    convertView = inflater.inflate(listId, null);
                    makeListItem(position, convertView);
                    break;
                case TYPE_SEPARATOR:
                    convertView = inflater.inflate(sectionId, null);
                    makeSectionHeader(position, convertView);
                    break;
            }
        }

        return convertView;
    }

    private void makeListItem(int position, View convertView) {
        TransactionModel transactionObj = (TransactionModel) mData.get(position);

        LinearLayout viewTransactTileLL = (LinearLayout) convertView.findViewById(R.id.viewTransactTileLLId);
        TextView viewTransactTileAmtTV = (TextView) convertView.findViewById(R.id.viewTransactTileAmtTVId);
        TextView viewTransactTileNameTV = (TextView) convertView.findViewById(R.id.viewTransactTileNameTVId);
        TextView viewTransactTileCatTV = (TextView) convertView.findViewById(R.id.viewTransactTileCatTVId);

        //TODO: APPROXIMATIZATION IS REQUIRED

        if("EXPENSE".equalsIgnoreCase(transactionObj.getTRAN_TYPE())){
            viewTransactTileAmtTV.setTextColor(viewTransactTileAmtTV.getResources().getColor(R.color.finappleCurrencyNegColor));
            viewTransactTileAmtTV.setText(String.valueOf("-"+transactionObj.getTRAN_AMT()));
        }
        else{
            viewTransactTileAmtTV.setTextColor(viewTransactTileAmtTV.getResources().getColor(R.color.finappleCurrencyPosColor));
            viewTransactTileAmtTV.setText(String.valueOf(transactionObj.getTRAN_AMT()));
        }

        viewTransactTileLL.setTag(transactionObj.getTRAN_ID());
        viewTransactTileNameTV.setText(transactionObj.getTRAN_NAME());
        viewTransactTileCatTV.setText(transactionObj.getCategory());

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) convertView.findViewById(R.id.viewTransactTileLLId), robotoCondensedLightFont);
    }

    private void makeSectionHeader(int position, View convertView) {
        DayTransactionsModel dayTransactionsModel = (DayTransactionsModel) mData.get(position);

        TextView viewTranHeaderDate = (TextView) convertView.findViewById(R.id.viewTranHeaderDateId);
        TextView viewTransactTileDayTotalTV = (TextView) convertView.findViewById(R.id.viewTransactTileDayTotalTVId);
        TextView viewTransactTileDayCountTV = (TextView) convertView.findViewById(R.id.viewTransactTileDayCountTVId);

        SimpleDateFormat wrongSdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat rightSdf = new SimpleDateFormat("d MMM ''yy");

        String formattedDateStr = "ERROR";
        try{
            formattedDateStr = rightSdf.format(wrongSdf.parse(dayTransactionsModel.getDateStr()));
        }
        catch(ParseException ex){
            Log.e(CLASS_NAME, "That was a bad date you provided...The calendar 'Date' you idiot !!"+ex);
        }

        viewTranHeaderDate.setText(formattedDateStr);
        viewTransactTileDayTotalTV.setText(String.valueOf(dayTransactionsModel.getDayTotal()));
        viewTransactTileDayCountTV.setText(String.valueOf(dayTransactionsModel.getDayTransactionsList().size()));

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) convertView.findViewById(R.id.viewTranHeaderLLId), robotoCondensedLightFont);
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
}