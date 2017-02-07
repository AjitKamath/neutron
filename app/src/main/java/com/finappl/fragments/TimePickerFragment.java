package com.finappl.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

/**
 * Created by ajit on 17/10/16.
 */

public class TimePickerFragment extends DialogFragment {
    TimePickerDialog.OnTimeSetListener onTimeSet;
    private int hour, minute;

    public TimePickerFragment() {}

    public void setCallBack(TimePickerDialog.OnTimeSetListener onTime) {
        onTimeSet = onTime;
    }

    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        hour = args.getInt("hour");
        minute = args.getInt("calendar_day__");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), onTimeSet, hour, minute, false);
    }
}
