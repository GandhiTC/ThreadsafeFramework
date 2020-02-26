package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import org.testng.Assert;
import org.testng.annotations.Test;



public class Example1_002 extends BaseClass
{
	@Test
	public void google4()
	{
		long threadID = Thread.currentThread().getId();
		
		System.out.println("\n\t\t\t\tgoogle4 Test Started! " + "Thread Id: " + threadID);
		
		driver().navigate().to("http://www.google.com");
		
		System.out.println("\n\t\t\t\tgoogle4 Test's Page title is: " + driver().getTitle() + " " + "Thread Id: " + threadID);
		
		Assert.assertEquals(driver().getTitle(), "Google");
		
		System.out.println("\n\t\t\t\tgoogle4 Test Ended! " + "Thread Id: " + threadID);
	}


	@Test
	public void yandex()
	{
		long threadID = Thread.currentThread().getId();
		
		System.out.println("\n\t\t\t\t\tyandex Test Started! " + "Thread Id: " + threadID);
		
		driver().navigate().to("http://www.yandex.com");
		
		System.out.println("\n\t\t\t\t\tyandex Test's Page title is: " + driver().getTitle() + " " + "Thread Id: " + threadID);
		
		Assert.assertEquals(driver().getTitle(), "Yandex");
		
		System.out.println("\n\t\t\t\t\tyandex Test Ended! " + "Thread Id: " + threadID);
	}
}