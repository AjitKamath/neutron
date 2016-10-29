package com.finappl.adapters;

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
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.SpinnerModel;
import com.finappl.utils.FinappleUtility;

import java.util.List;

public class AddUpdateTransactionSpinnerAdapter extends BaseAdapter{

    private Context context;
    private List<SpinnerModel> dataList;
    private List<Integer> colorList;
    private int resourceId;

    private LayoutInflater inflater;

    public AddUpdateTransactionSpinnerAdapter(Context context, int resourceId, List<SpinnerModel> dataList){
        super();
        this.context = context;
        this.dataList = dataList;
        this.resourceId = resourceId;

        //get random colors in a list
        colorList = FinappleUtility.getInstance().getUnRandomizedColorList(dataList.size());
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // This funtion called for each row ( Called data.size() times )
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}