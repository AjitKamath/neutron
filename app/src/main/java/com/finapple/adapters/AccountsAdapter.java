package com.finapple.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finapple.R;
import com.finapple.model.AccountsModel;
import com.finapple.util.FinappleUtility;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class AccountsAdapter extends ArrayAdapter<AccountsModel> {

    private Context mContext;
    private int layoutResourceId;
    private List<AccountsModel> dataList;
    private List<Integer> colorList;
    private LayoutInflater inflater;

    public AccountsAdapter(Context mContext, int layoutResourceId, List<AccountsModel> data) {
        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = data;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get random colors in a list
        colorList = FinappleUtility.getInstance().getUnRandomizedColorList(dataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;

        if(convertView == null) {
            convertView = inflater.inflate(layoutResourceId, null);

            mHolder = new ViewHolder();

            mHolder.summayAccountsLL = (LinearLayout) convertView.findViewById(R.id.summayAccountsLLId);
            mHolder.accountNameTV = (TextView) convertView.findViewById(R.id.accountsNameTVId);
            mHolder.accountTotalTV = (TextView) convertView.findViewById(R.id.accountsTotalTVId);
            mHolder.accountsAmtStatView = convertView.findViewById(R.id.accountsAmtStatViewId);

            //set font for all the text view
            final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
            setFont(mHolder.summayAccountsLL, robotoCondensedLightFont);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        // object item based on the position
        AccountsModel accountItem = dataList.get(position);

        mHolder.accountTotalTV.setText(String.valueOf(accountItem.getACC_TOTAL()));
        mHolder.accountNameTV.setText(accountItem.getACC_NAME());

        if(accountItem.getACC_TOTAL() <= 0){
            mHolder.accountTotalTV.setTextColor(mHolder.accountTotalTV.getResources().getColor(R.color.finappleCurrencyNegColor));
            mHolder.accountsAmtStatView.setBackgroundResource(R.drawable.capsule_vertical_negative_view);
        }
        else{
            mHolder.accountTotalTV.setTextColor(mHolder.accountTotalTV.getResources().getColor(R.color.finappleCurrencyPosColor));
            mHolder.accountsAmtStatView.setBackgroundResource(R.drawable.capsule_vertical_positive_view);
        }
        //TODO: Approximatization required

        return convertView;
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