package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



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
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;



public abstract class BaseClass extends DriverManager
{
	private		static	boolean					runHeadless		= false;
	private		static	boolean					runService		= false;
	private		static	boolean					isGridTest		= false;
	private		static	boolean					emailReport		= false;
	
	public		static 	Logger					logger			= logger();
	
	//	maintain order
	private		static 	JDBCDriver 				dbInstance		= JDBCDriver.INSTANCE;
	private		static	ThreadLocal<JDBCDriver>	threadedDB		= new ThreadLocal<>();
	private		static 	JDBCDriver 				db;
	
	//	maintain order
	private		static	XLUtils					xlInstance		= new XLUtils();
	private		static	ThreadLocal<XLUtils>	threadedXL		= new ThreadLocal<>();
	public		static	XLUtils					xlUtils			= xlUtils();



	//	Start setup
	
	//	Suite
	@BeforeSuite
	@Parameters(value = {"runHeadless", "runService", "isGridTest", "emailReport"})
	protected void setupSuite(String runHeadless, String runService, String isGridTest, String emailReport)
	{
		BaseClass.runHeadless 	= Boolean.valueOf(runHeadless);
		BaseClass.runService 	= Boolean.valueOf(runService);
		BaseClass.isGridTest 	= Boolean.valueOf(isGridTest);
		BaseClass.emailReport 	= Boolean.valueOf(emailReport);
		
		setupLogging();
		setupDB();
		setupXL();
	}
	
	
	//	Test
	@BeforeTest
	@Parameters(value = {"browser"})
	protected void setupTest(String browser, ITestContext testContext)
	{
		//	Setup WebDriver according to options
		setupDriver(browser, runHeadless, runService, isGridTest);
		
		//	Set test context attributes, to be used by TestNG/Extent listeners
		testContext.setAttribute("RunningHeadless", false);
		
		//	Update attribute value conditionally
		if(runHeadless)
		{
			if((browser.equalsIgnoreCase("firefox")) || (browser.equalsIgnoreCase("chrome")))
			{
				testContext.setAttribute("RunningHeadless", true);
			}
		}
		
		testContext.setAttribute("BaseLogger", logger);
		testContext.setAttribute("WebDriver", driver);
	}
	
	//	End setup
	
	
	
	
	//	Start teardown
	
	//	Test
	@AfterTest
	protected void tearDownTest()
	{
		if(driver != null)
		{
			driver.quit();
			driver = null;
		}
	}
	
	
	//	Suite
	@AfterSuite
	protected void tearDownSuite()
	{
		if(runService)
		{
			stopService();
		}

		removeDriver();
		removeDB();
		removeXL();
		
		
		/*
		 * Run the following to email the generated Extent test report
		 * at the end of each TestNG test suite.
		 * 
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
		if(emailReport)
		{
			String	fromEmail		= System.getenv("TestEmailFrom");
			String	toEmail			= System.getenv("TestEmailTo");
			String	emailPassword	= System.getenv("TestEmailPassword");
	
			sendHTMLReportByGmail(fromEmail, emailPassword, toEmail, "Test Report", ExtentListener.emailBodyText);
		}
	}
	
	//	End teardown
	
	
	
	
	//	Start logger
	
	private static synchronized void setupLogging()
	{
		if(logger == null)
		{
			//	because this property is needed in log4j2.xml (log4j's config file)
			//	make sure it is set before initializing the logger
			System.setProperty("logsFolder", logsFolder);
			
//			logger = LogManager.getLogger(BaseClass.class.getPackage().getName());
			logger = LogManager.getLogger("ThreadsafeFramework");
			
			//	disabling specific loggers must come after initializing log4j logger
			java.util.logging.Logger.getLogger("org.openqa.selenium.remote").setLevel(Level.OFF);
		}
	}
	
	
	private static Logger logger()
	{
		if(logger == null)
		{
			setupLogging();
		}

		return logger;
	}
	
	//	End logger
	
	
	
	
	//	Start DB
	
	private static synchronized void setupDB()
	{
		if(db == null)
		{
			threadedDB.set(dbInstance);
			db = threadedDB.get();
			getCredentialsFromDatabase();
		}
	}
	
	
	private static void removeDB()
	{
		threadedDB.remove();
		
		if(db != null)
		{
			db.closeConnection();
			clearCredentialsFromDatabase();
			db = null;
		}
	}
	
	
	private static void getCredentialsFromDatabase()
	{
		if(baseURL.isEmpty() || username.isEmpty() || password.isEmpty())
		{
			try
			{
				//	Check if "pomCredentials" exists in the database.  If it does not, add it.
				if(!db.checkIfTableExists("pomCredentials"))
				{
					System.out.println("\r\nCreating \"pomCredentials\" table in database.\r\n");

					db.parseSqlFile("src/test/resources/InsertCredsTable.sql", false, true, false, false);
				}
				
				
				//	Populate "pomCredentials" if table exists but is empty
				ResultSet resultSet = db.query("SELECT COUNT(*) AS rowcount FROM pomCredentials");
				resultSet.next();
				int count = resultSet.getInt("rowcount");
				
				if(count < 1)
				{
					System.out.println("\r\n\"pomCredentials\" table is empty, populating data.\r\n");
					
					db.parseSqlFile("src/test/resources/InsertCredsTable.sql", false, true, false, false);
				}
				
				
				//	Use first row of "pomCredentials" table, to assign framework values
				resultSet = db.query("select * from pomCredentials LIMIT 1");
				//	LIMIT 1 gets first row only
				//	LIMIT 1, 1 gets second row only
				//	Limit 3, 1 gets 4th row only
				//	select * from tableName ORDER BY columnName DESC LIMIT 2, 1 gets 3rd from last row only
				
				while(resultSet.next())
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
				db.closeConnection();
			}
		}
	}
	
	
	private static void clearCredentialsFromDatabase()
	{
		baseURL  = "";
		username = "";
		password = "";
	}
	
	//	End DB
	
	
	
	
	//	Start excel utils
	
	private static synchronized void setupXL()
	{
		if(xlUtils == null)
		{
			threadedXL.set(xlInstance);
			xlUtils = threadedXL.get();
		}
	}
	
	
	private static XLUtils xlUtils()
	{
		if(xlUtils == null)
		{
			setupXL();
		}

		return xlUtils;
	}
	
	
	private static void removeXL()
	{
		threadedXL.remove();

		if(xlUtils != null)
		{
			xlUtils = null;
		}
	}
	
	//	End excel utils
	
	
	
	
	//	Start Email
	
	private static void sendHTMLReportByGmail(String from, String pass, String to, String subject, String body)
	{
		//	Setup mail server & properties
		Properties	properties	= System.getProperties();
		String		host		= "smtp.gmail.com";
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.starttls.enable", "true");	//	use with TLS port 587
//		properties.put("mail.smtp.ssl.enable", "true");			//	use with SSL port 465
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
	
	//	End Email

	
	
	
	//	Start miscellaneous

	public String randomString(int numOfChars)
	{
		return RandomStringUtils.randomAlphabetic(numOfChars);
	}


	public int randomNumber(int numOfChars)
	{
		return Integer.parseInt(RandomStringUtils.randomNumeric(numOfChars));
	}
	
	//	End miscellaneous
}
