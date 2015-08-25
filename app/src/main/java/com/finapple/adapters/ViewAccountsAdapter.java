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
import com.finapple.model.AccountsModel;
import com.finapple.util.FinappleUtility;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class ViewAccountsAdapter extends ArrayAdapter<AccountsModel> {

    private Context mContext;
    private int layoutResourceId;
    private List<AccountsModel> dataList;
    private List<Integer> colorList;

    public ViewAccountsAdapter(Context mContext, int layoutResourceId, List<AccountsModel> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = data;
        this.colorList = FinappleUtility.getInstance().getRandomPleasantColorList(dataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        AccountsModel item = dataList.get(position);

        int color = colorList.get(position);

        TextView viewAccItemNameTV = (TextView) convertView.findViewById(R.id.viewAccItemNameTVId);
        TextView viewAccTotalTV = (TextView) convertView.findViewById(R.id.viewAccTotalTVId);
        TextView viewAccNoteTV = (TextView) convertView.findViewById(R.id.viewAccNoteTVId);
        TextView viewAccItemTattooTV = (TextView) convertView.findViewById(R.id.viewAccItemTattooTVId);
        ImageView viewAccOptionsImg = (ImageView) convertView.findViewById(R.id.viewAccItemOptionsImgId);

        //set font
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        viewAccItemNameTV.setTypeface(robotoCondensedLightFont);
        viewAccNoteTV.setTypeface(robotoCondensedLightFont);
        viewAccTotalTV.setTypeface(robotoCondensedLightFont);

        viewAccItemNameTV.setText(item.getACC_NAME());
        viewAccNoteTV.setText(item.getACC_NOTE());
        viewAccItemTattooTV.setText(item.getACC_NAME().toUpperCase());
        viewAccItemTattooTV.setBackgroundColor(viewAccItemTattooTV.getResources().getColor(color));

        String totalStr = String.valueOf(item.getACC_TOTAL());

        if(item.getACC_TOTAL() < 0){
            if(totalStr.contains("-")){
                totalStr = totalStr.replace("-", "");
            }
            viewAccTotalTV.setTextColor(viewAccTotalTV.getResources().getColor(R.color.red));
        }
        else{
            viewAccTotalTV.setTextColor(viewAccTotalTV.getResources().getColor(R.color.darkGreen));
        }

        viewAccTotalTV.setText(item.getCurrency()+totalStr);
        viewAccOptionsImg.setTag(item.getACC_ID());

        return convertView;
    }

}