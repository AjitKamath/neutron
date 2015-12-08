package com.finappl.utils;

import android.app.Activity;

/**
 * Created by ajit on 6/1/15.
 */
public class Constants extends Activity {
    //DB Name
    public static final String DB_NAME = "FINAPPL.db";

    //DB version
    public static final int DB_VERSION = 17;

    //DB Properties
    public static final String USERNAME = "Ajit";
    public static final String PASSWORD = "test";

    //Admin properties
    public static final String ADMIN_USERID = "ADMIN";

    //Test Constants
    //public static final String TEST_USERID = "USER";
    public static final String TEST_USERNAME = "Ajit";
    public static final String TEST_USERPASS = "Password";
    public static final String TEST_USEREMAIL = "ajitkamathk@gmail.com";
    public static final String TEST_USERGEND = "M";
    public static final String TEST_USERDOB = "17 Jul 1990";
    public static final String TEST_USERCOUNTRY = "CNTRY1";
    public static final String TEST_USERDEVICEID = "MotoG";
    public static final String TEST_USERISDELETED = "N";

    public static final String TEST_USERCURRENCY = "CUR1";
    public static final String TEST_DAILYLIMIT = "100";
    public static final String TEST_MONTHLYLIMIT = "3000";
    public static final String TEST_YEARLYLIMIT = "250000";

    //DB Date Time format
    public static final String DB_DATETIME = "yyyy-MM-dd KK:mm:ss";
    public static final String DB_DATE = "yyyy-MM-dd";
    public static final String DB_TIME = "KK:mm:ss";
    public static final String DB_YEARMONTH = "yyyy-MM";
    public static final String DB_YEAR = "yyyy";

    //UI Date Time format
    public static final String UI_DATETIME = "dd MMM yyyy KK:mm:ss";
    public static final String UI_DATE = "dd MMM yyyy";
    public static final String UI_TIME = "KK:mm:ss";
    public static final String UI_HOURMINUTE = "hh:mm";

    //DB flag value for affirmative/non affirmative
    public static final String DB_AFFIRMATIVE = "Y";
    public static final String DB_NONAFFIRMATIVE = "N";

    //This is the default time at which notifications will be shown to the user
    public static final String DB_DEFAULT_NOTIF_TIME = "10:00";

    //DB Tables
    public static final String DB_TABLE_USERSTABLE = "USERS";
    public static final String DB_TABLE_ACCOUNTTABLE = "ACCOUNT_MASTER";
    public static final String DB_TABLE_CATEGORYTABLE = "CATEGORY_MASTER";
    public static final String DB_TABLE_SPENTONTABLE = "SPENT_ON_MASTER";
    public static final String DB_TABLE_TRANSACTIONTABLE = "TRANSACTIONS";
    public static final String DB_TABLE_SCHEDULEDTRANSACTIONSTABLE = "SCHEDULED_TRANSACTIONS";
    public static final String DB_TABLE_BUDGETTABLE = "BUDGETS";
    public static final String DB_TABLE_CATEGORYTAGSTABLE = "CATEGORY_TAGS";
    public static final String DB_TABLE_TRANSFERSTABLE = "TRANSFERS";
    public static final String DB_TABLE_SHEDULEDTRANSFERSTABLE = "SCHEDULED_TRANSFERS";
    public static final String DB_TABLE_COUNTRYTABLE = "COUNTRY_MASTER";
    public static final String DB_TABLE_CURRENCYTABLE = "CURRENCY_MASTER";
    public static final String DB_TABLE_WORK_TIMELINETABLE = "WORK_TIMELINE";
    public static final String DB_TABLE_NOTIFICATIONSTABLE = "NOTIFICATIONS";
    public static final String DB_TABLE_SETTINGS_NOTIFICATIONS = "SETTINGS_NOTIFICATIONS";
    public static final String DB_TABLE_SETTINGS_SOUNDS = "SETTINGS_SOUNDS";
    public static final String DB_TABLE_SETTINGS_SECURITY = "SETTINGS_SECURITY";

    //defaults & their id's
    public static final String DEFAULTS_CATEGORIES = "Food/Drinks-CAT_1-expense,Fuel/Gas-CAT_2-expense,Health/Medicare-CAT_3-expense,Shopping-CAT_4-expense,Accommodation/Hotel-CAT_5-expense," +
                "Other-CAT_6-expense,Salary-CAT_7-income,Interest-CAT_8-income,Entertainment-CAT_9-expense,Lost-CAT_10-expense,Stationary-CAT_11-expense,Cellphone-CAT_12-expense,Logistics-CAT_13-expense,Education/Learning-CAT_14-expense,Investments-CAT_15-expense,Lend/Borrow-CAT_16-expense";
    public static final String DEFAULTS_CATEGORIES_SELECT = "Other";

    public static final String DEFAULTS_SPENTON = "Self-SPNT_1,Family-SPNT_2,Friends-SPNT_3,Business-SPNT_4";
    public static final String DEFAULTS_SPENTON_SELECT = "Self";

    public static final String DEFAULTS_ACCOUNTS = "Cash-ACC_1,Credit Card-ACC_2,Bank-ACC_3,Debit Card-ACC_4,Gift Card-ACC_5";
    public static final String DEFAULTS_ACCOUNTS_SELECT = "cash";

    public static final String DEFAULTS_COUNTRIES = "India-CNTRY1-CUR1-bug.png,USA-CNTRY2-CUR2-bug.png";
    public static final String DEFAULTS_CURRENCIES = "Rupee-CUR1-bug.png-₹,Dollar-CUR2-bug.png-$";

    public static final String DEFAULTS_TRANSACTIONTYPE = "expense-0,income-1";
    public static final String DEFAULTS_TRANSACTIONTYPE_SELECT = "expense";

    public static final String DEFAULT_QUICK_TRANSACTION_NAME = "Quick Transaction";

    //data for repeat spinner
    public static final String ADDRECUREXPENSEACTIVITY_REPEAT = "Everyday-0,Monthly-1,Yearly-2";

    //default_button values
    public static final String BUTTON_VALUES_DONE = "done";
    public static final String BUTTON_VALUES_CANCEL = "discard";
    public static final String BUTTON_VALUES_UPDATE = "update";

    //weekdays order
    public static final String[] WEEK_ARRAY = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    //months
    public static final String[] MONTHS_ARRAY = new String[]{"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER",
                "DECEMBER"};

    //date super scripts
    public static final String[] DATE_SUPERSCRIPT_ARRAY = new String[]{"", "st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
                "th", "th", "th", "st", "nd", "rd", "th", "th", "th", "th", "th",  "th", "th", "st"};

    //SETTINGS_ARRAY  PARENT1-child1_child2_child3|PARENT2-child1_child2_child3
    public static final String SETTINGS_ARRAY = "Manage-Categories_Accounts_Spent On|Budgets-Create_View|" +
                "Schedules-Transactions_Transfers|Profile-Profile Info_Notifications_Widget|Security-Lock Application_Pin|Backup & Restore-Backup_Restore_Backup " +
                "Interval|Stats-Usage Statistics";

}