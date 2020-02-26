package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import java.awt.AWTException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects.AddCustomerPage;
import com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects.LoginPage;



public class Example2_003_AddCustomer extends BaseClass
{
	@Test
	public void addNewCustomer(ITestContext context) throws InterruptedException, IOException, AWTException
	{
		driver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver().get(baseURL);
		
		LoginPage		lp		= new LoginPage(driver());
		AddCustomerPage	addcust	= new AddCustomerPage(driver());
		String 			errMsg	= "";
		
		//	logging in
		errMsg	= "Error while logging in";
		lp.setUserName(username);
		logger.info("User name is provided");
		
		lp.setPassword(password);
		logger.info("Passsword is provided");
		
		lp.clickSubmit();
		logger.info("Submit button clicked");
		Thread.sleep(1500);
		
		//	adding customer
		errMsg	= "Error while adding customer";
		logger.info("providing customer details....");
		addcust.clickAddNewCustomer();
		addcust.custName("Pavan");
		addcust.custgender("male");
		addcust.custdob("10","15","1985");
		Thread.sleep(1500);
		addcust.custaddress("INDIA");
		addcust.custcity("HYD");
		addcust.custstate("AP");
		addcust.custpinno("5000074");
		addcust.custtelephoneno("987890091");
		addcust.custemailid(randomString(8) + "@gmail.com");
		addcust.custpassword("abcdef");
		addcust.custsubmit();
		Thread.sleep(3000);
		
		//	validating
		errMsg	= "Error while validating";
		logger.info("validation started....");
		if(driver().getPageSource().contains("Customer Registered Successfully!!!"))
		{
			logger.info("test case passed....");
			Assert.assertTrue(true);
			
		}
		else
		{
			context.setAttribute("ERRMSG", errMsg);
			logger.info("test case failed....");
			Assert.assertTrue(false);
		}
	}
}
