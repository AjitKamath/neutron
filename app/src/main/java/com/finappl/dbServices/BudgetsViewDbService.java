package com.finappl.dbServices;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finappl.models.BudgetModel;
import com.finappl.models.BudgetsViewModel;
import com.finappl.utils.ColumnFetcher;
import com.finappl.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.finappl.utils.Constants.*;


public class BudgetsViewDbService extends SQLiteOpenHelper {

	private final String CLASS_NAME = this.getClass().getName();

	private static BudgetsViewDbService sInstance = null;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
	private SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(DB_DATE_TIME_FORMAT);

	//method to get all the budgets_view, Daily, Weekly, Monthly, Yearly.
	public BudgetsViewModel getAllBudgets(String userId){
		SQLiteDatabase db = this.getWritableDatabase();
		StringBuilder sqlQuerySB = new StringBuilder(50);

		//get all Budgets for the user
		sqlQuerySB.append(" SELECT ");
		sqlQuerySB.append(" BUD.BUDGET_ID, ");
		sqlQuerySB.append(" BUD.BUDGET_NAME, ");
		sqlQuerySB.append(" BUD.BUDGET_GRP_ID, ");
		sqlQuerySB.append(" BUD.BUDGET_GRP_TYPE, ");
		sqlQuerySB.append(" BUD.BUDGET_TYPE, ");
		sqlQuerySB.append(" BUD.BUDGET_AMT, ");
		sqlQuerySB.append(" BUD.BUDGET_NOTE, ");
		sqlQuerySB.append(" SPNT.SPNT_ON_NAME, ");
		sqlQuerySB.append(" ACC.ACC_NAME, ");
		sqlQuerySB.append(" CAT.CAT_NAME ");

		sqlQuerySB.append(" FROM ");
		sqlQuerySB.append(DB_TABLE_BUDGET+" BUD ");

		sqlQuerySB.append(" LEFT OUTER JOIN ");
		sqlQuerySB.append(DB_TABLE_SPENTON+" SPNT ");
		sqlQuerySB.append(" ON BUD.BUDGET_GRP_ID = SPNT.SPNT_ON_ID ");

		sqlQuerySB.append(" LEFT OUTER JOIN ");
		sqlQuerySB.append(DB_TABLE_ACCOUNT+" ACC ");
		sqlQuerySB.append(" ON BUD.BUDGET_GRP_ID = ACC.ACC_ID ");

		sqlQuerySB.append(" LEFT OUTER JOIN ");
		sqlQuerySB.append(DB_TABLE_CATEGORY+" CAT ");
		sqlQuerySB.append(" ON BUD.BUDGET_GRP_ID = CAT.CAT_ID ");

		sqlQuerySB.append(" WHERE ");
		sqlQuerySB.append(" BUD.USER_ID = '"+userId+"' ");

		sqlQuerySB.append(" AND ");
		sqlQuerySB.append(" BUD.BUDGET_IS_DEL = '"+DB_NONAFFIRMATIVE+"' ");

		sqlQuerySB.append(" ORDER BY ");
		sqlQuerySB.append(" BUD.BUDGET_TYPE, ");
		sqlQuerySB.append(" BUD.BUDGET_GRP_TYPE ");

		Log.i(CLASS_NAME, "query to get all budgets_view for budgets_view view screen : " + sqlQuerySB);
		Cursor cursor = db.rawQuery(sqlQuerySB.toString(), null);

		BudgetsViewModel budgetsViewModelObj = new BudgetsViewModel();
		Map<String, List<BudgetModel>> dailyBudgetsMap = new TreeMap<>();
		Map<String, List<BudgetModel>> weeklyBudgetsMap = new TreeMap<>();
		Map<String, List<BudgetModel>> monthlyBudgetsMap = new TreeMap<>();
		Map<String, List<BudgetModel>> yearlyBudgetsMap = new TreeMap<>();
        List<BudgetModel> budgetModelList;
        BudgetModel budgetModelObj;

		while (cursor.moveToNext()) {
			String budIdStr = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_ID");
			String budNameStr = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_NAME");
			String budGrpIdStr = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_GRP_ID");
			String budGrpTypeStr = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_GRP_TYPE");
			String budTypeStr = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_TYPE");
			Double budAmt = ColumnFetcher.getInstance().loadDouble(cursor, "BUDGET_AMT");
			String budNoteStr = ColumnFetcher.getInstance().loadString(cursor, "BUDGET_NOTE");
            String categoryNameStr = ColumnFetcher.getInstance().loadString(cursor, "CAT_NAME");
            String accountNameStr = ColumnFetcher.getInstance().loadString(cursor, "ACC_NAME");
            String spentOnNameStr = ColumnFetcher.getInstance().loadString(cursor, "SPNT_ON_NAME");

            budgetModelObj = new BudgetModel();
            budgetModelObj.setBUDGET_ID(budIdStr);
            budgetModelObj.setBUDGET_NAME(budNameStr);
            budgetModelObj.setBUDGET_GRP_ID(budGrpIdStr);
            budgetModelObj.setBUDGET_GRP_TYPE(budGrpTypeStr);
            budgetModelObj.setBUDGET_TYPE(budTypeStr);
            budgetModelObj.setBUDGET_AMT(budAmt);
            budgetModelObj.setBUDGET_NOTE(budNoteStr);
            budgetModelObj.setAccountNameStr(accountNameStr);
            budgetModelObj.setCategoryNameStr(categoryNameStr);
            budgetModelObj.setSpentOnNameStr(spentOnNameStr);

			if("DAILY".equalsIgnoreCase(budTypeStr)){
                if(dailyBudgetsMap.containsKey(budGrpTypeStr)){
                    budgetModelList = dailyBudgetsMap.get(budGrpTypeStr);
                }
                else{
                    budgetModelList = new ArrayList<>();
                }
                budgetModelList.add(budgetModelObj);
                dailyBudgetsMap.put(budGrpTypeStr, budgetModelList);
            }
            else if("WEEKLY".equalsIgnoreCase(budTypeStr)){
                if(weeklyBudgetsMap.containsKey(budGrpTypeStr)){
                    budgetModelList = weeklyBudgetsMap.get(budGrpTypeStr);
                }
                else{
                    budgetModelList = new ArrayList<>();
                }
                budgetModelList.add(budgetModelObj);
                weeklyBudgetsMap.put(budGrpTypeStr, budgetModelList);
            }
            else if("MONTHLY".equalsIgnoreCase(budTypeStr)){
                if(monthlyBudgetsMap.containsKey(budGrpTypeStr)){
                    budgetModelList = monthlyBudgetsMap.get(budGrpTypeStr);
                }
                else{
                    budgetModelList = new ArrayList<>();
                }
                budgetModelList.add(budgetModelObj);
                monthlyBudgetsMap.put(budGrpTypeStr, budgetModelList);
            }
            else if("YEARLY".equalsIgnoreCase(budTypeStr)){
                if(yearlyBudgetsMap.containsKey(budGrpTypeStr)){
                    budgetModelList = monthlyBudgetsMap.get(budGrpTypeStr);
                }
                else{
                    budgetModelList = new ArrayList<>();
                }
                budgetModelList.add(budgetModelObj);
                yearlyBudgetsMap.put(budGrpTypeStr, budgetModelList);
            }
		}
		//end of get all budgets_view

        budgetsViewModelObj.setBudgetDailyMap(dailyBudgetsMap);
        budgetsViewModelObj.setBudgetWeeklyMap(weeklyBudgetsMap);
        budgetsViewModelObj.setBudgetMonthlyMap(monthlyBudgetsMap);
        budgetsViewModelObj.setBudgetYearlyMap(yearlyBudgetsMap);

		db.close();
		return budgetsViewModelObj;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	// get class instance
	public static BudgetsViewDbService getInstance(Context context){
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null){
			sInstance = new BudgetsViewDbService(context.getApplicationContext());
		}
		return sInstance;
	}

	// constructors
	public BudgetsViewDbService(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}

}
