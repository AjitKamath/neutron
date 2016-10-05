
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
import com.finappl.models.CountryMO;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class CountriesSpinnerAdapter extends BaseAdapter {
    private String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private List<CountryMO> itemsList = new ArrayList<>();
    private LayoutInflater inflater;
    private int layout;

    public CountriesSpinnerAdapter(Context mContext, List<CountryMO> itemsList) {
        super();
        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.itemsList = itemsList;

        layout = R.layout.country_spinner_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.countryLL = (LinearLayout) convertView.findViewById(R.id.countryLLId);
            mHolder.countryNameTV = (TextView) convertView.findViewById(R.id.countryNameTVId);
            mHolder.countryCurrencyCodeTV = (TextView) convertView.findViewById(R.id.countryCurrencyCodeTVId);

            convertView.setTag(layout, mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        makeList(position, mHolder);
        return convertView;
    }

    private void makeList(int position, ViewHolder mHolder) {
        // object item based on the position
        CountryMO item = itemsList.get(position);

        mHolder.countryLL.setTag(item);
        mHolder.countryNameTV.setText(item.getCNTRY_NAME().toUpperCase());
        mHolder.countryCurrencyCodeTV.setText(item.getCUR_CODE().toUpperCase());

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.countryLL, robotoCondensedLightFont);
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsList.get(position);
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
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v, font);
            }
        }
    }

    private class ViewHolder {
        private LinearLayout countryLL;
        private TextView countryNameTV;
        private TextView countryCurrencyCodeTV;
    }
}