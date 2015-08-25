package com.finapple.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.finapple.R;
import com.finapple.model.SpentOnModel;
import com.finapple.util.FinappleUtility;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class ViewSpentOnAdapter extends ArrayAdapter<SpentOnModel> {

    private Context mContext;
    private int layoutResourceId;
    private List<SpentOnModel> dataList;
    private List<Integer> colorList;

    public ViewSpentOnAdapter(Context mContext, int layoutResourceId, List<SpentOnModel> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = data;
        this.colorList = FinappleUtility.getInstance().getUnRandomizedColorList(dataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        SpentOnModel item = dataList.get(position);

        int color = colorList.get(position);

        TextView viewSpentOnItemNameTV = (TextView) convertView.findViewById(R.id.viewSpentOnItemNameTVId);
        TextView viewSpentOnNoteTV = (TextView) convertView.findViewById(R.id.viewSpentOnNoteTVId);
        TextView viewSpentOnItemTattooTV = (TextView) convertView.findViewById(R.id.viewSpentOnItemTattooTVId);
        ImageView viewSpentOnOptionsImg = (ImageView) convertView.findViewById(R.id.viewSpentOnItemOptionsImgId);

        //set font
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        viewSpentOnItemNameTV.setTypeface(robotoCondensedLightFont);
        viewSpentOnNoteTV.setTypeface(robotoCondensedLightFont);
        viewSpentOnItemTattooTV.setTypeface(robotoCondensedLightFont);

        viewSpentOnItemNameTV.setText(item.getSPNT_ON_NAME());
        viewSpentOnNoteTV.setText(item.getSPNT_ON_NOTE());
        viewSpentOnItemTattooTV.setText(item.getSPNT_ON_NAME().toUpperCase());
        viewSpentOnItemTattooTV.setBackgroundColor(viewSpentOnItemTattooTV.getResources().getColor(color));

        viewSpentOnOptionsImg.setTag(item.getSPNT_ON_ID());

        return convertView;
    }

}