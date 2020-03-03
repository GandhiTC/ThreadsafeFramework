package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



import java.io.*;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;



/*
 * GandhiTC NOTES:
 * 
 * Original script:
 * 		  https://github.com/BenoitDuffez/ScriptRunner
 * 
 * I have removed:
 * 		- All usage of PrintWriter
 * 
 * I have added:
 * 		- Implementation of Log4j
 * 
 * I have modified:
 * 		- Some error messages
 * 		- Some access modifiers
 */

/*
 * Slightly modified version of the com.ibatis.common.jdbc.ScriptRunner class
 * from the iBATIS Apache project. Only removed dependency on Resource class
 * and a constructor
 * GPSHansl, 06.08.2015: regex for delimiter, rearrange comment/delimiter detection, remove some ide warnings.
 */

/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */



/**
 * Tool to run database scripts
 */
class ScriptRunner
{
	private static 	final 	String			DEFAULT_DELIMITER	= ";";
	private static 	final 	Pattern			SOURCE_COMMAND		= Pattern.compile("^\\s*SOURCE\\s+(.*?)\\s*$", Pattern.CASE_INSENSITIVE);
	
	/**
	 * regex to detect delimiter. ignores spaces, allows delimiter in comment, allows an equals-sign
	 */
	public	static	final 	Pattern			delimP				= Pattern.compile("^\\s*(--)?\\s*delimiter\\s*=?\\s*([^\\s]+)+\\s*.*$", Pattern.CASE_INSENSITIVE);
	private 		final 	Connection		connection;
	private 		final 	boolean			stopOnError;
	private 		final 	boolean			autoCommit;
	private 				String			delimiter			= DEFAULT_DELIMITER;
	private 				boolean			fullLineDelimiter	= false;
	private 				String			userDirectory		= System.getProperty("user.dir");
	
	private static			Logger			logger;


	/**
	 * Default constructor
	 */
	ScriptRunner(Connection connection, boolean autoCommit, boolean stopOnError, Logger logger)
	{
				this.connection		= connection;
				this.autoCommit		= autoCommit;
				this.stopOnError	= stopOnError;
				ScriptRunner.logger	= logger;
	}


	void setDelimiter(String delimiter, boolean fullLineDelimiter)
	{
		this.delimiter			= delimiter;
		this.fullLineDelimiter	= fullLineDelimiter;
	}


	/**
	 * Set the current working directory. Source commands will be relative to this.
	 */
	void setUserDirectory(String userDirectory)
	{
		this.userDirectory = userDirectory;
	}


	/**
	 * Runs an SQL script (read in using the Reader parameter)
	 *
	 * @param filepath
	 *                 - the filepath of the script to run. May be relative to the userDirectory.
	 */
	void runScript(String filepath) throws IOException, SQLException
	{
		File file = new File(userDirectory, filepath);
		this.runScript(new BufferedReader(new FileReader(file)));
	}


	/**
	 * Runs an SQL script (read in using the Reader parameter)
	 *
	 * @param reader
	 *               - the source of the script
	 */
	void runScript(Reader reader) throws IOException, SQLException
	{
		try
		{
			boolean originalAutoCommit = connection.getAutoCommit();

			try
			{
				if(originalAutoCommit != this.autoCommit)
				{
					connection.setAutoCommit(this.autoCommit);
				}
				
				runScript(connection, reader);
			}
			finally
			{
				connection.setAutoCommit(originalAutoCommit);
			}
		}
		catch(IOException | SQLException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			logger.error("Error running script.  Cause: " + e);
			throw new RuntimeException("Error running script.  Cause: " + e, e);
		}
	}


	/**
	 * Runs an SQL script (read in using the Reader parameter) using the connection passed in
	 *
	 * @param conn
	 *               - the connection to use for the script
	 * @param reader
	 *               - the source of the script
	 * @throws SQLException
	 *                      if any SQL errors occur
	 * @throws IOException
	 *                      if there is an error reading from the Reader
	 */
	private void runScript(Connection conn, Reader reader) throws IOException, SQLException
	{
		StringBuffer command = null;

		try
		{
			LineNumberReader	lineReader	= new LineNumberReader(reader);
			String				line;

			while((line = lineReader.readLine()) != null)
			{
				if(command == null)
				{
					command = new StringBuffer();
				}
				
				String			trimmedLine	= line.trim();
				final Matcher	delimMatch	= delimP.matcher(trimmedLine);

				if((trimmedLine.length() < 1) || trimmedLine.startsWith("//"))
				{
					// Do nothing
				}
				else if(delimMatch.matches())
				{
					setDelimiter(delimMatch.group(2), false);
				}
				else if(trimmedLine.startsWith("--"))
				{
					logger.info(trimmedLine);
				}
				else if((trimmedLine.length() < 1) || trimmedLine.startsWith("--"))
				{
					// Do nothing
				}
				else if((!fullLineDelimiter && trimmedLine.endsWith(getDelimiter()))
						|| (fullLineDelimiter && trimmedLine.equals(getDelimiter())))
				{
					command.append(line.substring(0, line.lastIndexOf(getDelimiter())));
					command.append(" ");
					this.execCommand(conn, command, lineReader);
					command = null;
				}
				else
				{
					command.append(line);
					command.append("\n");
				}
			}

			if(command != null)
			{
				this.execCommand(conn, command, lineReader);
			}

			if(!autoCommit)
			{
				conn.commit();
			}
		}
		catch(IOException e)
		{
			logger.error(String.format("Error executing '%s': %s", command, e.getMessage()));
			throw new IOException(String.format("Error executing '%s': %s", command, e.getMessage()), e);
		}
		finally
		{
			conn.rollback();
		}
	}


	private void execCommand(Connection conn, StringBuffer command, LineNumberReader lineReader) throws IOException, SQLException
	{
		if(command.length() == 0)
		{
			return;
		}
		
		Matcher sourceCommandMatcher = SOURCE_COMMAND.matcher(command);

		if(sourceCommandMatcher.matches())
		{
			this.runScriptFile(conn, sourceCommandMatcher.group(1));
			return;
		}
		
		this.execSqlCommand(conn, command, lineReader);
	}


	private void runScriptFile(Connection conn, String filepath) throws IOException, SQLException
	{
		File file = new File(userDirectory, filepath);
		this.runScript(conn, new BufferedReader(new FileReader(file)));
	}


	private void execSqlCommand(Connection conn, StringBuffer command, LineNumberReader lineReader) throws SQLException
	{
		Statement statement = conn.createStatement();
//		logger.info(command);
		boolean hasResults = false;

		try
		{
			hasResults = statement.execute(command.toString());
		}
		catch(SQLException e)
		{
			final String errText = String.format("Error executing \"%s\"\t\t(line %d): %s", command.toString().trim(), lineReader.getLineNumber(), e.getMessage());
			logger.error(errText);

			if(stopOnError)
			{
				throw new SQLException(errText, e);
			}
		}

		if(autoCommit && !conn.getAutoCommit())
		{
			conn.commit();
		}
		
		ResultSet rs = statement.getResultSet();

		if(hasResults && (rs != null))
		{
			ResultSetMetaData	md		= rs.getMetaData();
			int					cols	= md.getColumnCount();

			for(int i = 1; i <= cols; i++)
			{
				String name = md.getColumnLabel(i);
				logger.info(name + "\t");
			}
			
			logger.error("^^");

			while(rs.next())
			{
				for(int i = 1; i <= cols; i++)
				{
					String value = rs.getString(i);
					logger.info(value + "\t");
				}
				
				logger.error("^^");
			}
		}

		try
		{
			statement.close();
		}
		catch(Exception e)
		{
			// Ignore to workaround a bug in Jakarta DBCP
		}
	}


	private String getDelimiter()
	{
		return delimiter;
	}
}