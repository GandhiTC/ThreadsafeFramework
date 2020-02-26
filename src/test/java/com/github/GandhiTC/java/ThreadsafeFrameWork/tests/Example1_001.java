package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import org.testng.Assert;
import org.testng.annotations.Test;



public class Example1_001 extends BaseClass
{
	@Test
	public void google1()
	{
		long threadID = Thread.currentThread().getId();
		
		System.out.println("\ngoogle1 Test Started! " + "Thread Id: " + threadID);
		
		driver().navigate().to("http://www.google.com");
		
		System.out.println("\nGoogle1 Test's Page title is: " + driver().getTitle() + " " + "Thread Id: " + threadID);
		
		Assert.assertEquals(driver().getTitle(), "Google");
		
		System.out.println("\nGoogle1 Test Ended! " + "Thread Id: " + threadID);
	}


	@Test
	public void google2()
	{
		long threadID = Thread.currentThread().getId();
		
		System.out.println("\n\tGoogle2 Test Started! " + "Thread Id: " + threadID);
		
		driver().navigate().to("http://www.google.com");
		
		System.out.println("\n\tGoogle2 Test's Page title is: " + driver().getTitle() + " " + "Thread Id: " + threadID);
		
		Assert.assertEquals(driver().getTitle(), "Google");
		
		System.out.println("\n\tGoogle2 Test Ended! " + "Thread Id: " + threadID);
	}


	@Test
	public void google3()
	{
		long threadID = Thread.currentThread().getId();
		
		System.out.println("\n\t\tGoogle3 Test Started! " + "Thread Id: " + threadID);
		
		driver().navigate().to("http://www.google.com");
		
		System.out.println("\n\t\tGoogle3 Test's Page title is: " + driver().getTitle() + " " + "Thread Id: " + threadID);
		
		Assert.assertEquals(driver().getTitle(), "Google");
		
		System.out.println("\n\t\tGoogle3 Test Ended! " + "Thread Id: " + threadID);
	}
}