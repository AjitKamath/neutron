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
import com.finapple.model.TagModel;
import com.finapple.util.FinappleUtility;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class AddCategoryAdapter extends ArrayAdapter<TagModel> {

    private Context mContext;
    private int layoutResourceId;
    private List<TagModel> dataList;
    private List<Integer> colorList;

    public AddCategoryAdapter(Context mContext, int layoutResourceId, List<TagModel> data) {

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
        TagModel item = dataList.get(dataList.size()-1-position);

        TextView addCatTagListItemTV = (TextView) convertView.findViewById(R.id.addCatTagListItemTVId);
        ImageView addCatDelTagListItemIV = (ImageView) convertView.findViewById(R.id.addCatDelTagListItemIVId);
        View view = (View) convertView.findViewById(R.id.addCatTagListItemVId);

        //set font
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        addCatTagListItemTV.setTypeface(robotoCondensedLightFont);

        view.setBackgroundColor(view.getResources().getColor(colorList.get(position)));
        addCatTagListItemTV.setText(item.getTag());
        addCatDelTagListItemIV.setTag(item.getTag());

        return convertView;
    }

}