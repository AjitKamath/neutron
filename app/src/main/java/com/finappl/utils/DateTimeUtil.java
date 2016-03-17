package com.finappl.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.finappl.utils.Constants.DB_DATE_FORMAT;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT;

public class DateTimeUtil {

	private static final String CLASS_NAME = getInstance().getClass().getName();
	
	private static DateTimeUtil instance = null;

	public static Date cleanUpDate(String dateStr){
		if(dateStr.contains("-PAST")){
			dateStr = dateStr.substring(0, dateStr.indexOf("-PAST"));
		}
		else if(dateStr.contains("-FUTURE")){
			dateStr = dateStr.substring(0, dateStr.indexOf("-FUTURE"));
		}
		else if(dateStr.contains("-PRESENT")){
			dateStr = dateStr.substring(0, dateStr.indexOf("-PRESENT"));
		}
		else if(dateStr.contains("-TODAY")){
			dateStr = dateStr.substring(0, dateStr.indexOf("-TODAY"));
		}

		try{
			SimpleDateFormat sdf = new SimpleDateFormat(JAVA_DATE_FORMAT);
			return sdf.parse(dateStr);
		}
		catch(ParseException e){
			Log.e(CLASS_NAME, "Error !! Exception in parsing the date: "+dateStr);
		}
		return null;
	}

	public static String reformatDate(String dateStr){
		String dateStrArr[] = dateStr.split("-");
		
		if(dateStrArr.length != 3){
			Log.e(CLASS_NAME, "ERROR in date format !! expecting dd-MM-yyyy but found : "+dateStr);
			return null;
		}
		
		String dayStr = dateStrArr[0];
		String monthStr = dateStrArr[1];
		
		if(dayStr.length() != 2){
			dayStr = "0"+dayStr;
	    }
	    	
	    if(monthStr.length() != 2){
	    	monthStr = "0"+monthStr;
	    }
			
	    return dateStrArr[2]+"-"+monthStr+"-"+dayStr;
	}

	public static String[] getStartAndEndMonthDates(String dateStr, int range){
		String dateStrArr[] = dateStr.split("-");
		
		if(dateStrArr.length != 2){
			Log.e(CLASS_NAME, "ERROR in date format !! expecting MM-yyyy but found : "+dateStr);
			return null;
		}
		
		Integer month = Integer.parseInt(dateStrArr[0])-1;
		Integer year = Integer.parseInt(dateStrArr[1]);
		
		//get Start Date
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
	    cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		    	
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		    
		cal.add(Calendar.MONTH, -range);
		String startDayStr = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		String startMonthStr = String.valueOf(cal.get(Calendar.MONTH)+1);
		String startYearStr = String.valueOf(cal.get(Calendar.YEAR));
		//get Start Date ends
	    	
	    	//get End Date
		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		   	
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		   
		cal.add(Calendar.MONTH, +range+1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		String endDayStr = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		String endMonthStr = String.valueOf(cal.get(Calendar.MONTH)+1);
		String endYearStr = String.valueOf(cal.get(Calendar.YEAR));
		//get End Date ends
	    	
		String startDateStr =  reformatDate(startDayStr+"-"+startMonthStr+"-"+startYearStr);
		String endDateStr = reformatDate(endDayStr+"-"+endMonthStr+"-"+endYearStr);
		return new String[]{startDateStr, endDateStr};
	}
	
	public static boolean isDateAfterOrEquals(String checkDateStr, String withDateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		try{
			Date checkDate = sdf.parse(checkDateStr);
			Date withDate = sdf.parse(withDateStr);

			if(withDate.equals(checkDate) || withDate.before(checkDate)){
				return true;
			}
		}
		catch(ParseException e){
			Log.e(CLASS_NAME, "Date Parse Exception");
		}
		return false;
	}

	public static List<String> getAllDatesInWeekOnDate(String dateStr){
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		List<String> datesList = new ArrayList<>();

		String dateStrArr[] = dateStr.split("-");
		now.set(Integer.parseInt(dateStrArr[2]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[0]));

		int day = now.get(Calendar.DAY_OF_WEEK);
		switch (day){
			case 1:
				now.add(Calendar.DATE, -6);
				break;
			case 2:
				now.add(Calendar.DATE, 0);
				break;
			case 3:
				now.add(Calendar.DATE, -1);
				break;
			case 4:
				now.add(Calendar.DATE, -2);
				break;
			case 5:
				now.add(Calendar.DATE, -3);
				break;
			case 6:
				now.add(Calendar.DATE, -4);
				break;
			case 7:
				now.add(Calendar.DATE, -5);
				break;
		}

		// Print dates of the current week starting on Monday
		for (int i = 0; i < 7; i++) {
			datesList.add(format.format(now.getTime()));
			now.add(Calendar.DATE, 1);
		}

		return datesList;
	}

	public static String getDayOfWeekFromDate(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE", Locale.US);
		return dateFormat.format(date);
	}

	public static int getLastDayOfTheMonth(String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		try{
			Date today = sdf.parse(dateStr);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(today);

			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.DATE, -1);

			Date lastDayOfMonth = calendar.getTime();

			return Integer.parseInt(sdf.format(lastDayOfMonth).split("-")[0]);
		}
		catch(ParseException e){
			Log.e(CLASS_NAME, "Date Parse Exception");
		}
		return 0;
	}

	public static boolean checkBetween(Date date, Date dateStart, Date dateEnd){
	    if (date != null && dateStart != null && dateEnd != null){
	        if (date.after(dateStart) && date.before(dateEnd)){
	            return true;
	        }
	        else{
	            return false;
	        }
	    }
	    return false;
	}
	
	// get how manyth day this date is in that particular month
	/*public static int getDayNumberInMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DB_DATE);
		String dateStr = sdf.format(new Date());
		
		String dateArr[] = dateStr.split("-");

		Calendar calendar = Calendar.getInstance();

		int year = Integer.parseInt(dateArr[0]);
		int month = Integer.parseInt(dateArr[1]) - 1;
		int date = Integer.parseInt(dateArr[2]);

		calendar.set(year, month, date);
		int days = calendar.get(Calendar.DAY_OF_MONTH);

		// System.out.println("Number of Days: " + days);

		return days;
	}*/
	
	//get max days in that particular month
	public static int getMaxDaysInMonth(String dateStr){
		String dateArr[] = dateStr.split("-");
		
		Calendar calendar = Calendar.getInstance();
		
		int year = Integer.parseInt(dateArr[0]);
		int month = Integer.parseInt(dateArr[1]) - 1;
		int date = Integer.parseInt(dateArr[2]);
		
		calendar.set(year, month, date);
		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		//System.out.println("Number of Days: " + days);
		
		return days;
	}
	
	//get how manyth day this date is in that particular year
	/*public static int getDayNumberInYear(){
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DB_DATE);
		String dateStr = sdf.format(new Date());
		
		String dateArr[] = dateStr.split("-");
		
		Calendar calendar = Calendar.getInstance();
		
		int year = Integer.parseInt(dateArr[0]);
		int month = Integer.parseInt(dateArr[1]) - 1;
		int date = Integer.parseInt(dateArr[2]);
		
		calendar.set(year, month, date);
		int days = calendar.get(Calendar.DAY_OF_YEAR);
		
		//System.out.println("Number of Days: " + days);
		
		return days;
	}*/
	
	public static int getMaxDaysInYear(String dateStr){
		String dateArr[] = dateStr.split("-");
		
		Calendar calendar = Calendar.getInstance();
		
		int year = Integer.parseInt(dateArr[0]);
		int month = Integer.parseInt(dateArr[1]) - 1;
		int date = Integer.parseInt(dateArr[2]);
		
		calendar.set(year, month, date);
		int days = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		
		//System.out.println("Number of Days: " + days);
		
		return days;
	}
	
	//method to check if the chosen date is from past, present or future
	/*public static String checkDateForPastPresentFuture(String choosenDateStr, String checkWhat){
		SimpleDateFormat sdf =  null;
		String choosenDateStrArr[] = choosenDateStr.split("-");
		
		if(checkWhat.equals("YEAR")){
			sdf = new SimpleDateFormat(Constants.DB_YEAR);
			choosenDateStr = choosenDateStrArr[0];
		}
		else if(checkWhat.equals("YEARMONTH")){
			sdf = new SimpleDateFormat(Constants.DB_YEARMONTH);
			choosenDateStr = choosenDateStrArr[0]+"-"+choosenDateStrArr[1];
		}
		
		Date todaysDate = null;
		Date choosenDate = null;
		
		try{
			choosenDate = sdf.parse(choosenDateStr);
			todaysDate = sdf.parse(sdf.format(new Date()));
		} 
		catch (ParseException e){
			Log.e(CLASS_NAME, "Exception during date conversion in checkDateForPastPresentFuture Util method in DateTimeUtil Class :"+e.getMessage());
		}
		
		//check if past date
		if(choosenDate.before(todaysDate)){
			Log.i(CLASS_NAME, "The choosen date is past date");
			return "PAST";
		}
		//check if future date
		else if(choosenDate.after(todaysDate)){
			Log.i(CLASS_NAME, "The choosen date is future date");
			return "FUTURE";
		}
		////check if today
		else{
			Log.i(CLASS_NAME, "The choosen date/ pre selected date is today's date");
			return "TODAY";
		}
	}*/
	
	//method which returns DateTimestamp
	public static String getDateTimeStamp(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		
		return sdf.format(date);
	}
	
	//method to convert yyyy-MM-dd to dd MMM yyyy format
	public static String convertDateToGoodFormat(String dateStr){
		SimpleDateFormat sdfBad = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfGood = new SimpleDateFormat("dd MMM yyyy");
		
		try{
			String goodDateStr = sdfGood.format(sdfBad.parse(dateStr));
			Log.i(CLASS_NAME, "Bad date ("+dateStr+") converted to good date ("+goodDateStr+")");
			return goodDateStr;
		} 
		catch (ParseException e){
			Log.i(CLASS_NAME, "Error while parsing Bad date ("+dateStr+")"+e.getMessage());
			return "JUNK";
		}
	}
	
	//	method to convert UI date(String)dd MMM yyyy -> DB date(String) dd-MM-yyyy
	public static String uiDateStringToDbDateString(String dateStr){
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd MMM yyyy");
		SimpleDateFormat sdfDb = new SimpleDateFormat("dd-MM-yyyy");
		
		try{
			return sdfDb.format(sdfUi.parse(dateStr));
		} 
		catch (ParseException e){
			Log.e(CLASS_NAME, "Error in uiDateStringToDbDateString() while parsing date" + e.getMessage());
		}
		
		return null;
	}
	
	//	method to convert UI date(String)dd MM yyyy -> UI date(String) dd MMM yyyy
	public static String uiDateStringToUIDateString(String dateStr){
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd MM yyyy");
		SimpleDateFormat sdfUi2 = new SimpleDateFormat("dd MMM yyyy");
		
		try{
			return sdfUi2.format(sdfUi.parse(dateStr));
		} 
		catch (ParseException e){
			Log.e(CLASS_NAME, "Error in uiDateStringToDbDateString() while parsing date"+e.getMessage());
		}
		
		return null;
	}
	
	//	method to convert date(String) from yyyy-MM-dd to Date object
	public static Date appDateStringToDateObj(String appDateStr){
		SimpleDateFormat sdfUi = new SimpleDateFormat("yyyy-MM-dd");
		
		try{
			return sdfUi.parse(appDateStr);
		} 
		catch (ParseException e){
			Log.e(CLASS_NAME, "Error in appDateStringToUiDateString() while parsing date"+e.getMessage());
		}
		return null;
	}
	
	//	method to convert date(date) -> UI date(String) dd MMM yyyy
	public static String dateDateToUIDateString(Date date){
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd MMM yyyy");
		
		return sdfUi.format(date);
	}
	
	//	method to convert date(date) -> db date(String) dd-MM-yyyy
	public static String dateDateToDbDateString(Date date){
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd-MM-yyyy");
		return sdfUi.format(date);
	}
	
	//	method to convert date(date) -> db date(String) dd-MM-yyyy HH:mm:ss
	public static String dateDateToDbDateString1(Date date){
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return sdfUi.format(date);
	}
	
	//	method to convert UI date(String)dd MMM yyyy -> date(Date) 
	public static Date uiDateToDateDate(String dateStr){
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd MMM yyyy");
		
		try{
			return sdfUi.parse(dateStr);
		} 
		catch (ParseException e){
			Log.e(CLASS_NAME, "Error in uiDateStringToDbDateString() while parsing date"+e.getMessage());
		}
		return null;
	}
	
	//get instance
	private DateTimeUtil(){}

	public synchronized static DateTimeUtil getInstance(){
		if (instance == null){
			instance = new DateTimeUtil();
		}
		return instance;
	}

	public static List<String> getAllDatesBetweenRange(String[] dateStrArr) {
		SimpleDateFormat sdf = new SimpleDateFormat(DB_DATE_FORMAT);

		//begin th list from today
		dateStrArr[0] = sdf.format(new Date());

		String dateMonthStrArr[] = dateStrArr[0].split("-");
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.set(Integer.parseInt(dateMonthStrArr[0]), Integer.parseInt(dateMonthStrArr[1]) - 1, Integer.parseInt(dateMonthStrArr[2]));

		List<String> datesList = new ArrayList<>();
		try {
			while (true) {
                Date dates = sdf.parse(sdf.format(cal.getTime()));

				if ((dates.equals(sdf.parse(dateStrArr[0])) || dates.after(sdf.parse(dateStrArr[0])))
                        && (dates.equals(sdf.parse(dateStrArr[1])) || dates.before(sdf.parse(dateStrArr[1])))){
					datesList.add(sdf.format(dates));
					cal.add(Calendar.DATE, 1);
				}
				else{
					return datesList;
				}
			}
		}
		catch (ParseException pe){
			Log.e(CLASS_NAME, "Error !!:"+pe);
		}
		return datesList;
	}
}
