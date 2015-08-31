package com.finappl.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.BudgetModel;

import java.util.List;

/**
 * Created by ajit on 5/4/15.
 */
public class BudgetsAdapter extends ArrayAdapter<BudgetModel> {

    private Context mContext;
    private int layoutResourceId;
    private List<BudgetModel> dataList;

    public BudgetsAdapter(Context mContext, int layoutResourceId, List<BudgetModel> data) {
        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        BudgetModel budgetItem = dataList.get(position);

        LinearLayout budgetLL = (LinearLayout) convertView.findViewById(R.id.calendarBudgetLLId);
        LinearLayout budgetValLL = (LinearLayout) convertView.findViewById(R.id.calendarBdgValLLId);
        TextView calendarBdgTypeTV = (TextView) convertView.findViewById(R.id.calendarBdgTypeTVId);
        TextView calendarBdgGrpTypeTV = (TextView) convertView.findViewById(R.id.calendarBdgGrpTypeTVId);
        TextView calendarBdgApprxTV = (TextView) convertView.findViewById(R.id.calendarBdgApprxTVId);
        TextView calendarBdgTotalSpntTV = (TextView) convertView.findViewById(R.id.calendarBdgTotalSpntTVId);
        TextView calendarBdgCapTV = (TextView) convertView.findViewById(R.id.calendarBdgCapTVId);

        //decide color based on ACC_TOTAL
        String monthExpTotalStr = String.valueOf(budgetItem.getMonthExpenseTotal());

        //set dim red background if the total month expense exceeds the budget set
        if(budgetItem.getMonthExpenseTotal() > budgetItem.getBUDGET_AMT()){
            calendarBdgTotalSpntTV.setTextColor(calendarBdgTotalSpntTV.getResources().getColor(R.color.negativeButtonColor));
        }

        //TODO: Approximatization required

        budgetLL.setTag(budgetItem.getBUDGET_ID());

        //set color of calendarBdgTypeTV according to the budget type
        if("DAILY".equalsIgnoreCase(budgetItem.getBUDGET_TYPE())){
            calendarBdgTypeTV.setBackgroundResource(R.drawable.circle_day_text_view);
            calendarBdgTypeTV.setText("D");
        }
        else if("WEEKLY".equalsIgnoreCase(budgetItem.getBUDGET_TYPE())) {
            calendarBdgTypeTV.setBackgroundResource(R.drawable.circle_week_text_view);
            calendarBdgTypeTV.setText("W");
        }
        else if("MONTHLY".equalsIgnoreCase(budgetItem.getBUDGET_TYPE())){
            calendarBdgTypeTV.setBackgroundResource(R.drawable.circle_month_text_view);
            calendarBdgTypeTV.setText("M");
        }
        else if("YEARLY".equalsIgnoreCase(budgetItem.getBUDGET_TYPE())){
            calendarBdgTypeTV.setBackgroundResource(R.drawable.circle_year_text_view);
            calendarBdgTypeTV.setText("Y");
        }

        calendarBdgGrpTypeTV.setText(budgetItem.getBUDGET_NAME());
        calendarBdgApprxTV.setVisibility(View.GONE);

        calendarBdgTotalSpntTV.setText(monthExpTotalStr);
        calendarBdgCapTV.setText(String.valueOf(budgetItem.getBUDGET_AMT()));

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) convertView.findViewById(R.id.calendarBudgetLLId), robotoCondensedLightFont);

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

}