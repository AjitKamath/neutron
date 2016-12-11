package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.models.SpentOnMO;

import java.util.List;

import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 17/1/15.
 */
public class SpentonsFragmentListViewAdapter extends BaseAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;
    private LayoutInflater inflater;
    private List<SpentOnMO> spentonsList;
    private View.OnClickListener clickListener;

    public SpentonsFragmentListViewAdapter(Context mContext, List<SpentOnMO> spentonsList, View.OnClickListener clickListener) {
        super();

        this.mContext = mContext;
        this.spentonsList = spentonsList;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.clickListener = clickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        int layout = R.layout.spentons_spenton;

        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(layout, null);

            mHolder.spentonLL = (LinearLayout) convertView.findViewById(R.id.spentonLLId);
            mHolder.spentonIV = (ImageView) convertView.findViewById(R.id.spentonIVId);
            mHolder.spentonTV = (TextView) convertView.findViewById(R.id.spentonTVId);
            mHolder.spentonDeleteIV = (ImageView) convertView.findViewById(R.id.spentonDeleteIVId);
            mHolder.spentonModifyIV = (ImageView) convertView.findViewById(R.id.spentonModifyIVId);

            convertView.setTag(layout, mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag(layout);
        }

        SpentOnMO spenton = spentonsList.get(position);

        mHolder.spentonTV.setText(spenton.getSPNT_ON_NAME());
        mHolder.spentonIV.setBackgroundResource(Integer.parseInt(spenton.getSPNT_ON_IMG()));

        mHolder.spentonModifyIV.setTag(spenton);
        mHolder.spentonModifyIV.setOnClickListener(clickListener);

        //do not enable delete if the user id of the spenton is admin
        if(ADMIN_USERID.equalsIgnoreCase(spenton.getUSER_ID())){
            mHolder.spentonDeleteIV.setVisibility(View.INVISIBLE);
        }
        else{
            mHolder.spentonDeleteIV.setVisibility(View.VISIBLE);
            mHolder.spentonDeleteIV.setTag(spenton);
            mHolder.spentonDeleteIV.setOnClickListener(clickListener);
        }

        setFont(mHolder.spentonLL);

        return convertView;
    }

    @Override
    public int getCount() {
        return spentonsList.size();
    }

    @Override
    public SpentOnMO getItem(int position) {
        return spentonsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
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

    private class ViewHolder {
        private LinearLayout spentonLL;
        private ImageView spentonIV;
        private TextView spentonTV;
        private ImageView spentonDeleteIV;
        private ImageView spentonModifyIV;
    }

}