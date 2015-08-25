package com.finapple.util;

import com.finapple.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FinappleUtility{

    private final String CLASS_NAME = this.getClass().getName();
	private static FinappleUtility instance = null;
    private static final int[] COLOR_SET = new int[]{R.color.darkOrange, R.color.holo_blue_light, R.color.yellow, R.color.lime, R.color.Fuchsia, R.color.GreenYellow,
                R.color.DarkViolet, R.color.MediumAquamarine, R.color.today, R.color.SaddleBrown, R.color.greyDays, R.color.finOrb2};
    
    private static final int[] PLEASANT_COLOR_ARRAY = new int[]{R.color.SkyBlue, R.color.Plum, R.color.Aquamarine, R.color.Coral, R.color.Orange, R.color.Gold, R.color.LightSalmon, R.color.MediumSeaGreen, R.color.Violet, R.color.Tomato,
                R.color.SpringGreen, R.color.SandyBrown};

    private FinappleUtility(){}

	public synchronized static FinappleUtility getInstance(){
		if (instance == null) 
		{
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

}
