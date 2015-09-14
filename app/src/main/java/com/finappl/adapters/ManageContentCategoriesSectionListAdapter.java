package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.CategoryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by ajit on 17/1/15.
 */
public class ManageContentCategoriesSectionListAdapter extends BaseAdapter {

    private final String CLASS_NAME = this.getClass().getName();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    private ArrayList<Object> mData = new ArrayList<Object>();
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    private int sectionId;
    private int listId;
    private Map<String, List<CategoryModel>> itemsMap;
    private Context mContext;
    private String userNameStr;
    private LayoutInflater inflater;

    public ManageContentCategoriesSectionListAdapter(Context context, int sectionId, int listId, Map<String, List<CategoryModel>> itemsMap, String userNameStr) {
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
        for(Map.Entry<String, List<CategoryModel>> iterMap : itemsMap.entrySet()) {
            //if there's only default Categories then no need of showing dividers
            /*if(itemsMap.size() != 1 && "Y-DEFAULT".equalsIgnoreCase(iterMap.getKey())){
                addSeparatorItem(iterMap);
            }*/

            List<CategoryModel> catList = iterMap.getValue();

            for(CategoryModel iterCatList : catList) {
                addItem(iterCatList);
            }
        }
    }

    public void addItem(Object item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSeparatorItem(Object item) {
        mData.add(item);
        // save separator position
        mSeparatorsSet.add(mData.size() - 1);
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
                    convertView = (LinearLayout)inflater.inflate(listId, null);

                    mHolder.manageContentCatContentLL = (LinearLayout) convertView.findViewById(R.id.manageContentCatContentLLId);
                    //mHolder.manageContentCatTypeTV = (TextView) convertView.findViewById(R.id.manageContentCatTypeTVId);
                    mHolder.manageContentCatNameTV = (TextView) convertView.findViewById(R.id.manageContentCatNameTVId);
                    mHolder.manageContentCatNoteTV = (TextView) convertView.findViewById(R.id.manageContentCatNoteTVId);

                    break;
                case TYPE_SEPARATOR:
                    convertView = (LinearLayout)inflater.inflate(sectionId, null);

                    /*mHolder.manageContentCatHeaderLL = (LinearLayout) convertView.findViewById(R.id.manageContentCatHeaderLLId);
                    mHolder.manageContentCatHeaderHeadingTV = (TextView) convertView.findViewById(R.id.manageContentCatHeaderHeadingTVId);
                    mHolder.manageContentCatCounterTV = (TextView) convertView.findViewById(R.id.manageContentCatCounterTVId);*/

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
        CategoryModel categoryModelObj = (CategoryModel) mData.get(position);

        mHolder.manageContentCatNameTV.setText(categoryModelObj.getCAT_NAME());
        mHolder.manageContentCatNoteTV.setText(categoryModelObj.getCAT_NOTE());

        if(mHolder.manageContentCatNoteTV.getText().toString().isEmpty()){
            mHolder.manageContentCatNoteTV.setVisibility(View.GONE);
        }
        else{
            mHolder.manageContentCatNoteTV.setVisibility(View.VISIBLE);
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) mHolder.manageContentCatContentLL, robotoCondensedLightFont);
    }

    private void makeSectionHeader(int position, ViewHolder mHolder) {
        /*Map.Entry<String, List<CategoryModel>> categoriesListMap = (Map.Entry<String, List<CategoryModel>>) mData.get(position);

        if("USER".equalsIgnoreCase(categoriesListMap.getKey())) {
            mHolder.manageContentCatHeaderHeadingTV.setText(userNameStr + "'s Categories");
        } else if("Y-DEFAULT".equalsIgnoreCase(categoriesListMap.getKey())) {
            mHolder.manageContentCatHeaderHeadingTV.setText("Default Categories");
        }

        mHolder.manageContentCatCounterTV.setText(String.valueOf(categoriesListMap.getValue().size()));

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) mHolder.manageContentCatHeaderLL, robotoCondensedLightFont);*/
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
        /*private TextView manageContentCatHeaderHeadingTV;
        private TextView manageContentCatCounterTV;*/
        private TextView manageContentCatNameTV;
        private TextView manageContentCatNoteTV;
        private LinearLayout manageContentCatContentLL;
        /*private LinearLayout manageContentCatHeaderLL;*/
    }
}