package com.finapple.util;

import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ColumnFetcher{
	private final String CLASS_NAME = this.getClass().getName();
	
	private static ColumnFetcher instance = null;
	
	private ColumnFetcher(){}

	public synchronized static ColumnFetcher getInstance(){
		if (instance == null){
			instance = new ColumnFetcher();
		}
		return instance;
	}
	
	public String loadString(Cursor cursor, String column){
	    if (cursor.isNull(cursor.getColumnIndex(column))){
	        return null;
	    }
	    return cursor.getString(cursor.getColumnIndex(column));
	}
	
	public int loadInt(Cursor cursor, String column){
	    if (cursor.isNull(cursor.getColumnIndex(column))){
	        return 0;
	    }
	    return cursor.getInt(cursor.getColumnIndex(column));
	}

    public Double loadDouble(Cursor cursor, String column){
        if (cursor.isNull(cursor.getColumnIndex(column))){
            return 0.0;
        }
        return cursor.getDouble(cursor.getColumnIndex(column));
    }
	
	public Date loadDateTime(Cursor cursor, String column){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
	    if (cursor.isNull(cursor.getColumnIndex(column))) 
	    {
	        return null;
	    }
	    Date date = null;
		try 
		{
			date = sdf.parse(cursor.getString(cursor.getColumnIndex(column)));
		} 
		catch (ParseException e) 
		{
			Log.e(CLASS_NAME, "Error while fetching date object as string and parsing it to date object:"+e.getMessage());
		}
	    
	    return date;
	}
	
	public Date loadDate(Cursor cursor, String column) 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
	    if (cursor.isNull(cursor.getColumnIndex(column))) 
	    {
	        return null;
	    }
	    Date date = null;
		try 
		{
			date = sdf.parse(cursor.getString(cursor.getColumnIndex(column)));
		} 
		catch (ParseException e) 
		{
			Log.e(CLASS_NAME, "Error while fetching date object as string and parsing it to date object:"+e.getMessage());
		}
	    
	    return date;
	}
}
