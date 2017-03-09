package com.cognitionis.tipsem.helpers;

public class Logger {
	
	public static void Write(String message)
	{
		System.out.println(message);
	}
	
	public static void WriteDebug(String message)
	{
		  if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
              System.err.println(message);
          }
	}
	
	public static void WriteError(String message, Exception e)
	{ 
      System.err.println(message);
      if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
          e.printStackTrace(System.err);
      }
	}

}
