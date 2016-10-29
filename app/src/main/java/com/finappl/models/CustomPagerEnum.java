package com.finappl.models;

import com.finappl.R;

/**
 * Created by ajit on 30/9/15.
 */
public enum CustomPagerEnum {

    RED("calendar_transaction_options_popper_info", R.layout.calendar_transaction_options_popper_info),
    BLUE("blue", R.layout.blue),
    ORANGE("orange", R.layout.orange);

    private String mTitleResId;
    private int mLayoutResId;

    CustomPagerEnum(String titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public String getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
