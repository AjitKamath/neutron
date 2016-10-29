package com.finappl.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.finappl.R;

public class JimBrokeItActivity extends Activity {

	private final String CLASS_NAME = this.getClass().getName();

    @Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jim_broke_it);

        Log.e(CLASS_NAME, "JIM BROKE IT ERROR !!");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(JimBrokeItActivity.this, CalendarActivity.class);
        startActivity(intent);
        finish();
    }
}

