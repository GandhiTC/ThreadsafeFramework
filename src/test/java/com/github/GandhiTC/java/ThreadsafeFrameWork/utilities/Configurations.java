package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



public class Configurations
{
	private 	static final 	ConfigReader	readconfig			= new ConfigReader();
	public 		static final 	String			baseURL				= readconfig.getApplicationURL();
	protected 	static final 	String			username			= readconfig.getUsername();
	protected	static final 	String			password			= readconfig.getPassword();
	public 		static final 	String			firefoxPath			= readconfig.getFirefoxPath();
	public 		static final 	String			iePath				= readconfig.getIEPath();
	public 		static final 	String			chromePath			= readconfig.getChromePath();
	public 		static final 	String			edgePath			= readconfig.getEdgePath();
	public 		static final 	String			logsFolder			= readconfig.getLogsFolder();
	public 		static final 	String			reportsFolder		= readconfig.getReportsFolder();
	public 		static final 	String			screenshotsFolder	= readconfig.getScreenshotsFolder();
}
