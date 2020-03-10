package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



//import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.Map;
//import java.util.Map.Entry;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;



public class DriverManager2 extends Configurations
{
	private 	static 			Capabilities 			capabilities;
	private		static			ThreadLocal<Object>		threadedDriver	= new ThreadLocal<>();
	private 	static			boolean					isRemote		= false;
	
	
	protected static void setupDriver(String browser, String runHeadless, String runService, String isGridTest)
	{
		isRemote 	=  isGridTest.equalsIgnoreCase("true") ? true : false;
		
		try
		{
			threadedDriver.set(selectedWebDriver(browser, runHeadless, runService, isGridTest));
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	protected static void removeDriver()
	{
		threadedDriver.remove();
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
	
	
	private static Object selectedWebDriver(String browser, String runHeadless, String runService, String isGridTest) throws MalformedURLException
	{
		if(isGridTest.equalsIgnoreCase("true"))
		{
			RemoteWebDriver rwb = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), getCapabilities(browser, runHeadless));
			rwb.setFileDetector(new LocalFileDetector());
			
			return rwb;
		}
		else
		{
			//	Firefox
			if(browser.equalsIgnoreCase("firefox"))
			{
				System.setProperty("webdriver.gecko.driver", firefoxPath);
				System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, System.getProperty("java.io.tmpdir") + "geckodriverlogs.txt");
				
				if(runService.equalsIgnoreCase("true"))
				{
					FirefoxBinary 		binary 					= new FirefoxBinary();
					if(runHeadless.equalsIgnoreCase("true")) { binary.addCommandLineOptions("-headless"); }
					Map<String, String>	environmentVariables	= new HashMap<>();
					GeckoDriverService	geckoDriverService		= new GeckoDriverService.Builder().usingFirefoxBinary(binary).withEnvironment(environmentVariables).build();
					return new FirefoxDriver(geckoDriverService, getFirefoxOptions(runHeadless));
				}
				else
				{
					return new FirefoxDriver(getFirefoxOptions(runHeadless));
				}
			}
			//	IE
			else if(browser.equalsIgnoreCase("ie"))
			{
				System.setProperty("webdriver.ie.driver", iePath);
				
				if(runService.equalsIgnoreCase("true"))
				{
					InternetExplorerDriverService	ieDriverService	= new InternetExplorerDriverService.Builder().withSilent(true).build();
					return new InternetExplorerDriver(ieDriverService, getIeOptions());
				}
				else
				{
					return new InternetExplorerDriver(getIeOptions());
				}
			}
			//	Edge
			else if(browser.equalsIgnoreCase("edge"))
			{
				System.setProperty("webdriver.edge.driver", edgePath);
				
				if(runService.equalsIgnoreCase("true"))
				{
					EdgeDriverService edgeDriverService = new EdgeDriverService.Builder().withLogFile(new File(System.getProperty("java.io.tmpdir") + "edgedriverlogs.txt")).build();
					return new EdgeDriver(edgeDriverService, getEdgeOptions());
				}
				else
				{
					return new EdgeDriver(getEdgeOptions());
				}
			}
			//	Chrome
			else
			{
				System.setProperty("webdriver.chrome.driver", chromePath);
				System.setProperty("webdriver.chrome.args", "--disable-logging");
				System.setProperty("webdriver.chrome.silentOutput", "true");
				
				if(runService.equalsIgnoreCase("true"))
				{
					ChromeDriverService cdService = new ChromeDriverService.Builder().usingAnyFreePort().build();
					return new ChromeDriver(cdService, getChromeOptions(runHeadless));
				}
				else
				{
					return new ChromeDriver(getChromeOptions(runHeadless));
				}
			}
		}
	}


	private static Capabilities getCapabilities(String browser, String runHeadless)
	{
		if(browser.equalsIgnoreCase("firefox"))
		{
			capabilities = getFirefoxOptions(runHeadless);
		}
		else if(browser.equalsIgnoreCase("ie"))
		{
			capabilities = getIeOptions();
		}
		else if(browser.equalsIgnoreCase("edge"))
		{
			capabilities = getEdgeOptions();
		}
		else
		{
			capabilities = getChromeOptions(runHeadless);
		}
		
		return capabilities;
	}
	
	
	private static FirefoxOptions getFirefoxOptions(String runHeadless)
	{
		FirefoxOptions	options	= new FirefoxOptions();
		FirefoxProfile	profile	= new FirefoxProfile();
		
		//	Accept Untrusted Certificates
//		profile.setAcceptUntrustedCertificates(true);
//		profile.setAssumeUntrustedCertificateIssuer(false);
		
		//	Use No Proxy Settings
//		profile.setPreference("network.proxy.type", 0);
		
		//	Set Firefox profile to capabilities
		options.setCapability(FirefoxDriver.PROFILE, profile);
		
		if(runHeadless.equalsIgnoreCase("true"))
		{
			options.setHeadless(true);
		}
		
		return options;
	}


	private static ChromeOptions getChromeOptions(String runHeadless)
	{
//		http://chromedriver.chromium.org/capabilities
		
		
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--silent");
//		chromeOptions.addArguments("--start-maximized");
//		chromeOptions.addArguments("--ignore-certificate-errors");
//		chromeOptions.addArguments("--disable-popup-blocking");
//		chromeOptions.addArguments("--incognito");
//		chromeOptions.addArguments("disable-notifications");
		
//		chromeOptions.addExtensions(new File("/path/to/extension.crx"));
		
//		Map<String, Object> prefs = new HashMap<String, Object>();
//		prefs.put("profile.default_content_settings.popups", 0);
//		chromeOptions.setExperimentalOption("prefs", prefs);
		
//		DesiredCapabilities capabilities=DesiredCapabilities.chrome();
//		capabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
//		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
//		chromeOptions.merge(capabilities);
		
//		chromeOptions.setCapability("browserVersion", "67");
//		chromeOptions.setCapability("platformName", "Windows XP");
		
		if(runHeadless.equalsIgnoreCase("true"))
		{
			chromeOptions.setHeadless(true);
		}
		
		return chromeOptions;
	}
	
	
	private static InternetExplorerOptions getIeOptions()
	{
//		InternetExplorerOptions ieOptions = new InternetExplorerOptions().destructivelyEnsureCleanSession();
		InternetExplorerOptions ieOptions = new InternetExplorerOptions();
		
//		DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
//		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
//		ieOptions.merge(capabilities);
		
		return ieOptions;
	}
	
	
	private static EdgeOptions getEdgeOptions()
	{
		EdgeOptions edgeOptions = new EdgeOptions();
		edgeOptions.setCapability("useAutomationExtension", false);
		
		return edgeOptions;
	}
	
	
	
	
	protected static void setWindowSize(int width, int height)
	{
		driver().manage().window().setSize(new Dimension(width, height));
	}
	
	
	protected static void setWindowPosition(int posX, int posY)
	{
		driver().manage().window().setPosition(new Point(posX, posY));
	}
	
	
	protected static void getURL(String uRL, boolean waitForPageToLoad)
	{
		driver().get(uRL);
		
		if(waitForPageToLoad)
		{
			waitForPageToLoad();
		}
	}
	
	
	protected static void navigateURL(String uRL, boolean waitForPageToLoad)
	{
		driver().navigate().to(uRL);
		
		if(waitForPageToLoad)
		{
			waitForPageToLoad();
		}
	}
	
	
	protected static void navigateBack(String uRL, boolean waitForPageToLoad)
	{
		driver().navigate().back();
		
		if(waitForPageToLoad)
		{
			waitForPageToLoad();
		}
	}
	
	
	protected static void navigateForward(String uRL, boolean waitForPageToLoad)
	{
		driver().navigate().forward();
		
		if(waitForPageToLoad)
		{
			waitForPageToLoad();
		}
	}
	
	
	protected static void navigateRefresh(String uRL, boolean waitForPageToLoad)
	{
		driver().navigate().refresh();
		
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
	
	
	protected static void getAttributes(WebElement element)
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
		System.out.println("---------------------------------------");
		System.out.println("isDisplayed = " + element.isDisplayed());
		System.out.println("isEnabled   = " + element.isEnabled());
		System.out.println("isSelected  = " + element.isSelected());
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
}
