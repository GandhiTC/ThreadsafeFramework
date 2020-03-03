package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;



class ConfigReader
{
	private Properties properties;


	ConfigReader()
	{
		try
		{
			File 			src = new File("./Configuration/testing.properties");
			FileInputStream fis = new FileInputStream(src);
			
			properties = new Properties();
			properties.load(fis);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	String getApplicationURL()
	{
		return properties.getProperty("baseURL");
	}


	String getUsername()
	{
		return properties.getProperty("username");
	}


	String getPassword()
	{
		return properties.getProperty("password");
	}


	String getChromePath()
	{
		return properties.getProperty("chromepath");
	}


	String getIEPath()
	{
		return properties.getProperty("iepath");
	}


	String getFirefoxPath()
	{
		return properties.getProperty("firefoxpath");
	}
	
	
	String getEdgePath()
	{
		return properties.getProperty("edgepath");
	}
	
	
	String getLogsFolder()
	{
		return properties.getProperty("logsFolder");
	}
	
	
	String getReportsFolder()
	{
		return properties.getProperty("reportsFolder");
	}
	
	
	String getScreenshotsFolder()
	{
		return properties.getProperty("screenshotsFolder");
	}
}
