package com.finapple.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.finapple.R;
import com.finapple.model.TransactionModel;
import com.finapple.util.FinappleUtility;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class ConsolidatedTransactionsPopperListAdapter extends ArrayAdapter<TransactionModel> {

    private Context mContext;
    private int layoutResourceId;
    private List<TransactionModel> dataList;
    private List<Integer> colorList;

    public ConsolidatedTransactionsPopperListAdapter(Context mContext, int layoutResourceId, List<TransactionModel> data) {

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

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) convertView.findViewById(R.id.viewTransactPopperTileLLId), robotoCondensedLightFont);

        // object item based on the position
        TransactionModel tranItem = dataList.get(position);

        //LinearLayout tranLL = (LinearLayout) convertView.findViewById(R.id.consolTranLLId);
        TextView viewTransactTilePopperTypTV = (TextView) convertView.findViewById(R.id.viewTransactTilePopperTypTVId);
        TextView viewTransactTilePopperNameTV = (TextView) convertView.findViewById(R.id.viewTransactTilePopperNameTVId);
        TextView viewTransactTilePopperAccTV = (TextView) convertView.findViewById(R.id.viewTransactTilePopperAccTVId);
        ImageView viewTransactTilePopperCurImage = (ImageView) convertView.findViewById(R.id.viewTransactTilePopperCurImageId);
        TextView viewTransactTilePopperAmtTV = (TextView) convertView.findViewById(R.id.viewTransactTilePopperAmtTVId);
        TextView viewTransactTilePopperAppxTV = (TextView) convertView.findViewById(R.id.viewTransactTilePopperAppxTVId);

        if("EXPENSE".equalsIgnoreCase(tranItem.getTRAN_TYPE())){
            viewTransactTilePopperTypTV.setBackgroundResource(R.drawable.circle_expense_text_view);
            viewTransactTilePopperTypTV.setText("E");
            viewTransactTilePopperAmtTV.setTextColor(viewTransactTilePopperAmtTV.getResources().getColor(R.color.finappleCurrencyNegColor));
        }

        //TODO:APPROXIMATIZATION

        viewTransactTilePopperNameTV.setText(tranItem.getTRAN_NAME());
        viewTransactTilePopperAccTV.setText(tranItem.getAccount());
        viewTransactTilePopperAmtTV.setText(String.valueOf(tranItem.getTRAN_AMT()));

        return convertView;
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