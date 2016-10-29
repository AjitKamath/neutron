package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.finappl.R;

import java.util.List;

/**
 * Created by ajit on 30/9/15.
 */
public class CalendarTransactionsOptionsPopperViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Integer> layoutsList;

    public CalendarTransactionsOptionsPopperViewPagerAdapter(Context context, List<Integer> layoutsList) {
        this.mContext = context;
        this.layoutsList = layoutsList;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutsList.get(position), collection, false);

        switch(layoutsList.get(position)){
            case R.layout.blue:
                TextView textView = (TextView)layout.findViewById(R.id.text);
                textView.setText("I, Legend");

        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(layout, robotoCondensedLightFont);

        collection.addView(layout);

        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return layoutsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
       return mContext.getString(layoutsList.get(position));
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
