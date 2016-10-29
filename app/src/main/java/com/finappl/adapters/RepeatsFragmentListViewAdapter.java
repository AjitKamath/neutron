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
import com.finappl.models.CategoryMO;
import com.finappl.models.RepeatMO;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class RepeatsFragmentListViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private String selectedRepaetIdStr;
    private List<RepeatMO> repeatsList;

    public RepeatsFragmentListViewAdapter(Context mContext, List<RepeatMO> repeatsList, String selectedRepaetIdStr) {
        super();

        this.mContext = mContext;
        this.selectedRepaetIdStr = selectedRepaetIdStr;
        this.repeatsList = repeatsList;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.repeat;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.repeatRL = (RelativeLayout) convertView.findViewById(R.id.repeatRLId);
            mHolder.repeatIV = (ImageView) convertView.findViewById(R.id.repeatIVId);
            mHolder.repeatTV = (TextView) convertView.findViewById(R.id.repeatTVId);
            mHolder.repeatSelectedV = convertView.findViewById(R.id.repeatSelectedVId);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        RepeatMO repeatMO = repeatsList.get(position);
        mHolder.repeatTV.setText(repeatMO.getREPEAT_NAME());
        mHolder.repeatIV.setBackgroundResource(Integer.parseInt(repeatMO.getREPEAT_IMG()));
        if(selectedRepaetIdStr.equalsIgnoreCase(repeatMO.getREPEAT_ID())){
            mHolder.repeatSelectedV.setVisibility(View.VISIBLE);
        }
        else{
            mHolder.repeatSelectedV.setVisibility(View.INVISIBLE);
        }


        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.repeatRL, robotoCondensedLightFont);

        return convertView;
    }

    @Override
    public int getCount() {
        return repeatsList.size();
    }

    @Override
    public RepeatMO getItem(int position) {
        return repeatsList.get(position);
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
        RelativeLayout repeatRL;
        ImageView repeatIV;
        TextView repeatTV;
        View repeatSelectedV;
    }

}