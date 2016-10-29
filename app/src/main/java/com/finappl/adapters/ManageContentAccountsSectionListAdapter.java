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
import com.finappl.models.AccountsMO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by ajit on 17/1/15.
 */
public class ManageContentAccountsSectionListAdapter extends BaseAdapter {

    private final String CLASS_NAME = this.getClass().getName();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    private ArrayList<Object> mData = new ArrayList<Object>();
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    private int sectionId;
    private int listId;
    private Map<String, List<AccountsMO>> itemsMap;
    private Context mContext;
    private String userNameStr;
    private LayoutInflater inflater;

    public ManageContentAccountsSectionListAdapter(Context context, int sectionId, int listId, Map<String, List<AccountsMO>> itemsMap, String userNameStr) {
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
        for(Map.Entry<String, List<AccountsMO>> iterMap : itemsMap.entrySet()) {
            //if there's only default Accounts then no need of showing dividers
            /*if(itemsMap.size() != 1 && "Y-DEFAULT".equalsIgnoreCase(iterMap.getKey())){
                addSeparatorItem(iterMap);
            }*/

            List<AccountsMO> accList = iterMap.getValue();

            for(AccountsMO iterAccList : accList) {
                addItem(iterAccList);
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

                    mHolder.manageContentAccContentLL = (LinearLayout) convertView.findViewById(R.id.manageContentAccContentLLId);
                    mHolder.manageContentAccNameTV = (TextView) convertView.findViewById(R.id.manageContentAccNameTVId);
                    mHolder.manageContentAccNoteTV = (TextView) convertView.findViewById(R.id.manageContentAccNoteTVId);
                    mHolder.manageContentAccTotalTV = (TextView) convertView.findViewById(R.id.manageContentAccTotalTVId);

                    break;
                case TYPE_SEPARATOR:
                    convertView = (LinearLayout)inflater.inflate(sectionId, null);

                    /*mHolder.manageContentAccHeaderLL = (LinearLayout) convertView.findViewById(R.id.manageContentAccHeaderLLId);
                    mHolder.manageContentAccCounterTV = (TextView) convertView.findViewById(R.id.manageContentAccCounterTVId);
                    mHolder.manageContentAccHeaderHeadingTV = (TextView) convertView.findViewById(R.id.manageContentAccHeaderHeadingTVId);*/

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
        AccountsMO accountsModelObj = (AccountsMO) mData.get(position);

        mHolder.manageContentAccNameTV.setText(accountsModelObj.getACC_NAME());
        mHolder.manageContentAccNoteTV.setText(accountsModelObj.getACC_NOTE());
        mHolder.manageContentAccTotalTV.setText(String.valueOf(accountsModelObj.getACC_TOTAL()));

        if(mHolder.manageContentAccNoteTV.getText().toString().isEmpty()){
            mHolder.manageContentAccNoteTV.setVisibility(View.GONE);
        }
        else{
            mHolder.manageContentAccNoteTV.setVisibility(View.VISIBLE);
        }

        //TODO: APPROXIMATIZATION AND CURRENCY SETTING HAS TO BE DONE HERE

        if(accountsModelObj.getACC_TOTAL() < 0){
            mHolder.manageContentAccTotalTV.setTextColor(mHolder.manageContentAccTotalTV.getResources().getColor(R.color.finappleCurrencyNegColor));
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) mHolder.manageContentAccContentLL, robotoCondensedLightFont);
    }

    private void makeSectionHeader(int position, ViewHolder mHolder) {
        /*Map.Entry<String, List<AccountsMO>> accountsListMap = (Map.Entry<String, List<AccountsMO>>) mData.get(position);

        if("USER".equalsIgnoreCase(accountsListMap.getKey())){
            mHolder.manageContentAccHeaderHeadingTV.setText(userNameStr+"'s Accounts");
        }
        else if("Y-DEFAULT".equalsIgnoreCase(accountsListMap.getKey())){
            mHolder.manageContentAccHeaderHeadingTV.setText("Default Categories");
        }

        mHolder.manageContentAccCounterTV.setText(String.valueOf(accountsListMap.getValue().size()));

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) mHolder.manageContentAccHeaderLL, robotoCondensedLightFont);*/
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
        private TextView manageContentAccNameTV;
        private TextView manageContentAccNoteTV;
        private TextView manageContentAccTotalTV;
        /*private TextView manageContentAccCounterTV;
        private TextView manageContentAccHeaderHeadingTV;*/
        private LinearLayout manageContentAccContentLL;
       /* private LinearLayout manageContentAccHeaderLL;*/
    }
}