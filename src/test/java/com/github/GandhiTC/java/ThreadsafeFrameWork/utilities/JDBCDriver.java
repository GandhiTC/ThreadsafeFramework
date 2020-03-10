package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
//import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import com.github.GandhiTC.java.ThreadsafeFrameWork.tests.BaseClass2;



public enum JDBCDriver
{
	INSTANCE("Configuration/database.properties");
	
	
	private 		Logger		logger;
	private 		String 		host;
	private 		String 		port;
	private 		String 		database;
	private 		String 		username;
	private 		String 		password;
	private 		Connection	connection	= null;
	private 		Statement	statement	= null;
	private 		ResultSet	resultSet	= null;
	private 		String		prevQuery	= "";
	
	
	JDBCDriver(String propertiesFilePath)
	{
		this.logger = BaseClass2.logger();
		
		Properties props = new Properties();
		FileInputStream fis;

		try
		{
			fis = new FileInputStream(propertiesFilePath);
			props.load(fis);
		}
		catch(IOException e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
//			System.err.println(e.getMessage());
		}

		this.host		= props.getProperty("host");
		this.port		= props.getProperty("port");
		this.database	= props.getProperty("database");
		this.username	= props.getProperty("username");
		this.password	= props.getProperty("password");

		this.connection	= connection();
		this.statement	= statement();
		this.resultSet	= query("SELECT 1");	//	https://stackoverflow.com/a/3670000
	}
	
	
	public boolean isConnected()
	{
		try
		{
			return !connection.isValid(0) ? false : connection.isClosed() ? false : true;
		}
		catch(NullPointerException | SQLException npe)
		{
			return false;
		}
	}
	
	
	public Connection connection()
	{
		if(isConnected() && (connection != null))
		{
			return connection;
		}
		else
		{
			String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
			
			try
			{
				connection = DriverManager.getConnection(url, username, password);
				return connection;
			}
			catch (SQLException e)
			{
				logger.error(e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
	}
	
	
	public Statement statement()
	{
		if(isConnected() && (statement != null))
		{
			return statement;
		}
		else
		{
			try
			{
				statement = connection.createStatement();
				return statement;
			}
			catch (SQLException e)
			{
				logger.error(e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
	}
	
	
	public ResultSet query(String sqlQuery)
	{
		if(isConnected() && (resultSet != null) && sqlQuery.equalsIgnoreCase(prevQuery))
		{
			return resultSet;
		}
		else
		{
			boolean succeeded = true;
			
			try
			{
				resultSet = statement.executeQuery(sqlQuery);
				return resultSet;
			}
			catch (SQLException e)
			{
				succeeded = false;
				logger.error(e.getMessage());
				e.printStackTrace();
				return null;
			}
			finally
			{
				if(succeeded == true)
				{
					prevQuery = sqlQuery;
				}
			}
		}
	}
	
	
	public boolean checkIfTableExists(String tableName)
	{
		try
		{
			//	check if table named (in variable: tableName) exists in database
			String 				sqlQuery 	= 	"SELECT * " +
												"FROM information_schema.tables " +
												"WHERE table_schema = '" + database + "' " +
												"AND table_name = '" + tableName + "' " +
												"LIMIT 1;";
			
			//	get count of all tables in database
//			String 				sqlQuery 	= 	"SELECT count(*) AS TOTALNUMBEROFTABLES "
//												+ "FROM INFORMATION_SCHEMA.TABLES"
//												+ "WHERE TABLE_SCHEMA = '" + database + "';";
			
			//	get number of columns in a table
//			String 				sqlQuery 	= 	"SELECT COUNT(*) AS NUMBEROFCOLUMNS " +
//												"FROM INFORMATION_SCHEMA.COLUMNS " +
//												"WHERE table_schema = '" + database + "' " +
//												"AND table_name = '" + tableName + "';";
			
			ResultSet 			rs 			= 	query(sqlQuery);
			
			if (rs.next() == false)
			{
				return false;
		    }
			else
			{
				return true;
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	
	public void parseSqlFile(String filePath, boolean autoCommitEachLine, boolean stopScriptRunnerOnError, boolean closeConnectionOnError, boolean exitOnError)
	{
		try
		{
			validateFile(filePath, "sql");
			
			//	Initialize object for ScripRunner
			ScriptRunner sr = new ScriptRunner(connection, autoCommitEachLine, stopScriptRunnerOnError, logger);
			
			//	Give the input file to Reader
			Reader reader = new BufferedReader(new FileReader(filePath));
			
			//	Execute script
			sr.runScript(reader);
		}
		catch(Exception e)
		{
		}
		finally
		{
			if(closeConnectionOnError)
			{
				closeConnection();
				
				if(!isConnected())
				{
					System.out.println("Disconnected from database.");
				}
			}
			
			if(exitOnError)
			{
				System.out.println("Exiting program.");
				System.exit(1);
			}
		}
	}
	
	
	public void closeConnection()
	{
		try
		{
			//	Maintain correct order
			//	resultSet, statement, connection
			
			if(resultSet != null)
			{
				resultSet.close();
			}
			
			if(statement != null)
			{
				statement.close();
			}
			
			if(connection != null)
			{
				connection.close();
			}
			
		}
		catch(SQLException e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	private void validateFile(String filePath, String extensionRequired)
	{
		File file = new File(filePath);
		
		if(!file.isFile())
		{
			if(!file.exists())
			{
				String	fileName	= file.getName();
				int		dotIndex	= fileName.lastIndexOf('.');
				
				if(dotIndex == -1)
				{
					logger.error("Please make sure you are pointing to a file, not a path.");
					return;
				}
				
				String	extension	= fileName.substring(dotIndex + 1);
				
				if(!extensionRequired.equalsIgnoreCase(extension))
				{
					logger.error("Please make sure the file extension is: ." + extensionRequired);
					return;
				}
				
				logger.error("The file does not exist.");
				return;
			}
			else
			{
				logger.error("Error in given file path.");
				return;
			}
		}
	}
}