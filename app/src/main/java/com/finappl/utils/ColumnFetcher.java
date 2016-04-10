package com.finappl.utils;

import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.finappl.utils.Constants.*;

public class ColumnFetcher{
	private static final String CLASS_NAME = getInstance().getClass().getName();
	
	private static ColumnFetcher instance = null;
	
	private ColumnFetcher(){}

	public synchronized static ColumnFetcher getInstance(){
		if (instance == null){
			instance = new ColumnFetcher();
		}
		return instance;
	}
	
	public static String loadString(Cursor cursor, String column){
	    if (cursor.isNull(cursor.getColumnIndex(column))){
	        return "";
	    }
	    return cursor.getString(cursor.getColumnIndex(column));
	}
	
	public static int loadInt(Cursor cursor, String column){
	    if (cursor.isNull(cursor.getColumnIndex(column))){
	        return 0;
	    }
	    return cursor.getInt(cursor.getColumnIndex(column));
	}

    public static Double loadDouble(Cursor cursor, String column){
        if (cursor.isNull(cursor.getColumnIndex(column))){
            return 0.0;
        }
        return cursor.getDouble(cursor.getColumnIndex(column));
    }
	
	public static Date loadDateTime(Cursor cursor, String column){
		SimpleDateFormat sdf = new SimpleDateFormat(DB_DATE_TIME_FORMAT);
		
	    if (cursor.isNull(cursor.getColumnIndex(column))){
	        return null;
	    }
	    Date date = null;
		try{
			date = sdf.parse(cursor.getString(cursor.getColumnIndex(column)));
		} 
		catch (ParseException e){
			Log.e(CLASS_NAME, "Error while fetching date object as string and parsing it to date object:"+e);
		}
	    
	    return date;
	}
	
	public static Date loadDate(Cursor cursor, String column){
		SimpleDateFormat sdf = new SimpleDateFormat(DB_DATE_FORMAT);
		
	    if (cursor.isNull(cursor.getColumnIndex(column))){
	        return null;
	    }
	    Date date = null;
		try{
			date = sdf.parse(cursor.getString(cursor.getColumnIndex(column)));
		} 
		catch (ParseException e){
			Log.e(CLASS_NAME, "Error while fetching date object as string and parsing it to date object:"+e);
		}
	    
	    return date;
	}
}
