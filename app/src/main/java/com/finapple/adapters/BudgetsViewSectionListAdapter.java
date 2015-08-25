package com.finapple.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finapple.R;
import com.finapple.model.BudgetModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by ajit on 17/1/15.
 */
public class BudgetsViewSectionListAdapter extends BaseAdapter {

    private final String CLASS_NAME = this.getClass().getName();

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    private ArrayList<Object> mData = new ArrayList<>();
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<>();
    private int sectionId;
    private int listId;
    private Map<String, List<BudgetModel>> itemsMap;
    private Context mContext;
    private LayoutInflater inflater;

    public BudgetsViewSectionListAdapter(Context context, int sectionId, int listId, Map<String, List<BudgetModel>> itemsMap) {
        super();
        this.sectionId = sectionId;
        this.listId = listId;
        this.itemsMap = itemsMap;
        this.mContext = context;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        buildSectionList();
    }

    private void buildSectionList() {
        for(Map.Entry<String, List<BudgetModel>> iterMap : itemsMap.entrySet()) {
            addSeparatorItem(iterMap);

            List<BudgetModel> budgetList = iterMap.getValue();

            for(BudgetModel iterBudList : budgetList) {
                addItem(iterBudList);
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
                    convertView = inflater.inflate(listId, null);

                    mHolder.budgetsViewListContentLL = (LinearLayout) convertView.findViewById(R.id.budgetsViewListContentLLId);
                    mHolder.budgetsViewListNameTV = (TextView) convertView.findViewById(R.id.budgetsViewListNameTVId);
                    mHolder.budgetsViewListAmtTV = (TextView) convertView.findViewById(R.id.budgetsViewListAmtTVId);
                    mHolder.budgetsViewListGroupNameTV = (TextView) convertView.findViewById(R.id.budgetsViewListGroupNameTVId);
                    mHolder.budgetsViewListNoteTV = (TextView) convertView.findViewById(R.id.budgetsViewListNoteTVId);
                    mHolder.budgetsViewListBubbleTV = (TextView) convertView.findViewById(R.id.budgetsViewListBubbleTVId);

                    break;
                case TYPE_SEPARATOR:
                    convertView = inflater.inflate(sectionId, null);

                    mHolder.budgetsViewListHeaderLL = (LinearLayout) convertView.findViewById(R.id.budgetsViewListHeaderLLId);
                    mHolder.budgetsViewListHeaderHeadingLL = (LinearLayout) convertView.findViewById(R.id.budgetsViewListHeaderHeadingLLId);
                    mHolder.budgetsViewListHeaderHeadingTV = (TextView) convertView.findViewById(R.id.budgetsViewListHeaderHeadingTVId);
                    //mHolder.budgetsViewListCounterTV = (TextView) convertView.findViewById(R.id.budgetsViewListCounterTVId);

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
        BudgetModel budgetModelObj = (BudgetModel) mData.get(position);

        //mHolder.budgetsViewListContentLL.setTag(budgetModelObj.getBUDGET_ID());
        mHolder.budgetsViewListNameTV.setText(budgetModelObj.getBUDGET_NAME());
        mHolder.budgetsViewListAmtTV.setText(String.valueOf(budgetModelObj.getBUDGET_AMT()));
        mHolder.budgetsViewListNoteTV.setText(budgetModelObj.getBUDGET_NOTE());

        if("CATEGORY".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
            mHolder.budgetsViewListBubbleTV.setBackgroundResource(R.drawable.circle_budgets_category_text_view);
            mHolder.budgetsViewListGroupNameTV.setText(budgetModelObj.getCategoryNameStr());
        }
        else if("ACCOUNT".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
            mHolder.budgetsViewListBubbleTV.setBackgroundResource(R.drawable.circle_budgets_account_text_view);
            mHolder.budgetsViewListGroupNameTV.setText(budgetModelObj.getAccountNameStr());
        }
        else if("SPENT ON".equalsIgnoreCase(budgetModelObj.getBUDGET_GRP_TYPE())){
            mHolder.budgetsViewListBubbleTV.setBackgroundResource(R.drawable.circle_budgets_spenton_text_view);
            mHolder.budgetsViewListGroupNameTV.setText(budgetModelObj.getSpentOnNameStr());
        }

        if(mHolder.budgetsViewListNoteTV.getText().toString().isEmpty()){
            mHolder.budgetsViewListNoteTV.setVisibility(View.GONE);
        }
        else{
            mHolder.budgetsViewListNoteTV.setVisibility(View.VISIBLE);
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.budgetsViewListContentLL, robotoCondensedLightFont);
    }

    private void makeSectionHeader(int position, ViewHolder mHolder) {
        Map.Entry<String, List<BudgetModel>> budgetsMap = (Map.Entry<String, List<BudgetModel>>) mData.get(position);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.CENTER;
        if(position != 0) {
            params.setMargins(0, 70, 0, 0);
        }
        mHolder.budgetsViewListHeaderHeadingLL.setLayoutParams(params);

        if("CATEGORY".equalsIgnoreCase(budgetsMap.getKey())) {
            mHolder.budgetsViewListHeaderHeadingTV.setText("Categories");
            mHolder.budgetsViewListHeaderHeadingTV.setTextColor(mHolder.budgetsViewListHeaderHeadingTV
            .getResources().getColor(R.color.budgetsViewHeadingCategory));

        }
        else if("ACCOUNT".equalsIgnoreCase(budgetsMap.getKey())) {
            mHolder.budgetsViewListHeaderHeadingTV.setText("Accounts");
            mHolder.budgetsViewListHeaderHeadingTV.setTextColor(mHolder.budgetsViewListHeaderHeadingTV
                    .getResources().getColor(R.color.budgetsViewHeadingAccount));
        }
        else if("SPENT ON".equalsIgnoreCase(budgetsMap.getKey())) {
            mHolder.budgetsViewListHeaderHeadingTV.setText("Spent On's");
            mHolder.budgetsViewListHeaderHeadingTV.setTextColor(mHolder.budgetsViewListHeaderHeadingTV
                    .getResources().getColor(R.color.budgetsViewHeadingSpentOn));
        }

        //mHolder.budgetsViewListCounterTV.setText(String.valueOf(budgetsMap.getValue().size()));

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont(mHolder.budgetsViewListHeaderLL, robotoCondensedLightFont);
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
        //header
        private TextView budgetsViewListHeaderHeadingTV;
        private LinearLayout budgetsViewListHeaderLL;
        private LinearLayout budgetsViewListHeaderHeadingLL;
        //private TextView budgetsViewListCounterTV;

        //content
        private LinearLayout budgetsViewListContentLL;
        private TextView budgetsViewListBubbleTV;
        private TextView budgetsViewListNameTV;
        private TextView budgetsViewListAmtTV;
        private TextView budgetsViewListGroupNameTV;
        private TextView budgetsViewListNoteTV;
    }
}