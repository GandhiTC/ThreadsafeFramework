package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects.AddCustomerPage;
import com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects.LoginPage;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.BaseClass;



public class Example2_003_AddCustomer extends BaseClass
{
	@Test
	public void addNewCustomer(ITestContext context)
	{
		String 			errMsg	= "";
		
		try
		{
			maximizeWindow(driver);
			getURL(driver, baseURL, true);
			
			LoginPage		lp		= new LoginPage(driver);
			AddCustomerPage	addcust	= new AddCustomerPage(driver);
			
			//	logging in
			errMsg	= "Error while logging in";
			lp.setUserName(username);
			logger.info("User name is provided");
			
			lp.setPassword(password);
			logger.info("Passsword is provided");
			
			lp.clickSubmit();
			logger.info("Submit button clicked");
			
			sleep(1500L);
			
			
			//	adding customer
			errMsg	= "Error while adding customer";
			logger.info("providing customer details");
			addcust.clickAddNewCustomer();
			addcust.custName("New Customer");
			addcust.custgender("male");
			addcust.custdob("01", "02", "1903");
			addcust.custaddress("123 Lullaby Lane");
			addcust.custcity("HYD");
			addcust.custstate("AP");
			addcust.custpinno("5000074");
			addcust.custtelephoneno("987890091");
			addcust.custemailid(randomString(8) + "@gmail.com");
			addcust.custpassword("abcdef");
			addcust.custsubmit();
			
			
			//	validation
			logger.info("validation started");
			
			if(alertIsPresent(driver))
			{
				errMsg = alertText(driver);
				acceptAlert(driver);
			    context.setAttribute("Note", errMsg);
				logger.error("test case failed : " + errMsg);
				Assert.fail(errMsg);
			}
			
			if(!driver.getPageSource().contains("Customer Registered Successfully"))
			{
				errMsg	= "Error in validation";
				context.setAttribute("Note", errMsg);
				logger.error("test case failed : " + errMsg);
				Assert.fail(errMsg);
			}
			
			
			//	Pass test
			logger.info("test case passed");
			Assert.assertTrue(true);
		}
		catch(Exception e)
		{
			errMsg = e.getMessage();
			context.setAttribute("Note", errMsg);
			String causation = e.getCause() == null ? errMsg : e.getCause().toString();
			logger.error("test case failed : " + causation);
			Assert.fail(errMsg);
		}
	}
}
