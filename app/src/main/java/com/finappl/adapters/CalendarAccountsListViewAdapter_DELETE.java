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
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.util.List;

import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 17/1/15.
 */
public class CalendarAccountsListViewAdapter_DELETE extends BaseAdapter {

    private Context mContext;
    private int layoutResourceId;
    private List<AccountMO> dataList;
    private List<Integer> colorList;
    private LayoutInflater inflater;

    private UserMO user;

    public CalendarAccountsListViewAdapter_DELETE(Context mContext, int layoutResourceId, List<AccountMO> data, UserMO user) {
        super();

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = data;
        this.user = user;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get random colors in a list
        colorList = FinappleUtility.getInstance().getUnRandomizedColorList(dataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layoutResourceId, null);

            mHolder.summayAccountsLL = (LinearLayout) convertView.findViewById(R.id.summayAccountsLLId);
            mHolder.accountIV = (ImageView) convertView.findViewById(R.id.accountIVId);
            mHolder.accountsCurrTV = (TextView) convertView.findViewById(R.id.accountsCurrTVId);
            mHolder.accountNameTV = (TextView) convertView.findViewById(R.id.accountsNameTVId);
            mHolder.accountsCurrTV = (TextView) convertView.findViewById(R.id.accountsCurrTVId);
            mHolder.accountTotalTV = (TextView) convertView.findViewById(R.id.accountsTotalTVId);

            convertView.setTag(layoutResourceId, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layoutResourceId);
        }

        // object item based on the position
        AccountMO accountItem = dataList.get(position);

        mHolder.summayAccountsLL.setTag(accountItem);

        mHolder.accountIV.setBackgroundResource(Integer.parseInt(accountItem.getACC_IMG()));
        mHolder.accountNameTV.setText(accountItem.getACC_NAME());

        mHolder.accountsCurrTV.setText(user.getCUR_CODE());
        mHolder.accountTotalTV = FinappleUtility.formatAmountView(mHolder.accountTotalTV, user, accountItem.getACC_TOTAL());

        setFont(mHolder.summayAccountsLL);

        return convertView;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public AccountMO getItem(int position) {
        return dataList.get(position);
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

    private class ViewHolder {
        LinearLayout summayAccountsLL;
        ImageView accountIV;
        TextView accountNameTV;
        TextView accountsCurrTV;
        TextView accountTotalTV;
    }

}