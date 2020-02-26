package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects.LoginPage;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.XLUtils;



public class Example2_002_Login extends BaseClass
{
	@Test(dataProvider="LoginData")
	public void loginDDT(String user,String pwd, ITestContext context) throws InterruptedException
	{
		driver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver().get(baseURL);
		
		LoginPage lp = new LoginPage(driver());
		
		lp.setUserName(user);
		logger.info("username provided");
		
		lp.setPassword(pwd);
		logger.info("password provided");
		
		lp.clickSubmit();
		Thread.sleep(2000);
		
		if(isAlertPresent() == true)
		{
			logger.warn("Login failed");
			
			driver().switchTo().alert().accept();
			driver().switchTo().defaultContent();
			logger.info("Alert accepted");
			
			String errMsg = "Test failed - username: " + user + "  password: " + pwd + "\n";
			logger.error(errMsg);
			
			context.setAttribute("ERRMSG", errMsg);
			Assert.assertTrue(false);
		}
		else
		{
			logger.info("Login succeeded");
			
			lp.clickLogout();
			logger.info("Logout clicked");
			Thread.sleep(2000);
			
			driver().switchTo().alert().accept();
			driver().switchTo().defaultContent();
			logger.info("Alert accepted");
			
			logger.info("Test passed - username: " + user + "  password: " + pwd + "\n");
			Assert.assertTrue(true);
		}
	}
	
	
	public boolean isAlertPresent()
	{
		try
		{
			driver().switchTo().alert();
			return true;
		}
		catch (NoAlertPresentException e)
		{
			return false;
		}
	}
	
	
	@DataProvider(name="LoginData")
	String [][] getData() throws IOException
	{
		String	path			= System.getProperty("user.dir") + "/src/test/java/com/inetbanking/testData/LoginData.xlsx";
		int		rownum			= XLUtils.getRowCount(path, "Sheet1");
		int		colcount		= XLUtils.getCellCount(path, "Sheet1", 1);
		String	logindata[][]	= new String[rownum][colcount];
		
		//	r and c are referring to the *indexes* of rows and cols, *not* the actual row and col *numbers*
		//	ie:  row 2 col 1 would have indexes 1, 0 (we start with row 2 because row 1 has headers)
		for(int r=1; r<=rownum; r++)
		{
			for(int c=0; c<colcount; c++)
			{
				// we use r-1 instead of r, so that index 0 in logindata is not skipped
				logindata[r-1][c] = XLUtils.getCellData(path,"Sheet1", r, c);	//	1, 0
			}
		}
		
		return logindata;
	}
}
