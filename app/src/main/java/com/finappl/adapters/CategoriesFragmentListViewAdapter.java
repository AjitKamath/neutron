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
import com.finappl.models.CategoryMO;

import java.util.List;

import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 17/1/15.
 */
public class  CategoriesFragmentListViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private LayoutInflater inflater;
    private List<CategoryMO> categoriesList;
    private View.OnClickListener clickListener;

    public CategoriesFragmentListViewAdapter(Context mContext, List<CategoryMO> categoriesList, View.OnClickListener clickListener) {
        super();

        this.mContext = mContext;
        this.categoriesList = categoriesList;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.clickListener = clickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.categories_category;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.categoryLL = (LinearLayout) convertView.findViewById(R.id.categoryLLId);
            mHolder.categoryIV = (ImageView) convertView.findViewById(R.id.categoryIVId);
            mHolder.categoryTV = (TextView) convertView.findViewById(R.id.categoryTVId);
            mHolder.categoryDeleteIV = (ImageView) convertView.findViewById(R.id.categoryDeleteIVId);
            mHolder.categoryModifyIV = (ImageView) convertView.findViewById(R.id.categoryModifyIVId);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        CategoryMO categoryMO = categoriesList.get(position);

        mHolder.categoryTV.setText(categoryMO.getCAT_NAME());
        mHolder.categoryIV.setBackgroundResource(Integer.parseInt(categoryMO.getCAT_IMG()));

        mHolder.categoryModifyIV.setTag(categoryMO);
        mHolder.categoryModifyIV.setOnClickListener(clickListener);

        //do not enable delete if the user id of the category is admin
        if(ADMIN_USERID.equalsIgnoreCase(categoryMO.getUSER_ID())){
            mHolder.categoryDeleteIV.setVisibility(View.INVISIBLE);
        }
        else{
            mHolder.categoryDeleteIV.setVisibility(View.VISIBLE);
            mHolder.categoryDeleteIV.setTag(categoryMO);
            mHolder.categoryDeleteIV.setOnClickListener(clickListener);
        }

        setFont(mHolder.categoryLL);

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
    public void setFont(ViewGroup group) {
        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), UI_FONT);

        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(robotoCondensedLightFont);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v);
            }
        }
    }

    private class ViewHolder {
        private LinearLayout categoryLL;
        private ImageView categoryIV;
        private TextView categoryTV;
        private ImageView categoryDeleteIV;
        private ImageView categoryModifyIV;
    }

}