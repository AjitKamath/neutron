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
import com.finappl.models.CountryMO;

import java.util.List;

import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 17/1/15.
 */
public class SelectCountryFragmentListViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private String selectedCountryIdStr;
    private List<CountryMO> countriesList;

    public SelectCountryFragmentListViewAdapter(Context mContext, List<CountryMO> countriesList, String selectedCountryIdStr) {
        super();

        this.mContext = mContext;
        this.selectedCountryIdStr = selectedCountryIdStr;
        this.countriesList = countriesList;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.select_country_country;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.countryLL = (LinearLayout) convertView.findViewById(R.id.countryLLId);
            mHolder.countryIV = (ImageView) convertView.findViewById(R.id.countryIVId);
            mHolder.countryTV = (TextView) convertView.findViewById(R.id.countryTVId);
            mHolder.countrySelectedV = convertView.findViewById(R.id.countrySelectedVId);
            mHolder.countryCodeTV = (TextView) convertView.findViewById(R.id.countryCodeTVId);
            mHolder.countryCurrencyTV = (TextView) convertView.findViewById(R.id.countryCurrencyTVId);
            mHolder.countryCurrencyCodeTV = (TextView) convertView.findViewById(R.id.countryCurrencyCodeTVId);
            mHolder.countryMetricTV = (TextView) convertView.findViewById(R.id.countryMetricTVId);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        CountryMO country = countriesList.get(position);

        mHolder.countryIV.setBackgroundResource(Integer.parseInt(country.getCNTRY_IMG()));
        mHolder.countryTV.setText(country.getCNTRY_NAME());
        mHolder.countryCodeTV.setText("+"+country.getCNTRY_CODE());
        mHolder.countryCurrencyCodeTV.setText(country.getCUR_CODE());
        mHolder.countryCurrencyTV.setText(country.getCUR());
        mHolder.countryMetricTV.setText(country.getMETRIC());

        if(selectedCountryIdStr.equalsIgnoreCase(country.getCNTRY_ID())){
            mHolder.countrySelectedV.setVisibility(View.VISIBLE);
        }
        else{
            mHolder.countrySelectedV.setVisibility(View.INVISIBLE);
        }

        setFont(mHolder.countryLL);

        return convertView;
    }

    @Override
    public int getCount() {
        return countriesList.size();
    }

    @Override
    public CountryMO getItem(int position) {
        return countriesList.get(position);
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
        LinearLayout countryLL;
        ImageView countryIV;
        TextView countryTV;
        TextView countryCodeTV;
        TextView countryCurrencyTV;
        TextView countryCurrencyCodeTV;
        TextView countryMetricTV;
        View countrySelectedV;
    }
}