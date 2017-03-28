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
import com.finappl.models.BudgetMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.util.List;

import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 17/1/15.
 */
public class BudgetsFragmentListViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private LayoutInflater inflater;

    private UserMO user;
    private List<BudgetMO> budgetsList;
    private View.OnClickListener clickListener;

    public BudgetsFragmentListViewAdapter(Context mContext, UserMO user, List<BudgetMO> budgetsList, View.OnClickListener clickListener) {
        super();

        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.budgetsList = budgetsList;
        this.clickListener = clickListener;
        this.user = user;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.budgets_budget;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.budget_ll = (LinearLayout) convertView.findViewById(R.id.budget_ll);
            mHolder.budget_iv = (ImageView) convertView.findViewById(R.id.budget_iv);
            mHolder.budget_tv = (TextView) convertView.findViewById(R.id.budget_tv);
            mHolder.budget_amount_tv = (TextView) convertView.findViewById(R.id.budget_amount_tv);
            mHolder.budget_type_tv = (TextView) convertView.findViewById(R.id.budget_type_tv);
            mHolder.budget_for_tv = (TextView) convertView.findViewById(R.id.budget_for_tv);
            mHolder.budget_for_name_tv = (TextView) convertView.findViewById(R.id.budget_for_name_tv);
            mHolder.budget_notes_tv = (TextView) convertView.findViewById(R.id.budget_notes_tv);
            mHolder.budget_delete_iv = (ImageView) convertView.findViewById(R.id.budget_delete_iv);
            mHolder.budget_modify_iv = (ImageView) convertView.findViewById(R.id.budget_modify_iv);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        BudgetMO budget = budgetsList.get(position);

        mHolder.budget_tv.setText(budget.getBUDGET_NAME());
        mHolder.budget_iv.setBackgroundResource(Integer.parseInt(budget.getBudgetGroupImage()));
        mHolder.budget_amount_tv = FinappleUtility.formatAmountView(mHolder.budget_amount_tv, user, budget.getBUDGET_AMT());
        mHolder.budget_type_tv.setText(budget.getBUDGET_TYPE());
        mHolder.budget_for_tv.setText(budget.getBUDGET_GRP_TYPE());
        mHolder.budget_for_name_tv.setText(budget.getBudgetGroupName());

        if(budget.getBUDGET_NOTE() == null || budget.getBUDGET_NOTE().trim().isEmpty()){
            mHolder.budget_notes_tv.setVisibility(View.GONE);
        }
        else{
            mHolder.budget_notes_tv.setText(budget.getBUDGET_NOTE());
        }

        mHolder.budget_delete_iv.setOnClickListener(clickListener);
        mHolder.budget_modify_iv.setOnClickListener(clickListener);

        mHolder.budget_delete_iv.setTag(layout, budget);
        mHolder.budget_modify_iv.setTag(layout, budget);

        setFont(mHolder.budget_ll);

        return convertView;
    }

    @Override
    public int getCount() {
        return budgetsList.size();
    }

    @Override
    public BudgetMO getItem(int position) {
        return budgetsList.get(position);
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
        private LinearLayout budget_ll;
        private ImageView budget_iv;
        private TextView budget_tv;
        private TextView budget_amount_tv;
        private TextView budget_type_tv;
        private TextView budget_for_tv;
        private TextView budget_for_name_tv;
        private TextView budget_notes_tv;
        private ImageView budget_delete_iv;
        private ImageView budget_modify_iv;
    }

}