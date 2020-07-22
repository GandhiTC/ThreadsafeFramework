package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



public class Configurations
{
	private 	static 	final 	ConfigReader	readconfig			= new ConfigReader();
	
	//	The following 3 values are currently retrieved from a MySQL database
	//	Refer to getCredentialsFromDatabase() method in BaseClass.java
	protected	static 		 	String			baseURL				= "";
	protected 	static 		 	String			username			= "";
	protected	static 		 	String			password			= "";
	
	//	To get their values from a properties file instead
	//	Comment out previous 3 values and uncomment next 3 lines
//	protected	static 	final 	String			baseURL				= readconfig.getApplicationURL();
//	protected 	static 	final 	String			username			= readconfig.getUsername();
//	protected	static 	final 	String			password			= readconfig.getPassword();
	
	protected 	static 	final 	String			firefoxPath			= readconfig.getFirefoxPath();
	protected 	static 	final 	String			iePath				= readconfig.getIEPath();
	protected 	static 	final 	String			chromePath			= readconfig.getChromePath();
	protected 	static 	final 	String			edgePath			= readconfig.getEdgePath();
	protected 	static 	final 	String			logsFolder			= readconfig.getLogsFolder();
	protected 	static 	final 	String			reportsFolder		= readconfig.getReportsFolder();
}
