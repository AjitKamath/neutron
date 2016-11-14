package com.finappl.utils;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by ajit on 5/11/16.
 */

public class ReverseSortMapComparator implements Comparator<String>{

    @Override
    public int compare(String str1, String str2) {
        return str1.compareTo(str2);
    }

}
