package com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects;



//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;



public class AddCustomerPage
{
	WebDriver ldriver;


	public AddCustomerPage(WebDriver rdriver)
	{
		ldriver = rdriver;
		PageFactory.initElements(ldriver, this);
	}


	@FindBy(how = How.XPATH, using = "/html/body/div[3]/div/ul/li[2]/a")
	@CacheLookup
	WebElement	lnkAddNewCustomer;
	
	@FindBy(how = How.NAME, using = "name")
	@CacheLookup
	WebElement	txtCustomerName;
	
	@FindBy(how = How.NAME, using = "rad1")
	@CacheLookup
	WebElement	rdGender;
	
	@CacheLookup
	@FindBy(how = How.ID_OR_NAME, using = "dob")
	WebElement	txtdob;
	
	@CacheLookup
	@FindBy(how = How.NAME, using = "addr")
	WebElement	txtaddress;
	
	@CacheLookup
	@FindBy(how = How.NAME, using = "city")
	WebElement	txtcity;
	
	@CacheLookup
	@FindBy(how = How.NAME, using = "state")
	WebElement	txtstate;
	
	@CacheLookup
	@FindBy(how = How.NAME, using = "pinno")
	WebElement	txtpinno;
	
	@CacheLookup
	@FindBy(how = How.NAME, using = "telephoneno")
	WebElement	txttelephoneno;
	
	@CacheLookup
	@FindBy(how = How.NAME, using = "emailid")
	WebElement	txtemailid;
	
	@CacheLookup
	@FindBy(how = How.NAME, using = "password")
	WebElement	txtpassword;
	
	@CacheLookup
	@FindBy(how = How.NAME, using = "sub")
	WebElement	btnSubmit;
	
	@CacheLookup
	@FindBy(how = How.TAG_NAME, using = "body")
	WebElement	body;


	public void clickAddNewCustomer()
	{
		lnkAddNewCustomer.click();
	}


	public void custName(String cname)
	{
		txtCustomerName.sendKeys(cname);
	}


	public void custgender(String cgender)
	{
		rdGender.click();
	}
	
	
	// take notice of the birthday input box, it is different
	public void custdob(String mm, String dd, String yyyy)
	{
		txtdob.sendKeys("value", mm);
		txtdob.sendKeys("value", dd);
		txtdob.sendKeys("value", yyyy);
		
//		JavascriptExecutor js = (JavascriptExecutor) ldriver;
////		js.executeScript("arguments[0].focus(); arguments[0].blur();", txtdob);
////		js.executeScript("arguments[0].focus(); arguments[0].blur(); return true", txtdob);
////		js.executeScript("validatedob(); return true");
//		js.executeScript("validatedob();");

//		txtdob.sendKeys(Keys.ENTER);
	}


	public void custaddress(String caddress)
	{
		txtaddress.sendKeys(caddress);
	}


	public void custcity(String ccity)
	{
		txtcity.sendKeys(ccity);
	}


	public void custstate(String cstate)
	{
		txtstate.sendKeys(cstate);
	}


	// sendkeys does not accept numbers, it only sees strings and chars
	public void custpinno(String cpinno)
	{
		txtpinno.sendKeys(String.valueOf(cpinno)); // in case cpinno was passed as an int, String.valueof will convert it
	}


	public void custtelephoneno(String ctelephoneno)
	{
		txttelephoneno.sendKeys(ctelephoneno);
	}


	public void custemailid(String cemailid)
	{
		txtemailid.sendKeys(cemailid);
	}


	public void custpassword(String cpassword)
	{
		txtpassword.sendKeys(cpassword);
	}


	public void custsubmit()
	{
		btnSubmit.click();
	}
}