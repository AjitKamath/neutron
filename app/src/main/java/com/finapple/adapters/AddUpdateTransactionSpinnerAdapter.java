package com.finapple.adapters;

/**
 * Created by ajit on 25/1/15.
 */

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
import com.finapple.model.SpinnerModel;
import com.finapple.util.FinappleUtility;

import java.util.List;

public class AddUpdateTransactionSpinnerAdapter extends ArrayAdapter<SpinnerModel>{

    private Activity context;
    private List<SpinnerModel> dataList;
    private List<Integer> colorList;
    private int resourceId;

    LayoutInflater inflater;

    public AddUpdateTransactionSpinnerAdapter(Activity context, int resourceId, List<SpinnerModel> dataList){
        super(context, resourceId, dataList);
        this.context = context;
        this.dataList = dataList;
        this.resourceId = resourceId;

        //get random colors in a list
        colorList = FinappleUtility.getInstance().getUnRandomizedColorList(dataList.size());

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null){
            row = inflater.inflate(resourceId, parent, false);
        }

        SpinnerModel item = dataList.get(position);

        //get spinner item resources
        View view = (View) row.findViewById(R.id.spinnerItemVId);
        TextView spinnerTV = (TextView) row.findViewById(R.id.spinnerItemTVId);
        LinearLayout spinnerLL = (LinearLayout) row.findViewById(R.id.spinnerLLId);

        //set font
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        spinnerTV.setTypeface(robotoCondensedLightFont);

        //set color for view
        view.setBackgroundColor(view.getResources().getColor(colorList.get(position)));

        //set text for spinner
        spinnerTV.setText(item.getItemName());

        //set iemId as tag for later use
        spinnerLL.setTag(item.getItemId());

        return row;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}