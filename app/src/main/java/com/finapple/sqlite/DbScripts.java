package com.finapple.sqlite;




import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbScripts extends SQLiteOpenHelper {

	private static final String APP_NAME = "FINAPPLE";
	
	private static final String DATABASE_NAME = "FINAPPLE.db";
	
	private SQLiteDatabase database;

	private static final int DATABASE_VERSION = 1;

	private static String usersTable = "USERS_DATA";

	private static String expenseTable = "EXPENSE_DATA";

	private static String summaryTable = "SUMMARY_DATA";

	private static String rankTable = "RANK_DATA";

	public int entryCount = 10;	//change this to change no. of entries you want to add into the db.

	public String[] userId = new String[] { "FIN00000", "FIN00001", "FIN00002",
			"FIN00003", "FIN00004", "FIN00005", "FIN00006", "FIN00007",
			"FIN00008", "FIN00009" };

	public String[] dateTime = new String[] { "12-06-12|12:30",
			"13-06-12|17:56", "27-06-12|14:50", "12-07-12|13:23",
			"12-07-12|00:56", "13-06-12|01:55", "16-06-12|14:45",
			"01-12-12|03:34", "28-02-12|09:59", "13-11-13|02:45" };

	public DbScripts(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void init()	//call this to add some dummy data into the db for testing
	{
		dbAddUserTable();	//to add dummy users into the db
		//dbAddExpenseTable();	//to add expense as well as add/update summary table
	}
	
	public void dbAddUserTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		String[] password = new String[] { "finapple", "finapple", "finapple",
				"finapple", "finapple", "finapple", "finapple", "finapple",
				"finapple", "finapple" };
		String[] name = new String[] { "user0", "user1", "user2", "user3",
				"user4", "user5", "user6", "user7", "user8", "user9" };
		String[] email = new String[] { "user0@gmail.com", "user1@gmail.com",
				"user2@gmail.com", "user3@gmail.com", "user4@gmail.com",
				"user5@gmail.com", "user6@gmail.com", "user7@gmail.com",
				"user8@gmail.com", "user9@gmail.com" };
		int[] age = new int[] { 21, 22, 14, 90, 34, 33, 23, 66, 56, 77 };
		int[] salary = new int[] { 21000, 2000, 14000, 9000000, 340000000, 330,
				2303400, 66000, 56100, 77300 };
		String[] currency = new String[] { "Rupee", "Rupee", "Rupee", "Rupee",
				"Rupee", "Rupee", "Rupee", "Rupee", "Rupee", "Rupee" };
		String[] country = new String[] { "India", "India", "India", "India",
				"India", "India", "India", "India", "India", "India" };
		String[] deviceId = new String[] { "Android00", "Android01",
				"Android02", "Android03", "Android04", "Android05",
				"Android06", "Android07", "Android08", "Android09" };
		String[] custCat = new String[] { "rent", "swimming",
				"rent|swimming|play", "betting", "gambling|cards|oc",
				"lottery|temple", "club|kids", "accident|other", "", "business" };
		String[] custPayType = new String[] { "gift", "", "exchange",
				"stocks|cheque", "monopoly coins", "Bitcoins",
				"casino coins|cheque|dd|gold", "gold", "gold|cheque", "others" };
		String[] custSpentOn = new String[] { "relatives", "friends|relatives",
				"friends", "", "Neighbour", "2nd wife", "Children", "", "",
				"Wife" };
		String[] status = new String[] { "", "", "", "", "", "", "Active", "",
				"", "" };

		for (int i = 0; i < entryCount; i++) {
			values.put("USER_ID", userId[i]);
			values.put("PASSWORD", password[i]);
			values.put("NAME", name[i]);
			values.put("EMAIL", email[i]);
			values.put("AGE", age[i]);
			values.put("SALARY", salary[i]);
			values.put("DATE_TIME", dateTime[i]);
			values.put("CURRENCY", currency[i]);
			values.put("COUNTRY", country[i]);
			values.put("DEVICE_ID", deviceId[i]);
			values.put("CUST_CAT", custCat[i]);
			values.put("CUST_PAY_TYPE", custPayType[i]);
			values.put("CUST_SPENT_ON", custSpentOn[i]);
			values.put("STATUS", status[i]);

			db.insert(usersTable, null, values);
		}
		db.close();
	}

	public void dbAddExpenseTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		String[] expCat = new String[] { "food", "food", "cellphone",
				"commute", "commute", "shopping", "fuel", "fuel", "food",
				"entertainment" };
		int[] expense = new int[] { 300, 25, 350, 30, 44, 34, 70, 56, 400, 550 };
		String[] payType = new String[] { "cash", "gift", "cash", "cheque",
				"debit card", "credit card", "credit card", "cash", "cash",
				"cash" };
		String[] spentOn = new String[] { "family", "friends", "family",
				"personal", "business", "family", "kids", "grandparents",
				"2nd wife", "girlfriend" };
		String[] note = new String[] { "Pappu eats idli", "I lost my sandals",
				"i bought a can of petrol", "just a note", "typing is fun",
				"dont read this", "buhahaha", "i'm a disco dancer",
				"I love America", "i hate u like i love u" };
		String[] visibility = new String[] { "", "", "", "", "", "", "", "",
				"", "" };

		for (int i = 0; i < entryCount; i++) {
			values.put("USER_ID", userId[i]);
			values.put("DATE_TIME", dateTime[i]);
			values.put("EXP_CAT", expCat[i]);
			values.put("EXPENSE", expense[i]);
			values.put("PAY_TYPE", payType[i]);
			values.put("SPENT_ON", spentOn[i]);
			values.put("NOTE", note[i]);
			values.put("VISIBILITY", visibility[i]);

			db.insert(expenseTable, null, values);

			dbUpdateSummary(values); // to update/insert summaryTable entry

		}
		db.close();

	}

	public void dbUpdateSummary(ContentValues values) // TODO:Test-call this to update/insert summaryTable entry
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String[] splitDateTime = values.get("DATE_TIME").toString().split("\\|");
		String date = splitDateTime[0];
		Log.i(this.getClass().getName(), "Stripped date to be checked with that in summary table is:"
						+ date);
		String query = "SELECT * FROM " + summaryTable + " WHERE USER_ID="
				+ values.get("USER_ID") + " AND DATE=" + date + " AND EXP_CAT="
				+ values.get("EXP_CAT") + ";";
		
		try
		{
			Cursor cursor = db.rawQuery(query, null);
			if (cursor.moveToFirst()) 
			{
				Log.i(this.getClass().getName(), "This ExpCat Exists already !!\nFor EXP_CAT:"
						+ values.get("EXP_CAT") + " on:" + date + ".The row Count:"
						+ cursor.getCount() + " should be 1");

				int totalExpense = cursor.getInt(cursor
						.getColumnIndex("TOTAL_EXPENSE"));
				int expEntryCount = cursor.getInt(cursor
						.getColumnIndex("EXP_ENTRY_COUNT"));
				Log.i(this.getClass().getName(), "Old Total expense:" + totalExpense
						+ "\nOld Exp Entry Count:" + expEntryCount);

				totalExpense = totalExpense
						+ Integer.parseInt(values.get("EXPENSE").toString());
				expEntryCount = expEntryCount + 1;
				Log.i(this.getClass().getName(), "New Total expense:" + totalExpense
						+ "\nNew Exp Entry Count:" + expEntryCount);

				ContentValues updateValues = new ContentValues();

				updateValues.put("TOTAL_EXPENSE", totalExpense);
				updateValues.put("EXP_ENTRY_COUNT", expEntryCount);

				// updating an old row
				db.update(summaryTable, values, "DATE" + " = ?",
						new String[] { date });
			}// end of if(cursor.moveToFirst)
		}
		catch(Exception e)	// TODO optimization required. Not a good idea to check whether column not existing via exception.
		{
			Log.i(this.getClass().getName(), "Exception in dbUpdateSummary..may be because cloumn not exist..."+e);
			Log.i(this.getClass().getName(), "This ExpCat Doesnt Exist So Adding new entry !!");
			
			try
			{
				ContentValues insertValues = new ContentValues();
	
				insertValues.put("USER_ID", values.get("USER_ID").toString());
				insertValues.put("DATE", "NO DATE");
				insertValues.put("EXP_CAT", values.get("EXP_CAT").toString());
				insertValues.put("TOTAL_EXP", Integer.parseInt(values.get("EXPENSE").toString()));
				insertValues.put("EXP_ENTRY_COUNT", 1);
				insertValues.put("VISIBILITY", "YES");
	
				// inserting a new row
				
				db.insert(summaryTable, null, insertValues);
			}
			catch(Exception e1)
			{
				Log.e(this.getClass().getName(), "Exception while inserting new row in summary table:"+e1);
			}
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		setDatabase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

	public void setDatabase(SQLiteDatabase database) {
		this.database = database;
	}
	
	

}
