package com.finappl.adapters;

/**
 * Created by ajit on 9/2/17.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.activities.CommonActivity;
import com.finappl.models.CalendarSummary;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.util.List;

import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_OBJECT_TYPE;


public class CalendarSummaryRecyclerViewAdapter extends RecyclerView.Adapter<CalendarSummaryRecyclerViewAdapter.ViewHolder> {
    private static final String CLASS_NAME = CalendarSummaryRecyclerViewAdapter.class.getName();
    private Context mContext;
    private LayoutInflater inflater;

    private List<CalendarSummary> summaryList;
    private UserMO user;

    public CalendarSummaryRecyclerViewAdapter(Context mContext, UserMO user, List<CalendarSummary> summaryList) {
        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.summaryList = summaryList;
        this.user = user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.calendar_summary_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        CalendarSummary calendarSummary = summaryList.get(position);

        viewHolder.calendar_summary_list_item_heading_tv.setText(calendarSummary.getHeading());
        viewHolder.calendar_summary_list_item_amount_tv = FinappleUtility.formatAmountView(viewHolder.calendar_summary_list_item_amount_tv, user, calendarSummary.getAmount());

        if(calendarSummary.getListViewAdapter() != null){
            viewHolder.calendar_summary_list_item_no_transactions_tv.setVisibility(View.GONE);

            int baseHeightForListView = 110;
            int calculateListViewHeight = baseHeightForListView * 3;
            if(calendarSummary.getListViewAdapter() instanceof CalendarSummaryTransactionsListViewAdapter){
                viewHolder.calendar_summary_list_item_lv.setAdapter((CalendarSummaryTransactionsListViewAdapter)calendarSummary.getListViewAdapter());

                if(((CalendarSummaryTransactionsListViewAdapter) calendarSummary.getListViewAdapter()).getCount() <= 3){
                    calculateListViewHeight = (baseHeightForListView * ((CalendarSummaryTransactionsListViewAdapter) calendarSummary.getListViewAdapter()).getCount());
                }
            }
            else if(calendarSummary.getListViewAdapter() instanceof CalendarSummaryTransfersListViewAdapter){
                viewHolder.calendar_summary_list_item_lv.setAdapter((CalendarSummaryTransfersListViewAdapter)calendarSummary.getListViewAdapter());
                viewHolder.calendar_summary_list_item_amount_tv.setTextColor(viewHolder.calendar_summary_list_item_amount_tv.getResources().getColor(R.color.finappleCurrencyNeutralColor));

                if(((CalendarSummaryTransfersListViewAdapter) calendarSummary.getListViewAdapter()).getCount() <= 3){
                    calculateListViewHeight = (baseHeightForListView * ((CalendarSummaryTransfersListViewAdapter) calendarSummary.getListViewAdapter()).getCount());
                }
            }
            else{
                Log.e(CLASS_NAME, UN_IDENTIFIED_OBJECT_TYPE+calendarSummary.getListViewAdapter());
                return;
            }

            //set list views height
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, calculateListViewHeight);
            layoutParams.setMargins(0, 0, 0, 30);
            viewHolder.calendar_summary_list_item_lv.setLayoutParams(layoutParams);

            viewHolder.calendar_summary_list_item_lv.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });
        }
        else{
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
            layoutParams.setMargins(0, 0, 0, 30);
            viewHolder.calendar_summary_list_item_cv.setLayoutParams(layoutParams);

            viewHolder.calendar_summary_list_item_no_transactions_tv.setText(String.valueOf(viewHolder.calendar_summary_list_item_no_transactions_tv.getText()).replace("XXXXX", calendarSummary.getHeading()));
        }

        setFont(viewHolder.calendar_summary_list_item_cv);
    }

    @Override
    public int getItemCount() {
        return summaryList.size();
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        private CardView calendar_summary_list_item_cv;
        private TextView calendar_summary_list_item_heading_tv;
        private TextView calendar_summary_list_item_amount_tv;
        private TextView calendar_summary_list_item_no_transactions_tv;
        private ListView calendar_summary_list_item_lv;

        public ViewHolder(View view) {
            super(view);
            calendar_summary_list_item_cv = (CardView) view.findViewById(R.id.calendar_summary_list_item_cv);
            calendar_summary_list_item_heading_tv = (TextView) view.findViewById(R.id.calendar_summary_list_item_heading_tv);
            calendar_summary_list_item_amount_tv = (TextView) view.findViewById(R.id.calendar_summary_list_item_amount_tv);
            calendar_summary_list_item_no_transactions_tv = (TextView) view.findViewById(R.id.calendar_summary_list_item_no_transactions_tv);
            calendar_summary_list_item_lv = (ListView) view.findViewById(R.id.calendar_summary_list_item_lv);
        }
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
}
