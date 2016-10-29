package com.finappl.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.CategoryMO;
import com.finappl.utils.FinappleUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ajit on 17/1/15.
 */
public class CategoriesFragmentListViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private String selectedCategoryIdStr;
    private List<CategoryMO> categoriesList;

    public CategoriesFragmentListViewAdapter(Context mContext, List<CategoryMO> categoriesList, String selectedCategoryIdStr) {
        super();

        this.mContext = mContext;
        this.selectedCategoryIdStr = selectedCategoryIdStr;
        this.categoriesList = categoriesList;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.category;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.categoryRL = (RelativeLayout) convertView.findViewById(R.id.categoryRLId);
            mHolder.categoryIV = (ImageView) convertView.findViewById(R.id.categoryIVId);
            mHolder.categoryTV = (TextView) convertView.findViewById(R.id.categoryTVId);
            mHolder.categorySelectedV = (View) convertView.findViewById(R.id.categorySelectedVId);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        CategoryMO categoryMO = categoriesList.get(position);
        mHolder.categoryTV.setText(categoryMO.getCAT_NAME());
        mHolder.categoryIV.setBackgroundResource(Integer.parseInt(categoryMO.getCAT_IMG()));
        if(selectedCategoryIdStr.equalsIgnoreCase(categoryMO.getCAT_ID())){
            mHolder.categorySelectedV.setVisibility(View.VISIBLE);
        }
        else{
            mHolder.categorySelectedV.setVisibility(View.INVISIBLE);
        }


        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.categoryRL, robotoCondensedLightFont);

        return convertView;
    }

    @Override
    public int getCount() {
        return categoriesList.size();
    }

    @Override
    public CategoryMO getItem(int position) {
        return categoriesList.get(position);
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
        RelativeLayout categoryRL;
        ImageView categoryIV;
        TextView categoryTV;
        View categorySelectedV;
    }

}