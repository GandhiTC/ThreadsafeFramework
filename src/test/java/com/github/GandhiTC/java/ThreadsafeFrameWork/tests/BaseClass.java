package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.JDBCDriver;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.DriverManager;



public class BaseClass extends DriverManager
{
	protected	static 			Logger					logger;
	protected	static			SoftAssert				softAssert		= new SoftAssert();
	private		static			ThreadLocal<JDBCDriver>	threadedDB		= new ThreadLocal<>();
	private 	static 	final 	JDBCDriver 				dbInstance		= JDBCDriver.INSTANCE;
	protected	static 			String					baseURL			= "";
	protected 	static 		 	String					username		= "";
	protected	static 		 	String					password		= "";


	@BeforeTest
	@Parameters(value = {"browser", "runHeadless", "runService", "isGridTest"})
	public static void setup(String browser, String runHeadless, String runService, String isGridTest, ITestContext testContext)
	{
		setupLogging();
		
		threadedDB.set(dbInstance);
		getCredentialsFromDatabase();
		
		setupDriver(browser, runHeadless, runService, isGridTest);
		testContext.setAttribute("BaseLogger", logger);
		testContext.setAttribute("WebDriver", driver());
		testContext.setAttribute("ScreenshotFolder", screenshotsFolder);
		testContext.setAttribute("RunningHeadless", false);
		
		if(runHeadless.equalsIgnoreCase("true"))
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
		db().closeConnection();
		
		if(driver() != null)
		{
			driver().quit();
		}
		
		removeDriver();
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
	
	
	public static Logger logger()
	{
		if(logger == null)
		{
			setupLogging();
		}
		
		return logger;
	}
	
	
	protected static JDBCDriver db()
	{
		return threadedDB.get();
	}
	
	
	protected static void getCredentialsFromDatabase()
	{
		if(baseURL.equalsIgnoreCase("") || username.equalsIgnoreCase("") || password.equalsIgnoreCase(""))
		{
			try
			{
				//	Check if "pomCredentials" exists in the database.  If it does not, add it.
				if(!db().checkIfTableExists("pomCredentials"))
				{
					System.out.println("\r\nCreating \"pomCredentials\" table in database.\r\n");

					db().parseSqlFile("src/test/resources/InsertTestTable.sql", false, true, false, false);
				}


				ResultSet resultSet = db().query("select * from pomCredentials");

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
				db().closeConnection();
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
