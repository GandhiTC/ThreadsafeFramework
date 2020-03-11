This is an example of a Selenium testing framework in Java, with several built-in options and features.
It can be used as a base for more advanced test frameworks.

Two simple example tests are supplied, to show the framework in action.

I've tried to keep the project as simple as possible while still adding as many features as possible 
so that others can use it to learn from.

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
	- Test options taken directly from TestNG .xml file so that they can easily be changed on the fly
	- A JDBC/MySQL driver included
	- Implements Page Object Model
	- Separate trace and error logs via Log4j (v2)
	- Screenshots of browser window on test failure
	- HTML reports that are easier to read and more visually appealing via ExtentReports (v4)
		- Includes easy one-click access to screenshots for failed tests
		- Includes expandable/collapsable stack trace error message 
	- Loads base test configurations from a .properties file or from a MySQL database
	- Loads database connection info from a .properties file
	- Loads test data from an .xlsx file via Apache POI



---------------------------------------------------------------------------------------------------------------------------------------
Option-Controlled features include:
---------------------------------------------------------------------------------------------------------------------------------------
The following options can be changed by editing the TestNG parameters in the .xml files.
	- Test locally (tell test to use WebDriver) or in Selenium Grid (tell test to use RemoteWebDriver)
	- Run browser in headless mode (Chrome and Firefox)
	- Run WebDriver via a managed Service object



---------------------------------------------------------------------------------------------------------------------------------------
To Test
---------------------------------------------------------------------------------------------------------------------------------------
Edit parameters in your .xml TestNG file

Edit WebDriver options in: ./src/test/java/com/github/GandhiTC/java/ThreadSafeFrameWork/utilities/DriverManager

Run Example1_TestNG.xml as TestNG Suite to see a quick and easy example

Run Example2_TestNG.xml as TestNG Suite to see an example that implements all of the features (except Apache POI DataProvider)
	- NOTE:  I've set this file to run only 1 test, you can uncomment a couple of lines if you want to see the 2 other tests in action



