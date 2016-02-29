package com.finappl.models;

import android.view.ViewGroup;

/**
 * Created by ajit on 18/2/16.
 */
public class CalendarPageModel {
    private int index;
    private ViewGroup layout;

    public CalendarPageModel(int index) {
        this.index = index;
        setIndex(index);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ViewGroup getLayout() {
        return layout;
    }

    public void setLayout(ViewGroup layout) {
        this.layout = layout;
    }
}
