package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import java.awt.AWTException;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects.LoginPage;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.BaseClass;



public class Example2_001_Login extends BaseClass
{
	private LoginPage 	lp;
	private Set<String> errSet = new LinkedHashSet<String>();
	
	
	@Test
	public void loginTest(ITestContext context) throws IOException, AWTException, InterruptedException
	{
		//	Clear errSet at the beginning of every test
		errSet.clear();
		
		lp = new LoginPage(driver);
		
		maximizeWindow(driver);
		getURL(driver, baseURL, true);
		
		
		//	1)  Check if element exists
		//	2)  Print a collection of (non-css) element attributes
		
		//	By version
		By by = By.name("uid");

		if(elementExists(driver, by))
		{
			printAttributes(driver, driver.findElement(by));
		}
		else
		{
			System.err.println("Element does not exist.\r\n");
		}
		
		//	WebElement version
//		WebElement usr = driver.findElement(By.name("uid"));
//
//		if(elementExists(driver, usr))
//		{
//			printAttributes(driver, usr);
//		}
//		else
//		{
//			System.err.println("Element does not exist.\r\n");
//		}
		
		
		//	test username element
		try
		{
			lp.setUserName(username);
			logger.info("Entered username");
		}
		catch(Exception e)
		{
			softFail("Username webelement not found");
		}
		
		
		//	test password element
		try
		{
			lp.setPassword(password);
			logger.info("Entered password");
		}
		catch(Exception e)
		{
			softFail("Password webelement not found");
		}
		
		
		//	test submit button element
		try
		{
			lp.clickSubmit();
			logger.info("Button clicked");
		}
		catch(Exception e)
		{
			softFail("Submit button webelement not found");
		}
		
		
		//	test page title
		try
		{
			softAssert.assertEquals(driver.getTitle(), "Guru99 Bank Manager HomePage");
		}
		catch(Exception e)
		{
			softFail("Page title is not correct");
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
	
	
	private void softFail(String errMsg)
	{
		logger.error(errMsg);
		softAssert.fail(errMsg);
		errSet.add(errMsg);
	}
}
