package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.Capabilities;
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



public class DriverManager extends Configurations
{
	private Capabilities capabilities;
	
	
	public Object selectedWebDriver(String browser, String runHeadless, String runService, String isGridTest) throws MalformedURLException
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


	private Capabilities getCapabilities(String browser, String runHeadless)
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
	
	
	private FirefoxOptions getFirefoxOptions(String runHeadless)
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


	private ChromeOptions getChromeOptions(String runHeadless)
	{
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--silent");
//		chromeOptions.addArguments("--start-maximized");
//		chromeOptions.addArguments("--ignore-certificate-errors");
//		chromeOptions.addArguments("--disable-popup-blocking");
//		chromeOptions.addArguments("--incognito");
		
//		chromeOptions.setCapability("browserVersion", "67");
//		chromeOptions.setCapability("platformName", "Windows XP");
		
		if(runHeadless.equalsIgnoreCase("true"))
		{
			chromeOptions.setHeadless(true);
		}
		
		return chromeOptions;
	}
	
	
	private InternetExplorerOptions getIeOptions()
	{
//		InternetExplorerOptions ieOptions = new InternetExplorerOptions().destructivelyEnsureCleanSession();
		InternetExplorerOptions ieOptions = new InternetExplorerOptions();
		
		return ieOptions;
	}
	
	
	private EdgeOptions getEdgeOptions()
	{
		EdgeOptions edgeOptions = new EdgeOptions();
		edgeOptions.setCapability("useAutomationExtension", false);
		
		return edgeOptions;
	}
}
