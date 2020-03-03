package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import java.awt.AWTException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects.LoginPage;



public class Example2_001_Login extends BaseClass
{
	@Test
	public void loginTest(ITestContext context) throws IOException, AWTException, InterruptedException
	{
		driver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver().get(baseURL);
		
		LoginPage 	lp		= new LoginPage(driver());
		String 		errMsg	= "";
		
		try
		{
			errMsg = "Username webelement not found";
			lp.setUserName(username);
			logger.info("Entered username");
			
			errMsg = "Password webelement not found";
			lp.setPassword(password);
			logger.info("Entered password");
			
			errMsg = "Submit button webelement not found";
			lp.clickSubmit();
			logger.info("Button clicked");
			
			errMsg = "Page title is not correct";
			Assert.assertEquals(driver().getTitle(), "Guru99 Bank Manager HomePage");
			logger.info("Page title verified");
			
			logger.info("Login test passed");
			Assert.assertTrue(true);
		}
		catch (Throwable t)
		{
			context.setAttribute("ERRMSG", errMsg);
			
			logger.error("Login test failed" + ", " + errMsg);
			
			throw new AssertionError(t);	//	Assert.fail(errMsg, t);
		}
	}
}
