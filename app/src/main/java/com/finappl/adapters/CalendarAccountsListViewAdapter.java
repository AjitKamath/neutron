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
public class CalendarAccountsListViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private LayoutInflater inflater;

    private UserMO user;
    private List<AccountMO> accountsList;
    private View.OnClickListener clickListener;

    public CalendarAccountsListViewAdapter(Context mContext, UserMO user, List<AccountMO> accountsList, View.OnClickListener clickListener) {
        super();

        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.accountsList = accountsList;
        this.clickListener = clickListener;
        this.user = user;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.calendar_accounts_list_item;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.calendar_accounts_list_item_ll = (LinearLayout) convertView.findViewById(R.id.calendar_accounts_list_item_ll);
            mHolder.calendar_accounts_list_item_account_iv = (ImageView) convertView.findViewById(R.id.calendar_accounts_list_item_account_iv);
            mHolder.calendar_accounts_list_item_account_tv = (TextView) convertView.findViewById(R.id.calendar_accounts_list_item_account_tv);
            mHolder.calendar_accounts_list_item_amt_tv = (TextView) convertView.findViewById(R.id.calendar_accounts_list_item_amt_tv);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        AccountMO account = accountsList.get(position);

        mHolder.calendar_accounts_list_item_account_iv.setBackgroundResource(Integer.parseInt(account.getACC_IMG()));
        mHolder.calendar_accounts_list_item_account_tv.setText(account.getACC_NAME());
        mHolder.calendar_accounts_list_item_amt_tv = FinappleUtility.formatAmountView(mHolder.calendar_accounts_list_item_amt_tv, user, account.getACC_TOTAL());

        setFont(mHolder.calendar_accounts_list_item_ll);

        return convertView;
    }


    private class ViewHolder {
        private LinearLayout calendar_accounts_list_item_ll;
        private ImageView calendar_accounts_list_item_account_iv;
        private TextView calendar_accounts_list_item_account_tv;
        private TextView calendar_accounts_list_item_amt_tv;
    }

    @Override
    public int getCount() {
        return accountsList.size();
    }

    @Override
    public AccountMO getItem(int position) {
        return accountsList.get(position);
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
}