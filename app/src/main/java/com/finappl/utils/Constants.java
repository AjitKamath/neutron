package com.finappl.utils;

import com.finappl.R;

import java.text.SimpleDateFormat;

/**
 * Created by ajit on 6/1/15.
 */
public final class Constants{
    //DB
    public static final String DB_NAME = "FINAPPL.db";
    public static final int DB_VERSION = 73;

    //Admin properties
    public static final String ADMIN_USERID = "ADMIN";

    //DB flag value for affirmative/non affirmative
    public static final String DB_AFFIRMATIVE = "Y";
    public static final String DB_NONAFFIRMATIVE = "N";

    public static final String UI_FONT = "Roboto-Light.ttf";

    //This is the default time at which notifications will be shown to the user
    public static final String DB_DEFAULT_NOTIF_TIME = "21:00";

    //limits
    public static final Double ACCOUNT_LOW_BALANCE_LIMIT = 100.0;

    //DB Tables
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
    public static final String CATEGORIES = "TRAVEL-"+R.drawable.travel+",SHOPPING-"+R.drawable.shopping+",SALARY-"+R.drawable.salary+",MORTGAGE-"+R.drawable.mortgage+",INVESTMENT-"+R.drawable.investment+",GIFT-"+R.drawable.gift+",FUEL-"+R.drawable.fuel+",COMMUTE-"+R.drawable.commute+",BILL-"+R.drawable.bill+",FOOD-"+ R.drawable.food+",ENTERTAINMENT-"+ R.drawable.entertainment+",HEALTH-"+R.drawable.health+",GROCERY-"+R.drawable.grocery+",OTHER-"+ R.drawable.other;
    public static final String DEFAULT_CATEGORY = "OTHER";
    public static final String ACCOUNTS = "CASH-"+R.drawable.cash+",BANK-"+R.drawable.bank+",CREDIT CARD-"+R.drawable.credit_card+",DEBIT CARD-"+R.drawable.debit_card;
    public static final String DEFAULT_ACCOUNT = "CASH";
    public static final String SPENT_ONS = "MYSELF-"+R.drawable.myself+",FAMILY-"+R.drawable.family+",FRIENDS-"+R.drawable.friends+",WORK-"+R.drawable.work;
    public static final String DEFAULT_SPENTON = "MYSELF";
    public static final String REPEATS = "DAY-"+R.drawable.day+",WEEK-"+R.drawable.week+",MONTH-"+R.drawable.month+",YEAR-"+R.drawable.year;
    public static final String DEFAULT_REPEAT = "MONTH";
    public static final String DEFAULT_COUNTRIES_CURRENCIES = "INDIA-91-RUPEE-INR,USA-1-DOLLAR-USD,AUSTRALIA-61-DOLLAR-AUD";

    public static final String DEFAULT_QUICK_TRANSACTION_NAME = "Quick Transaction";

    //months
    public static final String[] MONTHS_ARRAY = new String[]{"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

    //Month Range to fetch data in one shot
    public static final int MONTHS_RANGE = 11;  //always keep it odd

    //Decimal Limit
    public static final Integer DECIMAL_AFTER_LIMIT = 2;
    public static final Integer DECIMAL_BEFORE_LIMIT = 999999999;

    //Date formats
    public static final String JAVA_DATE_FORMAT = "dd-MM-yyyy";
    public static final String JAVA_DATE_FORMAT_1 = "MM-yyyy";
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DB_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DB_TIME_FORMAT = "HH:mm";
    public static final String UI_DATE_TIME_FORMAT = "d MMM ''yy H:mm:ss a";
    public static final String UI_DATE_FORMAT = "d MMM yyyy";
    public static final String UI_TIME_FORMAT = "hh:mm a";
    public static final SimpleDateFormat JAVA_DATE_FORMAT_SDF = new SimpleDateFormat(JAVA_DATE_FORMAT);
    public static final SimpleDateFormat JAVA_DATE_FORMAT_SDF_1 = new SimpleDateFormat(JAVA_DATE_FORMAT_1);
    public static final SimpleDateFormat DB_DATE_FORMAT_SDF = new SimpleDateFormat(DB_DATE_FORMAT);
    public static final SimpleDateFormat DB_TIME_FORMAT_SDF = new SimpleDateFormat(DB_TIME_FORMAT);
    public static final SimpleDateFormat DB_DATE_TIME_FORMAT_SDF = new SimpleDateFormat(DB_DATE_TIME_FORMAT);
    public static final SimpleDateFormat UI_DATE_FORMAT_SDF = new SimpleDateFormat(UI_DATE_FORMAT);
    public static final SimpleDateFormat UI_TIME_FORMAT_SDF = new SimpleDateFormat(UI_TIME_FORMAT);
    public static final SimpleDateFormat UI_DATE_TIME_FORMAT_SDF = new SimpleDateFormat(UI_DATE_TIME_FORMAT);

    //Bundle Keys, Sharde Prefs, Intent, fragment names
    //FRAGMENT NAMES
    public static final String FRAGMENT_LOGIN = "FRAGMENT_LOGIN";
    public static final String FRAGMENT_TRANSACTION = "FRAGMENT_TRANSACTION";
    public static final String FRAGMENT_TRANSACTION_DETAILS = "FRAGMENT_TRANSACTION_DETAILS";
    public static final String FRAGMENT_TRANSFER = "FRAGMENT_TRANSFER";
    public static final String FRAGMENT_TRANSFER_DETAILS = "FRAGMENT_TRANSFER_DETAILS";
    public static final String FRAGMENT_REPEAT = "FRAGMENT_REPEAT";
    public static final String FRAGMENT_AMOUNT = "FRAGMENT_AMOUNT";
    public static final String FRAGMENT_CATEGORY = "FRAGMENT_CATEGORY";
    public static final String FRAGMENT_ACCOUNT = "FRAGMENT_ACCOUNT";
    public static final String FRAGMENT_SPENTON = "FRAGMENT_SPENTON";
    public static final String FRAGMENT_CONFIRM = "FRAGMENT_CONFIRM";
    public static final String ADD_ACTIVITY = "ADD_ACTIVITY";

    //FRAGMENT OBJECT KEYS
    public static final String CONFIRM_MESSAGE = "CONFIRM_MESSAGE";
    public static final String TRANSACTION_OBJECT = "TRANSACTION_OBJECT";
    public static final String TRANSFER_OBJECT = "TRANSFER_OBJECT";
    public static final String LOGGED_IN_OBJECT = "LOGGED_IN_OBJECT";
    public static final String SELECTED_DATE = "SELECTED_DATE";
    public static final String REPEAT_OBJECT = "REPEAT_OBJECT";
    public static final String AMOUNT_OBJECT = "AMOUNT_OBJECT";
    public static final String CATEGORY_OBJECT = "CATEGORY_OBJECT";
    public static final String ACCOUNT_OBJECT = "ACCOUNT_OBJECT";
    public static final String SPENTON_OBJECT = "SPENTON_OBJECT";
    public static final String SELECTED_CATEGORY_OBJECT = "SELECTED_CATEGORY_OBJECT";
    public static final String SELECTED_ACCOUNT_OBJECT = "SELECTED_ACCOUNT_OBJECT";
    public static final String SELECTED_SPENTON_OBJECT = "SELECTED_SPENTON_OBJECT";
    public static final String SELECTED_REPEAT_OBJECT = "SELECTED_REPEAT_OBJECT";
    public static final String SELECTED_AMOUNT_OBJECT = "SELECTED_AMOUNT_OBJECT";
    public static final String ACCOUNT_TYPE_FLAG = "ACCOUNT_TYPE_FLAG";

    //SHARED PREFS KEYS
    public static final String SHARED_PREF = "SHARED_PREFERENCE";
    public static final String SHARED_PREF_ACTIVE_USER_ID = "ACTIVE_USER_ID";
}