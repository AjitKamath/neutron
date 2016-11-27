package com.finappl.utils;

import java.util.Random;

public class IdGenerator {
	
	private static IdGenerator instance = null;
	
	private IdGenerator(){}

	public synchronized static IdGenerator getInstance(){
		if (instance == null) {
			instance = new IdGenerator();
		}
		return instance;
	}

	public static String generateUniqueId(String id){
		return genRand()+id+genRand();
	}

	public static int genRand() {
		Random r = new Random( System.currentTimeMillis() );
		return 10000 + r.nextInt(20000);
	}

	public Integer getIntegerOnString(String str){
        return str.hashCode();
    }
	
}
