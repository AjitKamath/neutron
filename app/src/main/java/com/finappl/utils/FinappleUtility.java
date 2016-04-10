package com.finappl.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import com.finappl.R;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.UserMO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    public UserMO getUser(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String userIdStr = sharedpreferences.getString(SHARED_PREF_ACTIVE_USER_ID, null);

        if(userIdStr != null && !userIdStr.isEmpty()){
            return authorizationDbService.getActiveUser(userIdStr);
        }
        else{
            Log.e(CLASS_NAME, "Error while fetching user id from the shared preference");
        }
        return null;
    }
}
