package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;



public class ConfigReader
{
	private Properties properties;


	public ConfigReader()
	{
		try
		{
			File 			src = new File("./Configuration/config.properties");
			FileInputStream fis = new FileInputStream(src);
			
			properties = new Properties();
			properties.load(fis);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public String getApplicationURL()
	{
		return properties.getProperty("baseURL");
	}


	public String getUsername()
	{
		return properties.getProperty("username");
	}


	public String getPassword()
	{
		return properties.getProperty("password");
	}


	public String getChromePath()
	{
		return properties.getProperty("chromepath");
	}


	public String getIEPath()
	{
		return properties.getProperty("iepath");
	}


	public String getFirefoxPath()
	{
		return properties.getProperty("firefoxpath");
	}
	
	
	public String getEdgePath()
	{
		return properties.getProperty("edgepath");
	}
	
	
	public String getLogsFolder()
	{
		return properties.getProperty("logsFolder");
	}
	
	
	public String getReportsFolder()
	{
		return properties.getProperty("reportsFolder");
	}
	
	
	public String getScreenshotsFolder()
	{
		return properties.getProperty("screenshotsFolder");
	}
}
