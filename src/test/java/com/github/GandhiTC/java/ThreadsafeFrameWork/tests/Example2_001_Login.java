package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import java.awt.AWTException;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects.LoginPage;



public class Example2_001_Login extends BaseClass
{
	private LoginPage 	lp;
	private Set<String> errSet;
	
	
	@Test
	public void loginTest(ITestContext context) throws IOException, AWTException, InterruptedException
	{
		lp		= new LoginPage(driver());
		errSet	= new LinkedHashSet<String>();
		
		maximizeBrowser();
		getURL(baseURL, true);
		
		
		//	1)  checking if element exists
		//	2)  printing a collection of element (non-css) attributes
		By by = By.name("uid");
		
		if(elementExists(by))
		{
			printAttributes(driver().findElement(by));
		}
		else
		{
			System.err.println("Element does not exist.\r\n");
		}
		
		
		//	test username element
		try
		{
			lp.setUserName(username);
			logger.info("Entered username");
		}
		catch(Exception e)
		{
			softFail(errSet, "Username webelement not found");
		}
		
		
		//	test password element
		try
		{
			lp.setPassword(password);
			logger.info("Entered password");
		}
		catch(Exception e)
		{
			softFail(errSet, "Password webelement not found");
		}
		
		
		//	test submit button element
		try
		{
			lp.clickSubmit();
			logger.info("Button clicked");
		}
		catch(Exception e)
		{
			softFail(errSet, "Submit button webelement not found");
		}
		
		
		//	test page title
		try
		{
			softAssert.assertEquals(driver().getTitle(), "Guru99 Bank Manager HomePage");
		}
		catch(Exception e)
		{
			softFail(errSet, "Page title is not correct");
		}
		
		
		//	apply soft assertions or pass
		if(!errSet.isEmpty())
		{
			context.setAttribute("Note", errSet);
			logger.error("Login test failed : " + errSet);
			softAssert.assertAll();
		}
		else
		{
			logger.info("Login test passed");
			Assert.assertTrue(true);
		}
	}
	
	
	private static void softFail(Set<String> errSet, String errMsg)
	{
		logger.error(errMsg);
		softAssert.fail(errMsg);
		errSet.add(errMsg);
//		softAssert.fail(errSet.toArray()[errSet.size() - 1].toString());
	}
}
