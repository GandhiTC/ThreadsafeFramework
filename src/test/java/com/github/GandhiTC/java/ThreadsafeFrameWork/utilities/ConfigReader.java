package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



import java.io.FileReader;
import java.util.Properties;



class ConfigReader
{
	private Properties properties;


	ConfigReader()
	{
		try
		{
			FileReader 		reader 	= new FileReader("./Configuration/testing.properties");
			
			properties = new Properties();
			properties.load(reader);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	String valueInQuotes(String propertyName)
	{
		return properties.getProperty(propertyName).replaceAll("\"", "");
	}
	
	
	String getApplicationURL()
	{
		return valueInQuotes("baseURL");
	}


	String getUsername()
	{
		return valueInQuotes("username");
	}


	String getPassword()
	{
		return valueInQuotes("password");
	}


	String getChromePath()
	{
		return valueInQuotes("chromepath");
	}


	String getIEPath()
	{
		return valueInQuotes("iepath");
	}


	String getFirefoxPath()
	{
		return valueInQuotes("firefoxpath");
	}
	
	
	String getEdgePath()
	{
		return valueInQuotes("edgepath");
	}
	
	
	String getLogsFolder()
	{
		return valueInQuotes("logsFolder");
	}
	
	
	String getReportsFolder()
	{
		return valueInQuotes("reportsFolder");
	}
}
