package com.finapple.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finapple.R;
import com.finapple.model.SpentOnModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by ajit on 17/1/15.
 */
public class ManageContentSpentOnsSectionListAdapter extends BaseAdapter {

    private final String CLASS_NAME = this.getClass().getName();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    private ArrayList<Object> mData = new ArrayList<Object>();
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    private int sectionId;
    private int listId;
    private Map<String, List<SpentOnModel>> itemsMap;
    private Context mContext;
    private String userNameStr;
    private LayoutInflater inflater;

    public ManageContentSpentOnsSectionListAdapter(Context context, int sectionId, int listId, Map<String, List<SpentOnModel>> itemsMap, String userNameStr) {
        super();
        this.sectionId = sectionId;
        this.listId = listId;
        this.itemsMap = itemsMap;
        this.mContext = context;
        this.userNameStr = userNameStr;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        buildSectionList();
    }

    private void buildSectionList() {
        for(Map.Entry<String, List<SpentOnModel>> iterMap : itemsMap.entrySet()) {
            addSeparatorItem(iterMap);

            List<SpentOnModel> spntOnList = iterMap.getValue();

            for(SpentOnModel iterSpntOnList : spntOnList) {
                addItem(iterSpntOnList);
            }
        }
    }

    public void addItem(Object item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSeparatorItem(Object item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int type = getItemViewType(position);

        if(convertView == null) {
            mHolder = new ViewHolder();

            switch(type) {
                case TYPE_ITEM:
                    convertView = inflater.inflate(listId, null);

                    mHolder.manageContentSpntOnContentLL = (LinearLayout) convertView.findViewById(R.id.manageContentSpntOnContentLLId);
                    mHolder.manageContentSpntOnNameTV = (TextView) convertView.findViewById(R.id.manageContentSpntOnNameTVId);
                    mHolder.manageContentSpntOnNoteTV = (TextView) convertView.findViewById(R.id.manageContentSpntOnNoteTVId);

                    break;
                case TYPE_SEPARATOR:
                    convertView = inflater.inflate(sectionId, null);

                    mHolder.manageContentSpntOnHeaderLL = (LinearLayout) convertView.findViewById(R.id.manageContentSpntOnHeaderLLId);
                    mHolder.manageContentSpntOnHeaderHeadingTV = (TextView) convertView.findViewById(R.id.manageContentSpntOnHeaderHeadingTVId);
                    mHolder.manageContentSpntOnCounterTV = (TextView) convertView.findViewById(R.id.manageContentSpntOnCounterTVId);

                    break;
            }

            convertView.setTag(mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        switch(type) {
            case TYPE_ITEM:
                makeListItem(position, mHolder);
                break;
            case TYPE_SEPARATOR:
                makeSectionHeader(position, mHolder);
                break;
        }

        return convertView;
    }

    private void makeListItem(int position, ViewHolder mHolder) {
        SpentOnModel spentOnModelObj = (SpentOnModel) mData.get(position);

        mHolder.manageContentSpntOnNameTV.setText(spentOnModelObj.getSPNT_ON_NAME());
        mHolder.manageContentSpntOnNoteTV.setText(spentOnModelObj.getSPNT_ON_NOTE());

        if(mHolder.manageContentSpntOnNoteTV.getText().toString().isEmpty()){
            mHolder.manageContentSpntOnNoteTV.setVisibility(View.GONE);
        }
        else{
            mHolder.manageContentSpntOnNoteTV.setVisibility(View.VISIBLE);
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) mHolder.manageContentSpntOnContentLL, robotoCondensedLightFont);
    }

    private void makeSectionHeader(int position, ViewHolder mHolder) {
        Map.Entry<String, List<SpentOnModel>> spntOnListMap = (Map.Entry<String, List<SpentOnModel>>) mData.get(position);

        if("USER".equalsIgnoreCase(spntOnListMap.getKey())){
            mHolder.manageContentSpntOnHeaderHeadingTV.setText(userNameStr+"'s Categories");
        }
        else if("Y-DEFAULT".equalsIgnoreCase(spntOnListMap.getKey())){
            mHolder.manageContentSpntOnHeaderHeadingTV.setText("Default Categories");
        }

        mHolder.manageContentSpntOnCounterTV.setText(String.valueOf(spntOnListMap.getValue().size()));

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) mHolder.manageContentSpntOnHeaderLL, robotoCondensedLightFont);
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

    private class ViewHolder {
        private TextView manageContentSpntOnNameTV;
        private TextView manageContentSpntOnNoteTV;
        private TextView manageContentSpntOnHeaderHeadingTV;
        private TextView manageContentSpntOnCounterTV;
        private LinearLayout manageContentSpntOnContentLL;
        private LinearLayout manageContentSpntOnHeaderLL;
    }
}