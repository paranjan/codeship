package com.ibm.cio.saucelab;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Test
{
	public static void main(String[] args)
	{
		Map<String, String> my = new HashMap<String, String>();
		my.put("city", "Latur");
		my.put("name", "ajit");
		
		Gson obj = new Gson();
		System.out.println(obj.toJson(my));
	}
}	
