package com.github.GandhiTC.java.ThreadsafeFrameWork.tests;



import java.sql.SQLException;



/*
You can set up a free MySQL database at remotemysql.com
*/



public class PopulateSampleData extends BaseClass
{
	public static void main(String[] args) throws SQLException
	{
		//	Check if "pomCredentials" exists in the database.  If it does not, add it.
		if(!db.checkIfTableExists("offices"))
		{
			System.out.println("Populating database with sample data, it may take a while, depending on sample size.");
			
			db.parseSqlFile("src/test/resources/mysqltutorial.org_sample_database_slightly_edited.sql", false, true, false, false);
		}
		
		db.closeConnection();
	}
}