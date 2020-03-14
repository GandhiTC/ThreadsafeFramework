package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects.AddCustomerPage;
import com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects.LoginPage;



public class Example2_003_AddCustomer extends BaseClass
{
	@Test
	public void addNewCustomer(ITestContext context)
	{
		maximizeBrowser();
		getURL(baseURL, true);
		
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
		
		threadSleep(1500L);
		
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
		
		//	validating
		logger.info("validation started");
		
		if(isAlertPresent())
		{
			if(alertText().equalsIgnoreCase("please fill all fields"))
			{
				acceptAlert();
				errMsg	= "Error in filling form";
				context.setAttribute("Note", errMsg);
				logger.error("test case failed");
				Assert.fail(errMsg);
			}
		}
		
		threadSleep(1500L);
		
		if(!driver().getPageSource().contains("Customer Registered Successfully"))
		{
			errMsg	= "Error in validation";
			context.setAttribute("Note", errMsg);
			logger.error("test case failed");
			Assert.fail(errMsg);
		}
		
		logger.info("test case passed");
		Assert.assertTrue(true);
	}
}
