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
import com.finappl.models.BudgetMO;
import com.finappl.utils.FinappleUtility;

import java.util.List;

/**
 * Created by ajit on 5/4/15.
 */
public class CalendarBudgetsListViewAdapter extends BaseAdapter {

    private Context mContext;
    private int layoutResourceId;
    private List<BudgetMO> dataList;
    private LayoutInflater inflater;

    public CalendarBudgetsListViewAdapter(Context mContext, int layoutResourceId, List<BudgetMO> data) {
        super();

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = data;
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

            mHolder.budgetLL = (LinearLayout) convertView.findViewById(R.id.calendarBudgetLLId);
            mHolder.budgetValLL = (LinearLayout) convertView.findViewById(R.id.calendarBdgValLLId);
            mHolder.calendarBdgTypeTV = (TextView) convertView.findViewById(R.id.calendarBdgTypeTVId);
            mHolder.calendarBdgGrpTypeTV = (TextView) convertView.findViewById(R.id.calendarBdgGrpTypeTVId);
            mHolder.calendarBdgApprxTV = (TextView) convertView.findViewById(R.id.calendarBdgApprxTVId);
            mHolder.calendarBdgTotalSpntTV = (TextView) convertView.findViewById(R.id.calendarBdgTotalSpntTVId);
            mHolder.calendarBdgCapTV = (TextView) convertView.findViewById(R.id.calendarBdgCapTVId);

            convertView.setTag(layoutResourceId, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layoutResourceId);
        }

        // object item based on the position
        BudgetMO budgetItem = dataList.get(position);

        //decide color based on ACC_TOTAL
        String monthExpTotalStr = String.valueOf(budgetItem.getBudgetRangeTotal());

        //set dim red background if the total range total exceeds the budget set
        if(budgetItem.getBudgetRangeTotal() > budgetItem.getBUDGET_AMT()){
            mHolder.calendarBdgTotalSpntTV.setTextColor(mHolder.calendarBdgTotalSpntTV.getResources().getColor(R.color.negativeButtonColor));
        }

        //TODO: Approximatization required

        mHolder.budgetLL.setTag(budgetItem);

        //set color of calendarBdgTypeTV according to the budget type
        if("DAILY".equalsIgnoreCase(budgetItem.getBUDGET_TYPE())){
            mHolder.calendarBdgTypeTV.setBackgroundResource(R.drawable.circle_day_text_view);
            mHolder.calendarBdgTypeTV.setText("D");
        }
        else if("WEEKLY".equalsIgnoreCase(budgetItem.getBUDGET_TYPE())) {
            mHolder.calendarBdgTypeTV.setBackgroundResource(R.drawable.circle_week_text_view);
            mHolder.calendarBdgTypeTV.setText("W");
        }
        else if("MONTHLY".equalsIgnoreCase(budgetItem.getBUDGET_TYPE())){
            mHolder.calendarBdgTypeTV.setBackgroundResource(R.drawable.circle_month_text_view);
            mHolder.calendarBdgTypeTV.setText("M");
        }
        else if("YEARLY".equalsIgnoreCase(budgetItem.getBUDGET_TYPE())){
            mHolder.calendarBdgTypeTV.setBackgroundResource(R.drawable.circle_year_text_view);
            mHolder.calendarBdgTypeTV.setText("Y");
        }

        mHolder.calendarBdgGrpTypeTV.setText(budgetItem.getBUDGET_NAME());
        mHolder.calendarBdgApprxTV.setVisibility(View.GONE);

        mHolder.calendarBdgTotalSpntTV.setText(monthExpTotalStr);
        mHolder.calendarBdgCapTV.setText(String.valueOf(budgetItem.getBUDGET_AMT()));

        //TODO: Approximatization required

        //this is to offset the last item to allow its content to be viewed by scrolling
        if(position == dataList.size()-1){
            mHolder.budgetLL.setPadding(0, 0, 0, FinappleUtility.getInstance().getDpAsPixels(mContext.getResources(), 65));
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.budgetLL, robotoCondensedLightFont);

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

    private class ViewHolder {
        private LinearLayout budgetLL;
        private LinearLayout budgetValLL;
        private TextView calendarBdgTypeTV;
        private TextView calendarBdgGrpTypeTV;
        private TextView calendarBdgApprxTV;
        private TextView calendarBdgTotalSpntTV;
        private TextView calendarBdgCapTV;
    }

}