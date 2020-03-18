package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.JDBCDriver;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.DriverManager;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.ExtentListener;
import com.github.GandhiTC.java.ThreadsafeFrameWork.utilities.ExtentManager;



public abstract class BaseClass extends DriverManager
{
	private 	static 	final 	JDBCDriver 				dbInstance		= JDBCDriver.INSTANCE;
	private		static			boolean					sendEmail		= false;
	private		static			boolean					isService		= false;
	private		static			ThreadLocal<JDBCDriver>	threadedDB		= new ThreadLocal<>();
	protected	static			SoftAssert				softAssert		= new SoftAssert();
	protected	static 			String					baseURL			= "";
	protected 	static 		 	String					username		= "";
	protected	static 		 	String					password		= "";
	protected	static 			Logger					logger;


	@BeforeTest
	@Parameters(value = {"browser", "runHeadless", "runService", "isGridTest", "emailReport"})
	protected static void setup(String browser, String runHeadless, String runService, String isGridTest, String emailReport, ITestContext testContext)
	{
		sendEmail = Boolean.valueOf(emailReport);
		isService = Boolean.valueOf(runService);
		
		setupLogging();
		
		threadedDB.set(dbInstance);
		getCredentialsFromDatabase();
		
		setupDriver(browser, Boolean.valueOf(runHeadless), Boolean.valueOf(runService), Boolean.valueOf(isGridTest));
		testContext.setAttribute("BaseLogger", logger);
		testContext.setAttribute("WebDriver", driver());
		testContext.setAttribute("RunningHeadless", false);
		
		if(Boolean.valueOf(runHeadless))
		{
			if((browser.equalsIgnoreCase("firefox")) || (browser.equalsIgnoreCase("chrome")))
			{
				testContext.setAttribute("RunningHeadless", true);
			}
		}
	}
	
	
	@AfterTest
	protected static void tearDownTest()
	{
		db().closeConnection();
		
		if(driver() != null)
		{
			driver().quit();
		}
	}
	
	
	@AfterSuite
	protected static void tearDownSuite()
	{
		if(isService)
		{
			stopService();
		}

		removeDriver();
		
		
		/*
		 * Assumes you have created the following system environment variables.
		 *  - TestEmailFrom
		 *  - TestEmailTo
		 *  - TestEmailPassword (Gmail app password, not account password)
		 * 
		 * If the variable values are showing as null:
		 * 	- Shut down your IDE
		 * 	- Open Windows Task Manager
		 *  - Restart Windows Explorer
		 *  - Restart your IDE
		 * 
		 *  For more info, please visit:
		 *  - https://support.google.com/a/answer/176600?hl=en
		 *  - https://support.google.com/mail/?p=InvalidSecondFactor
		 * 
		 *  For ExtentEmailReporter (not currently implemented), visit:
		 *  - http://extentreports.com/docs/versions/4/net/email-reporter.html
		 * 
		 */
		if(sendEmail)
		{
			String	fromEmail		= System.getenv("TestEmailFrom");
			String	toEmail			= System.getenv("TestEmailTo");
			String	emailPassword	= System.getenv("TestEmailPassword");
	
			sendHTMLReportByGmail(fromEmail, emailPassword, toEmail, "Test Report", ExtentListener.bodyText);
		}
	}
	
	
	private static void setupLogging()
	{
		//	because this property is needed in log4j2.xml (log4j's config file)
		//	make sure it is set before initializing the logger
		System.setProperty("logsFolder", logsFolder);
		
		logger = LogManager.getLogger(BaseClass.class.getPackage().getName());
		
		//	disabling specific loggers must come after initializing log4j logger
		java.util.logging.Logger.getLogger("org.openqa.selenium.remote").setLevel(Level.OFF);
	}
	
	
	public static Logger logger()
	{
		if(logger == null)
		{
			setupLogging();
		}
		
		return logger;
	}
	
	
	protected static JDBCDriver db()
	{
		return threadedDB.get();
	}
	
	
	protected static void getCredentialsFromDatabase()
	{
		if(baseURL.equalsIgnoreCase("") || username.equalsIgnoreCase("") || password.equalsIgnoreCase(""))
		{
			try
			{
				//	Check if "pomCredentials" exists in the database.  If it does not, add it.
				if(!db().checkIfTableExists("pomCredentials"))
				{
					System.out.println("\r\nCreating \"pomCredentials\" table in database.\r\n");

					db().parseSqlFile("src/test/resources/InsertCredsTable.sql", false, true, false, false);
				}


				ResultSet resultSet = db().query("select * from pomCredentials LIMIT 1");
				//	LIMIT 1 gets first row only
				//	LIMIT 1, 1 gets second row only
				//	Limit 3, 1 gets 4th row only
				//	select * from tableName ORDER BY columnName DESC LIMIT 2, 1 gets 3rd from last row only

				while (resultSet.next())
				{
					baseURL  = resultSet.getString("baseURL");
					username = resultSet.getString("username");
					password = resultSet.getString("password");
				}
			}
			catch(SQLException e)
			{
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			finally
			{
				db().closeConnection();
			}
		}
	}
	
	
	private static void sendHTMLReportByGmail(String from, String pass, String to, String subject, String body)
	{
		//	Setup mail server & properties
		Properties	properties	= System.getProperties();
		String		host		= "smtp.gmail.com";
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.starttls.enable", "true");	//	use with port 587
//		properties.put("mail.smtp.ssl.enable", "true");			//	use with port 465
		properties.put("mail.smtp.auth", "true");
		
		try
		{
			// Get the Session object
			Session session = Session.getInstance(properties, new Authenticator()
			{
				@Override
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(from, pass);
				}
			});
			
//			session.setDebug(true);
			
			
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));
			
			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			// Set Subject: header field
			message.setSubject(subject);
			
			Multipart		multipart		= new MimeMultipart();
			MimeBodyPart	attachmentPart	= new MimeBodyPart();
			MimeBodyPart	textPart		= new MimeBodyPart();

			try
			{
				File 		file 			= new File(ExtentManager.reportFile);
				
				textPart.setText(body);
				multipart.addBodyPart(textPart);
				
				//	add attachment(s) last
				attachmentPart.attachFile(file);
				multipart.addBodyPart(attachmentPart);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			message.setContent(multipart);
			
			
			//	Send message
			Transport.send(message);
		}
		catch(MessagingException mex)
		{
			mex.printStackTrace();
		}
	}


	public String randomString(int numOfChars)
	{
		return RandomStringUtils.randomAlphabetic(numOfChars);
	}


	public int randomNumber(int numOfChars)
	{
		return Integer.parseInt(RandomStringUtils.randomNumeric(numOfChars));
	}
}
