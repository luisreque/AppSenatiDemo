package com.demo.appsenatidemo.components;

import android.util.Log;

public class Flog {

	public static String CONSOLE="CONSOLE";
	
	public Flog() {
		// 
	}
	public static void v(Object ... parameters)
	{
		String output=">>> ";
		int aux=0;
		String auxs="";
		for (int i = 0; i < parameters.length; i++) 
		{
			if(parameters[i] instanceof Integer)
			{
				aux=(Integer)parameters[i];
				auxs=String.valueOf(aux);
				
				output+=" "+auxs;
			}else
			{
				output+=" "+parameters[i];
			}
			
		}
		Log.v(CONSOLE, output);
	}
	
	public static void vLogger(String str) {
	    if(str.length() > 4000) {
	    Log.i(CONSOLE,str.substring(0, 4000));
	    	vLogger(str.substring(4000));
	    } else
	    Log.i(CONSOLE,str);
	}

}
