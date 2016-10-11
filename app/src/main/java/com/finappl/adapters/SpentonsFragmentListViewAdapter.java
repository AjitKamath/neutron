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
import com.finappl.models.SpentOnMO;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class SpentonsFragmentListViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private String selectedSpentonIdStr;
    private List<SpentOnMO> spentonsList;

    public SpentonsFragmentListViewAdapter(Context mContext, List<SpentOnMO> spentonsList, String selectedSpentonIdStr) {
        super();

        this.mContext = mContext;
        this.selectedSpentonIdStr = selectedSpentonIdStr;
        this.spentonsList = spentonsList;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.spenton;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.spentonRL = (RelativeLayout) convertView.findViewById(R.id.spentonRLId);
            mHolder.spentonIV = (ImageView) convertView.findViewById(R.id.spentonIVId);
            mHolder.spentonTV = (TextView) convertView.findViewById(R.id.spentonTVId);
            mHolder.spentonSelectedV = (View) convertView.findViewById(R.id.spentonSelectedVId);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        SpentOnMO spentOnMO = spentonsList.get(position);
        mHolder.spentonTV.setText(spentOnMO.getSPNT_ON_NAME());
        if(selectedSpentonIdStr.equalsIgnoreCase(spentOnMO.getSPNT_ON_ID())){
            mHolder.spentonSelectedV.setVisibility(View.VISIBLE);
        }
        else{
            mHolder.spentonSelectedV.setVisibility(View.INVISIBLE);
        }


        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.spentonRL, robotoCondensedLightFont);

        return convertView;
    }

    @Override
    public int getCount() {
        return spentonsList.size();
    }

    @Override
    public SpentOnMO getItem(int position) {
        return spentonsList.get(position);
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
        RelativeLayout spentonRL;
        ImageView spentonIV;
        TextView spentonTV;
        View spentonSelectedV;
    }

}