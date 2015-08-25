package com.finapple.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finapple.R;
import com.finapple.model.ConsolidatedTransactionModel;
import com.finapple.util.FinappleUtility;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class ConsolidatedTransactionsAdapter extends ArrayAdapter<ConsolidatedTransactionModel> {

    private Context mContext;
    private int layoutResourceId;
    private List<ConsolidatedTransactionModel> dataList;
    private List<Integer> colorList;

    public ConsolidatedTransactionsAdapter(Context mContext, int layoutResourceId, List<ConsolidatedTransactionModel> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = data;

        //get random colors in a list
        colorList = FinappleUtility.getInstance().getUnRandomizedColorList(dataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        ConsolidatedTransactionModel tranItem = dataList.get(position);


        LinearLayout tranLL = (LinearLayout) convertView.findViewById(R.id.consolTranLLId);
        TextView consolTranTypTV = (TextView) convertView.findViewById(R.id.consolTranTypTVId);
        TextView catTV = (TextView) convertView.findViewById(R.id.consolTranCatTVId);
        TextView catCountTV = (TextView) convertView.findViewById(R.id.consolTranCatCountTVId);
        TextView catTotalTV = (TextView) convertView.findViewById(R.id.consolTranCatTotalTVId);

        //set font
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        catTV.setTypeface(robotoCondensedLightFont);
        catCountTV.setTypeface(robotoCondensedLightFont);
        catTotalTV.setTypeface(robotoCondensedLightFont);

        //decide color based on ACC_TOTAL
        int color = R.color.finappleCurrencyPosColor;
        if(tranItem.getTotal() <= 0){
            color = R.color.finappleCurrencyNegColor;
            consolTranTypTV.setBackgroundResource(R.drawable.circle_calendar_transaction_indicator);
            consolTranTypTV.setText("I");
        }

        //TODO: Approximatization required

        tranLL.setTag(tranItem.getCategory());
        catTV.setText(tranItem.getCategory());
        catCountTV.setText("x "+tranItem.getCount());
        catTotalTV.setText(String.valueOf(tranItem.getTotal()));
        catTotalTV.setTextColor(catTotalTV.getResources().getColor(color));

        return convertView;
    }

}