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
	default int getRowCount(String xlFile, String xlSheet) throws IOException
	{
		FileInputStream		fi		 	= new FileInputStream(xlFile);
		XSSFWorkbook		wb		 	= new XSSFWorkbook(fi);
		XSSFSheet			ws			= wb.getSheet(xlSheet);
		
		int 				rowcount 	= ws.getLastRowNum();
		
		wb.close();
		fi.close();
		
		return rowcount;
	}


	default int getCellCount(String xlFile, String xlSheet, int rowIdx) throws IOException
	{
		FileInputStream		fi			= new FileInputStream(xlFile);
		XSSFWorkbook		wb			= new XSSFWorkbook(fi);
		XSSFSheet			ws			= wb.getSheet(xlSheet);
		XSSFRow				row			= ws.getRow(rowIdx);
		
		int 				cellcount 	= row.getLastCellNum();
		
		wb.close();
		fi.close();
		
		return cellcount;
	}


	default String getCellData(String xlFile, String xlSheet, int rowIdx, int colIdx) throws IOException
	{
		FileInputStream		fi			= new FileInputStream(xlFile);
		XSSFWorkbook		wb			= new XSSFWorkbook(fi);
		XSSFSheet			ws			= wb.getSheet(xlSheet);
		XSSFRow				row			= ws.getRow(rowIdx);
		XSSFCell			cell		= row.getCell(colIdx);
		
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


	default void setCellData(String xlFile, String xlSheet, int rowIdx, int colIdx, String data) throws IOException
	{
		FileInputStream		fi			= new FileInputStream(xlFile);
		XSSFWorkbook		wb			= new XSSFWorkbook(fi);
		XSSFSheet			ws			= wb.getSheet(xlSheet);
		XSSFRow				row			= ws.getRow(rowIdx);
		XSSFCell			cell		= row.getCell(colIdx);
		
		cell.setCellValue(data);
		
		FileOutputStream	fo 			= new FileOutputStream(xlFile);
		
		wb.write(fo);
		wb.close();
		fi.close();
		fo.close();
	}
}
