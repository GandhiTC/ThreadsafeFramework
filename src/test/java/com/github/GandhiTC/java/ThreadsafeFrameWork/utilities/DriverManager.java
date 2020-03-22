package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.Point;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;



public abstract class DriverManager extends Configurations
{
	private 		 			Capabilities 					capabilities;
	private						ThreadLocal<Object>				threadedDriver		= new ThreadLocal<>();
	private 					boolean							runHeadless			= false;
	private 					boolean							isGridTest			= false;
	private						GeckoDriverService				geckoDriverService	= null;
	private						InternetExplorerDriverService	ieDriverService		= null;
	private						EdgeDriverService 				edgeDriverService	= null;
	private						ChromeDriverService 			chromeDriverService	= null;
	
	protected	static			SoftAssert						softAssert			= new SoftAssert();
	protected					WebDriver						driver;
	
	
	
	
	//	Start of base code
	
	protected void setupDriver(String browser, boolean runHeadless, boolean runService, boolean isGridTest)
	{
		this.runHeadless = runHeadless;
		this.isGridTest  = isGridTest;
		
		try
		{
			threadedDriver.set(selectedWebDriver(browser, runService, isGridTest));
			driver = driver();
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	private Object selectedWebDriver(String browser, boolean runService, boolean isGridTest) throws MalformedURLException
	{
		if(isGridTest)
		{
			RemoteWebDriver rwb = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), getCapabilities(browser));
			rwb.setFileDetector(new LocalFileDetector());
			
			return rwb;
		}
		else
		{
			
			//	*** FIREFOX ***
			
			if(browser.equalsIgnoreCase("firefox"))
			{
				System.setProperty("webdriver.gecko.driver", firefoxPath);
				System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, System.getProperty("java.io.tmpdir") + "geckodriverlogs.txt");
				
				if(runService)
				{
					if(geckoDriverService == null)
					{
						return getAsGeckoDriverService();
					}
					else
					{
						return geckoDriverService;
					}
				}
				else
				{
					return new FirefoxDriver(getFirefoxOptions());
				}
			}
			
			//	*** IE ***
			
			else if(browser.equalsIgnoreCase("ie"))
			{
				System.setProperty("webdriver.ie.driver", iePath);
				
				if(runService)
				{
					if(ieDriverService == null)
					{
						return getAsInternetExplorerDriverService();
					}
					else
					{
						return ieDriverService;
					}
				}
				else
				{
					return new InternetExplorerDriver(getIeOptions());
				}
			}
			
			//	*** EDGE ***
			
			else if(browser.equalsIgnoreCase("edge"))
			{
				System.setProperty("webdriver.edge.driver", edgePath);
				
				if(runService)
				{
					if(edgeDriverService == null)
					{
						return getAsEdgeDriverService();
					}
					else
					{
						return edgeDriverService;
					}
				}
				else
				{
					return new EdgeDriver(getEdgeOptions());
				}
			}
			
			//	*** ELSE CHROME ***
			
			else
			{
				System.setProperty("webdriver.chrome.driver", chromePath);
				System.setProperty("webdriver.chrome.args", "--disable-logging");
				System.setProperty("webdriver.chrome.silentOutput", "true");
				
				if(runService)
				{
					if(chromeDriverService == null)
					{
						return getAsChromeDriverService();
					}
					else
					{
						return chromeDriverService;
					}
				}
				else
				{
					return new ChromeDriver(getChromeOptions());
				}
			}
		}
	}
	
	
	private WebDriver driver()
	{
		if(isGridTest)
		{
			return (RemoteWebDriver)threadedDriver.get();
		}
		else
		{
			return (WebDriver)threadedDriver.get();
		}
	}
	
	
	protected void removeDriver()
	{
		threadedDriver.remove();
		
		if(driver != null)
		{
			driver.quit();
			driver = null;
		}
	}

	//	End of base code

	
	
	
	//	Start of capabilities & options
	
	private Capabilities getCapabilities(String browser)
	{
		if(browser.equalsIgnoreCase("firefox"))
		{
			capabilities = getFirefoxOptions();
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
			capabilities = getChromeOptions();
		}
		
		return capabilities;
	}
	
	
	private FirefoxOptions getFirefoxOptions()
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
		options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
		
		if(runHeadless)
		{
			options.setHeadless(true);
		}
		
		return options;
	}


	private ChromeOptions getChromeOptions()
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
		
		if(runHeadless)
		{
			chromeOptions.setHeadless(true);
		}
		
		return chromeOptions;
	}
	
	
	private InternetExplorerOptions getIeOptions()
	{
//		InternetExplorerOptions ieOptions = new InternetExplorerOptions().destructivelyEnsureCleanSession();
		InternetExplorerOptions ieOptions = new InternetExplorerOptions();
		
//		DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
//		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
//		ieOptions.merge(capabilities);
		
		return ieOptions;
	}
	
	
	private EdgeOptions getEdgeOptions()
	{
		EdgeOptions edgeOptions = new EdgeOptions();
		edgeOptions.setCapability("useAutomationExtension", false);
		
		return edgeOptions;
	}
	
	//	End of capabilities & options
	
	
	
	
	//	Start of driver services
	
	private FirefoxDriver getAsGeckoDriverService()
	{
			FirefoxBinary 		binary 					= new FirefoxBinary();
			if(runHeadless) 							  { binary.addCommandLineOptions("-headless"); }
			Map<String, String>	environmentVariables	= new HashMap<>();
								geckoDriverService		= new GeckoDriverService.Builder().usingFirefoxBinary(binary).withEnvironment(environmentVariables).build();
			
			try
			{
				geckoDriverService.start();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			return new FirefoxDriver(geckoDriverService, getFirefoxOptions());
	}
	
	
	private InternetExplorerDriver getAsInternetExplorerDriverService()
	{
		ieDriverService	= new InternetExplorerDriverService.Builder().withSilent(true).build();
		
		try
		{
			ieDriverService.start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return new InternetExplorerDriver(ieDriverService, getIeOptions());
	}
	
	
	private EdgeDriver getAsEdgeDriverService()
	{
		edgeDriverService = new EdgeDriverService.Builder().withLogFile(new File(System.getProperty("java.io.tmpdir") + "edgedriverlogs.txt")).build();
		
		try
		{
			edgeDriverService.start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return new EdgeDriver(edgeDriverService, getEdgeOptions());
	}
	
	
	private ChromeDriver getAsChromeDriverService()
	{
		chromeDriverService = new ChromeDriverService.Builder().usingAnyFreePort().build();
		
		try
		{
			chromeDriverService.start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return new ChromeDriver(chromeDriverService, getChromeOptions());
	}
	
	
	protected void stopService()
	{
		if(geckoDriverService != null)
		{
			if(geckoDriverService.isRunning())
			{
				geckoDriverService.stop();
			}
		}
		
		if(ieDriverService != null)
		{
			if(ieDriverService.isRunning())
			{
				ieDriverService.stop();
			}
		}
		
		if(edgeDriverService != null)
		{
			if(edgeDriverService.isRunning())
			{
				edgeDriverService.stop();
			}
		}
		
		if(chromeDriverService != null)
		{
			if(chromeDriverService.isRunning())
			{
				chromeDriverService.stop();
			}
		}
	}
	
	//	End of driver services
	
	
	
	
	//	Start of  driver methods
	
	public static void sleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static void maximizeWindow(WebDriver driver)
	{
		driver.manage().window().maximize();
	}
	
	
	public static void minimizeWindow(WebDriver driver)
	{
		try
		{
			((JavascriptExecutor)driver).executeScript("window.focus();");
			
			Robot robot = new Robot();
			
			robot.keyPress(KeyEvent.VK_WINDOWS);
			
			robot.keyPress(KeyEvent.VK_DOWN);
			robot.keyRelease(KeyEvent.VK_DOWN);
			
			try
			{
				Thread.sleep(300L);
			}
			catch(InterruptedException e)
			{
				System.out.println(e.getMessage());
			}
			
			//	press down arrow twice just in case
			//	original window state was maximized
			robot.keyPress(KeyEvent.VK_DOWN);
			robot.keyRelease(KeyEvent.VK_DOWN);
			
			robot.keyRelease(KeyEvent.VK_WINDOWS);
		}
		catch(AWTException e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	
	public static Dimension getWindowSize(WebDriver driver)
	{
		return driver.manage().window().getSize();
	}
	
	
	public static Point getWindowPosition(WebDriver driver)
	{
		return driver.manage().window().getPosition();
	}
	
	
	public static void setWindowSize(WebDriver driver, int width, int height)
	{
		driver.manage().window().setSize(new Dimension(width, height));
	}
	
	
	public static void setWindowSize(WebDriver driver, Dimension dimension)
	{
		driver.manage().window().setSize(dimension);
	}
	
	
	public static void setWindowPosition(WebDriver driver, int posX, int posY)
	{
		driver.manage().window().setPosition(new Point(posX, posY));
	}
	
	
	public static void setWindowPosition(WebDriver driver, Point point)
	{
		driver.manage().window().setPosition(point);
	}
	
	
	public static void bringToFront(WebDriver driver, boolean defaultDismissAlert)
	{
		//	If an alert is already present, capture it's text
		String alertText = "";
		
		try
		{
			Alert alert = driver.switchTo().alert();
			alertText   = alert.getText();
			
			if(defaultDismissAlert)
			{
				alert.dismiss();
			}
			else
			{
				alert.accept();
			}
		}
		catch(NoAlertPresentException e)
		{
		}
		
		String 		origHandle	= driver.getWindowHandle();
		Dimension 	origSize 	= getWindowSize(driver);
		Point		origPos		= getWindowPosition(driver);
		
		minimizeWindow(driver);
		
		sleep(300L);
		
		//	Do a little bit of everything to make it more universally compatible
		maximizeWindow(driver);
		
		switchByHandle(driver, origHandle);
		
		((JavascriptExecutor) driver).executeScript("window.focus();");
		setWindowSize(driver, origSize);
		setWindowPosition(driver, origPos);
		
//		((JavascriptExecutor) driver).executeScript("alert('" + alertText + "')");
//		acceptAlert(driver);
		
		//	If an alert was present when we first started
		//	bring it back up again
		if(!alertText.isEmpty())
		{
			((JavascriptExecutor) driver).executeScript("alert('" + alertText + "')");
		}
	}
	
	
	public static void getURL(WebDriver driver, String uRL, boolean waitForPageToLoad)
	{
		driver.get(uRL);
		
		if(waitForPageToLoad)
		{
			waitForPageToLoad(driver);
		}
	}
	
	
	public static void navigateURL(WebDriver driver, String uRL, boolean waitForPageToLoad)
	{
		driver.navigate().to(uRL);
		
		if(waitForPageToLoad)
		{
			waitForPageToLoad(driver);
		}
	}
	
	
	public static void navigateBack(WebDriver driver, String uRL, boolean waitForPageToLoad)
	{
		driver.navigate().back();
		
		if(waitForPageToLoad)
		{
			waitForPageToLoad(driver);
		}
	}
	
	
	public static void navigateForward(WebDriver driver, String uRL, boolean waitForPageToLoad)
	{
		driver.navigate().forward();
		
		if(waitForPageToLoad)
		{
			waitForPageToLoad(driver);
		}
	}
	
	
	public static void navigateRefresh(WebDriver driver, String uRL, boolean waitForPageToLoad)
	{
		driver.navigate().refresh();
		
		if(waitForPageToLoad)
		{
			waitForPageToLoad(driver);
		}
	}
	
	
	public static void waitForPageToLoad(WebDriver driver)
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
										
		wait						= 	new FluentWait<WebDriver>(driver)
											.withTimeout(Duration.ofSeconds(30))
											.pollingEvery(Duration.ofSeconds(1));
		
		wait.until(pageLoadCondition);
	}
	
	
	public static void waitForAlert(WebDriver driver, long seconds, boolean okToFail)
	{
		if(okToFail)
		{
			try
			{
				WebDriverWait wait = new WebDriverWait(driver, seconds);
				wait.until(ExpectedConditions.alertIsPresent());
			}
			catch(NoAlertPresentException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			int t = 0;
			
			while(!alertIsPresent(driver) && (t < seconds))
			{
				sleep(1000L);
				t++;
			}
		}
	}
	
	
	public static boolean alertIsPresent(WebDriver driver)
	{
		try
		{
			driver.switchTo().alert();
			return true;
		}
		catch (NoAlertPresentException e)
		{
			return false;
		}
	}
	
	
	public static void acceptAlert(WebDriver driver)
	{
		try
		{
			driver.switchTo().alert().accept();
		}
		catch(Exception e)
		{
		}
	}
	
	
	public static void dismissAlert(WebDriver driver)
	{
		try
		{
			driver.switchTo().alert().dismiss();
		}
		catch(Exception e)
		{
		}
	}
	
	
	public static String alertText(WebDriver driver)
	{
		try
		{
			return driver.switchTo().alert().getText();
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	
	public static boolean elementExists(WebDriver driver, By by)
	{
		try
		{
			driver.findElement(by);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	
	public static void printAttributes(WebDriver driver, WebElement element)
	{
		String							script		= 	"var items = {}; \r\n" +
														"for (index = 0; index < arguments[0].attributes.length; ++index)\r\n" +
														"{ \r\n" +
														"items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value\r\n" +
														"}; \r\n" +
														"return items;";
		Object							mapObj		= 	((JavascriptExecutor)driver).executeScript(script, element);
		
		
		
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
	}
	
	
	public static Map<String, String> getAttributesMap(WebDriver driver, WebElement element, boolean printToConsole)
	{
		String							script		= 	"var items = {}; \r\n" +
														"for (index = 0; index < arguments[0].attributes.length; ++index)\r\n" +
														"{ \r\n" +
														"items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value\r\n" +
														"}; \r\n" +
														"return items;";
		Object							mapObj		= 	((JavascriptExecutor)driver).executeScript(script, element);
		ObjectMapper 					oMapper 	= 	new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, String> 			map			= 	oMapper.convertValue(mapObj, Map.class);
		
		map.put("tag", element.getTagName());
		map.put("width", Integer.toString(element.getSize().getWidth()));
		map.put("height", Integer.toString(element.getSize().getHeight()));
		map.put("x", Integer.toString(element.getLocation().getX()));
		map.put("y", Integer.toString(element.getLocation().getY()));
		map.put("isDisplayed", Boolean.toString(element.isDisplayed()));
		map.put("isEnabled", Boolean.toString(element.isEnabled()));
		map.put("isSelected", Boolean.toString(element.isSelected()));
		
		if(!element.getText().equals(null) && !element.getText().isEmpty())
		{
			map.put("text", element.getText().trim());
		}
		
		
		if(printToConsole)
		{
			System.out.println(" ");
			
			Iterator<Entry<String, String>>	iterator	= 	map.entrySet().iterator();
	
			while(iterator.hasNext())
			{
				Entry<String, String> mEntry = iterator.next();
				System.out.println(mEntry.getKey() + " = " + mEntry.getValue());
			}
	
			System.out.println(" ");
		}

		
		return map;
	}
	
	
	public static void setAttribute(WebDriver driver, WebElement element, String attributeName, String attributeValue)
	{
		((JavascriptExecutor)driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
														element, attributeName, attributeValue);
	}
	
	
	public static String userAgentInfo(WebDriver driver)
	{
		return ((JavascriptExecutor)driver).executeScript("return navigator.userAgent;").toString();
	}
	
	
	public static void openLinkInNewTab(WebElement linkElement)
	{
		linkElement.sendKeys(Keys.chord(Keys.CONTROL, Keys.RETURN));
	}
	
	
	public static void openLinkInNewWindow(WebElement linkElement)
	{
		linkElement.sendKeys(Keys.chord(Keys.SHIFT, Keys.RETURN));
	}
	
	
	public static void openNewTab(WebDriver driver, boolean switchToNewTab)
	{
		bringToFront(driver, true);
		
		String			origHandle	= driver.getWindowHandle();
		
		try
		{
			Robot 		robot 		= new Robot();
			
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_N);
			
			robot.keyRelease(KeyEvent.VK_N);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		}
		catch(AWTException e)
		{
			System.err.println(e.getMessage());
			return;
		}
		
		//	convert Set<String> to List<String>
		List<String> 	handles 	= new ArrayList<>(driver.getWindowHandles());
		String			lastHandle	= handles.get(handles.size() - 1);
		
		if(switchToNewTab)
		{
			switchByHandle(driver, lastHandle);
		}
		else
		{
			switchByHandle(driver, origHandle);
		}
	}
	
	
	public static void openInNewTab(WebDriver driver, WebElement linkElement, boolean switchToNewTab)
	{
		bringToFront(driver, true);
		
		String			origHandle	= driver.getWindowHandle();
		
		linkElement.sendKeys(Keys.chord(Keys.CONTROL, Keys.RETURN));
		
		//	convert Set<String> to List<String>
		List<String> 	handles 	= new ArrayList<>(driver.getWindowHandles());
		String			lastHandle	= handles.get(handles.size() - 1);
		
		if(switchToNewTab)
		{
			switchByHandle(driver, lastHandle);
		}
		else
		{
			switchByHandle(driver, origHandle);
		}
	}
	
	
	public static void openNewWindow(WebDriver driver, boolean switchToNewWindow)
	{
		bringToFront(driver, true);
		
		String			origHandle	= driver.getWindowHandle();
		
		try
		{
			Robot 		robot 		= new Robot();
			
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_N);
			
			robot.keyRelease(KeyEvent.VK_N);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		}
		catch(AWTException e)
		{
			System.err.println(e.getMessage());
			return;
		}
		
		//	convert Set<String> to List<String>
		List<String> 	handles 	= new ArrayList<>(driver.getWindowHandles());
		String			lastHandle	= handles.get(handles.size() - 1);
		
		if(switchToNewWindow)
		{
			switchByHandle(driver, lastHandle);
		}
		else
		{
			switchByHandle(driver, origHandle);
		}
	}
	
	
	public static void openInNewWindow(WebDriver driver, WebElement linkElement, boolean switchToNewWindow)
	{
		bringToFront(driver, true);
		
		String			origHandle	= driver.getWindowHandle();
		
		linkElement.sendKeys(Keys.chord(Keys.SHIFT, Keys.RETURN));
		
		//	convert Set<String> to List<String>
		List<String> 	handles 	= new ArrayList<>(driver.getWindowHandles());
		String			lastHandle	= handles.get(handles.size() - 1);
		
		if(switchToNewWindow)
		{
			switchByHandle(driver, lastHandle);
		}
		else
		{
			switchByHandle(driver, origHandle);
		}
	}
	
	
	public static void switchByHandle(WebDriver driver, String nameOrHandle)
	{
		driver.switchTo().window(nameOrHandle);
	}
	
	
	public static void switchByTitle(WebDriver driver, String title)
	{
		for(String handle : driver.getWindowHandles())
		{
			driver.switchTo().window(handle);
			
			if(driver.getTitle().equals(title))
			{
				return;
			}
		}
	}
	
	//	End of driver methods
}
