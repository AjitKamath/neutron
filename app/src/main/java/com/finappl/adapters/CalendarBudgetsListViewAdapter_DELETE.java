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
import com.finappl.models.BudgetMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.util.List;

import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 5/4/15.
 */
public class CalendarBudgetsListViewAdapter_DELETE extends BaseAdapter {

    private Context mContext;
    private int layoutResourceId;
    private List<BudgetMO> dataList;
    private LayoutInflater inflater;

    private UserMO user;

    public CalendarBudgetsListViewAdapter_DELETE(Context mContext, int layoutResourceId, List<BudgetMO> data, UserMO user) {
        super();

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = data;
        this.user = user;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public BudgetMO getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layoutResourceId, null);

            mHolder.calendarBudgetLL = (LinearLayout) convertView.findViewById(R.id.calendarBudgetLLId);
            mHolder.calendarBudgetNameTV = (TextView) convertView.findViewById(R.id.calendarBudgetNameTVId);
            mHolder.calendarBudgetGroupIV = (ImageView) convertView.findViewById(R.id.calendarBudgetGroupIVId);
            mHolder.calendarBudgetGroupNameTV = (TextView) convertView.findViewById(R.id.calendarBudgetGroupNameTVId);
            mHolder.calendarBudgetTypeTV = (TextView) convertView.findViewById(R.id.calendarBudgetTypeTVId);
            mHolder.calendarBudgetCurrencyCode1TV = (TextView) convertView.findViewById(R.id.calendarBudgetCurrencyCode1TVId);
            mHolder.calendarBudgetCurrencyCode2TV = (TextView) convertView.findViewById(R.id.calendarBudgetCurrencyCode2TVId);
            mHolder.calendarBudgetTransactionTotalAmtTV = (TextView) convertView.findViewById(R.id.calendarBudgetTransactionTotalAmtTVId);
            mHolder.calendarBudgetTransactionBudgetAmtTV = (TextView) convertView.findViewById(R.id.calendarBudgetTransactionBudgetAmtTVId);

            convertView.setTag(layoutResourceId, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layoutResourceId);
        }

        // object item based on the position
        BudgetMO budget = dataList.get(position);

        mHolder.calendarBudgetLL.setTag(budget);

        Double budgetRangeTotal = budget.getBudgetRangeTotal();

        mHolder.calendarBudgetGroupIV.setBackgroundResource(Integer.parseInt(budget.getBudgetGroupImage()));
        mHolder.calendarBudgetNameTV.setText(budget.getBUDGET_NAME());
        mHolder.calendarBudgetGroupNameTV.setText(budget.getBudgetGroupName());
        mHolder.calendarBudgetTypeTV.setText(budget.getBUDGET_TYPE());
        mHolder.calendarBudgetCurrencyCode1TV.setText(user.getCUR_CODE());
        mHolder.calendarBudgetCurrencyCode2TV.setText(user.getCUR_CODE());

        mHolder.calendarBudgetTransactionTotalAmtTV = FinappleUtility.formatAmountView(mHolder.calendarBudgetTransactionTotalAmtTV, user, budgetRangeTotal);
        mHolder.calendarBudgetTransactionBudgetAmtTV = FinappleUtility.formatAmountView(mHolder.calendarBudgetTransactionBudgetAmtTV, user, budget.getBUDGET_AMT());

        mHolder.calendarBudgetTransactionBudgetAmtTV.setTextColor(mHolder.calendarBudgetTransactionBudgetAmtTV.getResources().getColor(R.color.finappleCurrencyPosColor));

        if(budgetRangeTotal < budget.getBUDGET_AMT()){
            mHolder.calendarBudgetTransactionTotalAmtTV.setTextColor(mHolder.calendarBudgetTransactionTotalAmtTV.getResources().getColor(R.color.finappleCurrencyPosColor));
        }
        else{
            mHolder.calendarBudgetTransactionTotalAmtTV.setTextColor(mHolder.calendarBudgetTransactionTotalAmtTV.getResources().getColor(R.color.finappleCurrencyNegColor));
        }

        setFont(mHolder.calendarBudgetLL);
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
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v, font);
            }
        }
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
        private LinearLayout calendarBudgetLL;
        private TextView calendarBudgetNameTV;
        private ImageView calendarBudgetGroupIV;
        private TextView calendarBudgetGroupNameTV;
        private TextView calendarBudgetTypeTV;
        private TextView calendarBudgetCurrencyCode1TV;
        private TextView calendarBudgetCurrencyCode2TV;
        private TextView calendarBudgetTransactionTotalAmtTV;
        private TextView calendarBudgetTransactionBudgetAmtTV;
    }

}