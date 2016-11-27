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
public class AccountsFragmentListViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private String selectedAccountIdStr;
    private List<AccountMO> accountsList;
    private UserMO loggedInUserMO;

    public AccountsFragmentListViewAdapter(Context mContext, List<AccountMO> accountsList, String selectedAccountIdStr, UserMO loggedInUserMO) {
        super();

        this.mContext = mContext;
        this.selectedAccountIdStr = selectedAccountIdStr;
        this.accountsList = accountsList;
        this.loggedInUserMO = loggedInUserMO;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.account;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.accountLL = (LinearLayout) convertView.findViewById(R.id.accountLLId);
            mHolder.accountIV = (ImageView) convertView.findViewById(R.id.accountIVId);
            mHolder.accountTV = (TextView) convertView.findViewById(R.id.accountTVId);
            mHolder.accountSelectedV = convertView.findViewById(R.id.accountSelectedVId);
            mHolder.accountCurrencyCodeTV = (TextView) convertView.findViewById(R.id.accountCurrencyCodeTVId);
            mHolder.accountTotalTV = (TextView) convertView.findViewById(R.id.accountTotalTVId);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        AccountMO accountMO = accountsList.get(position);
        mHolder.accountTV.setText(accountMO.getACC_NAME());
        mHolder.accountCurrencyCodeTV.setText(loggedInUserMO.getCUR_CODE().toUpperCase());
        mHolder.accountIV.setBackgroundResource(Integer.parseInt(accountMO.getACC_IMG()));
        mHolder.accountTotalTV = FinappleUtility.formatAmountView(mHolder.accountTotalTV, loggedInUserMO, accountMO.getACC_TOTAL());

        if(selectedAccountIdStr.equalsIgnoreCase(accountMO.getACC_ID())){
            mHolder.accountSelectedV.setVisibility(View.VISIBLE);
        }
        else{
            mHolder.accountSelectedV.setVisibility(View.INVISIBLE);
        }

        setFont(mHolder.accountLL);

        return convertView;
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
        final Typeface font = Typeface.createFromAsset(mContext.getAssets(), UI_FONT);

        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            } else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v);
            }
        }
    }

    private class ViewHolder {
        LinearLayout accountLL;
        ImageView accountIV;
        TextView accountTV;
        TextView accountCurrencyCodeTV;
        TextView accountTotalTV;
        View accountSelectedV;
    }

}