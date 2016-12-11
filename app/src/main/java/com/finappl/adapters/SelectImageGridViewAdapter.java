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
import com.finappl.models.AccountMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.SpentOnMO;

import java.util.List;

import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 8/1/15.
 */
// Inner Class
public class SelectImageGridViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private final Context mContext;
    private LayoutInflater inflater;
    private final int LAYOUT = R.layout.select_image_image;

    private List<? extends Object> dataList;
    private Object selectedImageObject;
    private View.OnClickListener clickListener;

    public SelectImageGridViewAdapter(Context context, List<? extends Object> dataList, Object selectedImageObject, View.OnClickListener clickListener) {
        super();
        this.mContext = context;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dataList = dataList;
        this.selectedImageObject = selectedImageObject;
        this.clickListener = clickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(LAYOUT, null);

            mHolder.imageLL = (LinearLayout) convertView.findViewById(R.id.imageLLId);
            mHolder.imageIV = (ImageView) convertView.findViewById(R.id.imageIVId);
            mHolder.imageSelectView = convertView.findViewById(R.id.imageSelectViewId);

            convertView.setTag(LAYOUT, mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag(LAYOUT);
        }

        //for category image selection
        if(dataList.get(0) instanceof CategoryMO){
            CategoryMO category = (CategoryMO)dataList.get(position);
            CategoryMO selectedCategory = (CategoryMO)selectedImageObject;

            mHolder.imageIV.setBackgroundResource(Integer.parseInt(category.getCAT_IMG()));

            if(selectedCategory.getCAT_IMG().equalsIgnoreCase(category.getCAT_IMG())){
                mHolder.imageSelectView.setVisibility(View.VISIBLE);
            }
            else{
                mHolder.imageSelectView.setVisibility(View.GONE);
            }

            mHolder.imageIV.setTag(category);
        }
        else if(dataList.get(0) instanceof AccountMO){
            AccountMO account = (AccountMO)dataList.get(position);
            AccountMO selectedAccount = (AccountMO)selectedImageObject;

            mHolder.imageIV.setBackgroundResource(Integer.parseInt(account.getACC_IMG()));

            if(selectedAccount.getACC_IMG().equalsIgnoreCase(account.getACC_IMG())){
                mHolder.imageSelectView.setVisibility(View.VISIBLE);
            }
            else{
                mHolder.imageSelectView.setVisibility(View.GONE);
            }

            mHolder.imageIV.setTag(account);
        }
        else if(dataList.get(0) instanceof SpentOnMO){
            SpentOnMO spenton = (SpentOnMO)dataList.get(position);
            SpentOnMO selectedSpenton = (SpentOnMO)selectedImageObject;

            mHolder.imageIV.setBackgroundResource(Integer.parseInt(spenton.getSPNT_ON_IMG()));

            if(selectedSpenton.getSPNT_ON_IMG().equalsIgnoreCase(spenton.getSPNT_ON_IMG())){
                mHolder.imageSelectView.setVisibility(View.VISIBLE);
            }
            else{
                mHolder.imageSelectView.setVisibility(View.GONE);
            }

            mHolder.imageIV.setTag(spenton);
        }
        mHolder.imageIV.setOnClickListener(clickListener);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        private LinearLayout imageLL;
        private ImageView imageIV;
        private View imageSelectView;
    }

    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public int getCount() {
        return dataList.size();
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
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v);
            }
        }
    }
}
