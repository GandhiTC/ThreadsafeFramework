package com.github.GandhiTC.java.ThreadsafeFrameWork.pageObjects;



import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;



public class LoginPage
{
	private WebDriver driver;


	public LoginPage(WebDriver driver)
	{
		this.driver = driver;
		PageFactory.initElements(this.driver, this);
	}


	@FindBy(name = "uid")
	@CacheLookup
	private WebElement	txtUserName;
	
	@FindBy(name = "password")
	@CacheLookup
	private WebElement	txtPassword;
	
	@FindBy(name = "btnLogin")
	@CacheLookup
	private WebElement	btnLogin;
	
	@FindBy(xpath = "/html/body/div[3]/div/ul/li[15]/a")
	@CacheLookup
	private WebElement	lnkLogout;


	public void setUserName(String userName)
	{
		txtUserName.sendKeys(userName);
	}


	public void setPassword(String pwd)
	{
		txtPassword.sendKeys(pwd);
	}


	public void clickSubmit()
	{
		btnLogin.click();
	}


	public void clickLogout()
	{
		lnkLogout.click();
	}
}
