package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



public class Configurations
{
	private 	static 	final 	ConfigReader	readconfig			= new ConfigReader();
	
	//	The following 3 values are now retrieved from a MySQL database
	//	Refer to getCredentials() in BaseClass.java
//	protected	static 	final 	String			baseURL				= readconfig.getApplicationURL();
//	protected 	static 	final 	String			username			= readconfig.getUsername();
//	protected	static 	final 	String			password			= readconfig.getPassword();
	
	protected 	static 	final 	String			firefoxPath			= readconfig.getFirefoxPath();
	protected 	static 	final 	String			iePath				= readconfig.getIEPath();
	protected 	static 	final 	String			chromePath			= readconfig.getChromePath();
	protected 	static 	final 	String			edgePath			= readconfig.getEdgePath();
	protected 	static 	final 	String			logsFolder			= readconfig.getLogsFolder();
	protected 	static 	final 	String			reportsFolder		= readconfig.getReportsFolder();
	protected 	static 	final 	String			screenshotsFolder	= readconfig.getScreenshotsFolder();
}
