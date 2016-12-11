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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 8/1/15.
 */
// Inner Class
public class TagsGridViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private final Context mContext;
    private LayoutInflater inflater;
    private static final int layout = R.layout.tag;

    private List<String> tagsList = new ArrayList<>();
    private View.OnClickListener clickListener;

    public TagsGridViewAdapter(Context context, Set<String> tagsSet, View.OnClickListener clickListener) {
        super();
        this.mContext = context;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.clickListener = clickListener;
        this.tagsList.addAll(tagsSet);
    }

    @Override
    public int getCount() {
        return tagsList.size();
    }

    @Override
    public Object getItem(int i) {
        return tagsList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        private LinearLayout tagLL;
        private TextView tagTV;
        private ImageView tagDeleteIV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.tagLL = (LinearLayout) convertView.findViewById(R.id.tagLLId);
            mHolder.tagTV = (TextView) convertView.findViewById(R.id.tagTVId);
            mHolder.tagDeleteIV = (ImageView) convertView.findViewById(R.id.tagDeleteIVId);

            convertView.setTag(layout, mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        String tagStr = tagsList.get(position);

        mHolder.tagTV.setText(tagStr);
        mHolder.tagDeleteIV.setOnClickListener(clickListener);
        mHolder.tagDeleteIV.setTag(tagStr);

        setFont(mHolder.tagLL);

        return convertView;
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
