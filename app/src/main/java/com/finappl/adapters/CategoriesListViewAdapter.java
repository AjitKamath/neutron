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
import com.finappl.models.AccountsMO;
import com.finappl.models.CategoryMO;
import com.finappl.utils.FinappleUtility;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class CategoriesListViewAdapter extends BaseAdapter {

    private Context mContext;
    private int layoutResourceId;
    private List<CategoryMO> dataList;
    private LayoutInflater inflater;

    public CategoriesListViewAdapter(Context mContext, int layoutResourceId, List<CategoryMO> dataList) {
        super();

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = dataList;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layoutResourceId, null);

            mHolder.summayAccountsLL = (LinearLayout) convertView.findViewById(R.id.summayAccountsLLId);
            mHolder.accountNameTV = (TextView) convertView.findViewById(R.id.accountsNameTVId);
            mHolder.accountTotalTV = (TextView) convertView.findViewById(R.id.accountsTotalTVId);
            mHolder.accountsAmtStatView = convertView.findViewById(R.id.accountsAmtStatViewId);

            convertView.setTag(layoutResourceId, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layoutResourceId);
        }

//        // object item based on the position
//        AccountsMO accountItem = dataList.get(position);
//
//        mHolder.summayAccountsLL.setTag(accountItem);
//        mHolder.accountTotalTV.setText(String.valueOf(accountItem.getACC_TOTAL()));
//        mHolder.accountNameTV.setText(accountItem.getACC_NAME());
//
//        if(accountItem.getACC_TOTAL() <= 0){
//            mHolder.accountTotalTV.setTextColor(mHolder.accountTotalTV.getResources().getColor(R.color.finappleCurrencyNegColor));
//            mHolder.accountsAmtStatView.setBackgroundResource(R.drawable.capsule_vertical_negative_view);
//        }
//        else{
//            mHolder.accountTotalTV.setTextColor(mHolder.accountTotalTV.getResources().getColor(R.color.finappleCurrencyPosColor));
//            mHolder.accountsAmtStatView.setBackgroundResource(R.drawable.capsule_vertical_positive_view);
//        }

        //TODO: Approximatization required

        //this is to offset the last item to allow its content to be viewed by scrolling
        if(position == dataList.size()-1){
            mHolder.summayAccountsLL.setPadding(FinappleUtility.getInstance().getDpAsPixels(mContext.getResources(), 10),
                    FinappleUtility.getInstance().getDpAsPixels(mContext.getResources(), 10),
                    FinappleUtility.getInstance().getDpAsPixels(mContext.getResources(), 10),
                    FinappleUtility.getInstance().getDpAsPixels(mContext.getResources(), 65));
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.summayAccountsLL, robotoCondensedLightFont);

        return convertView;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public CategoryMO getItem(int position) {
        return dataList.get(position);
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
            } else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v, font);
            }
        }
    }

    private class ViewHolder {
        LinearLayout summayAccountsLL;
        TextView accountNameTV;
        TextView accountTotalTV;
        View accountsAmtStatView;
    }

}