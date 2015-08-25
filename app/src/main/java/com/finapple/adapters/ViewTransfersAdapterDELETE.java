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
import com.finapple.model.TransferModel;
import com.finapple.util.FinappleUtility;

import java.util.List;

/**
 * Created by ajit on 17/1/15.
 */
public class ViewTransfersAdapterDELETE extends ArrayAdapter<TransferModel> {

    private Context mContext;
    private int layoutResourceId;
    private List<TransferModel> dataList;
    private List<Integer> colorList;

    public ViewTransfersAdapterDELETE(Context mContext, int layoutResourceId, List<TransferModel> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataList = data;
        this.colorList = FinappleUtility.getInstance().getRandomPleasantColorList(dataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        TransferModel dataItem = dataList.get(position);

        int color = colorList.get(position);

        TextView viewFromTransferTV = (TextView) convertView.findViewById(R.id.viewFromTransferTVId);
        TextView viewToTransferTV = (TextView) convertView.findViewById(R.id.viewToTransferTVId);
        TextView viewTransferAmtTV = (TextView) convertView.findViewById(R.id.viewTransferAmtTVId);
        TextView viewTransferDateTV = (TextView) convertView.findViewById(R.id.viewTransferDateTVId);
        View viewTransferTattooView = (View) convertView.findViewById(R.id.viewTransferTattooViewId);
        LinearLayout viewTransferItemLL = (LinearLayout) convertView.findViewById(R.id.viewTransferItemLLId);

        //set font
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        viewFromTransferTV.setTypeface(robotoCondensedLightFont);
        viewToTransferTV.setTypeface(robotoCondensedLightFont);
        viewTransferAmtTV.setTypeface(robotoCondensedLightFont);
        viewTransferDateTV.setTypeface(robotoCondensedLightFont);

        viewFromTransferTV.setText(dataItem.getFromAccName());
        viewToTransferTV.setText(dataItem.getToAccName());
        viewTransferAmtTV.setText(dataItem.getCurrency()+dataItem.getTRNFR_AMT());
        viewTransferDateTV.setText(FinappleUtility.getInstance().getFormattedDate(dataItem.getTRNFR_DATE()));

        viewTransferTattooView.setBackgroundColor(viewTransferTattooView.getResources().getColor(color));
        viewTransferItemLL.setTag(dataItem.getTRNFR_ID());

        return convertView;
    }

}