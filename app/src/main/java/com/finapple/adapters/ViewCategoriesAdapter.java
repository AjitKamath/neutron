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
import com.finapple.model.CategoryModel;
import com.finapple.util.FinappleUtility;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class ViewCategoriesAdapter extends ArrayAdapter<CategoryModel> {

    private Context mContext;
    private int layoutResourceId;
    private List<CategoryModel> dataList;
    private List<Integer> colorList;

    public ViewCategoriesAdapter(Context mContext, int layoutResourceId, List<CategoryModel> data) {

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
        CategoryModel catItem = dataList.get(position);

        int color = colorList.get(position);

        TextView viewCatItemNameTV = (TextView) convertView.findViewById(R.id.viewCatItemNameTVId);
        TextView viewCatNoteTV = (TextView) convertView.findViewById(R.id.viewCatNoteTVId);
        TextView viewCatItemTattooTV = (TextView) convertView.findViewById(R.id.viewCatItemTattooTVId);
        ImageView viewCatOptionsImg = (ImageView) convertView.findViewById(R.id.viewCatItemOptionsImgId);

        //set font
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        viewCatItemNameTV.setTypeface(robotoCondensedLightFont);
        viewCatNoteTV.setTypeface(robotoCondensedLightFont);

        viewCatItemNameTV.setText(catItem.getCAT_NAME());
        viewCatNoteTV.setText(catItem.getCAT_NOTE());
        viewCatItemTattooTV.setText(catItem.getCAT_NAME().toUpperCase());
        viewCatItemTattooTV.setBackgroundColor(viewCatItemTattooTV.getResources().getColor(color));

        if(catItem.getCAT_TYPE().equalsIgnoreCase("EXPENSE")){
            viewCatItemNameTV.setTextColor(viewCatItemNameTV.getResources().getColor(R.color.red));
        }
        else{
            viewCatItemNameTV.setTextColor(viewCatItemNameTV.getResources().getColor(R.color.darkGreen));
        }
        viewCatOptionsImg.setTag(catItem.getCAT_ID());

        return convertView;
    }

}