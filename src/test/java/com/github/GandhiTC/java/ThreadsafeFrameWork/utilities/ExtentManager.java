package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.openqa.selenium.Dimension;
//import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
//import org.openqa.selenium.TakesScreenshot;
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
	public 	static String			base64ThumbString	= null;
	public	static String			reportFile			= null;


	public static ExtentReports createInstance()
	{
		String 	css	  			= 	"		details {\r\n" +
									"		  font: \"Open Sans\", Calibri, sans-serif;\r\n" +
									"		  width: 100%;\r\n" +
									"		}\r\n" +
									"		\r\n" +
									"		details > summary {\r\n" +
									"		  color: white;\r\n" +
									"		  padding: 2px 6px;\r\n" +
									"		  width: 18em;\r\n" +
									"		  background-color: DarkRed;\r\n" +
									"		  border: solid black 1px;\r\n" +
									"		  box-shadow: 3px 3px 4px black;\r\n" +
									"		  cursor: pointer;\r\n" +
									"		  font-size: 1.25em;\r\n" +
									"		  font-weight: bold;\r\n" +
									"		  display: list-item;\r\n" +
									"		  border-radius: 5px 5px 0 0;\r\n" +
									"		}\r\n" +
									"		\r\n" +
									"		details > p {\r\n" +
									"		  border: solid black 1px;\r\n" +
									"		  border-radius: 0 0 10px 10px;\r\n" +
									"		  background-color: LightSlateGray;\r\n" +
									"		  padding: 2px 6px;\r\n" +
									"		  margin: 0;\r\n" +
									"		  box-shadow: 3px 3px 4px black;\r\n" +
									"		  font-weight: bold;\r\n" +
									"		}\r\n" +
									"		\r\n" +
									"		.tabbed {\r\n" +
									"			display:inline-block;\r\n" +
									"		    width:30px;\r\n" +
									"		}\r\n" +
									"		\r\n" +
									"		span.thumbnail {\r\n" +
									"			line-height: 30px;\r\n" +
									"    		border: none;\r\n" +
									"    		display: inline-block;\r\n" +
									"    		vertical-align: top;\r\n" +
									"    		padding-bottom: 30px !important;\r\n" +
									"		}\r\n" +
									"		\r\n" +
									"		.shifted {\r\n" +
									"			margin-left: 90px;\r\n" +
									"		}";
		
		
		String 	userDir 		= 	System.getProperty("user.dir");
		Path 	userDirPath 	= 	Paths.get(userDir);
		String 	projectName		= 	userDirPath.getFileName().toString();
		String	reportTitle		= 	"Test Results for Project: " + projectName;
		String	reportHeader	= 	"<font color=\"#039BE5\"><H5 class=\"shifted\"><u><b>Project: " + projectName + "</b></u></H5><H6 class=\"shifted\">Test Results</H6></font>";
		
		
		Date	currentDate		= 	new Date();
		
		String	monthFolder		= 	new SimpleDateFormat("yyyy-MM").format(currentDate);
		String	dayFolder		= 	new SimpleDateFormat("dd").format(currentDate);
		String	dateFolders		= 	monthFolder + "\\" + dayFolder;
		
		String 	timeString		= 	new SimpleDateFormat("HH_mm_ss_aa_z").format(currentDate);
		String	fileName		= 	timeString + "___Test-Report.html";
		
		String	filePath		= 	userDir + "\\" + Configurations.reportsFolder + "\\" + dateFolders + "\\" + fileName;
		
		
				reportFile		=	filePath;
		
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(filePath);
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setCSS(css);
		htmlReporter.config().setTheme(Theme.DARK);
		htmlReporter.config().setDocumentTitle(reportTitle);
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
//		TakesScreenshot		ts					= (TakesScreenshot)driver;
//							base64ImageString	= "data:image/png;base64," + ts.getScreenshotAs(OutputType.BASE64);
		
		try
		{
			DriverManager.bringToFront(false);
			
			Point 			position 			= driver.manage().window().getPosition();
			Dimension 		size 				= driver.manage().window().getSize();
			Robot 			robot 				= new Robot();
			Rectangle 		rect 				= new Rectangle(position.getX(), position.getY(), size.getWidth(), size.getHeight());
			BufferedImage 	image 				= robot.createScreenCapture(rect);
							base64ImageString 	= imageToString(image, "png");
							base64ThumbString	= imageThumbNail(image, "png");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	//	Encode
	private static String imageToString(BufferedImage image, String format)
	{
		String					imageString	= null;
		ByteArrayOutputStream	outStream	= new ByteArrayOutputStream();

		try
		{
			ImageIO.write(image, format, outStream);
			
			byte[]	imageBytes	= outStream.toByteArray();
					imageString = Base64.getEncoder().encodeToString(imageBytes);
							
			outStream.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		//	use with MediaEntityModelProvider
//		return imageString;
		
		//	use with custom code
		return "data:image/png;base64," + imageString;
	}


	//	Decode
	@SuppressWarnings("unused")
	private static BufferedImage stringToImage(String imageString)
	{
		BufferedImage	image		= null;

		try
		{
			byte[]					imageByte 	= Base64.getDecoder().decode(imageString);
			ByteArrayInputStream 	inStream	= new ByteArrayInputStream(imageByte);
									image 		= ImageIO.read(inStream);
			
			inStream.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return image;
	}
	
	
	//	thumbnail
	private static String imageThumbNail(BufferedImage image, String format)
	{
		String					imageString	= null;
		ByteArrayOutputStream	outStream	= new ByteArrayOutputStream();
		
//		int						thumbHeight	= 200;
//		double					sizeRatio	= thumbHeight / image.getHeight();
//		int						thumbWidth	= (int)(image.getWidth() * sizeRatio);
		BufferedImage			thumbImg	= Scalr.resize(image, Method.ULTRA_QUALITY, Mode.AUTOMATIC, 200, 200);

		try
		{
			ImageIO.write(thumbImg, format, outStream);
			
			byte[]	imageBytes	= outStream.toByteArray();
					imageString = Base64.getEncoder().encodeToString(imageBytes);
			
			outStream.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		//	use with MediaEntityModelProvider
//		return imageString;
		
		//	use with custom code
		return "data:image/png;base64," + imageString;
	}
}
