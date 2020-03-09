package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Map.Entry;
import java.util.logging.Level;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
//import com.fasterxml.jackson.databind.ObjectMapper;
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


	protected static WebDriver driver()
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
	
	
	protected static Logger logger()
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
	
	
	protected static void getURL(String uRL, boolean waitForPageToLoad)
	{
		driver().get(uRL);
		
		if(waitForPageToLoad)
		{
			waitForPageToLoad();
		}
	}
	
	
	protected static void waitForPageToLoad()
	{
		ExpectedCondition<Boolean>		pageLoadCondition;
		Wait<WebDriver>					wait;
		
		pageLoadCondition			= 	new ExpectedCondition<Boolean>()
										{
											@Override
											public Boolean apply(WebDriver driver)
											{
												return ((JavascriptExecutor)driver)
														.executeScript("return document.readyState")
														.equals("complete");
											}
										};
										
		wait						= 	new FluentWait<WebDriver>(driver())
											.withTimeout(Duration.ofSeconds(30))
											.pollingEvery(Duration.ofSeconds(1));
		
		wait.until(pageLoadCondition);
	}
	
	
	protected static boolean elementExists(By by)
	{
		try
		{
			driver().findElement(by);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	
	protected static void inspectElement(WebElement element)
	{
		String							script		= 	"var items = {}; \r\n" +
														"for (index = 0; index < arguments[0].attributes.length; ++index)\r\n" +
														"{ \r\n" +
														"items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value\r\n" +
														"}; \r\n" +
														"return items;";
		JavascriptExecutor 				executor	= 	(JavascriptExecutor) driver();
		Object							mapObj		= 	executor.executeScript(script, element);
		
		
		
		//	Option 1 - a simple way to just print the results
		System.out.println(" ");
		System.out.println("tag = " + element.getTagName());
		System.out.println("---------------------------------------");
		System.out.println(mapObj.toString().replace("{", "").replace("}", "").replace("=", " = ").replace(", ", "\r\n"));
		System.out.println("---------------------------------------");
		if(!element.getText().equals(null) && !element.getText().isEmpty())
		{
			System.out.println("text = " + element.getText().trim());
			System.out.println("---------------------------------------");
		}
		System.out.println("width  = " + element.getSize().getWidth());
		System.out.println("height = " + element.getSize().getHeight());
		System.out.println("---------------------------------------");
		System.out.println("X = " + element.getLocation().getX());
		System.out.println("Y = " + element.getLocation().getY());
		System.out.println(" ");
		
		
		
		//	Options 2 - if you'd rather return a Map<String, String>
//		ObjectMapper 					oMapper 	= 	new ObjectMapper();
//		Map<String, String> 			map			= 	oMapper.convertValue(mapObj, Map.class);
//		Iterator<Entry<String, String>>	iterator	= 	map.entrySet().iterator();
//
////		while(iterator.hasNext())
////		{
////			Entry<String, String> mEntry = iterator.next();
////			System.out.println(mEntry.getKey() + " = " + mEntry.getValue());
////		}
//
//		return map;
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