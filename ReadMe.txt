This is an example of a Selenium testing framework in Java, with several built-in options and features.
It can be used as a base for more advanced test frameworks.

Two simple example tests are supplied, to show the framework in action.

I've tried to keep the project as simple as possible while still adding as many features as possible 
so that others may use it to learn from.

NOTE:	In example 2, the test logs into a MySQL database to retrieve the URL, username, & password.
		This is done purely to show an example at work, it is not recommended in production.
		Instead, look into saving sensitive values into environment variables.

NOTE:	I've added a custom filter for the loggers used in this project.
		If you log an error where the message is "^^", it will add a blank line to the logs.
		ie:  logger.error("^^");



---------------------------------------------------------------------------------------------------------------------------------------
Base features include:
---------------------------------------------------------------------------------------------------------------------------------------
	- Thread-safe so that tests can be run using multiple threads without cross contamination
	- Test options taken directly from TestNG .xml file(s) so that they can easily be changed on the fly
	- A JDBC/MySQL driver included
		- Minimizes coding
	- Structured to allow easy implementation of Page Object Model
	- Separate info and error logs via Log4j (v2)
		- Directories based on date
	- Screenshots of browser window on test failure
		- Uses Base64 imaging to embed screenshots directly into report files
	- HTML reports that are easier to read and more visually appealing via ExtentReports (v4)
		- Includes easy one-click access to screenshots for failed tests
		- Includes expandable/collapsible stack trace error message 
	- Includes an example of loading test data from an .xlsx file via Apache POI DataProvider
	- Includes examples of loading configuration and/or test properties via
		- Properties file
		- MySQL database
		- System environment variable(s)
		- TestNG test context
		- TestNG parameters
	- Several WebDriver shortcut methods built in
		- Wait for page to load (via Javascript Executor)
		- Print or collect web element attributes
		- Open link in new tab or window
		- etc



---------------------------------------------------------------------------------------------------------------------------------------
Option-Controlled features include:
---------------------------------------------------------------------------------------------------------------------------------------
In addition to the base features, the following options can be changed by editing the TestNG parameters in the .xml files.
	- Test locally (tell test to use WebDriver) or in Selenium Grid (tell test to use RemoteWebDriver)
	- Run browser in headless mode (Chrome and Firefox)
	- Run WebDriver via a managed Service object
	- Email HTML report after completion of test suite



---------------------------------------------------------------------------------------------------------------------------------------
To Test
---------------------------------------------------------------------------------------------------------------------------------------
Go to : http://demo.guru99.com/
		Submit any email address, copy the username and password credentials
		Add credentials to LoginData.xlsx in testData package
		Update credentials in "Values" section of the InsertCredsTable.sql file in ./src/test/resources/
		Update credentials in testing.properties file in Configuration directory
		
Go to : https://www.freemysqlhosting.net/
		Create new account and database
		Update database credentials in database.properties file in Configuration directory
		
To send email reports:
		Setup a gmail account
		Setup an App password - for details, see : https://support.google.com/accounts/answer/185833
		Add the following system environment variables:
			TestEmailFrom 	  - Your new email address
			TestEmailTo   	  - The email address you want to send reports to
			TestEmailPassword - The App password created for your account

Edit parameters as needed in your .xml TestNG file in Runners directory

Edit WebDriver options as needed in: ./src/test/java/com/github/GandhiTC/java/ThreadSafeFrameWork/utilities/DriverManager

Run Example1_TestNG.xml as TestNG Suite to see a quick and easy example of threading at work

Run Example2_TestNG.xml as TestNG Suite to see an example that implements all features



