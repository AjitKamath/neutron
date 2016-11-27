package com.finappl.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.UserMO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.finappl.utils.Constants.DECIMAL_AFTER_LIMIT;
import static com.finappl.utils.Constants.DECIMAL_BEFORE_LIMIT;
import static com.finappl.utils.Constants.SHARED_PREF;
import static com.finappl.utils.Constants.SHARED_PREF_ACTIVE_USER_ID;

public class FinappleUtility extends Activity{

    private final String CLASS_NAME = this.getClass().getName();
	private static FinappleUtility instance = null;
    private static final int[] COLOR_SET = new int[]{R.color.darkOrange, R.color.holo_blue_light, R.color.yellow, R.color.lime, R.color.Fuchsia, R.color.GreenYellow,
                R.color.DarkViolet, R.color.MediumAquamarine, R.color.today, R.color.SaddleBrown, R.color.greyDays, R.color.finOrb2};
    
    private static final int[] PLEASANT_COLOR_ARRAY = new int[]{R.color.SkyBlue, R.color.Plum, R.color.Aquamarine, R.color.Coral, R.color.Orange, R.color.Gold, R.color.LightSalmon, R.color.MediumSeaGreen, R.color.Violet, R.color.Tomato,
                R.color.SpringGreen, R.color.SandyBrown};

    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(this);

    private FinappleUtility(){}

	public synchronized static FinappleUtility getInstance(){
		if (instance == null) {
			instance = new FinappleUtility();
        }
		return instance;
	}

    public static byte[] imageBitmapToByte(Resources resources, Integer imageId){
        Bitmap b = BitmapFactory.decodeResource(resources, imageId);
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    public static Bitmap byteToImageBitmap(byte[] img){
        return BitmapFactory.decodeByteArray(img, 0, img.length);
    }

    private static String shortenAmountAmerican(String amountStr) {
        String resultStr = "";

        Double amount = Double.parseDouble(amountStr);

        //0 - 999 -> 9, 99, 999
        if(amount < 1000) {
            resultStr = String.valueOf(amount);
        }
        //1,000 - 9,999 -> 1K, 9.9K
        else if (amount >= 1000 && amount < 10000) {
            resultStr = shortenDoubleWithDecimal(amount/1000)+"K";
        }
        //10,000 - 99,999 -> 10K, 99K
        else if (amount >= 10000 && amount < 100000) {
            resultStr = shortenDoubleWithoutDecimal(amount/1000)+"K";
        }
        //100,000 - 999,999 -> 0.1M, 0.9M
        else if (amount >= 100000 && amount < 1000000) {
            resultStr = shortenDoubleWithDecimal(amount/1000000)+"M";
        }
        //1,000,000 - 9,999,999 -> 1M, 9.9M
        else if (amount >= 1000000 && amount < 10000000) {
            resultStr = shortenDoubleWithDecimal(amount/1000000)+"M";
        }
        //10,000,000 - 99,999,999 -> 10M, 99M
        else if (amount >= 10000000 && amount < 100000000) {
            resultStr = shortenDoubleWithoutDecimal(amount/1000000)+"M";
        }
        else {
            resultStr = "ERR";
        }

        resultStr = resultStr.replace(".0", "");
        resultStr = resultStr.replace(".00", "");

        return resultStr;
    }

    private static String shortenAmountIndian(String amountStr) {
        String resultStr = "";

        Double amount = Double.parseDouble(amountStr);

        //0 - 999 -> 9, 99, 999
        if(amount < 1000) {
            resultStr = String.valueOf(amount);
        }
        //1,000 - 9,999 -> 1K, 9.9K
        else if (amount >= 1000 && amount < 10000) {
            resultStr = shortenDoubleWithDecimal(amount/1000)+"K";
        }
        //10,000 - 99,999 -> 10K, 99K
        else if (amount >= 10000 && amount < 100000) {
            resultStr = shortenDoubleWithoutDecimal(amount/1000)+"K";
        }
        //1,00,000 - 9,99,999 -> 1L, 9.9L
        else if (amount >= 100000 && amount < 1000000) {
            resultStr = shortenDoubleWithDecimal(amount/100000)+"L";
        }
        //10,00,000 - 99,99,999 -> 10L, 99L
        else if (amount >= 1000000 && amount < 10000000) {
            resultStr = shortenDoubleWithoutDecimal(amount/100000)+"L";
        }
        //1,00,00,000 - 9,99,99,999 -> 1C, 9.9C
        else if (amount >= 10000000 && amount < 100000000) {
            resultStr = shortenDoubleWithDecimal(amount/10000000)+"C";
        }
        else {
            resultStr = "ERR";
        }

        resultStr = resultStr.replace(".0", "");
        resultStr = resultStr.replace(".00", "");

        if(resultStr.equalsIgnoreCase("100K")){
            resultStr = "1L";
        }
        else if(resultStr.equalsIgnoreCase("100L")){
            resultStr = "1C";
        }

        return resultStr;
    }

    private static String shortenDoubleWithDecimal(Double value) {
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(value);
    }

    private static String shortenDoubleWithoutDecimal(Double value) {
        DecimalFormat df = new DecimalFormat("0");
        return df.format(value);
    }

    public static TextView shortenAmountView(TextView amountTV, UserMO userMO, Double amount){
        if(amount <= 0){
            if(amount < 0) {
                amount = amount * -1;
            }
            amountTV.setTextColor(amountTV.getResources().getColor(R.color.finappleCurrencyNegColor));
        }
        else{
            amountTV.setTextColor(amountTV.getResources().getColor(R.color.finappleCurrencyPosColor));
        }
        amountTV.setText(FinappleUtility.shortenAmount(userMO.getMETRIC(), String.valueOf(amount)));

        return amountTV;
    }

    public static TextView formatAmountView(TextView amountTV, UserMO userMO, Double amount){
        if(amount <= 0){
            if(amount < 0) {
                amount = amount * -1;
            }
            amountTV.setTextColor(amountTV.getResources().getColor(R.color.finappleCurrencyNegColor));
        }
        else{
            amountTV.setTextColor(amountTV.getResources().getColor(R.color.finappleCurrencyPosColor));
        }
        amountTV.setText(FinappleUtility.formatAmount(userMO.getMETRIC(), String.valueOf(amount)));

        return amountTV;
    }

    private static String formatDecimals(String inputStr){
        if(inputStr.contains(".")){
            String inputStrArr[] = inputStr.split("\\.");

            if(inputStrArr.length == 0){
                return "0.";
            }
            else if(inputStrArr.length > 1){
                String afterDeimalsStr = inputStrArr[1];

                if(afterDeimalsStr.length() >= DECIMAL_AFTER_LIMIT){
                    return inputStr.substring(0, inputStr.indexOf(".")+DECIMAL_AFTER_LIMIT+1);
                }
            }
        }
        return inputStr;
    }

    public static String cleanUpAmount(String amountStr){
        if(amountStr.startsWith(".")){
            return "0"+amountStr.substring(0, DECIMAL_AFTER_LIMIT);
        }

        if(amountStr.endsWith(".")){
            return amountStr.substring(0, amountStr.lastIndexOf("\\."));
        }
        return amountStr;
    }

    public static String formatAmount(String numberSystemStr, String amountStr){
        if("AMERICAN".equalsIgnoreCase(numberSystemStr)){
            return formatAmountAmerican(amountStr);
        }
        else{
            return formatAmountIndian(amountStr);
        }
    }

    public static String shortenAmount(String numberSystemStr, String amountStr){
        if(amountStr.equalsIgnoreCase("0.0")){
            return "";
        }

        if("AMERICAN".equalsIgnoreCase(numberSystemStr)){
            return shortenAmountAmerican(amountStr);
        }
        else{
            return shortenAmountIndian(amountStr);
        }
    }

    private static String formatAmountIndian(String amountStr) {
        amountStr = amountStr.replace(",", "");

        amountStr = formatDecimals(amountStr);

        //check if exceeds the upper limit
        String beforeDecStr = "";
        String afterDecStr = "";

        boolean hasDot = false;

        if(amountStr.contains(".") && !amountStr.endsWith(".")){
            beforeDecStr = amountStr.split("\\.")[0];
            afterDecStr = amountStr.split("\\.")[1];
        }
        else if(amountStr.contains(".")){
            beforeDecStr = amountStr.replace(".", "");
            hasDot = true;
        }
        else {
            beforeDecStr = amountStr;
        }

        if(Float.parseFloat(beforeDecStr) >= DECIMAL_BEFORE_LIMIT){
            beforeDecStr = beforeDecStr.substring(0, beforeDecStr.length()-1);
        }

        //99,99,99,999
        //formatting with commas
        switch(beforeDecStr.length()){
            case 4: beforeDecStr = beforeDecStr.substring(0, 1)+","+beforeDecStr.substring(1);
                break;
            case 5: beforeDecStr = beforeDecStr.substring(0, 2)+","+beforeDecStr.substring(2);
                break;
            case 6: beforeDecStr = beforeDecStr.substring(0, 1)+","+beforeDecStr.substring(1,3)+","+beforeDecStr.substring(3);
                break;
            case 7: beforeDecStr = beforeDecStr.substring(0, 2)+","+beforeDecStr.substring(2,4)+","+beforeDecStr.substring(4);
                break;
            case 8: beforeDecStr = beforeDecStr.substring(0, 1)+","+beforeDecStr.substring(1,3)+","+beforeDecStr.substring(3,5)+","+beforeDecStr.substring(5);
                break;
            case 9: beforeDecStr = beforeDecStr.substring(0, 2)+","+beforeDecStr.substring(2,4)+","+beforeDecStr.substring(4,6)+","+beforeDecStr.substring(6);
                break;
        }

        if(hasDot){
            beforeDecStr += ".";
        }

        String result;
        if(!afterDecStr.isEmpty()){
            result = beforeDecStr+"."+afterDecStr;
        }
        else{
            result = beforeDecStr;
        }

        if(result.endsWith(".0")){
            return result.substring(0, result.lastIndexOf(".0"));
        }
        else if(result.endsWith(".00")){
            return result.substring(0, result.lastIndexOf(".00"));
        }
        else{
            return result;
        }
    }

    private static String formatAmountAmerican(String amountStr) {
        amountStr = amountStr.replace(",", "");

        amountStr = formatDecimals(amountStr);

        //check if exceeds the upper limit
        String beforeDecStr = "";
        String afterDecStr = "";

        boolean hasDot = false;

        if(amountStr.contains(".") && !amountStr.endsWith(".")){
            beforeDecStr = amountStr.split("\\.")[0];
            afterDecStr = amountStr.split("\\.")[1];
        }
        else if(amountStr.contains(".")){
            beforeDecStr = amountStr.replace(".", "");
            hasDot = true;
        }
        else {
            beforeDecStr = amountStr;
        }

        if(Float.parseFloat(beforeDecStr) >= DECIMAL_BEFORE_LIMIT){
            beforeDecStr = beforeDecStr.substring(0, beforeDecStr.length()-1);
        }

        //999,999,999
        //formatting with commas
        switch(beforeDecStr.length()){
            case 4: beforeDecStr = beforeDecStr.substring(0, 1)+","+beforeDecStr.substring(1);
                break;
            case 5: beforeDecStr = beforeDecStr.substring(0, 2)+","+beforeDecStr.substring(2);
                break;
            case 6: beforeDecStr = beforeDecStr.substring(0, 3)+","+beforeDecStr.substring(3);
                break;
            case 7: beforeDecStr = beforeDecStr.substring(0, 1)+","+beforeDecStr.substring(1,4)+","+beforeDecStr.substring(4);
                break;
            case 8: beforeDecStr = beforeDecStr.substring(0, 2)+","+beforeDecStr.substring(2,5)+","+beforeDecStr.substring(5);
                break;
            case 9: beforeDecStr = beforeDecStr.substring(0, 3)+","+beforeDecStr.substring(3,6)+","+beforeDecStr.substring(6);
                break;
        }

        if(hasDot){
            beforeDecStr += ".";
        }

        if(!afterDecStr.isEmpty()){
            return beforeDecStr+"."+afterDecStr;
        }
        else{
            return beforeDecStr;
        }
    }

    public List<Integer> getRandomPleasantColorList(Integer resourcesCount){
        List<Integer> colorList = new ArrayList<Integer>();

        for(int i=0; i<resourcesCount; i++){
            if(i>PLEASANT_COLOR_ARRAY.length-1){
                colorList.add(PLEASANT_COLOR_ARRAY[i-PLEASANT_COLOR_ARRAY.length]);
            }
            else{
                colorList.add(PLEASANT_COLOR_ARRAY[i]);
            }
        }

        //shuffle the list
        long seed = System.nanoTime();
        Collections.shuffle(colorList, new Random(seed));

        return colorList;
    }

    public int getDpAsPixels(Resources res, int dp){
        float scale = res.getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
    }

    public List<Integer> getUnRandomizedColorList(Integer resourcesCount){
        List<Integer> colorList = new ArrayList<Integer>();

        for(int i=0; i<resourcesCount; i++){
            if(i>COLOR_SET.length-1){
                colorList.add(COLOR_SET[i-COLOR_SET.length]);
            }
            else{
                colorList.add(COLOR_SET[i]);
            }
        }

        return colorList;
    }
    
    public Integer getRandomPleasantColor(){
        List<Integer> colorList = new ArrayList<Integer>();
        for(Integer iterArr : PLEASANT_COLOR_ARRAY){
            colorList.add(iterArr);
        }

        //shuffle the list
        long seed = System.nanoTime();
        Collections.shuffle(colorList, new Random(seed));

        return colorList.get(0);
    }

    //convert 02-02-2015 to 2 feb '15
    public String getFormattedDate(String dateStr){
        if(dateStr != null && "".equalsIgnoreCase(dateStr)){
            return "";
        }

        if(dateStr == null){
            return "";
        }

        String dateStrArr[] = dateStr.split("-");
        int convertedDate = Integer.parseInt(dateStrArr[0]);
        String convertedMonthStr = Constants.MONTHS_ARRAY[Integer.parseInt(dateStrArr[1])-1];
        String convertedYearStr = "'"+dateStrArr[2].substring(2);

        return convertedDate + " " + convertedMonthStr + " " + convertedYearStr;
    }

    public void pullDbFromDeepSystem(){
        //--------------------------------------
        Log.i(CLASS_NAME, "Pulling Database out from the deep system..");
        File f=new File("/data/data/com.finappl.android/databases/"+Constants.DB_NAME);
        FileInputStream fis=null;
        FileOutputStream fos=null;

        try{
            fis = new FileInputStream(f);
            fos = new FileOutputStream(Environment.getExternalStorageDirectory() +"/database/"+Constants.DB_NAME);
            while(true){
                int i=fis.read();
                if(i!=-1){
                    fos.write(i);
                }
                else{
                    break;
                }
            }
            fos.flush();
        }
        catch(Exception e){
            Log.e(CLASS_NAME, "ERROR !!"+e);
        }
        finally{
            try{
                fos.close();
                fis.close();
                Log.i(CLASS_NAME, "Pulling Database out from the deep system..Completed");
            }
            catch(IOException ioe){
                Log.e(CLASS_NAME, "ERROR !!"+ioe);
            }
        }
        //--------------------------------------
    }

    public String getActiveUserId(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String userIdStr = sharedpreferences.getString(SHARED_PREF_ACTIVE_USER_ID, null);

        return userIdStr;
    }

    public static String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
