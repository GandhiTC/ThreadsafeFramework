package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;



public class ExtentManager
{
	private static ExtentReports 	extent;
	public 	static String			screenshotPath		= null;
	public 	static String			screenshotName		= null;
	public 	static String			base64ImageString	= null;
	public	static String			reportFile			= null;


	public static ExtentReports createInstance()
	{
		String 	css	  			= 	"details {\r\n" +
									"  font: \"Open Sans\", Calibri, sans-serif;\r\n" +
									"  width: 100%;\r\n" +
									"}\r\n" +
									"\r\n" +
									"details > summary {\r\n" +
									"  color: white;\r\n" +
									"  padding: 2px 6px;\r\n" +
									"  width: 18em;\r\n" +
									"  background-color: DarkRed;\r\n" +
									"  border: none;\r\n" +
									"  box-shadow: 3px 3px 4px black;\r\n" +
									"  cursor: pointer;\r\n" +
									"  font-size: 1.25em;\r\n" +
									"  font-weight: bold;\r\n" +
									"  display: list-item;\r\n" +
									"  border-radius: 5px 5px 0 0;\r\n" +
									"}\r\n" +
									"\r\n" +
									"details > p {\r\n" +
									"  border-radius: 0 0 10px 10px;\r\n" +
									"  background-color: LightSlateGray;\r\n" +
									"  padding: 2px 6px;\r\n" +
									"  margin: 0;\r\n" +
									"  box-shadow: 3px 3px 4px black;\r\n" +
									"  font-weight: bold;" +
									"}\r\n" +
									"\r\n" +
									".tabbed {\r\n" +
									"	display:inline-block;\r\n" +
									"    width:30px;\r\n" +
									"}";
		
		String 	userDir 		= 	System.getProperty("user.dir");
		Path 	userDirPath 	= 	Paths.get(userDir);
		String 	projectName		= 	userDirPath.getFileName().toString();
		String	reportHeader	= 	"Test Results for Project: " + projectName;
		
		Date	currentDate		= 	new Date();
		
		String	yearFolder		= 	new SimpleDateFormat("yyyy").format(currentDate);
		String	monthFolder		= 	new SimpleDateFormat("MMM").format(currentDate);
		String	dayFolder		= 	new SimpleDateFormat("dd-MMM-yyyy").format(currentDate);
		String	dateFolders		= 	yearFolder + "\\" + monthFolder + "\\" + dayFolder;
		
		String 	timeString		= 	new SimpleDateFormat("hh_mm_ss_aa_z").format(currentDate);
		String	fileName		= 	timeString + "___Test-Report.html";
		
		String	filePath		= 	userDir + "\\" + Configurations.reportsFolder + "\\" + dateFolders + "\\" + fileName;
		
				reportFile		=	filePath;
		
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(filePath);
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setCSS(css);
		htmlReporter.config().setTheme(Theme.DARK);
		htmlReporter.config().setDocumentTitle(reportHeader);
		htmlReporter.config().setReportName(reportHeader);
		
		//	as an alternative to using htmlReporter.config() code lines, the extent-config.xml file could be loaded instead
//		htmlReporter.loadXMLConfig(userDir + "/Configuration/extent-config.xml");
		
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setSystemInfo("Orgainzation", "com.github.GandhiTC");
		extent.setSystemInfo("Host", "localhost");
		extent.setSystemInfo("Environment", "QA");
		extent.setSystemInfo("Tester", "Tejas Gandhi");
		extent.setSystemInfo("Build No", "TG00-000x");
		
		return extent;
	}


	public static void captureScreenshot(WebDriver driver)
	{
		TakesScreenshot		ts					= (TakesScreenshot)driver;
							base64ImageString	= "data:image/png;base64," + ts.getScreenshotAs(OutputType.BASE64);
	}
}
