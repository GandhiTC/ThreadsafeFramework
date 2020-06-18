package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



//import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
//import com.aventstack.extentreports.MediaEntityBuilder;
//import com.aventstack.extentreports.MediaEntityModelProvider;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;



public class ExtentListener implements ITestListener, ISuiteListener
{
	public  static	ThreadLocal<ExtentTest>	testThread			= new ThreadLocal<ExtentTest>();
	public	static	ExtentReports			report				= ExtentManager.createInstance();
	
	private static	Logger					logger;
	private static	ITestContext			staticContext;
	
	private static	String					lastSkipped			= "";
	private			String					logText				= "";
	private			String					methodName			= "";
	private			Markup					markup;
	
	private			WebDriver				driver;
	
	public	static	String					emailBodyText		= "";
	private	static	int						passNum			= 0,
											failNum			= 0,
											skipNum			= 0;
	
	
	//	Suite Start
	@Override
	public void onStart(ISuite suite)
	{
		logger = BaseClass.logger;
	}
	
	
	//	Start of All Tests in Suite
	@Override
	public void onStart(ITestContext context)
	{
	}
	
	
	//	Individual Test Start
	@Override
	public void onTestStart(ITestResult result)
	{
						staticContext		= result.getTestContext();
//						logger 				= (Logger)staticContext.getAttribute("BaseLogger");
						methodName 			= result.getMethod().getMethodName() + "()";
					
		String			suiteName			= staticContext.getSuite().getName();
		String			xmlTest				= staticContext.getCurrentXmlTest().getName();
		
		String			fullClassName		= result.getTestClass().getName();
		int				periodIndex			= fullClassName.lastIndexOf(".");
		String			packageName			= fullClassName.substring(0, periodIndex);
		String			className			= fullClassName.substring(periodIndex + 1, fullClassName.length());
		
		ExtentTest		test				= report.createTest(methodName);
		
		testThread.set(test);
		
		StringBuilder	testInfo			= new StringBuilder();
		testInfo.append(suiteName     + "<br>");
		testInfo.append(xmlTest);
		
		StringBuilder	methodInfo			= new StringBuilder();
		methodInfo.append(packageName + "<br>");
		methodInfo.append(className   + "<br>");
		methodInfo.append(methodName);
		
		//	xml suite & test names
		testThread.get().info(testInfo.toString());
		
		//	package, class, & method names
		testThread.get().info(methodInfo.toString());
		
		//	user agent info from browser
		driver = (WebDriver)result.getTestContext().getAttribute("WebDriver");
		testThread.get().info(DriverManager.userAgentInfo(driver));
	}


	//	Test Pass
	@Override
	public void onTestSuccess(ITestResult result)
	{
		//	markup
		logText		= "<b>TEST CASE PASSED : " + methodName + "</b>";
		markup		= MarkupHelper.createLabel(logText, ExtentColor.GREEN);
		
		testThread.get().pass(markup);
		
		
		//	short message from custom attribute
		if(result.getTestContext().getAttribute("Note") != null)
		{
			testThread.get().pass(result.getTestContext().getAttribute("Note").toString());
		}
	}


	//	Test Fail
	@Override
	public synchronized void onTestFailure(ITestResult result)
	{
		ITestContext 	testContext	= result.getTestContext();
		
		
		//	markup
						logText		= "<b>TEST CASE FAILED : " + methodName + "</b>";
						markup		= MarkupHelper.createLabel(logText, ExtentColor.RED);
		testThread.get().log(Status.FAIL, markup);
		
		
		//	expandable stack trace
		String message		= result.getThrowable().getMessage();
		String stackTrace 	= Arrays.toString(result.getThrowable().getStackTrace()).replaceAll(", ", "<br><span class=\"tabbed\"></span>");

		testThread.get().fail("<details>"
								+ "<summary>Exception Occured : Click To View</summary>"
								+ "<p><br>"
								+ message
								+ "<br><br>"
								+ "<span class=\"tabbed\"></span>" + stackTrace
								+ "</p></details>");


		//	short error message from custom attribute
		if(testContext.getAttribute("Note") != null)
		{
			testThread.get().fail(testContext.getAttribute("Note").toString());
		}


		//	screenshot
		if((testContext.getAttribute("WebDriver") != null))
		{
			boolean runningHeadless = (boolean)result.getTestContext().getAttribute("RunningHeadless");
			
			if(runningHeadless == true)
			{
				logger.warn("Skipped taking screenshot due to running browser in headless mode.");
			}
			else
			{
				try
				{
					WebDriver				 webDriverAttribute	= (WebDriver)result.getTestContext().getAttribute("WebDriver");
					
					ExtentManager.captureScreenshot(webDriverAttribute);
					
					
//					MediaEntityModelProvider provider 			= MediaEntityBuilder.createScreenCaptureFromBase64String(ExtentManager.base64ImageString).build();
//					testThread.get().fail("<font color=red>Click for screenshot : &nbsp;</font>", provider);
					
					
					//	replacement for ExtentReport's MediaEntityModelProvider code
					String thumbLink = "<span class=\"thumbnail\"><font color=\"red\">Click for screenshot : &nbsp;</font><br>"
									 + "<a href=\""  + ExtentManager.base64ImageString + "\" data-featherlight=\"image\">"
									 + "<img src=\"" + ExtentManager.base64ThumbString + "\" title=\"Click for screenshot\"></a></span>";
					testThread.get().fail(thumbLink);
				}
				catch(Exception e)	//	IOException
				{
					System.err.println(e.getMessage());
				}
			}
		}
	}


	//	Test Skip
	@Override
	public void onTestSkipped(ITestResult result)
	{
		//	markup
				logText				= "<b>TEST CASE SKIPPED : " + methodName + "</b>";
				markup				= MarkupHelper.createLabel(logText, ExtentColor.ORANGE);
				
		
		String 	throwableTypeName 	= result.getThrowable().getClass().getTypeName();
		
		if(throwableTypeName.equalsIgnoreCase("org.testng.SkipException"))
		{
			testThread.get().skip(markup);
			return;
		}
		else if(throwableTypeName.equalsIgnoreCase("java.lang.Throwable"))
		{
			String trimmedMethodName = methodName.substring(0, methodName.length() - 2);
			
			if(!lastSkipped.equalsIgnoreCase(trimmedMethodName))
			{
				lastSkipped = result.getMethod().getMethodName();
				onTestStart(result);
				onTestSkipped(result);
			}
			else
			{
				testThread.get().log(Status.SKIP, markup);
				
				String requiredMethods = "";
				
				if(result.getMethod().getMethodsDependedUpon() != null)
				{
					String[] dependencies = result.getMethod().getMethodsDependedUpon();
				
					if(dependencies.length > 0)
					{
						List<String> passedMethods = new ArrayList<String>();
						
						for(ITestResult passedResult : result.getTestContext().getPassedTests().getAllResults())
						{
							passedMethods.add(passedResult.getMethod().getMethodName());
						}
						
						for(String currMethod : dependencies)
						{
							if(!passedMethods.contains(currMethod))
							{
								requiredMethods += currMethod + "(), ";
							}
						}
						
						if(!requiredMethods.equalsIgnoreCase(""))
						{
							requiredMethods = requiredMethods.substring(0, requiredMethods.length() - 2);
							testThread.get().log(Status.SKIP, "Method: " + methodName + " depends on the following failed or skipped methods: " + requiredMethods);
							return;
						}
						else
						{
							testThread.get().log(Status.SKIP, Arrays.toString(result.getThrowable().getStackTrace()));
							return;
						}
					}
				}
			}
		}
	}


	//	Test Fail Within Percentage
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result)
	{
	}


	//	End of All Tests in Suite
	@Override
	public void onFinish(ITestContext context)
	{
		logger.error("^^");
		
		if(report != null)
		{
			report.flush();
		}
		
		
		//	The following code makes more sense to be ran in ISuiteListener's onFinish() method
		//	but because we would not be able to pass resulting values to the report email
		//	we update the values at the end of each test instead.
		passNum		 += context.getPassedTests().getAllResults().size();
		failNum		 += context.getFailedTests().getAllResults().size();
		skipNum		 += context.getSkippedTests().getAllResults().size();
		
		emailBodyText = "Pass: " + passNum + "\r\n" + "Fail: " + failNum + "\r\n" + "Skip: " + skipNum + "\r\n";
	}


	//	End of Suite
	@Override
	public void onFinish(ISuite suite)
	{
		logger.error("^^");
		testThread.remove();
	}
}