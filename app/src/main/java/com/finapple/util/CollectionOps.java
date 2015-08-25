package com.finapple.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectionOps {

	private static CollectionOps instance = null;
	
	private CollectionOps(){}

	public synchronized static CollectionOps getInstance() 
	{
		if (instance == null) 
		{
			instance = new CollectionOps();
		}
		return instance;
	}
	
	//	generic method to sort map by its value
	public Map<String, String> sortedMapByValue(Map<String, String> unsortMap) 
	{
		List<Map.Entry<String, String>> list = new LinkedList<Map.Entry<String, String>>(unsortMap.entrySet()); // Convert Map to List

		Collections.sort(list, new Comparator<Map.Entry<String, String>>() 
		{
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) 
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		}); // Convert sorted map back to a Map

		Map<String, String> sortedMap = new LinkedHashMap<String, String>();

		for (Iterator<Map.Entry<String, String>> it = list.iterator(); it.hasNext();) 
		{
			Map.Entry<String, String> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
