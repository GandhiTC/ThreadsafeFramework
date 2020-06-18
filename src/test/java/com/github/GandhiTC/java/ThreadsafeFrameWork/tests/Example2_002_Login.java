package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import java.io.IOException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects.LoginPage;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.BaseClass;



public class Example2_002_Login extends BaseClass
{
	@Test(dataProvider="LoginData")
	public void loginDDT(String user, String pwd, ITestContext context) throws InterruptedException
	{
		maximizeWindow(driver);
		getURL(driver, baseURL, true);
		
		LoginPage lp = new LoginPage(driver);
		
		lp.setUserName(user);
		logger.info("username provided");
		
		lp.setPassword(pwd);
		logger.info("password provided");
		
		lp.clickSubmit();

		waitForAlert(driver, 5, false);
		
		if(alertIsPresent(driver))
		{
			String alertText = alertText(driver);
			
			logger.warn("Login failed");
			
			acceptAlert(driver);
			logger.info("Alert accepted");
			
			String errMsg = "Test failed - username: " + user + "  password: " + pwd + "\n";
			logger.error(errMsg);
			
			context.setAttribute("Note", errMsg);
			
			Assert.fail(alertText);
		}
		else
		{
			logger.info("Login succeeded");
			
			lp.clickLogout();
			logger.info("Logout clicked");

			waitForAlert(driver, 5, true);
			
			acceptAlert(driver);
			logger.info("Alert accepted");
			
			String passMsg = "Test passed - username: " + user + "  password: " + pwd + "\n";
			logger.info(passMsg);
			
			context.setAttribute("Note", passMsg);
		}
	}
	
	
	@DataProvider(name="LoginData")
	String [][] getData() throws IOException
	{
		String		path 		= null;
		int			rownum 		= 0;
		int			colcount 	= 0;
		String[][]	logindata 	= null;
		
		try
		{
			path			= System.getProperty("user.dir") + "/src/test/java/com/github/GandhiTC/java/ThreadsafeFrameWork/testData/LoginData.xlsx";
			rownum			= xlUtils.getRowCount(path, "Sheet1");
			colcount		= xlUtils.getCellCount(path, "Sheet1", 1);
			logindata		= new String[rownum][colcount];
			
			//	r and c are referring to the *indexes* of rows and cols, *not* the actual row and col *numbers*
			//	ie:  row 2 col 1 would have indexes 1, 0 (we start with row 2 because row 1 has headers)
			for(int r=1; r<=rownum; r++)
			{
				for(int c=0; c<colcount; c++)
				{
					// we use r-1 instead of r, so that index 0 in logindata is not skipped
					logindata[r-1][c] = xlUtils.getCellData(path,"Sheet1", r, c);	//	1, 0
				}
			}
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
		
		return logindata;
	}
}
