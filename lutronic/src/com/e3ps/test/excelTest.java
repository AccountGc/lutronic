package com.e3ps.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFFont;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.fc.QueryResult;
import wt.util.WTProperties;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.service.ECOHelper;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.POIUtil;
import com.e3ps.migration.beans.MigrationDocumentHelper;
import com.e3ps.part.service.PartHelper;


public class excelTest implements wt.method.RemoteAccess, java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
	public static excelTest manager = new excelTest();
	public static void main(String[] args) {
		
		/*
		System.out.println("===========test S===========");
		String name ="1BCDE";
		
		int len = name.length();
		
		for(int i = 0 ;  i < len ; i++){
			
			String temp=name.substring(i,);
			System.out.println(temp ); 
			String s = String.format("%02X%n", temp); // 16진수 문자열로 변환
			System.out.println(temp + "=" +s) ;
		}
		//test();
		
		
		System.out.println("===========test E===========");
	*/
		
		//excelReading(fileName);
		//excelTest.manager.excelReading();
		
		//test2();
		compareBomExcelDown();
	}
	
	public static void test2(){
		try{
			String excelFile ="D:\\AAA.xlsx";
			File orgFile = new File(excelFile);
			
			//System.out.println("orgFile.getName() =" + orgFile.getName());
			
			
			File newFile = CommonUtil.copyFile(orgFile, new File("D:\\AAA2.xlsx"));
			
			//System.out.println("newFile.getName() =" + newFile.getName());
			/*
			FileInputStream inFile = new FileInputStream(newFile);
			XSSFWorkbook workbook = new XSSFWorkbook(inFile);
			XSSFSheet sheet= POIUtil.getSheet(workbook, 0);
			
			XSSFRow newRow = sheet.createRow(2);
			XSSFCell partNumber1 = newRow.createCell(0);
			partNumber1.setCellValue("AA");
			
			inFile.close();
			
			FileOutputStream outFile = new FileOutputStream(newFile);
			workbook.write(outFile);
			workbook.close();
			*/
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static ResultData compareBomExcelDown() {
		
		ResultData data = new ResultData();
		try{
			
			String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
			String path = WTProperties.getServerProperties().getProperty("wt.temp");
			
			File orgFile = new File(wtHome + "/codebase/com/e3ps/common/excelDown/service/compareBom.xlsx");

			File newFile = CommonUtil.copyFile(orgFile, new File(path + "/" + "AAA"+ ".xlsx"));

			FileInputStream file = new FileInputStream(newFile);

			XSSFWorkbook workbook = new XSSFWorkbook(file);

			XSSFSheet sheet = workbook.getSheetAt(0);
			XSSFSheet sheetStyle =  workbook.getSheetAt(1);
			workbook.setSheetName(0, "compare");
			List<String[]> list = new ArrayList<String[]>();
			
			//compareData
			//System.out.println("1.sheet ========" + sheet.getSheetName());
			
		
			XSSFRow copyRow = sheetStyle.getRow(0);
			//System.out.println("1.copyRow  ========" + copyRow);
			XSSFCell copyCell = copyRow.getCell(12);
			//System.out.println("1.copyCell  ========" + copyCell.getCellStyle());
			
			// Copy style from old cell and apply to new cell
			XSSFCellStyle newCellStyle = workbook.createCellStyle();
			newCellStyle.cloneStyleFrom(copyCell.getCellStyle());
			
			
			
			
			
			
			
			XSSFRow newRow = sheet.createRow(2);
			XSSFCell partNumber1 = newRow.createCell(0);
			partNumber1.setCellStyle(newCellStyle);
			partNumber1.setCellValue("AAA");
			
			workbook.removeSheetAt(1);
			// 엑셀 파일명 설정
			file.close();
			FileOutputStream outFile = new FileOutputStream(newFile);
			workbook.write(outFile);
			outFile.close();

			workbook.close();
			data.setResult(true);
			data.setMessage(newFile.getName());
		
		}catch(Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		}
		
		
		
		return data;
		
	}
	public  void excelReading(){
		
		if(!SERVER) {
            Class argTypes[] = new Class[]{};
            Object args[] = new Object[]{};
            try {
               wt.method.RemoteMethodServer.getDefault().invoke(
                        "excelReading",
                        null,
                        this,
                        argTypes,
                        args);
               return;
            } catch(RemoteException e) {
                e.printStackTrace();
               
            } catch(InvocationTargetException e) {
                e.printStackTrace();
               
            } catch(Exception e){
                
                
            }
        }
		
		try{
			String oid = "com.e3ps.change.EChangeOrder:8653101";
			ECOHelper.service.excelDown(oid, "eco");
		/*
			EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(oid);
			QueryResult roleQr = ContentHelper.service.getContentsByRole (eco, ContentRoleType.toContentRoleType("ECO"));
			
			while(roleQr.hasMoreElements()) {
				ContentItem item = (ContentItem)roleQr.nextElement();
				if(item != null) {
					ApplicationData pAppData = (ApplicationData)item;
					CommonContentHelper.service.fileDown(CommonUtil.getOIDString(pAppData));
					
					String path = WTProperties.getServerProperties().getProperty("wt.temp");
					File file = new File(path + "/" + pAppData.getFileName());
					System.out.println(file);
					XSSFWorkbook workbook = POIUtil.getWorkBook(file);
				}
				
				
			}
			*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void excelReading(String fileName){
		try{
			String path = WTProperties.getServerProperties().getProperty("wt.temp");
			File file = new File(path + "/" + fileName);
			//System.out.println(file);
			XSSFWorkbook workbook = POIUtil.getWorkBook(file);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void test(){
		String excelFile ="D:\\excelTest.xlsx";
		File file = new File(excelFile);
	
		XSSFWorkbook workbook = POIUtil.getWorkBook(file);
		XSSFSheet sheet= POIUtil.getSheet(workbook, 0);
		
		for(int i = 0; i < POIUtil.getSheetRow(sheet); i++) {
        	/*
			int a = 0;
			String b ="A";
			a = b.charAt(0);
			*/
        	XSSFRow row=sheet.getRow(i);
        	String aa=POIUtil.getRowStringValue(row, 0);
        	for(int h = 0 ;  h <aa.length() ; h++ ){
        		String temp = aa.substring(h);
        		
        		System.out.print(temp);
        	}
        	
        	//System.out.println(aa);
		}
	}

}
