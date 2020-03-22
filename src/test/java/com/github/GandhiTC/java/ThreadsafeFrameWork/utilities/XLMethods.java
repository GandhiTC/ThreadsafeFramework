package com.github.GandhiTC.java.ThreadsafeFrameWork.utilities;



import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



interface XLMethods
{
	default int getRowCount(String xlfile, String xlsheet) throws IOException
	{
		FileInputStream		fi		 	= new FileInputStream(xlfile);
		XSSFWorkbook		wb		 	= new XSSFWorkbook(fi);
		XSSFSheet			ws			= wb.getSheet(xlsheet);
		
		int 				rowcount 	= ws.getLastRowNum();
		
		wb.close();
		fi.close();
		
		return rowcount;
	}


	default int getCellCount(String xlfile, String xlsheet, int rownum) throws IOException
	{
		FileInputStream		fi			= new FileInputStream(xlfile);
		XSSFWorkbook		wb			= new XSSFWorkbook(fi);
		XSSFSheet			ws			= wb.getSheet(xlsheet);
		XSSFRow				row			= ws.getRow(rownum);
		
		int 				cellcount 	= row.getLastCellNum();
		
		wb.close();
		fi.close();
		
		return cellcount;
	}


	default String getCellData(String xlfile, String xlsheet, int rownum, int colnum) throws IOException
	{
		FileInputStream		fi			= new FileInputStream(xlfile);
		XSSFWorkbook		wb			= new XSSFWorkbook(fi);
		XSSFSheet			ws			= wb.getSheet(xlsheet);
		XSSFRow				row			= ws.getRow(rownum);
		XSSFCell			cell		= row.getCell(colnum);
		
		String data;

		try
		{
			DataFormatter	formatter	= new DataFormatter();
			String			cellData	= formatter.formatCellValue(cell);
			
			wb.close();
			fi.close();
			
			return cellData;
		}
		catch(Exception e)
		{
			data = "";
		}
		
		if(wb != null)
		{
			wb.close();
		}
		
		if(fi != null)
		{
			fi.close();
		}
		
		return data;
	}


	default void setCellData(String xlfile, String xlsheet, int rownum, int colnum, String data) throws IOException
	{
		FileInputStream		fi			= new FileInputStream(xlfile);
		XSSFWorkbook		wb			= new XSSFWorkbook(fi);
		XSSFSheet			ws			= wb.getSheet(xlsheet);
		XSSFRow				row			= ws.getRow(rownum);
		XSSFCell			cell		= row.getCell(colnum);
		
		cell.setCellValue(data);
		
		FileOutputStream	fo 			= new FileOutputStream(xlfile);
		
		wb.write(fo);
		wb.close();
		fi.close();
		fo.close();
	}
}
