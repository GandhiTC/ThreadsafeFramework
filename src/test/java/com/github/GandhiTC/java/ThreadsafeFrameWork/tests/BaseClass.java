package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.JDBCDriver;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.Configurations;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.DriverManager;



public class BaseClass extends Configurations
{
	private		static			ThreadLocal<Object>	threadedDriver	= new ThreadLocal<>();
	private		static			DriverManager		driverManager	= new DriverManager();
	private 	static			boolean				isRemote		= false;
	private 	static			boolean				isHeadless		= false;
	protected	static 			Logger				logger;
	protected 	static 	final 	JDBCDriver 			db 				= JDBCDriver.INSTANCE;
	protected	static 			String				baseURL			= "";
	protected 	static 		 	String				username		= "";
	protected	static 		 	String				password		= "";


	@BeforeTest
	@Parameters(value = {"browser", "runHeadless", "runService", "isGridTest"})
	public static void setup(String browser, String runHeadless, String runService, String isGridTest, ITestContext testContext) throws MalformedURLException
	{
		setupLogging();
		getCredentials();
		
		isRemote 	= isGridTest.equalsIgnoreCase("true") ? true : false;
		isHeadless 	= runHeadless.equalsIgnoreCase("true") ? true : false;
		
		threadedDriver.set(driverManager.selectedWebDriver(browser, runHeadless, runService, isGridTest));
		testContext.setAttribute("BaseLogger", logger);
		testContext.setAttribute("WebDriver", driver());
		testContext.setAttribute("ScreenshotFolder", screenshotsFolder);
		testContext.setAttribute("RunningHeadless", false);
		
		if(isHeadless == true)
		{
			if((browser.equalsIgnoreCase("firefox")) || (browser.equalsIgnoreCase("chrome")))
			{
				testContext.setAttribute("RunningHeadless", true);
			}
		}
	}
	
	
	@AfterTest
	public static void tearDown()
	{
		db.closeConnection();
		
		if(driver() != null)
		{
			driver().quit();
		}
		
		threadedDriver.remove();
	}
	
	
	private static void setupLogging()
	{
		//	because this property is needed in log4j2.xml (log4j's config file)
		//	make sure it is set before initializing the logger
		System.setProperty("logsFolder", logsFolder);
		
		logger = LogManager.getLogger(BaseClass.class.getPackage().getName());
		
		//	disabling specific loggers must come after initializing log4j logger
		java.util.logging.Logger.getLogger("org.openqa.selenium.remote").setLevel(Level.OFF);
	}


	public static WebDriver driver()
	{
		if(isRemote == true)
		{
			return (RemoteWebDriver)threadedDriver.get();
		}
		else
		{
			return (WebDriver)threadedDriver.get();
		}
	}
	
	
	public static Logger logger()
	{
		if(logger == null)
		{
			setupLogging();
		}
		
		return logger;
	}
	
	
	protected static void getCredentials()
	{
		if(baseURL.equalsIgnoreCase("") || username.equalsIgnoreCase("") || password.equalsIgnoreCase(""))
		{
			try
			{
				//	Check if "pomCredentials" exists in the database.  If it does not, add it.
				if(!db.checkIfTableExists("pomCredentials"))
				{
					System.out.println("\r\nCreating \"pomCredentials\" table in database.\r\n");
					
					db.parseSqlFile("src/test/resources/InsertTestTable.sql", false, true, false, false);
				}
				
				
				ResultSet resultSet = db.query("select * from pomCredentials");
				
				while (resultSet.next())
				{
					baseURL  = resultSet.getString("baseURL");
					username = resultSet.getString("username");
					password = resultSet.getString("password");
				}
			}
			catch(SQLException e)
			{
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			finally
			{
				db.closeConnection();
			}
		}
	}


	public String randomString(int numOfChars)
	{
		return RandomStringUtils.randomAlphabetic(numOfChars);
	}


	public int randomNumber(int numOfChars)
	{
		return Integer.parseInt(RandomStringUtils.randomNumeric(numOfChars));
	}
}