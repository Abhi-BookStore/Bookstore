package com.bookstore.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IndiaConstants {
	
	public final static String INDIA = "INDIA";
	public final static HashMap<String, String> IndiaStateMap = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("DL", "DELHI");
			put("UP", "Uttar Pradesh");
			put("BR", "Bihar");
			put("HR", "Haryana");
			put("RJ", "Rajasthan");
			put("KL", "KERALA");
			put("TN", "TAMIL NADU");
			put("GJ", "GUJARAT");
			put("MH", "MAHARASHTRA");
		}
	};
	
	public final static List<String> listOfIndiaStateCodes = new ArrayList<>(IndiaStateMap.keySet());
	public final static List<String> listOfIndiaStateNames = new ArrayList<>(IndiaStateMap.values());

}
