package com.finapple.util;

public class DateFormatConverterDBtoUI {
	private static DateFormatConverterDBtoUI instance = null;

	private DateFormatConverterDBtoUI(){}

	public synchronized static DateFormatConverterDBtoUI getInstance() 
	{
		if (instance == null) 
		{
			instance = new DateFormatConverterDBtoUI();
		}
		return instance;
	}
	
	public String dbToUi(String date)
	{
		//from yyyy-MM-dd to dd-MM-yyyy
		String dateStrArr[] = date.split("-");
		return dateStrArr[2]+"-"+dateStrArr[1]+"-"+dateStrArr[0];
	}
	
	public String UiToDb(String date)
	{
		//from dd-MM-yyyy to yyyy-MM-dd 
		String dateStrArr[] = date.split("-");
		return dateStrArr[2]+"-"+dateStrArr[1]+"-"+dateStrArr[0];
	}
}
