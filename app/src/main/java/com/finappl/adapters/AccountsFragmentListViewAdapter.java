package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.AccountsMO;
import com.finappl.models.CategoryMO;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class AccountsFragmentListViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private String selectedAccountIdStr;
    private List<AccountsMO> accountsList;

    public AccountsFragmentListViewAdapter(Context mContext, List<AccountsMO> accountsList, String selectedAccountIdStr) {
        super();

        this.mContext = mContext;
        this.selectedAccountIdStr = selectedAccountIdStr;
        this.accountsList = accountsList;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.account;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.accountRL = (RelativeLayout) convertView.findViewById(R.id.accountRLId);
            mHolder.accountIV = (ImageView) convertView.findViewById(R.id.accountIVId);
            mHolder.accountTV = (TextView) convertView.findViewById(R.id.accountTVId);
            mHolder.accountSelectedV = (View) convertView.findViewById(R.id.accountSelectedVId);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        AccountsMO accountMO = accountsList.get(position);
        mHolder.accountTV.setText(accountMO.getACC_NAME());
        if(selectedAccountIdStr.equalsIgnoreCase(accountMO.getACC_ID())){
            mHolder.accountSelectedV.setVisibility(View.VISIBLE);
        }
        else{
            mHolder.accountSelectedV.setVisibility(View.INVISIBLE);
        }


        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.accountRL, robotoCondensedLightFont);

        return convertView;
    }

    @Override
    public int getCount() {
        return accountsList.size();
    }

    @Override
    public AccountsMO getItem(int position) {
        return accountsList.get(position);
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
        RelativeLayout accountRL;
        ImageView accountIV;
        TextView accountTV;
        View accountSelectedV;
    }

}