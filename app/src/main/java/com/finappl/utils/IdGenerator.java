package com.finappl.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IdGenerator {
	
	private static IdGenerator instance = null;
	
	private IdGenerator(){}

	public synchronized static IdGenerator getInstance(){
		if (instance == null) {
			instance = new IdGenerator();
		}
		return instance;
	}

	public String generateUniqueId(String id){
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy'"+id+"'HHmmss");
		return sdf.format(new Date());
	}

	public Integer getIntegerOnString(String str){
        return str.hashCode();
    }
	
}
