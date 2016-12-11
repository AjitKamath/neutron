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

import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 17/1/15.
 */
public class AccountsFragmentListViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private LayoutInflater inflater;

    private UserMO user;
    private List<AccountMO> accountsList;
    private View.OnClickListener clickListener;

    public AccountsFragmentListViewAdapter(Context mContext, UserMO user, List<AccountMO> accountsList, View.OnClickListener clickListener) {
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
        int layout = R.layout.accounts_account;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.accountLL = (LinearLayout) convertView.findViewById(R.id.accountLLId);
            mHolder.accountIV = (ImageView) convertView.findViewById(R.id.accountIVId);
            mHolder.accountTV = (TextView) convertView.findViewById(R.id.accountTVId);
            mHolder.accountCurrencyTV = (TextView) convertView.findViewById(R.id.accountCurrencyTVId);
            mHolder.accountAmountTV = (TextView) convertView.findViewById(R.id.accountAmountTVId);
            mHolder.accountDeleteIV = (ImageView) convertView.findViewById(R.id.accountDeleteIVId);
            mHolder.accountModifyIV = (ImageView) convertView.findViewById(R.id.accountModifyIVId);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        AccountMO account = accountsList.get(position);

        mHolder.accountTV.setText(account.getACC_NAME());
        mHolder.accountIV.setBackgroundResource(Integer.parseInt(account.getACC_IMG()));
        mHolder.accountCurrencyTV.setText(user.getCUR_CODE());
        mHolder.accountAmountTV = FinappleUtility.formatAmountView(mHolder.accountAmountTV, user, account.getACC_TOTAL());

        mHolder.accountModifyIV.setTag(account);
        mHolder.accountModifyIV.setOnClickListener(clickListener);

        //do not enable delete if the user id of the account is admin
        if(ADMIN_USERID.equalsIgnoreCase(account.getUSER_ID())){
            mHolder.accountDeleteIV.setVisibility(View.INVISIBLE);
        }
        else{
            mHolder.accountDeleteIV.setVisibility(View.VISIBLE);
            mHolder.accountDeleteIV.setTag(account);
            mHolder.accountDeleteIV.setOnClickListener(clickListener);
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
        private LinearLayout accountLL;
        private ImageView accountIV;
        private TextView accountTV;
        private TextView accountCurrencyTV;
        private TextView accountAmountTV;
        private ImageView accountDeleteIV;
        private ImageView accountModifyIV;
    }

}