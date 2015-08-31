package com.finappl.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.finappl.R;

public class NotificationScheduledTransactionActivity extends Activity {

	private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    @Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_scheduled_transaction);
        Log.e(CLASS_NAME, "User navigated to NotificationScheduledTransactionActivity");

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.notifiSchTransactLLId), robotoCondensedLightFont);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NotificationScheduledTransactionActivity.this, CalendarActivity.class);
        startActivity(intent);
        finish();
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

