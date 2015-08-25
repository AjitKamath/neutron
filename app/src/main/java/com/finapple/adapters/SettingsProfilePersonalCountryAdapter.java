package com.finapple.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finapple.R;
import com.finapple.model.CountryModel;
import com.finapple.util.FinappleUtility;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class SettingsProfilePersonalCountryAdapter extends BaseAdapter {

    private Context mContext;
    private int layoutResourceId;
    private List<CountryModel> dataList;
    private List<Integer> colorList;

    public SettingsProfilePersonalCountryAdapter(Context mContext, int layoutResourceId, List<CountryModel> data) {

        super();

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = data;

        //get random colors in a list
        colorList = FinappleUtility.getInstance().getUnRandomizedColorList(dataList.size());
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        CountryModel item = dataList.get(position);

        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.settingsProfilePersonalCountryLLId);
        ImageView image = (ImageView) convertView.findViewById(R.id.settingsProfilePersonalCountryFlagIVId);
        TextView name = (TextView) convertView.findViewById(R.id.settingsProfilePersonalCountryNameTVId);

        linearLayout.setTag(item.getCNTRY_ID());
        name.setText(item.getCNTRY_NAME());

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) convertView.findViewById(R.id.settingsProfilePersonalCountryLLId), robotoCondensedLightFont);

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