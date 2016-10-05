package com.finappl.utils;

import android.app.Activity;
import android.content.Context;

import java.text.SimpleDateFormat;

/**
 * Created by ajit on 6/1/15.
 */
public final class Constants{
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

    //DB flag value for affirmative/non affirmative
    public static final String DB_AFFIRMATIVE = "Y";
    public static final String DB_NONAFFIRMATIVE = "N";

    //This is the default time at which notifications will be shown to the user
    public static final String DB_DEFAULT_NOTIF_TIME = "10:00";

    //DB Tables
    //OBSOLETE TABLES
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
    //OBSOLETE TABLES ENDS--

    //DB
    public static final String DB_NAME = "FINAPPL.db";
    public static final int DB_VERSION = 29;

    public static final String DB_TABLE_USER = "USERS";
    public static final String DB_TABLE_ACCOUNT = "ACCOUNTS";
    public static final String DB_TABLE_CATEGORY = "CATEGORIES";
    public static final String DB_TABLE_SPENTON = "SPENT_ONS";
    public static final String DB_TABLE_TRANSACTION = "TRANSACTIONS";
    public static final String DB_TABLE_BUDGET = "BUDGETS";
    public static final String DB_TABLE_TRANSFER = "TRANSFERS";
    public static final String DB_TABLE_REPEAT = "REPEATS";
    public static final String DB_TABLE_COUNTRY = "COUNTRIES";
    public static final String DB_TABLE_NOTIFICATION = "NOTIFICATIONS";
    public static final String DB_TABLE_SETTING = "SETTINGS";
    //DB

    //defaults
    public static final String DEFAULT_CATEGORIES = "FOOD,ENTERTAINMENT,BILL,COMMUTE,SHOPPING,FUEL,HEALTH,OTHER";
    public static final String DEFAULT_CATEGORY = "OTHER";
    public static final String DEFAULT_ACCOUNTS = "CASH,BANK,CREDIT CARD,DEBIT CARD";
    public static final String DEFAULT_SPENT_ONS = "SELF,FAMILY,FRIENDS,BUSINESS";
    public static final String DEFAULT_COUNTRIES_CURRENCIES = "INDIA-91-RUPEE-INR,USA-1-DOLLAR-USD,AUSTRALIA-61-DOLLAR-AUD";

    public static final String DEFAULT_QUICK_TRANSACTION_NAME = "Quick Transaction";

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

    //Month Range to fetch data in one shot
    public static final int MONTHS_RANGE = 11;  //always keep it odd

    //Date formats
    public static final String JAVA_DATE_FORMAT = "dd-MM-yyyy";
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DB_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String UI_DATE_TIME_FORMAT = "d MMM ''yy H:mm:ss a";
    public static final String UI_DATE_FORMAT = "d MMM ''yy";
    public static final SimpleDateFormat JAVA_DATE_FORMAT_SDF = new SimpleDateFormat(JAVA_DATE_FORMAT);
    public static final SimpleDateFormat DB_DATE_FORMAT_SDF = new SimpleDateFormat(DB_DATE_FORMAT);
    public static final SimpleDateFormat DB_DATE_TIME_FORMAT_SDF = new SimpleDateFormat(DB_DATE_TIME_FORMAT);
    public static final SimpleDateFormat UI_DATE_FORMAT_SDF = new SimpleDateFormat(UI_DATE_FORMAT);
    public static final SimpleDateFormat UI_DATE_TIME_FORMAT_SDF = new SimpleDateFormat(UI_DATE_TIME_FORMAT);

    //Bundle Keys, Sharde Prefs, Intent, fragment names
    public static final String FRAGMENT_TRANSACTION = "FRAGMENT_TRANSACTION";
    public static final String FRAGMENT_CATEGORY = "FRAGMENT_CATEGORY";
    public static final String FRAGMENT_LOGIN = "FRAGMENT_LOGIN";
    public static final String TRANSACTION_OBJECT = "TRANSACTION_OBJECT";
    public static final String CATEGORY_OBJECT = "CATEGORY_OBJECT";
    public static final String SELECTED_CATEGORY_OBJECT = "SELECTED_CATEGORY_OBJECT";
    public static final String SHARED_PREF = "SHARED_PREFERENCE";
    public static final String SHARED_PREF_ACTIVE_USER_ID = "ACTIVE_USER_ID";
}